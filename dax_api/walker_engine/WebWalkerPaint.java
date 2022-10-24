package dax_api.walker_engine;

import org.powbot.mobile.drawing.Rendering;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import dax_api.walker_engine.real_time_collision.RealTimeCollisionTile;
import org.powbot.api.Point;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class WebWalkerPaint {

    private final int REGION_SIZE = 104, TILE_WIDTH = 4;
    private final BufferedImage nonDisplayableMapImage, mapDisplay;
//    private final Rendering2D nonDisplayableMapImageRendering, mapRenderingDisplay;

    private final Point mapCenter;
    private final ExecutorService service;
    private RSTile playerPosition;
    private int lastChange;


    private static WebWalkerPaint instance;

    private WebWalkerPaint(){
        nonDisplayableMapImage = new BufferedImage(REGION_SIZE * TILE_WIDTH, REGION_SIZE * TILE_WIDTH, BufferedImage.TYPE_INT_ARGB);
        mapDisplay = new BufferedImage(REGION_SIZE * TILE_WIDTH, REGION_SIZE * TILE_WIDTH, BufferedImage.TYPE_INT_ARGB);
//        nonDisplayableMapImageRendering = nonDisplayableMapImage.createRendering();
//        mapRenderingDisplay = mapDisplay.createRendering();
        mapCenter = new Point(641, 83);
        service = Executors.newSingleThreadExecutor();
        lastChange = -1;
    }

    public static WebWalkerPaint getInstance(){
        return instance != null ? instance : (instance = new WebWalkerPaint());
    }

    public void drawDebug(Rendering Rendering) {
        drawDebug(Rendering, true);
    }

    /**
     *
     * @param Rendering Rendering variable from on paint method
     * @param drawMap if you want to draw the map or not.
     */
    public void drawDebug(Rendering Rendering, boolean drawMap) {
        if (!WalkerEngine.getInstance().isNavigating()){
            return;
        }
        if (playerPosition == null || !playerPosition.equals(Player.getPosition()) || lastChange != RealTimeCollisionTile.getAllInitialized().size()) {
            lastChange = RealTimeCollisionTile.getAllInitialized().size();
            playerPosition = Player.getPosition();
            final int playerX = playerPosition.getX(), playerY = playerPosition.getY();
            service.submit(() -> {
//                nonDisplayableMapImageRendering.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
//                nonDisplayableMapImageRendering.fillRect(0, 0, REGION_SIZE * TILE_WIDTH, REGION_SIZE * TILE_WIDTH);
//                nonDisplayableMapImageRendering.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
//
//                nonDisplayableMapImageRendering.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

                int previousLocalX = -1, previousLocalY = -1;
                List<RSTile> path = WalkerEngine.getInstance().getCurrentPath();
                if (path != null) {
                    for (RSTile node : path) {
                        int relativeX = node.getX() - playerX, relativeY = playerY - node.getY();
                        int localX = (relativeX + REGION_SIZE / 2) * TILE_WIDTH, localY = (relativeY + REGION_SIZE / 2) * TILE_WIDTH;

//                    nonDisplayableMapImageRendering.fillRect(localX, localY, TILE_WIDTH, TILE_WIDTH);

                        if (previousLocalX == -1) {
                            previousLocalX = localX;
                            previousLocalY = localY;
                            continue;
                        }

                        switch (node.getPlane()){
                            case 1:
//                                nonDisplayableMapImageRendering.setColor(new Color(0, 224, 255));
                                break;
                            case 2:
//                                nonDisplayableMapImageRendering.setColor(new Color(255, 115, 166));
                                break;
                            default:
//                                nonDisplayableMapImageRendering.setColor(new Color(0, 255, 23));
                        }

                        if (new Point(previousLocalX, previousLocalY).distance(new Point(localX, localY)) > 20){
//                            nonDisplayableMapImageRendering.setColor(new Color(233, 255, 224, 120));
                        }

//                        nonDisplayableMapImageRendering.drawLine(previousLocalX + TILE_WIDTH / 2, previousLocalY + TILE_WIDTH / 2, localX + TILE_WIDTH / 2, localY + TILE_WIDTH / 2);
                        previousLocalX = localX;
                        previousLocalY = localY;
                    }
                }
                if (drawMap) {
                    for (RealTimeCollisionTile realTimeCollisionTile : RealTimeCollisionTile.getAllInitialized()) {
                        int relativeX = realTimeCollisionTile.getX() - playerX, relativeY = playerY - realTimeCollisionTile.getY();

                        int localX = (relativeX + REGION_SIZE / 2) * TILE_WIDTH, localY = (relativeY + REGION_SIZE / 2) * TILE_WIDTH;

//                        nonDisplayableMapImageRendering.setColor(new Color(0, 0, 0, 32));
//                        nonDisplayableMapImageRendering.drawRect(localX, localY, TILE_WIDTH, TILE_WIDTH);

//                        nonDisplayableMapImageRendering.setColor(new Color(255, 255, 255, 47));
//                        nonDisplayableMapImageRendering.fillRect(localX, localY, TILE_WIDTH, TILE_WIDTH);
//
//                        if (!realTimeCollisionTile.isWalkable()) {
//                            nonDisplayableMapImageRendering.setColor(new Color(255, 170, 4, 161));
//                            nonDisplayableMapImageRendering.fillRect(localX, localY, TILE_WIDTH, TILE_WIDTH);
//                        }
//
//                        RealTimeCollisionTile furthestReachable = PathAnalyzer.furthestReachable;
//                        if (furthestReachable != null && furthestReachable.equals(realTimeCollisionTile)) {
//                            nonDisplayableMapImageRendering.setColor(new Color(0, 183, 255, 255));
//                            nonDisplayableMapImageRendering.fillRect(localX, localY, TILE_WIDTH, TILE_WIDTH);
//                        }
//
//                        if (playerX == realTimeCollisionTile.getX() && playerY == realTimeCollisionTile.getY()) {
//                            nonDisplayableMapImageRendering.setColor(new Color(255, 8, 0, 161));
//                            nonDisplayableMapImageRendering.fillRect(localX, localY, TILE_WIDTH, TILE_WIDTH);
//
//
//                            RealTimeCollisionTile cache = PathAnalyzer.closestToPlayer;
//                            if (cache != null) {
//                                int relativeXToPlayer = cache.getX() - playerX, relativeYToPlayer = playerY - cache.getY();
//                                int localXToPlayer = (relativeXToPlayer + REGION_SIZE / 2) * TILE_WIDTH, localYToPlayer = (relativeYToPlayer + REGION_SIZE / 2) * TILE_WIDTH;
//                                nonDisplayableMapImageRendering.drawLine(localXToPlayer, localYToPlayer, localX + TILE_WIDTH / 2, localY + TILE_WIDTH / 2);
//                            }
//
//                        }
//
//                        try {
//                            if (NodeInfo.get(realTimeCollisionTile).traversed) {
//                                nonDisplayableMapImageRendering.setColor(new Color(237, 98, 255, 161));
//                                nonDisplayableMapImageRendering.fillRect(localX, localY, TILE_WIDTH, TILE_WIDTH);
//                            }
//                        } catch (Exception e) {
//
//                        }
//
//                        nonDisplayableMapImageRendering.setColor(new Color(255, 254, 253, 223));
//                        if (realTimeCollisionTile.blockedNorth()) {
//                            nonDisplayableMapImageRendering.fillRect(localX, localY, TILE_WIDTH, TILE_WIDTH / 4);
//                        }
//                        if (realTimeCollisionTile.blockedEast()) {
//                            nonDisplayableMapImageRendering.fillRect(localX + TILE_WIDTH - TILE_WIDTH / 4, localY, TILE_WIDTH / 4, TILE_WIDTH);
//                        }
//                        if (realTimeCollisionTile.blockedSouth()) {
//                            nonDisplayableMapImageRendering.fillRect(localX, localY + TILE_WIDTH - TILE_WIDTH / 4, TILE_WIDTH, TILE_WIDTH / 4);
//                        }
//                        if (realTimeCollisionTile.blockedWest()) {
//                            nonDisplayableMapImageRendering.fillRect(localX, localY, TILE_WIDTH / 4, TILE_WIDTH);
//                        }

                    }
                }
//                mapRenderingDisplay.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
//                mapRenderingDisplay.fillRect(0, 0, REGION_SIZE * TILE_WIDTH, REGION_SIZE * TILE_WIDTH);
//                mapRenderingDisplay.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
//
//                mapRenderingDisplay.drawImage(nonDisplayableMapImage, 0, 0, null);
            });
        }
//        Rendering2D Rendering2D = (Rendering2D) Rendering;
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(Camera.getCameraRotation()), mapCenter.getX() - (REGION_SIZE/2 * TILE_WIDTH) + (REGION_SIZE/2 * TILE_WIDTH) + TILE_WIDTH/2, mapCenter.getY() -(REGION_SIZE/2 * TILE_WIDTH) + (REGION_SIZE/2 * TILE_WIDTH) + TILE_WIDTH/2);
//        affineTransform.rotate(Game.getMinimapRotation(), mapCenter.x - (REGION_SIZE/2 * TILE_WIDTH) + (REGION_SIZE/2 * TILE_WIDTH) + TILE_WIDTH/2, mapCenter.y -(REGION_SIZE/2 * TILE_WIDTH) + (REGION_SIZE/2 * TILE_WIDTH) + TILE_WIDTH/2);
        affineTransform.translate(mapCenter.getX() - (REGION_SIZE/2 * TILE_WIDTH), mapCenter.getY() -(REGION_SIZE/2 * TILE_WIDTH));
//        Rendering2D.drawImage(mapDisplay, affineTransform, null);
    }

}
