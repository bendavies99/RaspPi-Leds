package net.bdavies.fx.basic;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;
import net.bdavies.fx.Effect;

import java.util.Map;


public class ColorWipe extends Effect {

    private Color currentColor = Color.RED;
    private int counter = 0;

    public ColorWipe(Map<String, Object> config) {
        super(config);
    }


    @Override
    public int getDelay() {
        return 50;
    }

    @Override
    public void render(Strip strip) {

        if (counter >= strip.getLedsCount()) {
            counter = 0;
            if (currentColor == Color.RED) {
                currentColor = Color.GREEN;
            } else if (currentColor == Color.GREEN) {
                currentColor = Color.BLUE;
            } else if (currentColor == Color.BLUE) {
                currentColor = Color.RED;
            }
        }
        strip.setPixel(counter, currentColor);
        strip.render();
        counter++;
    }
}
