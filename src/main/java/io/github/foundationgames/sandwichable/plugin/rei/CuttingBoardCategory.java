package io.github.foundationgames.sandwichable.plugin.rei;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.util.Util;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Optional;

public class CuttingBoardCategory implements DisplayCategory<CuttingBoardDisplay> {
    public static final EntryStack<ItemStack> ICON = EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(BlocksRegistry.OAK_CUTTING_BOARD));
    public static final CategoryIdentifier<CuttingBoardDisplay> ID = CategoryIdentifier.of(SandwichableREI.CUTTING_BOARD_CATEGORY);

    @Override
    public Renderer getIcon() {
        return ICON;
    }

    @Override
    public Text getTitle() {
        return new TranslatableText("category.sandwichable.cutting_board");
    }

    @Override
    public List<Widget> setupDisplay(CuttingBoardDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getMinX()+6, bounds.getMinY()+4);
        List<Widget> widgets = Lists.newArrayList();
        var knives = EntryIngredient.builder();
        SandwichableConfig cfg = Util.getConfig();
        for(SandwichableConfig.KitchenKnifeOption p : cfg.itemOptions.knives) {
            Identifier itemId = Identifier.tryParse(p.itemId);
            if(itemId != null) {
                Optional<Item> item = Registry.ITEM.getOrEmpty(itemId);
                item.ifPresent(value -> knives.add(EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(value))));
            }
        }

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(SandwichableREI.getCuttingGUITexture(), startPoint.x+15, startPoint.y, 0, 0, 94, 39));
        widgets.add(Widgets.createSlot(new Point(startPoint.x+25, startPoint.y + 9)).entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x+43, startPoint.y + 14)).entries(knives.build()).disableBackground());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 88, startPoint.y + 12)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }

    @Override
    public CategoryIdentifier<? extends CuttingBoardDisplay> getCategoryIdentifier() {
        return ID;
    }
}
