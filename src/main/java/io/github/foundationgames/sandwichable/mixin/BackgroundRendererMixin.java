package io.github.foundationgames.sandwichable.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.sandwichable.fluids.FluidsRegistry;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Shadow private static float red;

    @Shadow private static float green;

    @Shadow private static float blue;

    @Inject(method = "render", at = @At("TAIL"))
    private static void fogColor(Camera camera, float tickDelta, ClientWorld world, int i, float f, CallbackInfo ci) {
        FluidState fluidState = camera.getSubmergedFluidState();
        if(fluidState.getFluid() == FluidsRegistry.PICKLE_BRINE || fluidState.getFluid() == FluidsRegistry.PICKLE_BRINE_FLOWING) {
            red = 0.28f;
            green = 1.0f;
            blue = 0.4f;
        }
    }

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void pickleBrineFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
        FluidState fluidState = camera.getSubmergedFluidState();
        if(fluidState.getFluid() == FluidsRegistry.PICKLE_BRINE || fluidState.getFluid() == FluidsRegistry.PICKLE_BRINE_FLOWING) {
            RenderSystem.fogDensity(0.5f);
            RenderSystem.fogStart(0);
            RenderSystem.fogEnd(viewDistance * 0.04f);
            RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
            RenderSystem.setupNvFogDistance();
            ci.cancel();
        }
    }
}
