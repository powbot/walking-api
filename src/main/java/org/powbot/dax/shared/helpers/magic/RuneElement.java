package org.powbot.dax.shared.helpers.magic;

import org.powbot.api.rt4.Equipment;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;

import java.util.List;

public enum RuneElement {

    AIR("Air", "Smoke", "Mist", "Dust"),
    EARTH("Earth", "Lava", "Mud", "Dust"),
    FIRE("Fire", "Lava", "Smoke", "Steam"),
    WATER("Water", "Mud", "Steam", "Mist"),
    LAW("Law"),
    NATURE("Nature"),
    SOUL("Soul");

    private String[] alternativeNames;

    RuneElement(String... alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public String[] getAlternativeNames() {
        return alternativeNames;
    }

    public int getCount() {
        if (haveStaff()) {
            return Integer.MAX_VALUE;
        }
        return Inventory.stream().filtered(rsItem -> {
            String name = getItemName(rsItem).toLowerCase();

            if (!name.contains("rune")) {
                return false;
            }

            for (String alternativeName : alternativeNames) {
                if (name.startsWith(alternativeName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }).list().stream().mapToInt(Item::getStack).sum() + RunePouch.getQuantity(this);
    }

    public int getCount(List<Item> inventory, List<Item> equipment) {
        if (haveStaff(equipment) || (this == FIRE && equipment.stream().anyMatch(i -> i.name().equals("Tome of fire")))) {
            return Integer.MAX_VALUE;
        }
        return inventory.stream().filter(rsItem -> {
            String name = getItemName(rsItem).toLowerCase();

            if (!name.contains("rune")) {
                return false;
            }

            for (String alternativeName : alternativeNames) {
                if (name.startsWith(alternativeName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }).mapToInt(Item::getStack).sum() + RunePouch.getQuantity(this);
    }

    private boolean haveStaff(List<Item> equipment) {
        return equipment.stream().anyMatch(rsItem -> {
            String name = getItemName(rsItem).toLowerCase();
            if (!name.contains("staff")) {
                return false;
            }
            for (String alternativeName : alternativeNames) {
                if (name.contains(alternativeName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });
    }

    private boolean haveStaff() {
        return haveStaff(Equipment.stream().list());
    }

    /**
     * @param item
     * @return item name. Never null. "null" if no name.
     */
    private static String getItemName(Item item) {
        return item.name().equals("") ? "null" : item.name();
    }


}
