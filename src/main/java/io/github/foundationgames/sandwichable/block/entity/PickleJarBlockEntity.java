package io.github.foundationgames.sandwichable.block.entity;

import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.common.CommonTags;
import io.github.foundationgames.sandwichable.item.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PickleJarBlockEntity extends BlockEntity implements SidedInventory, Tickable, BlockEntityClientSerializable {

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
        if(playerItem.isIn(CommonTags.CUCUMBER) && this.fluid == PickleJarFluid.WATER && numItems < maxItems) {
            if(!player.isCreative()) {
                playerStack.decrement(1);
            }
            if(numItems == 0) this.areItemsPickled = false;
            numItems++;
            update();
            return ActionResult.SUCCESS;
        }
        //add pickle
        if(playerItem.isIn(CommonTags.PICKLED_CUCUMBER) && this.fluid == PickleJarFluid.PICKLED_BRINE && numItems < maxItems) {
            if(!player.isCreative()) {
                playerStack.decrement(1);
            }
            if(numItems == 0) this.areItemsPickled = true;
            numItems++;
            update();
            return ActionResult.SUCCESS;
        }
        //add water
        if(playerItem == Items.WATER_BUCKET && fluid == PickleJarFluid.AIR) {
            if(!player.isCreative()) {
                player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1));
            }
            fillWater(true);
            return ActionResult.SUCCESS;
        }
        //add brine
        if(playerItem == ItemsRegistry.PICKLE_BRINE_BUCKET && fluid == PickleJarFluid.AIR) {
            if(!player.isCreative()) {
                player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1));
            }
            fillBrine(true);
            return ActionResult.SUCCESS;
        }
        //take water
        if(playerItem == Items.BUCKET && (fluid == PickleJarFluid.WATER || fluid == PickleJarFluid.PICKLED_BRINE)) {
            if(!player.isCreative()) {
                playerStack.decrement(1);
                player.giveItemStack(new ItemStack(fluid == PickleJarFluid.WATER ? Items.WATER_BUCKET : ItemsRegistry.PICKLE_BRINE_BUCKET, 1));
            }
            emptyFluid(true);
            return ActionResult.SUCCESS;
        }
        //add salt
        if(playerItem.isIn(CommonTags.SALT) && fluid == PickleJarFluid.WATER && numItems > 0) {
            if(!player.isCreative()) {
                playerStack.decrement(1);
            }
            this.startPickling();
            update();
            return ActionResult.SUCCESS;
        }

        //NON HELD-ITEM SPECIFIC CASES
        //take cucumber
        if(fluid == PickleJarFluid.WATER && !areItemsPickled && numItems > 0) {
            numItems--;
            ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(ItemsRegistry.CUCUMBER));
            item.setToDefaultPickupDelay();
            world.spawnEntity(item);
            update();
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
            update();
            return ActionResult.SUCCESS;
        }
        update();
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

    public void startPickling() {
        this.fluid = PickleJarFluid.PICKLING_BRINE;
        world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.8F, 1.67F);
    }

    @Nullable
    public ItemStack emptyFluid(boolean withBucket) {
        if(this.getFluid() == PickleJarFluid.WATER || this.getFluid() == PickleJarFluid.PICKLED_BRINE) {
            for (int i = 0; i < numItems; i++) {
                ItemStack stack = areItemsPickled ? new ItemStack(ItemsRegistry.PICKLED_CUCUMBER) : new ItemStack(ItemsRegistry.CUCUMBER);
                ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, stack);
                world.spawnEntity(item);
            }
            this.fluid = PickleJarFluid.AIR;
            this.numItems = 0;
            if(withBucket) world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.8F, 1.0F);
            update();
            return new ItemStack(this.getFluid() == PickleJarFluid.WATER ? Items.WATER_BUCKET : ItemsRegistry.PICKLE_BRINE_BUCKET);
        }
        update();
        return null;
    }

    public void fillWater(boolean withBucket) {
        if(this.getFluid() == PickleJarFluid.AIR) {
            if(withBucket) world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.8F, 1.0F);
            fluid = PickleJarFluid.WATER;
            update();
        }
    }

    public void fillBrine(boolean withBucket) {
        if(this.getFluid() == PickleJarFluid.AIR) {
            if(withBucket) world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.8F, 1.0F);
            fluid = PickleJarFluid.PICKLED_BRINE;
            update();
        }
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

    public void update() {
        world.updateComparators(pos, world.getBlockState(pos).getBlock());
        Util.sync(this, world);
        markDirty();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[] {0, 1, 2, 3};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        slot = slot + 1;
         return slot > numItems && isInsertable(stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return numItems > 0;
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return numItems <= 0;
    }

    @Override
    public ItemStack getStack(int slot) {
        slot = slot + 1;
        ItemStack stack = getFluid() == PickleJarFluid.WATER && !areItemsPickled ? new ItemStack(ItemsRegistry.CUCUMBER) : getFluid() == PickleJarFluid.PICKLED_BRINE && areItemsPickled ? new ItemStack(ItemsRegistry.PICKLED_CUCUMBER) : ItemStack.EMPTY;
        ItemStack r = numItems > 0 ? slot <= numItems ? stack : ItemStack.EMPTY : ItemStack.EMPTY;
        return r;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = getStack(slot);
        if(numItems > 0) numItems--;
        if(numItems == 0 && getFluid() == PickleJarFluid.PICKLED_BRINE) fluid = PickleJarFluid.AIR;
        update();
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(isInsertable(stack)) numItems++;
        update();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        numItems = 0;
        update();
    }

    private boolean isInsertable(ItemStack stack) {
        if(numItems < 4) {
            return (stack.getItem() == ItemsRegistry.CUCUMBER && fluid == PickleJarFluid.WATER && !areItemsPickled) || (stack.getItem() == ItemsRegistry.PICKLED_CUCUMBER && fluid == PickleJarFluid.PICKLED_BRINE && areItemsPickled);
        }
        return false;
    }
}
