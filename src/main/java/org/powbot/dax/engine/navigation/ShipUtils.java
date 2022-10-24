package org.powbot.dax.engine.navigation;

import org.tribot.api.General;
import org.tribot.api2007.Game;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.powbot.dax.engine.WaitFor;


public class ShipUtils {

    private static final Tile[] SPECIAL_CASES = new Tile[]{new Tile(2663, 2676, 1)};

    public static boolean isOnShip() {
        Tile playerPos = Players.local().tile();
        for (RSTile specialCase : SPECIAL_CASES){
            if (new RSArea(specialCase, 5).contains(playerPos)){
                return true;
            }
        }
        return getGangplank() != null
                && Players.local().tile().getPlane() == 1
                && Objects.getAll(10, Filters.Objects.nameEquals("Ship's wheel", "Ship's ladder", "Anchor")).length > 0;
    }

    public static boolean crossGangplank() {
        RSObject gangplank = getGangplank();
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
        return WaitFor.condition(General.random(2500, 3000), () -> !ShipUtils
	        .isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static RSObject getGangplank(){
        RSObject[] obj = Objects.findNearest(7, Filters.Objects.nameEquals("Gangplank").and(Filters.Objects.actionsContains("Cross")));
        return obj.length > 0 ? obj[0] : null;
    }

}
