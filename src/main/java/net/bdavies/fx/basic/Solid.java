package net.bdavies.fx.basic;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;
import net.bdavies.fx.GlobalColorEffect;

import java.util.HashMap;
import java.util.Map;

public class Solid extends GlobalColorEffect {

    public Solid(Map<String, Object> data) {
        super(data);
    }

    public Solid() {
        this(new HashMap<>());
    }

    @Override
    protected void renderEffect(Color color, Strip strip) {
        strip.setStrip(color);
        strip.render();
    }
}
