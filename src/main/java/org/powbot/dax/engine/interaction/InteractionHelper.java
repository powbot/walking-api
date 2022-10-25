package org.powbot.dax.engine.interaction;

import org.powbot.api.Locatable;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.shared.helpers.AccurateMouse;
import org.powbot.dax.shared.helpers.Timing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


public class InteractionHelper {

    public static boolean click(Interactive clickable, String... action){
        return click(clickable, action, null);
    }

    public static boolean click(Interactive clickable, String action, WaitFor.Condition condition){
        return click(clickable, new String[]{action}, condition);
    }

    /**
     * Interacts with nearby object and waits for {@code condition}.
     *
     * @param clickable clickable entity
     * @param action actions to click
     * @param condition condition to wait for after the click action
     * @return if {@code condition} is null, then return the outcome of condition.
     *          Otherwise, return the result of the click action.
     */
    public static boolean click(Interactive clickable, String[] action, WaitFor.Condition condition){
        if (clickable == null){
            return false;
        }

        if (clickable instanceof Item){
            List<String> asList = new ArrayList<>(Arrays.asList(action));
            return clickable.interact(m -> asList.contains(m.getAction())) && (condition == null || WaitFor.condition(Random.nextInt(7000, 8000), condition) == WaitFor.Return.SUCCESS);
        }

        Tile position = ((Locatable) clickable).tile();

        if (position != Tile.getNil() && !clickable.inViewport()){
            Movement.moveTo(position);
        }

        WaitFor.Return result = WaitFor.condition(WaitFor.getMovementRandomSleep(position), new WaitFor.Condition() {
            final long startTime = System.currentTimeMillis();
            @Override
            public WaitFor.Return active() {
                if (clickable.inViewport()){
                    return WaitFor.Return.SUCCESS;
                }
                if (Timing.timeFromMark(startTime) > 2000 && !Players.local().inMotion()){
                    return WaitFor.Return.FAIL;
                }
                return WaitFor.Return.IGNORE;
            }
        });

        if (result != WaitFor.Return.SUCCESS){
            return false;
        }

        if (!AccurateMouse.click(clickable, action)){
            if (Camera.pitch() < 90){
                Camera.pitch(Random.nextInt(90, 100));
            }
            return false;
        }

        return condition == null || WaitFor.condition(Random.nextInt(7000, 8500), condition) == WaitFor.Return.SUCCESS;
    }

    public static Item getItem(Predicate<Item> filter){
        return Inventory.stream().filter(filter).first();
    }

    public static Npc getRSNPC(Predicate<Npc> filter){
        return Npcs.stream().filter(filter).nearest().first();
    }

    public static GameObject getGameObject(Predicate<GameObject> filter){
        return Objects.stream(15).filter(filter).nearest().first();
    }

    public static GroundItem getGroundItem(Predicate<GroundItem> filter) {
        return GroundItems.stream().filter(filter).nearest().first();
    }

    public static boolean focusCamera(Interactive clickable){
        if (clickable == null){
            return false;
        }
        if (clickable.inViewport()){
            return true;
        }
        Tile tile = ((Locatable) clickable).tile();
        Camera.turnTo(tile);
        Camera.pitch((int) (100 - (tile.distanceTo(Players.local().tile()) * 4)));
        return clickable.inViewport();
    }


}
