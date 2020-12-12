package io.github.foundationgames.sandwichable.entity;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.SpreadItem;
import io.github.foundationgames.sandwichable.util.Sandwich;
import io.github.foundationgames.sandwichable.util.SandwichHolder;
import io.github.foundationgames.sandwichable.util.SpreadRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SandwichTableMinecartEntity extends AbstractMinecartEntity implements SandwichHolder {
    private final Sandwich sandwich = new Sandwich();
    private Vec3d lastVelocity;

    public SandwichTableMinecartEntity(World world) {
        super(EntitiesRegistry.SANDWICH_TABLE_MINECART, world);
        sync();
    }

    public SandwichTableMinecartEntity(World world, double x, double y, double z) {
        super(EntitiesRegistry.SANDWICH_TABLE_MINECART, world, x, y, z);
        sync();
    }

    public SandwichTableMinecartEntity(EntityType<SandwichTableMinecartEntity> type, World world) {
        super(type, world);
        sync();
    }

    @Override
    public void tick() {
        lastVelocity = getVelocity();
        super.tick();
    }

    public Vec3d getLastVelocity() {
        return lastVelocity;
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
        }
    }

    public void sync() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(getEntityId());
        if(!world.isClient) {
            CompoundTag t = new CompoundTag();
            writeSandwichTableData(t);
            buf.writeCompoundTag(t);
            for(PlayerEntity player : world.getPlayers()) {
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Util.id("sync_sandwich_table_cart"), buf);
            }
            //System.out.println("synced to all clients, "+t);
        } else {
            ClientSidePacketRegistry.INSTANCE.sendToServer(Util.id("request_sandwich_table_cart_sync"), buf);
        }
    }

    public void readSandwichTableData(CompoundTag tag) {
        sandwich.setFromTag(tag);
    }

    public void writeSandwichTableData(CompoundTag tag) {
        sandwich.addToTag(tag);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        /*if(player.getStackInHand(hand).getItem().equals(BlocksRegistry.SANDWICH.asItem()) && player.getStackInHand(hand).getTag() != null && this.getFoodListSize() == 0) {
            DefaultedList<ItemStack> sandwichlist = DefaultedList.ofSize(128, ItemStack.EMPTY);
            CompoundTag tag = player.getStackInHand(hand).getSubTag("BlockEntityTag");
            Inventories.fromTag(tag, sandwichlist);
            player.getStackInHand(hand).decrement(1);
            this.setFoodList(sandwichlist);
        } else if(!player.getStackInHand(hand).isEmpty() && (player.getStackInHand(hand).isFood() || SpreadRegistry.INSTANCE.itemHasSpread(player.getStackInHand(hand).getItem())) && player.getStackInHand(hand).getItem() != BlocksRegistry.SANDWICH.asItem()) {
            if (Sandwichable.BREADS.contains(this.getFoodList().get(0).getItem()) || Sandwichable.BREADS.contains(player.getStackInHand(hand).getItem())) {
                ItemStack foodToBeAdded = player.getStackInHand(hand);
                this.addFood(player, foodToBeAdded);
            } else {
                player.sendMessage(new TranslatableText("message.sandwichtable.bottombread"), true);
            }
        } else if(this.getFoodListSize() > 0 && player.getStackInHand(hand).isEmpty()){
            if(!player.isSneaking()) {
                if (!player.isCreative()) {
                    ItemEntity item = new ItemEntity(world, getPos().getX(), getPos().getY() + 1.2, getPos().getZ(), this.removeTopFood());
                    world.spawnEntity(item);
                } else {
                    this.removeTopFood();
                }
            } else if(Sandwichable.BREADS.contains(this.getTopFood().getItem())){
                ItemStack item = new ItemStack(BlocksRegistry.SANDWICH);
                CompoundTag tag = this.sandwichToTag(new CompoundTag());
                if(!tag.isEmpty()) {
                    item.putSubTag("BlockEntityTag", tag);
                }
                ItemEntity itemEntity = new ItemEntity(world, getPos().getX(), getPos().getY()+1.2, getPos().getZ(), item);
                itemEntity.setToDefaultPickupDelay();
                this.setFoodList(DefaultedList.ofSize(128, ItemStack.EMPTY));
                world.spawnEntity(itemEntity);
            } else {
                player.sendMessage(new TranslatableText("message.sandwichtable.topbread"), true);
            }
        }*/
        sandwich.interact(world, getPos(), player, hand);
        sync();
        return ActionResult.SUCCESS;
    }



    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        sync();
    }

    @Override
    public void dropItems(DamageSource damageSource) {
        super.dropItems(damageSource);
        if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.dropItem(BlocksRegistry.SANDWICH_TABLE);
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        readSandwichTableData(tag);
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
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
