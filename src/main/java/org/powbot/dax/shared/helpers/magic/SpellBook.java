package org.powbot.dax.shared.helpers.magic;

import org.powbot.api.rt4.Varpbits;

import java.util.Arrays;

public class SpellBook {

    private static final int SPELLBOOK_VARBIT = 4070;

    public enum Type {
        STANDARD (0),
        ANCIENT (1),
        LUNAR (2),
        ARCEUUS (3);

        private int varbit;
        Type (int varbit){
            this.varbit = varbit;
        }

        public boolean isInUse() {
            return Varpbits.value(SPELLBOOK_VARBIT, true) == varbit;
        }
    }

    public static Type getCurrentSpellBook(){
        return Arrays.stream(Type.values()).filter(Type::isInUse).findAny().orElse(null);
    }

}
