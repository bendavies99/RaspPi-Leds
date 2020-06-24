package net.bdavies.fx.basic;

import net.bdavies.Strip;
import net.bdavies.fx.Effect;

import java.util.Map;

public class Reactive extends Effect {

    public Reactive(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void render(Strip strip) {
        strip.getReactiveUDP().start();
        for (int i = 0; i < strip.getLedsCount(); i++) {
            strip.setPixel(i, strip.getReactiveUDP().getData()[i]);
        }

        strip.render();
    }
}
