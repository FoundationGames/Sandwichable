package io.github.foundationgames.sandwichable.items;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class BiomeVariantItem extends InfoTooltipItem {
    public BiomeVariantItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        var biome = getBiome(stack);
        if (biome != null) {
            var key = biome.getKey();
            if (key.isPresent()) {
                var id = key.get().getValue();
                return Text.translatable(this.getTranslationKey() + ".biome", I18n.translate(id.toTranslationKey("biome")));
            }
        }

        return super.getName(stack);
    }

    public static void setBiome(ItemStack stack, RegistryEntry<Biome> biome) {
        biome.getKey().ifPresent(key ->
                stack.getOrCreateNbt().putString("biome", key.getValue().toString()));
    }

    public static @Nullable RegistryEntry<Biome> getBiome(ItemStack stack) {
        if (stack.hasNbt()) {
            var id = Identifier.tryParse(stack.getNbt().getString("biome"));

            if (id != null) {
                var entry = BuiltinRegistries.BIOME.getEntry(RegistryKey.of(BuiltinRegistries.BIOME.getKey(), id));
                if (entry.isPresent()) {
                    return entry.get();
                }
            }
        }

        return null;
    }
}
