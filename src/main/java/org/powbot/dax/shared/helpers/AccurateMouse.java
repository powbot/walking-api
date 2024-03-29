package org.powbot.dax.shared.helpers;

import org.powbot.api.*;
import org.powbot.api.rt4.Movement;
import org.powbot.dax.engine.WaitFor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class does NOT examine objects.
 * <p>
 * clickAction should never include the target entity's name. Just the action.
 */
public class AccurateMouse {

    public static void click(int x, int y) {
        click(x, y, 1);
    }

    public static void click(int x, int y, int button) {
        click(new Point(x, y), button);
    }

    public static void click(Point point) {
        click(point.getX(), point.getY(), 1);
    }

    public static void click(Point point, int button) {
        if(button == 3){
            Input.press(new Point(point.getX(), point.getY()));
        } else if(button == 1){
            Input.tap(new Point(point.getX(), point.getY()));
        }
    }


    public static boolean click(Interactable clickable, String... clickActions) {
        return action(clickable, false, clickActions);
    }

    public static boolean hover(Interactable clickable, String... clickActions) {
        return action(clickable, true, clickActions);
    }

    public static boolean clickMinimap(Locatable tile) {
        if (tile == null) {
            return false;
        }
        Tile currentDestination = Movement.destination();
        if (currentDestination != Tile.getNil() && currentDestination.equals(tile)) {
            return true;
        }

        if (!Projection.isInMinimap(tile)) {
            return false;
        }

        Movement.step(tile.tile());
        Tile newDestination = WaitFor.getValue(250, () -> {
            Tile destination = Movement.destination();
            return destination == Tile.getNil() || destination.equals(currentDestination) ? null : destination;
        });
        if(newDestination != null){
            if(newDestination.equals(tile) || newDestination.distanceTo(tile) <= 3) {
                return true;
            }
        }

        return false;
    }

    public static boolean action(Interactable clickable, boolean hover, String... clickActions) {
        if (clickable == null) {
            return false;
        }
        return action(clickable, clickActions);
    }

    /**
     * @param clickable    target entity
     * @param clickActions actions to click or hover. Do not include {@code targetName}
     * @return whether action was successful.
     */
    private static boolean action(Interactable clickable, String... clickActions) {
        return attemptAction(clickable, clickActions);
    }


    /**
     * Clicks or hovers desired action of entity.
     *
     * @param clickable    target entity
     * @param clickActions actions
     * @return result of action
     */
    private static boolean attemptAction(Interactable clickable, String... clickActions) {
        List<String> asList = new ArrayList<>(Arrays.asList(clickActions));
        return clickable.interact(m -> asList.contains(m.getAction()));
    }

}
