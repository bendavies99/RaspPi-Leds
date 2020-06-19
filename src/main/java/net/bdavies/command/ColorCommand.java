package net.bdavies.command;

import com.github.mbelling.ws281x.Color;
import net.bdavies.Application;
import net.bdavies.Strip;

public class ColorCommand extends Command<Color> {
    @Override
    public boolean run(Application application, Strip strip, Color data) {
        strip.setCurrentColor(data);
        return false;
    }
}
