package net.bdavies;

import com.github.mbelling.ws281x.Color;
import com.github.mbelling.ws281x.LedStripType;
import com.github.mbelling.ws281x.Ws281xLedStrip;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.config.StripConfig;
import net.bdavies.fx.Effect;
import net.bdavies.fx.FXUtil;
import net.bdavies.fx.basic.Solid;
import net.bdavies.mqtt.ChangeTopic;
import net.bdavies.networking.ReactiveUDP;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

@Slf4j
public class Strip {

    private static DatagramSocket socket;

    @Getter
    private final int ledsCount;
    private final int gpioPin;
    @Getter
    private final String id;
    private final int frequencyHz;
    private final int dma;
    private final int pwmChannel;
    private final boolean invert;
    private final LedStripType stripType;
    private final boolean clearOnExit, clearOnBoot;
    private final Color[] pixels;
    private final SetupType type;
    private final Application application;
    private final boolean[] pixelChanged;
    @Getter
    private final ReactiveUDP reactiveUDP;
    private int brightness;
    private Ws281xLedStrip productionStrip;
    private DevFrame frame;
    @Getter
    private Effect currentEffect;
    @Getter
    private boolean runEffect;
    private int oldBrightness;
    private long lastUpdateInMillis = System.currentTimeMillis();
    @Getter
    private Color currentColor;
    private boolean brightnessChange = false;
    private int toBrightness = 0;
    private long lastBrightnessChange = System.currentTimeMillis();
    private int lastBrightnessUsed = 0;
    @Getter
    private State state;
    private boolean colorChange = false;
    private Color toColor = Color.BLACK;
    private long lastColorChange = System.currentTimeMillis();
    private int blendAmt = 0;
    private Color oldColor = Color.RED;

