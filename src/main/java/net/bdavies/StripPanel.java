package net.bdavies;


import com.github.mbelling.ws281x.Color;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.fx.FXUtil;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

@Slf4j
public class StripPanel extends Canvas implements Runnable {

    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private int[] pixels, imagePixels;
    private int numLeds;

    public StripPanel(int numLeds) {
        Dimension dimension = new Dimension(numLeds * 6, 200);
        this.numLeds = numLeds;
        setMinimumSize(dimension);
        setPreferredSize(dimension);
        setMaximumSize(dimension);
        pixels = new int[numLeds];
        image = new BufferedImage(numLeds, 200, BufferedImage.TYPE_INT_RGB);
        imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        requestFocus();
        while (running) {
                render();
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        clear();
        copy();
        g.drawImage(image, 0, 0, numLeds * 6, 200, null);


        g.dispose();
        bs.show();

    }

    public synchronized void setPixel(int pixel, Color c, int brightness) {
        this.pixels[pixel] = (int) FXUtil.colorBlend(c, Color.BLACK, 255 - brightness).getColorBits();
    }

    private void copy() {
        for (int y = 0; y < 200; y++) {
            for (int x = 0; x < numLeds; x++) {
                imagePixels[x + y * numLeds] = pixels[x];
            }
        }
    }

    private void clear() {
        Arrays.fill(imagePixels, 0x000000);
    }
}
