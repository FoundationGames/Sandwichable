package io.github.foundationgames.sandwichable.plugin.rei;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ToastingCategory implements DisplayCategory<ToastingDisplay> {
    public static final EntryStack<ItemStack> ICON = EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(BlocksRegistry.TOASTER));
    public static final CategoryIdentifier<ToastingDisplay> ID = CategoryIdentifier.of(SandwichableREI.TOASTING_CATEGORY);

    public static final Text TOASTING_TIME = Text.translatable("category.sandwichable.toasting.time");

    @Override
    public Renderer getIcon() {
        return ICON;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("category.sandwichable.toasting");
    }

    @Override
    public List<Widget> setupDisplay(ToastingDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getMinX()+6, bounds.getMinY()+4);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(SandwichableREI.getToastingGUITexture(), startPoint.x+15, startPoint.y+10, 0, 0, 30, 22));
        widgets.add(Widgets.createTexturedWidget(SandwichableREI.getToastingGUITexture(), startPoint.x+82, startPoint.y+10, 30, 0, 30, 22));
        widgets.add(Widgets.createTexturedWidget(SandwichableREI.getToastingGUITexture(), startPoint.x+53, startPoint.y+10, 24, 22, 23, 17));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 52, startPoint.y + 10)).animationDurationTicks(240));
        widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5), TOASTING_TIME).noShadow().rightAligned().color(-12566464, -4473925));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 22, startPoint.y + 7)).entries(display.getInputEntries().get(0)).disableBackground().markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 89, startPoint.y + 7)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 45;
    }

    @Override
    public CategoryIdentifier<? extends ToastingDisplay> getCategoryIdentifier() {
        return ID;
    }
}
