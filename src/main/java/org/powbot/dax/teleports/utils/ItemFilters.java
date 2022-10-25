package org.powbot.dax.teleports.utils;

import org.powbot.api.rt4.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemFilters {
    public static Predicate<Item> nameContains(String... names){
        List<String> asList = Arrays.stream(names).map(String::toLowerCase).collect(Collectors.toList());
        return i -> {
            String name = i.name().toLowerCase(Locale.ROOT);
            return asList.stream().anyMatch(name::contains);
        };
    }
    public static Predicate<Item> nameEquals(String... names){
        List<String> asList = Arrays.stream(names).map(String::toLowerCase).collect(Collectors.toList());
        return i -> {
            String name = i.name().toLowerCase(Locale.ROOT);
            return asList.stream().anyMatch(name::equals);
        };
    }

    public static Predicate<Item> idEquals(int... ids){
        List<Integer> asList = Arrays.stream(ids).boxed().collect(Collectors.toList());
        return i -> {
            int id = i.id();
            return asList.contains(id);
        };
    }

}
