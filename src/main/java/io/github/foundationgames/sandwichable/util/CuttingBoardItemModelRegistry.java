package io.github.foundationgames.sandwichable.util;

import com.google.common.collect.Maps;
import io.github.foundationgames.sandwichable.Sandwichable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class CuttingBoardItemModelRegistry {
    public static CuttingBoardItemModelRegistry INSTANCE = new CuttingBoardItemModelRegistry();

    private CuttingBoardItemModelRegistry() {}

    private final Map<Item, CuttingBoardItemModel> entries = Maps.newHashMap();

    public void register(CuttingBoardItemModel model, Item item) {
        if(!entries.containsKey(item)) {
            entries.put(item, model);
        }
    }

    public CuttingBoardItemModel getModelForItem(Item item) {
        return entries.get(item);
    }
}
