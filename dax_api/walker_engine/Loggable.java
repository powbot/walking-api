package dax_api.walker_engine;

import org.tribot.api.General;
import dax_api.api_lib.DaxConfigs;

public interface Loggable {

    enum Level {
        VERBOSE,
        INFO,
        SEVERE,
        WARNING;
    }

    String getName();

    default void log(CharSequence debug){
        General.println("[" + getName() + "] " + debug);
    }

    default void log(Level level, CharSequence debug) {
        if (!DaxConfigs.logging){
            return;
        }
        System.out.println(level + " [" + getName() + "] " + debug);
    }
}
