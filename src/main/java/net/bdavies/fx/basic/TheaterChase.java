package net.bdavies.fx.basic;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;
import net.bdavies.fx.GlobalColorEffect;

import java.util.Map;

public class TheaterChase extends GlobalColorEffect {

    private int q = 0;

    public TheaterChase(Map<String, Object> data) {
        super(data);
    }

    @Override
    public int getDelay() {
        return 50;
    }

    @Override
    protected void renderEffect(Color color, Strip strip) {
        for (int j = 0; j < strip.getLedsCount(); j += 3) {
            strip.setPixel(j + (q - 1), Color.BLACK);
        }
        if (q >= 3) {
            q = 0;
        }

        for (int j = 0; j < strip.getLedsCount(); j += 3) {
            strip.setPixel(j + q, color);
        }
        strip.render();
        q++;
    }
}
