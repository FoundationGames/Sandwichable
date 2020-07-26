package io.github.foundationgames.sandwichable.util;

import com.google.common.collect.Maps;
import io.github.foundationgames.sandwichable.blocks.entity.BasinContent;
import io.github.foundationgames.sandwichable.blocks.entity.BasinContentType;
import io.github.foundationgames.sandwichable.items.CheeseType;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;

public class CheeseRegistry {

    public static final CheeseRegistry INSTANCE = new CheeseRegistry();

    private Map<String, CheeseType> cheeseTypes = Maps.newHashMap();
    private Map<String, BasinContent> basinContents = Maps.newHashMap();
    private Map<CheeseType, BasinContent> typeToCheese = Maps.newHashMap();
    private Map<CheeseType, BasinContent> typeToFermentingMilk = Maps.newHashMap();
    private Map<CheeseType, ItemStack> typeToCheeseItemStack = Maps.newHashMap();

    private CheeseRegistry() {
        this.typeToCheeseItemStack.put(CheeseType.REGULAR, new ItemStack(ItemsRegistry.CHEESE_WHEEL_REGULAR, 1));
        this.typeToCheeseItemStack.put(CheeseType.CREAMY, new ItemStack(ItemsRegistry.CHEESE_WHEEL_CREAMY, 1));
        this.typeToCheeseItemStack.put(CheeseType.INTOXICATING, new ItemStack(ItemsRegistry.CHEESE_WHEEL_INTOXICATING, 1));
        this.typeToCheeseItemStack.put(CheeseType.SOUR, new ItemStack(ItemsRegistry.CHEESE_WHEEL_SOUR, 1));
        this.typeToCheeseItemStack.put(CheeseType.CANDESCENT, new ItemStack(ItemsRegistry.CHEESE_WHEEL_CANDESCENT, 1));
        this.typeToCheeseItemStack.put(CheeseType.WARPED_BLEU, new ItemStack(ItemsRegistry.CHEESE_WHEEL_WARPED_BLEU, 1));
    }

    public void register(CheeseType type) {
        this.cheeseTypes.put(type.toString(), type);
    }
    public void register(BasinContent type) {
        this.basinContents.put(type.toString(), type);
        if(type.getContentType() == BasinContentType.CHEESE) {
            this.typeToCheese.put(type.getCheeseType(), type);
        } else if(type.getContentType() == BasinContentType.FERMENTING_MILK) {
            this.typeToFermentingMilk.put(type.getCheeseType(), type);
        }
    }
    public BasinContent basinContentFromString(String id) {
        return this.basinContents.get(id);
    }
    public CheeseType cheeseTypeFromString(String id) {
        return this.cheeseTypes.get(id);
    }
    public BasinContent cheeseFromCheeseType(CheeseType type) {
        return this.typeToCheese.get(type);
    }
    public ItemStack cheeseItemFromCheeseType(CheeseType type) {
        return this.typeToCheeseItemStack.get(type);
    }
    public BasinContent fermentingMilkFromCheeseType(CheeseType type) {
        return this.typeToFermentingMilk.get(type);
    }
}
