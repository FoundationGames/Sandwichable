package io.github.foundationgames.sandwichable.block.entity;

import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.util.Sandwich;
import io.github.foundationgames.sandwichable.util.SandwichHolder;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

public class SandwichBlockEntity extends BlockEntity implements SandwichHolder, BlockEntityClientSerializable {
    private final Sandwich sandwich = new Sandwich();

    public SandwichBlockEntity() {
        super(BlocksRegistry.SANDWICH_BLOCKENTITY);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        sandwich.setFromTag(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        sandwich.addToTag(tag);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(world.getBlockState(pos), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }

    @Override
    public Sandwich getSandwich() {
        return sandwich;
    }
}
