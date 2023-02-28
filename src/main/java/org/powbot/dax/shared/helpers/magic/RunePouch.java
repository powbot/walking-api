package org.powbot.dax.shared.helpers.magic;

import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Varpbits;
import org.powbot.api.rt4.Worlds;

import java.util.Arrays;

public class RunePouch{

   private static final int
			SLOT_1_TYPE_BIT = 29,
			SLOT_1_QUANTITY_BIT = 1624,
			SLOT_2_TYPE_BIT = 1622,
			SLOT_2_QUANTITY_BIT = 1625,
			SLOT_3_TYPE_BIT = 1623,
			SLOT_3_QUANTITY_BIT = 1626,
			SLOT_4_TYPE_BIT = 14285,
			SLOT_4_QUANTITY_BIT = 14286;

   private static final String[]
            POUCH_NAMES = {"Rune pouch", "Divine rune pouch"};

    public enum RuneSlot {
        FIRST (SLOT_1_TYPE_BIT,SLOT_1_QUANTITY_BIT),
        SECOND (SLOT_2_TYPE_BIT,SLOT_2_QUANTITY_BIT),
        THIRD (SLOT_3_TYPE_BIT,SLOT_3_QUANTITY_BIT),
        FOURTH(SLOT_4_TYPE_BIT,SLOT_4_QUANTITY_BIT);

        private final int type, quantityVarbitIndex;

        RuneSlot(int type, int quantity){
            this.type = type;
            this.quantityVarbitIndex = quantity;
        }

        public String getRuneName(){
            switch(Varpbits.value(type, true)){
                case 1: return "Air rune";
                case 2: return "Water rune";
                case 3: return "Earth rune";
                case 4: return "Fire rune";
                case 5: return "Mind rune";
                case 6: return "Chaos rune";
                case 7: return "Death rune";
                case 8: return "Blood rune";
                case 9: return "Cosmic rune";
                case 10: return "Nature rune";
                case 11: return "Law rune";
                case 12: return "Body rune";
                case 13: return "Soul rune";
                case 14: return "Astral rune";
                case 15: return "Mist rune";
                case 16: return "Mud rune";
                case 17: return "Dust rune";
                case 18: return "Lava rune";
                case 19: return "Steam rune";
                case 20: return "Smoke rune";
                default: return null;
            }
        }

        public int getQuantity(){
            return Varpbits.value(quantityVarbitIndex, true);
        }
        
        public int type() {
            return type;
        }

    }

    public static int getQuantity(RuneElement runeElement){
        if (!hasPouch()){
            return 0;
        }

        for(RuneSlot slot : RuneSlot.values()){
            String runeName = slot.getRuneName();
            if (runeName == null || Arrays.stream(runeElement.getAlternativeNames()).noneMatch(runeName::startsWith)){
                continue;
            }
            return slot.getQuantity();
        }

        return 0;
    }

    public static boolean hasPouch(){
        return Inventory.stream().name(POUCH_NAMES).isNotEmpty() && Worlds.isCurrentWorldMembers();
    }

}
