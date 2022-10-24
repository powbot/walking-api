package org.powbot.dax.engine.local;


import org.tribot.api.General;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import org.powbot.dax.shared.helpers.BankHelper;

import java.util.*;

public class Reachable {

    private Tile[][] map;

    /**
     * Generates reachable map from player position
     */
    public Reachable() {
        this(null);
    }

    public Reachable(RSTile homeTile) {
        map = generateMap(homeTile != null ? homeTile : Players.local().tile());
    }

    public boolean isBlocked(Positionable position){
        Tile localPosition = position.getPosition().toLocalTile();
        int[][] collisionData = PathFinding.getCollisionData();
        int localX = localPosition.getX(), localY = localPosition.getY();
        if(localX > collisionData.length || localY > collisionData[localX].length)
            return true;
        return AStarNode.isWalkable(collisionData[localX][localY]);
    }

    public boolean canReach(RSTile position) {
//        System.out.println("Checking if we can reach position: " + position);
        position = position.toWorldTile();
//        System.out.println("Updated position to world tile: " + position);
        Tile playerPosition = Players.local().tile();
        if (playerPosition.getX() == position.getX() && playerPosition.getY() == position.getY()) {
            return true;
        }
        Tile localTile = position.toLocalTile();
//        System.out.println("Updated position to local tile: " + localTile);
        return getParent(localTile) != null;
    }

    public boolean canReach(int x, int y) {
        Tile playerPosition = Players.local().tile();
        if (playerPosition.getX() == x && playerPosition.getY() == y) {
            return true;
        }
        Tile position = convertToLocal(x, y);
        return getParent(position) != null;
    }

