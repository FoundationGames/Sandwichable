package io.github.foundationgames.sandwichable.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class CheeseCultureItem extends InfoTooltipItem implements BottleCrateStorable {
    private final CheeseType type;
    private final int craftAmount;
    private final float growthChance;

    public CheeseCultureItem(CheeseType type, int craftAmount, float growthChance, Settings settings) {
        super(settings);
        this.type = type;
        this.craftAmount = craftAmount;
        this.growthChance = growthChance;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        fill(stack, craftAmount);
    }

    public ItemStack fill(ItemStack stack, int amount) {
        if(stack.getItem() == this) {
            NbtCompound tag = stack.getOrCreateSubNbt("UsageData");
            int old = tag.getInt("uses");
            tag.putInt("uses", Math.min(Math.max(0, old + amount), 10));
        } else if (stack.getItem() == Items.GLASS_BOTTLE) {
            stack = new ItemStack(this, 1);
            stack.getOrCreateSubNbt("UsageData").putInt("uses", Math.min(Math.max(0, amount), 10));
        }
        return stack;
    }

    public ItemStack deplete(ItemStack stack, int amount) {
        if(stack.getItem() == this) {
            NbtCompound tag = stack.getOrCreateSubNbt("UsageData");
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
            stack.getOrCreateSubNbt("UsageData").putInt("uses", 10);
            stacks.add(stack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("cheese.type."+type.toString()).formatted(Formatting.BLUE));
        int uses = stack.getOrCreateSubNbt("UsageData").getInt("uses");
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
    public ItemStack bottleCrateRandomTick(Inventory inventory, ItemStack stack, Random random) {
        return random.nextFloat() <= growthChance ? fill(stack, 1) : stack;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int uses = stack.getOrCreateSubNbt("UsageData").getInt("uses");
        return (int)(13 * ((float)uses / 10));
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        int uses = stack.getOrCreateSubNbt("UsageData").getInt("uses");
        return uses == 1 ? 0xff0000 : uses <= 3 ? 0x5465ff : 0x0099ff;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        int uses = stack.getOrCreateSubNbt("UsageData").getInt("uses");
        return uses < 10 && uses != 0;
    }
}
