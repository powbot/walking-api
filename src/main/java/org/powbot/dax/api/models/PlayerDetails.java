package org.powbot.dax.api.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.powbot.api.rt4.Item;
import org.powbot.api.rt4.Skills;
import org.powbot.api.rt4.Varpbits;
import org.powbot.api.rt4.Worlds;
import org.powbot.api.rt4.walking.model.Skill;

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
            179, 212, 226, 273, 299, 302, 307, 314, 317, 328, 335, 347, 351, 365, 371, 387, 399, 425, 437, 440, 482, 622, 655, 671, 705, 794, 810, 823,
            869, 896, 964, 1429, 1570, 1630, 1671, 1672)
                                       .map(value -> new IntPair(value, Varpbits.varpbit(value, true))).distinct().collect(Collectors.toList());

        List<IntPair> varbit = Arrays.stream(new int[]{
                192,
                199,
                357,
                386,
                571,
                1048,
                1990,
                2310,
                2328,
                2448,
                3534,
                3618, //dream mentor
                3741,
                4163, //teleblock
                4493,
                4494, //fremmy elite diary status
                4538,
                4566,
                4885,
                4895,
                4897,
                5027,
                5087,
                5088,
                5089,
                5090,
                5800, //fossil island- museum camp bank has been built if this is 1
                5810,
                6071,
                6104,
                7255,
                7796,//forsaken tower
                7928, //kourend elite diary
                9016,
                9459, //fremennik exiles
                9632, //Children of the sun,
                9649, //Twilights Promise
                9819,
                9956, //colossal wyrm remains quetzal landing site
                9957, //outer fortis quetzal landing site
                10150, //we've paid 1m to Andras for free boat rides to Slepe if this is 1,
                11379, //salvager outlook quetzal landing site
                12063, //below ice mountain quest
                13719, //unlocked the 93 agility blood altar shortcut by mining the other side (78 mining req)
                13738, // temple of the eye
                13903, //Sleeping Giants quest
                15288 //Path of Glouphrie quest
        })
                .mapToObj(value -> new IntPair(value, Varpbits.value(value, true))).distinct().collect(
				        Collectors.toList());

        boolean[] plantedSpiritTrees = {false, false, false, false, false};

        return new PlayerDetails(
                Skills.realLevel(Skill.Attack),
                Skills.realLevel(Skill.Defence),
                Skills.realLevel(Skill.Strength),
                Skills.realLevel(Skill.Hitpoints),
                Skills.realLevel(Skill.Ranged),
                Skills.realLevel(Skill.Prayer),
                Skills.realLevel(Skill.Magic),
                Skills.realLevel(Skill.Cooking),
                Skills.realLevel(Skill.Woodcutting),
                Skills.realLevel(Skill.Fletching),
                Skills.realLevel(Skill.Fishing),
                Skills.realLevel(Skill.Firemaking),
                Skills.realLevel(Skill.Crafting),
                Skills.realLevel(Skill.Smithing),
                Skills.realLevel(Skill.Mining),
                Skills.realLevel(Skill.Herblore),
                Skills.level(Skill.Agility),
                Skills.realLevel(Skill.Thieving),
                Skills.realLevel(Skill.Slayer),
                Skills.realLevel(Skill.Farming),
                Skills.realLevel(Skill.Runecrafting),
                Skills.realLevel(Skill.Hunter),
                Skills.realLevel(Skill.Construction),
                settings,
                varbit,
                Worlds.isCurrentWorldMembers(),
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

    private List<IntPair> walkerPreferences;

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
