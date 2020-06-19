package net.bdavies.command.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
public class SetEffectData {

    @Getter
    private final String name;
    @Getter
    private final Map<String, Object> fxConfig;

}
