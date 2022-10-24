package org.powbot.dax.teleports.utils;

import org.powbot.api.rt4.Combat;
import org.powbot.api.rt4.Varpbits;

public class TeleportConstants {


    public static final TeleportLimit
            LEVEL_20_WILDERNESS_LIMIT = () -> getWildernessLevel() <= 20,
            LEVEL_30_WILDERNESS_LIMIT = () -> getWildernessLevel() <= 30;

    public static final int
            GE_TELEPORT_VARBIT = 4585, SPELLBOOK_INTERFACE_MASTER = 218, SCROLL_INTERFACE_MASTER = 187;

    private static int getWildernessLevel() {
        return Combat.wildernessLevel();
    }

    public static boolean isVarrockTeleportAtGE(){
        return Varpbits.value(GE_TELEPORT_VARBIT) > 0;
    }

}
