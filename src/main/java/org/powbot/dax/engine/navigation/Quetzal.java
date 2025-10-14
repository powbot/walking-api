package org.powbot.dax.engine.navigation;

import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Component;
import org.powbot.api.rt4.Components;
import org.powbot.api.rt4.Players;
import org.powbot.api.rt4.Widgets;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.interaction.InteractionHelper;
import org.powbot.dax.shared.helpers.Filters;

public class Quetzal {

	public static final int QUETZAL_WIDGET_ROOT = 874;

	public enum Location {
		ALDARIN("Aldarin", 1390, 2901, 0),
		AUBURNVALE("Auburnvale", 1411, 3361, 0),
		CAM_TORUM_ENTRANCE("Cam Torum Entrance", 1446, 3108, 0),
		CIVITAS_ILLA_FORTIS("Civitas illa Fortis", 1696, 3140, 0),
		COLOSSAL_WYRM_REMAINS("Colossal Wyrm Remains", 1670, 2934, 0),
		//FORTIS_COLOSSEUM("Fortis Colosseum", ),
		OUTER_FORTIS("Outer Fortis", 1700, 3035, 0),
		QUETZACALLI_GORGE("Quetzacalli Gorge", 1510, 3222, 0),
		HUNTER_GUILD("Hunter Guild", 1585, 3053, 0),
		SALVAGER_OUTLOOK("Salvager Outlook", 1614, 3300, 0),
		SUNSET_COAST("Sunset Coast", 1548, 2995, 0),
		TAL_TEKLAN("Tal Teklan", 1226, 3091, 0),
		THE_TEOMAT("The Teomat", 1437, 3171, 0),
		;

		private int x, y, z;
		private String name;
		Location(String name, int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
			this.name = name;
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
	}

	public static boolean to(Location location){
		if (!Widgets.component(QUETZAL_WIDGET_ROOT, 0).visible()
					&& !InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.nameEquals("Renu")), "Travel", () -> Components.stream(QUETZAL_WIDGET_ROOT).anyMatch(Component::valid) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
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
}
