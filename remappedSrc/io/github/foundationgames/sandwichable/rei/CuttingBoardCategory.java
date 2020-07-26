package io.github.foundationgames.sandwichable.rei;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
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
import net.minecraft.util.Identifier;

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

    @Override
    public List<Widget> setupDisplay(Supplier<CuttingBoardDisplay> recipeDisplaySupplier, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getMinX()+6, bounds.getMinY()+4);
        List<Widget> widgets = Lists.newArrayList(new CategoryBaseWidget(bounds) {
            @Override
            public void render(int mouseX, int mouseY, float delta) {
                super.render(mouseX, mouseY, delta);
                MinecraftClient.getInstance().getTextureManager().bindTexture(SandwichableREI.getCuttingGUITexture());
                this.blit(startPoint.x+15, startPoint.y, 0, 0, 94, 39);
            }
        });
        CuttingBoardDisplay display = recipeDisplaySupplier.get();
        widgets.add(EntryWidget.create(startPoint.x+25, startPoint.y + 9).entries((recipeDisplaySupplier.get()).getInputEntries().get(0)).markIsInput());
        widgets.add(EntryWidget.create(startPoint.x+43, startPoint.y + 14).entry(new ItemEntryStack(new ItemStack(ItemsRegistry.KITCHEN_KNIFE))).noBackground());
        widgets.add(EntryWidget.create(startPoint.x + 88, startPoint.y + 12).entries((recipeDisplaySupplier.get()).getOutputEntries()).noBackground().markIsOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }
}
