package io.github.foundationgames.sandwichable.items;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class SandwichIngredientItem extends InfoTooltipItem {
    public final int fullness;
    private final boolean glinted;
    public final Flavor flavor;


    public SandwichIngredientItem(boolean glinted, Flavor flavor, FoodComponent food, int fullnessBenefit, ItemGroup group) {
        super(new Item.Settings().group(group).food(food));
        fullness = fullnessBenefit;
        this.glinted = glinted;
        this.flavor = flavor;
    }

    public SandwichIngredientItem(boolean glinted, Rarity rarity, Flavor flavor, FoodComponent food, int fullnessBenefit, ItemGroup group) {
        super(new Item.Settings().group(group).food(food).rarity(rarity));
        fullness = fullnessBenefit;
        this.glinted = glinted;
        this.flavor = flavor;
    }

    public SandwichIngredientItem(Flavor flavor, FoodComponent food, int fullnessBenefit, ItemGroup group) {
        this(false, flavor, food, fullnessBenefit, group);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return glinted;
    }

    public enum Flavor {

        VEGETABLE(0),
        MEAT(8),
        SAVORY(16),
        SWEET(24),
        SOUR(32),
        BREAD(40);

        public final int textureU;
        Flavor(int textureU) {
            this.textureU = textureU;
        }
    }
}
