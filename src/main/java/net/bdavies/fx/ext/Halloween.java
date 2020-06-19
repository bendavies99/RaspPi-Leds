package net.bdavies.fx.ext;

import com.github.mbelling.ws281x.Color;
import net.bdavies.fx.basic.Running;

import java.util.Map;

public class Halloween extends Running {
    public Halloween(Map<String, Object> data) {
        super(new Color(48, 1, 37), Color.ORANGE);
    }
}
