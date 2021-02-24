package io.github.foundationgames.sandwichable.item;

import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class InfoTooltipItem extends Item {

    public InfoTooltipItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        Util.appendInfoTooltip(tooltip, this.getTranslationKey());
    }
}
