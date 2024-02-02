package org.powbot.dax.engine.navigation;

import org.powbot.api.Locatable;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Actor;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.Movement;
import org.powbot.api.rt4.Players;
import org.powbot.dax.engine.local.AStarNode;
import org.powbot.dax.engine.local.Reachable;
import org.powbot.util.TransientGetter;
import org.powbot.util.TransientGetter2D;

import java.util.*;

/**
 * For local pathing ONLY. Anything outside your region will return unexpected results.
 */
public class DaxPathFinder {

    public static class Destination {
        private Tile tile;
        private Destination parent;
        private int distance;

        public Destination(Tile tile, Destination parent, int distance) {
            this.tile = tile;
            this.parent = parent;
            this.distance = distance;
        }

        public Tile getLocalTile() {
            return tile;
        }

        public Tile getWorldTile() {
            return tile.globalTile();
        }

        public Destination getParent() {
            return parent;
        }

        public int getDistance() {
            return distance;
        }

        public List<Tile> getPath() {
            return DaxPathFinder.getPath(this);
        }
    }

    /**
     * Method for grabbing the path your character is currently walking.
     *
     * @return The path your character is following.
     */
    public static List<Tile> getWalkingQueue() {
        return getWalkingQueue(getMap());
    }

    /**
     * Method for grabbing the path your character is currently walking.
     *
     * @param map
     * @return The path your character is following.
     */
    public static List<Tile> getWalkingQueue(Destination[][] map) {
        Tile destination = Movement.destination();
        if (destination == Tile.getNil()) {
            destination = getNextWalkingTile();
        }
        return destination != null ? getPath(map, destination) : null;
    }

    /**
     *
     * Method to check if your character is walking to a destination.
     *
     * @param tile
     * @return true if your character is walking or will walk to that tile in the next game tick.
     */
    public static boolean isWalkingTowards(Tile tile){
        Tile tile1 = getNextWalkingTile();
        return tile1 != null && tile1.equals(tile);
    }

    /**
     *
     * Next tile that your character is moving to in the current/next game tick.
     *
     * @return The next tile that your character is walking to
     */
    public static Tile getNextWalkingTile(){
        ArrayList<Tile> tiles = getWalkingHistory();
        return tiles.size() > 0 && !tiles.get(0).equals(Players.local().tile()) ? tiles.get(0) : null;
    }

    /**
     *
     * @param tile
     * @return Distance to a tile accounting for collision. Integer.MAX_VALUE if not reachable.
     */
    public static int distance(Locatable tile) {
        return distance(getMap(), tile.tile());
    }

    public static int distance(Destination[][] map, Locatable tile) {
        Tile worldTile = tile.tile().globalTile();
        int x = worldTile.getX(), y = worldTile.getY();

        if (!validLocalBounds(tile)) {
            return Integer.MAX_VALUE;
        }

        Destination destination = map[x][y];
        return destination == null ? Integer.MAX_VALUE : destination.distance;
    }

    public static boolean canReach(Tile tile) {
        return canReach(getMap(), tile);
    }

    public static boolean canReach(Destination[][] map, Tile tile) {
        if (tile.floor() != Players.local().tile().floor()) return false;
        Tile worldTile = tile.globalTile();
        int x = worldTile.getX(), y = worldTile.getY();
        if (!validLocalBounds(tile) || x > map.length || y > map[x].length) {
            return false;
        }
        Destination destination = map[x][y];
        return destination != null;
    }

    public static List<Tile> getPath(Tile tile) {
        return getPath(getMap(), tile);
    }

    public static List<Tile> getPath(Destination destination) {
        Stack<Tile> Tiles = new Stack<>();
        Destination parent = destination;
        while (parent != null) {
            Tiles.add(parent.getWorldTile());
            parent = parent.parent;
        }
        return new ArrayList<>(Tiles);
    }

    public static List<Tile> getPath(Destination[][] map, Tile tile) {
        int x = tile.localX(), y = tile.localY();

        Destination destination = map[x][y];

        if (destination == null) {
            return null;
        }

        return destination.getPath();
    }

    public static int[][] getCollisionData(){
        TransientGetter2D<Integer> flags = Movement.collisionMap(Game.floor()).flags();
        int[][] output = new int[flags.getSize()][];
        int index1 = 0;
        for (TransientGetter<Integer> next : flags) {
            output[index1] = Arrays.stream(next.clone()).mapToInt(i->i).toArray();
            index1++;
        }
        return output;
    }

