package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.world.World;

import java.util.Random;

public class BasinBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {

    private int fermentProgress = 0;
    public static final int fermentTime = 3600;
    private boolean hasCheese = false;
    private boolean fermenting = false;
    private boolean hasMilk = false;

    private Random rng = new Random();

    public BasinBlockEntity() {
        super(BlocksRegistry.BASIN_BLOCKENTITY);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        fermentProgress = tag.getInt("fermentProgress");
        hasCheese = tag.getBoolean("hasCheese");
        fermenting = tag.getBoolean("fermenting");
        hasMilk = tag.getBoolean("hasMilk");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("fermentProgress", fermentProgress);
        tag.putBoolean("hasCheese", hasCheese);
        tag.putBoolean("fermenting", fermenting);
        tag.putBoolean("hasMilk", hasMilk);
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

    public ActionResult onBlockUse(PlayerEntity player, Hand hand) {
        ItemStack playerStack = player.getStackInHand(hand);
        if(this.hasCheese) {
            ItemEntity cheese = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(ItemsRegistry.CHEESE_WHEEL, 1));
            this.hasCheese = false;
            world.spawnEntity(cheese);
            return ActionResult.SUCCESS;
        }
        if(playerStack.getItem().equals(Items.MILK_BUCKET) && !this.hasMilk && !this.hasCheese && !this.fermenting) {
            if(!player.isCreative()) { player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1)); }
            this.hasMilk = true;
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.8F, 1.0F);
            return ActionResult.SUCCESS;
        }
        if(playerStack.getItem().equals(Items.BUCKET) && this.hasMilk) {
            player.setStackInHand(hand, new ItemStack(Items.MILK_BUCKET, 1));
            this.hasMilk = false;
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.8F, 1.0F);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public int getFermentProgress() {
        return fermentProgress;
    }
    public boolean isFermenting() {
        return fermenting;
    }
    public boolean hasCheese() {
        return hasCheese;
    }
    public boolean hasMilk() {
        return hasMilk;
    }

    public void startFermenting() {
        if(hasMilk) {
            hasMilk = false;
            fermentProgress = 0;
            fermenting = true;
            hasCheese = false;
        }
    }
    public void finishFermenting() {
        if(fermenting) {
            fermentProgress = 0;
            fermenting = false;
            hasMilk = false;
            hasCheese = true;
        }
    }

    private int tickN = 0;

    @Override
    public void tick() {
        //This bit also seems redundant, but conditions to start fermenting may change in later update
        if(hasMilk) {
            startFermenting();
        }
        if(fermenting) {
            fermentProgress++;
        }
        if(fermentProgress == fermentTime) {
            finishFermenting();
        }
        if(hasCheese && tickN % 15 == 0) {
            double ox = ((double)rng.nextInt(10)/16);
            double oz = ((double)rng.nextInt(10)/16);
            world.addParticle(new DustParticleEffect(0.93F, 0.78F, 0.2F, 1.0F), pos.getX()+ox+0.2, pos.getY()+0.4, pos.getZ()+oz+0.2, 0.0D, 0.09D, 0.0D);
        }
        if(tickN < 60) { tickN++; } else if(tickN == 60) { tickN = 0; }
    }
}
