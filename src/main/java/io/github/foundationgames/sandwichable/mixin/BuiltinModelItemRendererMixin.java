package io.github.foundationgames.sandwichable.mixin;


import io.github.foundationgames.sandwichable.block.BlocksRegistry;
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
    private void renderSandwichGui(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        if(stack.getItem() == (BlocksRegistry.SANDWICH).asItem()) {
            matrices.push();
            if(stack.getTag() != null) {
                if(stack.getTag().contains("BlockEntityTag")) {
                    NbtCompound tag = stack.getSubTag("BlockEntityTag");
                    /*DefaultedList<ItemStack> foodList = DefaultedList.ofSize(128, ItemStack.EMPTY);
                    Inventories.fromTag(tag, foodList);
                    matrices.translate(0.5, 0.017, 0.4);
                    matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((90)));
                    for (int i = 0; i < foodList.size(); i++) {
                        MinecraftClient.getInstance().getItemRenderer().renderItem(foodList.get(i), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
                        matrices.translate(0.0, 0.0, -0.034);
                    }*/
                    matrices.translate(0.5, 0.017, 0.4);
                    cache.setFromTag(tag);
                    cache.render(matrices, vertexConsumers, light, overlay);
                }
            } else {
                matrices.translate(0.5, 0.017, 0.4);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((90)));
                MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Items.BARRIER), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);
            }
            matrices.pop();
        }
    }

    // OPTIFABRIC COMPAT (No longer needed, has been fixed on optifabric's side)
    /*
    public void sandwich_render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        this.renderSandwichGui(stack, mode, matrices, vertexConsumers, light, overlay, ci);
    }*/
}
