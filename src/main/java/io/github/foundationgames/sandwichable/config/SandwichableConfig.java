package io.github.foundationgames.sandwichable.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

@Config(name = "sandwichable")
@Config.Gui.Background("minecraft:textures/block/spruce_planks.png")
@Config.Gui.CategoryBackground(category = "gameplay", background = "minecraft:textures/block/oak_planks.png")
@Config.Gui.CategoryBackground(category = "server_gameplay", background = "minecraft:textures/block/crimson_planks.png")
public class SandwichableConfig implements ConfigData {

    @ConfigEntry.Category(value = "gameplay")
    public boolean showInfoTooltips = true;

    @ConfigEntry.Category(value = "gameplay")
    public TooltipKeyBind infoTooltipKeyBind = TooltipKeyBind.SHIFT;

    @ConfigEntry.Category(value = "server_gameplay")
    public boolean slowEatingLargeSandwiches = true;
    @ConfigEntry.Category(value = "server_gameplay")
    public int baseSandwichEatTime = 32;

    @ConfigEntry.Gui.Excluded
    public ItemOptions itemOptions = new ItemOptions();

    @ConfigEntry.Gui.Excluded
    public SaltySandGenOptions saltySandGenOptions = new SaltySandGenOptions();

    @ConfigEntry.Gui.Excluded
    public ShrubGenOptions shrubGenOptions = new ShrubGenOptions();

    @ConfigEntry.Gui.Excluded
    public SaltPoolGenOptions saltPoolGenOptions = new SaltPoolGenOptions();

    public static class SaltySandGenOptions {
        @ConfigEntry.Gui.Excluded
        public int rarity = 18;
        @ConfigEntry.Gui.Excluded
        public int veinSize = 5;

        @ConfigEntry.BoundedDiscrete(max = 255)
        @ConfigEntry.Gui.Excluded
        public int maxGenHeight = 128;
    }

    public static class ShrubGenOptions {
        @ConfigEntry.Gui.Excluded
        public int spawnTries = 10;
    }

    public static class SaltPoolGenOptions {
        @ConfigEntry.Gui.Excluded
        public boolean saltPools = true;
        @ConfigEntry.Gui.Excluded
        public boolean drySaltPools = true;
    }

    public static class ItemOptions {
        @ConfigEntry.Gui.Excluded
        public KitchenKnifeOption[] knives = knivesDefault();
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
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
        KitchenKnifeOption[] defaults = knivesDefault();
        for (KitchenKnifeOption def : defaults) {
            KitchenKnifeOption opt = getKnifeOption(def.itemId);
            if (opt != null && opt.sharpness == 0) {
                opt.sharpness = def.sharpness;
            }
        }
    }

    public static class KitchenKnifeOption {
        @ConfigEntry.Gui.Excluded
        public String itemId;

        @ConfigEntry.Gui.Excluded
        public int value;

        @ConfigEntry.Gui.Excluded
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
