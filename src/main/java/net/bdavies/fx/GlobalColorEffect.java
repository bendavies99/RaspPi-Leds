package net.bdavies.fx;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;
import net.bdavies.fx.Effect;

import java.util.HashMap;
import java.util.Map;

public abstract class GlobalColorEffect extends Effect {

    protected GlobalColorEffect() {
        super(new HashMap<>());
    }

    protected GlobalColorEffect(Map<String, Object> data) {
        super(data);
    }

    @Override
    public final void render(Strip strip) {
        renderEffect(strip.getCurrentColor(), strip);
    }

    protected abstract void renderEffect(Color color, Strip strip);

}
