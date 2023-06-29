package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.ToasterBlock;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ToasterBlockEntity extends BlockEntity implements SidedInventory, SyncedBlockEntity {
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private @Nullable UUID lastUser;
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
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        items = DefaultedList.ofSize(2, ItemStack.EMPTY);
        toastProgress = nbt.getInt("toastProgress");
        toasting = nbt.getBoolean("toasting");
        smokeProgress = nbt.getInt("smokeProgress");
        smoking = nbt.getBoolean("smoking");
        if (nbt.contains("lastUser")) {
            this.lastUser = nbt.getUuid("lastUser");
        } else this.lastUser = null;
        Inventories.readNbt(nbt, items);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("toastProgress", toastProgress);
        nbt.putBoolean("toasting", toasting);
        nbt.putInt("smokeProgress", smokeProgress);
        nbt.putBoolean("smoking", smoking);
        if (this.lastUser == null) {
            nbt.remove("lastUser");
        } else nbt.putUuid("lastUser", this.lastUser);
        Inventories.writeNbt(nbt, items);
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

    private void explode() {
        if(!world.isClient) {
            world.removeBlock(pos, true);
            world.createExplosion(world.getClosestPlayer(pos.getX(), pos.getZ(), 8, 10, false), pos.getX(), pos.getY(), pos.getZ(), 2.2F, true, World.ExplosionSourceType.BLOCK);
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

    public Optional<PlayerEntity> getLastUser() {
        return Optional.ofNullable(this.lastUser).map(this.world::getPlayerByUuid);
    }

    public void setLastUser(@Nullable PlayerEntity player) {
        this.lastUser = (player == null ? null : player.getUuid());
    }

    public ItemStack takeItem(@Nullable PlayerEntity player) {
        int index = !items.get(1).isEmpty() ? 1 : 0;
        ItemStack stack = items.get(index);
        items.set(index, ItemStack.EMPTY);
        updateNeighbors = true;
        this.setLastUser(player);
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
            this.setLastUser(player);
            return true;
        } return false;
    }

    public boolean hasMetalInside() {
        return items.get(0).isIn(Sandwichable.METAL_ITEMS) || items.get(1).isIn(Sandwichable.METAL_ITEMS);
    }

    private void toastItems() {
        for (int i = 0; i < 2; i++) {
            SimpleInventory inv = new SimpleInventory(items.get(i));
            Optional<ToastingRecipe> match = world.getRecipeManager().getFirstMatch(ToastingRecipe.Type.INSTANCE, inv, world);

            boolean changed = false;
            if(match.isPresent()) {
                items.set(i, match.get().craft(inv, world.getRegistryManager()).copy());
                changed = true;
            } else {
                if(items.get(i).isFood()) {
                    var config = Util.getConfig();
                    ItemStack stack = items.get(i).isIn(Sandwichable.SMALL_FOODS) ?
                            config.itemOptions.burntMorselItemGetter.get().copy() : config.itemOptions.burntFoodItemGetter.get().copy();
                    items.set(i, stack);
                    changed = true;
                }
            }

            if (!world.isClient() && changed) {
                ItemStack advStack = items.get(i);
                this.getLastUser().ifPresent(player -> {
                    if (player instanceof ServerPlayerEntity) {
                        Sandwichable.TOAST_ITEM.trigger((ServerPlayerEntity) player, advStack);
                    }
                });
            }
        }
    }

    public void startToasting(@Nullable PlayerEntity player) {
        if(this.world.getBlockState(this.pos).getBlock() == BlocksRegistry.TOASTER) {
            this.world.setBlockState(pos, this.world.getBlockState(this.pos).with(ToasterBlock.ON, true));
        }
        world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.5F, 0.8F);
        toastProgress = 0;
        toasting = true;
        updateNeighbors = true;
        if (player != null) this.setLastUser(player);
    }

    public void stopToasting(@Nullable PlayerEntity player) {
        if(this.world.getBlockState(this.pos).getBlock() == BlocksRegistry.TOASTER) {
            this.world.setBlockState(pos, this.world.getBlockState(this.pos).with(ToasterBlock.ON, false));
        }
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), SoundCategory.BLOCKS, 0.8F, 4);
        toastProgress = 0;
        toasting = false;
        updateNeighbors = true;
        if (player != null) this.setLastUser(player);
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

    public static void tick(World world, BlockPos pos, BlockState state, ToasterBlockEntity self) {
        int smokeTime = 80;
        if (!world.isClient()) {
            if(self.updateNeighbors) {
                world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
                self.updateNeighbors = false;
            }
            self.previouslyPowered = self.currentlyPowered;
            self.currentlyPowered = world.isReceivingRedstonePower(pos);
            if(self.toasting) {
                self.toastProgress++;
                if(self.toastProgress % 4 == 0 && self.toastProgress != toastTime) {
                    world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.05F, self.tickPitch ? 2.0F : 1.9F);
                    self.tickPitch = !self.tickPitch;
                }
                if(self.hasMetalInside() || world.getBlockState(pos).get(Properties.WATERLOGGED)) {
                    self.explode();
                }
                Util.sync(self);
            }
            if(self.toastProgress == toastTime) {
                self.stopToasting(null);
                self.toastItems();
                self.smoking = true;
                Util.sync(self);
            }
        }

        if(self.smoking) {
            if(self.smokeProgress % 3 == 0) {
                world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 0, 0.03, 0);
            }
            self.smokeProgress++;
        } if (self.smokeProgress == smokeTime) {
            self.smoking = false;
            self.smokeProgress = 0;
        }
        if(self.currentlyPowered && !self.previouslyPowered) {
            if(!self.toasting) self.startToasting(null);
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
        setLastUser(null);
        Util.sync(this);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        setLastUser(null);
        Util.sync(this);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        items.clear();
        Util.sync(this);
    }
}
