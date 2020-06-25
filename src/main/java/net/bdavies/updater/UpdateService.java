package net.bdavies.updater;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.Application;
import org.apache.commons.io.FileUtils;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
public class UpdateService {

    private final Application application;

    public UpdateService(Application application) {
        this.application = application;
    }

    public Mono<Void> updateTo(PollingService.TagItem tag) {
        return Mono.create(sink -> {
            try {
                File tmp = File.createTempFile("update", "rled-");
                File unZipTmp = new File("tmp/updates/" + tag.tag_name.toLowerCase().replace("v", ""));
                FileUtils.copyURLToFile(new URL(tag.assets.get(1).browser_download_url), tmp);
                if (!unZipTmp.exists()) {
                    unZipTmp.mkdirs();
                }
                File firstDir = null;
                ZipInputStream zis = new ZipInputStream(new FileInputStream(tmp));
                ZipEntry entry = zis.getNextEntry();
                while (entry != null) {
                    String filePath = unZipTmp + File.separator + entry.getName();
                    if (!entry.isDirectory()) {
                        // if the entry is a file, extracts it
                        extractFile(zis, filePath);
                    } else {
                        // if the entry is a directory, make the directory
                        File dir = new File(filePath);
                        dir.mkdir();
                        if (firstDir == null)
                            firstDir = dir;
                    }
                    zis.closeEntry();
                    entry = zis.getNextEntry();
                }
                zis.close();
                if (swapLibs(firstDir, tag.tag_name.toLowerCase().replace("v", ""))) {
                    if (swapScripts(firstDir)) {
                        unZipTmp.deleteOnExit();
                        sink.success();
                    } else {
                        sink.error(new Exception("Something went wrong.."));
                    }
                } else {
                    sink.error(new Exception("Something went wrong.."));
                }
            } catch (IOException e) {
                sink.error(e);
            }
        });
    }

    private boolean swapScripts(File unZipTmp) throws IOException {
        File unzippedApp = new File(unZipTmp + File.separator + "bin/RLed");
        log.info(unzippedApp.getAbsolutePath());
        File appSh = new File("RLed");

        if (!unzippedApp.exists()) {
            if (!unzippedApp.createNewFile()) {
                log.error("No APP file!!!");
                return false;
            }
        }

        if (!appSh.exists()) {
            if (!appSh.createNewFile()) {
                log.error("No APP file!!!");
                return false;
            }
        }

        if (appSh.delete()) {
            try {
                FileUtils.copyFile(unzippedApp, appSh);
                if (!appSh.setExecutable(true)) {
                    log.error("Cannot set file to executable..");
                    return false;
                }
            } catch (IOException e) {
                log.error("Cant copy", e);
                return false;
            }
        }

        return true;
    }

    private boolean swapLibs(File unZipTmp, String version) {
        File libsFolder = new File(unZipTmp + File.separator + "lib/");
        File ourLibsFolder = new File("../lib/");
        log.info("Lib folder: " + ourLibsFolder.getAbsolutePath());

        if (!ourLibsFolder.exists()) {
            if (!ourLibsFolder.mkdirs()) {
                log.error("No Lib folder!!!");
                return false;
            }
        }

        if (!libsFolder.exists()) {
            if (!libsFolder.mkdirs()) {
                log.error("No Lib folder!!!");
                return false;
            }
        }
        AtomicBoolean copy = new AtomicBoolean(true);
        try {
            Arrays.stream(Objects.requireNonNull(ourLibsFolder.listFiles()))
                    .filter(f1 -> !f1.getName().contains("rpi-ws"))
                    .filter(f ->
                            Arrays.stream(Objects.requireNonNull(libsFolder.listFiles()))
                                    .noneMatch(f1 -> f1.getName().equals(f.getName()))) /* delete outdated libraries */
                    .forEach(File::deleteOnExit);
            Arrays.stream(Objects.requireNonNull(libsFolder.listFiles()))
                    .filter(f -> !f.getName().contains("rpi-ws")).forEach(f -> {
                try {
                    FileUtils.copyFileToDirectory(f, ourLibsFolder);
                } catch (IOException e) {
                    log.error("Cant copy", e);
                    copy.set(false);
                }
            });
        } catch (NullPointerException e) {
            log.error("Cant copy", e);
            return false;
        }


        return copy.get();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

}
