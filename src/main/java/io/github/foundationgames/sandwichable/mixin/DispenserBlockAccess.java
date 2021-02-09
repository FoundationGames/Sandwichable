package io.github.foundationgames.sandwichable.mixin;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DispenserBlock.class)
public interface DispenserBlockAccess {
    @Accessor("BEHAVIORS")
    static Map<Item, DispenserBehavior> behaviors() {
        throw new AssertionError("dummy method body accessed");
    }
}
