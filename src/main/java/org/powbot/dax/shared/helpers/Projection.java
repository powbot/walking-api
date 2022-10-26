package org.powbot.dax.shared.helpers;

import org.powbot.api.Locatable;
import org.powbot.api.Point;
import org.powbot.api.rt4.Game;

public class Projection {

    public static boolean isInMinimap(Locatable tile){
        return tile.tile().matrix().onMap();
    }

    public static Point tileToMinimap(Locatable tile){
        Point p = Game.tileToMap(tile.tile());
        return new Point(p.getX(), p.getY());
    }
}
