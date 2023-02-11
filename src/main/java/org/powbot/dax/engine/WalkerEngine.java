package org.powbot.dax.engine;


import org.powbot.api.Condition;
import org.powbot.api.Point;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.dax.engine.bfs.BFS;
import org.powbot.dax.engine.collision.CollisionDataCollector;
import org.powbot.dax.engine.collision.RealTimeCollisionTile;
import org.powbot.dax.engine.interaction.PathObjectHandler;
import org.powbot.dax.engine.local.PathAnalyzer;
import org.powbot.dax.engine.local.Reachable;
import org.powbot.dax.engine.navigation.Charter;
import org.powbot.dax.engine.navigation.NavigationSpecialCase;
import org.powbot.dax.engine.navigation.PathUtils;
import org.powbot.dax.engine.navigation.ShipUtils;
import org.powbot.dax.shared.PathFindingNode;
import org.powbot.dax.shared.helpers.AccurateMouse;
import org.powbot.dax.shared.helpers.Projection;
import org.powbot.dax.teleports.Teleport;
import org.powbot.mobile.script.ScriptManager;

import java.util.List;

public class WalkerEngine implements Loggable {

    private static WalkerEngine walkerEngine;

    private int attemptsForAction;
    private final int failThreshold;
    private boolean navigating;
    private List<Tile> currentPath;

    private WalkerEngine(){
        attemptsForAction = 0;
        failThreshold = 3;
        navigating = false;
        currentPath = null;
    }

    public static WalkerEngine getInstance(){
        return walkerEngine != null ? walkerEngine : (walkerEngine = new WalkerEngine());
    }

    public boolean walkPath(List<Tile> path){
        return walkPath(path, null);
    }

    public List<Tile> getCurrentPath() {
        return currentPath;
    }

    /**
     *
     * @param path
     * @param walkingCondition
     * @return
     */
    public boolean walkPath(List<Tile> path, WalkingCondition walkingCondition){
        return walkPath(path, walkingCondition, 10, 75);
    }

