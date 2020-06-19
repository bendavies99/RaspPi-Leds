package net.bdavies.updater;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.Application;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class PollingService {

    private final Application application;
    private final ExecutorService service;
    private final Timer timer;
    private Version currentVersion;
    private UpdateService updateService;

    public PollingService(Application application) {
        this.application = application;
        this.service = Executors.newFixedThreadPool(10);
        this.timer = new Timer();
        this.currentVersion = Version.valueOf(application.getVersion());
        this.updateService = new UpdateService(application);
    }

    public synchronized void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {

                    String response = HttpClient
                            .create()
                            .get()
                            .uri("https://api.github.com/repos/bendavies99/RaspPi-Leds/releases")
                            .responseContent()
                            .aggregate()
                            .asString()
                            .block();

                    Gson gson = new GsonBuilder().create();

                    List<TagItem> res = gson.fromJson(response, new TypeToken<List<TagItem>>() {
                    }.getType());
                    assert res != null;
                    if (res.size() > 0) {
                        TagItem first = res.get(0);
                        int index = 0;
                        while (first.prerelease && (res.size() - 1 <= index + 1)) {
                            first = res.get(++index);
                        }
                        String versionName = first.tag_name.toLowerCase().replace("v", "");
                        Version tagVersion = Version.valueOf(versionName);
                        log.info("Latest Version Online is: {}", tagVersion.toString());
                        log.info("Your Version is: {}", currentVersion.toString());

                        if (tagVersion.greaterThan(currentVersion) &&
                                (tagVersion.getMajorVersion() == currentVersion.getMajorVersion() &&
                                        !application.hasArgument("--updateMajor"))) {
                            log.info("Updating...");
                            updateService.updateTo(first)
                                    .subscribe(b -> {
                                    }, t -> log.error("Error", t), () -> {
                                        log.info("Update complete.. Restarting...");
                                        //TODO: Send Notification to HA and Phone
                                        currentVersion = tagVersion;
                                        application.restart();
                                    });
                        } else {
                            if (tagVersion.getMajorVersion() > currentVersion.getMajorVersion()) {
                                log.info("There is a new major version available please run ./RLed --updateMajor");
                            } else {
                                log.info("Up to date");
                            }
                        }

                    } else {
                        log.info("No new Versions available");
                    }


                } catch (Exception e) {
                    log.error("Something went wrong", e);
                }
            }
        }, 3000, 1000 * 60 * 60 /* Every Hour */);
    }

    public synchronized void stop() {
        timer.cancel();
        service.shutdown();
    }

    public static class TagItem {
        public String tag_name;
        public List<Asset> assets;
        public boolean prerelease;
    }

    public static class Asset {
        public String browser_download_url;
    }
}
