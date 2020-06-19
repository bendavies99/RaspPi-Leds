package net.bdavies.command;

import net.bdavies.Application;
import net.bdavies.Strip;

public class BrightnessCommand extends Command<Integer> {
    @Override
    public boolean run(Application application, Strip strip, Integer data) {
        strip.setBrightness(data);
        return false;
    }
}
