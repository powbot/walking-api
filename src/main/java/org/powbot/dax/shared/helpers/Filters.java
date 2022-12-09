package org.powbot.dax.shared.helpers;

import org.powbot.api.Area;
import org.powbot.api.Locatable;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Filters {

    public static class Items {

        public static Predicate<Item> actionsContains(String... actions){
            List<String> asList = Arrays.asList(actions);
            return i -> asList.stream().anyMatch(action -> {
                List<String> curr = i.actions();
                return curr.stream().anyMatch(c -> c.contains(action));
            });
        }

        public static Predicate<Item> actionsEquals(String... actions){
            List<String> asList = Arrays.asList(actions);
            return i -> asList.stream().anyMatch(action -> {
                List<String> curr = i.actions();
                return curr.contains(action);
            });
        }

        public static Predicate<Item> actionsNotContains(String... actions){
            return actionsContains(actions).negate();
        }

        public static Predicate<Item> actionsNotEquals(String... actions){
            return actionsEquals(actions).negate();
        }

        public static Predicate<Item> nameContains(String... names){
            List<String> asList = Arrays.stream(names).map(String::toLowerCase).collect(Collectors.toList());
            return i -> {
                String name = i.name().toLowerCase();
                return asList.stream().anyMatch(name::contains);
            };
        }

        public static Predicate<Item> idEquals(int... ids){
            List<Integer> asList = Arrays.stream(ids).boxed().collect(Collectors.toList());
            return i -> asList.contains(i.id());
        }

        public static Predicate<Item> idNotEquals(int... ids){
            return idEquals(ids).negate();
        }

        public static Predicate<Item> nameEquals(String... names){
            List<String> asList = Arrays.asList(names);
            return i -> asList.contains(i.name());
        }

        public static Predicate<Item> nameNotContains(String... names){
            return nameContains(names).negate();
        }

        public static Predicate<Item> nameNotEquals(String... names){
            return nameEquals(names).negate();
        }
    }

    public static class GroundItems {

        public static Predicate<GroundItem> actionsContains(String... actions){
            List<String> asList = Arrays.asList(actions);
            return i -> asList.stream().anyMatch(action -> {
                String[] curr = i.getConfig().getGroundActions();
                return Arrays.stream(curr).anyMatch(c -> c.contains(action));
            });
        }

        public static Predicate<GroundItem> actionsEquals(String... actions){
            List<String> asList = Arrays.asList(actions);
            return i -> asList.stream().anyMatch(action -> {
                String[] curr = i.getConfig().getGroundActions();
                return Arrays.stream(curr).anyMatch(c -> c.contains(action));
            });
        }

        public static Predicate<GroundItem> actionsNotContains(String... actions){
            return actionsContains(actions).negate();
        }

        public static Predicate<GroundItem> actionsNotEquals(String... actions){
            return actionsEquals(actions).negate();
        }

        public static Predicate<GroundItem> nameContains(String... names){
            List<String> asList = Arrays.asList(names);
            return i -> asList.stream().anyMatch(name -> i.name().toLowerCase().contains(name.toLowerCase()));
        }

        public static Predicate<GroundItem> idEquals(int... ids){
            List<Integer> asList = Arrays.stream(ids).boxed().collect(Collectors.toList());
            return i -> asList.contains(i.id());
        }

        public static Predicate<GroundItem> idNotEquals(int... ids){
            return idEquals(ids).negate();
        }

        public static Predicate<GroundItem> inArea(Area area){
            return i -> area.contains(i.tile());
        }

        public static Predicate<GroundItem> nameEquals(String... names){
            List<String> asList = Arrays.asList(names);
            return i -> asList.contains(i.name());
        }

        public static Predicate<GroundItem> nameNotContains(String... names){
            return nameContains(names).negate();
        }

        public static Predicate<GroundItem> nameNotEquals(String... names){
            return nameEquals(names).negate();
        }

        public static Predicate<GroundItem> notInArea(Area area){
            return inArea(area).negate();
        }

        public static Predicate<GroundItem> tileEquals(Locatable pos){
            if(pos == null){
                return i -> i.tile() == Tile.getNil();
            }
            Tile target = pos.tile();
            return i -> target.equals(i.tile());
        }

        public static Predicate<GroundItem> tileNotEquals(Locatable pos){
            return tileEquals(pos).negate();
        }
    }

    public static class NPCs {

        public static Predicate<Npc> actionsContains(String... actions){
            List<String> asList = Arrays.asList(actions);
            return i -> asList.stream().anyMatch(action -> {
                List<String> curr = i.getActions();
                return curr.stream().filter(java.util.Objects::nonNull).anyMatch(c -> c.contains(action));
            });
        }

        public static Predicate<Npc> actionsEquals(String... actions){
            List<String> asList = Arrays.asList(actions);
            return i -> asList.stream().anyMatch(action -> {
                List<String> curr = i.getActions();
                return curr.contains(action);
            });
        }

        public static Predicate<Npc> actionsNotContains(String... actions){
            return actionsContains(actions).negate();
        }

        public static Predicate<Npc> actionsNotEquals(String... actions){
            return actionsEquals(actions).negate();
        }

        public static Predicate<Npc> nameContains(String... names){
            List<String> asList = Arrays.asList(names);
            return i -> asList.stream().anyMatch(name -> i.getName().contains(name));
        }

        public static Predicate<Npc> idEquals(int... ids){
            List<Integer> asList = Arrays.stream(ids).boxed().collect(Collectors.toList());
            return i -> asList.contains(i.id());
        }

        public static Predicate<Npc> idNotEquals(int... ids){
            return idEquals(ids).negate();
        }

        public static Predicate<Npc> inArea(Area area){
            return i -> area.contains(i.tile());
        }

        public static Predicate<Npc> nameEquals(String... names){
            List<String> asList = Arrays.asList(names);
            return i -> asList.contains(i.getName());
        }

        public static Predicate<Npc> nameNotContains(String... names){
            return nameContains(names).negate();
        }

        public static Predicate<Npc> nameNotEquals(String... names){
            return nameEquals(names).negate();
        }

        public static Predicate<Npc> notInArea(Area area){
            return inArea(area).negate();
        }

        public static Predicate<Npc> tileEquals(Locatable pos){
            if(pos == null){
                return i -> i.tile() == null;
            }
            Tile target = pos.tile();
            return i -> target.equals(i.tile());
        }

        public static Predicate<Npc> tileNotEquals(Locatable pos){
            return tileEquals(pos).negate();
        }
    }

    public static class Objects {

        public static Predicate<GameObject> actionsContains(String... actions){
            List<String> asList = Arrays.asList(actions);
            return i -> asList.stream().anyMatch(action -> {
                CacheObjectConfig def = CacheObjectConfig.load(i.id());
                String[] curr = def.getActions();
                return Arrays.stream(curr).filter(java.util.Objects::nonNull).anyMatch(c -> c.contains(action));
            });
        }

        public static Predicate<GameObject> actionsEquals(String... actions){
            List<String> asList = Arrays.asList(actions);
            return i -> asList.stream().anyMatch(action -> {
                CacheObjectConfig def = CacheObjectConfig.load(i.id());
                String[] curr = def.getActions();
                return Arrays.asList(curr).contains(action);
            });
        }

        public static Predicate<GameObject> actionsNotContains(String... actions){
            return actionsContains(actions).negate();
        }

        public static Predicate<GameObject> actionsNotEquals(String... actions){
            return actionsEquals(actions).negate();
        }

        public static Predicate<GameObject> nameContains(String... names){
            List<String> asList = Arrays.asList(names);
            return i -> {
                String objName = i.getName();
                return objName != null && asList.stream().anyMatch(o -> objName.toLowerCase().contains(o.toLowerCase()));
            };
        }

        public static Predicate<GameObject> idEquals(int... ids){
            List<Integer> asList = Arrays.stream(ids).boxed().collect(Collectors.toList());
            return i -> asList.contains(i.id());
        }

        public static Predicate<GameObject> idNotEquals(int... ids){
            return idEquals(ids).negate();
        }

        public static Predicate<GameObject> inArea(Area area){
            return i -> area.contains(i.tile());
        }

        public static Predicate<GameObject> nameEquals(String... names){
            List<String> asList = Arrays.asList(names);
            return i -> asList.contains(i.getName());
        }

        public static Predicate<GameObject> nameNotContains(String... names){
            return nameContains(names).negate();
        }

        public static Predicate<GameObject> nameNotEquals(String... names){
            return nameEquals(names).negate();
        }

        public static Predicate<GameObject> notInArea(Area area){
            return inArea(area).negate();
        }

        public static Predicate<GameObject> tileEquals(Locatable pos){
            if(pos == null){
                return i -> i.tile() == null;
            }
            Tile target = pos.tile();
            return i -> {
//                Tile[] allTiles = i.getAllTiles();
//                for(Tile t:allTiles){
                return i.tile().equals(target);
//                }
            };
        }

        public static Predicate<GameObject> tileNotEquals(Locatable pos){
            return tileEquals(pos).negate();
        }
    }

}
