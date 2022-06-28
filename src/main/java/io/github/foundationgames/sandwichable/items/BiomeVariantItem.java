package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class BiomeVariantItem extends InfoTooltipItem {
    public BiomeVariantItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        var biomeId = getBiomeId(stack);
        if (biomeId != null) {
            return Text.translatable(this.getTranslationKey() + ".biome", Util.biomeName(biomeId));
        }

        return super.getName(stack);
    }

    public static @Nullable Identifier getBiomeId(ItemStack stack) {
        if (stack.hasNbt()) {
            return Identifier.tryParse(stack.getNbt().getString("biome"));
        }

        return null;
    }

    public static void setBiome(ItemStack stack, RegistryEntry<Biome> biome) {
        biome.getKey().ifPresent(key ->
                stack.getOrCreateNbt().putString("biome", key.getValue().toString()));
    }

    public static @Nullable RegistryEntry<Biome> getBiome(@Nullable World world, ItemStack stack) {
        if (stack.hasNbt()) {
            var id = getBiomeId(stack);

            var registry = BuiltinRegistries.BIOME;

            if (world != null) {
                registry = world.getRegistryManager().get(Registry.BIOME_KEY);
            }

            if (id != null) {
                var entry = registry.getEntry(RegistryKey.of(BuiltinRegistries.BIOME.getKey(), id));
                if (entry.isPresent()) {
                    return entry.get();
                }
            }
        }

        return null;
    }

    public static void copyBiome(ItemStack from, ItemStack to) {
        if (from.hasNbt() && from.getNbt().contains("biome")) {
            to.getOrCreateNbt().putString("biome", from.getNbt().getString("biome"));
        }
    }

    @Environment(EnvType.CLIENT)
    private static World getClientWorld() {
        return MinecraftClient.getInstance().world;
    }
}
