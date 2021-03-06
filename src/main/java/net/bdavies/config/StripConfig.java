package net.bdavies.config;

import lombok.Getter;

public class StripConfig {
    @Getter
    private String name;
    @Getter
    private int pinNumber;
    @Getter
    private int ledCount;
    @Getter
    private int brightness;
    @Getter
    private boolean clearOnBoot;
    @Getter
    private String id;
    @Getter
    private int reactivePort;
}
