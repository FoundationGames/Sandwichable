package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.DesalinatorBlock;
import io.github.foundationgames.sandwichable.blocks.entity.container.DesalinatorContainer;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;

public class DesalinatorBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable, BlockEntityClientSerializable {
    private DefaultedList<ItemStack> inventory;
    private int waterAmount = 0;
    private int evaporateProgress = 0;
    private int fuelBurnProgress = 0;
    private boolean burning = false;
    private boolean evaporating = false;
    public static final int maxWaterAmount = 4;
    public static final int evaporateTime = 400;
    public static final int fuelBurnTime = 495;

    public DesalinatorBlockEntity() {
        super(BlocksRegistry.DESALINATOR_BLOCKENTITY);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        waterAmount = tag.getInt("waterAmount");
        evaporateProgress = tag.getInt("evaporateProgress");
        fuelBurnProgress = tag.getInt("fuelBurnProgress");
        Inventories.fromTag(tag, this.inventory);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.inventory);
        tag.putInt("waterAmount", waterAmount);
        tag.putInt("evaporateProgress", evaporateProgress);
        tag.putInt("fuelBurnProgress", fuelBurnProgress);
        return tag;
    }

    private void finishEvaporating() {
        waterAmount--;
        if(world.getBiome(pos).getCategory() == Biome.Category.OCEAN || world.getBiome(pos).getCategory() == Biome.Category.BEACH) {
            if(this.inventory.get(1).isEmpty()) {
                this.inventory.set(1, new ItemStack(ItemsRegistry.SALT));
            } else {
                this.inventory.get(1).increment(1);
            }
        }
        this.evaporateProgress = 0;
        this.evaporating = false;
        world.setBlockState(pos, world.getBlockState(pos).with(DesalinatorBlock.ON, false));
        this.markDirty();
    }

    private void stopBurning() {
        burning = false;
        fuelBurnProgress = 0;
        this.markDirty();
    }

    private void startBurning() {
        burning = true;
        this.markDirty();
    }

    private void startEvaporating() {
        if(this.inventory.get(1).isEmpty() || this.inventory.get(1).getItem() == ItemsRegistry.SALT) {
            evaporating = true;
            world.setBlockState(pos, world.getBlockState(pos).with(DesalinatorBlock.ON, true));
            this.markDirty();
        }
    }

    private void tryUpdateStateToOff() {
        if(world.getBlockState(pos).get(DesalinatorBlock.ON)) {
            world.setBlockState(pos, world.getBlockState(pos).with(DesalinatorBlock.ON, false));
        }
    }

    @Override
    public void tick() {
        if(world.getBlockState(pos).getBlock() == BlocksRegistry.DESALINATOR) {
            if(this.inventory.get(0).getCount() > 0 && this.waterAmount > 0) {
                if(!burning) {
                    this.inventory.get(0).decrement(1);
                    startBurning();
                }
                this.markDirty();
            }
            if(burning) {
                fuelBurnProgress++;
                if(fuelBurnProgress == fuelBurnTime) {
                    this.stopBurning();
                }
                if(waterAmount > 0) {
                    if(!evaporating && this.inventory.get(1).getCount() < 64) {
                        this.startEvaporating();
                    }
                }
                this.markDirty();
            }
            if(evaporating && burning) {
                evaporateProgress++;
                if(evaporateProgress == evaporateTime) {
                    finishEvaporating();
                }
                world.addParticle(ParticleTypes.CLOUD, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 0, 0.07, 0);
            }
            if(evaporating && !burning) {
                evaporating = false;
                tryUpdateStateToOff();
            }
            if(!evaporating && evaporateProgress > 0) {
                evaporateProgress--;
            }
            if(world.getBlockState(pos).get(Properties.WATERLOGGED) && waterAmount < maxWaterAmount) {
                world.setBlockState(pos, world.getBlockState(pos).with(Properties.WATERLOGGED, false));
                waterAmount++;
                world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
            }
        }
    }

    public int getEvaporateProgress() {
        return evaporateProgress;
    }

    public int getFuelBurnProgress() {
        return fuelBurnProgress;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public boolean isBurning() {
        return burning;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.sandwichable.desalinator");
    }

    @Override
    protected Container createContainer(int syncId, PlayerInventory playerInventory) {
        return new DesalinatorContainer(syncId, playerInventory, this);
    }

    @Override
    public int getInvSize() {
        return 2;
    }

    @Override
    public boolean isInvEmpty() {
        return this.inventory.get(0).isEmpty() && this.inventory.get(1).isEmpty();
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        this.markDirty();
        return this.inventory.get(slot).split(amount);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        this.markDirty();
        return this.inventory.remove(slot);
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        if(slot == 0) {
            if(stack.getItem() == Items.REDSTONE) {
                this.inventory.set(slot, stack);
            }
        }
        this.markDirty();
    }

    @Override
    public int[] getInvAvailableSlots(Direction side) {
        if(side == Direction.DOWN) {
            return new int[] {1};
        } else {
            return new int[] {0};
        }
    }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
        if(slot == 0) {
            return stack.getItem() == Items.REDSTONE;
        }
        return false;
    }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
        return slot == 1;
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return true;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}
