package net.bdavies.fx.basic;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;
import net.bdavies.fx.Effect;

import java.util.HashMap;
import java.util.Map;

public class Running extends Effect {

    private int counter = 0;

    public Running(Color c1, Color c2) {
        this(new HashMap<>());
        setConfig("c1", c1);
        setConfig("c2", c2);
    }

    public Running(Color c1) {
        this(new HashMap<>());
        setConfig("c1", c1);
    }

    public Running() {
        this(new HashMap<>());
    }

    public Running(Map<String, Object> data) {
        super(data);
        convertObjectToColor("c1");
        convertObjectToColor("c2");
        setDefaultConfig("size", 2);
        setDefaultConfig("c1", Color.RED);
        setDefaultConfig("c2", Color.BLUE);
    }

    @Override
    public int getDelay() {
        return 500;
    }

    @Override
    public void render(Strip strip) {
        Color c11 = (counter & getInt("size")) > 0 ? getColor("c1") : getColor("c2");
        Color c22 = (counter & getInt("size")) > 0 ? getColor("c2") : getColor("c1");
        for (int i = 0; i < strip.getLedsCount(); i++) {
            if (i % 2 == 0) {
                strip.setPixel(i, c11);
            } else {
                strip.setPixel(i, c22);
            }
        }

        counter = counter + 1 % strip.getLedsCount();
        strip.render();
    }
}
