package io.github.foundationgames.sandwichable.util;

import com.google.common.collect.ImmutableList;
import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Sandwich {
    private final ArrayList<ItemStack> foods = new ArrayList<>();

    public Sandwich() {}

    public boolean addFood(PlayerEntity player, ItemStack stack) {
        ItemStack g = addTopFoodFrom(player.isCreative() ? stack.copy() : stack);
        if(g == null) return false;
        if(!g.isEmpty() && !player.isCreative()) player.giveItemStack(g);
        return true;
    }
    
    public ItemStack addTopFoodFrom(ItemStack stack) {
        if(SpreadRegistry.INSTANCE.itemHasSpread(stack.getItem())) {
            ItemStack spread = new ItemStack(ItemsRegistry.SPREAD, 1);
            SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem()).onPour(stack, spread);
            spread.getOrCreateTag().putString("spreadType", SpreadRegistry.INSTANCE.asString(SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem())));
            foods.add(spread);
            ItemStack r = SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem()).getResultItem();
            stack.decrement(1);
            return r;
        } else if(stack.isFood()) {
            foods.add(stack.split(1));
            return ItemStack.EMPTY;
        }
        return null;
    }

    public ItemStack removeTopFood() {
        if(foods.size() > 0) return foods.remove(foods.size() - 1);
        return ItemStack.EMPTY;
    }

    public ItemStack getTopFood() {
        return foods.size() > 0 ? foods.get(foods.size() - 1) : ItemStack.EMPTY;
    }

    public List<ItemStack> getFoodList() {
        return foods;
    }

    public void setFoodList(List<ItemStack> list) {
        foods.clear();
        foods.addAll(list);
    }

    public void clearFoodList() {
        foods.clear();
    }
    
    public int getSize() {
        return foods.size();
    }

    public void cacheFoodValues() {

    }

    public CompoundTag addToTag(CompoundTag tag) {
        ListTag list = new ListTag();
        for (ItemStack stack : foods) {
            if (!stack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                stack.toTag(compoundTag);
                list.add(compoundTag);
            }
        }
        if (!list.isEmpty()) tag.put("Items", list);
        return tag;
    }

    public void setFromTag(CompoundTag tag) {
        foods.clear();
        ListTag list = tag.getList("Items", 10);
        for(int i = 0; i < list.size(); ++i) {
            CompoundTag stackTag = list.getCompound(i);
            foods.add(ItemStack.fromTag(stackTag));
        }
    }

    public boolean isEmpty() {
        return foods.size() == 0;
    }

    public boolean isComplete() {
        return hasBreadBottom() && hasBreadTop();
    }

    public boolean hasBreadBottom() {
        return getSize() > 0 && Sandwichable.BREADS.contains(foods.get(0).getItem());
    }

    public boolean hasBreadTop() {
        return Sandwichable.BREADS.contains(getTopFood().getItem());
    }

    public void ejectSandwich(World world, Vec3d pos) {
        if(getSize() > 0) {
            if(isComplete()){
                ItemStack item = new ItemStack(BlocksRegistry.SANDWICH);
                CompoundTag tag = addToTag(new CompoundTag());
                if(!tag.isEmpty()) {
                    item.putSubTag("BlockEntityTag", tag);
                }
                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY()+1.2, pos.getZ(), item);
                itemEntity.setToDefaultPickupDelay();
                clearFoodList();
                world.spawnEntity(itemEntity);
            } else {
                for(ItemStack stack : foods) {
                    if(!stack.isEmpty() && stack.getItem() != ItemsRegistry.SPREAD) {
                        ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY() + 1.2, pos.getZ(), stack);
                        item.setToDefaultPickupDelay();
                        world.spawnEntity(item);
                        clearFoodList();
                    }
                }
            }
        }
    }
    
    public void ejectTopFood(World world, Vec3d pos) {
        ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY()+1.2, pos.getZ(), removeTopFood());
        item.setToDefaultPickupDelay();
        world.spawnEntity(item);
    }

    public void interact(World world, Vec3d pos, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if(stack.getItem().equals(BlocksRegistry.SANDWICH.asItem()) && stack.getTag() != null && this.isEmpty()) {
            CompoundTag tag = stack.getOrCreateSubTag("BlockEntityTag");
            this.setFromTag(tag);
            stack.decrement(1);
        } else if(!this.hasBreadBottom() && !Sandwichable.BREADS.contains(stack.getItem())) {
            if(stack.isFood()) player.sendMessage(new TranslatableText("message.sandwichtable.bottombread"), true);
        } else if(!this.addFood(player, stack) && stack.isEmpty()) {
            if(player.isSneaking()) {
                if(this.isComplete()) this.ejectSandwich(world, pos);
                else player.sendMessage(new TranslatableText("message.sandwichtable.topbread"), true);
            } else if(!this.isEmpty()) {
                if(!player.isCreative()) ejectTopFood(world, pos);
                else removeTopFood();
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        render(matrices, vertexConsumers, light, overlay, 0, 0, 0);
    }

    @Environment(EnvType.CLIENT)
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, double xPush, double yPush, double zPush) {
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((90)));
        for (ItemStack food : foods) {
            MinecraftClient.getInstance().getItemRenderer().renderItem(food, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
            matrices.translate(0.0 + xPush, 0.0 + zPush, -0.034 - yPush);
        }
    }
}
