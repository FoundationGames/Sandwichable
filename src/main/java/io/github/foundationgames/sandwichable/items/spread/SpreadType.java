package io.github.foundationgames.sandwichable.items.spread;

import com.google.common.collect.ImmutableList;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.SpreadRegistry;
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
    public static final SpreadType GLOW_BERRY_JAM;
    public static final SpreadType MAYONNAISE;
    public static final SpreadType POTION;

    public static void init() {
        SpreadRegistry.INSTANCE.register("mushroom_stew", MUSHROOM_STEW);
        SpreadRegistry.INSTANCE.register("rabbit_stew", RABBIT_STEW);
        SpreadRegistry.INSTANCE.register("beetroot_soup", BEETROOT_SOUP);
        SpreadRegistry.INSTANCE.register("honey", HONEY);
        SpreadRegistry.INSTANCE.register("suspicious_stew", SUSPICIOUS_STEW);
        SpreadRegistry.INSTANCE.register("fermenting_milk", FERMENTING_MILK);
        SpreadRegistry.INSTANCE.register("sweet_berry_jam", SWEET_BERRY_JAM);
        SpreadRegistry.INSTANCE.register("glow_berry_jam", GLOW_BERRY_JAM);
        SpreadRegistry.INSTANCE.register("mayonnaise", MAYONNAISE);
        SpreadRegistry.INSTANCE.register("potion", POTION);
    }

    private final int hunger;
    private final float saturation;
    private final int color;
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

    public int getColor(ItemStack stack) { return color; };
    public int getHunger() { return hunger; };
    public float getSaturationModifier() { return saturation; };
    public List<StatusEffectInstance> getStatusEffects(ItemStack stack) { return effects; };
    public ItemConvertible getContainingItem() { return container; };
    public ItemStack getResultItem() { return new ItemStack(resultContainer); };
    public void finishUsing(ItemStack stack, World world, LivingEntity user) {};
    public void onPour(ItemStack container, ItemStack spread) {};
    public String getTranslationKey(String id, ItemStack stack) { return "item.sandwichable.spread."+id; };
    public boolean hasGlint(ItemStack stack) { return false; }

    static {
        MUSHROOM_STEW = new SpreadType(6, 0.6F, 0xAD7451, Items.MUSHROOM_STEW, Items.BOWL);
        RABBIT_STEW = new SpreadType(10, 0.6F, 0xBF7234, Items.RABBIT_STEW, Items.BOWL);
        BEETROOT_SOUP = new SpreadType(6, 0.6F, 0x8C0023, Items.BEETROOT_SOUP, Items.BOWL);
        HONEY = new HoneySpreadType();
        SUSPICIOUS_STEW = new SuspiciousStewSpreadType();
        FERMENTING_MILK = new FermentingMilkSpreadType();
        SWEET_BERRY_JAM = new SpreadType(5, 0.5F, 0xF00024, ItemsRegistry.SWEET_BERRY_JAM, Items.GLASS_BOTTLE);
        GLOW_BERRY_JAM = new SpreadType(5, 0.5F, 0xFFCB54, ItemsRegistry.GLOW_BERRY_JAM, Items.GLASS_BOTTLE);
        MAYONNAISE = new SpreadType(4, 0.6F, 0xFFD5B5, ItemsRegistry.MAYONNAISE, Items.GLASS_BOTTLE);
        POTION = new PotionSpreadType();
    }
}
