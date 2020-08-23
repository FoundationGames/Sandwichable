package io.github.foundationgames.sandwichable.worldgen;

import com.google.common.collect.Lists;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;

public class StructurePoolHelper {
    public static final List<Pair<Identifier, Pair<StructurePoolElement, Integer>>> ENTRIES = Lists.newArrayList();

    public static void add(Identifier targetPool, StructurePoolElement element, int weight) {
        ENTRIES.add(new Pair<>(targetPool, new Pair<>(element, weight)));
    }
}
