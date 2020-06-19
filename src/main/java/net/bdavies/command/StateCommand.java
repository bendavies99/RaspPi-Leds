package net.bdavies.command;

import net.bdavies.Application;
import net.bdavies.Strip;

public class StateCommand extends Command<String> {

    @Override
    public boolean run(Application application, Strip strip, String data) {
        if (data.equals("ON")) {
            strip.on();
        } else {
            strip.off();
        }
        return true;
    }

}
