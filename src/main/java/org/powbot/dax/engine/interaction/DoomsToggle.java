package org.powbot.dax.engine.interaction;

import org.powbot.api.Condition;
import org.powbot.api.rt4.Component;
import org.powbot.api.rt4.Components;
import org.powbot.api.rt4.Widgets;
import org.powbot.dax.engine.Loggable;
import org.powbot.dax.engine.WaitFor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DoomsToggle implements Loggable {

    public enum WARNINGS {
        STRONGHOLD_OF_SECURITY(579),
        WILDERNESS(475, 11),
        SHANTAY(565),
        WATERBIRTH(574),
        MORT_MYRE(580),
        LUMBRIDGE_SWAMP_CAVES(572),
        OBSERVATORY(560),
        REVENANT(720);
        ;
        private int widget, component;
        WARNINGS(int widget){
            this(widget, 17);
        }
        WARNINGS(int widget, int component){
            this.widget = widget;
            this.component = component;
        }
        public boolean isPresent(){
            return Widgets.component(widget, 0).visible();
        }
        public boolean handle(){
            getInstance().log("Handling Widget: " + widget);
            Component dontAskAgain = Components.stream().text("Don't ask me this again").viewable().first();
            if(dontAskAgain.valid()){
                dontAskAgain.click();
                WaitFor.milliseconds(200, 400);
            }
            Component c = Widgets.component(widget, component);
            return c != Component.Companion.getNil() && c.click() && Condition.wait(() -> !isPresent(), 450, 5);
        }
    }

    private static final int STRONGHOLD_TOGGLE = 579, WILDERNESS_TOGGLE = 475, SHANTY_TOGGLE = 565, WATERBIRTH = 574, MORT_MYRE = 580, LUMBRIDGE_SWAMP = 572,
            OBSERVATORY_TOGGLE = 560;

    private static final int[] GENERAL_CASES = {STRONGHOLD_TOGGLE, WILDERNESS_TOGGLE, SHANTY_TOGGLE, WATERBIRTH, MORT_MYRE, LUMBRIDGE_SWAMP, OBSERVATORY_TOGGLE};

    private static DoomsToggle instance;

    private static DoomsToggle getInstance(){
        return instance != null ? instance : (instance = new DoomsToggle());
    }


    public static void handleToggle(){
        Arrays.stream(WARNINGS.values()).filter(WARNINGS::isPresent).anyMatch(WARNINGS::handle);
    }

    public static void handle(int parentInterface, String... options){
        if (!Widgets.component(parentInterface, 0).valid()){
            return;
        }
        getInstance().log("Handling Interface: " + parentInterface);
        List<String> asList = new ArrayList<>(Arrays.asList(options));
        Components.stream(parentInterface).anyMatch(c -> {
            String txt = c.text();
            if(txt.length() == 0)
                return false;
            boolean matches = txt.contains("Proceed regardless") || asList.contains(txt);
            if(!matches){
                return false;
            }
            return c.click();
        });
    }

    @Override
    public String getName() {
        return "Dooms Toggle";
    }

}
