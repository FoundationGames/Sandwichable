package io.github.foundationgames.sandwichable.items.spread;

import com.google.common.collect.ImmutableList;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.SpreadRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.List;

public class SpreadType {

    public static final SpreadType MUSHROOM_STEW;
    public static final SpreadType RABBIT_STEW;
    public static final SpreadType BEETROOT_SOUP;
    public static final SpreadType HONEY;
    public static final SpreadType SUSPICIOUS_STEW;
    public static final SpreadType FERMENTING_MILK;
    public static final SpreadType SWEET_BERRY_JAM;
    public static final SpreadType MAYONNAISE;

    public static void init() {
        SpreadRegistry.INSTANCE.register("mushroom_stew", MUSHROOM_STEW);
        SpreadRegistry.INSTANCE.register("rabbit_stew", RABBIT_STEW);
        SpreadRegistry.INSTANCE.register("beetroot_soup", BEETROOT_SOUP);
        SpreadRegistry.INSTANCE.register("honey", HONEY);
        SpreadRegistry.INSTANCE.register("suspicious_stew", SUSPICIOUS_STEW);
        SpreadRegistry.INSTANCE.register("fermenting_milk", FERMENTING_MILK);
        SpreadRegistry.INSTANCE.register("sweet_berry_jam", SWEET_BERRY_JAM);
        SpreadRegistry.INSTANCE.register("mayonnaise", MAYONNAISE);
    }

    private int hunger;
    private float saturation;
    private int color;
    List<StatusEffectInstance> effects;
    ItemConvertible container;
    ItemConvertible resultContainer;

    public SpreadType(int hunger, float saturationModifier, int color, List<StatusEffectInstance> effects, ItemConvertible container, ItemConvertible resultContainer) {
        this.hunger = hunger;
        this.saturation = saturationModifier;
        this.color = color;
        this.effects = effects;
        this.container = container;
        this.resultContainer = resultContainer;
    }
    public SpreadType(int hunger, float saturationModifier, int color, ItemConvertible container, ItemConvertible resultContainer) {
        this(hunger, saturationModifier, color, ImmutableList.of(), container, resultContainer);
    }

    public int getColor() { return color; };
    public int getHunger() { return hunger; };
    public float getSaturationModifier() { return saturation; };
    public List<StatusEffectInstance> getStatusEffects() { return effects; };
    public ItemConvertible getContainingItem() { return container; };
    public ItemConvertible getResultItem() { return resultContainer; };
    public void finishUsing(ItemStack stack, World world, LivingEntity user) {};
    public void onPour(ItemStack container, ItemStack spread) {};

    static {
        MUSHROOM_STEW = new SpreadType(6, 0.6F, 0xAD7451, Items.MUSHROOM_STEW, Items.BOWL);
        RABBIT_STEW = new SpreadType(10, 0.6F, 0xBF7234, Items.RABBIT_STEW, Items.BOWL);
        BEETROOT_SOUP = new SpreadType(6, 0.6F, 0x8C0023, Items.BEETROOT_SOUP, Items.BOWL);
        HONEY = new HoneySpreadType();
        SUSPICIOUS_STEW = new SuspiciousStewSpreadType();
        FERMENTING_MILK = new FermentingMilkSpreadType();
        SWEET_BERRY_JAM = new SpreadType(5, 0.5F, 0xF00024, ItemsRegistry.SWEET_BERRY_JAM, Items.GLASS_BOTTLE);
        MAYONNAISE = new SpreadType(4, 0.6F, 0xFFD5B5, ItemsRegistry.MAYONNAISE, Items.GLASS_BOTTLE);
    }
}
