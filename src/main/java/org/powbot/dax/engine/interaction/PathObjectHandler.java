package org.powbot.dax.engine.interaction;

import org.powbot.api.Area;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Objects;
import org.powbot.api.rt4.*;
import org.powbot.dax.engine.Loggable;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.WalkerEngine;
import org.powbot.dax.engine.WalkingCondition;
import org.powbot.dax.engine.bfs.BFS;
import org.powbot.dax.engine.collision.RealTimeCollisionTile;
import org.powbot.dax.engine.local.PathAnalyzer;
import org.powbot.dax.engine.local.Reachable;
import org.powbot.dax.shared.helpers.AreaHelper;
import org.powbot.dax.shared.helpers.Filters;
import org.powbot.dax.shared.helpers.GameObjectHelper;
import org.powbot.dax.shared.helpers.General;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class PathObjectHandler implements Loggable {

    private static PathObjectHandler instance;

    private final TreeSet<String> sortedOptions, sortedBlackList, sortedBlackListOptions, sortedHighPriorityOptions;

    private final List<Predicate<GameObject>> blacklistedObjects;

    private PathObjectHandler(){
        sortedOptions = new TreeSet<>(
                Arrays.asList("Enter", "Cross", "Pass", "Open", "Close", "Walk-through", "Use", "Pass-through", "Exit",
                        "Walk-Across", "Go-through", "Walk-across", "Climb", "Climb-up", "Climb-down", "Climb-over", "Climb over", "Climb-into", "Climb-through", "Climb through",
                        "Board", "Jump-from", "Jump-across", "Jump-to", "Squeeze-through", "Jump-over", "Pay-toll(10gp)", "Step-over", "Walk-down", "Walk-up","Walk-Up", "Travel", "Get in",
                        "Investigate", "Operate", "Climb-under","Jump","Crawl-down","Crawl-through","Activate","Push","Squeeze-past","Walk-Down",
                        "Swing-on", "Climb up","Pass-Through","Jump-up","Jump-down","Swing across", "Climb Down", "Jump-Down", "Jump to", "Unlock"));


        sortedBlackList = new TreeSet<>(Arrays.asList("Coffin","Drawers","null","Ornate railing","Wardrobe"));
        sortedBlackListOptions = new TreeSet<>(Arrays.asList("Chop down"));
        sortedHighPriorityOptions = new TreeSet<>(Arrays.asList("Pay-toll(10gp)","Squeeze-past"));
        blacklistedObjects = new ArrayList<>(Arrays.asList(
                Filters.Objects.inArea(new Area(new Tile(3205, 3226, 0), new Tile(3212, 3218, 0)))//LUMBRIDGE DINING ROOM
        ));
    }

    private static PathObjectHandler getInstance(){
        return instance != null ? instance : (instance = new PathObjectHandler());
    }

    private enum SpecialObject {
        WEB(Filters.Objects.nameEquals("Web"), "Slash", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.stream(15).filter(
                        Filters.Objects.inArea(AreaHelper.fromCenter(destinationDetails.getAssumed(), 1))
                                .and(Filters.Objects.nameEquals("Web"))
                                .and(Filters.Objects.actionsContains("Slash"))).count() > 0;
            }
        }),
        ROCKFALL(Filters.Objects.nameEquals("Rockfall"), "Mine", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return  Objects.stream(15).filter(
                        Filters.Objects.inArea(AreaHelper.fromCenter(destinationDetails.getAssumed(), 1))
                                .and(Filters.Objects.nameEquals("Rockfall"))
                                .and(Filters.Objects.actionsContains("Mine"))).count() > 0;
            }
        }),
        ROOTS(Filters.Objects.nameEquals("Roots"), "Chop", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.stream(15).filter(
                        Filters.Objects.inArea(AreaHelper.fromCenter(destinationDetails.getAssumed(), 1))
                                .and(Filters.Objects.nameEquals("Roots"))
                                .and(Filters.Objects.actionsContains("Chop"))).count() > 0;
            }
        }),
        ROCK_SLIDE(Filters.Objects.nameEquals("Rockslide"), "Climb-over", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.stream(15).filter(
                        Filters.Objects.inArea(AreaHelper.fromCenter(destinationDetails.getAssumed(), 1))
                                .and(Filters.Objects.nameEquals("Rockslide"))
                                .and(Filters.Objects.actionsContains("Climb-over"))).count() > 0;
            }
        }),
        ROOT(Filters.Objects.nameEquals("Root"), "Step-over", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.stream(15).filter(
                        Filters.Objects.inArea(AreaHelper.fromCenter(destinationDetails.getAssumed(), 1))
                                .and(Filters.Objects.nameEquals("Root"))
                                .and(Filters.Objects.actionsContains("Step-over"))).count() > 0;
            }
        }),
        BRIMHAVEN_VINES(Filters.Objects.nameEquals("Vines"), "Chop-down", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.stream(15).filter(
                        Filters.Objects.inArea(AreaHelper.fromCenter(destinationDetails.getAssumed(), 1))
                                .and(Filters.Objects.nameEquals("Vines"))
                                .and(Filters.Objects.actionsContains("Chop-down"))).count() > 0;
            }
        }),
        AVA_BOOKCASE (Filters.Objects.nameEquals("Bookcase"), "Search", new Tile(3097, 3359, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getX() >= 3097 && destinationDetails.getAssumed().equals(new Tile(3097, 3359, 0));
            }
        }),
        AVA_LEVER (Filters.Objects.nameEquals("Lever"), "Pull", new Tile(3096, 3357, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getX() < 3097 && destinationDetails.getAssumed().equals(new Tile(3097, 3359, 0));
            }
        }),
        ARDY_DOOR_LOCK_SIDE(Filters.Objects.nameEquals("Door"), "Pick-lock", new Tile(2565, 3356, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Players.local().tile().getX() >= 2565 && Players.local().tile().distanceTo(new Tile(2565, 3356, 0)) < 3;
            }
        }),
        ARDY_DOOR_UNLOCKED_SIDE(Filters.Objects.nameEquals("Door"), "Open", new Tile(2565, 3356, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Players.local().tile().getX() < 2565 && Players.local().tile().distanceTo(new Tile(2565, 3356, 0)) < 3;
            }
        }),
        YANILLE_DOOR_LOCK_SIDE(Filters.Objects.nameEquals("Door"), "Pick-lock", new Tile(2601, 9482, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Players.local().tile().getY() <= 9481 && Players.local().tile().distanceTo(new Tile(2601, 9482, 0)) < 3;
            }
        }),
        YANILLE_DOOR_UNLOCKED_SIDE(Filters.Objects.nameEquals("Door"), "Open", new Tile(2601, 9482, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Players.local().tile().getY() > 9481 && Players.local().tile().distanceTo(new Tile(2601, 9482, 0)) < 3;
            }
        }),
        EDGEVILLE_UNDERWALL_TUNNEL(Filters.Objects.nameEquals("Underwall tunnel"), "Climb-into", new Tile(3138, 3516, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new Tile(3138, 3516, 0));
            }
        }),
        VARROCK_UNDERWALL_TUNNEL(Filters.Objects.nameEquals("Underwall tunnel"), "Climb-into", new Tile(3141, 3513, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new Tile(3141, 3513, 0 ));
            }
        }),
        GAMES_ROOM_STAIRS(Filters.Objects.nameEquals("Stairs"), "Climb-down", new Tile(2899, 3565, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getTile().equals(new Tile(2899, 3565, 0)) &&
                        destinationDetails.getAssumed().equals(new Tile(2205, 4934, 1));
            }
        }),
        FALADOR_GATE(Filters.Objects.nameEquals("Gate"), "Close", new Tile(3031, 3314, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getTile().equals(new Tile(3031, 3314, 0));
            }
        }),
        CANIFIS_BASEMENT_WALL(Filters.Objects.nameEquals("Wall"), "Search", new Tile(3480, 9836, 0),new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getTile().equals(new Tile(3480, 9836, 0)) ||
                        destinationDetails.getAssumed().equals(new Tile(3480, 9836, 0));
            }
        }),
        BRINE_RAT_CAVE_BOULDER(Filters.Objects.nameEquals("Cave"), "Exit", new Tile(2690, 10125, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getTile().equals(new Tile(2690, 10125, 0))
                        && Npcs.stream().name("Boulder").action("Roll").count() > 0;
            }
        }),
        ARDOUGNE_LOCKED_HOUSE(Filters.Objects.nameEquals("Door"), "Pick-lock", new Tile(2611, 3316, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new Tile(2611, 3316, 0)) && destinationDetails.getDestination().getTile().equals(new Tile(2610, 3316, 0));
            }
        }),
        WILDERNESS_CAVERN(Filters.Objects.nameEquals("Cavern"), "Enter", new Tile(3126, 3832, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new Tile(3126, 3832, 0)) && destinationDetails.getDestination().getTile().equals(new Tile(3241, 10233, 0));
            }
        }),
        WILDERNESS_CREVICE(Filters.Objects.nameEquals("Crevice"), "Jump-Down", new Tile(3067, 3740, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new Tile(3067, 3740, 0)) && destinationDetails.getDestination().getTile().equals(new Tile(3187, 10127, 0));
            }
        }),
        WILDERNESS_CAVERN_2(Filters.Objects.nameEquals("Cavern"), "Enter", new Tile(3075, 3653, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new Tile(3075, 3653, 0)) && destinationDetails.getDestination().getTile().equals(new Tile(3197, 10056, 0));
            }
        }),
        BASILISK_SHORTCUT(Filters.Objects.nameEquals("Crevice"), "Squeeze-through", new Tile(2735, 10008, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getTile().equals(new Tile(2735, 10008, 0)) || destinationDetails.getDestination().getTile().equals(new Tile(2730, 10008, 0));
            }
        }),;

        private Predicate<GameObject> filter;
        private String action;
        private Tile location;
        private SpecialCondition specialCondition;

        SpecialObject(Predicate<GameObject> filter, String action, Tile location, SpecialCondition specialCondition){
            this.filter = filter;
            this.action = action;
            this.location = location;
            this.specialCondition = specialCondition;
        }

        public Predicate<GameObject> getFilter() {
            return filter;
        }

        public String getAction() {
            return action;
        }

        public Tile getLocation() {
            return location;
        }

        public boolean isSpecialCondition(PathAnalyzer.DestinationDetails destinationDetails){
            return specialCondition.isSpecialLocation(destinationDetails);
        }

        public static SpecialObject getValidSpecialObjects(PathAnalyzer.DestinationDetails destinationDetails){
            for (SpecialObject object : values()){
                if (object.isSpecialCondition(destinationDetails)){
                    return object;
                }
            }
            return null;
        }

    }

    private abstract static class SpecialCondition {
        abstract boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails);
    }

    public static boolean handle(PathAnalyzer.DestinationDetails destinationDetails, List<Tile> path, WalkingCondition walkingCondition){
        RealTimeCollisionTile start = destinationDetails.getDestination(), end = destinationDetails.getNextTile();

        List<GameObject> interactiveObjects = null;

        String action = null;
        SpecialObject specialObject = SpecialObject.getValidSpecialObjects(destinationDetails);
        if (specialObject == null) {
            if ((interactiveObjects = getInteractiveObjects(start.getX(), start.getY(), start.getZ(), destinationDetails)).size() < 1 && end != null) {
                interactiveObjects = getInteractiveObjects(end.getX(), end.getY(), end.getZ(), destinationDetails);
            }
        } else {
            action = specialObject.getAction();
            Predicate<GameObject> specialObjectFilter = specialObject.getFilter()
//                    .and(Filters.Objects.actionsContains(specialObject.getAction()))
                    .and(Filters.Objects.inArea(AreaHelper.fromCenter(specialObject.getLocation() != null ? specialObject.getLocation() : destinationDetails.getAssumed(), 1)));
            interactiveObjects = Objects.stream(15).filter(specialObjectFilter).list();

        }

        if (interactiveObjects.size() == 0) {
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder("Sort Order: ");
        interactiveObjects.forEach(rsObject -> stringBuilder.append(rsObject.name()).append(" ").append(
                rsObject.actions()).append(", "));
        getInstance().log(stringBuilder);

        return handle(path, interactiveObjects.get(0), destinationDetails, action, specialObject, walkingCondition);
    }

    private static boolean handle(List<Tile> path, GameObject object, PathAnalyzer.DestinationDetails destinationDetails, String action, SpecialObject specialObject, WalkingCondition walkingCondition){
        PathAnalyzer.DestinationDetails current = PathAnalyzer.furthestReachableTile(path);

        if (current == null){
            return false;
        }

        RealTimeCollisionTile currentFurthest = current.getDestination();
        if (!Players.local().inMotion() && !object.inViewport(true)){
            if (!WalkerEngine.getInstance().clickMinimap(destinationDetails.getDestination())){
                return false;
            }
        }
        if (WaitFor.condition(Random.nextInt(5000, 8000), () -> object.inViewport(true) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
            return false;
        }

        boolean successfulClick = false;

        if (specialObject != null) {
            getInstance().log("Detected Special Object: " + specialObject);
            switch (specialObject){
                case WEB:
                    List<GameObject> webs;
                    int iterations = 0;
                    while ((webs = Objects.get(object.getTile()).stream()
                            .filter(object1 -> Arrays.asList(GameObjectHelper.getActions(object1)).contains("Slash")).collect(Collectors.toList())).size() > 0){
                        GameObject web = webs.get(0);
                        if (canLeftclickWeb()) {
                            InteractionHelper.click(web, "Slash");
                        } else {
                            useBladeOnWeb(web);
                        }
                        if(Inventory.selectedItem() != Item.getNil()){
                            Movement.walkTo(Players.local().tile());
                        }
                        if (web.getTile().distanceTo(Players.local().tile()) <= 1) {
                            WaitFor.milliseconds(General.randomSD(50, 800, 250, 150));
                        } else {
                            WaitFor.milliseconds(2000, 4000);
                        }
                        if (Reachable.getMap().getParent(destinationDetails.getAssumedX(), destinationDetails.getAssumedY()) != null &&
                                (webs = Objects.get(object.getTile()).stream().filter(object1 -> Arrays.asList(GameObjectHelper.getActions(object1)).contains("Slash")).collect(Collectors.toList())).size() == 0){
                            successfulClick = true;
                            break;
                        }
                        if (iterations++ > 5){
                            break;
                        }
                    }
                    break;
                case ARDY_DOOR_LOCK_SIDE:
                case YANILLE_DOOR_LOCK_SIDE:
                    for (int i = 0; i < Random.nextInt(15, 25); i++) {
                        if (Players.local().tile().equals(new Tile(2564, 3356, 0))){
                            successfulClick = true;
                            break;
                        }
                        if (!clickOnObject(object, specialObject.getAction())){
                            continue;
                        }
                        if (Players.local().tile().distanceTo(specialObject.getLocation()) > 1){
                            WaitFor.condition(Random.nextInt(3000, 4000), () -> Players.local().tile().distanceTo(specialObject.getLocation()) <= 1 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        }
                    }
                    break;
                case VARROCK_UNDERWALL_TUNNEL:
                    if(!clickOnObject(object,specialObject.getAction())){
                        return false;
                    }
                    successfulClick = true;
                    WaitFor.condition(10000, () ->
                            SpecialObject.EDGEVILLE_UNDERWALL_TUNNEL.getLocation().equals(Players.local().tile()) ?
                                    WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                    break;
                case EDGEVILLE_UNDERWALL_TUNNEL:
                    if(!clickOnObject(object,specialObject.getAction())){
                        return false;
                    }
                    successfulClick = true;
                    WaitFor.condition(10000, () ->
                            SpecialObject.VARROCK_UNDERWALL_TUNNEL.getLocation().equals(Players.local().tile()) ?
                                    WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                    break;
                case FALADOR_GATE:
                    int targetTile = path.indexOf(PathAnalyzer.closestTileInPathToPlayer(path).getTile());
                    targetTile += 10;
                    if(targetTile > path.size()){
                        targetTile = path.size()-1;
                    }
                    return Movement.walkTo(path.get(targetTile));
                case BRINE_RAT_CAVE_BOULDER:
                    Npc boulder = InteractionHelper.getRSNPC(Filters.NPCs.nameEquals("Boulder").and(Filters.NPCs.actionsContains("Roll")));
                    if(InteractionHelper.click(boulder, "Roll")){
                        if(WaitFor.condition(12000,
                                () -> Npcs.stream().name("Boulder").action("Roll").count() == 0 ?
                                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
                            WaitFor.milliseconds(3500, 6000);
                            successfulClick = true;
                        }
                    }
                    break;
                case ARDOUGNE_LOCKED_HOUSE:
                    for (int i = 0; i < Random.nextInt(10, 15); i++) {
                        if (Players.local().tile().equals(specialObject.getLocation())) {
                            successfulClick = true;
                            break;
                        }
                        if (!clickOnObject(object, specialObject.getAction())) {
                            continue;
                        }
                        if (Players.local().tile().distanceTo(specialObject.getLocation()) > 1) {
                            WaitFor.condition(Random.nextInt(3000, 4000), () -> Players.local().tile().distanceTo(specialObject.getLocation()) <= 1 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        }
                    }
                    break;
                case WILDERNESS_CREVICE:
                case WILDERNESS_CAVERN:
                case WILDERNESS_CAVERN_2:
                    if(NPCInteraction.isConversationWindowUp()){
                        NPCInteraction.handleConversation("Yes, and don't ask again.");
                    } else if(clickOnObject(object,specialObject.getAction())){
                        WaitFor.condition(4500, () -> (Players.local().tile().equals(new Tile(3187, 10127, 0)) || NPCInteraction.isConversationWindowUp() || Widgets.widget(720).valid()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        if(NPCInteraction.isConversationWindowUp()){
                            NPCInteraction.handleConversation("Yes, and don't ask again.");
                        } else if(Widgets.widget(720).valid()){
                            DoomsToggle.handle(720, "Let me jump, and don't warn me again!");
                        }
                    }
                    break;
                case BASILISK_SHORTCUT:
                    object.bounds(-52, 42, -217, 20, -52, 52);
                    break;
            }
        }

        if (!successfulClick){
            try {
                String[] validOptions = action != null ? new String[]{action} : getViableOption(
                        object.actions().stream().filter(getInstance().sortedOptions::contains).collect(
                                Collectors.toList()), destinationDetails);
                if (!clickOnObject(object, validOptions)) {
                    return false;
                }
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        boolean strongholdDoor = isStrongholdDoor(object);

        if (strongholdDoor){
            if (WaitFor.condition(Random.nextInt(6700, 7800), () -> {
                Tile playerPosition = Players.local().tile();
                if (BFS.isReachable(RealTimeCollisionTile.get(playerPosition.getX(), playerPosition.getY(), playerPosition.floor()), destinationDetails.getNextTile(), 50)) {
                    WaitFor.milliseconds(500, 1000);
                    return WaitFor.Return.SUCCESS;
                }
                if (NPCInteraction.isConversationWindowUp()) {
                    handleStrongholdQuestions();
                    return WaitFor.Return.SUCCESS;
                }
                return WaitFor.Return.IGNORE;
            }) != WaitFor.Return.SUCCESS){
                return false;
            }
        }

        WaitFor.Return result = WaitFor.condition(Random.nextInt(8500, 11000), () -> {
            switch (walkingCondition.action()) {
                case EXIT_OUT_WALKER_FAIL:
                    return WaitFor.Return.FAIL;
                case EXIT_OUT_WALKER_SUCCESS:
                    return WaitFor.Return.SUCCESS;
            }

            DoomsToggle.handleToggle();
            if (NPCInteraction.isConversationWindowUp()) {
                NPCInteraction.handleConversation(NPCInteraction.GENERAL_RESPONSES);
            }

            PathAnalyzer.DestinationDetails destinationDetails1 = PathAnalyzer.furthestReachableTile(path);
            if (destinationDetails1 != null) {
                if (!destinationDetails1.getDestination().equals(currentFurthest)){
                    return WaitFor.Return.SUCCESS;
                }
            }

            if (current.getNextTile() != null){
                PathAnalyzer.DestinationDetails hoverDetails = PathAnalyzer.furthestReachableTile(path, current.getNextTile());
                if (hoverDetails != null && hoverDetails.getDestination() != null && hoverDetails.getDestination().getTile().distanceTo(Players.local().tile()) > 7 && !strongholdDoor && Players.local().tile().distanceTo(object) <= 2){
                    WalkerEngine.getInstance().hoverMinimap(hoverDetails.getDestination());
                }
            }

            return WaitFor.Return.IGNORE;
        });

        if (strongholdDoor){
            WaitFor.milliseconds(800, 1200);
        }

        return result == WaitFor.Return.SUCCESS;
    }

    public static List<GameObject> getInteractiveObjects(int x, int y, int z, PathAnalyzer.DestinationDetails destinationDetails){
        List<GameObject> objects = Objects.stream(15).filter(interactiveObjectFilter(x, y, z, destinationDetails)).list();
        final Tile base = new Tile(x, y, z);
        objects.sort((o1, o2) -> {
            int c = Integer.compare((int)o1.getTile().distanceTo(base), (int)o2.getTile().distanceTo(base));
            int assumedZ = destinationDetails.getAssumedZ(), destinationZ = destinationDetails.getDestination().getZ();
            List<String> actions1 = o1.actions();
            List<String> actions2 = o2.actions();

            if (assumedZ > destinationZ){
                if (actions1.contains("Climb-up")){
                    return -1;
                }
                if (actions2.contains("Climb-up")){
                    return 1;
                }
            } else if (assumedZ < destinationZ){
                if (actions1.contains("Climb-down")){
                    return -1;
                }
                if (actions2.contains("Climb-down")){
                    return 1;
                }
            } else if(destinationDetails.getAssumed().distanceTo(destinationDetails.getDestination().getTile()) > 20){
                if(actions1.contains("Climb-up") || actions1.contains("Climb-down")){
                    return -1;
                } else if(actions2.contains("Climb-up") || actions2.contains("Climb-down")){
                    return 1;
                }
            } else if(actions1.contains("Climb-down") || actions1.contains("Climb-up")){
                return 1;
            } else if(actions2.contains("Climb-down") || actions2.contains("Climb-up")){
                return -1;
            }
            if (actions1.contains("Close")) {
                return 1;
            }
            if(actions2.contains("Close")){
                return -1;
            }
//            else if(actions1.contains("Climb-up") || actions1.contains("Climb-down")){
//                return 1;
//            } else if(actions2.contains("Climb-up") || actions2.contains("Climb-down")){
//                return -1;
//            }
//            if(actions1.contains("Open")){
//                if(actions2.contains("Close")){
//                    return -1;
//                }
//            }
//            if(actions2.contains("Open")){
//                if(actions1.contains("Close")){
//                    return 1;
//                }
//            }
            return c;
        });
        StringBuilder a = new StringBuilder("Detected: ");
        objects.forEach(object -> a.append(object.name()).append(" "));
        getInstance().log(a);



        return objects;
    }

    /**
     * Filter that accepts only interactive objects to progress in path.
     *
     * @param x
     * @param y
     * @param z
     * @param destinationDetails context where destination is at
     * @return
     */
    private static Predicate<GameObject> interactiveObjectFilter(int x, int y, int z, PathAnalyzer.DestinationDetails destinationDetails){
        return rsObject -> {
            String name = rsObject.getName();
            if (getInstance().sortedBlackList.contains(name)) {
                return false;
            }
            if(getInstance().blacklistedObjects.stream().anyMatch(p -> p.test(rsObject))){
                return false;
            }
            List<String> actionsList = rsObject.actions();
            if (actionsList.stream().anyMatch(s -> s != null && getInstance().sortedBlackListOptions.contains(s))) {
                return false;
            }
            if (rsObject.getTile().distanceTo(destinationDetails.getDestination().getTile()) > 5) {
                return false;
            }
//            if (Arrays.stream(rsObject.getAllTiles()).noneMatch(rsTile -> rsTile.distanceTo(position) <= 3)) {
//                return false;
//            }
            return actionsList.stream().anyMatch(getInstance().sortedOptions::contains);
        };
    }

    private static String[] getViableOption(Collection<String> collection, PathAnalyzer.DestinationDetails destinationDetails){
        Set<String> set = new HashSet<>(collection);
        if (set.retainAll(getInstance().sortedHighPriorityOptions) && set.size() > 0){
            return set.toArray(new String[0]);
        }
        if (destinationDetails.getAssumedZ() > destinationDetails.getDestination().getZ()){
            if (collection.contains("Climb-up")){
                return new String[]{"Climb-up"};
            }
        }
        if (destinationDetails.getAssumedZ() < destinationDetails.getDestination().getZ()){
            if (collection.contains("Climb-down")){
                return new String[]{"Climb-down"};
            }
        }
        if (destinationDetails.getAssumedY() > 5000 && destinationDetails.getDestination().getZ() == 0 && destinationDetails.getAssumedZ() == 0){
            if (collection.contains("Climb-down")){
                return new String[]{"Climb-down"};
            }
        }
        String[] options = new String[collection.size()];
        collection.toArray(options);
        return options;
    }

    private static boolean clickOnObject(GameObject object, String... options){
        boolean result;

        if (isClosedTrapDoor(object, options)){
            result = handleTrapDoor(object);
        } else {
            result = InteractionHelper.click(object, options);
            getInstance().log("Interacting with (" + GameObjectHelper.getName(object) + ") at " + object.getTile() + " with options: " + Arrays.toString(options) + " " + (result ? "SUCCESS" : "FAIL"));
            WaitFor.milliseconds(250,800);
        }

        return result;
    }

    private static boolean isStrongholdDoor(GameObject object){
        List<String> doorNames = Arrays.asList("Gate of War", "Rickety door", "Oozing barrier", "Portal of Death");
        return  doorNames.contains(object.name());
    }



    private static void handleStrongholdQuestions() {
        NPCInteraction.handleConversation("Use the Account Recovery System.",
                "Nobody.",
                "Don't tell them anything and click the 'Report Abuse' button.",
                "Me.",
                "Only on the RuneScape website.",
                "Report the incident and do not click any links.",
                "Authenticator and two-step login on my registered email.",
                "No way! You'll just take my gold for your own! Reported!",
                "No.",
                "Don't give them the information and send an 'Abuse Report'.",
                "Don't give them my password.",
                "The birthday of a famous person or event.",
                "Through account settings on oldschool.runescape.com.",
                "Secure my device and reset my RuneScape password.",
                "Report the player for phishing.",
                "Don't click any links, forward the email to reportphishing@jagex.com.",
                "Inform Jagex by emailing reportphishing@jagex.com.",
                "Don't give out your password to anyone. Not even close friends.",
                "Politely tell them no, then use the 'Report Abuse' button.",
                "Set up 2 step authentication with my email provider.",
                "No, you should never buy a RuneScape account.",
                "Do not visit the website and report the player who messaged you.",
                "Only on the RuneScape website.",
                "Don't type in my password backwards and report the player.",
                "Virus scan my device then change my password.",
                "No, you should never allow anyone to level your account.",
                "Don't give out your password to anyone. Not even close friends.",
                "Report the stream as a scam. Real Jagex streams have a 'verified' mark.",
                "Read the text and follow the advice given.",
                "No way! I'm reporting you to Jagex!",
                "Talk to any banker in RuneScape.",
                "Talk to any banker.", // Not sure if its a seperate question to above?
                "Secure my device and reset my RuneScape password.",
                "Don't share your information and report the player.",
                "Nothing, it's a fake.",
                "Delete it - it's a fake!"
        );
    }

    private static boolean isClosedTrapDoor(GameObject object, String[] options){
        return  (object.name().equals("Trapdoor") && Arrays.asList(options).contains("Open"));
    }

    private static boolean handleTrapDoor(GameObject object){
        if (getActions(object).contains("Open")){
            if (!InteractionHelper.click(object, "Open") && WaitFor.condition(8000, () -> {
                List<GameObject> objects = Objects.stream(15).action("Climb-down").filter(Filters.Objects.inArea(AreaHelper.fromCenter(object, 2))).list();
                if (objects.size() > 0 && getActions(objects.get(0)).contains("Climb-down")){
                    return WaitFor.Return.SUCCESS;
                }
                return WaitFor.Return.IGNORE;
            }) == WaitFor.Return.SUCCESS){
                return false;
            } else {
                List<GameObject> objects = Objects.stream(15).action("Climb-down").filter(Filters.Objects.inArea(AreaHelper.fromCenter(object, 2))).list();
                return objects.size() > 0 && handleTrapDoor(objects.get(0));
            }
        }
        getInstance().log("Interacting with (" + object.name() + ") at " + object.getTile() + " with option: Climb-down");
        return InteractionHelper.click(object, "Climb-down");
    }

    public static List<String> getActions(GameObject object){
        List<String> list = new ArrayList<>();
        if (object == GameObject.getNil()){
            return list;
        }
        return object.actions();
    }

    @Override
    public String getName() {
        return "Object Handler";
    }

    private static List<Integer> SLASH_WEAPONS = new ArrayList<>(Arrays.asList(1,4,9,10,12,17,20,21));

    private static boolean canLeftclickWeb(){
        return (SLASH_WEAPONS.contains(Varpbits.value(357))) || Inventory.stream().name("Knife").count() > 0;
    }
    private static boolean useBladeOnWeb(GameObject web){
        if(Inventory.selectedItem() != Item.getNil()){
            List<Item> slashable = Inventory.stream().name("whip", "sword", "dagger", "claws", "scimitar", " axe", "knife", "halberd", "machete", "rapier").list();
            if(slashable.size() == 0 || !slashable.get(0).click("Use"))
                return false;
        }
        return InteractionHelper.click(web, "Use");
    }

}
