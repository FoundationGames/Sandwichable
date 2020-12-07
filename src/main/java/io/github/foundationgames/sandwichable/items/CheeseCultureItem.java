package io.github.foundationgames.sandwichable.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class CheeseCultureItem extends InfoTooltipItem implements BottleCrateStorable, CustomDurabilityBar {
    private final CheeseType type;
    private final int craftAmount;

    public CheeseCultureItem(CheeseType type, int craftAmount, Settings settings) {
        super(settings);
        this.type = type;
        this.craftAmount = craftAmount;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        fill(stack, craftAmount);
    }

    public ItemStack fill(ItemStack stack, int amount) {
        if(stack.getItem() == this) {
            CompoundTag tag = stack.getOrCreateSubTag("UsageData");
            int old = tag.getInt("uses");
            tag.putInt("uses", Math.min(Math.max(0, old + amount), 10));
        } else if (stack.getItem() == Items.GLASS_BOTTLE) {
            stack = new ItemStack(this, 1);
            stack.getOrCreateSubTag("UsageData").putInt("uses", Math.min(Math.max(0, amount), 10));
        }
        return stack;
    }

    public ItemStack deplete(ItemStack stack, int amount) {
        if(stack.getItem() == this) {
            CompoundTag tag = stack.getOrCreateSubTag("UsageData");
            int old = tag.getInt("uses");
            int newA = Math.min(Math.max(0, old - amount), 10);
            if(newA == 0) stack = new ItemStack(Items.GLASS_BOTTLE);
            else tag.putInt("uses", newA);
        }
        return stack;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if(this.isIn(group)) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateSubTag("UsageData").putInt("uses", 10);
            stacks.add(stack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("cheese.type."+type.toString()).formatted(Formatting.BLUE));
        int uses = stack.getOrCreateSubTag("UsageData").getInt("uses");
        tooltip.add(new TranslatableText("cheese_culture_bottle.tooltip.uses", uses).formatted(Formatting.DARK_GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }

    public CheeseType getCheeseType() {
        return this.type;
    }

    @Override
    public String getTranslationKey() {
        return "item.sandwichable.cheese_culture_bottle";
    }

    @Override
    public ItemStack bottleCrateRandomTick(Inventory inventory, ItemStack stack) {
        return fill(stack, 1);
    }

    @Override
    public float getBarLength(ItemStack stack) {
        int uses = stack.getOrCreateSubTag("UsageData").getInt("uses");
        return (float)uses / 10;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int uses = stack.getOrCreateSubTag("UsageData").getInt("uses");
        return uses == 1 ? 0xff4400 : 0x00d0ff;
    }

    @Override
    public boolean showBar(ItemStack stack) {
        int uses = stack.getOrCreateSubTag("UsageData").getInt("uses");
        return uses < 10 && uses != 0;
    }
}
