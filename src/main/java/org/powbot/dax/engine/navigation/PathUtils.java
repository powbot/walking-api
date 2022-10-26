package org.powbot.dax.engine.navigation;

import org.powbot.api.Tile;
import org.powbot.api.rt4.Players;
import org.powbot.dax.shared.helpers.Projection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

;

public class PathUtils {

    public static Tile getNextTileInPath(Tile current, List<Tile> path) {
        int index = path.indexOf(current);

        if (index == -1) {
            return null;
        }

        int next = index + 1;
        return next < path.size() ? path.get(next) : null;
    }

    public static Tile getClosestTileInPath(List<Tile> path) {
        Tile player = Players.local().tile();
        return path.stream().min(Comparator.comparingDouble(o -> o.distanceTo(player))).orElse(null);
    }

    public static Tile getFurthestReachableTileInMinimap(List<Tile> path) {
        List<Tile> reversed = new ArrayList<>(path);
        Collections.reverse(reversed);

        DaxPathFinder.Destination[][] map = DaxPathFinder.getMap();
        for (Tile tile : reversed) {
            if(tile.isRendered())
                return tile;
            if (Projection.isInMinimap(tile) && DaxPathFinder.canReach(map, tile)) {
                return tile;
            }
        }
        return null;
    }

    public static Tile getFurthestReachableTileOnScreen(List<Tile> path) {
        List<Tile> reversed = new ArrayList<>(path);
        Collections.reverse(reversed);

        DaxPathFinder.Destination[][] map = DaxPathFinder.getMap();
        for (Tile tile : reversed) {
            if (DaxPathFinder.canReach(map, tile) && tile.isRendered() && Projection.isInMinimap(tile)) {
                return tile;
            }
        }
        return null;
    }

//    public static void drawDebug(Rendering Rendering, List<Tile> path) {
//        Rendering2D g = (Rendering2D) Rendering;
//        Tile player = Players.local().tile();
//
//        g.setColor(new Color(0, 191, 23, 80));
//        for (Tile tile : path) {
//            if (tile.distanceTo(player) > 25) {
//                continue;
//            }
//            Polygon polygon = Projection.getTileBoundsPoly(tile, 0);
//            if (polygon == null) {
//                continue;
//            }
////            g.fillPolygon(polygon);
//        }
//
//        Tile closest = getClosestTileInPath(path);
//        if (closest != null) {
//            Polygon polygon = Projection.getTileBoundsPoly(closest, 0);
//            if (polygon != null) {
//                g.setColor(new Color(205, 0, 255, 80));
////                g.fillPolygon(polygon);
//
//                g.setColor(Color.BLACK);
//                Rendering.drawString("Closest In Path", polygon.getXpoints()[0] - 24, polygon.getYpoints()[1] + 1);
//                g.setColor(Color.WHITE);
//                Rendering.drawString("Closest In Path", polygon.getXpoints()[0] - 25, polygon.getYpoints()[1]);
//            }
//        }
//
//        Tile furthestScreenTile = getFurthestReachableTileOnScreen(path);
//        if (furthestScreenTile != null) {
//            Polygon polygon = Projection.getTileBoundsPoly(furthestScreenTile, 0);
//            if (polygon != null) {
//                g.setColor(new Color(255, 0, 11, 157));
////                g.fillPolygon(polygon);
//
//                g.setColor(Color.BLACK);
//                Rendering.drawString("Furthest Screen Tile", polygon.getXpoints()[0] - 24, polygon.getYpoints()[1] + 30);
//                g.setColor(Color.WHITE);
//                Rendering.drawString("Furthest Screen Tile", polygon.getXpoints()[0] - 25, polygon.getYpoints()[1] + 30);
//            }
//        }
//
//        Tile furthestMapTile = getFurthestReachableTileInMinimap(path);
//        if (furthestMapTile != null) {
//            Point p = Projection.tileToMinimap(furthestMapTile);
//            if (p != null) {
//                g.setColor(new Color(255, 0, 11, 157));
//                g.fillRect(p.getX() - 3, p.getY() - 3, 6, 6);
//
//                g.setColor(Color.BLACK);
//                Rendering.drawString("Furthest Map Tile", p.getX() + 1, p.getY() + 14);
//                g.setColor(Color.WHITE);
//                Rendering.drawString("Furthest Map Tile", p.getX(), p.getY() + 15);
//            }
//        }
//
//        Tile nextTile = getNextTileInPath(furthestMapTile, path);
//        if (nextTile != null) {
//            Polygon polygon = Projection.getTileBoundsPoly(nextTile, 0);
//            if (polygon != null) {
//                g.setColor(new Color(255, 242, 0, 157));
////                g.fillPolygon(polygon);
//
//                g.setColor(Color.BLACK);
//                Rendering.drawString("Next Tile", polygon.getXpoints()[0] - 24, polygon.getYpoints()[1]);
//                g.setColor(Color.WHITE);
//                Rendering.drawString("Next Tile", polygon.getXpoints()[0] - 25, polygon.getYpoints()[1]);
//            }
//        }
//
//
//    }

    public static class NotInPathException extends RuntimeException {
        public NotInPathException() {
        }
    }

}
