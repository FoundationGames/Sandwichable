package io.github.foundationgames.sandwichable.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.sandwichable.fluids.FluidsRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Shadow private static float red;

    @Shadow private static float green;

    @Shadow private static float blue;

    @Inject(method = "render", at = @At("TAIL"))
    private static void sandwichable$pickleBrineFogColor(Camera camera, float tickDelta, ClientWorld world, int i, float f, CallbackInfo ci) {
        if(sandwichable$inPickleBrine(camera)) {
            red = 0.13f;
            green = 0.486f;
            blue = 0.17f;
        }
    }

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void sandwichable$pickleBrineFogRender(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
        if(sandwichable$inPickleBrine(camera)) {
            RenderSystem.setShaderFogStart(0);
            RenderSystem.setShaderFogEnd(viewDistance * 0.067f);
            ci.cancel();
        }
    }

    @Unique
    private static boolean sandwichable$inPickleBrine(Camera camera) {
        var world = MinecraftClient.getInstance().world;
        var pos = camera.getBlockPos();
        var fluidState = MinecraftClient.getInstance().world.getFluidState(pos);
        return (fluidState.getFluid() == FluidsRegistry.PICKLE_BRINE || fluidState.getFluid() == FluidsRegistry.PICKLE_BRINE_FLOWING)
                && camera.getPos().y < (pos.getY() + fluidState.getHeight(world, pos));
    }
}
