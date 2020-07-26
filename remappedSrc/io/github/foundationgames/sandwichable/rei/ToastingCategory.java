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
import me.shedaniel.rei.gui.widget.RecipeArrowWidget;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.impl.ItemEntryStack;
import me.shedaniel.rei.impl.ScreenHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

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
    public List<Widget> setupDisplay(Supplier<ToastingDisplay> recipeDisplaySupplier, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getMinX()+6, bounds.getMinY()+4);
        List<Widget> widgets = Lists.newArrayList(new CategoryBaseWidget(bounds) {
            @Override
            public void render(int mouseX, int mouseY, float delta) {
                super.render(mouseX, mouseY, delta);
                MinecraftClient.getInstance().getTextureManager().bindTexture(SandwichableREI.getToastingGUITexture());
                this.blit(startPoint.x+15, startPoint.y+10, 0, 0, 30, 22);
                this.blit(startPoint.x+82, startPoint.y+10, 30, 0, 30, 22);
                this.blit(startPoint.x+53, startPoint.y+10, 24, 22, 23, 17);
                String timeDisplay = I18n.translate("category.sandwichable.toasting.time");
                int textLength = MinecraftClient.getInstance().textRenderer.getStringWidth(timeDisplay);
                MinecraftClient.getInstance().textRenderer.draw(timeDisplay, (bounds.x + bounds.width - textLength - 5), (float)(bounds.y + 5), ScreenHelper.isDarkModeEnabled() ? -4473925 : -12566464);
            }
        });
        ToastingDisplay display = recipeDisplaySupplier.get();
        widgets.add(RecipeArrowWidget.create(new Point(startPoint.x + 52, startPoint.y + 10), true).time(240 * 50.0D));
        widgets.add(EntryWidget.create(startPoint.x + 22, startPoint.y + 7).entries((recipeDisplaySupplier.get()).getInputEntries().get(0)).noBackground().markIsInput());
        widgets.add(EntryWidget.create(startPoint.x + 89, startPoint.y + 7).entries((recipeDisplaySupplier.get()).getOutputEntries()).noBackground().markIsOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 43;
    }
}
