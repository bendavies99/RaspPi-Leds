package net.bdavies.fx;

import com.github.mbelling.ws281x.Color;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.Strip;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FXUtil {

    private static final List<Color> possibleColorsInWheel = new ArrayList<>(256);

    public static void fillColourWheelList() {
        for (int i = 0; i < 256; i++) {
            possibleColorsInWheel.add(colorWheelSet(i));
        }
    }

    public static void fillStrip(Strip strip, Color c) {
        strip.setStrip(c);
        strip.render();
    }

    public static Color colorBlend(Color c1, Color c2, int blendAmt) {
        if (blendAmt <= 0) return c1;
        if (blendAmt >= 255) return c2;

        int r = ((c2.getRed() * blendAmt) + (c1.getRed() * (255 - blendAmt))) / 256;
        int g = ((c2.getGreen() * blendAmt) + (c1.getGreen() * (255 - blendAmt))) / 256;
        int b = ((c2.getBlue() * blendAmt) + (c1.getBlue() * (255 - blendAmt))) / 256;

        return new Color(r, g, b);
    }

    public static Color hexToColor(String hex) {
        hex = hex.replace("#", "");
        switch (hex.length()) {
            case 6:
                return new Color(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16));
            case 8:
                return new Color(
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16),
                        Integer.valueOf(hex.substring(6, 8), 16));
        }
        log.warn("Invalid hex supplied!");
        return Color.BLACK;
    }

    private static Color colorWheelSet(int curPos) {
        int pos = curPos & 255;
        if (pos < 85) {
            return new Color(pos * 3, 255 - pos * 3, 0);
        } else if (pos < 170) {
            pos -= 85;
            return new Color(255 - pos * 3, 0, pos * 3);
        } else {
            pos -= 170;
            return new Color(0, pos * 3, 255 - pos * 3);
        }
    }

    public static Color colorWheel(int curPos) {
        int pos = curPos & 255;
        return possibleColorsInWheel.get(pos);
    }

}
