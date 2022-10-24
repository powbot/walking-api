package org.powbot.dax.shared.helpers;

import org.powbot.api.Random;

public class General {

    public static int randomSD(int min, int max, int mean, int sd){
        return Random.nextGaussian(min, max, mean , sd);
    }

    public static int randomSD(int min, int max, int sd){
        return Random.nextGaussian(min, max, ((min + max) / 2d) / sd);
    }

    public static double randomSD(double min, double max, double mean, double sd){
        double gaussian = Random.nextGaussian();
        double output = mean + (gaussian * sd);
        if(output < min)
            return min;
        return Math.min(output, max);
    }
    public static int random(int min, int max){
        if(min == max)
            return min;
        return Random.nextInt(min, max);
    }
}
