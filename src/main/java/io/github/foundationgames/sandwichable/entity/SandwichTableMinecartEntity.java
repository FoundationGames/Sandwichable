package io.github.foundationgames.sandwichable.entity;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.SpreadItem;
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
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SandwichTableMinecartEntity extends AbstractMinecartEntity {
    private DefaultedList<ItemStack> foods = DefaultedList.ofSize(128, ItemStack.EMPTY);

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
    public Type getMinecartType() {
        return null;
    }

    @Override
    public void onActivatorRail(int x, int y, int z, boolean powered) {
        super.onActivatorRail(x, y, z, powered);
        if(powered) {
            if(this.getFoodListSize() > 0) {
                if(Sandwichable.BREADS.contains(this.getTopFood().getItem()) && this.getFoodListSize() > 1){
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
                    for(ItemStack stack : this.getFoodList()) {
                        if(!stack.isEmpty() && stack.getItem() != ItemsRegistry.SPREAD) {
                            ItemEntity item = new ItemEntity(world, getPos().getX(), getPos().getY() + 1.2, getPos().getZ(), stack);
                            world.spawnEntity(item);
                            this.setFoodList(DefaultedList.ofSize(128, ItemStack.EMPTY));
                        }
                    }
                }
            }
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
        } else {
            ClientSidePacketRegistry.INSTANCE.sendToServer(Util.id("request_sandwich_table_cart_sync"), buf);
        }
    }

    public void readSandwichTableData(CompoundTag tag) {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(128, ItemStack.EMPTY);
        Inventories.fromTag(tag, list);
        setFoodList(list);
    }

    public void writeSandwichTableData(CompoundTag tag) {
        Inventories.toTag(tag, this.foods);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if(player.getStackInHand(hand).getItem().equals(BlocksRegistry.SANDWICH.asItem()) && player.getStackInHand(hand).getTag() != null && this.getFoodListSize() == 0) {
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
        }
        return ActionResult.SUCCESS;
    }

    public void addFood(PlayerEntity player, ItemStack playerStack) {
        int i=0;
        while(this.foods.get(i)!=ItemStack.EMPTY && i < this.foods.size()-1) {i++;}
        ItemStack stack;
        if(!player.abilities.creativeMode && !(getFoodListSize() >= 127)) {
            stack = playerStack.split(1);
        } else {
            stack = playerStack.copy();
        }
        if (i < this.foods.size()-1) {
            if(SpreadRegistry.INSTANCE.itemHasSpread(stack.getItem())) {
                ItemStack spread = new ItemStack(ItemsRegistry.SPREAD, 1);
                SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem()).onPour(stack, spread);
                spread.getOrCreateTag().putString("spreadType", SpreadRegistry.INSTANCE.asString(SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem())));
                this.foods.set(i, spread);
                if(!player.isCreative()) {
                    player.giveItemStack(SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem()).getResultItem());
                }
            } else {
                this.foods.set(i, stack);
            }
        }
        if(this.getFoodListSize() >= 127) {
            player.sendMessage(new TranslatableText("message.sandwichtable.fullsandwich").formatted(Formatting.RED), true);
        }
        sync();
    }

    public void addTopStackFrom(ItemStack stack) {
        int i=0;
        while(this.foods.get(i)!=ItemStack.EMPTY && i < this.foods.size()-1) {i++;}
        ItemStack nstack = stack.split(1);
        this.foods.set(i, nstack);
        sync();
    }

    public ItemStack removeTopFood() {
        ItemStack r;
        int i=0;
        while(this.foods.get(i)!=ItemStack.EMPTY) {i++;}
        r = this.foods.get(i-1);
        this.foods.set(i-1, ItemStack.EMPTY);
        sync();
        return r.getItem() instanceof SpreadItem ? ItemStack.EMPTY : r;
    }

    public ItemStack getTopFood() {
        ItemStack r;
        int i=0;
        while(this.foods.get(i)!=ItemStack.EMPTY) {i++;}
        r = this.foods.get(i-1);
        return r;
    }

    public DefaultedList<ItemStack> getFoodList() {
        return this.foods;
    }

    public void setFoodList(DefaultedList<ItemStack> list) {
        this.foods = list;
        sync();
    }

    public void dropItems(DamageSource damageSource) {
        super.dropItems(damageSource);
        if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.dropItem(BlocksRegistry.SANDWICH_TABLE);
        }
    }

    public int getFoodListSize() {
        int i=0;
        while(this.foods.get(i)!=ItemStack.EMPTY && i < 128) {i++;}
        return i;
    }

    public CompoundTag sandwichToTag(CompoundTag tag) {
        Inventories.toTag(tag, this.foods);
        return tag;
    }

    public void sandwichFromTag(CompoundTag tag) {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(128, ItemStack.EMPTY);
        Inventories.fromTag(tag, list);
        setFoodList(list);
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
}
