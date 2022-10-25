package org.powbot.dax.shared.helpers;

import org.powbot.api.Locatable;

public class Projection {

    public static boolean isInMinimap(Locatable tile){
        return tile.tile().matrix().onMap();
    }
}
