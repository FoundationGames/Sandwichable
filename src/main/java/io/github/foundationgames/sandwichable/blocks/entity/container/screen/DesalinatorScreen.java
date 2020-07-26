package io.github.foundationgames.sandwichable.blocks.entity.container.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.entity.DesalinatorBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.container.DesalinatorScreenHandler;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.entity.BatEntityRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class DesalinatorScreen extends HandledScreen<DesalinatorScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("sandwichable", "textures/gui/container/desalinator.png");
    private MouseOverBox waterTankBox;

    public DesalinatorScreen(DesalinatorScreenHandler container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.waterTankBox = new MouseOverBox(this.x+56, this.y+15, 20, 17);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
        if(waterTankBox.isMouseOver(mouseX, mouseY)) {
            List<Text> tooltip = Lists.newArrayList(
                    //YES I KNOW THIS CODE IS HORRENDOUS
                    new LiteralText(Integer.toString(((DesalinatorBlockEntity)this.handler.inventory).getWaterAmount())+" B "+ I18n.translate("desalinator.tooltip.filled")),
                    new LiteralText(I18n.translate("desalinator.tooltip.maxCapacity")+" "+Integer.toString(DesalinatorBlockEntity.maxWaterAmount)+" B")
            );
            if(this.getScreenHandler().inventory instanceof DesalinatorBlockEntity) {
                if(((DesalinatorBlockEntity)this.handler.inventory).getWaterAmount() > 0) {
                    boolean saline = ((DesalinatorBlockEntity) this.getScreenHandler().inventory).isWaterSaline();
                    tooltip.add(saline ? new TranslatableText("desalinator.tooltip.saline").formatted(Formatting.GREEN) : new TranslatableText("desalinator.tooltip.notSaline").formatted(Formatting.RED));
                    if (!saline) tooltip.add(new TranslatableText("desalinator.tooltip.moveToSaltyBiome").formatted(Formatting.GRAY));
                }
            }
            this.renderTooltip(matrixStack, tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.textRenderer.draw(matrixStack, this.title, 20.0F, 6.0F, 4210752);
        this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float)(this.backgroundHeight - 96 + 4), 4210752);
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int x = this.x;
        int y = this.y;
        this.drawTexture(matrixStack, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawTexture(matrixStack, x + 79, y + 18, 176, 0, Util.floatToIntWithBounds((float)((DesalinatorBlockEntity)this.handler.inventory).getEvaporateProgress()/DesalinatorBlockEntity.evaporateTime, 19), 10);
        int fireSize =  14 - Util.floatToIntWithBounds((float)((DesalinatorBlockEntity)this.handler.inventory).getFuelBurnProgress()/DesalinatorBlockEntity.fuelBurnTime, 14);
        if(((DesalinatorBlockEntity)this.handler.inventory).isBurning()) {
            this.drawTexture(matrixStack, x + 58, y + 45-fireSize, 176, 40-fireSize, 14, fireSize+1);
        }
        int waterSize =  Util.floatToIntWithBounds((float)((DesalinatorBlockEntity)this.handler.inventory).getWaterAmount()/DesalinatorBlockEntity.maxWaterAmount, 17);
        this.drawTexture(matrixStack, x + 56, y + 32-waterSize, 176, 27-waterSize, 20, waterSize);
        if(!this.handler.inventory.getStack(0).isEmpty()) {
            this.drawTexture(matrixStack, x + 64, y + 47, 176, 41, 15, 12);
        }
        if(((DesalinatorBlockEntity)this.handler.inventory).getWaterAmount() > 0) {
            this.drawTexture(matrixStack, x + 43, y + 21, 176, 53 + (((DesalinatorBlockEntity)this.handler.inventory).isWaterSaline() ? 0 : 1) * 7, 12, 7);
        } else {
            this.drawTexture(matrixStack, x + 43, y + 21, 176, 67, 12, 7);
        }
    }

    static class MouseOverBox {
        int x, y, width, height;
        public MouseOverBox(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        public boolean isMouseOver(int mouseX, int mouseY) {
            return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + width;
        }
    }
}
