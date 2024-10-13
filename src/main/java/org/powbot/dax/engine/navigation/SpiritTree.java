package org.powbot.dax.engine.navigation;

import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Component;
import org.powbot.api.rt4.Components;
import org.powbot.api.rt4.Players;
import org.powbot.api.rt4.Widgets;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.interaction.InteractionHelper;
import org.powbot.dax.shared.helpers.Filters;

/**
 * Created by Me on 3/13/2017.
 */
public class SpiritTree {

    private static final int SPIRIT_TREE_MASTER_INTERFACE = 187;

    public enum Location {
        SPIRIT_TREE_GRAND_EXCHANGE("Grand Exchange", 3183, 3508, 0),
        SPIRIT_TREE_STRONGHOLD("Gnome Stronghold", 2461, 3444, 0),
        SPIRIT_TREE_KHAZARD("Battlefield of Khazard", 2555, 3259, 0),
        SPIRIT_TREE_VILLAGE("Tree Gnome Village", 2542, 3170, 0),
        SPIRIT_TREE_FELDIP("Feldip Hills", 2488, 2850, 0),
        SPIRIT_TREE_POISON_WASTE("Poison Waste", 2339, 3109, 0),
        SPIRIT_TREE_PRIFDDINAS("Prifddinas", 3274, 6123, 0);

        private int x, y, z;
        private String name;
        Location(String name, int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public Tile getTile(){
            return new Tile(x, y, z);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }

    public static boolean to(Location location){
        if (!Widgets.component(SPIRIT_TREE_MASTER_INTERFACE, 0).visible()
                && !InteractionHelper.click(InteractionHelper.getGameObject(Filters.Objects.actionsContains("Travel")), "Travel", () -> Components.stream(SPIRIT_TREE_MASTER_INTERFACE).anyMatch(Component::valid) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
            return false;
        }

        Component option = Components.stream(SPIRIT_TREE_MASTER_INTERFACE).text(location.getName()).findAny().orElse(null);

        if (option == null){
            return false;
        }

        if (!option.click()){
            return false;
        }

        if (WaitFor.condition(Random.nextInt(5400, 6500), () -> location.getTile().distanceTo(Players.local().tile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.milliseconds(250, 500);
            return true;
        }
        return false;
    }

}
