package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.ToasterBlock;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.explosion.Explosion;

import java.util.List;
import java.util.Optional;

public class ToasterBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private static int toastTime = 240;
    private int toastProgress = 0;
    private boolean toasting = false;
    private boolean smoking = false;
    private int smokeProgress = 0;

    private boolean currentlyPowered = false;
    private boolean previouslyPowered = false;
    private boolean updateNeighbors = false;

    public ToasterBlockEntity() {
        super(BlocksRegistry.TOASTER_BLOCKENTITY);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        items = DefaultedList.ofSize(2, ItemStack.EMPTY);
        toastProgress = tag.getInt("toastProgress");
        toasting = tag.getBoolean("toasting");
        smokeProgress = tag.getInt("smokeProgress");
        smoking = tag.getBoolean("smoking");
        Inventories.fromTag(tag, items);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("toastProgress", toastProgress);
        tag.putBoolean("toasting", toasting);
        tag.putInt("smokeProgress", smokeProgress);
        tag.putBoolean("smoking", smoking);
        Inventories.toTag(tag, items);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }

    private void explode() {
        world.breakBlock(pos, true);
        world.createExplosion(world.getClosestPlayer(pos.getX(), pos.getZ(), 8), pos.getX(), pos.getY(), pos.getZ(), 2.2F, true, Explosion.DestructionType.DESTROY);
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
            BasicInventory inv = new BasicInventory(items.get(i));
            Optional<ToastingRecipe> match = world.getRecipeManager().getFirstMatch(ToastingRecipe.Type.INSTANCE, inv, world);

            if(match.isPresent()) {
                items.set(i, match.get().getOutput().copy());
            } else {
                if(items.get(i).isFood()) {
                    items.set(i, new ItemStack(ItemsRegistry.BURNT_FOOD, 1));
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

    @Override
    public void tick() {
        int smokeTime = 80;
        if(updateNeighbors) {
            world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
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
}
