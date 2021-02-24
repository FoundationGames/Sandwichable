package io.github.foundationgames.sandwichable.compat.module;

import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.block.CuttingBoardBlock;
import io.github.foundationgames.sandwichable.util.Util;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.blockstate.JVariant;
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

public class CuttingBoardModuleMember extends BlockRegistryModuleMember {
    private final String blockName;
    private final Identifier parentBlock;
    private final String texture;

    private Block parent;

    public CuttingBoardModuleMember(String blockName, Identifier parentBlock, String texture) {
        this.blockName = blockName;
        this.parentBlock = parentBlock;
        this.texture = texture;
    }

    @Override
    public Block createBlock() {
        return new CuttingBoardBlock(FabricBlockSettings.copy(getParentBlock()));
    }

    @Override
    public Identifier getBlockId() {
        return Util.id(blockName);
    }

    @Override
    public void processResources(RuntimeResourcePack assets, RuntimeResourcePack data) {
        assets.addBlockState(
                JState.state(
                        JState.variant()
                                .put("facing=north", new JBlockModel(Util.id("block/" + this.blockName)).y(90))
                                .put("facing=south", new JBlockModel(Util.id("block/" + this.blockName)).y(270))
                                .put("facing=east", new JBlockModel(Util.id("block/" + this.blockName)))
                                .put("facing=west", new JBlockModel(Util.id("block/" + this.blockName)).y(180))
                ),
                Util.id(this.blockName)
        );
        assets.addModel(
                JModel.model()
                        .parent("sandwichable:block/generated_cutting_board_template")
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
        data.addRecipe(Util.id(this.blockName), JRecipe.shaped(
                JPattern.pattern("AA"),
                JKeys.keys().key("A", JIngredient.ingredient().item(parentBlock.toString())),
                JResult.stackedResult("sandwichable:"+this.blockName, 2)
        ));
    }

    @Override
    public List<Block> getBlockEntityBlockList() {
        return BlocksRegistry.CUTTING_BOARDS;
    }

    private Block getParentBlock() {
        if(parent == null) {
            parent = Registry.BLOCK.getOrEmpty(parentBlock).orElse(Blocks.OAK_PLANKS);
        }
        return parent;
    }
}
