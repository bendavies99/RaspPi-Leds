package net.bdavies;

import com.github.mbelling.ws281x.Color;
import com.github.mbelling.ws281x.LedStripType;
import com.github.mbelling.ws281x.Ws281xLedStrip;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.config.StripConfig;
import net.bdavies.fx.Effect;
import net.bdavies.fx.FXUtil;
import net.bdavies.fx.basic.Solid;
import net.bdavies.mqtt.ChangeTopic;
import net.bdavies.networking.ReactiveUDP;

import java.util.HashMap;

@Slf4j
public class Strip {

    private static int DMA_CHAN = 10;

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
    private int lastBrightnessUsed = 0;
    @Getter
    private State state;
    private boolean colorChange = false;
    private Color toColor = Color.BLACK;
    private int blendAmt = 0;
    private Color oldColor = Color.RED;
    @Getter
    @Setter
    private boolean forceRender = false;

    public Strip(StripConfig config, SetupType type, Application application) {
        this.ledsCount = config.getLedCount();
        this.gpioPin = config.getPinNumber();
        this.type = type;
        this.application = application;
        this.frequencyHz = 800000;
        this.dma = Strip.DMA_CHAN;
        Strip.DMA_CHAN += 1;
        if (Strip.DMA_CHAN > 14) Strip.DMA_CHAN = 14;
        this.pwmChannel = (gpioPin == 18 || gpioPin == 12 || gpioPin == 10 || gpioPin == 21) ? 0 : 1;
        this.invert = false;
        this.stripType = LedStripType.WS2811_STRIP_GRB;
        this.clearOnExit = false;
        this.clearOnBoot = config.isClearOnBoot();
        this.pixels = new Color[ledsCount];
        this.pixelChanged = new boolean[ledsCount];
        this.runEffect = true;
        this.id = config.getId();
        this.oldBrightness = config.getBrightness();
        setState(State.ON);
        setCurrentColor(Color.RED);
        setBrightness(config.getBrightness());
        setCurrentEffect(new Solid(new HashMap<>()), "Solid");
        this.reactiveUDP = new ReactiveUDP(this, config.getReactivePort());
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
            setCurrentColor(Color.BLACK);
            render();
        }
    }

    public void render() {
        try {
            boolean brightnessChanged = false;

            if (lastBrightnessUsed != brightness) {
                lastBrightnessUsed = brightness;
                brightnessChanged = true;
            }
            if (type == SetupType.PROD) {
                boolean didAPixelChange = false;
                if (brightnessChanged) {
                    productionStrip.setBrightness(brightness);
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

                    if (pixelChanged[i] || brightnessChanged) {
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


    public void setPixel(int pixel, int red, int green, int blue) {
        setPixel(pixel, new Color(red, green, blue));
    }


    public void setPixel(int pixel, Color color) {
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


    public void setStrip(int red, int green, int blue) {
        setStrip(new Color(red, green, blue));
    }

    public void setCurrentColor(Color currentColor) {

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

    public void setStrip(Color color) {
        for (int i = 0; i < ledsCount; i++) {
            setPixel(i, color);
        }
    }


    public Color getPixel(int pixel) {
        return this.pixels[pixel];
    }

    public void loop(long curTime) {
        Effect e = this.currentEffect;
        if (this.isRunEffect()) {
            if (curTime - lastUpdateInMillis >= e.delay) {
                lastUpdateInMillis = curTime;
                e.render(this);
            }
        } else {
            if (curTime - lastUpdateInMillis >= 10000) {
                lastUpdateInMillis = curTime;
                render();
            }
        }


    }

    public void setCurrentEffect(Effect effect, String name) {
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

    public void loopBrightness() {
        if (this.brightnessChange) {
            this.forceRender = true;
            if (toBrightness < brightness) {
                brightness -= 5;
                if (brightness < 0) brightness = 0;
            } else if (toBrightness > brightness) {
                brightness += 5;
                if (brightness > 255) brightness = 255;
            } else {
                if (this.brightness == 0) {
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

    public void loopColor() {
        if (this.colorChange) {
            this.forceRender = true;
            if (blendAmt < 255) {
                blendAmt += 5;
                blendAmt = Math.min(blendAmt, 255);
                this.currentColor = FXUtil.colorBlend(this.oldColor, this.toColor, blendAmt);
            } else {
                this.colorChange = false;
                this.blendAmt = 0;
                this.currentColor = this.toColor;
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
