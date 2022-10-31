package org.powbot.dax.engine.local;

import org.powbot.api.Tile;
import org.powbot.api.rt4.Players;
import org.powbot.dax.engine.bfs.BFS;
import org.powbot.dax.engine.collision.CollisionDataCollector;
import org.powbot.dax.engine.collision.CollisionFlags;
import org.powbot.dax.engine.collision.RealTimeCollisionTile;
import org.powbot.dax.shared.PathFindingNode;

import java.util.List;


public class PathAnalyzer {

    public static RealTimeCollisionTile closestToPlayer = null, furthestReachable = null;

    public static RealTimeCollisionTile closestTileInPathToPlayer(List<Tile> path) {
        CollisionDataCollector.generateRealTimeCollision();
        final Tile playerPosition = Players.local().tile();
        closestToPlayer = (RealTimeCollisionTile) BFS.bfsClosestToPath(path, RealTimeCollisionTile.get(playerPosition.getX(), playerPosition.getY(), playerPosition.floor()));
        return closestToPlayer;
    }


    public static DestinationDetails furthestReachableTile(List<Tile> path){
        return furthestReachableTile(path, closestTileInPathToPlayer(path));
    }

    public static DestinationDetails furthestReachableTile(List<Tile> path, PathFindingNode currentPosition){
        if (path == null || currentPosition == null){
            System.out.println("PathAnalyzer attempt to find closest tile in path: " + currentPosition + " " + path);
            return null;
        }
        outside:
        for (int i = path.indexOf(currentPosition.getTile()); i < path.size() && i >= 0; i++) {
            Tile currentNode = path.get(i);
            RealTimeCollisionTile current = RealTimeCollisionTile.get(currentNode.getX(), currentNode.getY(), currentNode.floor());
            if (current == null){
//                System.out.println("Current RTCT is null.");
                return null;
            }
            if (i + 1 >= path.size()){
//                System.out.println("We are at the end of the path from the furthestReachableTile method.");
                return new DestinationDetails(PathState.END_OF_PATH, current);
            }
            Tile nextNode = path.get(i + 1);
            RealTimeCollisionTile next = RealTimeCollisionTile.get(nextNode.getX(), nextNode.getY(), nextNode.floor());
            if(next != null && !isLoaded(next) && nextNode.matrix().onMap()){
//                System.out.println("Next node is not loaded and is on screen.");
                return new DestinationDetails(PathState.FURTHEST_CLICKABLE_TILE, current);
            }
            Direction direction = directionTo(current.getTile(), nextNode);
            if (direction == Direction.UNKNOWN){
                furthestReachable = current;
                return new DestinationDetails(PathState.DISCONNECTED_PATH, current, nextNode.getX(), nextNode.getY(), nextNode.floor());
            }
            if (!direction.confirmTileMovable(RealTimeCollisionTile.get(current.getX(), current.getY(), current.getZ()))){
//                System.out.println("Unable to confirm that the next rtct is movable from the current rtct.");
                for (int j = 1; j < 5 && j + i < path.size(); j++) {
                    Tile nextInPath = path.get(i + j);
                    RealTimeCollisionTile nextInPathCollision = RealTimeCollisionTile.get(nextInPath.getX(), nextInPath.getY(), nextInPath.floor());
                    if (nextInPathCollision != null && nextInPathCollision.isWalkable()){
                        if (BFS.isReachable(current, nextInPathCollision, 150) && nextInPathCollision.getTile().matrix().onMap()) {
                            i += j-2;
                            continue outside;
                        }
                    }
                }

                furthestReachable = current;
                if (next != null) {
                    return new DestinationDetails(PathState.OBJECT_BLOCKING, current, next);
                }
                return new DestinationDetails(PathState.OBJECT_BLOCKING, current, nextNode.getX(), nextNode.getY(), nextNode.floor());
            }
            if (!new Tile(nextNode.getX(), nextNode.getY(), nextNode.floor()).matrix().onMap()){
//                System.out.println("Next node is not clickable.");
//            if(new Tile(nextNode.getX(), nextNode.getY(), nextNode.floor()).isClickable()) {
                furthestReachable = current;
                if (next != null) {
                    return new DestinationDetails(PathState.FURTHEST_CLICKABLE_TILE, current, next);
                }
                return new DestinationDetails(
                    PathState.FURTHEST_CLICKABLE_TILE, current, nextNode.getX(), nextNode.getY(), nextNode.floor());
            }
        }
//        System.out.println("Reached end of furthestReachableTile method");
        return null;
    }


