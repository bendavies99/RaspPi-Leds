package net.bdavies.fx;

import net.bdavies.fx.basic.*;
import net.bdavies.fx.ext.Halloween;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Important notes for api
 * - the matcher must uppercase names
 * - it must also change spaces to an underscore (_)
 * e.g. name.toUpperCase().replaceAll(" ", "_")
 */
public class EffectContainer {

    public static Effect getEffect(String name, Map<String, Object> data) {
        return EffectEnum.valueOf(name.toUpperCase().replaceAll(" ", "_")).createEffect(data);
    }

    public static List<String> getEffectNames() {
        return Arrays.stream(EffectEnum.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .map(n -> n.substring(0, 1).toUpperCase() + n.substring(1))
                .map(n -> n.replaceAll("_", " "))
                .collect(Collectors.toList());
    }

    private enum EffectEnum {
        RAINBOW(Rainbow.class),
        BREATH(Breath.class),
        BPM(BPM.class),
        COLOR_WIPE(ColorWipe.class),
        FADE(Fade.class),
        RUNNING(Running.class),
        SCAN(Scan.class),
        SOLID(Solid.class),
        STROBE(Strobe.class),
        THEATER_CHASE(TheaterChase.class),
        HALLOWEEN(Halloween.class);


        Class<? extends Effect> effectClass;

        EffectEnum(Class<? extends Effect> effectClass) {
            this.effectClass = effectClass;
        }

        public Effect createEffect(Map<String, Object> data) {
            try {
                return this.effectClass.getConstructor(Map.class).newInstance(data);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
