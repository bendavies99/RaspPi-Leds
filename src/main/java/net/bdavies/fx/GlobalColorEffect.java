package net.bdavies.fx;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Strip;

import java.util.HashMap;
import java.util.Map;

public abstract class GlobalColorEffect extends Effect {

    private Color oldColor;

    protected GlobalColorEffect() {
        super(new HashMap<>());
    }

    protected GlobalColorEffect(Map<String, Object> data) {
        super(data);
    }

    @Override
    public final void render(Strip strip) {
        if (oldColor == null) {
            updateRender(strip);
        } else if (oldColor != strip.getCurrentColor()) {
            updateRender(strip);
        }

        renderEffect(oldColor, strip);

    }

    private void updateRender(Strip strip) {
        oldColor = strip.getCurrentColor();
        renderEffect(oldColor, strip);
    }

    protected abstract void renderEffect(Color color, Strip strip);

}