    public static DestinationDetails furthestReachableScreenTile(List<Tile> path){
        return furthestReachableScreenTile(path, closestTileInPathToPlayer(path));
    }

    public static DestinationDetails furthestReachableScreenTile(List<Tile> path, PathFindingNode currentPosition){
        if (path == null || currentPosition == null){
//            System.out.println("PathAnalyzer attempt to find closest tile in path: " + currentPosition + " " + path);
            return null;
        }
        outside:
        for (int i = path.indexOf(currentPosition.getTile()); i < path.size() && i >= 0; i++) {
            Tile currentNode = path.get(i);
            RealTimeCollisionTile current = RealTimeCollisionTile.get(currentNode.getX(), currentNode.getY(), currentNode.floor());
            if (current == null){
                return null;
            }
            if (i + 1 >= path.size()){
                return new DestinationDetails(PathState.END_OF_PATH, current);
            }
            Tile nextNode = path.get(i + 1);
            RealTimeCollisionTile next = RealTimeCollisionTile.get(nextNode.getX(), nextNode.getY(), nextNode.floor());
            if(!isLoaded(next) && nextNode.matrix().inViewport()){
                return new DestinationDetails(PathState.FURTHEST_CLICKABLE_TILE, current);
            }
            Direction direction = directionTo(current.getTile(), nextNode);
            if (direction == Direction.UNKNOWN){
                furthestReachable = current;
                return new DestinationDetails(PathState.DISCONNECTED_PATH, current, nextNode.getX(), nextNode.getY(), nextNode.floor());
            }
            if (!direction.confirmTileMovable(RealTimeCollisionTile.get(current.getX(), current.getY(), current.getZ()))){

                for (int j = 1; j < 5 && j + i < path.size(); j++) {
                    Tile nextInPath = path.get(i + j);
                    RealTimeCollisionTile nextInPathCollision = RealTimeCollisionTile.get(nextInPath.getX(), nextInPath.getY(), nextInPath.floor());
                    if (nextInPathCollision != null && nextInPathCollision.isWalkable()){
                        if (BFS.isReachable(current, nextInPathCollision, 200)) {
                            i += j-2;
                            continue outside;
                        }
                    }
                }

                furthestReachable = current;
                if (next != null) {
                    return new DestinationDetails(PathState.OBJECT_BLOCKING, current, next);
                }
                return new DestinationDetails(PathState.OBJECT_BLOCKING, current, nextNode.getX(), nextNode.getY(), nextNode.floor());
            }
            if (!new Tile(nextNode.getX(), nextNode.getY(), nextNode.floor()).tile().matrix().inViewport(true)){
                furthestReachable = current;
                if (next != null) {
                    return new DestinationDetails(PathState.FURTHEST_CLICKABLE_TILE, current, next);
                }
                return new DestinationDetails(
                    PathState.FURTHEST_CLICKABLE_TILE, current, nextNode.getX(), nextNode.getY(), nextNode.floor());
            }
        }
        return null;
    }

    public static Direction directionTo(Tile fromNode, Tile toNode){
        if (fromNode.floor() != toNode.floor()){
            return Direction.UNKNOWN;
        }
        for (Direction direction : Direction.values()){
            if (fromNode.getX() + direction.x == toNode.getX() && fromNode.getY() + direction.y == toNode.getY()){
                return direction;
            }
        }
        return Direction.UNKNOWN;
    }

