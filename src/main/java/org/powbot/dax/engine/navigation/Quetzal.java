package org.powbot.dax.engine.navigation;

import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.interaction.InteractionHelper;
import org.powbot.dax.shared.helpers.Filters;
import org.powbot.dax.shared.helpers.ItemHelper;
import org.powbot.dax.teleports.utils.ItemFilters;

public class Quetzal {

	public static final int
			QUETZAL_WIDGET_ROOT = 874,
			WHISTLE_WIDGET_ROOT = 949,
			WHISTLE_WIDGET_CHILD = 12,
			LAST_LOCATION_VARBIT = 9950;

	private int currentCharges;

	private static Quetzal instance;

	private Quetzal(){
		currentCharges = 10;
	}
	public static Quetzal getInstance(){
		return instance != null ? instance : (instance = new Quetzal());
	}

	public int getCurrentCharges() {
		return currentCharges;
	}

	public void setCurrentCharges(int currentCharges) {
		this.currentCharges = currentCharges;
	}

	public enum Location {
		ALDARIN("Aldarin", 1390, 2901, 0, 11377, 9),
		AUBURNVALE("Auburnvale", 1411, 3361, 0, 17756, 13),
		CAM_TORUM_ENTRANCE("Cam Torum Entrance", 1446, 3108, 0, 9955, 5),
		CIVITAS_ILLA_FORTIS("Civitas illa Fortis", 1696, 3140, 0, 9951, 1),
		COLOSSAL_WYRM_REMAINS("Colossal Wyrm Remains", 1670, 2934, 0, 9956, 6),
		FORTIS_COLOSSEUM("Fortis Colosseum", 1779, 3111, 0, 9958, 8),
		HUNTER_GUILD("Hunter Guild", 1585, 3053, 0, 9954, 4),
		KASTORI("Kastori", 1344, 3022, 0, 17757, 14),
		OUTER_FORTIS("Outer Fortis", 1700, 3035, 0, 9957, 7),
		QUETZACALLI_GORGE("Quetzacalli Gorge", 1510, 3222, 0, 11378, 10),
		SALVAGER_OVERLOOK("Salvager Overlook", 1614, 3300, 0, 11379, 11),
		SUNSET_COAST("Sunset Coast", 1548, 2995, 0, 9953, 3),
		TAL_TEKLAN("Tal Teklan", 1226, 3091, 0, 17755, 12),
		THE_TEOMAT("The Teomat", 1437, 3171, 0, 9952, 2),
		;

		private int x, y, z, lastLocationValue;
		private String name;
		Location(String name, int x, int y, int z, int constructedVarbit, int lastLocationVarbit){
			this.x = x;
			this.y = y;
			this.z = z;
			this.name = name;
			this.lastLocationValue = lastLocationVarbit;
		}
		public String getName() {
			return name;
		}
		public Tile getTile(){
			return new Tile(x, y, z);
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}
		public int getLastLocationValue() {
			return lastLocationValue;
		}
	}

	public static boolean to(Location location){
		if (!Widgets.component(QUETZAL_WIDGET_ROOT, 0).visible()
					&& !InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.nameEquals("Renu")), "Travel", () -> Widgets.component(QUETZAL_WIDGET_ROOT, 0).visible() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
			return false;
		}

		Component option = Components.stream(QUETZAL_WIDGET_ROOT).action(location.getName()).findAny().orElse(null);

		if (option == null){
			return false;
		}

		if (!option.click()){
			return false;
		}

		if (WaitFor.condition(Random.nextInt(5400, 6500), () -> location.getTile().distanceTo(Players.local().tile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
			WaitFor.milliseconds(250, 500);
			return true;
		}
		return false;
	}

	public static boolean teleport(Location location){
		int lastLocation = Varpbits.value(LAST_LOCATION_VARBIT);
		if(lastLocation != location.getLastLocationValue()){
			if (!Widgets.component(WHISTLE_WIDGET_ROOT, WHISTLE_WIDGET_CHILD).visible() &&
						!ItemHelper.click(ItemFilters.nameContains("quetzal whistle"), "Signal") &&
						WaitFor.condition(4500, () -> Components.stream(WHISTLE_WIDGET_ROOT).anyMatch(Component::valid) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
				return false;
			}
			Component option = Components.stream(WHISTLE_WIDGET_ROOT, WHISTLE_WIDGET_CHILD).action(location.getName()).findAny().orElse(null);
			if (option == null){
				return false;
			}

			if (!option.click()){
				return false;
			}
		} else if(!ItemHelper.click(ItemFilters.nameContains("quetzal whistle"), "Last-destination")){
			return false;
		}
		if (WaitFor.condition(Random.nextInt(5400, 6500), () -> location.getTile().distanceTo(Players.local().tile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
			WaitFor.milliseconds(250, 500);
			return true;
		}
		return false;
	}
}