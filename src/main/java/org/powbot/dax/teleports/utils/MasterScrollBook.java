package org.powbot.dax.teleports.utils;

import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class MasterScrollBook {

	public static final int
			INTERFACE_MASTER = 597, DEFAULT_VARBIT = 5685,
			SELECT_OPTION_MASTER = 219, SELECT_OPTION_CHILD = 1,
			GAMETABS_INTERFACE_MASTER = 161;
	private static Map<String, Integer> cache = new HashMap<String, Integer>();
	
	public enum Teleports {
		NARDAH(5672,"Nardah", TeleportScrolls.NARDAH.getLocation()),
		DIGSITE(5673,"Digsite", TeleportScrolls.DIGSITE.getLocation()),
		FELDIP_HILLS(5674,"Feldip Hills", TeleportScrolls.FELDIP_HILLS.getLocation()),
		LUNAR_ISLE(5675,"Lunar Isle", TeleportScrolls.LUNAR_ISLE.getLocation()),
		MORTTON(5676,"Mort'ton", TeleportScrolls.MORTTON.getLocation()),
		PEST_CONTROL(5677,"Pest Control", TeleportScrolls.PEST_CONTROL.getLocation()),
		PISCATORIS(5678,"Piscatoris", TeleportScrolls.PISCATORIS.getLocation()),
		TAI_BWO_WANNAI(5679,"Tai Bwo Wannai", TeleportScrolls.TAI_BWO_WANNAI.getLocation()),
		ELF_CAMP(5680,"Elf Camp", TeleportScrolls.ELF_CAMP.getLocation()),
		MOS_LE_HARMLESS(5681,"Mos Le'Harmless", TeleportScrolls.MOS_LE_HARMLESS.getLocation()),
		LUMBERYARD(5682,"Lumberyard", TeleportScrolls.LUMBERYARD.getLocation()),
		ZULLANDRA(5683,"Zul-Andra", TeleportScrolls.ZULLANDRA.getLocation()),
		KEY_MASTER(5684,"Key Master", TeleportScrolls.KEY_MASTER.getLocation()),
		REVENANT_CAVES(6056,"Revenant cave", TeleportScrolls.REVENANT_CAVES.getLocation()),
		WATSON(8253, "Watson", TeleportScrolls.WATSON.getLocation());
		
		private int varbit;
		private String name;
		private Tile destination;
		Teleports(int varbit, String name, Tile destination){
			this.varbit = varbit;
			this.name = name;
			this.destination = destination;
		}
		
		//Returns the number of scrolls stored in the book.
		public int getCount(){
			return Varpbits.value(varbit);
		}
		
		//Returns the name of the teleport.
		public String getName(){
			return name;
		}
		
		//Returns the destination that the teleport will take you to.
		public Tile getDestination(){
			return destination;
		}
		
		//Sets the teleport as the default left-click option of the book.
		public boolean setAsDefault(){
			if(Chat.stream().isNotEmpty()){
				String text = getDefaultTeleportText();
				if(text.contains(this.getName())){
					return Chat.stream().textContains("Yes").first().select();
				}
			}
			if(!isOpen()){
				openBook();
			}
			Component target = getInterface(this);
			return target.valid() && target.interact("Set as default") && waitForOptions() && Chat.stream().textContains("Yes").first().select();
			
		}
		
		//Uses the teleport and waits until you arrive at the destination.
		public boolean use(){
			if(this == getDefault()){
				Item book = getBook();
				return book.valid() && book.interact("Teleport") && waitTillAtDestination(this);
			}
			if(this == REVENANT_CAVES) // bug where you can't activate it from the interface for whatever reason.
				return setAsDefault() && use();
			if(!isOpen() && !openBook())
				return false;
			Component target = getInterface(this);
			return target.valid() && target.interact("Activate") && waitTillAtDestination(this);
		}
		
	}
	
	public static boolean teleport(Teleports teleport){
		return teleport != null && teleport.getCount() > 0 && teleport.use();
	}
	
	public static int getCount(Teleports teleport){
		return teleport != null ? teleport.getCount() : 0;
	}
	
	public static boolean isDefault(Teleports teleport){
		return getDefault() == teleport;
	}
	
	public static boolean setAsDefault(Teleports teleport){
		return teleport != null && teleport.setAsDefault();
	}
	
	public static Teleports getDefault(){
		int value;
		if((value = Varpbits.value(DEFAULT_VARBIT)) == 0)
			return null;
		return Teleports.values()[value-1];
	}
	
	//Removes the default left click teleport option.
	public static boolean removeDefault(){
		Item book = getBook();
		if(Inventory.selectedItem().valid()){
			Game.closeOpenTab();
			Game.tab(Game.Tab.INVENTORY);
		}
		return book.valid() && book.interact("Remove default") && waitForOptions() && Chat.stream().textContains("Yes").first().select();
	}
	
	//Caches the index and returns the RSInterface associated with the selected teleport.
	private static Component getInterface(Teleports teleport){
		if(cache.containsKey(teleport.getName())){
			return Widgets.component(INTERFACE_MASTER, cache.get(teleport.getName()));
		}
		Component comp = Components.stream(INTERFACE_MASTER).filtered(c -> {
			return c.name().contains(teleport.getName());
		}).first();
		if (comp.valid()) {
			cache.put(teleport.name, comp.index());
		}

		return comp;
	}
	
	//Returns true if the Master scroll book interface is open.
	public static boolean isOpen(){
		return Widgets.component(INTERFACE_MASTER, 0).visible();
	}
	
	//Opens the master scroll book interface.
	public static boolean openBook(){
		Item book = getBook();
		return book.valid() && book.interact("Open") && waitForBookToOpen();
	}


	public static boolean hasBook(){
		return getBook().valid();
	}

	public static boolean has(){
		return getBook().valid();
	}

	private static Item getBook(){
		return Inventory.stream().name("Master scroll book").first();
	}
	
	private static boolean waitForBookToOpen(){
		return Condition.wait(MasterScrollBook::isOpen, 250, 20);
	}
	
	private static boolean waitForOptions(){
		return Condition.wait(() -> Chat.stream().isNotEmpty(), 250, 20);
	}
	
	//Checks which scroll we are setting to default currently.
	private static String getDefaultTeleportText(){
		Component comp = Components.stream(SELECT_OPTION_MASTER, SELECT_OPTION_CHILD)
				.textContains("Set").first();
		if (comp.valid()) {
			return comp.text();
		}

		return null;
	}
	
	private static boolean waitTillAtDestination(Teleports location){
		return Condition.wait(() -> location.getDestination().distance() < 10, 800, 10);
	}
	
	
}
