package org.powbot.dax.api.models;

import org.powbot.api.rt4.Item;

import java.util.List;

//@DoNotRename
public interface Requirement {
	boolean satisfies(List<Item> inventory, List<Item> equipment);
}