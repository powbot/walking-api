package org.powbot.dax.engine.navigation.fairyring.letters;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Component;
import org.powbot.api.rt4.Varpbits;
import org.powbot.api.rt4.Widgets;
import org.powbot.dax.engine.navigation.fairyring.FairyRing;

public enum FirstLetter {
    A(0),
    B(3),
    C(2),
    D(1)
    ;
    int value;
    FirstLetter(int value){
        this.value = value;
    }


    public int getValue(){
        return value;
    }

    public static final int
            VARBIT = 3985,
            CLOCKWISE_CHILD = 19,
            ANTI_CLOCKWISE_CHILD = 20;

    private static int get(){
        return Varpbits.value(VARBIT);
    }

    public boolean isSelected(){
        return get() == this.value;
    }

    public boolean turnTo(){
        int current = get();
        int target = getValue();
        if(current == target) {
            return true;
        }
        int diff = current - target;
        int abs = Math.abs(diff);
        if(abs == 2){
            return Random.nextBoolean() ? turnClockwise(2) : turnAntiClockwise(2);
        } else if(diff == 3 || diff == -1){
            return turnClockwise(1);
        } else {
            return turnAntiClockwise(1);
        }
    }

    public static boolean turnClockwise(int rotations){
        if(rotations == 0)
            return true;
        Component iface = getClockwise();
        final int value = get();
        if(iface == null)
            return false;
        if(!iface.click()){
            System.out.println("Failed to click clockwise.");
            return false;
        }
        if( !Condition.wait(() -> get() != value ,250, 10)){
            return false;
        }
        return turnClockwise(--rotations);
//        return iface != null && iface.click()
//                && Timing.waitCondition(() -> get() != value ,2500)
//                && turnClockwise(--rotations);
    }

    public static boolean turnAntiClockwise(int rotations){
        if(rotations <= 0)
            return true;
        Component iface = getAntiClockwise();
        final int value = get();
        return iface.valid() && iface.click()
                && Condition.wait(() -> get() != value ,250, 10)
                && turnAntiClockwise(--rotations);
    }

    private static Component getClockwise() {
        return Widgets.component(FairyRing.INTERFACE_MASTER, CLOCKWISE_CHILD);
    }
    private static Component getAntiClockwise() {
        return Widgets.component(FairyRing.INTERFACE_MASTER, ANTI_CLOCKWISE_CHILD);
    }
}
