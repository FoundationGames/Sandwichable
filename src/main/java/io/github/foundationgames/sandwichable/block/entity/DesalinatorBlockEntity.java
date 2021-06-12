package io.github.foundationgames.sandwichable.block.entity;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.block.DesalinatorBlock;
import io.github.foundationgames.sandwichable.block.entity.container.DesalinatorScreenHandler;
import io.github.foundationgames.sandwichable.item.ItemsRegistry;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;

public class DesalinatorBlockEntity extends LockableContainerBlockEntity implements ExtendedScreenHandlerFactory, SidedInventory, Tickable, BlockEntityClientSerializable {
    private DefaultedList<ItemStack> inventory;
    private int fluidAmount = 0;
    private int evaporateProgress = 0;
    private int fuelBurnProgress = 0;
    private boolean isPickleBrine = false;
    private boolean burning = false;
    private boolean evaporating = false;
    private boolean wasEvaporating = evaporating;
    public static final int maxFluidAmount = 4;
    public static final int evaporateTime = 185;
    public static final int fuelBurnTime = 990;

    public DesalinatorBlockEntity() {
        super(BlocksRegistry.DESALINATOR_BLOCKENTITY);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    }

    @Override
    public void readNbt(BlockState state, NbtCompound tag) {
        super.readNbt(state, tag);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        fluidAmount = tag.getInt("waterAmount");
        evaporateProgress = tag.getInt("evaporateProgress");
        fuelBurnProgress = tag.getInt("fuelBurnProgress");
        Inventories.readNbt(tag, this.inventory);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, this.inventory);
        tag.putInt("waterAmount", fluidAmount);
        tag.putInt("evaporateProgress", evaporateProgress);
        tag.putInt("fuelBurnProgress", fuelBurnProgress);
        return tag;
    }

    private void finishEvaporating() {
        fluidAmount--;
        if(isWaterSaline()) {
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
            if(this.inventory.get(0).getCount() > 0 && this.fluidAmount > 0) {
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
                if(fluidAmount > 0) {
                    if(!evaporating && this.inventory.get(1).getCount() < 64) {
                        this.startEvaporating();
                    }
                }
                this.markDirty();
            }
            if(evaporating && burning) {
                evaporateProgress += isPickleBrine ? 1 : 2;
                if(evaporateProgress >= evaporateTime) {
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
            DesalinatorBlock.FluidType fluid = world.getBlockState(pos).get(DesalinatorBlock.FLUID);
            if(fluid != DesalinatorBlock.FluidType.NONE && fluidAmount < maxFluidAmount && (((fluid == DesalinatorBlock.FluidType.PICKLE_BRINE) == isPickleBrine) || this.fluidAmount == 0)) {
                this.isPickleBrine = fluid == DesalinatorBlock.FluidType.PICKLE_BRINE;
                world.setBlockState(pos, world.getBlockState(pos).with(DesalinatorBlock.FLUID, DesalinatorBlock.FluidType.NONE));
                fluidAmount++;
                world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
            }
        }
        if(evaporateProgress % 6 == 0 || evaporating != wasEvaporating) {
            SoundEvent sound= null;
            if(evaporating && !wasEvaporating) sound = Sandwichable.DESALINATOR_START;
            else if(!evaporating && wasEvaporating) sound = Sandwichable.DESALINATOR_STOP;
            else if(evaporating) sound = Sandwichable.DESALINATOR_RUN;

            if(sound != null) world.playSound(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, sound, SoundCategory.BLOCKS, 0.4f, 1.0f, false);
        }
        wasEvaporating = evaporating;
    }

    public int getEvaporateProgress() {
        return evaporateProgress;
    }

    public int getFuelBurnProgress() {
        return fuelBurnProgress;
    }

    public int getWaterAmount() {
        return fluidAmount;
    }

    public boolean isBurning() {
        return burning;
    }

    public boolean isPickleBrine() {
        return isPickleBrine;
    }

    public boolean isWaterSaline() { return isPickleBrine || world.getBiome(pos).getCategory() == Biome.Category.OCEAN || world.getBiome(pos).getCategory() == Biome.Category.BEACH || world.getBlockState(pos.down()).getBlock().isIn(Sandwichable.SALT_PRODUCING_BLOCKS); }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.sandwichable.desalinator");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new DesalinatorScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.get(0).isEmpty() && this.inventory.get(1).isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        this.markDirty();
        return this.inventory.remove(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        this.markDirty();
        return this.inventory.get(slot).split(amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(slot == 0) {
            if(stack.getItem() == Items.REDSTONE) {
                this.inventory.set(slot, stack);
            }
        }
        this.markDirty();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if(side == Direction.DOWN) {
            return new int[] {1};
        } else {
            return new int[] {0};
        }
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        if(slot == 0) {
            return stack.getItem() == Items.REDSTONE;
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 1;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void fromClientTag(NbtCompound compoundTag) {
        this.readNbt(world.getBlockState(pos), compoundTag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound compoundTag) {
        return this.writeNbt(compoundTag);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
