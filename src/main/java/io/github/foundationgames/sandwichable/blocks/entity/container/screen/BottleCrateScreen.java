package io.github.foundationgames.sandwichable.blocks.entity.container.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.sandwichable.blocks.entity.container.BottleCrateScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.textRenderer.draw(matrixStack, this.title, 24.0F, 6.0F, 4210752);
        this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 7.0F, (float)(this.backgroundHeight - 95 + 2), 4210752);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}
