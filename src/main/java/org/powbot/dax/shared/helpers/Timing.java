package org.powbot.dax.shared.helpers;


import java.util.concurrent.TimeUnit;

public class Timing {


    public static long currentTimeMillis(){
        return System.currentTimeMillis();
    }

    public static long timeFromMark(long ms){
        return Math.abs(currentTimeMillis() - ms);
    }

    public static String msToString(long millis){
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

}
