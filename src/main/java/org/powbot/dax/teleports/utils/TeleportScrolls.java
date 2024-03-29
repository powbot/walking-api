package org.powbot.dax.teleports.utils;

import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Chat;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;
import org.powbot.api.rt4.Players;
import org.powbot.dax.shared.helpers.ItemHelper;

public enum TeleportScrolls {
	NARDAH("Nardah teleport",new Tile(3419, 2916, 0)),
	DIGSITE("Digsite teleport",new Tile(3325, 3411, 0)),
	FELDIP_HILLS("Feldip hills teleport",new Tile(2540, 2924, 0)),
	LUNAR_ISLE("Lunar isle teleport",new Tile(2095, 3913, 0)),
	MORTTON("Mort'ton teleport",new Tile(3487, 3287, 0)),
	PEST_CONTROL("Pest control teleport",new Tile(2658, 2658, 0)),
	PISCATORIS("Piscatoris teleport",new Tile(2340, 3650, 0)),
	TAI_BWO_WANNAI("Tai bwo wannai teleport",new Tile(2789,3065,0)),
	ELF_CAMP("Elf camp teleport",new Tile(2193, 3258, 0)),
	MOS_LE_HARMLESS("Mos le'harmless teleport", new Tile(3700, 2996, 0)),
	LUMBERYARD("Lumberyard teleport",new Tile(3302, 3487, 0)),
	ZULLANDRA("Zul-andra teleport",new Tile(2195, 3055, 0)),
	KEY_MASTER("Key master teleport",new Tile(1311, 1251, 0)),
	REVENANT_CAVES("Revenant cave teleport",new Tile(3130, 3832, 0)),
	WATSON("Watson teleport", new Tile(1645, 3579,0))
	;
	private String name;
	private Tile location;
	TeleportScrolls(String name, Tile location){
		this.name = name;
		this.location = location;
	}

	public int getX(){
		return location.getX();
	}
	public int getY(){
		return location.getY();
	}
	public int getZ(){
		return location.floor();
	}

	public boolean teleportTo(boolean shouldWait){
		Item scroll = Inventory.stream().name(name).first();
		if(!ItemHelper.click(scroll, "Teleport"))
			return false;
		if(this == REVENANT_CAVES && Condition.wait(() -> Chat.get().size() > 0, 250, 10)){
			if(!Chat.continueChat("Yes, teleport me now.")){
				return false;
			}
		}
		return (!shouldWait || Condition.wait(() -> this.location.distanceTo(Players.local().tile()) < 15, 500, 16));
	}
	
	public boolean hasScroll(){
		return Inventory.stream().name(this.name).isNotEmpty();
	}

	public Tile getLocation(){
		return location;
	}

	public boolean canUse(){
		return this.hasScroll() || (MasterScrollBook.hasBook() && this.scrollbookContains());
	}

	public boolean scrollbookContains(){
		if(!MasterScrollBook.has())
			return false;
		switch(this){

			case NARDAH:
				return MasterScrollBook.Teleports.NARDAH.getCount() > 0;
			case DIGSITE:
				return MasterScrollBook.Teleports.DIGSITE.getCount() > 0;
			case FELDIP_HILLS:
				return MasterScrollBook.Teleports.FELDIP_HILLS.getCount() > 0;
			case LUNAR_ISLE:
				return MasterScrollBook.Teleports.LUNAR_ISLE.getCount() > 0;
			case MORTTON:
				return MasterScrollBook.Teleports.MORTTON.getCount() > 0;
			case PEST_CONTROL:
				return MasterScrollBook.Teleports.PEST_CONTROL.getCount() > 0;
			case PISCATORIS:
				return MasterScrollBook.Teleports.PISCATORIS.getCount() > 0;
			case TAI_BWO_WANNAI:
				return MasterScrollBook.Teleports.TAI_BWO_WANNAI.getCount() > 0;
			case ELF_CAMP:
				return MasterScrollBook.Teleports.ELF_CAMP.getCount() > 0;
			case MOS_LE_HARMLESS:
				return MasterScrollBook.Teleports.MOS_LE_HARMLESS.getCount() > 0;
			case LUMBERYARD:
				return MasterScrollBook.Teleports.LUMBERYARD.getCount() > 0;
			case ZULLANDRA:
				return MasterScrollBook.Teleports.ZULLANDRA.getCount() > 0;
			case KEY_MASTER:
				return MasterScrollBook.Teleports.KEY_MASTER.getCount() > 0;
			case REVENANT_CAVES:
				return MasterScrollBook.Teleports.REVENANT_CAVES.getCount() > 0;
		}
		return false;
	}

}
