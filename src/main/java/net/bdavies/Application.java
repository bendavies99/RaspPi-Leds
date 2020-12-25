package net.bdavies;

import com.github.mbelling.ws281x.Color;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.config.Config;
import net.bdavies.config.ConfigFactory;
import net.bdavies.fx.FXUtil;
import net.bdavies.mqtt.MQTTClient;
import net.bdavies.updater.PollingService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Application implements Runnable {

    public static final double FPS = 15.0; //RaspPi Will handle this better
    @Getter
    private static ExecutorService service;
    private final List<Strip> strips;
    private final Thread thread, auxThread;
    @Getter
    private final MQTTClient mqttClient;
    @Getter
    private final String[] args;
    private final PollingService pollingService;
    @Getter
    private String version;
    private boolean running;

    @SneakyThrows
    public Application(boolean dev, String[] args) {
        this.args = args;
        FXUtil.fillColourWheelList();
        try {
            InputStreamReader r = new InputStreamReader(getClass().getResourceAsStream("/version.txt"));
            BufferedReader reader = new BufferedReader(r);
            version = reader.readLine().trim();
            log.info("Version is: " + version);
        } catch (IOException | NullPointerException e) {
            log.error("Please make sure that you have ran createProperties in gradle..", e);
            e.printStackTrace();
            System.exit(-1);
        }
        Config config = ConfigFactory.makeConfig(new File("config/config.json"));
        service = Executors.newCachedThreadPool();
        this.strips = new ArrayList<>();
        this.mqttClient = new MQTTClient(this, config.getMqtt());
        this.mqttClient.connect();

        config.getStrips().forEach(s -> {
            Strip strip = new Strip(s, dev ? Strip.SetupType.DEV : Strip.SetupType.PROD, this);
            this.strips.add(strip);
        });
        int progressBar = 0;
        while (!mqttClient.isConnected()) {

            for (Strip strip : strips) {
                int count = (progressBar++) * (int)Math.ceil((float)strip.getLedsCount() / 100.0f);
                if (count >= strip.getLedsCount()) {
                    int idx =0;
                    while(idx <= 4) {
                        strip.setStrip(Color.RED);
                        strip.render();
                        Thread.sleep(500);
                        strip.setStrip(Color.BLACK);
                        strip.render();
                        Thread.sleep(500);
                        idx++;
                    }
                    progressBar = 0;
                }
                strip.setPixel(count, 0, 0, 255);
                strip.render();
            }
            try {
                //noinspection BusyWait
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Strip strip : strips) {
                int idx =0;
                while(idx <= 4) {
                    strip.setStrip(Color.GREEN);
                    strip.render();
                    Thread.sleep(500);
                    strip.setStrip(Color.BLACK);
                    strip.render();
                    Thread.sleep(500);
                    idx++;
                }
        }

        for (Strip strip : strips) {
            strip.init();
        }


        config.getStrips().forEach(mqttClient::listenToStrip);

        thread = new Thread(this);

        pollingService = new PollingService(this);
        auxThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            double ns = 1000000000.0 / FPS;
            double delta = 0;
            while (running) {
                try {
                    long now = System.nanoTime();
                    delta += (now - lastTime) / ns;
                    lastTime = now;
                    if (delta >= 1) {
                        for (Strip strip : strips) {
                            strip.loopColor();
                            strip.loopBrightness();
                        }
                        delta--;
                    }
                    //noinspection BusyWait
                    Thread.sleep(1000 / 30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        log.info("Booting....");
        log.info("Args: {}", Arrays.toString(args));
        Application application = new Application(Arrays.stream(args).anyMatch(a -> a.toLowerCase().equals("-dev")),
                args);
        application.start();

    }

    public Strip getStripById(String id) {
        //noinspection OptionalGetWithoutIsPresent
        return this.strips.stream().filter(strip -> strip.getId().equals(id))
                .findFirst().get();
    }

    private synchronized void start() {
        running = true;
        pollingService.start();
        thread.start();
        auxThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double ns = 1000000000.0 / FPS;
        double delta = 0;
        while (running) {
            long curTime = System.currentTimeMillis();
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                for (int i = 0; i < strips.size(); i++) {
                    strips.get(i).loop(curTime);
                }
                delta--;
            }

            try {
                //noinspection BusyWait
                Thread.sleep((long) (1000 / FPS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void restart() {
        Executors.newSingleThreadExecutor().submit(() -> {
            this.strips.forEach(Strip::shutdown);
            running = false;
            try {
                thread.join(1);
                auxThread.join(1);

                mqttClient.shutdown();
                service.shutdownNow();
                pollingService.stop();
                Runtime.getRuntime().runFinalization();
                try {
                    List<String> command = new ArrayList<>();

                    if (new File("RLed").exists()) {
                        command.add("./RLed");
                    } else {
                        String cmd = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                        command.add(cmd);
                        command.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
                        command.add("-cp");
                        command.add(ManagementFactory.getRuntimeMXBean().getClassPath());
                        command.add(getClass().getName());
                        command.addAll(Arrays.asList(args));
                    }
                    command.add("-restart");


                    ProcessBuilder pb = new ProcessBuilder();
                    log.info("Built command: {} ", command);
                    pb.command(command);
                    pb.inheritIO();
                    Process p = pb.start();
                    p.waitFor();
                    System.exit(p.exitValue());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean hasArgument(String argument) {
        return Arrays.asList(args).contains(argument);
    }
}
