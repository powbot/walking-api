package org.powbot.dax.engine.collision;


import org.powbot.api.Tile;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.Movement;
import org.powbot.api.rt4.Players;
import org.powbot.util.TransientGetter2D;

public class CollisionDataCollector {

    public static void generateRealTimeCollision(){
        RealTimeCollisionTile.clearMemory();

        Tile playerPosition = Players.local().tile();
        TransientGetter2D<Integer> collisionData = Movement.collisionMap(Game.floor()).flags();

        Tile mapOffset = Game.mapOffset();
        for (int i = 0; i < collisionData.getSize(); i++) {
            for (int j = 0; j < collisionData.get(i).getSize(); j++) {
                Tile worldTile = new Tile(mapOffset.x() + i, mapOffset.y() + j, playerPosition.floor());
                RealTimeCollisionTile.create(worldTile.getX(), worldTile.getY(), worldTile.floor(), collisionData.get(i).get(j));
            }
        }
    }

    public static void updateRealTimeCollision(){
        Tile playerPosition = Players.local().tile();
        TransientGetter2D<Integer> collisionData = Movement.collisionMap(Game.floor()).flags();

        Tile mapOffset = Game.mapOffset();
        for (int i = 0; i < collisionData.getSize(); i++) {
            for (int j = 0; j < collisionData.get(i).getSize(); j++) {
                Tile worldTile = new Tile(mapOffset.x() + i, mapOffset.y() + j, playerPosition.floor());
                RealTimeCollisionTile realTimeCollisionTile = RealTimeCollisionTile.get(worldTile.getX(), worldTile.getY(), worldTile.floor());
                if (realTimeCollisionTile != null){
                    realTimeCollisionTile.setCollisionData(collisionData.get(i).get(j));
                } else {
                    RealTimeCollisionTile.create(worldTile.getX(), worldTile.getY(), worldTile.floor(), collisionData.get(i).get(j));
                }
            }
        }
    }

}
