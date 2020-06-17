package net.bdavies;

import com.github.mbelling.ws281x.Color;
import com.github.mbelling.ws281x.LedStripType;
import com.github.mbelling.ws281x.Ws281xLedStrip;

public class Main implements Runnable {

    Ws281xLedStrip strip;
    Thread thread;

    public Main(Ws281xLedStrip strip) {
        this.strip = strip;
        thread = new Thread(this);
    }

    public static void main(String[] args) throws InterruptedException {
        Ws281xLedStrip strip = new Ws281xLedStrip(30,
                19,
                800000,
                10,
                255,
                1,
                false,
                LedStripType.WS2811_STRIP_GRB,
                false);
        Main main = new Main(strip);
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
        thread.start();

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
