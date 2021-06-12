package io.github.foundationgames.sandwichable.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.sandwichable.fluid.FluidsRegistry;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
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
        if(isInPickleBrine(camera)) {
            red = 0.13f;
            green = 0.486f;
            blue = 0.17f;
        }
    }

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void pickleBrineFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
        if(isInPickleBrine(camera)) {
            //TODO some of these don't seem to have an alternative in 1.17
            //RenderSystem.fogDensity(0.22f);
            RenderSystem.setShaderFogStart(0);
            RenderSystem.setShaderFogEnd(viewDistance * 0.067f);
            //RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
            //RenderSystem.setupNvFogDistance();
            ci.cancel();
        }
    }

    private static boolean isInPickleBrine(Camera camera) {
        var fluidState = ((CameraAccessor)camera).getArea().getFluidState(camera.getBlockPos());
        return (fluidState.getFluid() == FluidsRegistry.PICKLE_BRINE || fluidState.getFluid() == FluidsRegistry.PICKLE_BRINE_FLOWING) &&
                camera.getPos().y < camera.getBlockPos().getY() + fluidState.getHeight(((CameraAccessor)camera).getArea(), camera.getBlockPos());
    }
}
