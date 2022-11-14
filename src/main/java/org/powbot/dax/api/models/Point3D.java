package org.powbot.dax.api.models;

//import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.powbot.api.Tile;

//@DoNotRename
public class Point3D {


    //@DoNotRename
    private int x, y, z;

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public JsonElement toJson() {
        return new Gson().toJsonTree(this);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public Tile toTile() {
        return new Tile(x, y, z);
    }

    public static Point3D fromTile(Tile tile) {
        return new Point3D(tile.getX(), tile.getY(), tile.floor());
    }

}
