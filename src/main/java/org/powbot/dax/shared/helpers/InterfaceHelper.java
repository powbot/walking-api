package org.powbot.dax.shared.helpers;


import org.powbot.api.rt4.Component;
import org.powbot.api.rt4.Widget;
import org.powbot.api.rt4.Widgets;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

import java.util.*;

public class InterfaceHelper {

    /**
     *
     * @param ids
     * @return never null
     */
    public static List<RSInterface> getAllInterfaces(int... ids){
        return Interfaces.getAllInterfaces(ids);
    }

    public static List<RSInterface> getAllInterfaces(RSInterface parent){
        ArrayList<RSInterface> interfaces = new ArrayList<>();
        Queue<RSInterface> queue = new LinkedList<>();

        if (parent == null){
            return interfaces;
        }

        queue.add(parent);
        while (!queue.isEmpty()){
            RSInterface rsInterface = queue.poll();
            interfaces.add(rsInterface);
            RSInterface[] children = rsInterface.getChildren();
            if (children != null) {
                Collections.addAll(queue, children);
            }
        }

        return interfaces;
    }

    public static boolean textEquals(RSInterface rsInterface, String match){
        String text = rsInterface.getText();
        return text != null && text.equals(match);
    }

    public static boolean textContains(RSInterface rsInterface, String match){
        String text = rsInterface.getText();
        return text != null && text.contains(match);
    }

    public static boolean textMatches(RSInterface rsInterface, String match){
        if (rsInterface == null){
            return false;
        }
        String text = rsInterface.getText();
        return text != null && text.matches(match);
    }

    public static List<String> getActions(RSInterface rsInterface){
        if (rsInterface == null){
            return Collections.emptyList();
        }
        String[] actions = rsInterface.getActions();
        if (actions == null){
            return Collections.emptyList();
        }
        return Arrays.asList(actions);
    }

}
