package io.github.foundationgames.sandwichable.blocks.entity.container.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.sandwichable.blocks.entity.container.BottleCrateScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BottleCrateScreen extends HandledScreen<BottleCrateScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("sandwichable", "textures/gui/container/bottle_crate.png");

    public BottleCrateScreen(BottleCrateScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 173;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, 24, 6, 4210752, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, 7, this.backgroundHeight - 95 + 2, 4210752, false);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}
