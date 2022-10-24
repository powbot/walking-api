package org.powbot.dax.api;

import org.powbot.api.Locatable;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Players;
import org.powbot.dax.api.models.*;
import org.powbot.dax.shared.helpers.InterfaceHelper;
import org.powbot.dax.teleports.Teleport;
import org.powbot.dax.engine.Loggable;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.WalkerEngine;
import org.powbot.dax.engine.WalkingCondition;
import org.powbot.dax.engine.navigation.ShipUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DaxWalker implements Loggable {

    private static Map<Tile, Teleport> map;
    private static DaxWalker daxWalker;
    public static DaxWalker getInstance() {
        return daxWalker != null ? daxWalker : (daxWalker = new DaxWalker());
    }

    public boolean useRun = true;

    private WalkingCondition globalWalkingCondition;

    private DaxWalker() {
        globalWalkingCondition = () -> WalkingCondition.State.CONTINUE_WALKER;

        map = new ConcurrentHashMap<>();
        for (Teleport teleport : Teleport.values()) {
            map.put(teleport.getLocation(), teleport);
        }
    }

    public static WalkingCondition getGlobalWalkingCondition() {
        return getInstance().globalWalkingCondition;
    }

    public void useLocalDevelopmentServer(boolean b) {
        WebWalkerServerApi.getInstance().setTestMode(b);
    }

    public static void setGlobalWalkingCondition(WalkingCondition walkingCondition) {
        getInstance().globalWalkingCondition = walkingCondition;
    }

    public static void setCredentials(DaxCredentialsProvider daxCredentialsProvider) {
        WebWalkerServerApi.getInstance().setDaxCredentialsProvider(daxCredentialsProvider);
    }

    public static boolean walkTo(Locatable positionable) {
        return walkTo(positionable, null);
    }

    public static boolean walkTo(Locatable destination, WalkingCondition walkingCondition) {
        if (ShipUtils.isOnShip()) {
            ShipUtils.crossGangplank();
            WaitFor.milliseconds(500, 1200);
        }
        Tile start = Players.local().tile();
        if (start.equals(destination)) {
            return true;
        }

        RSItem[] inventory = Inventory.getAll();
        RSItem[] equipment = Equipment.getItems();
        PlayerDetails playerDetails = PlayerDetails.generate(inventory, equipment);
        boolean isInPvpWorld = InterfaceHelper.getAllInterfaces(90).stream()
                .anyMatch(i -> i.getTextureID() == 1046 && Interfaces.isInterfaceSubstantiated(i));


        List<PathRequestPair> pathRequestPairs = getInstance().getPathTeleports(playerDetails.isMember(), isInPvpWorld, destination.getPosition(), inventory, equipment);

        pathRequestPairs.add(new PathRequestPair(Point3D.fromPositionable(start), Point3D.fromPositionable(destination)));

	    List<PathResult> pathResults = WebWalkerServerApi.getInstance().getPaths(new BulkPathRequest(playerDetails,pathRequestPairs));

	    List<PathResult> validPaths = getInstance().validPaths(pathResults);

	    PathResult pathResult = getInstance().getBestPath(validPaths);
	    if (pathResult == null) {
            getInstance().log(Level.WARNING, "No valid path found");
		    return false;
	    } else {
            getInstance().log("Path cost: " + pathResult.getCost());
        }

        List<RSTile> path = new ArrayList<>(pathResult.toRSTilePath());
        getInstance().log("Path: [" + path.stream().map(Object::toString)
                .collect(Collectors.joining(", ")) + "]");

	    return WalkerEngine.getInstance().walkPath(path, getGlobalWalkingCondition().combine(walkingCondition));
    }

    public static boolean walkToBank() {
        return walkToBank(null, null);
    }

    public static boolean walkToBank(RunescapeBank bank) {
        return walkToBank(bank, null);
    }

    public static boolean walkToBank(WalkingCondition walkingCondition) {
        return walkToBank(null, walkingCondition);
    }

    public static boolean walkToBank(RunescapeBank bank, WalkingCondition walkingCondition) {
        if (ShipUtils.isOnShip()) {
            ShipUtils.crossGangplank();
            WaitFor.milliseconds(500, 1200);
        }

        if(bank != null)
            return walkTo(bank.getPosition(), walkingCondition);

        RSItem[] inventory = Inventory.getAll();
        RSItem[] equipment = Equipment.getItems();
        PlayerDetails playerDetails = PlayerDetails.generate(inventory, equipment);
        boolean isInPvpWorld = InterfaceHelper.getAllInterfaces(90).stream()
                .anyMatch(i -> i.getTextureID() == 1046 && Interfaces.isInterfaceSubstantiated(i));

        List<BankPathRequestPair> pathRequestPairs = getInstance().getBankPathTeleports(playerDetails.isMember(), isInPvpWorld, inventory, equipment);

        pathRequestPairs.add(new BankPathRequestPair(Point3D.fromPositionable(Players.local().tile()),null));

        List<PathResult> pathResults = WebWalkerServerApi.getInstance().getBankPaths(new BulkBankPathRequest(
	        playerDetails,pathRequestPairs));

        List<PathResult> validPaths = getInstance().validPaths(pathResults);
        PathResult pathResult = getInstance().getBestPath(validPaths);
        if (pathResult == null) {
            getInstance().log(Level.WARNING, "No valid path found");
            return false;
        }
        return WalkerEngine.getInstance().walkPath(pathResult.toRSTilePath(), getGlobalWalkingCondition().combine(walkingCondition));
    }

    public static List<RSTile> getPath(Positionable destination){
        Tile start = Players.local().tile();
        if (start.equals(destination)) {
            return Collections.emptyList();
        }

        RSItem[] inventory = Inventory.getAll();
        RSItem[] equipment = Equipment.getItems();
        PlayerDetails playerDetails = PlayerDetails.generate(inventory, equipment);
        boolean isInPvpWorld = InterfaceHelper.getAllInterfaces(90).stream()
                .anyMatch(i -> i.getTextureID() == 1046 && Interfaces.isInterfaceSubstantiated(i));


        List<PathRequestPair> pathRequestPairs = getInstance().getPathTeleports(playerDetails.isMember(), isInPvpWorld, destination.getPosition(), inventory, equipment);

        pathRequestPairs.add(new PathRequestPair(Point3D.fromPositionable(start), Point3D.fromPositionable(destination.getPosition())));

        List<PathResult> pathResults = WebWalkerServerApi.getInstance().getPaths(new BulkPathRequest(playerDetails,pathRequestPairs));

        List<PathResult> validPaths = getInstance().validPaths(pathResults);

        PathResult pathResult = getInstance().getBestPath(validPaths);
        if (pathResult == null) {
            getInstance().log(Level.WARNING, "No valid path found");
            return Collections.emptyList();
        }

        getInstance().log("Got valid path.");

        return pathResult.toRSTilePath();
    }

    private static final List<Teleport> blacklist = new ArrayList<>();

    private static List<Teleport> getBlacklist() {
        return blacklist;
    }

    public static void blacklistTeleports(Teleport... teleports){
        getBlacklist().addAll(Arrays.asList(teleports));
    }

    public static void clearTeleportBlacklist(){
        getBlacklist().clear();
    }

    private List<PathRequestPair> getPathTeleports(boolean members, boolean pvp, Tile start, RSItem[] inventory, RSItem[] equipment) {
        return Teleport.getValidStartingRSTiles(members, pvp, getBlacklist(), inventory, equipment).stream()
                .map(t -> new PathRequestPair(Point3D.fromPositionable(t),
                        Point3D.fromPositionable(start)))
                .collect(Collectors.toList());
    }

    private List<BankPathRequestPair> getBankPathTeleports(boolean members, boolean pvp, RSItem[] inventory, RSItem[] equipment) {
        return Teleport.getValidStartingRSTiles(members, pvp, getBlacklist(), inventory, equipment).stream()
                .map(t -> new BankPathRequestPair(Point3D.fromPositionable(t), null))
                .collect(Collectors.toList());
    }

    public List<PathResult> validPaths(List<PathResult> list) {
        List<PathResult> result = list.stream().filter(pathResult -> pathResult.getPathStatus() == PathStatus.SUCCESS).collect(
		        Collectors.toList());
        if (!result.isEmpty()) {
            return result;
        }
        return Collections.emptyList();
    }

    public PathResult getBestPath(List<PathResult> list) {
        return list.stream().min(Comparator.comparingInt(this::getPathMoveCost)).orElse(null);
    }

    private int getPathMoveCost(PathResult pathResult) {
        if (Players.local().tile().equals(pathResult.getPath().get(0).toPositionable().getPosition())) {
//            System.out.println("Path starts at player current position.  Path cost: " + pathResult.getCost());
            return pathResult.getCost();
        }
        Tile startTile = pathResult.getPath().get(0).toPositionable().getPosition();
        Teleport teleport = map.get(startTile);
        if (teleport == null) {
//            System.out.println("Path is not with a teleport. Cost: " + pathResult.getCost());
            return pathResult.getCost();
        }
//        System.out.println("Path is with a teleport. Teleport used: " + teleport + " total cost: " + (teleport.getMoveCost() + pathResult.getCost()));
        return teleport.getMoveCost() + pathResult.getCost();
    }

    @Override
    public String getName() {
        return "DaxWalker";
    }
}
