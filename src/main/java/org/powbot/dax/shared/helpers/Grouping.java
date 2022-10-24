package org.powbot.dax.shared.helpers;

import org.powbot.api.Input;
import org.powbot.api.Point;
import org.powbot.api.Rectangle;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Players;

import java.util.Arrays;
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
                General.println("Selecting minigame: " + this.getName());
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
        return GameTab.TABS.CLAN.isOpen() && Interfaces.isInterfaceSubstantiated(MAIN_INTERFACE_ID);
    }

//    public static boolean inMinigameChat(){ //CHECKS IF YOU ARE IN A MINIGAME CHAT. Returns false if not.
//        RSInterface clan = Interfaces.get(clanInterfaceID);
//        if (clan != null){
//            for (RSInterface x : clan.getChildren()){
//                if (x != null && x.getText().contains("Osrs")){
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    public static boolean openMinigameTab(){ //Opens the Minigame tab inside the Clan tab. Returns false if failed to open.
        if (GameTab.getOpen() != GameTab.TABS.CLAN){
            if(GameTab.TABS.CLAN.open()){
                if(!Timing.waitCondition(GameTab.TABS.CLAN::isOpen, 1500))
                    return false;
            }
        }

//        RSInterface master = Interfaces.get(399);

//        if (master != null){
        if(isMinigameTabOpen())
            return true;
        RSInterface[] button = Entities.find(InterfaceEntity::new).inMaster(707).actionEquals("Grouping").getResults();
        if(button.length == 0)
            return false;
        if(Clicking.click(button[0]))
            General.sleep(400,900);
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
//        RSInterface mini = Interfaces.get(mainInterfaceID);
//
//        if (mini!= null){
//            RSInterface button = mini.getChild(26);
//            String text;
//            if (button != null){
//                if ((text = button.getText()) != null && text.contains("Leave")){
//                    return true;
//                }
//                Clicking.click(button);
//                return (Timing.waitCondition(new BooleanSupplier() {
//                    @Override
//                    public boolean getAsBoolean() {
//                        General.sleep(350);
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

        RSInterface button = Interfaces.get(MAIN_INTERFACE_ID, TELEPORT_BUTTON_INDEX);

        if (button != null){

            final Tile TILE = Players.local().tile();
            if (Clicking.click(button) && Timing.waitCondition( new BooleanSupplier() {
                @Override
                public boolean getAsBoolean() {
                    General.sleep(350);
                    return Player.getAnimation() != -1;
                }
            },4000))
                return Timing.waitCondition(new BooleanSupplier() {
                    @Override
                    public boolean getAsBoolean() {
                        General.sleep(350);
                        return Player.getPosition().distanceTo(TILE) > 10;
                    }
                },20000);
        }

        return false;
    }

    public static boolean isSelected(String name){

        final RSInterface mini = Interfaces.get(MAIN_INTERFACE_ID, SELECTED_MINIGAME_INDEX);
        String minigame;

        return(mini != null
                && (minigame = mini.getText()) != null && minigame.toLowerCase().contains(name.toLowerCase()));
    }

    private static boolean selectMinigame(String name){

        if(!isMinigameTabOpen() && !openMinigameTab()) {
            General.println("failed to open minigame tab.");
            return false;
        }


        if (isSelected(name)){
            return true;
        }
        final RSInterface currentMinigame = Interfaces.get(MAIN_INTERFACE_ID, 7);
        if(currentMinigame == null)
            return false;
        RSInterface minigamesBox = Interfaces.get(MAIN_INTERFACE_ID, MINIGAMES_SELECTION_BOX_INDEX);
        if((minigamesBox == null || minigamesBox.getChildren() == null) && Clicking.click(currentMinigame)) {
            if (!Timing.waitCondition(() -> {
                General.sleep(350);
                RSInterface minigamesBox1 = Interfaces.get(MAIN_INTERFACE_ID, MINIGAMES_SELECTION_BOX_INDEX);
                return minigamesBox1 != null && minigamesBox1.getChildren() != null;
            },2000)){
                General.println("Failed to wait for minigames children to appear.");
                return false;
            }
            minigamesBox = Interfaces.get(MAIN_INTERFACE_ID, MINIGAMES_SELECTION_BOX_INDEX);

        }
        RSInterface[] children;
        if(minigamesBox == null || (children = minigamesBox.getChildren()) == null) {
            General.println("Unable to detect minigames children.");
            return false;
        }
        RSInterface ourMinigame = Arrays.stream(children).filter(i -> {
            String txt = i.getText();
            return txt != null && txt.toLowerCase().contains(name.toLowerCase());
        }).findFirst().orElse(null);

        Rectangle rec = minigamesBox.getAbsoluteBounds();
        Point minigamePoint = ourMinigame != null ? ourMinigame.getHumanHoverPoint() : null;

        if(minigamePoint == null) {
            General.println("Our clicking point for the minigame is null.");
            return false;
        }

        long timeout = Timing.currentTimeMillis() + 10000;
        while (!isMinigameVisible(minigamePoint) && Timing.currentTimeMillis() < timeout) {
            if(Context.isStopped())
                break;
            if(Context.isPaused()){
                General.sleep(1000);
                continue;
            }

            RSInterface arrowDown = Entities.find(InterfaceEntity::new).inMasterAndChild(MAIN_INTERFACE_ID, 23).textureIdEquals(773).getFirstResult();
            if(arrowDown == null) {
                General.println("Failed to find arrowDown button.");
                return false;
            }
            Point scroll = arrowDown.getAbsolutePosition();

            if(minigamePoint.getY() < rec.getY()){//scroll up
                General.println("Scrolling up");
                Point randomizedStart = new Point(scroll.getX() - General.randomSD(2, 30, 10, 5), scroll.getY() + General.randomSD(10, 150, 80, 40) );
                Point randomizedEnd = new Point(randomizedStart.getX() + General.random(-5, 5), randomizedStart.getY() + General.randomSD(30, 120, 60, 30));
                General.println("Randomized start: " + randomizedStart + ", randomized end: " + randomizedEnd);
                Input.drag(randomizedStart, randomizedEnd);
            } else {
                General.println("Scrolling down");
                Point randomizedStart = new Point(scroll.getX() - General.randomSD(2, 30, 10, 5), scroll.getY() + General.randomSD(2, 40, 15, 20) );
                Point randomizedEnd = new Point(randomizedStart.getX() - General.random(-5, 5), randomizedStart.getY() - General.randomSD(30, 120, 60, 30));
                General.println("Randomized start: " + randomizedStart + ", randomized end: " + randomizedEnd);
                Input.drag(randomizedStart, randomizedEnd);
            }
            General.sleep(400,800);


            minigamesBox = Interfaces.get(MAIN_INTERFACE_ID, 22);
            if(minigamesBox == null || minigamesBox.getChildren() == null)
                break;
            rec = minigamesBox.getAbsoluteBounds();
            minigamePoint = ourMinigame.getHumanHoverPoint();

        }

        if(isMinigameVisible(minigamePoint) && Clicking.click(ourMinigame)){
            return Timing.waitCondition(() -> isSelected(name), 2500);
        }

        return isSelected(name);

    }

    private static boolean isMinigameVisible(Point p){
        General.println("Checking if point is visible: " + p);
        return p.getY() > 275 && p.getY() < 400;
    }

    public static boolean canMinigameTeleport(){
        return WorldHopper.isCurrentWorldMembers().orElse(false) && !Player.getRSPlayer().isInCombat() &&
                ((long) Game.getSetting(888) * 60 * 1000) + (20 * 60 * 1000) < Timing.currentTimeMillis();
    }


    public static void receiveServerMessage(String message){
//        if(message.startsWith("You must wait") && message.contains("minigame teleports")){
//            String[] split = message.split(" ");
//            int minutes = Integer.parseInt(split[4]);
//            lastTeleport = Timing.currentTimeMillis() - (TimeUnit.MINUTES.toMillis(20 - minutes));
//        }
    }

}