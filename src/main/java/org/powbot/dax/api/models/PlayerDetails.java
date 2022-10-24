package org.powbot.dax.api.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.Item;
import org.powbot.api.rt4.Skills;
import org.powbot.api.rt4.Varpbits;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerDetails {

    public static PlayerDetails generate(List<Item> inventoryItems, List<Item> equipmentItems) {

        List<IntPair> inventory = inventoryItems
                .stream()
                .map(rsItem -> new IntPair(rsItem.id(), rsItem.getStack())).collect(Collectors.toList());

        List<IntPair> equipment = equipmentItems
                .stream()
                .map(rsItem -> new IntPair(rsItem.id(), rsItem.getStack())).collect(Collectors.toList());

        List<IntPair> settings = Stream.of(10, 11, 17, 32, 63, 68, 71, 101, 111, 116, 131, 144, 145, 150, 165, 176,
            179, 273, 299, 302, 307, 314, 335, 347, 351, 365, 371, 387, 399, 425, 437, 440, 482, 622, 655, 671, 705, 794, 810,
            869, 896, 964, 1630, 1671, 1672)
                                       .map(value -> new IntPair(value, Varpbits.varpbit(value))).distinct().collect(Collectors.toList());

        List<IntPair> varbit = Arrays.stream(new int[]{
            192,
            199,
            357,
            2310,
            2328,
            3534,
            3741,
            4538,
            4566,
            4885,
            4895,
            4897,
            5087,
            5088,
            5089,
            5090,
            5810,
            9016
        })
                .mapToObj(value -> new IntPair(value, Varpbits.value(value))).distinct().collect(
				        Collectors.toList());

        boolean[] plantedSpiritTrees = {false, false, false, false, false};

        return new PlayerDetails(
                Skills.realLevel(Skill.ATTACK),
                Skills.realLevel(Skills.SKILLS.DEFENCE),
                Skills.realLevel(Skills.SKILLS.STRENGTH),
                Skills.realLevel(Skills.SKILLS.HITPOINTS),
                Skills.realLevel(Skills.SKILLS.RANGED),
                Skills.realLevel(Skills.SKILLS.PRAYER),
                Skills.realLevel(Skills.SKILLS.MAGIC),
                Skills.realLevel(Skills.SKILLS.COOKING),
                Skills.realLevel(Skills.SKILLS.WOODCUTTING),
                Skills.realLevel(Skills.SKILLS.FLETCHING),
                Skills.realLevel(Skills.SKILLS.FISHING),
                Skills.realLevel(Skills.SKILLS.FIREMAKING),
                Skills.realLevel(Skills.SKILLS.CRAFTING),
                Skills.realLevel(Skills.SKILLS.SMITHING),
                Skills.realLevel(Skills.SKILLS.MINING),
                Skills.realLevel(Skills.SKILLS.HERBLORE),
                Skills.level(Skills.SKILLS.AGILITY),
                Skills.realLevel(Skills.SKILLS.THIEVING),
                Skills.realLevel(Skills.SKILLS.SLAYER),
                Skills.realLevel(Skills.SKILLS.FARMING),
                Skills.realLevel(Skills.SKILLS.RUNECRAFTING),
                Skills.realLevel(Skills.SKILLS.HUNTER),
                Skills.realLevel(Skills.SKILLS.CONSTRUCTION),
                settings,
                varbit,
                Game.is().orElse(false),
                equipment,
                inventory
        );
    }


    private int attack;

    private int defence;

    private int strength;

    private int hitpoints;

    private int ranged;

    private int prayer;

    private int magic;

    private int cooking;

    private int woodcutting;

    private int fletching;

    private int fishing;

    private int firemaking;

    private int crafting;

    private int smithing;

    private int mining;

    private int herblore;

    private int agility;

    private int thieving;

    private int slayer;

    private int farming;

    private int runecrafting;

    private int hunter;

    private int construction;

    private List<IntPair> setting;

    private List<IntPair> varbit;

    private boolean member;

    private List<IntPair> equipment;

    private List<IntPair> inventory;

    public PlayerDetails() {

    }

    public PlayerDetails(int attack, int defence, int strength, int hitpoints, int ranged, int prayer, int magic, int cooking, int woodcutting, int fletching, int fishing, int firemaking, int crafting, int smithing, int mining, int herblore, int agility, int thieving, int slayer, int farming, int runecrafting, int hunter, int construction, List<IntPair> setting, List<IntPair> varbit, boolean member, List<IntPair> equipment, List<IntPair> inventory) {
        this.attack = attack;
        this.defence = defence;
        this.strength = strength;
        this.hitpoints = hitpoints;
        this.ranged = ranged;
        this.prayer = prayer;
        this.magic = magic;
        this.cooking = cooking;
        this.woodcutting = woodcutting;
        this.fletching = fletching;
        this.fishing = fishing;
        this.firemaking = firemaking;
        this.crafting = crafting;
        this.smithing = smithing;
        this.mining = mining;
        this.herblore = herblore;
        this.agility = agility;
        this.thieving = thieving;
        this.slayer = slayer;
        this.farming = farming;
        this.runecrafting = runecrafting;
        this.hunter = hunter;
        this.construction = construction;
        this.setting = setting;
        this.varbit = varbit;
        this.member = member;
        this.equipment = equipment;
        this.inventory = inventory;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefence() {
        return defence;
    }

    public int getStrength() {
        return strength;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public int getRanged() {
        return ranged;
    }

    public int getPrayer() {
        return prayer;
    }

    public int getMagic() {
        return magic;
    }

    public int getCooking() {
        return cooking;
    }

    public int getWoodcutting() {
        return woodcutting;
    }

    public int getFletching() {
        return fletching;
    }

    public int getFishing() {
        return fishing;
    }

    public int getFiremaking() {
        return firemaking;
    }

    public int getCrafting() {
        return crafting;
    }

    public int getSmithing() {
        return smithing;
    }

    public int getMining() {
        return mining;
    }

    public int getHerblore() {
        return herblore;
    }

    public int getAgility() {
        return agility;
    }

    public int getThieving() {
        return thieving;
    }

    public int getSlayer() {
        return slayer;
    }

    public int getFarming() {
        return farming;
    }

    public int getRunecrafting() {
        return runecrafting;
    }

    public int getHunter() {
        return hunter;
    }

    public int getConstruction() {
        return construction;
    }

    public List<IntPair> getSetting() {
        return setting;
    }

    public List<IntPair> getVarbit() {
        return varbit;
    }

    public boolean isMember() {
        return member;
    }

    public List<IntPair> getEquipment() {
        return equipment;
    }

    public List<IntPair> getInventory() {
        return inventory;
    }

    public JsonElement toJson() {
        return new Gson().toJsonTree(this);
    }

}
