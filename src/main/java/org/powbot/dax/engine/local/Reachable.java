package org.powbot.dax.engine.local;


import org.powbot.api.Locatable;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.Movement;
import org.powbot.api.rt4.Players;
import org.powbot.dax.shared.helpers.BankHelper;
import org.powbot.util.TransientGetter2D;

import java.util.*;

public class Reachable {

    private Tile[][] map;

    /**
     * Generates reachable map from player position
     */
    public Reachable() {
        this(null);
    }

    public Reachable(Tile homeTile) {
        map = generateMap(homeTile != null ? homeTile : Players.local().tile());
    }

    public boolean isBlocked(Locatable position) {
        TransientGetter2D<Integer> collisionData = Movement.collisionMap(position.tile().floor()).flags();
        int localX = position.tile().localX(), localY = position.tile().localY();
        if (localX > collisionData.getSize() || localY > collisionData.get(localX).getSize())
            return true;
        return AStarNode.isWalkable(collisionData.get(localX).get(localY));
    }

    public boolean canReach(Tile position) {
//        System.out.println("Checking if we can reach position: " + position);
//        System.out.println("Updated position to world tile: " + position);
        Tile playerPosition = Players.local().tile();
        if (playerPosition.getX() == position.getX() && playerPosition.getY() == position.getY()) {
            return true;
        }
//        System.out.println("Updated position to local tile: " + localTile);
        return getParent(position) != null;
    }

    public boolean canReach(int x, int y) {
        Tile playerPosition = Players.local().tile();
        if (playerPosition.getX() == x && playerPosition.getY() == y) {
            return true;
        }
        return getParent(new Tile(x, y)) != null;
    }

