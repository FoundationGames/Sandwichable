package io.github.foundationgames.sandwichable.compat.module;

import io.github.foundationgames.sandwichable.compat.CompatModuleManager;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public abstract class BlockRegistryModuleMember extends AbstractModuleMember {
    @Override
    public final void onInitialized() {
        Identifier id = getBlockId();
        Block block = createBlock();
        Item item = createItem(block, CompatModuleManager.SANDWICHABLE_COMPAT);
        if(getBlockEntityBlockList() != null) getBlockEntityBlockList().add(block);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, item);
        processResources(CompatModuleManager.ASSETS, CompatModuleManager.DATA);
    }

    public abstract Block createBlock();

    public Item createItem(Block block, ItemGroup group) {
        return new BlockItem(block, new Item.Settings().group(group));
    }

    public abstract Identifier getBlockId();

    public abstract void processResources(RuntimeResourcePack assets, RuntimeResourcePack data);

    public List<Block> getBlockEntityBlockList() {
        return null;
    }
}
