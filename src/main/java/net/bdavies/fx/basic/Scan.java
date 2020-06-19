package net.bdavies.fx.basic;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;
import net.bdavies.fx.Effect;

import java.util.HashMap;
import java.util.Map;

public class Scan extends Effect {

    private int counter = 0;
    private boolean rev = false;

    public Scan(Color c1, Color c2) {
        this(new HashMap<>());
        setConfig("c1", c1);
        setConfig("c2", c2);
    }

    public Scan(Color c1) {
        this(new HashMap<>());
        setConfig("c1", c1);
    }

    public Scan() {
        this(new HashMap<>());
    }

    public Scan(Map<String, Object> data) {
        super(data);
        convertObjectToColor("c1");
        convertObjectToColor("c2");
        setDefaultConfig("size", 2);
        setDefaultConfig("c1", Color.WHITE);
        setDefaultConfig("c2", Color.BLACK);
    }

    @Override
    public int getDelay() {
        return 10;
    }

    @Override
    public void render(Strip strip) {
        int dir = rev ? -1 : 1;
        strip.setStrip(getColor("c2"));
        strip.render();

        for (int i = 0; i < getInt("size"); i++) {
            strip.setPixel(counter - i, getColor("c1"));
        }

        counter += dir;

        if (counter >= strip.getLedsCount()) {
            counter = strip.getLedsCount() - 1;
            rev = true;
        } else if (counter <= 0) {
            counter = 0;
            rev = false;
        }

        strip.render();

    }
}
