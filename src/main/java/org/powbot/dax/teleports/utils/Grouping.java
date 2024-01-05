package org.powbot.dax.teleports.utils;

import org.powbot.api.*;
import org.powbot.api.rt4.*;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.shared.helpers.Timing;
import org.powbot.mobile.script.ScriptManager;

import java.util.Arrays;
import java.util.List;

public class Grouping {

    //IDs
    public static final int
            MAIN_INTERFACE_ID = 76,
            TELEPORT_BUTTON_INDEX = 32,
            SELECTED_MINIGAME_INDEX = 11,
            MINIGAMES_SELECTION_BOX_INDEX = 22,

    PVP_ARENA_VARBIT = 13143,
            PVP_WIDGET = 762,
            PVP_CLOSE_COMPONENT = 3;
//            clanInterfaceID = 589;

    //ENUMS
    public enum MINIGAMES {
        BARBARIAN_ASSAULT("barbarian"),
        BLAST_FURNACE("blast"),
        BURTHORPE_GAMES_ROOM("burthorpe"),
        CASTLE_WARS("castle"),
        CLAN_WARS("clan"),
        DAGANNOTH_KINGS("dagannoth"),
        FISHING_TRAWLER("fishing"),
        GIANTS_FOUNDRY("giant"),
        GOD_WARS("god war"),
        GUARDIANS_OF_THE_RIFT("guardians"),
        LAST_MAN_STANDING("last m"),
        NIGHTMARE_ZONE("nightmare"),
        PEST_CONTROL("pest"),
        PLAYER_OWNED_HOUSES("player"),
        RAT_PITS("pits"),
        SHADES_OF_MORTTON("shades"),
        SHIELD_OF_ARRAV("shield"),
        SOUL_WARS("soul"),
        TROUBLE_BREWING("trouble"),
        TZHAAR_FIGHT_PIT("tzhaar");

        private String name;

        MINIGAMES(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public boolean teleportTo() {
            if (!Game.tab(Game.Tab.GROUPING)) {
                return false;
            }
            if (!isSelected()) {
                if (!selectMinigame(this.getName()))
                    return false;
            }
            return isSelected() && teleport();
        }

        public boolean isSelected() {
            return Grouping.isSelected(this.getName());
        }

    }

    //METHODS
    public static boolean selectMinigame(MINIGAMES name) { //Selects Desired Minigame. Returns false if failed to select.
        return Game.tab(Game.Tab.GROUPING) && selectMinigame(name.getName());
    }

    public static boolean teleport() {
        if (!Game.tab(Game.Tab.GROUPING)) {
            return false;
        }

        Component button = Widgets.widget(MAIN_INTERFACE_ID).component(TELEPORT_BUTTON_INDEX);

        if (button.valid()) {

            final Tile TILE = Players.local().tile();
            if (button.click() && Condition.wait(() -> Players.local().animation() != -1, 200, 20)) {
                return Condition.wait(() -> TILE.distance() > 10, 2000, 10);
            }
        }

        return false;
    }

    public static boolean isSelected(String name) {

        final Component mini = Widgets.component(MAIN_INTERFACE_ID, SELECTED_MINIGAME_INDEX);
        String minigame = mini.text();

        return (mini.valid() && minigame.toLowerCase().contains(name.toLowerCase()));
    }

    private static boolean selectMinigame(String name) {
        if (!Game.tab(Game.Tab.GROUPING)) {
            System.out.println("failed to open minigame tab.");
            return false;
        }


        if (isSelected(name)) {
            return true;
        }
        final Component currentMinigame = Widgets.component(MAIN_INTERFACE_ID, 7);
        if (!currentMinigame.valid())
            return false;



        final Component minigamesBox = minigameBox(currentMinigame);
        if (!minigamesBox.visible()) {
            System.out.println("Unable to detect minigames children.");
            return false;
        }

        if (ScrollHelper.scrollTo(
                () -> minigameItem(minigamesBox, name),
                () -> Widgets.component(MAIN_INTERFACE_ID, 21),
                () -> Widgets.component(MAIN_INTERFACE_ID, 23)
        ) && minigameItem(minigamesBox, name).click()) {
            return WaitFor.condition(2500, () -> isSelected(name) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)
                    == WaitFor.Return.SUCCESS;
        }

        return isSelected(name);

    }

    private static Component minigameBox(Component currentMinigame) {
        Component minigamesBox = Widgets.component(MAIN_INTERFACE_ID, MINIGAMES_SELECTION_BOX_INDEX);
        if ((!minigamesBox.visible() || minigamesBox.components().getSize() == 0) && currentMinigame.click()) {
            if (!Condition.wait(() -> {
                Component minigamesBox1 = Widgets.component(MAIN_INTERFACE_ID, MINIGAMES_SELECTION_BOX_INDEX);
                return minigamesBox1.visible() && minigamesBox1.components().getSize() > 0;
            }, 200, 10)) {
                System.out.println("Failed to wait for minigames children to appear.");
                return Component.Companion.getNil();
            }
            return Widgets.component(MAIN_INTERFACE_ID, MINIGAMES_SELECTION_BOX_INDEX);
        }

        return minigamesBox;
    }

    private static Component minigameItem(Component minigamesBox, String name) {
        return Components.stream(minigamesBox.widgetId(), minigamesBox.index()).withChildren(true).textContains(name).first();
    }

    public static boolean hasPvpArenaWidget() {
        return Varpbits.value(PVP_ARENA_VARBIT) == 1;
    }

    public static boolean closePvpArenaWidget() {
        Component close = Widgets.component(PVP_WIDGET, PVP_CLOSE_COMPONENT);
        return close != Component.Companion.getNil() && close.visible() && close.click();
    }

}
