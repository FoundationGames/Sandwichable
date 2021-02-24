package io.github.foundationgames.sandwichable.compat.module;

import io.github.foundationgames.sandwichable.block.BasinBlock;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.loot.JCondition;
import net.devtech.arrp.json.loot.JEntry;
import net.devtech.arrp.json.loot.JLootTable;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.devtech.arrp.json.recipe.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class BasinModuleMember extends BlockRegistryModuleMember {
    private final String blockName;
    private final Identifier parentBlock;
    private final String texture;
    private final String ingredient;
    private final int outputCount;
    private final boolean isIngredientTag;

    public BasinModuleMember(String blockName, Identifier parentBlock, String texture, String ingredient, int outputCount) {
        this.blockName = blockName;
        this.parentBlock = parentBlock;
        this.texture = texture;
        this.isIngredientTag = ingredient.startsWith("#");
        this.ingredient = ingredient.replace("#", "");
        this.outputCount = outputCount;
    }

    @Override
    public Block createBlock() {
        Block parent = Registry.BLOCK.getOrEmpty(parentBlock).orElse(Blocks.STONE);
        return new BasinBlock(FabricBlockSettings.copy(parent));
    }

    @Override
    public Identifier getBlockId() {
        return Util.id(blockName);
    }

    @Override
    public void processResources(RuntimeResourcePack assets, RuntimeResourcePack data) {
        assets.addBlockState(
                JState.state(
                        JState.variant(
                                new JBlockModel(Util.id("block/" + this.blockName))
                        )
                ),
                Util.id(this.blockName)
        );
        assets.addModel(
                JModel.model()
                        .parent("sandwichable:block/generated_basin_template")
                        .textures(
                                new JTextures()
                                        .var("texture", texture)
                        ),
                Util.id("block/" + this.blockName)
        );
        assets.addModel(
                JModel.model()
                        .parent("sandwichable:block/" + this.blockName),
                Util.id("item/" + this.blockName)
        );
        data.addLootTable(
                Util.id("blocks/" + this.blockName),
                JLootTable.loot("block")
                        .pool(
                                JLootTable.pool()
                                        .rolls(1)
                                        .entry(new JEntry()
                                                .type("minecraft:item")
                                                .name("sandwichable:"+this.blockName)
                                        )
                                        .condition(new JCondition("minecraft:survives_explosion"))
                        )
        );
        JIngredient ing = isIngredientTag ? JIngredient.ingredient().tag(this.ingredient) : JIngredient.ingredient().item(ingredient);
        data.addRecipe(Util.id(this.blockName), JRecipe.shaped(
                JPattern.pattern("A A", " A "),
                JKeys.keys().key("A", ing),
                JResult.stackedResult("sandwichable:"+this.blockName, this.outputCount)
        ));
    }

    @Override
    public List<Block> getBlockEntityBlockList() {
        return BlocksRegistry.BASINS;
    }
}
