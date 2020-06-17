package net.bdavies.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Config {

    @Getter
    private List<StripConfig> strips;

    @Getter
    private MQTTConfig mqtt;
}
