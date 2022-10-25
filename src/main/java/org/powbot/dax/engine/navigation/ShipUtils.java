package org.powbot.dax.engine.navigation;

import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.GameObject;
import org.powbot.api.rt4.Objects;
import org.powbot.api.rt4.Players;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.shared.helpers.AreaHelper;


public class ShipUtils {

    private static final Tile[] SPECIAL_CASES = new Tile[]{new Tile(2663, 2676, 1)};

    public static boolean isOnShip() {
        Tile playerPos = Players.local().tile();
        for (Tile specialCase : SPECIAL_CASES){
            if (AreaHelper.fromCenter(specialCase, 5).contains(playerPos)){
                return true;
            }
        }
        return getGangplank() != null
                && Players.local().tile().floor() == 1
                && Objects.stream(10).name("Ship's wheel", "Ship's ladder", "Anchor").isNotEmpty();
    }

    public static boolean crossGangplank() {
        GameObject gangplank = getGangplank();
        if (gangplank == null){
            return false;
        }
        if (!gangplank.click("Cross")){
            return false;
        }
//        if (WaitFor
//	        .condition(1000, () -> Game.getCrosshairState() == 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
//            return false;
//        }
        return WaitFor.condition(Random.nextInt(2500, 3000), () -> !ShipUtils
	        .isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static GameObject getGangplank(){
        GameObject obj = Objects.stream(7).name("Gangplank").action("Cross").nearest().first();
        return obj.valid() ? obj : null;
    }

}
