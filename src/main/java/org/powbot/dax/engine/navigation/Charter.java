package org.powbot.dax.engine.navigation;

import org.powbot.api.Area;
import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Component;
import org.powbot.api.rt4.Components;
import org.powbot.api.rt4.Npcs;
import org.powbot.api.rt4.Widgets;
import org.powbot.dax.engine.Loggable;
import org.powbot.dax.engine.interaction.NPCInteraction;
import org.powbot.dax.shared.helpers.AreaHelper;

import java.util.HashMap;
import java.util.Map;

public class Charter implements Loggable {

    private static final int CHARTER_INTERFACE_MASTER = 72;
    private static Charter instance;

    private static Charter getInstance() {
        return instance != null ? instance : (instance = new Charter());
    }

    public static boolean to(LocationProperty locationProperty) {
        if (locationProperty == null) {
            return false;
        }
        if (!openCharterMenu()) {
            getInstance().log("Failed to open charter menu.");
            return false;
        }
        Map<LocationProperty, Location> charterLocations = getCharterLocations();
        Location location = charterLocations.get(locationProperty);
        if (location == null) {
            getInstance().log("Location: " + locationProperty + " is not available. " + charterLocations.keySet());
            return false;
        }
        if (!location.click()) {
            getInstance().log("Failed to click charter location.");
            return false;
        }
        if (!NPCInteraction.waitForConversationWindow()) {
            getInstance().log("Confirmation dialogue did not appear.");
        }
        NPCInteraction.handleConversation("Ok", "Okay");
        return Condition.wait(ShipUtils::isOnShip, 1000, 10);
    }

    private static boolean openCharterMenu() {
        return Widgets.component(CHARTER_INTERFACE_MASTER, 0).visible() ||
                (Npcs.stream().action("Charter").nearest().first().interact("Charter") &&
                        Condition.wait(() -> Widgets.component(CHARTER_INTERFACE_MASTER, 0).visible(), 300, 20));
    }

    private static Map<LocationProperty, Location> getCharterLocations() {
        Map<LocationProperty, Location> locations = new HashMap<>();
        for (Component c : Components.stream(CHARTER_INTERFACE_MASTER).filtered(c -> c.text().length() > 0 && c.visible())) {
            locations.put(LocationProperty.stringToLocation(c.text()), new Location(c));
        }

        return locations;
    }

    @Override
    public String getName() {
        return "Charter";
    }

    public enum LocationProperty {
        BRIMHAVEN ("Brimhaven", new Tile(2760, 3237, 0)),
        CATHERBY ("Catherby", new Tile(2796, 3414, 0)),
        CORSAIR_COVE ("Corsair Cove", new Tile(2587, 2851, 0)),
        CIVITAS_ILLA_FORTIS("Civitas illa Fortis", new Tile(1746, 3136, 0)),
        MUSA_POINT("Musa Point", new Tile(2954, 3158, 0)),
        MOS_LE_HARMLESS ("Mos Le'Harmless", new Tile(3671, 2931, 0)),
        PORT_KHAZARD ("Port Khazard", new Tile(2674, 3144, 0)),
        PORT_PHASMATYS ("Port Phasmatys", new Tile(3702, 3503, 0)),
        PORT_SARIM ("Port Sarim", new Tile(3038, 3192, 0)),
        PORT_TYRAS ("Port Tyras", new Tile(2142, 3122, 0)),
        PRIFDDINAS ("Prifddinas", new Tile(2159, 3329, 0)),
        SHIPYARD ("Shipyard", new Tile(3001, 3032, 0)),
        SUNSET_COAST("Sunset Coast", new Tile(1514, 2871, 0)),

        ;

        private String name;
        private Area area;

        LocationProperty(String name, Tile center) {
            this.name = name;
            if (center != null) {
                this.area = AreaHelper.fromCenter(center, 15);
            }
        }

        public boolean valid(Tile tile) {
            return area != null && tile != null && area.contains(tile);
        }

        public static LocationProperty stringToLocation(String name) {
            for (LocationProperty locationProperty : values()) {
                if (name.equals(locationProperty.name)) {
                    return locationProperty;
                }
            }
            return null;
        }

        public static LocationProperty getLocation(Tile tile) {
            for (LocationProperty locationProperty : values()) {
                if (locationProperty.valid(tile)) {
                    return locationProperty;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    public static class Location {

        private String name;
        private Component component;

        private Location(Component component) {
            this.name = component.text();
            this.component = component;
        }

        public String getName() {
            return name;
        }

        public Component getComponent() {
            return component;
        }

        public boolean click(String option) {
            return component.click(option);
        }

        public boolean click() {
            return component.click();
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
