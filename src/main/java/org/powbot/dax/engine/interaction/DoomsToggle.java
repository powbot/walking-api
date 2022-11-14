package org.powbot.dax.engine.interaction;

import org.powbot.api.rt4.Component;
import org.powbot.api.rt4.Components;
import org.powbot.api.rt4.Widgets;
import org.powbot.dax.engine.Loggable;

import java.util.Arrays;
import java.util.List;


public class DoomsToggle implements Loggable {

    private static final int STRONGHOLD_TOGGLE = 579, WILDERNESS_TOGGLE = 475, SHANTY_TOGGLE = 565, WATERBIRTH = 574, MORT_MYRE = 580, LUMBRIDGE_SWAMP = 572,
            OBSERVATORY_TOGGLE = 560;

    private static final int[] GENERAL_CASES = {STRONGHOLD_TOGGLE, WILDERNESS_TOGGLE, SHANTY_TOGGLE, WATERBIRTH, MORT_MYRE, LUMBRIDGE_SWAMP, OBSERVATORY_TOGGLE};

    private static DoomsToggle instance;

    private static DoomsToggle getInstance(){
        return instance != null ? instance : (instance = new DoomsToggle());
    }


    public static void handleToggle(){
        for (int generalCase : GENERAL_CASES){
            handle(generalCase, "Yes", "Enter Wilderness","Enter the swamp.","I'll be fine without a tinderbox.",
                    "Proceed regardless");
        }
    }

    public static void handle(int parentInterface, String... options){
        if (!Widgets.component(parentInterface, 0).valid()){
            return;
        }
        getInstance().log("Handling Interface: " + parentInterface);
        List<String> asList = Arrays.asList(options);
        Components.stream(parentInterface).filter(c -> asList.contains(c.text())).anyMatch(Component::click);
    }

    @Override
    public String getName() {
        return "Dooms Toggle";
    }

}
