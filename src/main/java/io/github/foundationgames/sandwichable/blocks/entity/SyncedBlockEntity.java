package io.github.foundationgames.sandwichable.blocks.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;

public interface SyncedBlockEntity {
    default void sync() {
        if (this instanceof BlockEntity be && be.getWorld() instanceof ServerWorld world) {
            world.getChunkManager().markForUpdate(be.getPos());
        }
    }

    default Packet<ClientPlayPacketListener> getPacket() {
        if (this instanceof BlockEntity be) {
            return BlockEntityUpdateS2CPacket.create(be);
        }
        return null;
    }
}
