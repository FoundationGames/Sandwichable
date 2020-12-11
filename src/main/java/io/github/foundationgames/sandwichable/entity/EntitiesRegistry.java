package io.github.foundationgames.sandwichable.entity;

import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class EntitiesRegistry {
    public static final EntityType<SandwichTableMinecartEntity> SANDWICH_TABLE_MINECART = Registry.register(
            Registry.ENTITY_TYPE,
            Util.id("sandwich_table_minecart"),
            FabricEntityTypeBuilder.<SandwichTableMinecartEntity>create(SpawnGroup.MISC, SandwichTableMinecartEntity::new).dimensions(EntityDimensions.fixed(0.98F, 0.7F)).build()
    );

    public static void init() {

    }
}
