package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.Sandwichable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class CheeseItem extends InfoTooltipItem {

    private CheeseType type;
    private boolean isSlice;

    private static FoodComponent wheelComponent(CheeseType type) {
        return new FoodComponent.Builder().hunger(type.hunger * 3).saturationModifier(type.saturation).statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 420, 5), 1.0F).build();
    }
    private static FoodComponent sliceComponent(CheeseType type) {
        return new FoodComponent.Builder().hunger(type.hunger).saturationModifier(type.saturation).snack().build();
    }

    public CheeseItem(CheeseType type, boolean isSlice) {
        super(new Item.Settings().food(isSlice ? CheeseItem.sliceComponent(type) : CheeseItem.wheelComponent(type)).group(Sandwichable.SANDWICHABLE_ITEMS));
        this.type = type;
        this.isSlice = isSlice;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("cheese.type."+type.toString()).formatted(Formatting.BLUE));
        LocalDate date = LocalDate.now();
        if(date.getMonth() == Month.APRIL && date.getDayOfMonth() == 1) {
            tooltip.add(Text.translatable("cheese.type.dairy_free").formatted(Formatting.AQUA));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    public CheeseType getCheeseType() {
        return type;
    }

    @Override
    public String getTranslationKey() {
        return "item.sandwichable.cheese" + ( isSlice ? "_slice" : "_wheel" );
    }

    public boolean isSlice() {
        return isSlice;
    }
}
