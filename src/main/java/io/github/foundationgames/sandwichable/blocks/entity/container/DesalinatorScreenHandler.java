package io.github.foundationgames.sandwichable.blocks.entity.container;

import io.github.foundationgames.sandwichable.Sandwichable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class DesalinatorScreenHandler extends ScreenHandler {
    public final Inventory inventory;

    public DesalinatorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(Sandwichable.DESALINATOR_HANDLER, syncId);
        this.inventory = inventory;
        checkSize(inventory, 2);
        inventory.onOpen(playerInventory.player);
        this.addSlot(new FuelSlot(inventory, 0, 80, 49));
        this.addSlot(new OutputSlot(inventory, 1, 101, 16));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(slotStack, this.inventory.size(), 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(slotStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
        }

        return itemStack;
    }

    private static class OutputSlot extends Slot {
        public OutputSlot(Inventory inventory, int invSlot, int xPosition, int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }
    }

    private static class FuelSlot extends Slot {
        public FuelSlot(Inventory inventory, int invSlot, int xPosition, int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem().equals(Items.REDSTONE);
        }
    }
}
