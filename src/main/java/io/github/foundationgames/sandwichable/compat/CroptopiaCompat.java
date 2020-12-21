package io.github.foundationgames.sandwichable.compat;

import com.google.common.collect.ImmutableList;
import io.github.foundationgames.sandwichable.items.spread.SpreadType;
import io.github.foundationgames.sandwichable.util.SpreadRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Optional;

public class CroptopiaCompat {
    public static void init() {
        addJamSpread("grape_jam", 0x4d3dff);
        addJamSpread("strawberry_jam", 0xc21d39);
        addJamSpread("peach_jam", 0xff801f);
        addJamSpread("apricot_jam", 0xff9b21);
        addJamSpread("blackberry_jam", 0x1a0e29);
        addJamSpread("blueberry_jam", 0x17287a);
        addJamSpread("cherry_jam", 0x8f0018);
        addJamSpread("elderberry_jam", 0x071124);
        addJamSpread("raspberry_jam", 0x8a0e2b);
    }

    private static void addJamSpread(String itemId, int color) {
        Optional<Item> oitem = Registry.ITEM.getOrEmpty(new Identifier("croptopia", itemId));
        if(oitem.isPresent()) {
            Item item = oitem.get();
            int h = 0;
            float s = 0;
            if(item.isFood()) {
                h = item.getFoodComponent().getHunger();
                s = item.getFoodComponent().getSaturationModifier();
            }
            SpreadRegistry.INSTANCE.register(itemId, new SpreadType(h, s, color, item, Items.GLASS_BOTTLE));
        }
    }
}
