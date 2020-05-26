package io.github.foundationgames.sandwichable.blocks.entity;

import com.google.common.collect.Maps;

import java.util.Map;

public enum PickleJarFluid {
    AIR("air"),
    WATER("water"),
    PICKLING_BRINE("pickling_brine"),
    PICKLED_BRINE("pickled_brine");

    String id;

    private static Map<String, PickleJarFluid> pickleJarFluidFromString() {
        Map<String, PickleJarFluid> map = Maps.newHashMap();
        map.put("air", PickleJarFluid.AIR);
        map.put("water", PickleJarFluid.WATER);
        map.put("pickling_brine", PickleJarFluid.PICKLING_BRINE);
        map.put("pickled_brine", PickleJarFluid.PICKLED_BRINE);
        return map;
    }

    PickleJarFluid(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }

    public static PickleJarFluid fromString(String string) {
        return PickleJarFluid.pickleJarFluidFromString().get(string);
    }
}