    public boolean walkPath(List<Tile> path, WalkingCondition walkingCondition, int runMin, int runMax){
        if (path.size() == 0) {
            log("Path is empty");
            return false;
        }


        if (!handleTeleports(path)) {
            log(Level.WARNING, "Failed to handle teleports...");
            return false;
        } else {
            log("Successfully handled teleports.");
        }

        Varpbits.invalidateCache();

        int wantedEnergy = runMax > runMin ? Random.nextInt(runMin, runMax) : 0;


        navigating = true;
        currentPath = path;
        try {
            PathAnalyzer.DestinationDetails destinationDetails;
            resetAttempts();

            while (true) {

                if(ScriptManager.INSTANCE.isStopping()){
                    return false;
                }
                if(ScriptManager.INSTANCE.isPaused()){
                    WaitFor.milliseconds(1000);
                    continue;
                }

                if (!Game.loggedIn()){
                    log("We are not logged in.");
                    return false;
                }

                if (wantedEnergy > 0 && !Movement.running()) {
                    Movement.running(true);
                }

                if (ShipUtils.isOnShip()) {
                    log("Exiting ship via gangplank.");
                    if (!ShipUtils.crossGangplank()) {
                        log("Failed to exit ship via gangplank.");
                        failedAttempt();
                    }
                    WaitFor.milliseconds(50);
                    continue;
                }

                if (isFailedOverThreshhold()) {
                    log("Too many failed attempts");
                    return false;
                }

                if (Bank.opened()) {
                    Bank.close();
                }

                destinationDetails = PathAnalyzer.furthestReachableTile(path);
                if (destinationDetails == null || PathUtils.getFurthestReachableTileInMinimap(path) == null) {
                    log("Could not grab destination details, destination details: " + destinationDetails);
                    failedAttempt();
                    continue;
                }

                RealTimeCollisionTile currentNode = destinationDetails.getDestination();
                Tile assumedNext = destinationDetails.getAssumed();



//                if (destinationDetails.getState() != PathAnalyzer.PathState.FURTHEST_CLICKABLE_TILE) {
                    log(destinationDetails.toString());
//                } 

                final RealTimeCollisionTile destination = currentNode;
                if (!Projection.isInMinimap(new Tile(destination.getX(), destination.getY(), destination.getZ()))) {
                    log("Closest tile in path is not in minimap: " + destination);
                    failedAttempt();
                    continue;
                }


                CustomConditionContainer conditionContainer = new CustomConditionContainer(walkingCondition);
                switch (destinationDetails.getState()) {
                    case DISCONNECTED_PATH:
                        if (currentNode.getTile().distanceTo(Players.local().tile()) > 10){
                            clickMinimap(currentNode);
                            WaitFor.milliseconds(1200, 3400);
                        }
                        NavigationSpecialCase.SpecialLocation specialLocation = NavigationSpecialCase.getLocation(currentNode.getTile()),
                            specialLocationDestination = NavigationSpecialCase.getLocation(assumedNext);
                        if (specialLocation != null && specialLocationDestination != null) {
                            log("[SPECIAL LOCATION] We are at " + specialLocation + " and our destination is " + specialLocationDestination);
                            if (!NavigationSpecialCase.handle(specialLocationDestination)) {
                                log("Failed to handle special case.");
                                failedAttempt();
                            } else {
                                successfulAttempt();
                            }
                            break;
                        }

                        Charter.LocationProperty
                            locationProperty = Charter.LocationProperty.getLocation(currentNode.getTile()),
                            destinationProperty = Charter.LocationProperty.getLocation(assumedNext);
                        if (locationProperty != null && destinationProperty != null) {
                            log("Chartering to: " + destinationProperty);
                            if (!Charter.to(destinationProperty)) {
                                failedAttempt();
                            } else {
                                successfulAttempt();
                            }
                            break;
                        }
                        //DO NOT BREAK OUT
                    case OBJECT_BLOCKING:
                        Tile walkingTile = Reachable.getBestWalkableTile(destination.getTile(), new Reachable());
                        if (isDestinationClose(destination) || (walkingTile != null ? AccurateMouse.clickMinimap(walkingTile) : clickMinimap(destination))) {
                            log("Handling Object...");
                            if (!PathObjectHandler.handle(destinationDetails, path)) {
                                log("Failed to handle object.");
                                failedAttempt();
                            } else {
                                successfulAttempt();
                                WaitFor.milliseconds(200,800);
                            }
                            break;
                        }
                        break;

                    case FURTHEST_CLICKABLE_TILE:
                        if (clickMinimap(currentNode)) {
                            long offsetWalkingTimeout = System.currentTimeMillis() + Random.nextInt(2500, 4000);
                            WaitFor.condition(10000, () -> {
                                switch (conditionContainer.trigger()) {
                                    case EXIT_OUT_WALKER_SUCCESS:
                                    case EXIT_OUT_WALKER_FAIL:
                                        return WaitFor.Return.SUCCESS;
                                }

                                PathAnalyzer.DestinationDetails furthestReachable = PathAnalyzer.furthestReachableTile(path);
                                PathFindingNode currentDestination = BFS.bfsClosestToPath(path, RealTimeCollisionTile.get(destination.getX(), destination.getY(), destination.getZ()));
                                if (currentDestination == null) {
                                    log("Could not walk to closest tile in path.");
                                    failedAttempt();
                                    return WaitFor.Return.FAIL;
                                }
                                int indexCurrentDestination = path.indexOf(currentDestination.getTile());

                                PathFindingNode closestToPlayer = PathAnalyzer.closestTileInPathToPlayer(path);
                                if (closestToPlayer == null) {
                                    log("Could not detect closest tile to player in path.");
                                    failedAttempt();
                                    return WaitFor.Return.FAIL;
                                }
                                int indexCurrentPosition = path.indexOf(closestToPlayer.getTile());
                                if (furthestReachable == null) {
                                    System.out.println("Furthest reachable is null");
                                    return WaitFor.Return.FAIL;
                                }
                                int indexNextDestination = path.indexOf(furthestReachable.getDestination().getTile());
                                if (indexNextDestination - indexCurrentDestination > 4 || indexCurrentDestination - indexCurrentPosition < 4) {
                                    log("New destination is available: " + furthestReachable.getDestination().getTile());
                                    return WaitFor.Return.SUCCESS;
                                }
                                if (System.currentTimeMillis() > offsetWalkingTimeout && !Players.local().inMotion()){
                                    log("Player is not moving and we are past the offsetWalkingTimeout");
                                    return WaitFor.Return.FAIL;
                                }
                                return WaitFor.milliseconds(100);
                            });
                        } else {
                            log("Failed to click minimap tile: " + currentNode.getTile());
                            failedAttempt();
                        }
                        break;

                    case END_OF_PATH:
                        clickMinimap(destinationDetails.getDestination());
                        log("Reached end of path");
                        return true;
                    default:
                        log(Level.WARNING, "Reachedd end of walker engine switch statement without a valid destination details: " + destinationDetails);
                }

                switch (conditionContainer.getResult()) {
                    case EXIT_OUT_WALKER_SUCCESS:
                        return true;
                    case EXIT_OUT_WALKER_FAIL:
                        return false;
                }

                WaitFor.milliseconds(50, 100);

            }
        } finally {
            navigating = false;
        }
    }

