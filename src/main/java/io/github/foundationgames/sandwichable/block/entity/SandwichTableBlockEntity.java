package io.github.foundationgames.sandwichable.block.entity;

import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.util.Sandwich;
import io.github.foundationgames.sandwichable.util.SandwichHolder;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

public class SandwichTableBlockEntity extends BlockEntity implements SandwichHolder, BlockEntityClientSerializable {

    private final Sandwich sandwich = new Sandwich();

    public SandwichTableBlockEntity() {
        super(BlocksRegistry.SANDWICHTABLE_BLOCKENTITY);
    }

    @Override
    public void readNbt(BlockState state, NbtCompound tag) {
        super.readNbt(state, tag);
        sandwich.setFromTag(tag);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        sandwich.addToTag(tag);
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound compoundTag) {
        this.readNbt(world.getBlockState(pos), compoundTag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound compoundTag) {
        return this.writeNbt(compoundTag);
    }

    @Override
    public Sandwich getSandwich() {
        return sandwich;
    }
}
