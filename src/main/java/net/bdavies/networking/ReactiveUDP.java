package net.bdavies.networking;

import com.github.mbelling.ws281x.Color;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.Strip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

@Slf4j
public class ReactiveUDP implements Runnable {

    private final Strip strip;
    private final DatagramSocket socket;
    private final byte[] buf;
    private final byte[] ledBuf;
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    @Getter
    private final Color[] data;
    private Thread thread;
    private boolean running;

    public ReactiveUDP(Strip s, DatagramSocket socket) {
        this.strip = s;
        this.socket = socket;
        buf = new byte[(s.getId() + "\\e").getBytes().length + (s.getLedsCount() * 4)]; //Name + \e + numOfLeds [idx, r g b]
        ledBuf = new byte[(s.getLedsCount() * 4)];
        data = new Color[s.getLedsCount()];
        Arrays.fill(data, Color.BLACK);
    }

    public synchronized void start() {
        if (running) return;

        running = true;

        thread = new Thread(this);
        thread.start();

    }


    public synchronized void stop() {
        if (!running) return;
        running = false;

        try {
            thread.join(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        while (running) {

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                if (received.contains(strip.getId() + "\\e")) {

                    System.arraycopy(packet.getData(), (strip.getId() + "\\e").getBytes().length,
                            ledBuf, 0, ledBuf.length);


                    for (int i = 0; i < ledBuf.length; i += 4) {
                        int idx = ledBuf[i] & 0xff;
                        int red = ledBuf[i + 1] & 0xff;
                        int green = ledBuf[i + 2] & 0xff;
                        int blue = ledBuf[i + 3] & 0xff;

                        //log.info("R: {}, G: {}, B: {}", red, green, blue);

                        data[idx] = new Color(red, green, blue);
                    }

                } else {
                    continue;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

//            try {
//                //noinspection BusyWait
//                Thread.sleep((int) (1000 / Application.FPS));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }

    }
}
