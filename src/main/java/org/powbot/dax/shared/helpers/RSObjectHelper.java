package org.powbot.dax.shared.helpers;

import org.tribot.api2007.Objects;
import org.tribot.api2007.types.GameObject;
import org.tribot.api2007.types.CacheObjectConfig;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


public class GameObjectHelper {

    public static GameObject get(Predicate<GameObject> filter){
        GameObject[] objects = Objects.find(10, filter);
        return objects.length > 0 ? objects[0] : null;
    }

    public static boolean exists(Predicate<GameObject> filter){
        return Objects.find(10, filter).length > 0;
    }

    public static List<String> getActionsList(GameObject object){
        return Arrays.asList(getActions(object));
    }

    public static String[] getActions(GameObject object){
        String[] emptyActions = new String[0];
        CacheObjectConfig definition = object.getConfig();
        if (definition == null){
            return emptyActions;
        }
        String[] actions = definition.getActions();
        return actions != null ? actions : emptyActions;
    }

    public static String getName(GameObject object){
        CacheObjectConfig definition = object.getConfig();
        if (definition == null){
            return "null";
        }
        String name = definition.getName();
        return name != null ? name : "null";
    }

}
