package net.bdavies.networking;

import com.github.mbelling.ws281x.Color;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.Strip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

@Slf4j
public class ReactiveUDP implements Runnable {

    private final Strip strip;
    private DatagramSocket socket;
    private final byte[] buf;
    private final byte[] ledBuf;
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    @Getter
    private final Color[] data;
    private Thread thread;
    private boolean running;

    public ReactiveUDP(Strip s, int port) {
        this.strip = s;
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
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

        this.socket.close();

    }

    @Override
    public void run() {

        while (running) {

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                    System.arraycopy(packet.getData(), 0,
                            ledBuf, 0, ledBuf.length);


                    int idx = 0;
                    for (int i = 0; i < ledBuf.length; i += 3) {
                        if(idx > strip.getLedsCount() - 1) break;
                        int red = ledBuf[i] & 0xff;
                        int green = ledBuf[i + 1] & 0xff;
                        int blue = ledBuf[i + 2] & 0xff;

                        //log.info("R: {}, G: {}, B: {}", red, green, blue);

                        data[idx++] = new Color(red, green, blue);
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
