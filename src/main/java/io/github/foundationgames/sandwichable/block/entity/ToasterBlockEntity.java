package io.github.foundationgames.sandwichable.block.entity;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.block.ToasterBlock;
import io.github.foundationgames.sandwichable.item.ItemsRegistry;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import java.util.Optional;

public class ToasterBlockEntity extends BlockEntity implements SidedInventory, BlockEntityClientSerializable {

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private static int toastTime = 240;
    private int toastProgress = 0;
    private boolean toasting = false;
    private boolean smoking = false;
    private int smokeProgress = 0;

    private boolean currentlyPowered = false;
    private boolean previouslyPowered = false;
    private boolean updateNeighbors = false;

    public ToasterBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.TOASTER_BLOCKENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        items = DefaultedList.ofSize(2, ItemStack.EMPTY);
        toastProgress = tag.getInt("toastProgress");
        toasting = tag.getBoolean("toasting");
        smokeProgress = tag.getInt("smokeProgress");
        smoking = tag.getBoolean("smoking");
        Inventories.readNbt(tag, items);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("toastProgress", toastProgress);
        tag.putBoolean("toasting", toasting);
        tag.putInt("smokeProgress", smokeProgress);
        tag.putBoolean("smoking", smoking);
        Inventories.writeNbt(tag, items);
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound compoundTag) {
        this.readNbt(compoundTag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound compoundTag) {
        return this.writeNbt(compoundTag);
    }

    private void explode() {
        if(!world.isClient) {
            world.removeBlock(pos, true);
            world.createExplosion(world.getClosestPlayer(pos.getX(), pos.getZ(), 8, 10, false), pos.getX(), pos.getY(), pos.getZ(), 2.2F, true, Explosion.DestructionType.DESTROY);
        }
    }

    public Direction getToasterFacing() {
        if(this.world.getBlockState(this.pos).getBlock() == BlocksRegistry.TOASTER) {
            return this.world.getBlockState(this.pos).get(Properties.HORIZONTAL_FACING);
        }
        return Direction.NORTH;
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public ItemStack takeItem() {
        int index = !items.get(1).isEmpty() ? 1 : 0;
        ItemStack stack = items.get(index);
        items.set(index, ItemStack.EMPTY);
        updateNeighbors = true;
        return stack;
    }

    public boolean addItem(Hand hand, PlayerEntity player) {
        if(!toasting) {
            ItemStack playerItem = player.getStackInHand(hand).copy();
            playerItem.setCount(1);
            if (!items.get(0).isEmpty() && !items.get(1).isEmpty()) {
                return false;
            }
            if (!player.isCreative()) {
                player.getStackInHand(hand).decrement(1);
            }
            int index = !items.get(0).isEmpty() ? 1 : 0;
            items.set(index, playerItem);
            updateNeighbors = true;
            return true;
        } return false;
    }

    public boolean hasMetalInside() {
        return Sandwichable.METAL_ITEMS.contains(items.get(0).getItem()) || Sandwichable.METAL_ITEMS.contains(items.get(1).getItem());
    }

    private void toastItems() {
        for (int i = 0; i < 2; i++) {
            SimpleInventory inv = new SimpleInventory(items.get(i));
            Optional<ToastingRecipe> match = world.getRecipeManager().getFirstMatch(ToastingRecipe.Type.INSTANCE, inv, world);

            if(match.isPresent()) {
                items.set(i, match.get().getOutput().copy());
            } else {
                if(items.get(i).isFood()) {
                    Item item = Sandwichable.SMALL_FOODS.contains(items.get(i).getItem()) ? ItemsRegistry.BURNT_MORSEL : ItemsRegistry.BURNT_FOOD;
                    items.set(i, new ItemStack(item, 1));
                }
            }
        }
    }

    public void startToasting() {
        if(this.world.getBlockState(this.pos).getBlock() == BlocksRegistry.TOASTER) {
            this.world.setBlockState(pos, this.world.getBlockState(this.pos).with(ToasterBlock.ON, true));
        }
        world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.5F, 0.8F);
        toastProgress = 0;
        toasting = true;
        updateNeighbors = true;
    }

    public void stopToasting() {
        if(this.world.getBlockState(this.pos).getBlock() == BlocksRegistry.TOASTER) {
            this.world.setBlockState(pos, this.world.getBlockState(this.pos).with(ToasterBlock.ON, false));
        }
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.BLOCKS, 0.8F, 4);
        toastProgress = 0;
        toasting = false;
        updateNeighbors = true;
    }

    public int getComparatorOutput() {
        int r = 0;
        for (int i = 0; i < 2; i++) {
            r += items.get(i).isEmpty() ? 0 : 1;
        }
        r = (int)Math.round(r * 7.5);
        return r;
    }

    public boolean isToasting() {
        return toasting;
    }

    public int getToastingProgress() {
        return toastProgress;
    }

    private boolean tickPitch = false;

    public static void tick(World world, BlockPos pos, BlockState state, ToasterBlockEntity be) {
        be.tick(world, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        int smokeTime = 80;
        if(updateNeighbors) {
            world.updateNeighbors(pos, state.getBlock());
            updateNeighbors = false;
        }
        previouslyPowered = currentlyPowered;
        currentlyPowered = world.isReceivingRedstonePower(pos);
        if(toasting) {
            toastProgress++;
            if(toastProgress % 4 == 0 && toastProgress != toastTime) {
                world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.05F, tickPitch ? 2.0F : 1.9F);
                tickPitch = !tickPitch;
            }
            if(this.hasMetalInside() || world.getBlockState(this.pos).get(Properties.WATERLOGGED)) {
                explode();
            }
        }
        if(toastProgress == toastTime) {
            stopToasting();
            toastItems();
            smoking = true;
        }
        if(smoking) {
            if(smokeProgress % 3 == 0) {
                world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 0, 0.03, 0);
            }
            smokeProgress++;
        } if (smokeProgress == smokeTime) { smoking = false; smokeProgress = 0; }
        if(currentlyPowered && !previouslyPowered) {
            if(!toasting) {startToasting(); }
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return items.get(slot).isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !world.getBlockState(pos).get(ToasterBlock.ON);
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = items.get(slot).copy();
        items.set(slot, ItemStack.EMPTY);
        Util.sync(this, world);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        Util.sync(this, world);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        items.clear();
        Util.sync(this, world);
    }
}
