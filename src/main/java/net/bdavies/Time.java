package net.bdavies;

public class Time {


    private static long START_TIME = System.currentTimeMillis();
    // Roll over roughly every 24 days due to signed integer size limits

    public static long getMillis() {
        long timeDif = System.currentTimeMillis() - START_TIME;
        if (timeDif >= 2045382647) {
            START_TIME = System.currentTimeMillis();
        }
        return timeDif;
    }

}
