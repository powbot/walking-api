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

public class ZeahMineCarts {

	public static int MINECART_WIDGET = -1;

	public enum Location {
		ARCEUUS("Arceuus", 1670, 3833, 0),
		FARMING_GUILD("Farming Guild", 1218, 3737, 0),
		HOSIDIUS_SOUTH("Hosidius South", 1808, 3479, 0),
		HOSIDIUS_WEST("Hosidius West", 1655, 3543, 0),
		KINGSTOWN("Kingstown", 1699, 3660, 0),
		KOUREND_WOODLAND("Kourend Woodland", 1572, 3466, 0),
		LOVAKENGJ("Lovakengj", 1518, 3733, 0),
		MOUNT_QUIDAMORTEM("Mount Quidamortem", 1255, 3548, 0),
		NORTHERN_TUNDRAS("Northern Tundras", 1648, 3931, 0),
		PORT_PISCARILIUS("Port Piscarilius", 1761, 3710, 0),
		SHAYZIEN_EAST("Shayzien East", 1590, 3620, 0),
		SHAYZIEN_WEST("Shayzien West", 1415, 3577, 0)
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
		if (!Widgets.component(MINECART_WIDGET, 0).visible()
					&& !InteractionHelper.click(InteractionHelper.getGameObject(Filters.Objects.actionsContains("Travel")), "Travel", () -> Components.stream(MINECART_WIDGET).anyMatch(Component::valid) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
			return false;
		}

		Component option = Components.stream(MINECART_WIDGET).text(location.getName()).findAny().orElse(null);

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
