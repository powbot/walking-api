package org.powbot.dax.api;

import org.powbot.api.Locatable;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.Objects;
import org.powbot.api.rt4.walking.FailureReason;
import org.powbot.api.rt4.walking.WebWalkingResult;
import org.powbot.dax.api.models.*;
import org.powbot.dax.engine.Loggable;
import org.powbot.dax.engine.WaitFor;
import org.powbot.dax.engine.WalkerEngine;
import org.powbot.dax.engine.WalkingCondition;
import org.powbot.dax.engine.navigation.ShipUtils;
import org.powbot.dax.engine.navigation.fairyring.FairyRing;
import org.powbot.dax.teleports.Teleport;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DaxWalker implements Loggable {

    private static Map<Tile, Teleport> map;
    private static DaxWalker daxWalker;

    public static DaxWalker getInstance() {
        return daxWalker != null ? daxWalker : (daxWalker = new DaxWalker());
    }

    public boolean useRun = true;

    private WalkingCondition globalWalkingCondition;

    private DaxWalker() {
        globalWalkingCondition = () -> WalkingCondition.State.CONTINUE_WALKER;

        map = new ConcurrentHashMap<>();
        for (Teleport teleport : Teleport.values()) {
            map.put(teleport.getLocation(), teleport);
        }
        blacklist.addAll(Arrays.asList(
                Teleport.NARDAH_TELEPORT, Teleport.DIGSITE_TELEPORT, Teleport.FELDIP_HILLS_TELEPORT,
                Teleport.LUNAR_ISLE_TELEPORT, Teleport.MORTTON_TELEPORT, Teleport.PEST_CONTROL_TELEPORT, Teleport.TAI_BWO_WANNAI_TELEPORT,
                Teleport.ELF_CAMP_TELEPORT, Teleport.MOS_LE_HARMLESS_TELEPORT, Teleport.LUMBERYARD_TELEPORT, Teleport.ZULLANDRA_TELEPORT,
                Teleport.KEY_MASTER_TELEPORT, Teleport.REVENANT_CAVES_TELEPORT, Teleport.WATSON_TELEPORT,
                Teleport.BURNING_AMULET_BANDIT_CAMP, Teleport.BURNING_AMULET_CHAOS_TEMPLE, Teleport.BURNING_AMULET_LAVA_MAZE,
                Teleport.ECTOPHIAL, Teleport.LLETYA, Teleport.XERICS_GLADE, Teleport.XERICS_INFERNO, Teleport.XERICS_LOOKOUT,
                Teleport.WEST_ARDOUGNE_TELEPORT_TAB, Teleport.RADAS_BLESSING_KOUREND_WOODLAND, Teleport.RADAS_BLESSING_MOUNT_KARUULM,
                Teleport.CRAFTING_CAPE_TELEPORT, Teleport.CABBAGE_PATCH_TELEPORT, Teleport.LEGENDS_GUILD_TELEPORT,
                Teleport.RIMMINGTON_TELEPORT_TAB, Teleport.TAVERLEY_TELEPORT_TAB, Teleport.RELLEKKA_TELEPORT_TAB, Teleport.BRIMHAVEN_TELEPORT_TAB,
                Teleport.POLLNIVNEACH_TELEPORT_TAB, Teleport.YANILLE_TELEPORT_TAB, Teleport.HOSIDIUS_TELEPORT_TAB, Teleport.CONSTRUCTION_CAPE_RELLEKKA,
                Teleport.CONSTRUCTION_CAPE_BRIMHAVEN, Teleport.CONSTRUCTION_CAPE_HOSIDIUS, Teleport.CONSTRUCTION_CAPE_RIMMINGTON,
                Teleport.CONSTRUCTION_CAPE_TAVERLEY, Teleport.CONSTRUCTION_CAPE_POLLNIVNEACH, Teleport.CONSTRUCTION_CAPE_YANILLE,
                Teleport.SLAYER_RING_MORYTANIA, Teleport.SLAYER_RING_GNOME_STRONGHOLD, Teleport.SLAYER_RING_RELLEKKA_CAVE,
                Teleport.SALVE_GRAVEYARD_TAB, Teleport.FENKENSTRAINS_CASTLE_TAB, Teleport.BARROWS_TAB, Teleport.ARCEUUS_LIBRARY_TAB,
                Teleport.BATTLEFRONT_TAB, Teleport.DRAYNOR_MANOR_TAB, Teleport.MIND_ALTAR_TAB, Teleport.ENCHANTED_LYRE_RELLEKA,
                Teleport.FARMING_CAPE_TELEPORT, Teleport.ROYAL_SEED_POD, Teleport.DRAKANS_MEDALLION_VER_SINHAZA, Teleport.DRAKANS_MEDALLION_DARKMEYER,
                Teleport.OURANIA_TELEPORT_TAB, Teleport.WATERBIRTH_TELEPORT_TAB, Teleport.BARBARIAN_OUTPOST_TELEPORT_TAB,
                Teleport.KHAZARD_TELEPORT_TAB, Teleport.FISHING_GUILD_TELEPORT_TAB, Teleport.CATHERBY_TELEPORT_TAB,
                Teleport.CASTLE_WARS_MINIGAME, Teleport.CLAN_WARS_MINIGAME,
                Teleport.POH_OUTSIDE_RIMMINGTON, Teleport.POH_OUTSIDE_RIMMINGTON_TAB,
                Teleport.POH_OUTSIDE_TAVERLY, Teleport.POH_OUTSIDE_TAVERLY_TAB,
                Teleport.POH_OUTSIDE_HOSIDIUS, Teleport.POH_OUTSIDE_HOSIDIUS_TAB,
                Teleport.POH_OUTSIDE_POLLNIVNEACH, Teleport.POH_OUTSIDE_POLLNIVNEACH_TAB,
                Teleport.POH_OUTSIDE_BRIMHAVEN, Teleport.POH_OUTSIDE_BRIMHAVEN_TAB,
                Teleport.POH_OUTSIDE_RELLEKKA, Teleport.POH_OUTSIDE_RELLEKKA_TAB,
                Teleport.POH_OUTSIDE_YANILLE, Teleport.POH_OUTSIDE_YANILLE_TAB,
                Teleport.POH_OUTSIDE_PRIFDDINAS, Teleport.POH_OUTSIDE_PRIFDDINAS_TAB,
                Teleport.ARDOUGNE_FARM_TELEPORT, Teleport.KHARYRLL_TELEPORT_TAB,
                Teleport.LASSAR_TELEPORT_TAB
        ));
    }

    private static final List<Teleport> blacklist = new ArrayList<>();

    public static List<Teleport> getBlacklist() {
        return DaxWalker.getInstance().blacklist;
    }

    public static void blacklistTeleports(Teleport... teleports) {
        getBlacklist().addAll(Arrays.asList(teleports));
    }

    public static void removeBlacklistTeleports(Teleport... teleports) {
        getBlacklist().removeAll(Arrays.asList(teleports));
    }

    public static void clearTeleportBlacklist() {
        getBlacklist().clear();
    }

    public static WalkingCondition getGlobalWalkingCondition() {
        return getInstance().globalWalkingCondition;
    }

    public void useLocalDevelopmentServer(boolean b) {
        WebWalkerServerApi.getInstance().setTestMode(b);
    }

    public static void setGlobalWalkingCondition(WalkingCondition walkingCondition) {
        getInstance().globalWalkingCondition = walkingCondition;
    }
    public static boolean walkTo(Locatable positionable) {
        return walkToWithResult(positionable).getSuccess();
    }

    public static WebWalkingResult walkToWithResult(Locatable positionable) {
        return walkToWithResult(positionable, null);
    }

    public static boolean walkTo(Locatable destination, WalkingCondition walkingCondition) {
        return walkToWithResult(destination, walkingCondition).getSuccess();
    }

    public static WebWalkingResult walkToWithResult(Locatable destination, WalkingCondition walkingCondition) {
        return walkToWithResult(destination, walkingCondition, 10, 75);
    }

    public static boolean walkTo(Locatable destination, WalkingCondition walkingCondition, int runMin, int runMax) {
        return walkToWithResult(destination, walkingCondition, runMin, runMax).getSuccess();
    }

    public static WebWalkingResult walkToWithResult(Locatable destination, WalkingCondition walkingCondition, int runMin, int runMax) {
        if (ShipUtils.isOnShip()) {
            ShipUtils.crossGangplank();
            WaitFor.milliseconds(500, 1200);
        }
        Tile start = Players.local().tile();
        if (start.equals(destination)) {
            return new WebWalkingResult(true, true, null);
        }
        if(Objects.stream(start, GameObject.Type.FLOOR_DECORATION).name("Fairy ring").isNotEmpty()){
            start = start.derive(0, 1);
        }

        List<Item> inventory = Inventory.stream().list();
        List<Item> equipment = Equipment.stream().list();
        PlayerDetails playerDetails = PlayerDetails.generate(inventory, equipment);
        boolean isInPvpWorld = Worlds.current().getSpecialty() == World.Specialty.PVP;
        Varpbits.cache();

        List<PathRequestPair> pathRequestPairs = getInstance().getPathTeleports(playerDetails.isMember(), isInPvpWorld, destination.tile(), inventory, equipment);

        pathRequestPairs.add(new PathRequestPair(Point3D.fromTile(start), Point3D.fromTile(destination.tile())));

        List<PathResult> pathResults = WebWalkerServerApi.getInstance().getPaths(new BulkPathRequest(playerDetails, pathRequestPairs));

        List<PathResult> validPaths = getInstance().validPaths(pathResults);

        PathResult pathResult = getInstance().getBestPath(validPaths);
        if (pathResult == null) {
            getInstance().log(Level.WARNING, "No valid path found");
            return new WebWalkingResult(true, false, FailureReason.NoPath);
        } else {
            getInstance().log("Path cost: " + pathResult.getCost());
        }

        List<Tile> path = new ArrayList<>(pathResult.toRSTilePath());
        getInstance().log("Path: [" + path.stream().map(Object::toString)
                .collect(Collectors.joining(", ")) + "]");

        boolean res = WalkerEngine.getInstance().walkPath(path, getGlobalWalkingCondition().combine(walkingCondition), runMin, runMax);
        if (res) {
            return new WebWalkingResult(true, true, null);
        }

        return new WebWalkingResult(true, false, FailureReason.Unknown);
    }

    public static boolean walkToBank() {
        return walkToBank(null, null);
    }

    public static boolean walkToBank(RunescapeBank bank) {
        return walkToBank(bank, null);
    }

    public static boolean walkToBank(WalkingCondition walkingCondition) {
        return walkToBank(null, walkingCondition);
    }

    public static boolean walkToBank(RunescapeBank bank, WalkingCondition walkingCondition) {
        return walkToBank(bank, walkingCondition, 10, 75);
    }

    public static boolean walkToBank(RunescapeBank bank, WalkingCondition walkingCondition, int runMin, int runMax) {
        if (ShipUtils.isOnShip()) {
            ShipUtils.crossGangplank();
            WaitFor.milliseconds(500, 1200);
        }

        if (bank != null)
            return walkTo(bank.getPosition(), getGlobalWalkingCondition().combine(walkingCondition), runMin, runMax);

        List<Item> inventory = Inventory.stream().list();
        List<Item> equipment = Equipment.stream().list();
        PlayerDetails playerDetails = PlayerDetails.generate(inventory, equipment);
        boolean isInPvpWorld = Worlds.current().getSpecialty() == World.Specialty.PVP;
        Varpbits.cache();

        List<BankPathRequestPair> pathRequestPairs = getInstance().getBankPathTeleports(playerDetails.isMember(), isInPvpWorld, inventory, equipment);
        Tile start = Players.local().tile();
        if(Objects.stream(start, GameObject.Type.FLOOR_DECORATION).name("Fairy ring").isNotEmpty()){
            start = start.derive(0, 1);
        }
        pathRequestPairs.add(new BankPathRequestPair(Point3D.fromTile(start), null));

        List<PathResult> pathResults = WebWalkerServerApi.getInstance().getBankPaths(new BulkBankPathRequest(
                playerDetails, pathRequestPairs));

        List<PathResult> validPaths = getInstance().validPaths(pathResults);
        PathResult pathResult = getInstance().getBestPath(validPaths);
        if (pathResult == null) {
            getInstance().log(Level.WARNING, "No valid path found");
            return false;
        }
        return WalkerEngine.getInstance().walkPath(pathResult.toRSTilePath(), getGlobalWalkingCondition().combine(walkingCondition), runMax, runMax);
    }

    public static List<Tile> getPath(Locatable destination) {
        Tile start = Players.local().tile();
        if(Objects.stream(start, GameObject.Type.FLOOR_DECORATION).name("Fairy ring").isNotEmpty()){
            start = start.derive(0, 1);
        }
        return getPath(start, destination);
    }

    public static List<Tile> getPath(Locatable start, Locatable destination) {
        if (start.equals(destination)) {
            return Collections.emptyList();
        }

        List<Item> inventory = Inventory.stream().list();
        List<Item> equipment = Equipment.stream().list();
        PlayerDetails playerDetails = PlayerDetails.generate(inventory, equipment);
        boolean isInPvpWorld = Worlds.current().getSpecialty() == World.Specialty.PVP;
        Varpbits.cache();

        List<PathRequestPair> pathRequestPairs = getInstance().getPathTeleports(playerDetails.isMember(), isInPvpWorld, destination.tile(), inventory, equipment);

        pathRequestPairs.add(new PathRequestPair(Point3D.fromTile(start.tile()), Point3D.fromTile(destination.tile())));

        List<PathResult> pathResults = WebWalkerServerApi.getInstance().getPaths(new BulkPathRequest(playerDetails, pathRequestPairs));

        List<PathResult> validPaths = getInstance().validPaths(pathResults);

        PathResult pathResult = getInstance().getBestPath(validPaths);
        if (pathResult == null) {
            getInstance().log(Level.WARNING, "No valid path found");
            return Collections.emptyList();
        }

        getInstance().log("Got valid path.");

        return pathResult.toRSTilePath();
    }

    private List<PathRequestPair> getPathTeleports(boolean members, boolean pvp, Tile start, List<Item> inventory, List<Item> equipment) {
        return Teleport.getValidStartingRSTiles(members, pvp, getBlacklist(), inventory, equipment).stream()
                .map(t -> new PathRequestPair(Point3D.fromTile(t),
                        Point3D.fromTile(start)))
                .collect(Collectors.toList());
    }

    private List<BankPathRequestPair> getBankPathTeleports(boolean members, boolean pvp, List<Item> inventory, List<Item> equipment) {
        return Teleport.getValidStartingRSTiles(members, pvp, getBlacklist(), inventory, equipment).stream()
                .map(t -> new BankPathRequestPair(Point3D.fromTile(t), null))
                .collect(Collectors.toList());
    }

    public List<PathResult> validPaths(List<PathResult> list) {
        if (list == null)
            return Collections.emptyList();
        List<PathResult> result = list.stream().filter(pathResult -> pathResult.getPathStatus() == PathStatus.SUCCESS).collect(
                Collectors.toList());
        if (!result.isEmpty()) {
            return result;
        }
        return Collections.emptyList();
    }

    public PathResult getBestPath(List<PathResult> list) {
        return list.stream().min(Comparator.comparingInt(this::getPathMoveCost)).orElse(null);
    }

    private int getPathMoveCost(PathResult pathResult) {
        if (Players.local().tile().equals(pathResult.getPath().get(0).toTile())) {
//            System.out.println("Path starts at player current position.  Path cost: " + pathResult.getCost());
            return pathResult.getCost();
        }
        Tile startTile = pathResult.getPath().get(0).toTile();
        Teleport teleport = map.get(startTile);
        if (teleport == null) {
//            System.out.println("Path is not with a teleport. Cost: " + pathResult.getCost());
            return pathResult.getCost();
        }
//        System.out.println("Path is with a teleport. Teleport used: " + teleport + " total cost: " + (teleport.getMoveCost() + pathResult.getCost()));
        return teleport.getMoveCost() + pathResult.getCost();
    }

    @Override
    public String getName() {
        return "DaxWalker";
    }
}
