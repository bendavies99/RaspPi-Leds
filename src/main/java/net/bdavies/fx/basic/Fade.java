package net.bdavies.fx.basic;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;
import net.bdavies.fx.Effect;
import net.bdavies.fx.FXUtil;

import java.util.HashMap;
import java.util.Map;


public class Fade extends Effect {

    private int counter = 0;

    public Fade(Color c1, Color c2) {
        this(new HashMap<>());
        setConfig("c1", c1);
        setConfig("c2", c2);
    }

    public Fade(Color c1) {
        this(new HashMap<>());
        setConfig("c1", c1);
    }

    public Fade(Map<String, Object> data) {
        super(data);
        convertObjectToColor("c1");
        convertObjectToColor("c2");
        setDefaultConfig("c1", Color.RED);
        setDefaultConfig("c2", Color.BLACK);
    }

    @Override
    public int getDelay() {
        return 50;
    }

    @Override
    public void render(Strip strip) {
        int lum = counter;
        if (lum > 255) lum = 511 - lum;

        Color c = FXUtil.colorBlend(getColor("c1"), getColor("c2"), lum);
        strip.setStrip(c);
        strip.render();

        counter += 4;

        if (counter > 511) {
            counter = 0;
        }
    }
}
