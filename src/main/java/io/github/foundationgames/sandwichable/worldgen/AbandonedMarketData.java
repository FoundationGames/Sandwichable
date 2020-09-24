package io.github.foundationgames.sandwichable.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class AbandonedMarketData {
    public static final StructurePool START_POOL;

    public static void init() {}

    static {
        registerSingle(Util.id("north_road"), StructurePoolElement.method_30426("sandwichable:market/roads/north_road", StructureProcessorLists.STREET_PLAINS), StructurePool.Projection.TERRAIN_MATCHING);
        registerSingle(Util.id("south_road"), StructurePoolElement.method_30426("sandwichable:market/roads/south_road", StructureProcessorLists.STREET_PLAINS), StructurePool.Projection.TERRAIN_MATCHING);
        registerSingle(Util.id("east_road"), StructurePoolElement.method_30426("sandwichable:market/roads/east_road", StructureProcessorLists.STREET_PLAINS), StructurePool.Projection.TERRAIN_MATCHING);
        registerSingle(Util.id("west_road"), StructurePoolElement.method_30426("sandwichable:market/roads/west_road", StructureProcessorLists.STREET_PLAINS), StructurePool.Projection.TERRAIN_MATCHING);
        registerBooth("farming");
        registerBooth("gardening");
        registerBooth("cheese");
        registerBooth("cutting_board");
        registerBooth("appliance");
        registerBooth("automation");
        registerBooth("cooking");
        registerBooth("pickle");
        START_POOL = registerSingle(Util.id("market_center"), StructurePoolElement.method_30426("sandwichable:market/booths/market_center", StructureProcessorLists.EMPTY), StructurePool.Projection.RIGID);
    }

    private static StructurePool registerSingle(Identifier poolId, Function<StructurePool.Projection, LegacySinglePoolElement> element, StructurePool.Projection projection) {
        return StructurePools.register(
                new StructurePool(
                        poolId,
                        new Identifier("empty"),
                        ImmutableList.of(
                                Pair.of(element, 1)
                        ), projection
                )
        );
    }
    private static StructurePool registerBooth(String name) {
        return registerSingle(Util.id(name+"_booth"), StructurePoolElement.method_30426("sandwichable:market/booths/market_"+name+"_booth", StructureProcessorLists.EMPTY), StructurePool.Projection.RIGID);
    }
}
