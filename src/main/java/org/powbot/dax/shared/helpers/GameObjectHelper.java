package org.powbot.dax.shared.helpers;

import org.powbot.api.rt4.GameObject;
import org.powbot.api.rt4.Objects;
import org.powbot.mobile.rlib.generated.RObjectDefinition;
import org.powbot.mobile.rscache.loader.ObjectLoader;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


public class GameObjectHelper {

    public static GameObject get(Predicate<GameObject> filter){
        List<GameObject> objects = Objects.stream(10, GameObject.Type.INTERACTIVE).filter(filter).list();
        if(objects.size() > 0)
            return objects.get(0);
        objects = Objects.stream(10, GameObject.Type.BOUNDARY).filter(filter).list();
        if(objects.size() > 0)
            return objects.get(0);
        objects = Objects.stream(10, GameObject.Type.WALL_DECORATION).filter(filter).list();
        if(objects.size() > 0)
            return objects.get(0);
        objects = Objects.stream(10, GameObject.Type.FLOOR_DECORATION).filter(filter).list();
        return objects.size() > 0 ? objects.get(0) : null;
    }

    public static boolean exists(Predicate<GameObject> filter){
        return get(filter) != null;
    }

    public static List<String> getActionsList(GameObject object){
        return Arrays.asList(getActions(object));
    }

    public static String[] getActions(GameObject object){
        String[] emptyActions = new String[0];
        return ObjectLoader.lookup(object.id()).actions();
    }

    public static String getName(GameObject object){
        return ObjectLoader.lookup(object.id()).name();
    }

}
