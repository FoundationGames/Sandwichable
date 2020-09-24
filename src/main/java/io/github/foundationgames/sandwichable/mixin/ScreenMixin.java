package io.github.foundationgames.sandwichable.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.items.SandwichIngredientItem;
import io.github.foundationgames.sandwichable.util.RenderUtil;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public class ScreenMixin extends AbstractParentElement {

    //@Shadow public void fillGradient(Matrix4f matrix, BufferBuilder bufferBuilder, int xStart, int yStart, int xEnd, int yEnd, int z, -267386864, -267386864);
    @Shadow protected TextRenderer textRenderer;
    @Final @Shadow protected List<Element> children = Lists.newArrayList();

    private static ItemStack stack = null;

    private static BufferBuilder builder = null;

    @Inject(method = "renderTooltip", at = @At(value = "HEAD"))
    public void setStack(MatrixStack matrices, ItemStack istack, int tx, int ty, CallbackInfo ci) {
        stack = istack.copy();
    }

    @ModifyVariable(method = "renderOrderedTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;begin(ILnet/minecraft/client/render/VertexFormat;)V", shift = At.Shift.BEFORE), index = 15)
    public BufferBuilder getBuilder(BufferBuilder b) {
        builder = b;
        return b;
    }

    @Inject(method = "renderOrderedTooltip", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableDepthTest()V", shift = At.Shift.BEFORE))
    public void renderSandwichableTooltipBG(MatrixStack matrices, List<? extends OrderedText> lines, int tx, int ty, CallbackInfo ci) {
        if(stack != null && stack.getItem() instanceof SandwichIngredientItem) {
            int x = tx + 12;
            int y = ty - 12;
            Screen self = (Screen)(Object)this;
            int fullnessBonus = ((SandwichIngredientItem)stack.getItem()).fullness;
            List<Text> tooltip = self.getTooltipFromItem(stack);
            int twidth = 0;
            for(Text txt : tooltip) {
                int w = this.textRenderer.getWidth(txt);
                if(w > twidth) {
                    twidth = w;
                }
            }
            int xs = x + twidth + 4;
            int ys = y - 1;

            int length = (fullnessBonus * 7) + 3;

            Matrix4f matrix4f = matrices.peek().getModel();
            if(builder != null) {
                fillGradient(matrix4f, builder, xs, ys, xs + length, ys + 1, 400, 0xee203e5a, 0xee203e5a);
                fillGradient(matrix4f, builder, xs, ys+1, xs + length, ys+9, 400, 0xe44788c9, 0xe43c74aa);
                fillGradient(matrix4f, builder, xs + length, ys + 1, xs + length + 1, ys + 9, 400, 0xee203e5a, 0xee172d41);
                fillGradient(matrix4f, builder, xs, ys + 9, xs + length, ys + 10, 400, 0xee172d41, 0xee172d41);
            }
        }
    }

    @Inject(method = "renderOrderedTooltip", at = @At(value = "TAIL"))
    public void renderSandwichableTooltipIcons(MatrixStack matrices, List<? extends OrderedText> lines, int tx, int ty, CallbackInfo ci) {
        if(stack != null && stack.getItem() instanceof SandwichIngredientItem) {
            int x = tx + 12;
            int y = ty - 12;
            int twidth = 0;
            for(OrderedText txt : lines) {
                int w = this.textRenderer.getWidth(txt);
                if(w > twidth) {
                    twidth = w;
                }
            }
            int fullnessBonus = ((SandwichIngredientItem)stack.getItem()).fullness;
            SandwichIngredientItem.Flavor flavor = ((SandwichIngredientItem)stack.getItem()).flavor;
            int xs = x + twidth + 4;
            int ys = y - 1;
            MinecraftClient.getInstance().getTextureManager().bindTexture(Util.id("textures/gui/icons.png"));
            for (int i = 0; i < fullnessBonus; i++) {
                drawTexture(matrices, xs + 1 + (i * 7), ys + 2, flavor.textureU, 0, 8, 6);
            }
        }
    }


    @Inject(method = "renderTooltip", at = @At(value = "TAIL"))
    public void deleteCaches(MatrixStack matrices, ItemStack istack, int tx, int ty, CallbackInfo ci) {
        stack = null;
        builder = null;
    }

    public List<? extends Element> children() { return this.children; }
}
