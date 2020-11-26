package io.github.foundationgames.sandwichable.util;

import com.google.common.collect.Maps;
import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.entity.BasinContent;
import io.github.foundationgames.sandwichable.blocks.entity.BasinContentType;
import io.github.foundationgames.sandwichable.items.CheeseType;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class CheeseRegistry {

    public static final CheeseRegistry INSTANCE = new CheeseRegistry();

    private final Map<String, CheeseType> cheeseTypes = Maps.newHashMap();
    private final Map<String, BasinContent> basinContents = Maps.newHashMap();
    private final Map<CheeseType, BasinContent> typeToCheese = Maps.newHashMap();
    private final Map<CheeseType, BasinContent> typeToFermentingMilk = Maps.newHashMap();
    private final Map<CheeseType, Item> typeToCheeseItem = Maps.newHashMap();

    private CheeseRegistry() {
        this.typeToCheeseItem.put(CheeseType.REGULAR, ItemsRegistry.CHEESE_WHEEL_REGULAR);
        this.typeToCheeseItem.put(CheeseType.CREAMY, ItemsRegistry.CHEESE_WHEEL_CREAMY);
        this.typeToCheeseItem.put(CheeseType.INTOXICATING, ItemsRegistry.CHEESE_WHEEL_INTOXICATING);
        this.typeToCheeseItem.put(CheeseType.SOUR, ItemsRegistry.CHEESE_WHEEL_SOUR);
        this.typeToCheeseItem.put(CheeseType.CANDESCENT, ItemsRegistry.CHEESE_WHEEL_CANDESCENT);
        this.typeToCheeseItem.put(CheeseType.WARPED_BLEU, ItemsRegistry.CHEESE_WHEEL_WARPED_BLEU);
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
        if(type == null) {
            Sandwichable.LOG.error("NULL CheeseType");
            return ItemStack.EMPTY;
        }
        return new ItemStack(this.typeToCheeseItem.get(type));
    }
    public BasinContent fermentingMilkFromCheeseType(CheeseType type) {
        return this.typeToFermentingMilk.get(type);
    }
}
