package io.github.foundationgames.sandwichable.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccess {
    @Accessor(value = "fpsCounter")
    int getFps();
}
