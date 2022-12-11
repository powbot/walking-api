package org.powbot.dax.shared.helpers;

import org.powbot.api.Locatable;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.interaction.InteractionHelper;
import org.powbot.mobile.rlib.generated.RBank;
import org.powbot.util.TransientGetter3D;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BankHelper {


    private static final Predicate<GameObject> BANK_OBJECT_FILTER = i -> {
        String lowerCase = i.name().toLowerCase();
        return (lowerCase.contains("bank") || lowerCase.equals("exchange booth") || lowerCase.equals("open chest")) &&
                i.actions().contains("Collect") && i.actions().contains("Bank");
    };

    public static boolean isInBank(){
        return isInBank(Players.local().tile());
    }

    public static boolean isInBank(Locatable positionable){
        List<GameObject> bankObjects = Objects.stream(15, GameObject.Type.INTERACTIVE).filter(BANK_OBJECT_FILTER).list();
        if (bankObjects.size() == 0){
            return false;
        }
        GameObject bankObject = bankObjects.get(0);
        return positionable.tile().distanceTo(bankObject) < 5;
    }

    /**
     *
     * @return whether if the action succeeded
     */
    public static boolean openBank() {
        return Bank.opened() || InteractionHelper
	        .click(InteractionHelper.getGameObject(BANK_OBJECT_FILTER), "Bank");
    }

    /**
     *
     * @return bank screen is open
     */
    public static boolean openBankAndWait(){
        if (Bank.opened()){
            return true;
        }
        GameObject object = InteractionHelper.getGameObject(BANK_OBJECT_FILTER);
        return InteractionHelper.click(object, "Bank") && waitForBankScreen(object);
    }

    private static boolean waitForBankScreen(GameObject object){
        return WaitFor.condition(WaitFor
	        .getMovementRandomSleep(object), ((WaitFor.Condition) () -> Bank.opened() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE).combine(
	        WaitFor.getNotMovingCondition())) == WaitFor.Return.SUCCESS;
    }

    public static Set<Tile> getBuilding(Locatable positionable){
        return Arrays.stream(RBank.computeBuilding(positionable.tile())).map(t -> new Tile(t.x(), t.y(), t.floor())).collect(Collectors.toSet());
    }

}