    public static class DestinationDetails {
        private PathState state;
        private RealTimeCollisionTile destination, nextTile;
        private int assumedX, assumedY, assumedZ;

        private DestinationDetails(PathState state, RealTimeCollisionTile destination){
            this.state = state;
            this.destination = destination;
            this.assumedX = -1;
            this.assumedY = -1;
            this.assumedZ = -1;
        }
        private DestinationDetails(PathState state, RealTimeCollisionTile destination, RealTimeCollisionTile nextTile){
            this.state = state;
            this.destination = destination;
            this.nextTile = nextTile;
            this.assumedX = nextTile.getX();
            this.assumedY = nextTile.getY();
            this.assumedZ = nextTile.getZ();

        }
        private DestinationDetails(PathState state, RealTimeCollisionTile destination, int x, int y, int z){
            this.state = state;
            this.destination = destination;
            this.assumedX = x;
            this.assumedY = y;
            this.assumedZ = z;
        }

        public PathState getState() {
            return state;
        }

        public Tile getAssumed(){
            return new Tile(assumedX, assumedY, assumedZ);
        }


        public RealTimeCollisionTile getDestination() {
            return destination;
        }

        public RealTimeCollisionTile getNextTile() {
            return nextTile;
        }

        public int getAssumedX() {
            return assumedX;
        }

        public int getAssumedY() {
            return assumedY;
        }

        public int getAssumedZ() {
            return assumedZ;
        }

        @Override
        public String toString(){
            String debug = "PATH_DEBUG[ ";
            if (state == PathState.END_OF_PATH){
                debug += state;
            } else {
                if (destination != null){
                    debug += (destination.getX() + ", " + destination.getY() + ", " + destination.getZ());
                } else {
                    debug += null;
                }
                debug += ") -> " + state + " -> (";
                if (nextTile != null){
                    debug += (nextTile.getX() + ", " + nextTile.getY() + ", " + nextTile.getZ());
                } else {
                    debug += null + " [" + assumedX + ", " + assumedY + ", " + assumedZ + "] ";
                }
                debug += ")";
            }
            debug += " ]";
            return debug;
        }
    }

    public enum PathState {
        FURTHEST_CLICKABLE_TILE,
        DISCONNECTED_PATH,
        OBJECT_BLOCKING,
        END_OF_PATH
    }

    private enum Direction {
        NORTH (0, 1),
        EAST (1, 0),
        SOUTH (0, -1),
        WEST (-1, 0),
        NORTH_EAST (1, 1),
        SOUTH_EAST (1, -1),
        NORTH_WEST (-1, 1),
        SOUTH_WEST (-1, -1),
        SAME_TILE (0, 0),
        UNKNOWN (104, 104);

        int x, y;

        Direction(int x, int y){
            this.x = x;
            this.y = y;
        }

        boolean confirmTileMovable(RealTimeCollisionTile realTimeCollisionTile){
            if (this == SAME_TILE){
                return true;
            }
            RealTimeCollisionTile destination = RealTimeCollisionTile.get(realTimeCollisionTile.getX() + this.x, realTimeCollisionTile.getY() + this.y, realTimeCollisionTile.getZ());
            if (destination == null){
                return false;
            }
            if (realTimeCollisionTile.getNeighbors().contains(destination)){
                return true;
            }
            return BFS.isReachable(realTimeCollisionTile, destination, 150);
        }
    }

    private static boolean isLoaded(RealTimeCollisionTile rtc){
        Tile tile = rtc.getTile();
        int localX = tile.localX(), localY = tile.localY();
        return !(CollisionFlags.check(rtc.getCollisionData(), CollisionFlags.CLOSED) && ((localX < 5 || localX > 98) || (localY
         < 5 || localY > 98)));
    }

}
