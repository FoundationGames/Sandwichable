package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.DesalinatorBlock;
import io.github.foundationgames.sandwichable.blocks.entity.container.DesalinatorScreenHandler;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
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
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DesalinatorBlockEntity extends LockableContainerBlockEntity implements ExtendedScreenHandlerFactory, SidedInventory, SyncedBlockEntity {
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

    public DesalinatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.DESALINATOR_BLOCKENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        fluidAmount = nbt.getInt("waterAmount");
        evaporateProgress = nbt.getInt("evaporateProgress");
        fuelBurnProgress = nbt.getInt("fuelBurnProgress");
        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("waterAmount", fluidAmount);
        nbt.putInt("evaporateProgress", evaporateProgress);
        nbt.putInt("fuelBurnProgress", fuelBurnProgress);
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

    public static void tick(World world, BlockPos pos, BlockState state, DesalinatorBlockEntity self) {
        if(world.getBlockState(pos).getBlock() == BlocksRegistry.DESALINATOR) {
            if(self.inventory.get(0).getCount() > 0 && self.fluidAmount > 0) {
                if(!self.burning) {
                    self.inventory.get(0).decrement(1);
                    self.startBurning();
                }
                self.markDirty();
            }
            if(self.burning) {
                self.fuelBurnProgress++;
                if(self.fuelBurnProgress == fuelBurnTime) {
                    self.stopBurning();
                }
                if(self.fluidAmount > 0) {
                    if(!self.evaporating && self.inventory.get(1).getCount() < 64) {
                        self.startEvaporating();
                    }
                }
                self.markDirty();
            }
            if(self.evaporating && self.burning) {
                self.evaporateProgress += self.isPickleBrine ? 1 : 2;
                if(self.evaporateProgress >= evaporateTime) {
                    self.finishEvaporating();
                }
                world.addParticle(ParticleTypes.CLOUD, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 0, 0.07, 0);
            }
            if(self.evaporating && !self.burning) {
                self.evaporating = false;
                self.tryUpdateStateToOff();
            }
            if(!self.evaporating && self.evaporateProgress > 0) {
                self.evaporateProgress--;
            }
            DesalinatorBlock.FluidType fluid = world.getBlockState(pos).get(DesalinatorBlock.FLUID);
            if(fluid != DesalinatorBlock.FluidType.NONE && self.fluidAmount < maxFluidAmount && (((fluid == DesalinatorBlock.FluidType.PICKLE_BRINE) == self.isPickleBrine) || self.fluidAmount == 0)) {
                self.isPickleBrine = fluid == DesalinatorBlock.FluidType.PICKLE_BRINE;
                world.setBlockState(pos, world.getBlockState(pos).with(DesalinatorBlock.FLUID, DesalinatorBlock.FluidType.NONE));
                self.fluidAmount++;
                world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
            }
        }
        if(self.evaporateProgress % 6 == 0 || self.evaporating != self.wasEvaporating) {
            SoundEvent sound= null;
            if(self.evaporating && !self.wasEvaporating) sound = Sandwichable.DESALINATOR_START;
            else if(!self.evaporating && self.wasEvaporating) sound = Sandwichable.DESALINATOR_STOP;
            else if(self.evaporating) sound = Sandwichable.DESALINATOR_RUN;

            if(sound != null) world.playSound(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, sound, SoundCategory.BLOCKS, 0.4f, 1.0f, false);
        }
        self.wasEvaporating = self.evaporating;
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

    public boolean isWaterSaline() { return isPickleBrine || world.getBiome(pos).isIn(Sandwichable.SALT_WATER_BODIES) || world.getBlockState(pos.down()).isIn(Sandwichable.SALT_PRODUCING_BLOCKS); }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.sandwichable.desalinator");
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
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return this.getPacket();
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
