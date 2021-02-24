package io.github.foundationgames.sandwichable.util;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.item.ItemsRegistry;
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
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Sandwich {
    private final ArrayList<ItemStack> foods = new ArrayList<>();

    public Sandwich() {}

    public boolean addFood(PlayerEntity player, ItemStack stack) {
        if(stack.getItem() == BlocksRegistry.SANDWICH.asItem()) return false;
        ItemStack g = addTopFoodFrom(player.isCreative() ? stack.copy() : stack);
        if(g == null) return false;
        if(!g.isEmpty() && !player.isCreative()) player.giveItemStack(g);
        return true;
    }
    
    public ItemStack addTopFoodFrom(ItemStack stack) {
        if(stack.getItem() == BlocksRegistry.SANDWICH.asItem()) return stack;
        if(SpreadRegistry.INSTANCE.itemHasSpread(stack.getItem())) {
            ItemStack spread = new ItemStack(ItemsRegistry.SPREAD, 1);
            SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem()).onPour(stack, spread);
            spread.getOrCreateTag().putString("spreadType", SpreadRegistry.INSTANCE.asString(SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem())));
            if(foods.size() > 0) spread.getOrCreateTag().putBoolean("onLoaf", Sandwichable.BREAD_LOAVES.contains(foods.get(0).getItem()));
            foods.add(spread);
            ItemStack r = SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem()).getResultItem();
            stack.decrement(1);
            return r;
        } else if(stack.isFood()) {
            foods.add(prepareAdd(stack.split(1)));
            return ItemStack.EMPTY;
        }
        return null;
    }

    public ItemStack removeTopFood() {
        if(foods.size() > 0) return prepareRemove(foods.remove(foods.size() - 1));
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
        for(ItemStack i : list) {
            foods.add(prepareAdd(i));
        }
    }

    public void clearFoodList() {
        foods.clear();
    }
    
    public int getSize() {
        return foods.size();
    }

    public void putDisplayValues(CompoundTag tag) {
        CompoundTag displayValues = new CompoundTag();
        int h = 0;
        float s = 0;
        for(ItemStack item : foods) {
            if(item.getItem().isFood()) {
                h += item.getItem().getFoodComponent().getHunger();
                s += item.getItem().getFoodComponent().getSaturationModifier();
            }
        }
        s /= Math.max(foods.size(), 1);
        displayValues.putInt("hunger", h);
        displayValues.putFloat("saturation", s);
        tag.put("DisplayValues", displayValues);
    }

    public static DisplayValues getDisplayValues(CompoundTag displayValuesTag) {
        return new DisplayValues(displayValuesTag.getInt("hunger"), displayValuesTag.getFloat("saturation"));
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
        addFromTag(tag);
    }

    public void addFromTag(CompoundTag tag) {
        ListTag list = tag.getList("Items", 10);
        ItemStack stack;
        for(int i = 0; i < list.size(); ++i) {
            CompoundTag stackTag = list.getCompound(i);
            stack = ItemStack.fromTag(stackTag);
            if(stack.getItem() != BlocksRegistry.SANDWICH.asItem()) foods.add(prepareAdd(stack));
        }
    }

    private ItemStack prepareAdd(ItemStack stack) {
        stack.getOrCreateTag().putInt("s", (this.foods.size() % 3) + 1);
        return stack;
    }

    private ItemStack prepareRemove(ItemStack stack) {
        stack.getOrCreateTag().remove("s");
        return stack;
    }

    public boolean isEmpty() {
        return foods.size() == 0;
    }

    public boolean isComplete() {
        return hasBreadBottom() && hasBreadTop() && getSize() >= 2;
    }

    public boolean hasBreadBottom() {
        return getSize() > 0 && Sandwichable.isBread(foods.get(0).getItem());
    }

    public boolean hasBreadTop() {
        return Sandwichable.isBread(getTopFood().getItem());
    }

    public ItemStack createSandwich() {
        if(isComplete()) {
            ItemStack item = new ItemStack(BlocksRegistry.SANDWICH);
            CompoundTag tag = addToTag(new CompoundTag());
            putDisplayValues(tag);
            if(!tag.isEmpty()) {
                item.putSubTag("BlockEntityTag", tag);
            }
            return item;
        }
        return ItemStack.EMPTY;
    }

    public void ejectSandwich(World world, Vec3d pos) {
        if(getSize() > 0) {
            if(isComplete()) {
                ItemStack item = createSandwich();
                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY()+1.2, pos.getZ(), item);
                itemEntity.setToDefaultPickupDelay();
                clearFoodList();
                world.spawnEntity(itemEntity);
            } else {
                for(ItemStack stack : foods) {
                    if(!stack.isEmpty() && stack.getItem() != ItemsRegistry.SPREAD) {
                        ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY() + 1.2, pos.getZ(), prepareRemove(stack));
                        item.setToDefaultPickupDelay();
                        world.spawnEntity(item);
                    }
                }
                clearFoodList();
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
        if(stack.getItem().equals(BlocksRegistry.SANDWICH.asItem())) {
            if(stack.getTag() != null) {
                CompoundTag tag = stack.getOrCreateSubTag("BlockEntityTag");
                this.addFromTag(tag);
                stack.decrement(1);
            }
        } else if(!this.hasBreadBottom() && !Sandwichable.isBread(stack.getItem())) {
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
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((90)));
        for (ItemStack food : foods) {
            MinecraftClient.getInstance().getItemRenderer().renderItem(food, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
            matrices.translate(0.0, 0.0, -0.03124);
        }
    }

    public static class DisplayValues {
        private final int hunger;
        private final float saturation;

        public DisplayValues(int hunger, float saturation) {
            this.hunger = hunger;
            this.saturation = saturation;
        }

        public int getHunger() {
            return hunger;
        }

        public float getSaturation() {
            return saturation;
        }
    }
}
