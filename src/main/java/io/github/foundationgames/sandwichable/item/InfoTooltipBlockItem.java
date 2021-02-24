package io.github.foundationgames.sandwichable.item;

import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class InfoTooltipBlockItem extends BlockItem {

    public InfoTooltipBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        Util.appendInfoTooltip(tooltip, this.getTranslationKey());
    }
}
