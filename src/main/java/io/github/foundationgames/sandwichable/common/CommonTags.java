package io.github.foundationgames.sandwichable.common;

import io.github.foundationgames.sandwichable.items.CheeseType;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class CommonTags {
    public static final Tag<Item> BREAD_SLICE = t("bread_slice");
    public static final Tag<Item> TOASTED_BREAD_SLICE = t("toasted_bread_slice");
    public static final Tag<Item> TOMATO = t("tomato");
    public static final Tag<Item> TOMATO_SLICE = t("tomato_slice");
    public static final Tag<Item> LETTUCE_HEAD = t("lettuce_head");
    public static final Tag<Item> LETTUCE_LEAF = t("lettuce_leaf");
    public static final Tag<Item> CUCUMBER = t("cucumber");
    public static final Tag<Item> PICKLED_CUCUMBER = t("pickled_cucumber");
    public static final Tag<Item> PICKLE_CHIPS = t("pickle_chips");
    public static final Tag<Item> ONION = t("onion");
    public static final Tag<Item> CHOPPED_ONION = t("chopped_onion");
    public static final Tag<Item> TOMATO_SEEDS = t("tomato_seeds");
    public static final Tag<Item> LETTUCE_SEEDS = t("lettuce_seeds");
    public static final Tag<Item> ONION_SEEDS = t("onion_seeds");
    public static final Tag<Item> CUCUMBER_SEEDS = t("cucumber_seeds");
    public static final Tag<Item> SALT = t("salt");
    public static final Tag<Item> SALT_ROCK = t("salt_rock");
    public static final Tag<Item> MAYONNAISE_BOTTLE = t("mayonnaise_bottle");
    public static final Tag<Item> SWEET_BERRY_JAM_BOTTLE = t("sweet_berry_jam_bottle");
    public static final Tag<Item> BACON = t("bacon");
    public static final Tag<Item> PORK_FILET = t("pork_filet");
    public static final Tag<Item> COOKED_PORK_FILET = t("cooked_pork_filet");
    public static final Tag<Item> CHICKEN_FILET = t("chicken_filet");
    public static final Tag<Item> COOKED_CHICKEN_FILET = t("cooked_chicken_filet");
    public static final Tag<Item> COD_FILET = t("cod_filet");
    public static final Tag<Item> COOKED_COD_FILET = t("cooked_cod_filet");
    public static final Tag<Item> SALMON_FILET = t("salmon_filet");
    public static final Tag<Item> COOKED_SALMON_FILET = t("cooked_salmon_filet");
    public static final Tag<Item> DEFAULT_CHEESE_WHEEL = t("default_cheese_wheel");
    public static final Tag<Item> DEFAULT_CHEESE_PIECE = t("default_cheese_piece");
    /*public static final Map<Pair<CheeseType, Boolean>, Tag<Item>> CHEESE_TAGS = Util.create(() -> {
        Map<Pair<CheeseType, Boolean>, Tag<Item>> map = new HashMap<>();
        for(CheeseType type : CheeseType.values()) {
            if(type != CheeseType.NONE) {
                map.put(new Pair<>(type, false), t(type.toString()+"_cheese_wheel"));
                map.put(new Pair<>(type, true), t(type.toString()+"_cheese_slice"));
            }
        }
        return map;
    });*/

    public static void init() {}

    private static Tag<Item> t(String name) {
        return TagRegistry.item(new Identifier("c", name));
    }

    /*public static Tag<Item> getCheeseTag(CheeseType type, boolean isSliceTag) {
        return CHEESE_TAGS.get(new Pair<>(type, isSliceTag));
    }*/
}
