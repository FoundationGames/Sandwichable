package io.github.foundationgames.sandwichable.mixin;


import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.util.Sandwich;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {
    private static final Sandwich cache = new Sandwich();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;getBlock()Lnet/minecraft/block/Block;", shift = At.Shift.AFTER), method = "render")
    private void sandwichable$renderSandwichGui(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        if(stack.getItem() == (BlocksRegistry.SANDWICH).asItem()) {
            matrices.push();
            if(stack.getNbt() != null) {
                if(stack.getNbt().contains("BlockEntityTag")) {
                    NbtCompound tag = stack.getSubNbt("BlockEntityTag");
                    matrices.translate(0.5, 0.017, 0.4);
                    cache.setFromNbt(tag);
                    cache.render(matrices, vertexConsumers, light, overlay);
                }
            } else {
                matrices.translate(0.5, 0.017, 0.4);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((90)));
                MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Items.BARRIER), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 204874579);
            }
            matrices.pop();
        }
    }
}
