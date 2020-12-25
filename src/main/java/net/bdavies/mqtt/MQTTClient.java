package net.bdavies.mqtt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.Application;
import net.bdavies.Strip;
import net.bdavies.command.BrightnessCommand;
import net.bdavies.command.ColorCommand;
import net.bdavies.command.SetEffectCommand;
import net.bdavies.command.StateCommand;
import net.bdavies.command.data.SetEffectData;
import net.bdavies.config.MQTTConfig;
import net.bdavies.config.StripConfig;
import net.bdavies.fx.FXUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class MQTTClient {

    private final MQTTConfig config;
    private final Application application;
    private MqttClient client;
    private final Timer timer;
    private int connectAttempts = 0;
    private boolean callbackSet = false;
    @Getter
    private boolean connected = false;

    public MQTTClient(Application application, MQTTConfig config) {
        this.config = config;
        this.application = application;
        timer = new Timer();
    }

    public void connect() {
        try {
            if(client == null) {
                client = new MqttClient("tcp://" + config.getBrokerIp() + ":" + config.getPort(),
                        "RaspPi-Leds:" + RandomStringUtils.randomAlphabetic(7), new MemoryPersistence());
            }
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            try {
                connectOptions.setUserName(config.getUsername());
                connectOptions.setPassword(config.getPassword().toCharArray());
            } catch (IllegalArgumentException e) {
                if (connectAttempts == 0)
                    log.warn("No Username or password supplied, this may be a security risk.");
            }

            connectOptions.setCleanSession(true);
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setConnectionTimeout(5); // 5 second

            try {

                connectAttempts++;

                client.connectWithResult(connectOptions);
                client.subscribe("Rasp-Pi-Leds");
                log.info("Connected to the MQTT Server");
                connectAttempts = 0;
                connected = true;
                if(!callbackSet) {
                client.setCallback(new MqttCallback() {

                    final SetEffectCommand setEffect = new SetEffectCommand();
                    final StateCommand stateCommand = new StateCommand();
                    final ColorCommand colorCommand = new ColorCommand();
                    final BrightnessCommand brightnessCommand = new BrightnessCommand();
                    
                    public MqttCallback() {
                       callbackSet = true;
                    }

                    @Override
                    public void connectionLost(Throwable cause) {
                        log.error("MQTT Lost Connection Restarting...");
                        connected = false;
                        connect();
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        try {
                            log.info("Message Received - Topic: {}, Message: {}", topic, new String(message.getPayload()));
                            Gson gson = new GsonBuilder().create();

                            if (topic.contains("/")) {
                                String command = topic.split("/")[1].trim().toLowerCase();
                                String stripId = topic.split("/")[0];

                                log.info(command);
                                if (command.equals("sfx")) {
                                    setEffect.run(application, application.getStripById(stripId),
                                            new SetEffectData(new String(message.getPayload()), new HashMap<>()));
                                } else if (command.equals("col")) {
                                    colorCommand.run(application, application.getStripById(stripId),
                                            FXUtil.hexToColor(new String(message.getPayload())));
                                } else if (command.equals("bri")) {
                                    brightnessCommand.run(application, application.getStripById(stripId),
                                            Integer.valueOf(new String(message.getPayload())));
                                } else {
                                    log.warn("Command does not exist: {}, command will not run...", topic);
                                }
                            } else {
                                //Assume State
                                stateCommand.run(application, application.getStripById(topic),
                                        new String(message.getPayload()));
                            }
                        } catch (Exception e) {
                            log.error("Something went wrong", e);
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
                    timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            client.publish("ping", "ping".getBytes(), 2, false);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000, 5000);
                }
                
            } catch (MqttException e) {
                if (connectAttempts == 1) {
                    log.error("Failed to connect to MQTT Server.. Will try to reconnect in the background and will " +
                            "log when it has connected.");
                } else {
                    log.error("Still trying to connect to MQTT Server, please ensure it is accessible, att connect attempt: {}", connectAttempts);
                }
                // Application.getService().submit(this::connect);
                this.connect();
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishChange(Strip strip, ChangeTopic changeTopic, String data) {
        try {
            client.publish(strip.getId() + changeTopic.getTopic(), data.getBytes(), 2, false);
            log.info(strip.getId() + changeTopic.getTopic() + ": " + changeTopic.name() + " -  has been set to " + data);
        } catch (MqttException e) {
            log.error("Something went wrong when attempting to public to: " + strip.getId() + "/" + changeTopic, e);
            e.printStackTrace();
        }
    }

    public void listenToStrip(StripConfig config) {
        try {
            MQTTDiscovery discovery = new MQTTDiscovery(config);
            String uid = "RaspPi-Leds_" + config.getId();
            client.publish("homeassistant/light/" + uid + "/config",
                    new GsonBuilder().create().toJson(discovery).getBytes(), 2, true);
            log.info("Sent discovery");
            client.subscribe(config.getId()); //State
            client.subscribe(config.getId() + "/col"); // Color
            client.subscribe(config.getId() + "/bri"); // Brightness
            client.subscribe(config.getId() + "/sfx"); // Set Effects
            log.info("Now Listening to Strip: {}:- GPIO{}", config.getId(), config.getPinNumber());
        } catch (MqttException e) {
            log.error("Something went wrong when attempting to listen to: " + config.getId(), e);
        }
    }

    public void shutdown() {
        try {
            client.disconnectForcibly(1);
            client.close();
        } catch (MqttException e) {
            log.error("Failed to disconnect");
            e.printStackTrace();
        }
    }
}
