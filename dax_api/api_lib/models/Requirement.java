package dax_api.api_lib.models;

//import com.allatori.annotations.DoNotRename;

import org.tribot.api2007.types.RSItem;

//@DoNotRename
public interface Requirement {
	boolean satisfies(RSItem[] inventory, RSItem[] equipment);
}