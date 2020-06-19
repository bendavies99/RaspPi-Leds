package net.bdavies.math;

import net.bdavies.Time;

public class Beat {

    public static double beat(int bpm, int timebase) {
        return ((((Time.getMillis() - timebase) * (bpm) * 5)) >> 16);
    }

    public static double beatsin(int bpm, int low, int high) {
        double beat = beat(bpm, 0);
        double beatsin = Math.sin(beat);

        int rangewidth = high - low;
        int scaledbeat = (int) ((beatsin) * (4 + (float) rangewidth));
        return Math.abs((float) low + scaledbeat);
    }

}
