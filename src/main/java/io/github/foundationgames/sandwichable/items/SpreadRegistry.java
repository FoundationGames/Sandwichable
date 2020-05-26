package io.github.foundationgames.sandwichable.items;

import com.google.common.collect.Maps;
import io.github.foundationgames.sandwichable.items.spread.SpreadType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;

import java.util.Map;

public class SpreadRegistry {
    public static final SpreadRegistry INSTANCE = new SpreadRegistry();

    private final Map<String, SpreadType> idToSpreadType = Maps.newHashMap();
    private final Map<SpreadType, String> spreadTypeToString = Maps.newHashMap();
    private final Map<ItemConvertible, SpreadType> spreadContainerToType = Maps.newHashMap();

    private SpreadRegistry() {}

    public SpreadType register(String id, SpreadType type) {
        idToSpreadType.put(id, type);
        spreadTypeToString.put(type, id);
        spreadContainerToType.put(type.getContainingItem(), type);
        return type;
    }

    public SpreadType getSpreadFromItem(ItemConvertible item) {
        return spreadContainerToType.get(item);
    }

    public boolean itemHasSpread(ItemConvertible item) {
        return spreadContainerToType.containsKey(item);
    }

    public SpreadType deserialize(String id) {
        return idToSpreadType.get(id);
    }
    public String serialize(SpreadType type) {
        return spreadTypeToString.get(type);
    }
}