    public Tile closestTile(Collection<RSTile> tiles) {
        Tile closest = null;
        double closestDistance = Integer.MAX_VALUE;
        Tile playerPosition = Players.local().tile();
        for (RSTile positionable : tiles) {
            double distance = playerPosition.distanceToDouble(positionable);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = positionable;
            }
        }
        return closest;
    }

    /**
     * @param x
     * @param y
     * @return parent tile of x and y through BFS.
     */
    public Tile getParent(int x, int y) {
        Tile position = convertToLocal(x, y);
        return getParent(position);
    }

    public Tile getParent(Positionable positionable) {
        Tile tile = positionable.getPosition();
        if (tile.getType() != Tile.TYPES.LOCAL) {
            tile = tile.toLocalTile();
        }
//        System.out.println("Checking local tile: " + tile);
        int x = tile.getX(), y = tile.getY();
        if (x < 0 || y < 0) {
            System.out.println("Tile x or y is below 0");
            return null;
        }
        if (x >= 104 || y >= 104 || x >= map.length || y >= map[x].length){
            System.out.println("Tile x or y is above 104, or the map length");
            return null;
        }
        return map[x][y];
    }

    /**
     * @param x
     * @param y
     * @return Distance to tile. Max integer value if unreachable. Does not account for positionable behind doors
     */
    public int getDistance(int x, int y) {
        Tile position = convertToLocal(x, y);
        return getDistance(position);
    }

    /**
     * @param positionable
     * @return path to tile. Does not account for positionable behind doors
     */
    public ArrayList<RSTile> getPath(Positionable positionable) {
        Tile position = convertToLocal(positionable.getPosition().getX(), positionable.getPosition().getY());
        int x = position.getX(), y = position.getY();
        return getPath(x, y);
    }

    /**
     * @param x
     * @param y
     * @return null if no path.
     */
    public ArrayList<RSTile> getPath(int x, int y) {
        ArrayList<RSTile> path = new ArrayList<>();
        Tile playerPos = Players.local().tile().toLocalTile();
        if (x == playerPos.getX() && y == playerPos.getY()) {
            return path;
        }
        if (x < 0 || y < 0) {
            return null;
        }
        if (x >= 104 || y >= 104) {
            return null;
        }
        if (map[x][y] == null) {
            return null;
        }
        Tile tile = new Tile(x, y, Players.local().tile().getPlane(), Tile.TYPES.LOCAL);
        while ((tile = map[tile.getX()][tile.getY()]) != null) {
            path.add(tile.toWorldTile());
        }
        Collections.reverse(path);
        return path;
    }

    public int getDistance(Positionable positionable) {
        Tile position = convertToLocal(positionable.getPosition().getX(), positionable.getPosition().getY());
        int x = position.getX(), y = position.getY();
        Tile playerPos = Players.local().tile().toLocalTile();
        if (x == playerPos.getX() && y == playerPos.getY()) {
            return 0;
        }
        if (x < 0 || y < 0) {
            return Integer.MAX_VALUE;
        }
        if (x >= 104 || y >= 104) {
            return Integer.MAX_VALUE;
        }
        if (map[x][y] == null) {
            return Integer.MAX_VALUE;
        }
        int length = 0;
        Tile tile = position;
        while ((tile = map[tile.getX()][tile.getY()]) != null) {
            length++;
        }
        return length;
    }

    private static Tile convertToLocal(int x, int y) {
        Tile position = new Tile(x, y, Players.local().tile().getPlane(), x >= 104 || y >= 104 ? Tile.TYPES.WORLD : Tile.TYPES.LOCAL);
        if (position.getType() != Tile.TYPES.LOCAL) {
            position = position.toLocalTile();
        }
        return position;
    }

    public static Tile getBestWalkableTile(Positionable positionable, Reachable reachable) {
        Tile localPosition = positionable.getPosition().toLocalTile();
        HashSet<RSTile> building = BankHelper.getBuilding(positionable);
        boolean[][] traversed = new boolean[104][104];
        Tile[][] parentMap = new Tile[104][104];
        Queue<RSTile> queue = new LinkedList<>();
        int[][] collisionData = PathFinding.getCollisionData();
        if(collisionData == null)
            return null;

        queue.add(localPosition);
        try {
            traversed[localPosition.getX()][localPosition.getY()] = true;
            parentMap[localPosition.getX()][localPosition.getY()] = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        while (!queue.isEmpty()) {
            Tile currentLocal = queue.poll();
            int x = currentLocal.getX(), y = currentLocal.getY();

            int currentCollisionFlags = collisionData[x][y];
            if (AStarNode.isWalkable(currentCollisionFlags)) {
                if (reachable != null && !reachable.canReach(currentLocal.toWorldTile().getX(), currentLocal.toWorldTile().getY())) {
                    continue;
                }
                if (building != null && building.size() > 0) {
                    if (building.contains(currentLocal.toWorldTile())) {
                        return currentLocal.toWorldTile();
                    }
                    continue; //Next tile because we are now outside of building.
                } else {
                    return currentLocal.toWorldTile();
                }
            }

            for (Direction direction : Direction.values()) {
                if (!direction.isValidDirection(x, y, collisionData)) {
                    continue; //Cannot traverse to tile from current.
                }

                Tile neighbor = direction.getPointingTile(currentLocal);
                int destinationX = neighbor.getX(), destinationY = neighbor.getY();
                if (traversed[destinationX][destinationY]) {
                    continue; //Traversed already
                }
                traversed[destinationX][destinationY] = true;
                parentMap[destinationX][destinationY] = currentLocal;
                queue.add(neighbor);
            }

        }
        return null;
    }

    /**
     * @return gets collision map.
     */
    public static Reachable getMap() {
        return new Reachable(Players.local().tile());
    }

    public static Reachable getMap(RSTile homeTile) {
        return new Reachable(homeTile);
    }

    /**
     * @return local reachable tiles
     */
    public static Tile[][] generateMap(RSTile homeTile) {
        Tile localPlayerPosition = homeTile.toLocalTile();
        boolean[][] traversed = new boolean[104][104];
        Tile[][] parentMap = new Tile[104][104];
        Queue<RSTile> queue = new LinkedList<>();
        int[][] collisionData = PathFinding.getCollisionData();

        if(collisionData == null)
            return new Tile[][]{};

        queue.add(localPlayerPosition);
        try {
            traversed[localPlayerPosition.getX()][localPlayerPosition.getY()] = true;
            parentMap[localPlayerPosition.getX()][localPlayerPosition.getY()] = null;
        } catch (Exception e) {
            return parentMap;
        }

        while (!queue.isEmpty()) {
            Tile currentLocal = queue.poll();
            int x = currentLocal.getX(), y = currentLocal.getY();

            int currentCollisionFlags = collisionData[x][y];
            if (!AStarNode.isWalkable(currentCollisionFlags)) {
                continue;
            }

            for (Direction direction : Direction.values()) {
                if (!direction.isValidDirection(x, y, collisionData)) {
                    continue; //Cannot traverse to tile from current.
                }

                Tile neighbor = direction.getPointingTile(currentLocal);
                int destinationX = neighbor.getX(), destinationY = neighbor.getY();
                if (traversed[destinationX][destinationY]) {
                    continue; //Traversed already
                }
                traversed[destinationX][destinationY] = true;
                parentMap[destinationX][destinationY] = currentLocal;
                queue.add(neighbor);
            }

        }
        return parentMap;
    }

    public enum Direction {
        EAST(1, 0),
        NORTH(0, 1),
        WEST(-1, 0),
        SOUTH(0, -1),
        NORTH_EAST(1, 1),
        NORTH_WEST(-1, 1),
        SOUTH_EAST(1, -1),
        SOUTH_WEST(-1, -1),
        ;

        int x, y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Tile getPointingTile(RSTile tile) {
            return tile.translate(x, y);
        }

        public boolean isValidDirection(int x, int y, int[][] collisionData) {
            try {
                switch (this) {
                    case NORTH:
                        return !AStarNode.blockedNorth(collisionData[x][y]);
                    case EAST:
                        return !AStarNode.blockedEast(collisionData[x][y]);
                    case SOUTH:
                        return !AStarNode.blockedSouth(collisionData[x][y]);
                    case WEST:
                        return !AStarNode.blockedWest(collisionData[x][y]);
                    case NORTH_EAST:
                        if (AStarNode.blockedNorth(collisionData[x][y]) || AStarNode.blockedEast(collisionData[x][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x + 1][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x][y + 1])) {
                            return false;
                        }
                        if (AStarNode.blockedNorth(collisionData[x + 1][y])) {
                            return false;
                        }
                        if (AStarNode.blockedEast(collisionData[x][y + 1])) {
                            return false;
                        }
                        return true;
                    case NORTH_WEST:
                        if (AStarNode.blockedNorth(collisionData[x][y]) || AStarNode.blockedWest(collisionData[x][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x - 1][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x][y + 1])) {
                            return false;
                        }
                        if (AStarNode.blockedNorth(collisionData[x - 1][y])) {
                            return false;
                        }
                        if (AStarNode.blockedWest(collisionData[x][y + 1])) {
                            return false;
                        }
                        return true;
                    case SOUTH_EAST:
                        if (AStarNode.blockedSouth(collisionData[x][y]) || AStarNode.blockedEast(collisionData[x][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x + 1][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x][y - 1])) {
                            return false;
                        }
                        if (AStarNode.blockedSouth(collisionData[x + 1][y])) {
                            return false;
                        }
                        if (AStarNode.blockedEast(collisionData[x][y - 1])) {
                            return false;
                        }
                        return true;
                    case SOUTH_WEST:
                        if (AStarNode.blockedSouth(collisionData[x][y]) || AStarNode.blockedWest(collisionData[x][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x - 1][y])) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData[x][y - 1])) {
                            return false;
                        }
                        if (AStarNode.blockedSouth(collisionData[x - 1][y])) {
                            return false;
                        }
                        if (AStarNode.blockedWest(collisionData[x][y - 1])) {
                            return false;
                        }
                        return true;
                    default:
                        return false;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return false;
            }
        }
    }

}
