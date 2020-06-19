package net.bdavies.fx.basic;

import com.github.mbelling.ws281x.Color;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.Strip;
import net.bdavies.Time;
import net.bdavies.fx.Effect;
import net.bdavies.fx.FXUtil;
import net.bdavies.math.Beat;

import java.util.Map;

@Slf4j
public class BPM extends Effect {

    public BPM(Map<String, Object> config) {
        super(config);
    }

    @Override
    public int getDelay() {
        return 10;
    }

    @Override
    public void render(Strip strip) {
        int step = (int) (Time.getMillis() / 20) & 0xFF;
        int beat = (int) Beat.beatsin(150, 0, 255);

        for (int i = 0; i < strip.getLedsCount(); i++) {
            Color c = FXUtil.colorWheel(beat + step + (i * 10));
            strip.setPixel(i, c);
        }


        strip.render();
    }
}
