package io.github.foundationgames.sandwichable.config;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import io.github.foundationgames.sandwichable.worldgen.CascadeFeatureConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SandwichableConfig extends ConfigInABarrel {
    public boolean showInfoTooltips = true;
    public TooltipKeyBind infoTooltipKeyBind = TooltipKeyBind.SHIFT;

    public boolean slowEatingLargeSandwiches = true;
    public int baseSandwichEatTime = 32;

    @Value(gui = false) public ItemOptions itemOptions = new ItemOptions();
    public SaltySandGenOptions saltySandGenOptions = new SaltySandGenOptions();
    public ShrubGenOptions shrubGenOptions = new ShrubGenOptions();
    public SaltPoolGenOptions saltPoolGenOptions = new SaltPoolGenOptions();

    public static class SaltySandGenOptions {
        public int rarity = 18;
        public int veinSize = 5;
        public int maxGenHeight = 128;
    }

    public static class ShrubGenOptions {
        public int spawnTries = 10;
    }

    public static class SaltPoolGenOptions {
        public boolean saltPools = true;
        public boolean drySaltPools = true;
        @Value(gui = false) public transient @Nullable CascadeFeatureConfig waterSaltPoolConfig = null;
        @Value(gui = false) public transient @Nullable CascadeFeatureConfig drySaltPoolConfig = null;
    }

    public static class ItemOptions {
        public KitchenKnifeOption[] knives = knivesDefault();
        @Value(gui = false) public String saltItem = "sandwichable:salt";
        @Value(gui = false) public String cucumberItem = "sandwichable:cucumber";
        @Value(gui = false) public String pickledCucumberItem = "sandwichable:pickled_cucumber";
        @Value(gui = false) public String burntFoodItem = "sandwichable:burnt_food";
        @Value(gui = false) public String burntMorselItem = "sandwichable:burnt_morsel";

        @Value(gui = false) public transient final Supplier<ItemStack> saltItemGetter = Suppliers.memoize(() ->
                Util.itemFromString(saltItem, () -> new ItemStack(ItemsRegistry.SALT)));
        @Value(gui = false) public transient final Supplier<ItemStack> cucumberItemGetter = Suppliers.memoize(() ->
                Util.itemFromString(cucumberItem, () -> new ItemStack(ItemsRegistry.CUCUMBER)));
        @Value(gui = false) public transient final Supplier<ItemStack> pickledCucumberItemGetter = Suppliers.memoize(() ->
                Util.itemFromString(pickledCucumberItem, () -> new ItemStack(ItemsRegistry.PICKLED_CUCUMBER)));
        @Value(gui = false) public transient final Supplier<ItemStack> burntFoodItemGetter = Suppliers.memoize(() ->
                Util.itemFromString(burntFoodItem, () -> new ItemStack(ItemsRegistry.BURNT_FOOD)));
        @Value(gui = false) public transient final Supplier<ItemStack> burntMorselItemGetter = Suppliers.memoize(() ->
                Util.itemFromString(burntMorselItem, () -> new ItemStack(ItemsRegistry.BURNT_MORSEL)));
    }

    public static KitchenKnifeOption[] knivesDefault() {
        return new KitchenKnifeOption[] {
                new KitchenKnifeOption("sandwichable:stone_kitchen_knife", 1, 132),
                new KitchenKnifeOption("sandwichable:kitchen_knife", 3, 850),
                new KitchenKnifeOption("sandwichable:golden_kitchen_knife", 5, 225),
                new KitchenKnifeOption("sandwichable:diamond_kitchen_knife", 8, 1025),
                new KitchenKnifeOption("sandwichable:netherite_kitchen_knife", 20, 1984),
                new KitchenKnifeOption("sandwichable:glass_kitchen_knife", 1, 0)
        };
    }

    public KitchenKnifeOption getKnifeOption(String knife) {
        for (KitchenKnifeOption opt : itemOptions.knives) {
            if (knife.equals(opt.itemId)) {
                return opt;
            }
        }
        return null;
    }

    public KitchenKnifeOption getKnifeOption(Item knife) {
        return this.getKnifeOption(Registry.ITEM.getId(knife).toString());
    }

    @Override
    public void afterLoad() {
        super.afterLoad();
        KitchenKnifeOption[] defaults = knivesDefault();
        for (KitchenKnifeOption def : defaults) {
            KitchenKnifeOption opt = getKnifeOption(def.itemId);
            if (opt != null && opt.sharpness == 0) {
                opt.sharpness = def.sharpness;
            }
        }
    }

    @Override
    protected void loadExtraData(JsonObject file) {
        this.saltPoolGenOptions.waterSaltPoolConfig = null;
        this.saltPoolGenOptions.drySaltPoolConfig = null;
        if (file.has("saltPoolGenOptions") && file.get("saltPoolGenOptions").isJsonObject()) {
            JsonObject saltPools = file.getAsJsonObject("saltPoolGenOptions");
            if (saltPools.has("waterSaltPoolConfig")) {
                DataResult<Pair<CascadeFeatureConfig, JsonElement>> result = CascadeFeatureConfig.CODEC.decode(JsonOps.INSTANCE, saltPools.get("waterSaltPoolConfig"));
                result.get().ifLeft(p -> this.saltPoolGenOptions.waterSaltPoolConfig = p.getFirst()).ifRight(p -> Sandwichable.LOG.error(p.message()));
            }
            if (saltPools.has("drySaltPoolConfig")) {
                DataResult<Pair<CascadeFeatureConfig, JsonElement>> result = CascadeFeatureConfig.CODEC.decode(JsonOps.INSTANCE, saltPools.get("drySaltPoolConfig"));
                result.get().ifLeft(p -> this.saltPoolGenOptions.drySaltPoolConfig = p.getFirst()).ifRight(p -> Sandwichable.LOG.error(p.message()));
            }
        }
    }

    public static class KitchenKnifeOption {
        public String itemId;
        public int value;
        public int sharpness;

        public KitchenKnifeOption(String item, int value, int sharpness) {
            this.itemId = item;
            this.value = value;
            this.sharpness = sharpness;
        }
    }

    public enum TooltipKeyBind {
        SHIFT("shift"),
        CTRL("control"),
        ALT("alt");

        String name;

        TooltipKeyBind(String name) { this.name = name; }

        public boolean isPressed() {
            if(this.equals(SHIFT)) {
                return Screen.hasShiftDown();
            } else if(this.equals(CTRL)) {
                return Screen.hasControlDown();
            } else if(this.equals(ALT)) {
                return Screen.hasAltDown();
            }
            return false;
        }

        public String getName() {
            return name;
        }
    }
}
