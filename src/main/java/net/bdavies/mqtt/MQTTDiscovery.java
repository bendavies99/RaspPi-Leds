package net.bdavies.mqtt;

import lombok.Getter;
import net.bdavies.config.StripConfig;
import net.bdavies.fx.EffectContainer;

public class MQTTDiscovery {

    @Getter
    private final String stat_t, cmd_t, rgb_stat_t, unique_id;
    @Getter
    private final String rgb_cmd_t, bri_cmd_t, bri_stat_t, bri_val_tpl;
    @Getter
    private final String rgb_cmd_tpl, rgb_val_tpl;
    @Getter
    private final int qos;
    @Getter
    private final boolean opt;
    @Getter
    private final String pl_on, pl_off, fx_cmd_t, fx_stat_t, fx_val_tpl;
    @Getter
    private final String[] fx_list;

    @Getter
    public String name;


    public MQTTDiscovery(StripConfig config) {
        String deviceTopic = config.getId();
        name = config.getName();
        stat_t = String.format("%s/s", deviceTopic);
        cmd_t = deviceTopic;
        rgb_stat_t = String.format("%s/c", deviceTopic);
        rgb_cmd_t = String.format("%s/col", deviceTopic);
        bri_cmd_t = String.format("%s/bri", deviceTopic);
        bri_stat_t = String.format("%s/b", deviceTopic);
        bri_val_tpl = "{{value}}";
        rgb_cmd_tpl = "{{'#%02x%02x%02x' | format(red, green, blue)}}";
        rgb_val_tpl = "{{value[1:3]|int(base=16)}},{{value[3:5]|int(base=16)}},{{value[5:7]|int(base=16)}}";
        unique_id = config.getId();
        qos = 0;
        opt = true;
        pl_on = "ON";
        pl_off = "OFF";
        fx_cmd_t = String.format("%s/sfx", deviceTopic);
        fx_stat_t = String.format("%s/fx", deviceTopic);
        fx_val_tpl = "{{value}}";
        fx_list = EffectContainer.getEffectNames().toArray(new String[0]);
    }
}
