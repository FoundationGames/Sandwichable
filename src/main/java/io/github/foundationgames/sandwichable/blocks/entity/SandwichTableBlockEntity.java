package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
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
        sandwich.setFromNbt(tag);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        sandwich.writeToNbt(nbt);
        return nbt;
    }

    @Override
    public void fromClientTag(NbtCompound nbt) {
        this.readNbt(nbt);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt) {
        return this.writeNbt(nbt);
    }

    @Override
    public Sandwich getSandwich() {
        return sandwich;
    }
}