    public Tile closestTile(Collection<Tile> tiles) {
        Tile closest = null;
        double closestDistance = Integer.MAX_VALUE;
        Tile playerPosition = Players.local().tile();
        for (Tile positionable : tiles) {
            double distance = playerPosition.distanceTo(positionable);
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
        return getParent(new Tile(x, y));
    }

    public Tile getParent(Locatable positionable) {
        Tile tile = positionable.tile();
//        System.out.println("Checking local tile: " + tile);
        int x = tile.localX(), y = tile.localY();
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
        return getDistance(new Tile(x, y));
    }

    /**
     * @param positionable
     * @return path to tile. Does not account for positionable behind doors
     */
    public ArrayList<Tile> getPath(Locatable positionable) {
        int x = positionable.tile().localX(), y = positionable.tile().localY();
        return getPath(x, y);
    }

    /**
     * @param x
     * @param y
     * @return null if no path.
     */
    public ArrayList<Tile> getPath(int x, int y) {
        ArrayList<Tile> path = new ArrayList<>();
        Tile mapOffset = Game.mapOffset();
        Tile playerPos = Players.local().tile();
        if (x == playerPos.localX() && y == playerPos.localY()) {
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
        Tile tile = new Tile(mapOffset.x() + x, mapOffset.y() + y, Players.local().tile().floor());
        while ((tile = map[tile.localX()][tile.localY()]) != null) {
            path.add(tile);
        }
        Collections.reverse(path);
        return path;
    }

    public int getDistance(Locatable positionable) {
        int x = positionable.tile().localX(), y = positionable.tile().localY();
        Tile playerPos = Players.local().tile();
        if (x == playerPos.localX() && y == playerPos.localY()) {
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
        Tile tile = positionable.tile();
        while ((tile = map[tile.localX()][tile.localY()]) != null) {
            length++;
        }
        return length;
    }

    public static Tile getBestWalkableTile(Locatable positionable, Reachable reachable) {
        Set<Tile> building = BankHelper.getBuilding(positionable);
        boolean[][] traversed = new boolean[104][104];
        Tile[][] parentMap = new Tile[104][104];
        Queue<Tile> queue = new LinkedList<>();
        TransientGetter2D<Integer> collisionData = Movement.collisionMap(Game.floor()).flags();
        
        queue.add(positionable.tile());
        try {
            traversed[positionable.tile().localX()][positionable.tile().localY()] = true;
            parentMap[positionable.tile().localX()][positionable.tile().localY()] = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

        while (!queue.isEmpty()) {
            Tile current = queue.poll();
            int x = current.localX(), y = current.localY();

            int currentCollisionFlags = collisionData.get(x).get(y);
            if (AStarNode.isWalkable(currentCollisionFlags)) {
                if (reachable != null && !reachable.canReach(current.getX(), current.getY())) {
                    continue;
                }
                if (building != null && building.size() > 0) {
                    if (building.contains(current)) {
                        return current;
                    }
                    continue; //Next tile because we are now outside of building.
                } else {
                    return current;
                }
            }

            for (Direction direction : Direction.values()) {
                if (!direction.isValidDirection(x, y, collisionData)) {
                    continue; //Cannot traverse to tile from current.
                }

                Tile neighbor = direction.getPointingTile(current);
                int destinationX = neighbor.localX(), destinationY = neighbor.localY();
                if(destinationX < 0 || destinationY < 0)
                    continue;
                if (traversed[destinationX][destinationY]) {
                    continue; //Traversed already
                }
                traversed[destinationX][destinationY] = true;
                parentMap[destinationX][destinationY] = current;
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

    public static Reachable getMap(Tile homeTile) {
        return new Reachable(homeTile);
    }

    /**
     * @return local reachable tiles
     */
    public static Tile[][] generateMap(Tile homeTile) {
        boolean[][] traversed = new boolean[104][104];
        Tile[][] parentMap = new Tile[104][104];
        Queue<Tile> queue = new LinkedList<>();
        TransientGetter2D<Integer> collisionData = Movement.collisionMap(Game.floor()).flags();

        if(collisionData == null)
            return new Tile[][]{};

        queue.add(homeTile);
        try {
            traversed[homeTile.localX()][homeTile.localY()] = true;
            parentMap[homeTile.localX()][homeTile.localY()] = null;
        } catch (Exception e) {
            return parentMap;
        }

        while (!queue.isEmpty()) {
            Tile currentLocal = queue.poll();
            int x = currentLocal.localX(), y = currentLocal.localY();

            int currentCollisionFlags = collisionData.get(x).get(y);
            if (!AStarNode.isWalkable(currentCollisionFlags)) {
                continue;
            }

            for (Direction direction : Direction.values()) {
                if (!direction.isValidDirection(x, y, collisionData)) {
                    continue; //Cannot traverse to tile from current.
                }

                Tile neighbor = direction.getPointingTile(currentLocal);
                int destinationX = neighbor.localX(), destinationY = neighbor.localY();
                if(destinationX < 0 || destinationY < 0 || destinationX > 103 || destinationY > 103)
                    continue;
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

        public Tile getPointingTile(Tile tile) {
            return tile.derive(x, y);
        }

        public boolean isValidDirection(int x, int y, TransientGetter2D<Integer> collisionData) {
            try {
                switch (this) {
                    case NORTH:
                        return !AStarNode.blockedNorth(collisionData.get(x).get(y));
                    case EAST:
                        return !AStarNode.blockedEast(collisionData.get(x).get(y));
                    case SOUTH:
                        return !AStarNode.blockedSouth(collisionData.get(x).get(y));
                    case WEST:
                        return !AStarNode.blockedWest(collisionData.get(x).get(y));
                    case NORTH_EAST:
                        if (AStarNode.blockedNorth(collisionData.get(x).get(y)) || AStarNode.blockedEast(collisionData.get(x).get(y))) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData.get(x + 1).get(y))) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData.get(x).get(y + 1))) {
                            return false;
                        }
                        if (AStarNode.blockedNorth(collisionData.get(x + 1).get(y))) {
                            return false;
                        }
                        if (AStarNode.blockedEast(collisionData.get(x).get(y + 1))) {
                            return false;
                        }
                        return true;
                    case NORTH_WEST:
                        if (AStarNode.blockedNorth(collisionData.get(x).get(y)) || AStarNode.blockedWest(collisionData.get(x).get(y))) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData.get(x - 1).get(y))) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData.get(x).get(y + 1))) {
                            return false;
                        }
                        if (AStarNode.blockedNorth(collisionData.get(x - 1).get(y))) {
                            return false;
                        }
                        if (AStarNode.blockedWest(collisionData.get(x).get(y + 1))) {
                            return false;
                        }
                        return true;
                    case SOUTH_EAST:
                        if (AStarNode.blockedSouth(collisionData.get(x).get(y)) || AStarNode.blockedEast(collisionData.get(x).get(y))) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData.get(x + 1).get(y))) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData.get(x).get(y - 1))) {
                            return false;
                        }
                        if (AStarNode.blockedSouth(collisionData.get(x + 1).get(y))) {
                            return false;
                        }
                        if (AStarNode.blockedEast(collisionData.get(x).get(y - 1))) {
                            return false;
                        }
                        return true;
                    case SOUTH_WEST:
                        if (AStarNode.blockedSouth(collisionData.get(x).get(y)) || AStarNode.blockedWest(collisionData.get(x).get(y))) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData.get(x - 1).get(y))) {
                            return false;
                        }
                        if (!AStarNode.isWalkable(collisionData.get(x).get(y - 1))) {
                            return false;
                        }
                        if (AStarNode.blockedSouth(collisionData.get(x - 1).get(y))) {
                            return false;
                        }
                        if (AStarNode.blockedWest(collisionData.get(x).get(y - 1))) {
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
