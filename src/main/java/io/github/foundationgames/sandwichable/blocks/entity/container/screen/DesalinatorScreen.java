package io.github.foundationgames.sandwichable.blocks.entity.container.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.sandwichable.blocks.entity.DesalinatorBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.container.DesalinatorContainer;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

public class DesalinatorScreen extends ContainerScreen<DesalinatorContainer> {
    private static final Identifier TEXTURE = new Identifier("sandwichable", "textures/gui/container/desalinator.png");
    private MouseOverBox waterTankBox;

    public DesalinatorScreen(DesalinatorContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
        this.containerHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.waterTankBox = new MouseOverBox(this.x+56, this.y+15, 20, 17);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
        if(waterTankBox.isMouseOver(mouseX, mouseY)) {
            List<String> tooltip = Lists.newArrayList(
                    Integer.toString(((DesalinatorBlockEntity)this.container.inventory).getWaterAmount())+" B "+ I18n.translate("desalinator.tooltip.filled"),
                    I18n.translate("desalinator.tooltip.maxCapacity")+" "+Integer.toString(DesalinatorBlockEntity.maxWaterAmount)+" B"
            );
            this.renderTooltip(tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        this.font.draw(this.title.asFormattedString(), 20.0F, 6.0F, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 4), 4210752);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        int x = this.x;
        int y = this.y;
        this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
        this.blit(x + 79, y + 18, 176, 0, Util.floatToIntWithBounds((float)((DesalinatorBlockEntity)this.container.inventory).getEvaporateProgress()/DesalinatorBlockEntity.evaporateTime, 19), 10);
        int fireSize =  14 - Util.floatToIntWithBounds((float)((DesalinatorBlockEntity)this.container.inventory).getFuelBurnProgress()/DesalinatorBlockEntity.fuelBurnTime, 14);
        if(((DesalinatorBlockEntity)this.container.inventory).isBurning()) {
            this.blit(x + 58, y + 45-fireSize, 176, 40-fireSize, 14, fireSize+1);
        }
        int waterSize =  Util.floatToIntWithBounds((float)((DesalinatorBlockEntity)this.container.inventory).getWaterAmount()/DesalinatorBlockEntity.maxWaterAmount, 17);
        this.blit(x + 56, y + 32-waterSize, 176, 27-waterSize, 20, waterSize);
        if(!this.container.inventory.getInvStack(0).isEmpty()) {
            this.blit(x + 64, y + 47, 176, 41, 15, 12);
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
