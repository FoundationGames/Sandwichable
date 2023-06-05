package io.github.foundationgames.sandwichable.structure;

import com.mojang.datafixers.util.Pair;
import io.github.foundationgames.sandwichable.mixin.StructurePoolAccessor;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SandwichableStructures {
	public static final int SANDWICH_STAND_CHANCE = 2;

	public static void addStructures(MinecraftServer server) {
		addToStructurePool(server, new Identifier("village/plains/houses"), Util.id("village/plains/houses/plains_sandwich_stand"), new Identifier("empty"), SANDWICH_STAND_CHANCE);
		addToStructurePool(server, new Identifier("village/savanna/houses"), Util.id("village/savanna/houses/savanna_sandwich_stand"), new Identifier("empty"), SANDWICH_STAND_CHANCE);
		addToStructurePool(server, new Identifier("village/taiga/houses"), Util.id("village/plains/taiga/taiga_sandwich_stand"), new Identifier("empty"), SANDWICH_STAND_CHANCE);
		addToStructurePool(server, new Identifier("village/snowy/houses"), Util.id("village/snowy/houses/snowy_sandwich_stand"), new Identifier("empty"), SANDWICH_STAND_CHANCE);
	}

	private static void addToStructurePool(MinecraftServer server, Identifier poolId, Identifier structureId, Identifier processor, int weight) {
		Optional<StructurePool> poolGetter = server.getRegistryManager().get(Registry.STRUCTURE_POOL_KEY).getOrEmpty(poolId);

		if (poolGetter.isEmpty()) {
			return;
		}
		StructurePool pool = poolGetter.get();

		Optional<RegistryEntry<StructureProcessorList>> processorList = server.getRegistryManager().get(Registry.STRUCTURE_PROCESSOR_LIST_KEY).getEntry(RegistryKey.of(Registry.STRUCTURE_PROCESSOR_LIST_KEY, processor));

		List<StructurePoolElement> pieceList = ((StructurePoolAccessor) pool).getElements();
		SinglePoolElement piece = processorList.isPresent() ? StructurePoolElement.ofProcessedSingle(structureId.toString(), processorList.orElseThrow()).apply(StructurePool.Projection.RIGID) : StructurePoolElement.ofLegacySingle(structureId.toString()).apply(StructurePool.Projection.RIGID);

		ArrayList<Pair<StructurePoolElement, Integer>> list = new ArrayList<>(((StructurePoolAccessor) pool).getElementCounts());
		list.add(Pair.of(piece, weight));
		((StructurePoolAccessor) pool).setElementCounts(list);

		for (int i = 0; i < weight; ++i) {
			pieceList.add(piece);
		}
	}
}
