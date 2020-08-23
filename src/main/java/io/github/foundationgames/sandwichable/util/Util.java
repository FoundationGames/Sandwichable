package io.github.foundationgames.sandwichable.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.events.StructurePoolAddCallback;
import io.github.foundationgames.sandwichable.worldgen.ModifiableStructurePool;
import io.github.foundationgames.sandwichable.worldgen.StructurePoolHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.structure.Structure;
import net.minecraft.structure.pool.*;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Util {

    public static String MOD_ID = "sandwichable";

    public static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }

    public static void scatterBlockDust(World world, BlockPos pos, Block block, int intensity, int density) {
        Random random = new Random();
        for (int i = 0; i < density; i++) {
            double ox, oy, oz, vx, vy, vz;
            ox = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            oy = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            oz = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            vx = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            vy = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            vz = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, block.getDefaultState()), pos.getX()+0.5+ox, pos.getY()+0.5+oy, pos.getZ()+0.5+oz, 0.0D+vx, 0.0D+vy, 0.0D+vz);
        }
    }
    public static void scatterDroppedBlockDust(World world, BlockPos pos, Block block, int intensity, int density) {
        Random random = new Random();
        for (int i = 0; i < density; i++) {
            double ox, oy, oz, vx, vy, vz;
            ox = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            oy = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            oz = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, block.getDefaultState()), pos.getX()+0.5+ox, pos.getY()+0.5+oy, pos.getZ()+0.5+oz, 0.0D, -0.3D, 0.0D);
        }
    }
    public static Int2ObjectMap<TradeOffers.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, TradeOffers.Factory[]> immutableMap) {
        return new Int2ObjectOpenHashMap(immutableMap);
    }
    public static StructurePool tryAddElementToPool(Identifier targetPool, StructurePool pool, String elementId, StructurePool.Projection projection, int weight) {
        if(targetPool.equals(pool.getId())) {
            ModifiableStructurePool modPool = new ModifiableStructurePool(pool);
            modPool.addStructurePoolElement(StructurePoolElement.method_30426(elementId, StructureProcessorLists.EMPTY).apply(projection), weight);
            return modPool.getStructurePool();
        }
        return pool;
    }
    public static void sync(BlockEntityClientSerializable be, World world) {
        if(!world.isClient) be.sync();
    }
    public static float pxToFlt(double d) {
        return (float) 1 / (float) 16 * (float) d;
    }
    public static float getXFromU(Sprite sprite, float f) {
        float g = sprite.getMaxU() - sprite.getMinU();
        return (f - sprite.getMinU()) / g * 16.0F;
    }

    public static float getYFromV(Sprite sprite, float f) {
        float g = sprite.getMaxV() - sprite.getMinV();
        return (f - sprite.getMinV()) / g * 16.0F;
    }

    public static int floatToIntWithBounds(float input, int bounds) {
        return (int)(input*bounds);
    }

    public static void appendInfoTooltip(List<Text> tooltip, String itemTranslationKey) {
        SandwichableConfig config = AutoConfig.getConfigHolder(SandwichableConfig.class).getConfig();
        if(config.showInfoTooltips) {
            if (config.infoTooltipKeyBind.isPressed()) {
                tooltip.add(new TranslatableText("sandwichable.tooltip.infoheader").formatted(Formatting.GOLD));
                char[] infoChars = I18n.translate(itemTranslationKey + ".info").toCharArray();
                int lineLength = I18n.translate(itemTranslationKey).length();
                if (lineLength < Math.sqrt(infoChars.length) * 1.5) {
                    lineLength = (int) Math.sqrt(infoChars.length * 1.5);
                }
                StringBuilder ln = new StringBuilder();
                for (char c : infoChars) {
                    ln.append(c);
                    if (c == ' ' && ln.toString().length() > lineLength) {
                        tooltip.add(new LiteralText(ln.toString()).formatted(Formatting.GRAY));
                        ln = new StringBuilder();
                    }
                }
                if (!ln.toString().isEmpty()) {
                    tooltip.add(new LiteralText(ln.toString()).formatted(Formatting.GRAY));
                }
            } else {
                tooltip.add(new TranslatableText("sandwichable.tooltip."+config.infoTooltipKeyBind.getName()).formatted(Formatting.GOLD));
            }
        }
    }
}
