package net.bdavies.fx.basic;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;
import net.bdavies.fx.GlobalColorEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FireFlicker extends GlobalColorEffect {

    private final Random random = new Random();

    public FireFlicker(Map<String, Object> data) {
        super(data);
        delay = 100;
    }

    public FireFlicker() {
        this(new HashMap<>());
    }

    @Override
    protected void renderEffect(Color color, Strip strip) {
        int lum = Math.max(color.getRed(), Math.max(color.getGreen(), color.getBlue()));
        for (int i = 0; i < strip.getLedsCount(); i++) {
            int flicker = random.nextInt(Math.abs(lum) + 1);
            strip.setPixel(i, Math.max(color.getRed() - flicker, 0), Math.max(color.getGreen() - flicker, 0), Math.max(color.getBlue() - flicker, 0));
        }
        strip.render();
    }
}
