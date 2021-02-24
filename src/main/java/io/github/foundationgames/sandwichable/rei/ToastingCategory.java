package io.github.foundationgames.sandwichable.rei;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

public class ToastingCategory implements RecipeCategory<ToastingDisplay> {

    public static final EntryStack ICON = EntryStack.create(BlocksRegistry.TOASTER);

    @Override
    public EntryStack getLogo() {
        return ICON;
    }

    @Override
    public Identifier getIdentifier() {
        return SandwichableREI.TOASTING_CATEGORY;
    }

    @Override
    public String getCategoryName() {
        return I18n.translate("category.sandwichable.toasting");
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
        widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5),new TranslatableText("category.sandwichable.toasting.time")).noShadow().rightAligned().color(-12566464, -4473925));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 22, startPoint.y + 7)).entries((Collection)display.getInputEntries().get(0)).disableBackground().markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 89, startPoint.y + 7)).entries(display.getOutputEntries()).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 45;
    }
}
