package net.bdavies.fx.basic;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;
import net.bdavies.fx.Effect;
import net.bdavies.fx.FXUtil;

import java.util.HashMap;
import java.util.Map;


public class Breath extends Effect {

    private int delay = 0;
    private int counter = 0;

    public Breath(Color c1, Color c2) {
        this(new HashMap<>());
        setConfig("c1", c1);
        setConfig("c2", c2);
    }

    public Breath(Color c1) {
        this(new HashMap<>());
        setConfig("c1", c1);
    }

    public Breath(Map<String, Object> data) {
        super(data);
        convertObjectToColor("c1");
        convertObjectToColor("c2");
        setDefaultConfig("c1", Color.BLACK);
        setDefaultConfig("c2", Color.BLACK);
    }


    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void render(Strip strip) {
        int lum = counter;
        if (lum > 255) lum = 511 - lum;

        if (lum == 15) delay = 970; // 970 pause before each breath
        else if (lum <= 25) delay = 38; // 19
        else if (lum <= 50) delay = 36; // 18
        else if (lum <= 75) delay = 28; // 14
        else if (lum <= 100) delay = 20; // 10
        else if (lum <= 125) delay = 14; // 7
        else if (lum <= 150) delay = 11; // 5
        else delay = 10; // 4

        Color c = FXUtil.colorBlend(getColor("c1"), getColor("c2"), lum);
        strip.setStrip(c);
        strip.render();

        counter += 2;

        if (counter > 497) {
            counter = 15;
        }
    }
}
