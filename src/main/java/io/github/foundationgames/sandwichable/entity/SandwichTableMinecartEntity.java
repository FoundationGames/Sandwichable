package io.github.foundationgames.sandwichable.entity;

import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.util.Sandwich;
import io.github.foundationgames.sandwichable.util.SandwichHolder;
import io.github.foundationgames.sandwichable.util.Util;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SandwichTableMinecartEntity extends AbstractMinecartEntity implements SandwichHolder {
    private final Sandwich sandwich = new Sandwich();

    public SandwichTableMinecartEntity(World world) {
        super(EntitiesRegistry.SANDWICH_TABLE_MINECART, world);
    }

    public SandwichTableMinecartEntity(World world, double x, double y, double z) {
        super(EntitiesRegistry.SANDWICH_TABLE_MINECART, world, x, y, z);
    }

    public SandwichTableMinecartEntity(EntityType<SandwichTableMinecartEntity> type, World world) {
        super(type, world);
    }

    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    @Override
    public void onActivatorRail(int x, int y, int z, boolean powered) {
        super.onActivatorRail(x, y, z, powered);
        if(powered) {
            sandwich.ejectSandwich(world, getPos());
            sync();
        }
    }

    public void sync() {
        if(!world.isClient) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(getId());
            NbtCompound t = new NbtCompound();
            writeSandwichTableData(t);
            buf.writeNbt(t);
            for(PlayerEntity player : world.getPlayers()) {
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Util.id("sync_sandwich_table_cart"), buf);
            }
        }
    }

    public void clientSync() {
        if(world.isClient) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(getId());
            ClientSidePacketRegistry.INSTANCE.sendToServer(Util.id("request_sandwich_table_cart_sync"), buf);
        }
    }

    public void readSandwichTableData(NbtCompound tag) {
        sandwich.setFromTag(tag);
    }

    public void writeSandwichTableData(NbtCompound tag) {
        sandwich.addToTag(tag);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        sandwich.interact(world, getPos(), player, hand);
        sync();
        return ActionResult.SUCCESS;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        clientSync();
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
    }

    @Override
    public void dropItems(DamageSource damageSource) {
        super.dropItems(damageSource);
        if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.dropItem(BlocksRegistry.SANDWICH_TABLE);
            this.sandwich.ejectSandwich(world, getPos());
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        readSandwichTableData(tag);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        writeSandwichTableData(tag);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public BlockState getContainedBlock() {
        return BlocksRegistry.SANDWICH_TABLE.getDefaultState();
    }

    @Override
    public Sandwich getSandwich() {
        return sandwich;
    }
}
