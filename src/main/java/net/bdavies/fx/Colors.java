package net.bdavies.fx;

import com.github.mbelling.ws281x.Color;

import java.util.Random;

public class Colors {

    private static final Color[] palette = new Color[]{
            Color.CYAN,
            Color.RED,
            Color.BLUE,
            Color.GREEN,
    };
    private static int lastIndex = 0;

    public static Color colorFromPalette(int index, int brightness) {
        int newIn = index;
        newIn = index >> 4;
        if (newIn >= palette.length - 1 || newIn < 0) newIn = new Random().nextInt(palette.length);
        Color c = palette[newIn];
        if (brightness < 255) {
            c = FXUtil.colorBlend(c, Color.BLACK, brightness);
        }

        return c;
    }

}
