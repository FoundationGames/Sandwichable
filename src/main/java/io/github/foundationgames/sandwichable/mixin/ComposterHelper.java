package io.github.foundationgames.sandwichable.mixin;

import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ComposterBlock.class)
public interface ComposterHelper {
    @Invoker("registerCompostableItem")
    static void registerCompostable(float levelIncreaseChance, ItemConvertible item) {
        throw new AssertionError("dummy method body");
    }
}
