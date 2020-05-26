package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.entity.PickleJarBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.PickleJarFluid;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class PickleJarBlockItem extends InfoTooltipBlockItem {

    private String tooltipKey;
    private boolean isDefault;

    public PickleJarBlockItem(String tooltipKey, boolean isDefault, Settings settings) {
        super(BlocksRegistry.PICKLE_JAR, settings);
        this.tooltipKey = tooltipKey;
        this.isDefault = isDefault;
    }

    public String getTranslationKey() {
        return "block.sandwichable.pickle_jar";
    }

    public static ItemStack createFromBlockEntity(PickleJarBlockEntity entity) {
        CompoundTag tag = entity.toTag(new CompoundTag());
        ItemStack stack = new ItemStack(ItemsRegistry.EMPTY_PICKLE_JAR, 1);
        PickleJarFluid fluid = PickleJarFluid.fromString(tag.getString("pickleJarFluid"));
        int numItems = tag.getInt("numItems");

        if(fluid == PickleJarFluid.WATER && numItems > 0) {
            stack = new ItemStack(ItemsRegistry.CUCUMBER_FILLED_PICKLE_JAR, 1);
            stack.putSubTag("BlockEntityTag", tag);
        }
        else if(fluid == PickleJarFluid.WATER && numItems == 0) {
            stack = new ItemStack(ItemsRegistry.WATER_FILLED_PICKLE_JAR, 1);
            stack.putSubTag("BlockEntityTag", tag);
        }
        else if(fluid == PickleJarFluid.PICKLING_BRINE) {
            stack = new ItemStack(ItemsRegistry.PICKLING_PICKLE_JAR, 1);
            stack.putSubTag("BlockEntityTag", tag);
        }
        else if(fluid == PickleJarFluid.PICKLED_BRINE) {
            stack = new ItemStack(ItemsRegistry.PICKLE_FILLED_PICKLE_JAR, 1);
            stack.putSubTag("BlockEntityTag", tag);
        }
        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("pickle_jar.tooltip.contents").formatted(Formatting.AQUA));
        if(stack.getSubTag("BlockEntityTag") != null) {
            tooltip.add(new TranslatableText(this.tooltipKey).formatted(Formatting.BLUE));
            CompoundTag tag = stack.getSubTag("BlockEntityTag");
            PickleJarFluid fluid = PickleJarFluid.fromString(tag.getString("pickleJarFluid"));
            int numItems = tag.getInt("numItems");
            int pickleProgress = tag.getInt("pickleProgress");

            if(fluid == PickleJarFluid.WATER && numItems > 0) {
                tooltip.add(new TranslatableText("pickle_jar.tooltip.cucumber_ct", numItems).formatted(Formatting.BLUE));
            }
            else if((fluid == PickleJarFluid.PICKLED_BRINE || fluid == PickleJarFluid.PICKLING_BRINE) && numItems > 0) {
                tooltip.add(new TranslatableText("pickle_jar.tooltip.pickle_ct", numItems).formatted(Formatting.BLUE));
            }
            if(fluid == PickleJarFluid.PICKLING_BRINE) {
                int pct = (int)(((float)pickleProgress/PickleJarBlockEntity.pickleTime)*100);
                tooltip.add(new TranslatableText("pickle_jar.tooltip.pct_pickled", pct).formatted(Formatting.BLUE));
            }
        } else if(this.isDefault) {
            tooltip.add(new TranslatableText(this.tooltipKey).formatted(Formatting.BLUE));
        } else {
            tooltip.add(new TranslatableText("pickle_jar.content.null").formatted(Formatting.RED));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
