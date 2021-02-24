package io.github.foundationgames.sandwichable.block;

import io.github.foundationgames.sandwichable.block.DesalinatorBlock.FluidType;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;

public class BlockProperties {
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final BooleanProperty SNIPPED = BooleanProperty.of("snipped");

    public static final IntProperty AGE = IntProperty.of("age", 0, 4);
    public static final IntProperty STAGE = IntProperty.of("stage", 0, 4);

    public static final EnumProperty<FluidType> FLUID = EnumProperty.of("fluid", FluidType.class);
}
