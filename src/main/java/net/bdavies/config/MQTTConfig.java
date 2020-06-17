package net.bdavies.config;

import lombok.Getter;
import lombok.Setter;

public class MQTTConfig {

    @Getter
    @Setter
    private String username = "", password = "";

    @Getter
    private String brokerIp;

    @Getter
    @Setter
    private int port = 1883;

}
