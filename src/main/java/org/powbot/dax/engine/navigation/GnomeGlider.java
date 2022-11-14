package org.powbot.dax.engine.navigation;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;

import java.util.List;


public class GnomeGlider {

    private static final int GNOME_GLIDER_MASTER_INTERFACE = 138;

    public enum Location {
        TA_QUIR_PRIW ("Ta Quir Priw", 2465, 3501, 3),
        GANDIUS ("Gandius", 2970, 2972, 0),
        LEMANTO_ANDRA ("Lemanto Andra", 3321, 3430, 0),
        KAR_HEWO ("Kar-Hewo", 3284, 3211, 0),
        SINDARPOS ("Sindarpos", 2850, 3498, 0)
        ;

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
        public Tile getRSTile(){
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

    public static boolean to(Location location) {
        if (!Widgets.component(GNOME_GLIDER_MASTER_INTERFACE, 0).visible()
                && (!Npcs.stream().action("Glider").nearest().first().interact("Glider") ||
                !Condition.wait(() -> Widgets.component(GNOME_GLIDER_MASTER_INTERFACE, 0).visible(), 300, 10))) {
            return false;
        }

        Component option = Components.stream(GNOME_GLIDER_MASTER_INTERFACE).filter(rsInterface -> {
            List<String> actions = rsInterface.actions();
            return actions != null && actions.stream().anyMatch(s -> s.contains(location.getName()));
        }).findAny().orElse(null);

        if (!option.visible()){
            return false;
        }

        if (!option.click()){
            return false;
        }

        return Condition.wait(() -> location.getRSTile().distanceTo(Players.local().tile()) < 10, Random.nextInt(540, 650), 10);
    }

}
