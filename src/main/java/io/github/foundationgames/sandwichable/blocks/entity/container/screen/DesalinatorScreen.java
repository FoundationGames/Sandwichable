package io.github.foundationgames.sandwichable.blocks.entity.container.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.sandwichable.blocks.entity.DesalinatorBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.container.DesalinatorScreenHandler;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        if(waterTankBox.isMouseOver(mouseX, mouseY)) {
            List<Text> tooltip = Lists.newArrayList(
                    Text.literal(((DesalinatorBlockEntity) this.handler.inventory).getWaterAmount() +" B "+ I18n.translate("desalinator.tooltip.filled")),
                    Text.literal(I18n.translate("desalinator.tooltip.maxCapacity")+" "+DesalinatorBlockEntity.maxFluidAmount +" B")
            );
            if(this.getScreenHandler().inventory instanceof DesalinatorBlockEntity) {
                if(((DesalinatorBlockEntity)this.handler.inventory).getWaterAmount() > 0) {
                    boolean saline = ((DesalinatorBlockEntity) this.getScreenHandler().inventory).isWaterSaline();
                    tooltip.add(saline ? Text.translatable("desalinator.tooltip.saline").formatted(Formatting.GREEN) : Text.translatable("desalinator.tooltip.notSaline").formatted(Formatting.RED));
                    if (!saline) tooltip.add(Text.translatable("desalinator.tooltip.moveToSaltyBiome").formatted(Formatting.GRAY));
                }
            }
            context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, 20, 6, 4210752, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, 8, this.backgroundHeight - 96 + 4, 4210752, false);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int x = this.x;
        int y = this.y;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        context.drawTexture(TEXTURE, x + 79, y + 18, 176, 0, Util.floatToIntWithBounds((float)((DesalinatorBlockEntity)this.handler.inventory).getEvaporateProgress()/DesalinatorBlockEntity.evaporateTime, 19), 10);
        int fireSize =  14 - Util.floatToIntWithBounds((float)((DesalinatorBlockEntity)this.handler.inventory).getFuelBurnProgress()/DesalinatorBlockEntity.fuelBurnTime, 14);
        if(((DesalinatorBlockEntity)this.handler.inventory).isBurning()) {
            context.drawTexture(TEXTURE, x + 58, y + 45-fireSize, 176, 40-fireSize, 14, fireSize+1);
        }
        int fluidSize =  Util.floatToIntWithBounds((float)((DesalinatorBlockEntity)this.handler.inventory).getWaterAmount()/DesalinatorBlockEntity.maxFluidAmount, 17);
        context.drawTexture(TEXTURE, x + 56, y + 32-fluidSize, ((DesalinatorBlockEntity)this.handler.inventory).isPickleBrine() ? 196 : 176, 27-fluidSize, 20, fluidSize);
        if(!this.handler.inventory.getStack(0).isEmpty()) {
            context.drawTexture(TEXTURE, x + 64, y + 47, 176, 41, 15, 12);
        }
        if(((DesalinatorBlockEntity)this.handler.inventory).getWaterAmount() > 0) {
            context.drawTexture(TEXTURE, x + 43, y + 21, 176, 53 + (((DesalinatorBlockEntity)this.handler.inventory).isWaterSaline() ? 0 : 1) * 7, 12, 7);
        } else {
            context.drawTexture(TEXTURE, x + 43, y + 21, 176, 67, 12, 7);
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
