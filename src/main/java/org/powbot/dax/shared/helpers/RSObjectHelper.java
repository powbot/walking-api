package org.powbot.dax.shared.helpers;

import org.tribot.api2007.Objects;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


public class RSObjectHelper {

    public static RSObject get(Predicate<RSObject> filter){
        RSObject[] objects = Objects.find(10, filter);
        return objects.length > 0 ? objects[0] : null;
    }

    public static boolean exists(Predicate<RSObject> filter){
        return Objects.find(10, filter).length > 0;
    }

    public static List<String> getActionsList(RSObject object){
        return Arrays.asList(getActions(object));
    }

    public static String[] getActions(RSObject object){
        String[] emptyActions = new String[0];
        RSObjectDefinition definition = object.getDefinition();
        if (definition == null){
            return emptyActions;
        }
        String[] actions = definition.getActions();
        return actions != null ? actions : emptyActions;
    }

    public static String getName(RSObject object){
        RSObjectDefinition definition = object.getDefinition();
        if (definition == null){
            return "null";
        }
        String name = definition.getName();
        return name != null ? name : "null";
    }

}
