package net.bdavies.mqtt;

import lombok.Getter;

public enum ChangeTopic {

    COLOR("/c"),
    BRIGHTNESS("/b"),
    STATE("/s"),
    FX("/fx");

    @Getter
    private final String topic;

    ChangeTopic(String t) {
        this.topic = t;
    }
}
