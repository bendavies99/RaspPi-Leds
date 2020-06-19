package net.bdavies.fx.basic;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;
import net.bdavies.fx.GlobalColorEffect;

import java.util.HashMap;
import java.util.Map;

public class Strobe extends GlobalColorEffect {

    private boolean on = false;

    public Strobe(int speed) {
        this(new HashMap<>());
        setConfig("speed", speed);
    }

    public Strobe(Map<String, Object> data) {
        super(data);
        convertObjectToColor("color");
        setDefaultConfig("speed", 150);
    }

    @Override
    public int getDelay() {
        return getInt("speed");
    }

    @Override
    protected void renderEffect(Color color, Strip strip) {
        if (on) {
            on = false;
            strip.setStrip(Color.BLACK);
        } else {
            on = true;
            strip.setStrip(color);
        }

        strip.render();
    }
}
