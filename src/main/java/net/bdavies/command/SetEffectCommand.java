package net.bdavies.command;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.Application;
import net.bdavies.Strip;
import net.bdavies.command.data.SetEffectData;
import net.bdavies.fx.EffectContainer;

@Slf4j
public class SetEffectCommand extends Command<SetEffectData> {

    @Override
    public boolean run(Application application, Strip strip, SetEffectData data) {
        log.info("Running Set Effect Command");
        strip.setCurrentEffect(EffectContainer.getEffect(data.getName(), data.getFxConfig()), data.getName());
        return true;
    }
}
