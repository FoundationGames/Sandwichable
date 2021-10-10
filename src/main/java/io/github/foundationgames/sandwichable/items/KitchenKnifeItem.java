package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class KitchenKnifeItem extends InfoTooltipItem implements CustomDurabilityBar {
    public KitchenKnifeItem(Settings settings) {
        super(settings);
    }

    public static int getSharpness(ItemStack knife) {
        if (!(knife.getItem() instanceof KitchenKnifeItem)) return 0;
        NbtCompound kData = knife.getOrCreateSubTag("KnifeData");
        if (!kData.contains("sharpness")) {
            SandwichableConfig.KitchenKnifeOption opt = Util.getConfig().getKnifeOption(knife.getItem());
            if (opt != null) {
                kData.putInt("sharpness", opt.sharpness);
            }
        }
        return kData.getInt("sharpness");
    }

    public static int getMaxSharpness(ItemStack knife) {
        if (!(knife.getItem() instanceof KitchenKnifeItem)) return 0;
        NbtCompound kData = knife.getOrCreateSubTag("KnifeData");
        if (!kData.contains("maxSharpness")) {
            SandwichableConfig.KitchenKnifeOption opt = Util.getConfig().getKnifeOption(knife.getItem());
            if (opt != null) {
                kData.putInt("maxSharpness", opt.sharpness);
            }
        }
        return kData.getInt("maxSharpness");
    }

    public static void setSharpness(ItemStack knife, int amount) {
        if (!(knife.getItem() instanceof KitchenKnifeItem)) return;
        NbtCompound kData = knife.getOrCreateSubTag("KnifeData");
        SandwichableConfig.KitchenKnifeOption opt = Util.getConfig().getKnifeOption(knife.getItem());
        if (opt != null) kData.putInt("sharpness", MathHelper.clamp(amount, 0, getMaxSharpness(knife)));
    }

    public static float getSharpnessF(ItemStack knife) {
        return (float)getSharpness(knife) / getMaxSharpness(knife);
    }

    public static int getItemCutAmount(ItemStack knife) {
        SandwichableConfig.KitchenKnifeOption opt = Util.getConfig().getKnifeOption(knife.getItem());
        if (opt == null) return 0;
        if (!(knife.getItem() instanceof KitchenKnifeItem)) return opt.value;
        return itemCutAmountFrom(getSharpnessF(knife), opt.value);
    }

    private static int itemCutAmountFrom(float sharpness, int knifeValue) {
        return (int)Math.ceil(sharpness * knifeValue);
    }

    public static void processCut(ItemStack knife, int items) {
        if (!(knife.getItem() instanceof KitchenKnifeItem)) return;
        int decrement = (int)Math.ceil((float)items * 0.7);
        setSharpness(knife, getSharpness(knife) - decrement);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        SandwichableConfig.KitchenKnifeOption opt = Util.getConfig().getKnifeOption(stack.getItem());
        if (opt != null) {
            float sharpness = getSharpnessF(stack);
            tooltip.add(new TranslatableText("kitchen_knife.tooltip.sharpness", Math.round(sharpness * 100)).formatted(Formatting.DARK_GRAY));
            int itemsCut = itemCutAmountFrom(sharpness, opt.value);
            tooltip.add(new TranslatableText("kitchen_knife.tooltip.items_cut" + (itemsCut == 1 ? "_singular" : ""), itemsCut).formatted(Formatting.DARK_GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (group == this.getGroup() || group == ItemGroup.SEARCH) {
            ItemStack stack = new ItemStack(this);
            getSharpness(stack);
            stacks.add(stack);
        }
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        getSharpness(stack);
    }

    @Override
    public float getBarLength(ItemStack stack) {
        return getSharpnessF(stack);
    }

    public static final int[] COLORS = {
        0x4b5e4e, 0x456b51, 0x448261, 0x449477, 0x43ba9a, 0x43f0cd
    };

    @Override
    public int getBarColor(ItemStack stack) {
        return COLORS[Math.round(getSharpnessF(stack) * (COLORS.length - 1))];
    }

    @Override
    public boolean showBar(ItemStack stack) {
        return getSharpnessF(stack) < 1;
    }
}
