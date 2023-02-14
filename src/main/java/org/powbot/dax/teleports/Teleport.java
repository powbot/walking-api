package org.powbot.dax.teleports;

import org.powbot.api.Condition;
import org.powbot.api.Input;
import org.powbot.api.StringUtils;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.dax.api.models.Requirement;
import org.powbot.dax.teleports.utils.POH;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.interaction.NPCInteraction;
import org.powbot.dax.shared.helpers.ItemHelper;
import org.powbot.dax.shared.helpers.magic.Spell;
import org.powbot.dax.shared.helpers.magic.SpellBook;
import org.powbot.dax.teleports.utils.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum Teleport {

	VARROCK_TELEPORT(
			35, new Tile(3212, 3424, 0),
			Spell.VARROCK_TELEPORT::canUse,
			() -> selectSpell(Spell.VARROCK_TELEPORT,"Cast"),
			false
	),

	VARROCK_TELEPORT_TAB(
			35, new Tile(3212, 3424, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Varrock teleport")),
			() -> ItemHelper.click("Varrock t.*", "Break")
	),

	VARROCK_TELEPORT_GRAND_EXCHANGE(
			35, new Tile(3161, 3478, 0),
			(i1, i2) -> Spell.VARROCK_TELEPORT.canUse(i1, i2) && TeleportConstants.isVarrockTeleportAtGE(),
			() -> selectSpell(Spell.VARROCK_TELEPORT,"Grand Exchange")
	),

	LUMBRIDGE_TELEPORT(
			35, new Tile(3225, 3219, 0),
			Spell.LUMBRIDGE_TELEPORT::canUse,
			() -> selectSpell(Spell.LUMBRIDGE_TELEPORT,"Cast"),
			false
	),

	LUMBRIDGE_TELEPORT_TAB(
			35, new Tile(3225, 3219, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Lumbridge teleport")),
			() -> ItemHelper.click("Lumbridge t.*", "Break")
	),

	FALADOR_TELEPORT(
			35, new Tile(2966, 3379, 0),
			Spell.FALADOR_TELEPORT::canUse,
			() -> selectSpell(Spell.FALADOR_TELEPORT,"Cast"),
			false
	),

	FALADOR_TELEPORT_TAB(
			35, new Tile(2966, 3379, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Falador teleport")),
			() -> ItemHelper.click("Falador t.*", "Break")
	),

	CAMELOT_TELEPORT(
			35, new Tile(2757, 3479, 0),
			Spell.CAMELOT_TELEPORT::canUse,
			() -> selectSpell(Spell.CAMELOT_TELEPORT,"Cast")

	),

	CAMELOT_TELEPORT_TAB(
			35, new Tile(2757, 3479, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Camelot teleport")),
			() -> ItemHelper.click("Camelot t.*", "Break")
	),

	SEERS_TELEPORT(
			35, new Tile(2757, 3479, 0),
			(i1, i2) -> Spell.CAMELOT_TELEPORT.canUse(i1, i2) && Varpbits.value(4560, true) == 1,
			() -> selectSpell(Spell.CAMELOT_TELEPORT,"Seers'")
	),

	ARDOUGNE_TELEPORT(
			35, new Tile(2661, 3300, 0),
			Spell.ARDOUGNE_TELEPORT::canUse,
			() -> selectSpell(Spell.ARDOUGNE_TELEPORT,"Cast")

	),

	ARDOUGNE_TELEPORT_TAB(
			35, new Tile(2661, 3300, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Ardougne teleport")),
			() -> ItemHelper.click("Ardougne t.*", "Break")
	),

	NARDAH_TELEPORT(
			35, TeleportScrolls.NARDAH
	),
	DIGSITE_TELEPORT(
			35, TeleportScrolls.DIGSITE
	),
	FELDIP_HILLS_TELEPORT(
			35, TeleportScrolls.FELDIP_HILLS
	),
	LUNAR_ISLE_TELEPORT(
			35, TeleportScrolls.LUNAR_ISLE
	),
	MORTTON_TELEPORT(
			35, TeleportScrolls.MORTTON
	),
	PEST_CONTROL_TELEPORT(
			35, TeleportScrolls.PEST_CONTROL
	),
	PISCATORIS_TELEPORT(
			35, TeleportScrolls.PISCATORIS
	),
	TAI_BWO_WANNAI_TELEPORT(
			35, TeleportScrolls.TAI_BWO_WANNAI
	),
	ELF_CAMP_TELEPORT(
			35, TeleportScrolls.ELF_CAMP
	),
	MOS_LE_HARMLESS_TELEPORT(
			35, TeleportScrolls.MOS_LE_HARMLESS
	),
	LUMBERYARD_TELEPORT(
			35, TeleportScrolls.LUMBERYARD
	),
	ZULLANDRA_TELEPORT(
			35, TeleportScrolls.ZULLANDRA
	),
	KEY_MASTER_TELEPORT(
			35, TeleportScrolls.KEY_MASTER
	),
	REVENANT_CAVES_TELEPORT(
			35, TeleportScrolls.REVENANT_CAVES
	),
	WATSON_TELEPORT(
			35, TeleportScrolls.WATSON
	),


	RING_OF_WEALTH_GRAND_EXCHANGE(
			35, new Tile(3161, 3478, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)Grand Exchange"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	RING_OF_WEALTH_FALADOR(
			35, new Tile(2994, 3377, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)falador.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	RING_OF_WEALTH_MISCELLANIA(
			35, new Tile(2535, 3861, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_FILTER, i1, i2) && Varpbits.varpbit(359, true) >= 100,
			() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)misc.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	RING_OF_DUELING_PVP_ARENA (
			35, new Tile(3313, 3233, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*PvP Arena.*")
	),

	RING_OF_DUELING_CASTLE_WARS (
			35, new Tile(2440, 3090, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*Castle Wars.*")
	),

	RING_OF_DUELING_FEROX_ENCLAVE (
			35, new Tile(3150, 3635, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*Ferox Enclave.*")
	),

	NECKLACE_OF_PASSAGE_WIZARD_TOWER (
			35, new Tile(3113, 3179, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*wizard.+tower.*")
	),

	NECKLACE_OF_PASSAGE_OUTPOST (
			35, new Tile(2430, 3347, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*the.+outpost.*")
	),

	NECKLACE_OF_PASSAGE_EYRIE (
			35, new Tile(3406, 3156, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*eagl.+eyrie.*")
	),

	COMBAT_BRACE_WARRIORS_GUILD (
			35, new Tile(2882, 3550, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*warrior.+guild.*")
	),

	COMBAT_BRACE_CHAMPIONS_GUILD (
			35, new Tile(3190, 3366, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*champion.+guild.*")
	),

	COMBAT_BRACE_MONASTARY (
			35, new Tile(3053, 3486, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*monastery.*")
	),

	COMBAT_BRACE_RANGE_GUILD (
			35, new Tile(2656, 3442, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*rang.+guild.*")
	),

	GAMES_NECK_BURTHORPE (
			35, new Tile(2897, 3551, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*burthorpe.*")
	),

	GAMES_NECK_BARBARIAN_OUTPOST (
			35, new Tile(2520, 3570, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*barbarian.*")
	),

	GAMES_NECK_CORPOREAL (
			35, new Tile(2965, 4382, 2),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*corporeal.*")
	),

	GAMES_NECK_WINTERTODT (
			35, new Tile(1623, 3937, 0),
			(i1, i2) -> hasBeenToZeah() && WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*wintertodt.*")
	),

	GLORY_EDGEVILLE (
			35, new Tile(3087, 3496, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER, "(?i).*edgeville.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	GLORY_KARAMJA (
			35, new Tile(2918, 3176, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER,"(?i).*karamja.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	GLORY_DRAYNOR (
			35, new Tile(3105, 3251, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER,"(?i).*draynor.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	GLORY_AL_KHARID (
			35, new Tile(3293, 3163, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER, "(?i).*al kharid.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_FISHING_GUILD (
			35, new Tile(2610, 3391, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Fishing.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_MINING_GUILD (
			35, new Tile(3052, 9764, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Mining.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_CRAFTING_GUILD (
			35, new Tile(2935, 3293, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Craft.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_COOKING_GUILD (
			35, new Tile(3145, 3442, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Cooking.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_WOODCUTTING_GUILD (
			35, new Tile(1663, 3507, 0),
			(i1, i2) -> hasBeenToZeah() && WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Woodcutting.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_FARMING_GUILD_OUTSIDE (
			35, new Tile(1248, 3719, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER, i1, i2) && (Varpbits.value(4895, true) < 600 || Skills.realLevel(Skill.Farming) < 45),
			() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Farming.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_FARMING_GUILD_INSIDE (
			35, new Tile(1249, 3727, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER, i1, i2)
								&& Varpbits.value(4895, true) >= 600 && Skills.realLevel(Skill.Farming) >= 45,
			() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Farming.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	BURNING_AMULET_CHAOS_TEMPLE (
			35, new Tile(3236, 3635, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.BURNING_AMULET_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Chaos.*|Okay, teleport to level.*)")
	),

	BURNING_AMULET_BANDIT_CAMP (
			35, new Tile(3039, 3652, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.BURNING_AMULET_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Bandit.*|Okay, teleport to level.*)")
	),

	BURNING_AMULET_LAVA_MAZE (
			35, new Tile(3029, 3843, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.BURNING_AMULET_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Lava.*|Okay, teleport to level.*)")
	),

	DIGSITE_PENDANT_BARGE(
			35, new Tile(3346,3445,0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.DIGSITE_PENDANT_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.DIGSITE_PENDANT_FILTER, "Digsite")
	),


	ECTOPHIAL (
			0, new Tile(3660, 3524, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Ectophial")),
			() -> Inventory.stream().filter(ItemFilters.nameContains("Ectophial")).first().interact("Empty")
	),

	LLETYA (
			35, new Tile(2330,3172,0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.TELEPORT_CRYSTAL_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.TELEPORT_CRYSTAL_FILTER, "Lletya")
	),

	XERICS_GLADE(
			35, new Tile(1753, 3565, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.XERICS_TALISMAN_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Glade")
	),
	XERICS_INFERNO(
			35, new Tile(1505,3809,0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.XERICS_TALISMAN_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Inferno")
	),
	XERICS_LOOKOUT(
			35, new Tile(1575, 3531, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.XERICS_TALISMAN_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Lookout")
	),

	WEST_ARDOUGNE_TELEPORT_TAB(
			35, new Tile(2500,3290,0),
			(i1, i2) -> Inventory.stream().name("West ardougne teleport").count(true) > 0,
			() -> ItemHelper.click("West ardougne t.*", "Break")
	),

	RADAS_BLESSING_KOUREND_WOODLAND(
			0, new Tile(1558, 3458, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.RADAS_BLESSING_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.RADAS_BLESSING_FILTER, "Kourend .*")
	),
	RADAS_BLESSING_MOUNT_KARUULM(
			0, new Tile(1310, 3796, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.RADAS_BLESSING_FILTER.and(ItemFilters.nameContains("3","4")), i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.RADAS_BLESSING_FILTER, "Mount.*")
	),

	CRAFTING_CAPE_TELEPORT(
			0, new Tile(2931, 3286, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.CRAFTING_CAPE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.CRAFTING_CAPE_FILTER, "Teleport")
	),

	CABBAGE_PATCH_TELEPORT(
			0, new Tile(3049, 3287, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.EXPLORERS_RING_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.EXPLORERS_RING_FILTER, "Teleport")
	),

	LEGENDS_GUILD_TELEPORT(
			0, new Tile(2729, 3348, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.QUEST_CAPE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.QUEST_CAPE_FILTER, "Teleport")
	),

	KANDARIN_MONASTERY_TELEPORT(
			0, new Tile(2606, 3216, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.ARDOUGNE_CLOAK_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.ARDOUGNE_CLOAK_FILTER, ".*Monastery.*")
	),

	RIMMINGTON_TELEPORT_TAB(
			35, new Tile(2954,3224, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Rimmington teleport")),
			() -> ItemHelper.click("Rimmington t.*", "Break")
	),

	TAVERLEY_TELEPORT_TAB(
			35, new Tile(2894, 3465, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Taverly teleport")),
			() -> ItemHelper.click("Taverley t.*", "Break")
	),

	RELLEKKA_TELEPORT_TAB(
			35, new Tile(2668, 3631, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Rellekka teleport")),
			() -> ItemHelper.click("Rellekka t.*", "Break")
	),

	BRIMHAVEN_TELEPORT_TAB(
			35, new Tile(2758, 3178, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Brimhaven teleport")),
			() -> ItemHelper.click("Brimhaven t.*", "Break")
	),

	POLLNIVNEACH_TELEPORT_TAB(
			35, new Tile(3340, 3004, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Pollnivneach teleport")),
			() -> ItemHelper.click("Pollnivneach t.*", "Break")
	),

	YANILLE_TELEPORT_TAB(
			35, new Tile(2544, 3095, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Yanille teleport")),
			() -> ItemHelper.click("Yanille t.*", "Break")
	),

	HOSIDIUS_TELEPORT_TAB(
			35, new Tile(1744, 3517, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Hosidius teleport")),
			() -> ItemHelper.click("Hosidius t.*", "Break")
	),

	CONSTRUCTION_CAPE_RIMMINGTON(
			0, new Tile(2954,3224, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Rimmington")
	),

	CONSTRUCTION_CAPE_TAVERLEY(
			0, new Tile(2894, 3465, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Taverley")
	),

	CONSTRUCTION_CAPE_RELLEKKA(
			0, new Tile(2668, 3631, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Rellekka")
	),

	CONSTRUCTION_CAPE_BRIMHAVEN(
			0, new Tile(2758, 3178, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Brimhaven")
	),

	CONSTRUCTION_CAPE_POLLNIVNEACH(
			0, new Tile(3340, 3004, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Pollnivneach")
	),

	CONSTRUCTION_CAPE_YANILLE(
			0, new Tile(2544, 3095, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Yanille")
	),

	CONSTRUCTION_CAPE_HOSIDIUS(
			0, new Tile(1744, 3517, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER, i1, i2),
			() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Hosidius")
	),

	SLAYER_RING_GNOME_STRONGHOLD(
			35, new Tile(2433, 3424, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.SLAYER_RING, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Stronghold")
	),

	SLAYER_RING_MORYTANIA(
			35, new Tile(3422, 3537, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.SLAYER_RING, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Tower")
	),

	SLAYER_RING_RELLEKKA_CAVE(
			35, new Tile(2801, 9999, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.SLAYER_RING, i1 , i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Rellekka")
	),

	SALVE_GRAVEYARD_TAB(
			35, new Tile(3432, 3460, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Salve graveyard teleport")),
			() -> ItemHelper.click("Salve graveyard t.*", "Break")
	),

	FENKENSTRAINS_CASTLE_TAB(
			35, new Tile(3547, 3528, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Fenkenstrain's castle teleport")),
			() -> ItemHelper.click("Fenkenstrain's castle t.*", "Break")
	),

	BARROWS_TAB(
			35, new Tile(3565, 3314, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Barrows teleport")),
			() -> ItemHelper.click("Barrows t.*", "Break")
	),

	ARCEUUS_LIBRARY_TAB(
			35, new Tile(1632, 3838, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Arceuus library teleport")),
			() -> ItemHelper.click("Arceuus library t.*", "Break")
	),

	BATTLEFRONT_TAB(
			35, new Tile(1349,3738,0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Battlefront teleport")),
			() -> ItemHelper.click("Battlefront t.*", "Break")
	),

	DRAYNOR_MANOR_TAB(
			35, new Tile(3109,3352,0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Draynor manor teleport")),
			() -> ItemHelper.click("Draynor manor t.*", "Break")
	),

	MIND_ALTAR_TAB(
			35, new Tile(2980, 3510, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Mind altar teleport")),
			() -> ItemHelper.click("Mind altar t.*", "Break")
	),

	ENCHANTED_LYRE_RELLEKA(
			35, new Tile(2661, 3465, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameContains("Enchanted lyre")),
			() -> {
				return ItemHelper.click("Enchanted lyre", "Play|Rellekka.*");
			}
	),

	FARMING_CAPE_TELEPORT(
			0, new Tile(1248, 3726, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.FARMING_CAPE_FILTER, i1, i2),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.FARMING_CAPE_FILTER, "Teleport")
	),

	ROYAL_SEED_POD(
			0, new Tile(2465, 3495, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Royal seed pod")),
			() -> ItemHelper.click("Royal seed.*", "Commune") && Condition.wait(() -> Players.local().animation() != -1, 200, 10) && Condition.wait(() -> Players.local().animation() == -1, 200, 40)
	),

	DRAKANS_MEDALLION_VER_SINHAZA(
			0, new Tile(3649, 3230, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.DRAKANS_MEDALLION_FILTER, i1, i2),
			() -> ItemHelper.click("Drakan's.*", "Ver Sinhaza")

	),

	DRAKANS_MEDALLION_DARKMEYER(
			0, new Tile(3592, 3337, 0),
			(i1, i2) -> WearableItemTeleport.has(WearableItemTeleport.DRAKANS_MEDALLION_FILTER, i1, i2),
			() -> ItemHelper.click("Drakan's.*", "Darkmeyer")

	),

	BARBARIAN_ASSAULT_MINIGAME(
			Grouping.MINIGAMES.BARBARIAN_ASSAULT,
			new Tile(2532, 3577, 0),
			(i1, i2) -> Varpbits.value(3251, true) > 0
	),
	BLAST_FURNACE_MINIGAME(
			Grouping.MINIGAMES.BLAST_FURNACE,
			new Tile(0, 0 ,0),
			(i1, i2) -> Varpbits.value(575, true) >= 1
	),
	BURTHROPE_GAMES_ROOM_MINIGAME(
			Grouping.MINIGAMES.BURTHORPE_GAMES_ROOM,
			new Tile(2207, 4938, 0)
	),
	CASTLE_WARS_MINIGAME(
			Grouping.MINIGAMES.CASTLE_WARS,
			new Tile(2442, 3092, 0),
			false
	),
	CLAN_WARS_MINIGAME(
			Grouping.MINIGAMES.CLAN_WARS,
			new Tile(3149, 3635, 0),
			false
	),
	FISHING_TRAWLER_MINIGAME(
			Grouping.MINIGAMES.FISHING_TRAWLER,
			new Tile(2660, 3158, 0),
			(i1, i2) -> Skills.realLevel(Skill.Fishing) >= 15
	),
	GIANTS_FOUNDRY_MINIGAME(
			Grouping.MINIGAMES.GIANTS_FOUNDRY,
			new Tile(3361, 3147, 0)
	),
//	GUARDIANS_OF_THE_RIFT_MINIGAME(
//			Grouping.MINIGAMES.GUARDIANS_OF_THE_RIFT,
//			new Tile(3614, 9478, 0),
//			() -> Quest.TEMPLE_OF_THE_EYE.getState() == Quest.State.COMPLETE
//	),
	LAST_MAN_STANDING_MINIGAME(
			Grouping.MINIGAMES.LAST_MAN_STANDING,
			new Tile(3151, 3635, 0),
			false
	),
//	NMZ_MINIGAME(
//			Grouping.MINIGAMES.NIGHTMARE_ZONE,
//			new Tile(2611, 3122, 0),
//			() -> QuestHelper.getNmzQuestsCompleted() >= 5
//	),
	PEST_CONTROL_MINIGAME(
			Grouping.MINIGAMES.PEST_CONTROL,
			new Tile(2653, 2656, 0),
			(i1, i2) -> Players.local().getCombatLevel() >= 40
	),
	//	RAT_PITS_ARDOUGNE_MINIGAME(Minigame.RAT_PITS, new Tile(0, 0, 0)),
//	RAT_PITS_VARROCK_MINIGAME(Minigame.RAT_PITS, new Tile(0, 0, 0)),
//	RAT_PITS_KELDAGRIM_MINIGAME(Minigame.RAT_PITS, new Tile(0, 0, 0)),
//	RAT_PITS_PORT_SARIM_MINIGAME(Minigame.RAT_PITS, new Tile(3021, 3228, 0), "Port Sarim (wily cats)"),
//	SHADES_OF_MORTTON_MINIGAME(
//			Grouping.MINIGAMES.SHADES_OF_MORTON,
//			new Tile(3499, 3298, 0),
//			() -> Quest.SHADES_OF_MORTTON.getState() == Quest.State.COMPLETE
//	),
	SOUL_WARS_MINIGAME(
			Grouping.MINIGAMES.SOUL_WARS,
			new Tile(2210, 2857, 0)
	),
	//	TITHE_FARM_MINIGAME(Minigame.TITHE_FARM, new Tile(0, 0, 0),) I didn't see a way to determine if we've unlocked it.
//	TROUBLE_BREWING_MINIGAME(
//			Grouping.MINIGAMES.TROUBLE_BREWING,
//			new Tile(3817, 3025, 0),
//			() -> Quest.CABIN_FEVER.getState() == Quest.State.COMPLETE && Skills.SKILLS.COOKING.getActualLevel() >= 40,
//			"No."
//	),
	TZHAAR_FIGHT_PIT_MINIGAME(
			Grouping.MINIGAMES.TZHAAR_FIGHT_PIT,
			new Tile(2402, 5181, 0),
			(i1, i2) -> Worlds.current().getNumber() == 362,
			"No."
	),

//	MOONCLAN_TELEPORT_TAB(
//			35, new Tile(2115, 3914, 0),
//			() -> Quest.LUNAR_DIPLOMACY.getState() == Quest.State.COMPLETE && Inventory.getCount("Moonclan teleport") > 0,
//			() -> ItemHelper.click("Moonclan tele.*", "Break")
//	),

	OURANIA_TELEPORT_TAB(
			35, new Tile(2468, 3246, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Ourania teleport")),
			() -> ItemHelper.click("Ourania t.*", "Break")
	),

	WATERBIRTH_TELEPORT_TAB(
			35, new Tile(2546, 3757, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Waterbirth teleport")),
			() -> ItemHelper.click("Waterbirth t.*", "Break")
	),

	BARBARIAN_OUTPOST_TELEPORT_TAB(
			35, new Tile(2544, 3568, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Barbarian teleport")),
			() -> ItemHelper.click("Barbarian tele.*", "Break")
	),

	KHAZARD_TELEPORT_TAB(
			35, new Tile(2637, 3167, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Khazard teleport")),
			() -> ItemHelper.click("Khazard tele.*", "Break")
	),

	FISHING_GUILD_TELEPORT_TAB(
			35, new Tile(2612, 3391, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Fishing guild teleport")),
			() -> ItemHelper.click("Fishing guild t.*", "Break")
	),

	CATHERBY_TELEPORT_TAB(
			35, new Tile(2801, 3449, 0),
			(i1, i2) -> i1.stream().anyMatch(ItemFilters.nameEquals("Catherby teleport")),
			() -> ItemHelper.click("Catherby t.*", "Break")
	),

	LUMBRIDGE_HOME_TELEPORT(
			150, new Tile(3225, 3219, 0),
			(i1, i2) -> canUseHomeTeleport() && SpellBook.getCurrentSpellBook() == SpellBook.Type.STANDARD,
			() -> {
				final Tile myPos = Players.local().tile();
				return selectSpell(Spell.HOME_TELEPORT, "Cast") && Condition.wait(() ->  !Players.local().inCombat() &&
																									   !Players.local().tile().equals(myPos), 1500, 10);
			},
			false
	),

	POH_OUTSIDE_RIMMINGTON_TAB(
			35, new Tile(2954, 3224, 0),
			(i1, i2) -> POH.RIMMINGTON.isHouseLocation() && Spell.TELEPORT_TO_HOUSE.canUse(i1, i2),
			() -> Spell.TELEPORT_TO_HOUSE.cast("Outside")
	),
	POH_OUTSIDE_RIMMINGTON(
			35, new Tile(2954, 3224, 0),
			(i1, i2) -> POH.RIMMINGTON.isHouseLocation() && i1.stream().anyMatch(ItemFilters.nameEquals("Teleport to house")),
			() -> ItemHelper.click("Teleport to house", "Outside")
	),
	POH_OUTSIDE_TAVERLY_TAB(
			35, new Tile(2894, 3465, 0),
			(i1, i2) -> POH.TAVERLY.isHouseLocation() && Spell.TELEPORT_TO_HOUSE.canUse(i1, i2),
			() -> Spell.TELEPORT_TO_HOUSE.cast("Outside")
	),
	POH_OUTSIDE_TAVERLY(
			35, new Tile(2894, 3465, 0),
			(i1, i2) -> POH.TAVERLY.isHouseLocation() && i1.stream().anyMatch(ItemFilters.nameEquals("Teleport to house")),
			() -> ItemHelper.click("Teleport to house", "Outside")
	),
	POH_OUTSIDE_POLLNIVNEACH_TAB(
			35, new Tile(3340, 3004, 0),
			(i1, i2) -> POH.POLLNIVNEACH.isHouseLocation() && Spell.TELEPORT_TO_HOUSE.canUse(i1, i2),
			() -> Spell.TELEPORT_TO_HOUSE.cast("Outside")
	),
	POH_OUTSIDE_POLLNIVNEACH(
			35, new Tile(3340, 3004, 0),
			(i1, i2) -> POH.POLLNIVNEACH.isHouseLocation() && i1.stream().anyMatch(ItemFilters.nameEquals("Teleport to house")),
			() -> ItemHelper.click("Teleport to house", "Outside")
	),
	POH_OUTSIDE_HOSIDIUS_TAB(
			35, new Tile(1744, 3517, 0),
			(i1, i2) -> POH.HOSIDIUS.isHouseLocation() && Spell.TELEPORT_TO_HOUSE.canUse(i1, i2),
			() -> Spell.TELEPORT_TO_HOUSE.cast("Outside")
	),
	POH_OUTSIDE_HOSIDIUS(
			35, new Tile(1744, 3517, 0),
			(i1, i2) -> POH.HOSIDIUS.isHouseLocation() && i1.stream().anyMatch(ItemFilters.nameEquals("Teleport to house")),
			() -> ItemHelper.click("Teleport to house", "Outside")
	),
	POH_OUTSIDE_RELLEKKA_TAB(
			35, new Tile(2670, 3632, 0),
			(i1, i2) -> POH.RELLEKKA.isHouseLocation() && Spell.TELEPORT_TO_HOUSE.canUse(i1, i2),
			() -> Spell.TELEPORT_TO_HOUSE.cast("Outside")
	),
	POH_OUTSIDE_RELLEKKA(
			35, new Tile(2670, 3632, 0),
			(i1, i2) -> POH.RELLEKKA.isHouseLocation() && i1.stream().anyMatch(ItemFilters.nameEquals("Teleport to house")),
			() -> ItemHelper.click("Teleport to house", "Outside")
	),
	POH_OUTSIDE_BRIMHAVEN_TAB(
			35, new Tile(2750, 3178, 0),
			(i1, i2) -> POH.BRIMHAVEN.isHouseLocation() && Spell.TELEPORT_TO_HOUSE.canUse(i1, i2),
			() -> Spell.TELEPORT_TO_HOUSE.cast("Outside")
	),
	POH_OUTSIDE_BRIMHAVEN(
			35, new Tile(2750, 3178, 0),
			(i1, i2) -> POH.BRIMHAVEN.isHouseLocation() && i1.stream().anyMatch(ItemFilters.nameEquals("Teleport to house")),
			() -> ItemHelper.click("Teleport to house", "Outside")
	),
	POH_OUTSIDE_YANILLE_TAB(
			35, new Tile(2544, 3095, 0),
			(i1, i2) -> POH.YANILLE.isHouseLocation() && Spell.TELEPORT_TO_HOUSE.canUse(i1, i2),
			() -> Spell.TELEPORT_TO_HOUSE.cast("Outside")
	),
	POH_OUTSIDE_YANILLE(
			35, new Tile(2544, 3095, 0),
			(i1, i2) -> POH.YANILLE.isHouseLocation() && i1.stream().anyMatch(ItemFilters.nameEquals("Teleport to house")),
			() -> ItemHelper.click("Teleport to house", "Outside")
	),
	POH_OUTSIDE_PRIFDDINAS_TAB(
			35, new Tile(3239, 6076, 0),
			(i1, i2) -> POH.PRIFDDINAS.isHouseLocation() && Spell.TELEPORT_TO_HOUSE.canUse(i1, i2),
			() -> Spell.TELEPORT_TO_HOUSE.cast("Outside")
	),
	POH_OUTSIDE_PRIFDDINAS(
			35, new Tile(3239, 6076, 0),
			(i1, i2) -> POH.PRIFDDINAS.isHouseLocation() && i1.stream().anyMatch(ItemFilters.nameEquals("Teleport to house")),
			() -> ItemHelper.click("Teleport to house", "Outside")
	),

//	ARCEUUS_HOME_TELEPORT(
//			150, new Tile(1712, 3883, 0),
//			(i1, i2) -> canUseHomeTeleport() && SpellBook.getCurrentSpellBook() == SpellBook.Type.ARCEUUS,
//			() -> {
//				final Tile myPos = Players.local().tile();
//				return selectSpell(org.powbot.api.rt4.Magic.Spell.HOME_TELEPORT, "Cast") && Timing.waitCondition(() -> !Players.local().isInCombat() &&
//						!Players.local().tile().equals(myPos), 15000);
//			}
//	),
//
//	EDGEVILLE_HOME_TELEPORT(
//			150, new Tile(3087, 3496, 0),
//			(i1, i2) -> canUseHomeTeleport() && SpellBook.getCurrentSpellBook() == SpellBook.Type.ANCIENT,
//			() -> {
//				final Tile myPos = Players.local().tile();
//				return selectSpell(org.powbot.api.rt4.Magic.Spell.HOME_TELEPORT, "Cast") && Timing.waitCondition(() ->  !Players.local().isInCombat() &&
//						!Players.local().tile().equals(myPos), 15000);
//			}
//	),
//
//	LUNAR_HOME_TELEPORT(
//			150, new Tile(2095, 3913, 0),
//			(i1, i2) -> canUseHomeTeleport() && SpellBook.getCurrentSpellBook() == SpellBook.Type.LUNAR,
//			() -> {
//				final Tile myPos = Players.local().tile();
//				return selectSpell(org.powbot.api.rt4.Magic.Spell.HOME_TELEPORT, "Cast") && Timing.waitCondition(() ->  !Players.local().isInCombat() &&
//						!Players.local().tile().equals(myPos), 15000);
//			}
//	),

	;

	private int moveCost;
	private final Tile location;
	private final Requirement requirement;
	private final Action action;
	private final TeleportLimit teleportLimit;
	private final boolean requiresMembers;
	private final boolean canBeUsedInPvpWorlds;

	Teleport(int moveCost, Tile location, Requirement requirement, Action action) {
		this(moveCost, location, requirement, action, true);
	}

	Teleport(int moveCost, Tile location, Requirement requirement, Action action, boolean requiresMembers) {
		setMoveCost(moveCost);
		this.location = location;
		this.requirement = requirement;
		this.action = action;
		this.teleportLimit = TeleportConstants.LEVEL_20_WILDERNESS_LIMIT;
		this.requiresMembers = requiresMembers;
		this.canBeUsedInPvpWorlds = true;
	}

	Teleport(int moveCost, Tile location, Requirement requirement, Action action, TeleportLimit limit) {
		this(moveCost, location, requirement, action, limit, true);
	}

	Teleport(int moveCost, Tile location, Requirement requirement, Action action, TeleportLimit limit, boolean requiresMembers) {
		setMoveCost(moveCost);
		this.location = location;
		this.requirement = requirement;
		this.action = action;
		this.teleportLimit = limit;
		this.requiresMembers = requiresMembers;
		this.canBeUsedInPvpWorlds = true;
	}

	Teleport(int movecost, TeleportScrolls scroll) {
		setMoveCost(movecost);
		this.location = scroll.getLocation();
		this.requirement = (i1, i2) -> scroll.canUse();
		this.action = () -> scroll.teleportTo(false);
		this.teleportLimit = TeleportConstants.LEVEL_20_WILDERNESS_LIMIT;
		this.requiresMembers = true;
		this.canBeUsedInPvpWorlds = true;
	}

	Teleport(Grouping.MINIGAMES minigame, Tile location, String... chatOptions){
		this(minigame, location, null, chatOptions);
	}

	Teleport(Grouping.MINIGAMES minigame, Tile location, boolean requiresMembers, String... chatOptions){
		this(minigame, location, null, requiresMembers, chatOptions);
	}
	Teleport(Grouping.MINIGAMES minigame, Tile location, Requirement requirement, String... chatOptions){
		this(minigame, location, requirement, true, chatOptions);
	}
	Teleport(Grouping.MINIGAMES minigame, Tile location, Requirement requirement, boolean requiresMembers, String... chatOptions){
		setMoveCost(75);
		this.location = location;
		if(requirement != null){
			this.requirement = (i1, i2) -> canUseMinigameTeleport() && requirement.satisfies(i1, i2);
		} else{
			this.requirement = (i1, i2) -> Teleport.canUseMinigameTeleport();
		}
		this.action = () -> {
			if(Chat.canContinue() || Chat.get().size() > 0){
				Input.tap(Players.local().tile().matrix().mapPoint());
				WaitFor.milliseconds(200, 600);
			}
			if(!minigame.teleportTo()){
				return false;
			}
			if(chatOptions.length > 0){
				NPCInteraction.handleConversation(chatOptions);
			}
			return true;
		};
		this.teleportLimit = TeleportConstants.LEVEL_20_WILDERNESS_LIMIT;
		this.requiresMembers = requiresMembers;
		this.canBeUsedInPvpWorlds = false;
	}

	public int getMoveCost() {
		return moveCost;
	}

	public void setMoveCost(int cost) {
		this.moveCost=cost;
	}

	public Tile getLocation() {
		return location;
	}

	public Requirement getRequirement() {
		return requirement;
	}

	public boolean canUse(){
		return canUse(Inventory.stream().list(), Equipment.stream().list());
	}

	public boolean canUse(List<Item> inventory, List<Item> equipment){
		return getRequirement().satisfies(inventory, equipment);
	}

	public boolean requiresMembers(){
		return requiresMembers;
	}

	public boolean trigger() {
		return this.action.trigger();
	}

	public boolean isAtTeleportSpot(Tile tile) {
		return tile.distanceTo(location) < 10;
	}

	public static void setMoveCosts(int moveCost){
		Arrays.stream(values()).forEach(t -> t.setMoveCost(moveCost));
	}

	public static List<Tile> getValidStartingRSTiles(boolean membersWorld, boolean pvpWorld, List<Teleport> blacklist, List<Item> inventory, List<Item> equipment) {
		final int wildernessLevel = Combat.wildernessLevel();
		return Arrays.stream(values())
				.filter(t -> {
					if((!membersWorld && t.requiresMembers) || (pvpWorld && !t.canBeUsedInPvpWorlds))
						return false;
					return !blacklist.contains(t) && t.teleportLimit.canCast(wildernessLevel) && t.canUse(inventory, equipment);
				})
				.map(Teleport::getLocation)
				.collect(Collectors.toList());
	}

	public interface Action {
		boolean trigger();
	}


	private static boolean teleportWithScrollInterface(Predicate<Item> itemFilter, String regex){
		Item teleportItem = Inventory.stream().filter(itemFilter).first();
		if (!teleportItem.valid()) {
			teleportItem = Equipment.stream().filter(itemFilter).first();
		}

		if (!teleportItem.valid()) {
			return false;
		}

		if(!Widgets.component(TeleportConstants.SCROLL_INTERFACE_MASTER, 0).visible()){
			if (!ItemHelper.clickMatch(teleportItem, "(Rub|Teleport|" + regex + ")") ||
						!Condition.wait(() -> Widgets.component(TeleportConstants.SCROLL_INTERFACE_MASTER, 0).visible(), 250, 10)) {
				return false;
			}
		}

		return handleScrollInterface(regex);
	}

	private static boolean handleScrollInterface(String regex){
		Component box = Widgets.component(187, 3);
		if(!box.valid())
			return false;
		for(Component child : box.components()){
			String txt = child.text();
			if(StringUtils.stripHtml(txt).matches(regex)){
				return child.click();
			}
		}
		return false;
	}

	private static boolean selectSpell(Spell spell, String action){
		if(!Game.tab(Game.Tab.MAGIC)){
			return false;
		}
		return spell.cast(action);
	}

	private static boolean hasBeenToZeah(){
		return Varpbits.value(4897, true) > 0;
	}

	private static boolean canUseHomeTeleport(){
		return !Players.local().inCombat() &&
					   ((long) Varpbits.varpbit(892, true) * 60 * 1000) + (30 * 60 * 1000) < System.currentTimeMillis();
	}

	private static boolean canUseMinigameTeleport(){
		if(Varpbits.value(14022) == 1){//can't minigame teleport when we are at Duel Arena
			return false;
		}
		return !Players.local().inCombat() &&
					   ((long) Varpbits.varpbit(888, true) * 60 * 1000) + (20 * 60 * 1000) < System.currentTimeMillis();
	}
}

