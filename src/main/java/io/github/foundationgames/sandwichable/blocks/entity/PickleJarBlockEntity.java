package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class PickleJarBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {

    private PickleJarFluid fluid = PickleJarFluid.AIR;
    private int numItems = 0;
    private boolean areItemsPickled = false;
    private int pickleProgress = 0;
    public static final int pickleTime = 1200; //1200
    private static final int maxItems = 4;

    public PickleJarBlockEntity() {
        super(BlocksRegistry.PICKLEJAR_BLOCKENTITY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.fluid = PickleJarFluid.fromString(tag.getString("pickleJarFluid"));
        this.numItems = tag.getInt("numItems");
        this.areItemsPickled = tag.getBoolean("areItemsPickled");
        this.pickleProgress = tag.getInt("pickleProgress");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putString("pickleJarFluid", fluid.toString());
        tag.putInt("numItems", numItems);
        tag.putBoolean("areItemsPickled", areItemsPickled);
        tag.putInt("pickleProgress", pickleProgress);
        return tag;
    }

    public ActionResult onUse(World world, PlayerEntity player, Hand hand, BlockPos pos) {
        ItemStack playerStack = player.getStackInHand(hand);
        Item playerItem = playerStack.getItem();
        //add cucumber
        if(playerItem == ItemsRegistry.CUCUMBER && this.fluid == PickleJarFluid.WATER && numItems < maxItems) {
            if(!player.isCreative()) {
                playerStack.decrement(1);
            }
            numItems++;
            this.markDirty();
            return ActionResult.SUCCESS;
        }
        //add pickle
        if(playerItem == ItemsRegistry.PICKLED_CUCUMBER && this.fluid == PickleJarFluid.PICKLED_BRINE && numItems < maxItems) {
            if(!player.isCreative()) {
                playerStack.decrement(1);
            }
            numItems++;
            this.markDirty();
            return ActionResult.SUCCESS;
        }
        //add water
        if(playerItem == Items.WATER_BUCKET && fluid == PickleJarFluid.AIR) {
            if(!player.isCreative()) {
                player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1));
            }
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.8F, 1.0F);
            fluid = PickleJarFluid.WATER;
            this.markDirty();
            return ActionResult.SUCCESS;
        }
        //take water
        if(playerItem == Items.BUCKET && fluid == PickleJarFluid.WATER) {
            if(!player.isCreative()) {
                playerStack.decrement(1);
                player.giveItemStack(new ItemStack(Items.WATER_BUCKET, 1));
            }
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.8F, 1.0F);
            fluid = PickleJarFluid.AIR;
            this.markDirty();
            return ActionResult.SUCCESS;
        }
        //add salt
        if(playerItem == ItemsRegistry.SALT && fluid == PickleJarFluid.WATER && numItems > 0) {
            if(!player.isCreative()) {
                playerStack.decrement(1);
            }
            world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.BLOCKS, 0.8F, 1.0F);
            this.startPickling();
            this.markDirty();
            return ActionResult.SUCCESS;
        }

        //NON HELD-ITEM SPECIFIC CASES
        //take cucumber
        if(fluid == PickleJarFluid.WATER && !areItemsPickled && numItems > 0) {
            numItems--;
            ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.05, pos.getZ()+0.5, new ItemStack(ItemsRegistry.CUCUMBER));
            item.setToDefaultPickupDelay();
            world.spawnEntity(item);
            this.markDirty();
            return ActionResult.SUCCESS;
        }
        //take pickle
        if(fluid == PickleJarFluid.PICKLED_BRINE && areItemsPickled && numItems > 0) {
            numItems--;
            ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.05, pos.getZ()+0.5, new ItemStack(ItemsRegistry.PICKLED_CUCUMBER));
            item.setToDefaultPickupDelay();
            world.spawnEntity(item);
            if(numItems == 0) {
                this.fluid = PickleJarFluid.AIR;
                this.areItemsPickled = false;
            }
            this.markDirty();
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public PickleJarFluid getFluid() {
        return fluid;
    }

    public int getItemCount() {
        return numItems;
    }

    public boolean areItemsPickled() {
        return areItemsPickled;
    }

    public int getPickleProgress() {
        return pickleProgress;
    }

    private void startPickling() {
        this.fluid = PickleJarFluid.PICKLING_BRINE;
    }

    private void finishPickling() {
        this.fluid = PickleJarFluid.PICKLED_BRINE;
        this.pickleProgress = 0;
        this.areItemsPickled = true;
        this.markDirty();
    }

    @Override
    public void tick() {
        if(this.fluid == PickleJarFluid.PICKLING_BRINE && pickleProgress < pickleTime) {
            this.pickleProgress++;
        } else if(pickleProgress == pickleTime) {
            this.finishPickling();
        }
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(world.getBlockState(pos), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }
}