    public static Destination[][] getMap() {
        final Tile home = Players.local().tile();
        Destination[][] map = new Destination[104][104];
        if(home.getX() < 0 || home.getY() < 0)
            return map;
        int[][] collisionData = getCollisionData();
        if(collisionData.length < home.getX() || collisionData[home.getX()].length < home.getY()){
            return map;
        }

        Queue<Destination> queue = new LinkedList<>();
        queue.add(new Destination(home, null, 0));
        map[home.getX()][home.getY()] = queue.peek();

        while (!queue.isEmpty()) {
            Destination currentLocal = queue.poll();

            int x = currentLocal.getLocalTile().getX(), y = currentLocal.getLocalTile().getY();
            Destination destination = map[x][y];

            for (Reachable.Direction direction : Reachable.Direction.values()) {
                if (!direction.isValidDirection(x, y, Movement.collisionMap(Game.floor()).flags())) {
                    continue; //Cannot traverse to tile from current.
                }

                Tile neighbor = direction.getPointingTile(currentLocal.getLocalTile());
                int destinationX = neighbor.getX(), destinationY = neighbor.getY();

                if (!AStarNode.isWalkable(collisionData[destinationX][destinationY])) {
                    continue;
                }

                if (map[destinationX][destinationY] != null) {
                    continue; //Traversed already
                }

                map[destinationX][destinationY] = new Destination(neighbor, currentLocal, destination.getDistance() + 1);
                queue.add(map[destinationX][destinationY]);
            }

        }
        return map;
    }

//    public static void drawQueue(Destination[][] map, Rendering Rendering) {
//        Rendering2D g = (Rendering2D) Rendering;
//        List<Tile> path = getWalkingQueue(map);
//        if (path == null) {
//            return;
//        }
//
//        Tile previousTile = path.get(0);
//        for (int i = 1; i < path.size(); i++) {
//            Point point1 = Projection.tileToScreen(path.get(i), 0);
//            Point point2 = Projection.tileToScreen(previousTile, 0);
//            if (point1 == null || point1.getX() == -1 || point2 == null || point2.getX() == -1) {
//                continue;
//            }
//            g.setColor(new Color(255, 0, 11, 116));
//            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//            g.drawLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
//            previousTile = path.get(i);
//        }

//    }

//    public static void drawPaths(Destination[][] map, Rendering Rendering) {
//        Rendering2D g = (Rendering2D) Rendering;
//        for (Destination[] destinations : map) {
//            for (Destination destination : destinations) {
//
//                if (destination == null || destination.getParent() == null) {
//                    continue;
//                }
//
//                Tile tile = destination.getWorldTile();
//                Tile parent = destination.getParent().getWorldTile();
//
//                if (!tile.isOnScreen() && !parent.isOnScreen()) {
//                    continue;
//                }
//
//                Point point1 = Projection.tileToScreen(tile, 0);
//                Point point2 = Projection.tileToScreen(parent, 0);
//
//                if (point1 == null || point1.getX() == -1 || point2 == null || point2.getX() == -1) {
//                    continue;
//                }
//
//                g.setColor(new Color(255, 255, 255, 60));
//                g.setStroke(new BasicStroke(1));
//                g.drawLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
//            }
//        }
//    }

    private static boolean validLocalBounds(Locatable locatable) {
        Tile tile = locatable.tile();
        return tile.getX() >= 0 && tile.getX() < 104 && tile.getY() >= 0 && tile.getY() < 104;
    }

    private static ArrayList<Tile> getWalkingHistory(){
        return getWalkingHistory(Players.local());
    }

    private static ArrayList<Tile> getWalkingHistory(Actor rsCharacter){
        ArrayList<Tile> walkingQueue = new ArrayList<>();
        if (rsCharacter == null){
            return walkingQueue;
        }
        int plane = rsCharacter.tile().floor();
        int[] xIndex = getWalkingQueueX(rsCharacter), yIndex = getWalkingQueueY(rsCharacter);
        for (int i = 0; i < xIndex.length && i < yIndex.length; i++) {
            walkingQueue.add(new Tile(xIndex[i], yIndex[i], plane));
        }
        return walkingQueue;
    }

    public enum DIRECTION {
        E(6), N(4), NE(5), NONE(-1), NW(3), S(0), SE(7), SW(7), W(2);

        private final int orientation;
        DIRECTION(int orientation){
            this.orientation = orientation;
        }

        public static DIRECTION of(int orientation){
            return Arrays.stream(values()).filter(t -> t.orientation == orientation).findFirst().orElse(NONE);
        }
    }

    private static int[] getWalkingQueueX(Actor actor){
        boolean moving = actor.inMotion();
        if(!moving){
            return new int[0];
        }
        int x = actor.localX();
//        int speed = this.actor.speed();
        switch(DIRECTION.of(actor.orientation())){

            case E:
            case SE:
            case NE:
                return new int[]{x, x + 1, x + 2};
            case N:
            case S:
                return new int[]{x, x, x};
            case NONE:
                return new int[0];
            case NW:
            case SW:
            case W:
                return new int[]{x, x - 1, x - 2};
        }
        return new int[0];
    }

    private static int[] getWalkingQueueY(Actor actor){
        boolean moving = actor.inMotion();
        if(!moving){
            return new int[0];
        }
        int y = actor.localY();
//        int speed = this.actor.speed();
        switch(DIRECTION.of(actor.orientation())){

            case NE:
            case NW:
            case N:
                return new int[]{y, y + 1, y + 2};
            case E:
            case W:
                return new int[]{y, y, y};
            case NONE:
                return new int[0];
            case SE:
            case SW:
            case S:
                return new int[]{y, y - 1, y - 2};
        }
        return new int[0];
    }
}
