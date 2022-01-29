package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.BottleCrateBlock;
import io.github.foundationgames.sandwichable.blocks.entity.container.BottleCrateScreenHandler;
import io.github.foundationgames.sandwichable.items.BottleCrateStorable;
import io.github.foundationgames.sandwichable.util.Util;
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
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BottleCrateBlockEntity extends LockableContainerBlockEntity implements ExtendedScreenHandlerFactory, SidedInventory, SyncedBlockEntity {
    private DefaultedList<ItemStack> inventory;
    private final Random random = new Random();
    private int growthTicks = randomTime();

    public BottleCrateBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.BOTTLECRATE_BLOCKENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(21, ItemStack.EMPTY);
    }

    private void update() {
        world.updateComparators(pos, world.getBlockState(pos).getBlock());
        Util.sync(this);
        markDirty();
    }

    private void updateBlockState() {
        if(world != null && !world.isClient()) {
            int state = 0;
            int filledSlots = 0;
            for(ItemStack item : inventory) filledSlots += item.isEmpty() ? 0 : 1;
            if(filledSlots > 0) {
                state = Math.round(((float)filledSlots / 21) * 3) + 1;
            }
            if(world.getBlockState(pos).get(BottleCrateBlock.STAGE) != state) world.setBlockState(pos, world.getBlockState(pos).with(BottleCrateBlock.STAGE, state));
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, BottleCrateBlockEntity self) {
        self.growthTicks--;
        if(self.growthTicks <= 0) {
            self.tickItems(self.random);
            self.growthTicks = self.randomTime();
        }
    }

    private int randomTime() {
        return this.random.nextInt(1000) + 500;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(21, ItemStack.EMPTY);
        this.growthTicks = nbt.getInt("growthTicks");
        Inventories.readNbt(nbt, this.inventory);
        updateBlockState();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("growthTicks", growthTicks);
    }

    public void tickItems(Random random) {
        List<Integer> tickedSlots = new ArrayList<>();
        for (int i = 0; i < random.nextInt(2) + 1; i++) {
            int slot;
            do {
                slot = random.nextInt(21);
            } while(tickedSlots.contains(slot));
            tickedSlots.add(slot);
            ItemStack stack = getStack(slot);
            if(stack.getItem() instanceof BottleCrateStorable) {
                setStack(slot, ((BottleCrateStorable)stack.getItem()).bottleCrateRandomTick(this, stack, random));
            }
        }
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.sandwichable.bottle_crate");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BottleCrateScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public int size() {
        return 21;
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack r = inventory.get(slot).split(amount);
        update();
        updateBlockState();
        return r;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack r = inventory.remove(slot);
        update();
        updateBlockState();
        return r;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        update();
        updateBlockState();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        inventory.clear();
        update();
        updateBlockState();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] slots = new int[21];
        for(int i = 0; i < 21; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return stack.getItem() instanceof BottleCrateStorable || stack.getItem() == Items.GLASS_BOTTLE;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
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
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
