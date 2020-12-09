package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.BottleCrateBlock;
import io.github.foundationgames.sandwichable.blocks.entity.container.BottleCrateScreenHandler;
import io.github.foundationgames.sandwichable.items.BottleCrateStorable;
import io.github.foundationgames.sandwichable.util.Util;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BottleCrateBlockEntity extends LockableContainerBlockEntity implements Tickable, ExtendedScreenHandlerFactory, SidedInventory, BlockEntityClientSerializable {
    private DefaultedList<ItemStack> items;
    private final Random random = new Random();
    private int growthTicks = randomTime();

    public BottleCrateBlockEntity() {
        super(BlocksRegistry.BOTTLECRATE_BLOCKENTITY);
        items = DefaultedList.ofSize(21, ItemStack.EMPTY);
    }

    private void update() {
        world.updateComparators(pos, world.getBlockState(pos).getBlock());
        Util.sync(this, world);
        markDirty();
    }

    private void updateBlockState() {
        if(!world.isClient()) {
            int state = 0;
            int filledSlots = 0;
            for(ItemStack item : items) filledSlots += item.isEmpty() ? 0 : 1;
            if(filledSlots > 0) {
                state = Math.round(((float)filledSlots / 21) * 3) + 1;
            }
            if(world.getBlockState(pos).get(BottleCrateBlock.STAGE) != state) world.setBlockState(pos, world.getBlockState(pos).with(BottleCrateBlock.STAGE, state));
        }
    }

    @Override
    public void tick() {
        growthTicks--;
        if(growthTicks <= 0) {
            tickItems(this.random);
            growthTicks = randomTime();
        }
    }

    private int randomTime() {
        return this.random.nextInt(1500) + 1000;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.items = DefaultedList.ofSize(21, ItemStack.EMPTY);
        Inventories.fromTag(tag, items);
        this.growthTicks = tag.getInt("growthTicks");
        updateBlockState();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.items);
        tag.putInt("growthTicks", growthTicks);
        return tag;
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
        return items.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack r = items.get(slot).split(amount);
        update();
        updateBlockState();
        return r;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack r = items.remove(slot);
        update();
        updateBlockState();
        return r;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        update();
        updateBlockState();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        items.clear();
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
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(this.getCachedState(), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
