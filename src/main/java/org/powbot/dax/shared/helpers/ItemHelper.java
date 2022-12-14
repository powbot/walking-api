package org.powbot.dax.shared.helpers;

import org.powbot.api.Point;
import org.powbot.api.Rectangle;
import org.powbot.api.rt4.*;
import org.powbot.dax.teleports.utils.ItemFilters;
import org.powbot.mobile.rlib.generated.RItemDefinition;
import org.powbot.mobile.rscache.loader.ItemLoader;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ItemHelper {

    public static boolean click(String itemNameRegex, String itemAction){
        return click(item -> getItemName(item).matches(itemNameRegex) && Arrays.stream(getItemActions(item)).anyMatch(s -> s.equals(itemAction)), itemAction);
    }

    public static boolean clickMatch(Item item, String regex){
        Game.Tab tab = item.getType() == ItemType.INVENTORY ? Game.Tab.INVENTORY : Game.Tab.EQUIPMENT;
        if(Game.tab() != tab && !Game.tab(tab)){
            return false;
        }
        return item.interact(rsMenuNode -> {
            String action = rsMenuNode.getAction();
            return action.matches(regex);
        });
    }

    public static boolean click(int itemID){
        return click(itemID, null);
    }

    public static boolean click(int itemID, String action){
        return click(ItemFilters.idEquals(itemID), action, true);
    }

    public static boolean click(Predicate<Item> filter, String action){
        return click(filter, action, true);
    }

    /**
     *
     * @param filter filter for items
     * @param action action to click
     * @param one click only one item.
     * @return
     */
    public static boolean click(Predicate<Item> filter, String action, boolean one){
        if (action == null){
            action = "";
        }
        if(Game.tab() != Game.Tab.INVENTORY && !Game.tab(Game.Tab.INVENTORY)){
            return false;
        }
        List<Item> list = Inventory.get(filter);
        if (one) {
            Item closest = getClosestToMouse(list);
            return closest != null && closest.interact(action);
        }
        boolean value = false;
        while (!list.isEmpty()){
            Item item = getClosestToMouse(list);
            if (item != null) {
                list.remove(item);
                if (item.interact(action)){
                    value = true;
                }
            }
        }
        return value;
    }

    public static boolean click(Item item, String action){
        if (Bank.opened()){
            Bank.close();
        }
        if(Game.tab() != Game.Tab.INVENTORY && !Game.tab(Game.Tab.INVENTORY)){
            return false;
        }
        return action != null ? item.interact(action) : item.click();
    }

    public static boolean use(int itemID){
        String name = Inventory.selectedItem().name();
        RItemDefinition config = ItemLoader.lookup(itemID);
        if(config == null)
            return false;
        String itemName;
        if(Game.tab() != Game.Tab.INVENTORY && !Game.tab(Game.Tab.INVENTORY)){
            return false;
        }
        if (Inventory.selectionType() == 1 && (itemName = config.name()).length() > 0 && name.equals(itemName)){
            return true;
        } else if (Inventory.selectionType() == 1){
            Inventory.selectedItem().click();
        }
        return ItemHelper.click(itemID, "Use");
    }

    public static Item getClosestToMouse(List<Item> rsItems){
        return rsItems.size() > 0 ? rsItems.get(0) : null;
    }

    private static Point getCenter(Rectangle rectangle){
        return new Point(rectangle.getX() + rectangle.getWidth()/2, rectangle.getY() + rectangle.getHeight()/2);
    }


    public static Item getItem(Predicate<Item> filter){
        return getClosestToMouse(Inventory.get(filter));
    }

    public static boolean isNoted(Item item) {
        return item != null && isNoted(item.id());
    }

    public static boolean isNoted(int id) {
        RItemDefinition definition = ItemLoader.lookup(id);
        return definition.noted();
    }


    public static String[] getItemActions(Item rsItem){
        return rsItem.actions().toArray(new String[0]);
    }


    public static String getItemName(Item rsItem){
        return rsItem.name();
    }


    private static String[] getItemActions(RItemDefinition rsItemDefinition){
        if (rsItemDefinition == null){
            return new String[0];
        }
        String[] actions = rsItemDefinition.actions();
        return actions != null ? actions : new String[0];
    }

    private static String getItemName(RItemDefinition definition){
        String name = definition.name();
        return name != null ? name : "null";
    }


}
