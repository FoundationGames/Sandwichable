package io.github.foundationgames.sandwichable.block.entity;

import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.util.Sandwich;
import io.github.foundationgames.sandwichable.util.SandwichHolder;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SandwichTableBlockEntity extends BlockEntity implements SandwichHolder, BlockEntityClientSerializable {

    private final Sandwich sandwich = new Sandwich();

    public SandwichTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.SANDWICHTABLE_BLOCKENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
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
        this.readNbt(compoundTag);
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
