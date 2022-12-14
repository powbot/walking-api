package org.powbot.dax.engine;

import org.powbot.api.InteractableEntity;
import org.powbot.api.Locatable;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Movement;
import org.powbot.api.rt4.Players;
import org.powbot.dax.shared.helpers.General;
import org.powbot.dax.shared.helpers.Timing;

import java.util.Random;

public class WaitFor {

    private static final Random random = new Random();

    public static Condition getNotMovingCondition(){
        return new Condition() {
            final Tile initialTile = Players.local().tile();
            final long movingDelay = 1300, startTime = System.currentTimeMillis();

            @Override
            public Return active() {
                if (Timing.timeFromMark(startTime) > movingDelay && initialTile.equals(Players.local().tile()) && !Players.local().inMotion()) {
                    return Return.FAIL;
                }
                return Return.IGNORE;
            }
        };
    }

    public static int getMovementRandomSleep(Locatable positionable){
        return getMovementRandomSleep((int) Players.local().tile().distanceTo(positionable));
    }

    public static int getMovementRandomSleep(int distance){
        final double multiplier = Movement.running() ? 0.3 : 0.6;
        final int base = random(1800, 2400);
        if (distance > 25){
            return base;
        }
        int sleep = (int) (multiplier * distance);
        return (int) General.randomSD(base * .8, base * 1.2, base, base * 0.1) + sleep;
    }


    public static Return isOnScreenAndClickable(InteractableEntity clickable){
        return WaitFor.condition(getMovementRandomSleep(clickable), () -> (
                clickable.inViewport(true) ? Return.SUCCESS : Return.IGNORE));
    }

    public static Return condition(int timeout, Condition condition){
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + timeout){
            switch (condition.active()){
                case SUCCESS: return Return.SUCCESS;
                case FAIL: return Return.FAIL;
                case IGNORE: milliseconds(75);
            }
        }
        return Return.TIMEOUT;
    }

    /**
     *
     * @param timeout
     * @param condition
     * @param <V>
     * @return waits {@code timeout} for the return value to not be null.
     */
    public static <V> V getValue(int timeout, ReturnCondition<V> condition){
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + timeout){
            V v = condition.getValue();
            if (v != null){
                return v;
            }
            milliseconds(25);
        }
        return null;
    }

    public static int random(int low, int high) {
        return random.nextInt((high - low) + 1) + low;
    }

    public static Return milliseconds(int low, int high){
        org.powbot.api.Condition.sleep(General.random(low, high));
        return Return.IGNORE;
    }

    public static Return milliseconds(int amount){
        return milliseconds(amount, amount);
    }


    public enum Return {
        TIMEOUT,    //EXIT CONDITION BECAUSE OF TIMEOUT
        SUCCESS,    //EXIT CONDITION BECAUSE SUCCESS
        FAIL,       //EXIT CONDITION BECAUSE OF FAILURE
        IGNORE      //NOTHING HAPPENS, CONTINUE CONDITION

    }

    public interface ReturnCondition <V> {
        V getValue();
    }

    public interface Condition{
        Return active();
        default Condition combine(Condition a){
            Condition b = this;
            return () -> {
                Return result = a.active();
                return result != Return.IGNORE ? result : b.active();
            };
        }
    }

}