    public Strip(StripConfig config, SetupType type, Application application) {
        if (Strip.socket == null) {
            try {
                Strip.socket = new DatagramSocket(3457);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        this.ledsCount = config.getLedCount();
        this.gpioPin = config.getPinNumber();
        this.type = type;
        this.application = application;
        this.frequencyHz = 800000;
        this.dma = 10;
        this.pwmChannel = (gpioPin == 18 || gpioPin == 12 || gpioPin == 10) ? 0 : 1;
        this.invert = false;
        this.stripType = LedStripType.WS2811_STRIP_GRB;
        this.clearOnExit = false;
        this.clearOnBoot = config.isClearOnBoot();
        this.pixels = new Color[ledsCount];
        this.pixelChanged = new boolean[ledsCount];
        this.runEffect = true;
        this.id = config.getId();
        this.oldBrightness = config.getBrightness();
        setCurrentColor(Color.RED);
        setBrightness(config.getBrightness());
        setCurrentEffect(new Solid(new HashMap<>()), "Solid");
        this.reactiveUDP = new ReactiveUDP(this, Strip.socket);
    }

    public synchronized void init() {

        if (type == SetupType.PROD) {
            productionStrip = new Ws281xLedStrip(ledsCount, gpioPin, frequencyHz, dma, brightness, pwmChannel,
                    invert, stripType, clearOnExit);
            log.info("Setup Strip");
        } else {
            frame = new DevFrame(ledsCount, this.getId());
            frame.getPanel().start();
        }

        if (clearOnBoot) {
            setStrip(Color.BLACK);
            render();
        }
    }

    public void render() {
        try {
            if (type == SetupType.PROD) {
                boolean brightnessChanged = false;
                boolean didAPixelChange = false;
                if (lastBrightnessUsed != brightness) {
                    productionStrip.setBrightness(brightness);
                    lastBrightnessUsed = brightness;
                    brightnessChanged = true;
                }
                for (int i = 0; i < pixels.length; i++) {
                    if (pixelChanged[i]) {
                        productionStrip.setPixel(i, pixels[i]);
                        pixelChanged[i] = false;
                        didAPixelChange = true;
                    }
                }
                if (brightnessChanged || didAPixelChange) {
                    productionStrip.render();
                }
            } else {
                // Render to JFrame
                for (int i = 0; i < pixels.length; i++) {
                    if (pixelChanged[i]) {
                        pixelChanged[i] = false;
                        frame.getPanel().setPixel(i, pixels[i], brightness);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void off() {
        if (this.state == State.ON) {
            this.oldBrightness = brightness;
            this.setBrightness(0);
        }
    }

    public synchronized void on() {
        if (this.state == State.OFF) {
            this.setBrightness(oldBrightness);
        }
    }

    private void setState(State state) {
        this.state = state;
        application.getMqttClient().publishChange(this, ChangeTopic.STATE, state.name());
    }

    public synchronized void setBrightness(int brightness) {
        if (this.brightness == 0) {
            setState(State.ON);
        }
        application.getMqttClient().publishChange(this, ChangeTopic.BRIGHTNESS,
                String.valueOf(brightness));
        log.info("setBrightness({}): Strip", brightness);
        this.runEffect = true;
        this.toBrightness = brightness;
        this.brightnessChange = true;
    }


    public synchronized void setPixel(int pixel, int red, int green, int blue) {
        setPixel(pixel, new Color(red, green, blue));
    }


    public synchronized void setPixel(int pixel, Color color) {
        if (pixel < 0 || pixel > ledsCount - 1) return;
        Color c = this.pixels[pixel];
        if (c != null) {
            if (c.getRed() == color.getRed() && c.getGreen() == color.getGreen() && c.getBlue() == color.getBlue()) {
                return;
            }
        }
        this.pixelChanged[pixel] = true;
        this.pixels[pixel] = color;
    }


    public synchronized void setStrip(int red, int green, int blue) {
        setStrip(new Color(red, green, blue));
    }

    public synchronized void setCurrentColor(Color currentColor) {

        if (this.currentColor != null) {
            this.oldColor = this.currentColor;
            this.toColor = currentColor;
            this.colorChange = true;
        } else {
            this.currentColor = currentColor;
        }


        String bits = String.format("#%02x%02x%02x", currentColor.getRed(), currentColor.getGreen(),
                currentColor.getBlue());

        application.getMqttClient().publishChange(this, ChangeTopic.COLOR,
                bits);
    }

    public synchronized void setStrip(Color color) {
        for (int i = 0; i < ledsCount; i++) {
            setPixel(i, color);
        }
    }


    public synchronized Color getPixel(int pixel) {
        return this.pixels[pixel];
    }

    public synchronized void loop(long curTime) {
        Effect e = this.currentEffect;
        if (this.isRunEffect()) {
            if (curTime - lastUpdateInMillis >= e.delay) {
                lastUpdateInMillis = curTime;
                e.render(this);
            }
        }

        loopColor(curTime);
        loopBrightness(curTime);

    }

    public synchronized void setCurrentEffect(Effect effect, String name) {
        this.currentEffect = effect;
        application.getMqttClient().publishChange(this, ChangeTopic.FX, name);

    }

    public synchronized void shutdown() {
        off();
        this.reactiveUDP.stop();
        if (this.frame != null) {
            frame.getPanel().stop();
            frame.dispose();
        }
    }

    public void loopBrightness(long curTime) {
        int alphaDecayDelay = 5;
        if (this.brightnessChange) {
            if (curTime - lastBrightnessChange >= alphaDecayDelay) {
                if (toBrightness < brightness) {
                    lastBrightnessChange = curTime;
                    brightness--;

                } else if (toBrightness > brightness) {
                    lastBrightnessChange = curTime;
                    brightness++;
                } else {
                    if (this.brightness == 0) {
                        System.out.println("Yo");
                        this.runEffect = false;
                        setState(State.OFF);
                    }
                    application.getMqttClient().publishChange(this, ChangeTopic.BRIGHTNESS,
                            String.valueOf(brightness));
                    brightnessChange = false;
                }


                render();
            }
        }
    }

    public void loopColor(long curTime) {
        int alphaDecayDelay = 5;
        if (this.colorChange) {
            if (curTime - lastColorChange >= alphaDecayDelay) {
                lastColorChange = curTime;
                if (blendAmt < 255) {
                    this.currentColor = FXUtil.colorBlend(this.oldColor, this.toColor, ++blendAmt);
                } else {
                    this.colorChange = false;
                    this.blendAmt = 0;
                    this.currentColor = this.toColor;
                }
                render();
            }
        }
    }

    public enum SetupType {
        DEV,
        PROD
    }

    private enum State {
        ON,
        OFF
    }
}
