package io.github.foundationgames.sandwichable.blocks.entity.container;

import net.minecraft.container.BrewingStandContainer;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class DesalinatorContainer extends Container {
    public final Inventory inventory;

    public DesalinatorContainer(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(null, syncId); // Since we didn't create a ContainerType, we will place null here.
        this.inventory = inventory;
        checkContainerSize(inventory, 2);
        inventory.onInvOpen(playerInventory.player);
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
        return this.inventory.canPlayerUseInv(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.getInvSize()) {
                if(invSlot == 0) {
                    if (newStack.getItem().equals(Items.REDSTONE)) {
                        if (this.insertItem(originalStack, 0, 1, false) && !this.insertItem(originalStack, 0, 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.getInvSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    private class OutputSlot extends Slot {
        public OutputSlot(Inventory inventory, int invSlot, int xPosition, int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }
    }

    private class FuelSlot extends Slot {
        public FuelSlot(Inventory inventory, int invSlot, int xPosition, int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem().equals(Items.REDSTONE);
        }
    }
}
