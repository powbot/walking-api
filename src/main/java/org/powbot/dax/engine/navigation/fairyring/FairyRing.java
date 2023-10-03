package org.powbot.dax.engine.navigation.fairyring;

import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.interaction.InteractionHelper;
import org.powbot.dax.engine.navigation.fairyring.letters.FirstLetter;
import org.powbot.dax.engine.navigation.fairyring.letters.SecondLetter;
import org.powbot.dax.engine.navigation.fairyring.letters.ThirdLetter;

import static org.powbot.dax.engine.navigation.fairyring.letters.FirstLetter.*;
import static org.powbot.dax.engine.navigation.fairyring.letters.SecondLetter.*;
import static org.powbot.dax.engine.navigation.fairyring.letters.ThirdLetter.*;

public class FairyRing {

	public static final int
		INTERFACE_MASTER = 398,
		TELEPORT_CHILD = 26,
		ELITE_DIARY_VARBIT = 4538;
	private static final int[]
			DRAMEN_STAFFS = {772,9084};

	private static GameObject ring;


	private static Component getTeleportButton() {
		return Widgets.component(INTERFACE_MASTER, TELEPORT_CHILD);
	}

	public static boolean takeFairyRing(Locations location){

		if(location == null)
			return false;
		if (Varpbits.value(ELITE_DIARY_VARBIT) == 0 && Equipment.stream().id(DRAMEN_STAFFS).isEmpty()){
			if (!InteractionHelper.click(Inventory.stream().id(DRAMEN_STAFFS).first(), "Wield")){
				return false;
			}
		}
		handleSpecialCases();
		final Tile myPos = Players.local().tile();
		if(location == Locations.ZANARIS){
			return InteractionHelper.click(ring,"Zanaris") &&
						   Condition.wait(() -> myPos.distanceTo(Players.local().tile()) > 20,800, 10);
		}
		if(!hasInterface()){
			if(hasCachedLocation(location)){
				return takeLastDestination(location) && WaitFor.milliseconds(500, 1200) != null;
			} else if(!openFairyRing()){
				return false;
			}
		}
		return location.turnTo() && pressTeleport() && Condition.wait(() -> myPos.distanceTo(Players.local().tile()) > 20, 800, 10) && WaitFor.milliseconds(500, 1200) != null;
	}

	private static boolean hasInterface(){
		return Widgets.component(INTERFACE_MASTER, 0).visible();
	}

	private static boolean hasCachedLocation(Locations location){
		ring = Objects.stream(25).name("Fairy ring").nearest().first();
		return ring.valid() && ring.actions().stream().anyMatch(a -> a.contains(location.toString()));
	}

	private static boolean takeLastDestination(Locations location){
		final Tile myPos = Players.local().tile();
		return InteractionHelper.click(ring,"Last-destination (" + location + ")") &&
				Condition.wait(() -> myPos.distanceTo(Players.local().tile()) > 20,800, 10);
	}

	private static boolean pressTeleport(){
		Component iface = getTeleportButton();
		return iface.valid() && iface.click();
	}

	private static boolean openFairyRing(){
		return ring.valid() && InteractionHelper.click(ring,"Configure") &&
				Condition.wait(() -> Widgets.component(INTERFACE_MASTER, 0).visible(),1000, 10);
	}

	public enum Locations {
		ABYSSAL_AREA(A, L, R),
		ABYSSAL_NEXUS(D, I, P),
		APE_ATOLL(C, L, R),
		ARCEUUS_LIBRARY(C, I, S),
		ARDOUGNE_ZOO(B, I, S),
		CANIFIS(C, K, S),
		CHASM_OF_FIRE(D, J, R),
		COSMIC_ENTITYS_PLANE(C, K, P),
		DORGESH_KAAN_SOUTHERN_CAVE(A, J, Q),
		DRAYNOR_VILLAGE_ISLAND(C, L, P),
		EDGEVILLE(D, K, R),
		ENCHANTED_VALLEY(B, K, Q),
		FELDIP_HILLS_HUNTER_AREA(A, K, S),
		FISHER_KINGS_REALM(B, J, R),
		GORAKS_PLANE(D, I, R),
		HAUNTED_WOODS(A, L, Q),
		HAZELMERE(C, L, S),
		ISLAND_SOUTHEAST_ARDOUGNE(A, I, R),
		KALPHITE_HIVE(B, I, Q),
		KARAMJA_KARAMBWAN_SPOT(D, K, P),
		LEGENDS_GUILD(B, L, R),
		LIGHTHOUSE(A, L, P),
		MCGRUBOR_WOODS(A, L, S),
		MISCELLANIA(C, I, P),
		MISCELLANIA_PENGUINS(A, J, S),
		MORT_MYRE_ISLAND(B, I, P),
		MORT_MYRE_SWAMP(B, K, R),
		MOUNT_KARUULM(C, I, R),
		MUDSKIPPER_POINT(A, I, Q),
		MYREQUE_HIDEOUT(D, L, S),
		NORTH_OF_NARDAH(D, L, Q),
		PISCATORIS_HUNTER_AREA(A, K, Q),
		POH(D, I, Q),
		POISON_WASTE(D, L, R),
		POLAR_HUNTER_AREA(D, K, S),
		RELLEKKA_SLAYER_CAVE(A, J, R),
		SHILO_VILLAGE(C, K, R),
		SINCLAIR_MANSION(C, J, R),
		SOUTH_CASTLE_WARS(B, K, P),
		STRANGLEWOOD(B, L, S),
		TOWER_OF_LIFE(D, J, P),
		TZHAAR(B, L, P),
		WIZARDS_TOWER(D, I, S),
		YANILLE(C, I, Q),
		ZANARIS(B, K, S),
		ZUL_ANDRA(B, J, S);

		FirstLetter first;
		SecondLetter second;
		ThirdLetter third;

		Locations(FirstLetter first, SecondLetter second, ThirdLetter third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}

		public boolean turnTo() {
			return first.turnTo() && WaitFor.milliseconds(400, 800) != null &&
					second.turnTo() && WaitFor.milliseconds(400, 800) != null &&
					third.turnTo() && WaitFor.milliseconds(400, 800) != null;
		}

		@Override
		public String toString() {
			return "" + first + second + third;
		}
	}

	private static final Tile SINCLAIR_TILE = new Tile(2705, 3576, 0);

	private static void handleSpecialCases(){
		Tile myPos = Players.local().tile();
		if(myPos.distanceTo(SINCLAIR_TILE) < 8 && Players.local().inCombat() &&
				Npcs.stream().name("Wolf").interactingWithMe().within(3).isNotEmpty()) {
			if(myPos.getY() >= 3577){
				Movement.step(SINCLAIR_TILE.derive(-1, 0));
			} else {
				Movement.step(SINCLAIR_TILE.derive(0, 1));
			}
		}
	}
}
