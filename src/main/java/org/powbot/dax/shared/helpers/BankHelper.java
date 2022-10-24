package org.powbot.dax.shared.helpers;

import org.powbot.api.Locatable;
import org.powbot.api.Tile;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Game;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.interaction.InteractionHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class BankHelper {

    private static final Predicate<RSObject> BANK_OBJECT_FILTER = Filters.Objects.nameContains("bank", "Bank", "Exchange booth", "Open chest")
            .and(Filters.Objects.actionsContains("Collect"))
            .and(Filters.Objects.actionsContains("Bank"));

    public static boolean isInBank(){
        return isInBank(Players.local().tile());
    }

    public static boolean isInBank(Positionable positionable){
        RSObject[] bankObjects = Objects.findNearest(15, BANK_OBJECT_FILTER);
        if (bankObjects.length == 0){
            return false;
        }
        RSObject bankObject = bankObjects[0];
        HashSet<RSTile> building = getBuilding(bankObject);
        return building.contains(positionable.getPosition()) || (building.size() == 0 && positionable.getPosition().distanceTo(bankObject) < 5);
    }

    /**
     *
     * @return whether if the action succeeded
     */
    public static boolean openBank() {
        return Banking.isBankScreenOpen() || InteractionHelper
	        .click(InteractionHelper.getRSObject(BANK_OBJECT_FILTER), "Bank");
    }

    /**
     *
     * @return bank screen is open
     */
    public static boolean openBankAndWait(){
        if (Banking.isBankScreenOpen()){
            return true;
        }
        RSObject object = InteractionHelper.getRSObject(BANK_OBJECT_FILTER);
        return InteractionHelper.click(object, "Bank") && waitForBankScreen(object);
    }

    public static Set<Tile> getBuilding(Locatable positionable){
        return computeBuilding(positionable, Game.getSceneFlags(), new HashSet<>());
    }

    private static Set<Tile> computeBuilding(Locatable positionable, byte[][][] sceneFlags, Set<Tile> tiles){
        try {
            Tile tile = positionable.tile();
            int localX = tile.localX(), localY = tile.localY(), localZ = tile.floor();
            if (localX < 0 || localY < 0 || localZ < 0){
                return tiles;
            }
            if (sceneFlags.length <= localZ || sceneFlags[localZ].length <= localX || sceneFlags[localZ][localX].length <= localY){ //Not within bounds
                return tiles;
            }
            if (sceneFlags[localZ][localX][localY] < 4){ //Not a building
                return tiles;
            }
            if (!tiles.add(local.toWorldTile())){ //Already computed
                return tiles;
            }
            computeBuilding(new Tile(localX, localY + 1, localZ, Tile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
            computeBuilding(new Tile(localX + 1, localY, localZ, Tile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
            computeBuilding(new Tile(localX, localY - 1, localZ, Tile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
            computeBuilding(new Tile(localX - 1, localY, localZ, Tile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return tiles;
    }

    private static boolean isInBuilding(Tile localRSTile, byte[][][] sceneFlags) {
        return !(sceneFlags.length <= localRSTile.floor()
                    || sceneFlags[localRSTile.floor()].length <= localRSTile.getX()
                    || sceneFlags[localRSTile.floor()][localRSTile.getX()].length <= localRSTile.getY())
                && sceneFlags[localRSTile.floor()][localRSTile.getX()][localRSTile.getY()] >= 4;
    }

    private static boolean waitForBankScreen(RSObject object){
        return WaitFor.condition(WaitFor
	        .getMovementRandomSleep(object), ((WaitFor.Condition) () -> Banking.isBankScreenOpen() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE).combine(
	        WaitFor.getNotMovingCondition())) == WaitFor.Return.SUCCESS;
    }

}
