package io.github.foundationgames.sandwichable.config;

import io.github.foundationgames.sandwichable.Sandwichable;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer.GlobalData;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.gui.screen.Screen;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Config(name = "sandwichable")
@Config.Gui.Background("minecraft:textures/block/spruce_planks.png")
@Config.Gui.CategoryBackground(category = "gameplay", background = "minecraft:textures/block/oak_planks.png")
@Config.Gui.CategoryBackground(category = "server_gameplay", background = "minecraft:textures/block/crimson_planks.png")
@Config.Gui.CategoryBackground(category = "world_gen", background = "minecraft:textures/block/smooth_stone.png")
public class SandwichableConfig implements ConfigData {

    @ConfigEntry.Category(value = "gameplay")
    public boolean showInfoTooltips = true;

    @ConfigEntry.Category(value = "gameplay")
    public TooltipKeyBind infoTooltipKeyBind = TooltipKeyBind.SHIFT;

    @ConfigEntry.Category(value = "server_gameplay")
    public boolean slowEatingLargeSandwiches = true;
    @ConfigEntry.Category(value = "server_gameplay")
    public int baseSandwichEatTime = 32;

    @ConfigEntry.Category(value = "world_gen")
    @ConfigEntry.Gui.CollapsibleObject
    public SaltySandGenOptions saltySandGenOptions = new SaltySandGenOptions();

    @ConfigEntry.Category(value = "world_gen")
    @ConfigEntry.Gui.CollapsibleObject
    public ShrubGenOptions shrubGenOptions = new ShrubGenOptions();

    public static class SaltySandGenOptions {

        public int rarity = 18;
        public int veinSize = 5;

        @ConfigEntry.BoundedDiscrete(max = 255)
        public int maxGenHeight = 128;

    }
    public static class ShrubGenOptions {
        public int spawnTries = 10;
    }
    public static class DesalinatorOptions {
        //FUTURE UPDATE
        @ConfigEntry.Gui.Excluded
        public String[] saltyBiomes = new String[] {
            "category=OCEANS",
            "category=BEACHES"
        };
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
