package io.github.foundationgames.sandwichable.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

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
    }

    public static class ItemOptions {
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
