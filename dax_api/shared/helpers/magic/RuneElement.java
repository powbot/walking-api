package dax_api.shared.helpers.magic;

import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

import java.util.Arrays;

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
        RSItem[] items = Inventory.find(rsItem -> {
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
        });
        return Arrays.stream(items).mapToInt(RSItem::getStack).sum() + RunePouch.getQuantity(this);
    }

    public int getCount(RSItem[] inventory, RSItem[] equipment) {
        if (haveStaff(equipment)) {
            return Integer.MAX_VALUE;
        }
        return Arrays.stream(inventory).filter(rsItem -> {
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
        }).mapToInt(RSItem::getStack).sum() + RunePouch.getQuantity(this);
    }

    private boolean haveStaff(RSItem[] equipment) {
        return Arrays.stream(equipment).anyMatch(rsItem -> {
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
        return haveStaff(Equipment.getItems());
    }

    /**
     * @param item
     * @return item name. Never null. "null" if no name.
     */
    private static String getItemName(RSItem item) {
        RSItemDefinition definition = item.getDefinition();
        String name;
        return definition == null || (name = definition.getName()) == null ? "null" : name;
    }


}
