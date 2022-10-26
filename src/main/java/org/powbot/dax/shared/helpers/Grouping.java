package org.powbot.dax.shared.helpers;

import org.powbot.api.*;
import org.powbot.api.rt4.*;
import org.powbot.dax.engine.WaitFor;
import org.powbot.mobile.script.ScriptManager;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Grouping {

    //IDs
    public static final int
            MAIN_INTERFACE_ID = 76,
            TELEPORT_BUTTON_INDEX = 32,
            SELECTED_MINIGAME_INDEX = 11,
            MINIGAMES_SELECTION_BOX_INDEX = 22;
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
            if (!isMinigameTabOpen()) {
                openMinigameTab();
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
        if (!isMinigameTabOpen()){                        //Can be used to check if desired Minigame is already selected.
            openMinigameTab();
        }
        return selectMinigame(name.getName());
    }

    public static boolean isMinigameTabOpen(){ //Checks if Minigame Tab is open. Returns false if not.
        return Game.tab() == Game.Tab.CLAN_CHAT && Widgets.widget(MAIN_INTERFACE_ID).valid();
    }

//    public static boolean inMinigameChat(){ //CHECKS IF YOU ARE IN A MINIGAME CHAT. Returns false if not.
//        Component clan = Interfaces.get(clanInterfaceID);
//        if (clan != null){
//            for (Component x : clan.getChildren()){
//                if (x != null && x.getText().contains("Osrs")){
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    public static boolean openMinigameTab(){ //Opens the Minigame tab inside the Clan tab. Returns false if failed to open.
        if (Game.tab() != Game.Tab.CLAN_CHAT){
            if(Game.tab(Game.Tab.CLAN_CHAT)){
                if(WaitFor.condition(1500, () -> Game.tab() == Game.Tab.CLAN_CHAT ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)
                 != WaitFor.Return.SUCCESS)
                    return false;
            }
        }

//        Component master = Interfaces.get(399);

//        if (master != null){
        if(isMinigameTabOpen())
            return true;
        List<Component> button = Components.stream(707).action("Grouping").list();
        if(button.size() == 0)
            return false;
        if(button.get(0).click())
            WaitFor.milliseconds(400,900);
        return isMinigameTabOpen();
//        }

//        return false;

    }

//
//    public static boolean joinChat(){ //Joins the chat of selected minigame. Returns false if failed to join.
//
//        if (!isMinigameTabOpen()){
//            openMinigameTab();
//        }
//
//        Component mini = Interfaces.get(mainInterfaceID);
//
//        if (mini!= null){
//            Component button = mini.getChild(26);
//            String text;
//            if (button != null){
//                if ((text = button.getText()) != null && text.contains("Leave")){
//                    return true;
//                }
//                Clicking.click(button);
//                return (Timing.waitCondition(new BooleanSupplier() {
//                    @Override
//                    public boolean getAsBoolean() {
//                        WaitFor.milliseconds(350);
//                        return inMinigameChat();
//                    }
//                },3000));
//            }
//        }
//        return false;
//    }

    public static boolean teleport(){ //Teleports with selected minigame. Returns false if Teleport failed.

        if (!isMinigameTabOpen()){
            openMinigameTab();
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

        if(!isMinigameTabOpen() && !openMinigameTab()) {
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


    public static void receiveServerMessage(String message){
//        if(message.startsWith("You must wait") && message.contains("minigame teleports")){
//            String[] split = message.split(" ");
//            int minutes = Integer.parseInt(split[4]);
//            lastTeleport = Timing.currentTimeMillis() - (TimeUnit.MINUTES.toMillis(20 - minutes));
//        }
    }

}