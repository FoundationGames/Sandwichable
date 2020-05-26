package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.Sandwichable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class CheeseCultureItem extends InfoTooltipItem {
    private CheeseType type;

    public CheeseCultureItem(CheeseType type, Settings settings) {
        super(settings);
        this.type = type;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("cheese.type."+type.toString()).formatted(Formatting.BLUE));
        super.appendTooltip(stack, world, tooltip, context);
    }

    public CheeseType getCheeseType() {
        return this.type;
    }

    @Override
    public String getTranslationKey() {
        return "item.sandwichable.cheese_culture_bottle";
    }
}
