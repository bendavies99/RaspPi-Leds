package net.bdavies;

import com.github.mbelling.ws281x.Color;
import com.github.mbelling.ws281x.Ws281xLedStrip;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.config.Config;
import net.bdavies.config.ConfigFactory;

import java.io.File;

@Slf4j
public class Main implements Runnable {

    Ws281xLedStrip strip;
    Thread thread;

    public Main() {

        Config config = ConfigFactory.makeConfig(new File("config/config.json"));
        log.info("Broker IP: {}", config.getMqtt().getBrokerIp());
        thread = new Thread(this);
    }

    public static void main(String[] args) throws InterruptedException {
//        Ws281xLedStrip strip = new Ws281xLedStrip(30,
//                19,
//                800000,
//                10,
//                255,
//                1,
//                false,
//                LedStripType.WS2811_STRIP_GRB,
//                false);
        Main main = new Main();
        main.start();

    }

    private void colourWipe(Ws281xLedStrip strip, Color c) throws InterruptedException {
        for (int i = 0; i < strip.getLedsCount(); i++) {
            strip.setPixel(i, c);
            strip.render();
            Thread.sleep(500);
        }
    }

    private void start() {
        //thread.start();

    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Running Colour wipes on pin 19");
            try {
                colourWipe(strip, Color.RED);
                colourWipe(strip, Color.GREEN);
                colourWipe(strip, Color.BLUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
