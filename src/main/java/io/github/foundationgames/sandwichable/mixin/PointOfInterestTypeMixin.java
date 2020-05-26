package io.github.foundationgames.sandwichable.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;
import java.util.Set;

@Mixin(PointOfInterestType.class)
public interface PointOfInterestTypeMixin {
    @Accessor(value = "BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE")
    static Map<BlockState, PointOfInterestType> sandwichTableStatesMap() {
        throw new IllegalStateException();
    }

    @Invoker("<init>")
    static PointOfInterestType create(String id, Set<BlockState> blockStates, int ticketCount, int searchDistance) {
        throw new AssertionError("empty body");
    }
}
