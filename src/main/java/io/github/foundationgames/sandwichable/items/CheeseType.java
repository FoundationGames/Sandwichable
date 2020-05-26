package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.blocks.entity.BasinContent;
import io.github.foundationgames.sandwichable.util.CheeseRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.lwjgl.system.CallbackI;

import java.awt.*;

public enum CheeseType {

    NONE("air", 0, 0.0F, new float[] {0F, 0F, 0F}),
    REGULAR("regular", 3, 0.6F, new float[] {0.93F, 0.78F, 0.2F}),
    CREAMY("creamy", 4, 0.5F, new float[] {0.98F, 0.94F, 0.83F}),
    INTOXICATING("intoxicating", 2, 1.1F, new float[] {1.0F, 0.78F, 0.56F}),
    SOUR("sour", 3, 0.9F, new float[] {0.97F, 0.83F, 0.72F});

    String id;
    int hunger;
    float saturation;
    float[] particleColorRGB;

    CheeseType(String name, int hunger, float satModifier, float[] particleColorRGB) {
        this.id = name;
        this.hunger = hunger;
        this.saturation = satModifier;
        this.particleColorRGB = particleColorRGB;
        CheeseRegistry.INSTANCE.register(this);
    }

    @Override
    public String toString() {
        return id;
    }

    public float[] getParticleColorRGB() {
        return particleColorRGB;
    }

    public Identifier getTexture() {
        return new Identifier("sandwichable", "textures/entity/basin/"+id+".png");
    }
}
