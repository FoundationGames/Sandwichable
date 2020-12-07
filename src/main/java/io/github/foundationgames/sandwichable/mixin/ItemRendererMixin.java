package io.github.foundationgames.sandwichable.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.sandwichable.items.CustomDurabilityBar;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "HEAD"))
    public void renderCustomDurabilityBars(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        if(!stack.isEmpty() && stack.getItem() instanceof CustomDurabilityBar) {
            CustomDurabilityBar item = (CustomDurabilityBar)stack.getItem();
            if(item.showBar(stack)) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                int i = Math.round(13.0f * item.getBarLength(stack));
                int color = item.getBarColor(stack);
                this.renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                this.renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1, color >> 16 & 255, color >> 8 & 255, color & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }
}
