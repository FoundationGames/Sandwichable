package io.github.foundationgames.sandwichable.util;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sandwich {
    private final ArrayList<ItemStack> foods = new ArrayList<>();

    public Sandwich() {}

    public boolean addFood(PlayerEntity player, ItemStack stack) {
        int maxSize = player.getWorld().getGameRules().getInt(Sandwichable.SANDWICH_SIZE_RULE);
        if(stack.getItem() == BlocksRegistry.SANDWICH.asItem()) return false;
        if (maxSize >= 0 && this.getSize() >= maxSize && canAdd(stack)) {
            player.sendMessage(Text.translatable("message.sandwichtable.maxSize", maxSize).formatted(Formatting.RED), true);
            return false;
        }
        ItemStack g = addTopFoodFrom(player.isCreative() ? stack.copy() : stack);
        if(g == null) return false;
        if(!g.isEmpty() && !player.isCreative()) player.giveItemStack(g);
        return true;
    }

    public static boolean canAdd(ItemStack stack) {
        return stack.isFood() || SpreadRegistry.INSTANCE.itemHasSpread(stack.getItem());
    }

    public ItemStack tryAddTopFoodFrom(World world, ItemStack stack) {
        int maxSize = world.getGameRules().getInt(Sandwichable.SANDWICH_SIZE_RULE);
        if (maxSize >= 0 && this.getSize() >= maxSize && canAdd(stack)) {
            return null;
        }
        return addTopFoodFrom(stack);
    }
    
    public ItemStack addTopFoodFrom(ItemStack stack) {
        if(stack.getItem() == BlocksRegistry.SANDWICH.asItem()) return stack;
        if(SpreadRegistry.INSTANCE.itemHasSpread(stack.getItem())) {
            ItemStack spread = new ItemStack(ItemsRegistry.SPREAD, 1);
            SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem()).onPour(stack, spread);
            spread.getOrCreateNbt().putString("spreadType", SpreadRegistry.INSTANCE.asString(SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem())));
            if(foods.size() > 0) spread.getOrCreateNbt().putBoolean("onLoaf", foods.get(0).isIn(Sandwichable.BREAD_LOAVES));
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
        if(foods.size() > 0) return fixForEject(foods.remove(foods.size() - 1));
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

    public void putDisplayValues(NbtCompound tag) {
        NbtCompound displayValues = new NbtCompound();
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

    public static DisplayValues getDisplayValues(NbtCompound displayValuesTag) {
        return new DisplayValues(displayValuesTag.getInt("hunger"), displayValuesTag.getFloat("saturation"));
    }

    public NbtCompound writeToNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (ItemStack stack : foods) {
            if (!stack.isEmpty()) {
                NbtCompound NbtCompound = new NbtCompound();
                stack.writeNbt(NbtCompound);
                list.add(NbtCompound);
            }
        }
        nbt.put("Items", list);
        return nbt;
    }

    public void setFromNbt(NbtCompound nbt) {
        clearFoodList();
        addFromNbt(nbt);
    }

    public void addFromNbt(NbtCompound nbt) {
        NbtList list = nbt.getList("Items", 10);
        ItemStack stack;
        for(int i = 0; i < list.size(); ++i) {
            NbtCompound stackTag = list.getCompound(i);
            stack = ItemStack.fromNbt(stackTag);
            if(stack.getItem() != BlocksRegistry.SANDWICH.asItem()) foods.add(stack);
        }
    }

    /**
     * For handling sandwiches from previous versions,
     * to be removed in 1.17
     */
    private ItemStack fixForEject(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("s")) stack.getNbt().remove("s");
        return stack;
    }

    public boolean isEmpty() {
        return foods.size() == 0;
    }

    public boolean isComplete() {
        return hasBreadBottom() && hasBreadTop() && getSize() >= 2;
    }

    public boolean hasBreadBottom() {
        return getSize() > 0 && Sandwichable.isBread(foods.get(0));
    }

    public boolean hasBreadTop() {
        return Sandwichable.isBread(getTopFood());
    }

    public ItemStack createSandwich() {
        if(isComplete()) {
            ItemStack item = new ItemStack(BlocksRegistry.SANDWICH);
            NbtCompound tag = writeToNbt(new NbtCompound());
            putDisplayValues(tag);
            if(!tag.isEmpty()) {
                item.setSubNbt("BlockEntityTag", tag);
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
                        ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY() + 1.2, pos.getZ(), fixForEject(stack));
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

    public void interact(World world, Vec3d pos, PlayerEntity player, Hand hand, boolean intendsRemoval) {
        ItemStack stack = player.getStackInHand(hand);
        int maxSize = world.getGameRules().getInt(Sandwichable.SANDWICH_SIZE_RULE);
        if(stack.getItem().equals(BlocksRegistry.SANDWICH.asItem())) {
            if(stack.getNbt() != null) {
                NbtCompound tag = stack.getOrCreateSubNbt("BlockEntityTag");
                if (maxSize >= 0) {
                    Sandwich toAdd = new Sandwich();
                    toAdd.addFromNbt(tag);
                    if (toAdd.getSize() + this.getSize() > maxSize) {
                        player.sendMessage(Text.translatable("message.sandwichtable.maxSize", maxSize).formatted(Formatting.RED), true);
                        return;
                    }
                }
                this.addFromNbt(tag);
                stack.decrement(1);
            }
        } else if(!this.hasBreadBottom() && !Sandwichable.isBread(stack)) {
            if(stack.isFood()) player.sendMessage(Text.translatable("message.sandwichtable.bottombread"), true);
        } else if(!this.addFood(player, stack) && stack.isEmpty()) {
            if(intendsRemoval) {
                if(this.isComplete()) this.ejectSandwich(world, pos);
                else player.sendMessage(Text.translatable("message.sandwichtable.topbread"), true);
            } else if(!this.isEmpty()) {
                if(!player.isCreative()) ejectTopFood(world, pos);
                else removeTopFood();
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((90)));
        int i = 0;
        for (ItemStack food : foods) {
            RenderFlags.RENDERING_SANDWICH_ITEM = (i % 3) + 1;
            MinecraftClient.getInstance().getItemRenderer().renderItem(food, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, null, Objects.hash(i));
            matrices.translate(0.0, 0.0, -0.03124);
            i++;
        }
        RenderFlags.RENDERING_SANDWICH_ITEM = 0;
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
