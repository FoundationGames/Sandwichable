package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.util.Sandwich;
import io.github.foundationgames.sandwichable.util.SandwichHolder;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

public class SandwichBlockEntity extends BlockEntity implements SandwichHolder, BlockEntityClientSerializable {
    private final Sandwich sandwich = new Sandwich();

    public SandwichBlockEntity() {
        super(BlocksRegistry.SANDWICH_BLOCKENTITY);
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);
        sandwich.setFromNbt(tag);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        sandwich.writeToNbt(tag);
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound NbtCompound) {
        this.fromTag(world.getBlockState(pos), NbtCompound);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound NbtCompound) {
        return this.writeNbt(NbtCompound);
    }

    @Override
    public Sandwich getSandwich() {
        return sandwich;
    }
}
