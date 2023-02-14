package org.powbot.dax.teleports.utils;

import org.powbot.api.rt4.Varpbits;

public enum POH {
	RIMMINGTON(1),
	TAVERLY(2),
	POLLNIVNEACH(3),
	RELLEKKA(4),
	BRIMHAVEN(5),
	YANILLE(6),
	HOSIDIUS(8),
	PRIFDDINAS(9)
	;
	int value;
	POH(int value){
		this.value = value;
	}
	private static final int
			HOUSE_LOCATION_VARBIT = 2187;

	public boolean isHouseLocation(){
		return Varpbits.value(HOUSE_LOCATION_VARBIT) == this.value;
	}
}
