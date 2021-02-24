package io.github.foundationgames.sandwichable.item.spread;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.World;

public class SuspiciousStewSpreadType extends SpreadType {
    public SuspiciousStewSpreadType() {
        super(6, 0.6F, 0xC3C45E, Items.SUSPICIOUS_STEW, Items.BOWL);
    }

    @Override
    public void finishUsing(ItemStack stack, World world, LivingEntity user) {
        CompoundTag tag = stack.getOrCreateTag().getCompound("stewData");
        if (tag != null && tag.contains("Effects", 9)) {
            ListTag effects = tag.getList("Effects", 10);
            for(int i = 0; i < effects.size(); ++i) {
                int duration = 160;
                CompoundTag effectData = effects.getCompound(i);
                if (effectData.contains("EffectDuration", 3)) {
                    duration = effectData.getInt("EffectDuration");
                }
                StatusEffect statusEffect = StatusEffect.byRawId(effectData.getByte("EffectId"));
                if (statusEffect != null) {
                    user.addStatusEffect(new StatusEffectInstance(statusEffect, duration));
                }
            }
        }
    }

    @Override
    public void onPour(ItemStack container, ItemStack spread) {
        spread.getOrCreateTag().put("stewData", container.getTag());
    }
}
