package net.bdavies.fx.basic;

import net.bdavies.Strip;
import net.bdavies.fx.Effect;
import net.bdavies.fx.FXUtil;

import java.util.HashMap;
import java.util.Map;

public class RainbowCycle extends Effect {

    private int counter = 0;

    public RainbowCycle(Map<String, Object> config) {
        super(config);
    }

    public RainbowCycle() {
        super(new HashMap<>());
    }

    @Override
    public int getDelay() {
        return 20;
    }


    @Override
    public void render(Strip strip) {
        if (counter > 256) {
            counter = 0;
        }

        for (int j = 0; j < strip.getLedsCount(); j++) {
            int index = (j * (16 << 4 / 29) / strip.getLedsCount()) + counter;
            strip.setPixel(j, FXUtil.colorWheel(index));
        }
        strip.render();
        counter++;
    }
}
