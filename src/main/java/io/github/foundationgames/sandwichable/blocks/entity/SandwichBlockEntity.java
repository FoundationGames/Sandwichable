package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.util.Sandwich;
import io.github.foundationgames.sandwichable.util.SandwichHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SandwichBlockEntity extends BlockEntity implements SandwichHolder, SyncedBlockEntity {
    private final Sandwich sandwich = new Sandwich();

    public SandwichBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.SANDWICH_BLOCKENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        sandwich.setFromNbt(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        sandwich.writeToNbt(nbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return this.getPacket();
    }

    @Override
    public Sandwich getSandwich() {
        return sandwich;
    }
}
