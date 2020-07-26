package io.github.foundationgames.sandwichable.rei;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.CategoryBaseWidget;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.impl.ItemEntryStack;
import me.shedaniel.rei.plugin.DefaultPlugin;
import me.shedaniel.rei.plugin.campfire.DefaultCampfireDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class CuttingBoardCategory implements RecipeCategory<CuttingBoardDisplay> {

    public static final EntryStack ICON = EntryStack.create(BlocksRegistry.OAK_CUTTING_BOARD);

    @Override
    public EntryStack getLogo() {
        return ICON;
    }

    @Override
    public Identifier getIdentifier() {
        return SandwichableREI.CUTTING_BOARD_CATEGORY;
    }

    @Override
    public String getCategoryName() {
        return I18n.translate("category.sandwichable.cutting_board");
    }

    public List<Widget> setupDisplay(CuttingBoardDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getMinX()+6, bounds.getMinY()+4);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(SandwichableREI.getCuttingGUITexture(), startPoint.x+15, startPoint.y, 0, 0, 94, 39));
        widgets.add(Widgets.createSlot(new Point(startPoint.x+25, startPoint.y + 9)).entries((Collection)display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x+43, startPoint.y + 14)).entry(EntryStack.create(ItemsRegistry.KITCHEN_KNIFE)).disableBackground());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 88, startPoint.y + 12)).entries(display.getOutputEntries()).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }
}
