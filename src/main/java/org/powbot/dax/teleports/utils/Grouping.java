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
    public static boolean selectMinigame(MINIGAMES name){ //Selects Desired Minigame. Returns false if failed to select.
        return Game.tab(Game.Tab.GROUPING) && selectMinigame(name.getName());
    }

    public static boolean teleport() {
        if (!Game.tab(Game.Tab.GROUPING)){
            return false;
        }

        Component button = Widgets.widget(MAIN_INTERFACE_ID).component(TELEPORT_BUTTON_INDEX);

        if (button.valid()){

            final Tile TILE = Players.local().tile();
            if (button.click() && Condition.wait(() -> Players.local().animation() != -1, 200, 20)) {
                return Condition.wait(() -> TILE.distance() > 10, 2000, 10);
            }
        }

        return false;
    }

    public static boolean isSelected(String name){

        final Component mini = Widgets.component(MAIN_INTERFACE_ID, SELECTED_MINIGAME_INDEX);
        String minigame = mini.text();

        return(mini.valid() && minigame.toLowerCase().contains(name.toLowerCase()));
    }

    private static boolean selectMinigame(String name){
        if(!Game.tab(Game.Tab.GROUPING)) {
            System.out.println("failed to open minigame tab.");
            return false;
        }


        if (isSelected(name)){
            return true;
        }
        final Component currentMinigame = Widgets.component(MAIN_INTERFACE_ID, 7);
        if(!currentMinigame.valid())
            return false;
        Component minigamesBox = Widgets.component(MAIN_INTERFACE_ID, MINIGAMES_SELECTION_BOX_INDEX);
        if((!minigamesBox.valid() || minigamesBox.components().getSize() == 0) && currentMinigame.click()) {
            if (!Condition.wait(() -> {
                Component minigamesBox1 = Widgets.component(MAIN_INTERFACE_ID, MINIGAMES_SELECTION_BOX_INDEX);
                return minigamesBox1.valid() && minigamesBox1.components().getSize() > 0;
            },200, 10)){
                System.out.println("Failed to wait for minigames children to appear.");
                return false;
            }
            minigamesBox = Widgets.component(MAIN_INTERFACE_ID, MINIGAMES_SELECTION_BOX_INDEX);

        }

        if(!minigamesBox.valid()) {
            System.out.println("Unable to detect minigames children.");
            return false;
        }

        Component ourMinigame = Arrays.stream(minigamesBox.components().clone()).filter(i -> {
            String txt = i.text();
            return txt.toLowerCase().contains(name.toLowerCase());
        }).findFirst().orElse(null);

        Rectangle rec = minigamesBox.boundingRect();
        Point minigamePoint = ourMinigame != null ? ourMinigame.nextPoint() : null;

        if(minigamePoint == null) {
            System.out.println("Our clicking point for the minigame is null.");
            return false;
        }

        long timeout = Timing.currentTimeMillis() + 10000;
        while (!isMinigameVisible(minigamePoint) && Timing.currentTimeMillis() < timeout) {
            if(ScriptManager.INSTANCE.isStopping())
                break;
            if(ScriptManager.INSTANCE.isPaused()) {
                WaitFor.milliseconds(1000);
                continue;
            }

            Component arrowDown = Components.stream(MAIN_INTERFACE_ID, 23).texture(773).first();
            if(!arrowDown.valid()) {
                System.out.println("Failed to find arrowDown button.");
                return false;
            }
            Point scroll = arrowDown.screenPoint();

            if(minigamePoint.getY() < rec.getY()){//scroll up
                System.out.println("Scrolling up");
                Point randomizedStart = new Point(scroll.getX() - Random.nextGaussian(2, 30, 10, 5), scroll.getY() + Random.nextGaussian(10, 150, 80, 40) );
                Point randomizedEnd = new Point(randomizedStart.getX() + Random.nextInt(-5, 5), randomizedStart.getY() + Random.nextGaussian(30, 120, 60, 30));
                System.out.println("Randomized start: " + randomizedStart + ", randomized end: " + randomizedEnd);
                Input.drag(randomizedStart, randomizedEnd);
            } else {
                System.out.println("Scrolling down");
                Point randomizedStart = new Point(scroll.getX() - Random.nextGaussian(2, 30, 10, 5), scroll.getY() + Random.nextGaussian(2, 40, 15, 20) );
                Point randomizedEnd = new Point(randomizedStart.getX() - Random.nextInt(-5, 5), randomizedStart.getY() - Random.nextGaussian(30, 120, 60, 30));
                System.out.println("Randomized start: " + randomizedStart + ", randomized end: " + randomizedEnd);
                Input.drag(randomizedStart, randomizedEnd);
            }
            WaitFor.milliseconds(400,800);


            minigamesBox = Widgets.component(MAIN_INTERFACE_ID, 22);
            if(!minigamesBox.valid() || minigamesBox.components().isEmpty())
                break;
            rec = minigamesBox.boundingRect();
            minigamePoint = ourMinigame.nextPoint();

        }

        if(isMinigameVisible(minigamePoint) && ourMinigame.click()){
            return WaitFor.condition(2500, () -> isSelected(name) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)
                    == WaitFor.Return.SUCCESS;
        }

        return isSelected(name);

    }

    private static boolean isMinigameVisible(Point p){
        return p.getY() > 275 && p.getY() < 400;
    }

    public static boolean canMinigameTeleport(){
        return Worlds.isCurrentWorldMembers() && !Players.local().inCombat() &&
                ((long) Varpbits.varpbit(888) * 60 * 1000) + (20 * 60 * 1000) < Timing.currentTimeMillis();
    }

    public static boolean hasPvpArenaWidget(){
        return Varpbits.value(PVP_ARENA_VARBIT) == 1;
    }

    public static boolean closePvpArenaWidget(){
        Component close = Widgets.component(PVP_WIDGET, PVP_CLOSE_COMPONENT);
        return close != Component.Companion.getNil() && close.visible() && close.click();
    }

}