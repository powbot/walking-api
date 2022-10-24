package org.powbot.dax.teleports;

import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Equipment;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;
import org.powbot.api.rt4.Players;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.interaction.NPCInteraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class WearableItemTeleport {

	private static final Predicate<Item> NOT_NOTED = i -> !i.getDefinition().isNoted();

	public static final Predicate<Item> RING_OF_WEALTH_FILTER = Filters.Items.nameContains("Ring of wealth (").and(i -> i.getDefinition().getName().matches(".*[1-5]\\)")).and(NOT_NOTED);
	public static final Predicate<Item> RING_OF_DUELING_FILTER = Filters.Items.nameContains("Ring of dueling").and(NOT_NOTED);
	public static final Predicate<Item> NECKLACE_OF_PASSAGE_FILTER = Filters.Items.nameContains("Necklace of passage").and(NOT_NOTED);
	public static final Predicate<Item> COMBAT_BRACE_FILTER = Filters.Items.nameContains("Combat bracelet(").and(NOT_NOTED);
	public static final Predicate<Item> GAMES_NECKLACE_FILTER = Filters.Items.nameContains("Games necklace").and(NOT_NOTED);
	public static final Predicate<Item> GLORY_FILTER = Filters.Items.nameContains("glory").and(Filters.Items.nameContains("eternal","(")).and(NOT_NOTED);
	public static final Predicate<Item> SKILLS_FILTER = Filters.Items.nameContains("Skills necklace(").and(NOT_NOTED);
	public static final Predicate<Item> BURNING_AMULET_FILTER = Filters.Items.nameContains("Burning amulet(").and(NOT_NOTED);
	public static final Predicate<Item> DIGSITE_PENDANT_FILTER = Filters.Items.nameContains("Digsite pendant");
	public static final Predicate<Item> TELEPORT_CRYSTAL_FILTER = Filters.Items.nameContains("Teleport crystal");
	public static final Predicate<Item> XERICS_TALISMAN_FILTER = Filters.Items.nameEquals("Xeric's talisman");
	public static final Predicate<Item> RADAS_BLESSING_FILTER = Filters.Items.nameContains("Rada's blessing");
	public static final Predicate<Item> CRAFTING_CAPE_FILTER = Filters.Items.nameContains("Crafting cape");
	public static final Predicate<Item> EXPLORERS_RING_FILTER = Filters.Items.nameContains("Explorer's ring");
	public static final Predicate<Item> QUEST_CAPE_FILTER = Filters.Items.nameContains("Quest point cape");
	public static final Predicate<Item> ARDOUGNE_CLOAK_FILTER = Filters.Items.nameContains("Ardougne cloak");
	public static final Predicate<Item> CONSTRUCTION_CAPE_FILTER = Filters.Items.nameContains("Construct. cape");
	public static final Predicate<Item> SLAYER_RING = Filters.Items.nameContains("Slayer ring");
	public static final Predicate<Item> FARMING_CAPE_FILTER = Filters.Items.nameContains("Farming cape");
	public static final Predicate<Item> DRAKANS_MEDALLION_FILTER = Filters.Items.nameEquals("Drakan's medallion");


	private WearableItemTeleport() {

	}

	public static boolean has(Predicate<Item> filter) {
		return Inventory.stream().filter(filter).isNotEmpty() || Equipment.stream().filter(filter).isNotEmpty();
	}

	public static boolean teleport(Predicate<Item> filter, String action) {
		return teleportWithItem(filter,action);
	}


	private static boolean teleportWithItem(Predicate<Item> itemFilter, String regex) {
		Item teleportItem = Inventory.stream().filter(itemFilter).first();
		if (!teleportItem.valid()) {
			teleportItem = Equipment.stream().filter(itemFilter).first();
		}

		if (!teleportItem.valid()) {
			return false;
		}

		final Tile startingPosition = Players.local().tile();

		boolean interact = teleportItem.interact("(Rub|Teleport|" + regex + ")");
		System.out.println("Interaction return value: " + interact);
		return interact && WaitFor.condition(
				Random.nextInt(3800, 4600), () -> {
					NPCInteraction.handleConversationRegex("(Teleport|"+regex+")");
					if (startingPosition.distanceTo(Players.local().tile()) > 5) {
						return WaitFor.Return.SUCCESS;
					}
					return WaitFor.Return.IGNORE;
				}) == WaitFor.Return.SUCCESS;
	}

}