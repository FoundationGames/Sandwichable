package io.github.foundationgames.sandwichable.blocks.entity.container;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.items.BottleCrateStorable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BottleCrateScreenHandler extends ScreenHandler {
    public final Inventory inventory;

    public BottleCrateScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(Sandwichable.BOTTLE_CRATE_HANDLER, syncId);
        this.inventory = inventory;
        checkSize(inventory, 21);
        inventory.onOpen(playerInventory.player);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                this.addSlot(new BottleSlot(inventory, (i * 7) + j, 26 + j * 18, 20 + i * 18));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 91 + i * 18));
            }
        }
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 149));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(slotStack, this.inventory.size(), 57, true)) {
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

    private static class BottleSlot extends Slot {
        public BottleSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof BottleCrateStorable || stack.getItem() == Items.GLASS_BOTTLE;
        }
    }
}
