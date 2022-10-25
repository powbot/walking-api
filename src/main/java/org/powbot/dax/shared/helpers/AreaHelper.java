package org.powbot.dax.shared.helpers;

import org.powbot.api.Area;
import org.powbot.api.Tile;

import java.util.ArrayList;
import java.util.List;

public class AreaHelper {

    public static Area fromCenter(Tile center, int radius) {
        List<Tile> tiles = new ArrayList<>();
        for (int x = center.x() - radius; x <= center.x() + radius; x++) {
            for (int y = center.y() - radius; y <= center.y() + radius; y++) {
                tiles.add(new Tile(x, y, center.floor()));
            }
        }

        return new Area(tiles.toArray(new Tile[0]));
    }
}
