package io.github.foundationgames.sandwichable.util;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class AncientGrainType {
    public static final AncientGrainType DEFAULT = new AncientGrainType(0xD4B25D, 0xFFBA6B, 5, 0.65f);
    private static final Map<RegistryEntry<Biome>, AncientGrainType> CACHE = new HashMap<>();

    public final int color;
    public final int breadColor;
    public final int food;
    public final float saturation;

    public AncientGrainType(int color, int breadColor, int food, float saturation) {
        this.color = color;
        this.breadColor = breadColor;
        this.food = food;
        this.saturation = saturation;
    }

    public static AncientGrainType from(RegistryEntry<Biome> entry) {
        var biome = entry.value();
        var random = Random.create(((long)(biome.getTemperature() * ((float)0xFFFFFFFF)) << 32) | (long)(biome.weather.downfall() * ((float)0xFFFFFFFF)));

        float magicFoodNumber = random.nextFloat();
        return new AncientGrainType(
                createColor(biome), createBreadColor(biome), (int)Math.floor(5 + (magicFoodNumber * 4.2)), 0.95f - ((magicFoodNumber * 0.3f) * (0.8f + (random.nextFloat()) * 0.2f))
        );
    }

    private static int createColor(Biome biome) {
        return createColor(biome.getTemperature(), biome.weather.downfall(), biome.getGrassColorAt(0, 0));
    }

    private static int createBreadColor(Biome biome) {
        return createBreadColor(biome.getTemperature(), biome.weather.downfall());
    }

    private static int createColor(float temperature, float precipitation, int grassColor) {
        temperature = 1 - (temperature * 0.5f);
        float x = (-(0.15f - 1) * precipitation) + 0.15f;
        float y = (-(0.15f - 0.9f) * temperature) + 0.15f;
        x += 0.2 * Math.sin(13 * precipitation);
        y += 0.15 * Math.sin(17 * temperature);

        float avg = 0.5f * (x + y);
        float sum = x + y;
        float dif = x - y;

        float mul = (float)(0.5 * (0.9 + 0.2 * Math.sin(sum * 90))) * 255;

        float grassR = (float)(Util.getRed(grassColor) + 200) / 500;
        float grassG = (float)(Util.getGreen(grassColor) + 255) / 500;
        float grassB = (float)(Util.getBlue(grassColor) + 60) / 365;

        int r = (int)((Math.cos(avg - 0.2) + grassR) * mul);
        int g = (int)((Math.sin(avg + 0.15) + grassG) * mul);
        int b = (int)((0.3 + (0.75 * Math.sin(dif)) + grassB) * mul);

        return Util.getColor(Math.min(r, 0xFF), Math.min(g, 0xFF), Math.min(b, 0xFF));
    }

    private static int createBreadColor(float temperature, float precipitation) {
        temperature = 1 - (temperature * 0.5f);

        float r = 1 - (0.2f * temperature);
        float g = 0.3f + (0.2f * (precipitation + temperature));
        float b = 0.3f * temperature - 0.1f;

        float mul = 0.1f + (float)(0.05 * Math.sin((temperature + precipitation) * 120));

        r = (r + 1) * 0.5f + mul;
        g = (g + 0.95f) * 0.5f + mul;
        b = (b + 0.8f) * 0.5f + mul;

        return Util.getColor(Math.min((int)(r * 255), 0xFF), Math.min((int)(g * 255), 0xFF), Math.min((int)(b * 255), 0xFF));
    }

    public static AncientGrainType get(RegistryEntry<Biome> biome) {
        if (biome != null && !CACHE.containsKey(biome)) {
            CACHE.put(biome, from(biome));
        }

        return CACHE.getOrDefault(biome, DEFAULT);
    }

    public static void reset() {
        CACHE.clear();
    }

    public static void init() {
        DynamicRegistrySetupCallback.EVENT.register(manager -> reset());
    }
}
