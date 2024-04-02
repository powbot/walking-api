package org.powbot.dax.shared.helpers.magic;

import org.powbot.api.rt4.*;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.dax.shared.Pair;

import java.util.List;


public enum Spell {

    HOME_TELEPORT(
            SpellBook.Type.STANDARD, 1, Magic.Spell.HOME_TELEPORT
    ),
    VARROCK_TELEPORT    (
	    SpellBook.Type.STANDARD, 25, Magic.Spell.VARROCK_TELEPORT,    new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR),     new Pair<>(1, RuneElement.FIRE)),
    LUMBRIDGE_TELEPORT  (
	    SpellBook.Type.STANDARD, 31, Magic.Spell.LUMBRIDGE_TELEPORT,  new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR),     new Pair<>(1, RuneElement.EARTH)),
    FALADOR_TELEPORT    (
	    SpellBook.Type.STANDARD, 37, Magic.Spell.FALADOR_TELEPORT,    new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR),     new Pair<>(1, RuneElement.WATER)),
    CAMELOT_TELEPORT    (
	    SpellBook.Type.STANDARD, 45, Magic.Spell.CAMELOT_TELEPORT,    new Pair<>(1, RuneElement.LAW), new Pair<>(5, RuneElement.AIR)),
    ARDOUGNE_TELEPORT   (
	    SpellBook.Type.STANDARD, 51, Magic.Spell.ARDOUGNE_TELEPORT,   new Pair<>(2, RuneElement.LAW), new Pair<>(2, RuneElement.WATER)),
    KOUREND_TELEPORT	(
	    SpellBook.Type.STANDARD, 69, Magic.Spell.KOUREND_CASTLE_TELEPORT,new Pair<>(2, RuneElement.LAW),new Pair<>(1, RuneElement.WATER), new Pair<>(1, RuneElement.FIRE)),
    TELEPORT_TO_HOUSE   (
        SpellBook.Type.STANDARD, 40, Magic.Spell.TELEPORT_TO_HOUSE,new Pair<>(1, RuneElement.LAW), new Pair<>(1, RuneElement.AIR), new Pair<>(1, RuneElement.EARTH)),
    CIVITAS_ILLA_FORTIS (
        SpellBook.Type.STANDARD, 54, Magic.Spell.CIVITAS_ILLA_FORTIS_TELEPORT, new Pair<>(2, RuneElement.LAW), new Pair<>(1, RuneElement.EARTH), new Pair<>(1, RuneElement.FIRE)
    )
    ;

    private final SpellBook.Type spellBookType;
    private final int requiredLevel;
    private final Magic.Spell spell;
    private final Pair<Integer, RuneElement>[] recipe;

    Spell(SpellBook.Type spellBookType, int level, Magic.Spell spellName, Pair<Integer, RuneElement>... recipe){
        this.spellBookType = spellBookType;
        this.requiredLevel = level;
        this.spell = spellName;
        this.recipe = recipe;
    }

    public Pair<Integer, RuneElement>[] getRecipe(){
        return recipe;
    }

    public Magic.Spell getSpell() {
        return spell;
    }

    public boolean cast() {
        return canUse(Inventory.stream().list(), Equipment.stream().list()) && spell.cast();
    }

    public boolean cast(String action){
        return spell.cast(action);
    }

    public boolean canUse(){
        return canUse(Inventory.stream().list(), Equipment.stream().list());
    }

    public boolean canUse(List<Item> inventory, List<Item> equipment){
        if (SpellBook.getCurrentSpellBook() != spellBookType){
            return false;
        }
        if (requiredLevel > Skills.level(Skill.Magic)){
            return false;
        }
        if (this == ARDOUGNE_TELEPORT && Varpbits.varpbit(165, true) < 30){
            return false;
        }
        if (this == KOUREND_TELEPORT && Varpbits.value(6027) < 11){
            return false;
        }
        if (this == CIVITAS_ILLA_FORTIS && Varpbits.value(9649) <= 48){
            return false;
        }


        for (Pair<Integer, RuneElement> pair : recipe){
            int amountRequiredForSpell = pair.getKey();
            RuneElement runeElement = pair.getValue();
            if (runeElement.getCount(inventory, equipment) < amountRequiredForSpell){
                return false;
            }
        }
        return true;
    }

}
