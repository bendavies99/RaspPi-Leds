package net.bdavies.fx;

import com.github.mbelling.ws281x.Color;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.Strip;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class Effect {

    protected final Map<String, Object> config;
    /**
     * This is how much to wait in milliseconds before next update
     */
    @Getter
    public int delay = 50;

    public Effect(Map<String, Object> config) {
        this.config = config;
    }

    public void setConfig(String k, Object o) {
        config.remove(k);
        config.put(k, o);
    }

    public Color getColor(String k) {
        return (Color) config.get(k);
    }

    public int getInt(String k) {
        return (int) config.get(k);
    }

    protected void convertObjectToColor(String k) {
        if (config.containsKey(k)) {
            if (config.get(k) instanceof String) {
                //Assume it is a hex code
                String val = (String) config.get(k);
                config.remove(k);
                config.put(k, hexToColor(val));
            } else {
                log.info("List: {}", config.get(k));
                //noinspection unchecked
                List<Double> rgb = (List<Double>) config.get(k);
                config.remove(k);
                config.put(k, new Color((int) Math.round(rgb.get(0)),
                        (int) Math.round(rgb.get(1)), (int) Math.round(rgb.get(2))));
            }
        }
    }

    public Color hexToColor(String hex) {
        return FXUtil.hexToColor(hex);
    }

    protected void setDefaultConfig(String k, Object o) {
        if (!config.containsKey(k)) {
            config.put(k, o);
        }
    }

    public abstract void render(Strip strip);

}
