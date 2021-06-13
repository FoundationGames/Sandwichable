package io.github.foundationgames.sandwichable.rei;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.util.Util;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CuttingBoardCategory implements DisplayCategory<CuttingBoardDisplay> {

    public static final EntryStack<ItemStack> ICON = EntryStacks.of(BlocksRegistry.OAK_CUTTING_BOARD);

    @Override
    public Renderer getIcon() {
        return ICON;
    }

    @Override
    public CategoryIdentifier<CuttingBoardDisplay> getCategoryIdentifier() {
        return SandwichableREI.CUTTING_BOARD_CATEGORY;
    }

    @Override
    public Text getTitle() {
        return new TranslatableText("category.sandwichable.cutting_board");
    }

    public List<Widget> setupDisplay(CuttingBoardDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getMinX()+6, bounds.getMinY()+4);
        List<Widget> widgets = Lists.newArrayList();
        List<EntryStack<ItemStack>> knives = new ArrayList<>();
        SandwichableConfig cfg = Util.getConfig();
        for(SandwichableConfig.ItemIntPair p : cfg.itemOptions.knives) {
            Identifier itemId = Identifier.tryParse(p.itemId);
            if(itemId != null) {
                Optional<Item> item = Registry.ITEM.getOrEmpty(itemId);
                item.ifPresent(value -> knives.add(EntryStacks.of(value)));
            }
        }

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(SandwichableREI.getCuttingGUITexture(), startPoint.x+15, startPoint.y, 0, 0, 94, 39));
        widgets.add(Widgets.createSlot(new Point(startPoint.x+25, startPoint.y + 9)).entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x+43, startPoint.y + 14)).entries(knives).disableBackground());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 88, startPoint.y + 12)).entries(display.getDisplayOutputEntries()).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }
}