    boolean isNavigating() {
        return navigating;
    }

    boolean isDestinationClose(PathFindingNode pathFindingNode){
        final Tile playerPosition = Players.local().tile();
        return Projection.isInMinimap(new Tile(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ()))
            && playerPosition.distanceTo(new Tile(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ())) <= 12
            && (BFS.isReachable(RealTimeCollisionTile.get(playerPosition.getX(), playerPosition.getY(), playerPosition.floor()), RealTimeCollisionTile.get(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ()), 250));
    }

    public boolean clickMinimap(PathFindingNode pathFindingNode){
        final Tile playerPosition = Players.local().tile();
        if (playerPosition.distanceTo(pathFindingNode.getTile()) <= 1){
            log("We are 1 tile or less away from target node, returning true.");
            return true;
        }
        PathFindingNode randomNearby = BFS.getRandomTileNearby(pathFindingNode);

        if (randomNearby == null){
            log("Unable to generate randomization.");
            return false;
        }

        log("Randomize(" + pathFindingNode.getX() + "," + pathFindingNode.getY() + "," + pathFindingNode.getZ() + ") -> (" + randomNearby.getX() + "," + randomNearby.getY() + "," + randomNearby.getZ() + ")");
        return AccurateMouse.clickMinimap(new Tile(randomNearby.getX(), randomNearby.getY(), randomNearby.getZ())) || AccurateMouse.clickMinimap(new Tile(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ()));
    }

    public void hoverMinimap(PathFindingNode pathFindingNode){
        if (pathFindingNode == null){
            return;
        }
        Point point = Projection.tileToMinimap(new Tile(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ()));
//        Mouse.move(point);
    }

    private boolean resetAttempts(){
        return successfulAttempt();
    }

    private boolean successfulAttempt(){
        attemptsForAction = 0;
        return true;
    }

    private void failedAttempt(){
        if (Camera.pitch() < 90) {
            Camera.pitch(Random.nextInt(90, 100));
        }
        if (++attemptsForAction > 1) {
            Camera.angle(Random.nextInt(0, 360));
        }
        log("Failed attempt on action.");
        WaitFor.milliseconds(450 * (attemptsForAction + 1), 850 * (attemptsForAction + 1));
        CollisionDataCollector.generateRealTimeCollision();
    }

    private boolean isFailedOverThreshhold(){
        return attemptsForAction >= failThreshold;
    }

    private static class CustomConditionContainer {
        private final WalkingCondition walkingCondition;
        private WalkingCondition.State result;
        CustomConditionContainer(WalkingCondition walkingCondition){
            this.walkingCondition = walkingCondition;
            this.result = WalkingCondition.State.CONTINUE_WALKER;
        }
        public WalkingCondition.State trigger(){
            result = (walkingCondition != null ? walkingCondition.action() : result);
            return result != null ? result : WalkingCondition.State.CONTINUE_WALKER;
        }
        public WalkingCondition.State getResult() {
            return result;
        }
    }

    @Override
    public String getName() {
        return "Walker Engine";
    }

    private boolean handleTeleports(List<Tile> path) {
        Tile startPosition = path.get(0);
        Tile playerPosition = Players.local().tile();
        if(startPosition.equals(playerPosition))
            return true;
        if(Bank.opened())
            Bank.close();
        boolean members = Worlds.isCurrentWorldMembers();
        for (Teleport teleport : Teleport.values()) {
            if (!teleport.canUse() || (teleport.requiresMembers() && !members)) continue;
            if(teleport.isAtTeleportSpot(startPosition) && !teleport.isAtTeleportSpot(playerPosition)){
                log("Using teleport method: " + teleport);
                teleport.trigger();
                if (WaitFor.condition(Random.nextInt(3000, 20000),
                    () -> startPosition.distanceTo(Players.local().tile()) < 10 ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS) {
                    return Condition.wait(() -> Players.local().animation() == -1, 200, 10);
                }
            }
        }
        return true;
    }

}
