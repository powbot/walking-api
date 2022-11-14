package org.powbot.dax.shared.helpers;

import org.powbot.api.Point;
import org.powbot.api.Rectangle;
import org.powbot.api.rt4.*;
import org.powbot.dax.teleports.utils.ItemFilters;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ItemHelper {

    public static boolean click(String itemNameRegex, String itemAction){
        return click(item -> getItemName(item).matches(itemNameRegex) && Arrays.stream(getItemActions(item)).anyMatch(s -> s.equals(itemAction)), itemAction);
    }

    public static boolean clickMatch(Item item, String regex){
        return item.click(rsMenuNode -> {
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
            return closest != null && closest.click(action);
        }
        boolean value = false;
        while (!list.isEmpty()){
            Item item = getClosestToMouse(list);
            if (item != null) {
                list.remove(item);
                if (item.click(action)){
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
        return action != null ? item.click(action) : item.click();
    }

    public static boolean use(int itemID){
        String name = Inventory.selectedItem().name();
        CacheItemConfig rsItemDefinition = CacheItemConfig.load(itemID);
        String itemName;
        if (Inventory.selectionType() == 1 && (itemName = rsItemDefinition.getName()).length() > 0 && name.equals(itemName)){
            return true;
        } else if (Inventory.selectionType() == 1){
            Inventory.selectedItem().click();
        }
        return ItemHelper.click(itemID, "Use");
    }

    public static Item getClosestToMouse(List<Item> rsItems){
//        Point mouse = Mouse.getPos();
//        rsItems.sort(Comparator.comparingInt(o -> (int) getCenter(o.getArea()).distance(mouse)));
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
        CacheItemConfig definition = CacheItemConfig.load(id);
        return definition.getNoted();
    }


    public static String[] getItemActions(Item rsItem){
        return rsItem.actions().toArray(new String[0]);
    }


    public static String getItemName(Item rsItem){
        return rsItem.name();
    }


    private static String[] getItemActions(CacheItemConfig rsItemDefinition){
        if (rsItemDefinition == null){
            return new String[0];
        }
        String[] actions = rsItemDefinition.getActions();
        return actions != null ? actions : new String[0];
    }

    private static String getItemName(CacheItemConfig definition){
        String name = definition.getName();
        return name != null ? name : "null";
    }


}
