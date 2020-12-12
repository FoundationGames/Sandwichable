package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.entity.SandwichTableBlockEntity;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.SpreadItem;
import io.github.foundationgames.sandwichable.util.SpreadRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class SandwichTableBlock extends Block implements BlockEntityProvider {
    public SandwichTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new SandwichTableBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof SandwichTableBlockEntity) {
            SandwichTableBlockEntity sBlockEntity = (SandwichTableBlockEntity)world.getBlockEntity(pos);
            /*if(player.getStackInHand(hand).getItem().equals(BlocksRegistry.SANDWICH.asItem()) && player.getStackInHand(hand).getTag() != null && sBlockEntity.getSandwich().isEmpty()) {
                CompoundTag tag = player.getStackInHand(hand).getSubTag("BlockEntityTag");
                sBlockEntity.getSandwich().setFromTag(tag);
                Util.sync(sBlockEntity, world);
            } else if(!player.getStackInHand(hand).isEmpty() && (player.getStackInHand(hand).isFood() || SpreadRegistry.INSTANCE.itemHasSpread(player.getStackInHand(hand).getItem())) && player.getStackInHand(hand).getItem() != BlocksRegistry.SANDWICH.asItem()) {
                if() {
                } else {
                    player.sendMessage(new TranslatableText("message.sandwichtable.bottombread"), true);
                }
            } else if(((SandwichTableBlockEntity)blockEntity).getFoodListSize() > 0 && player.getStackInHand(hand).isEmpty()){
                if(!player.isSneaking()) {
                    if (!player.isCreative()) {
                        ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, ((SandwichTableBlockEntity) blockEntity).removeTopFood());
                        world.spawnEntity(item);
                    } else {
                        ((SandwichTableBlockEntity) blockEntity).removeTopFood();
                    }
                } else if(Sandwichable.BREADS.contains(((SandwichTableBlockEntity)blockEntity).getTopFood().getItem())){
                    ItemStack item = new ItemStack(BlocksRegistry.SANDWICH);
                    CompoundTag tag = sBlockEntity.sandwichToTag(new CompoundTag());
                    if(!tag.isEmpty()) {
                        item.putSubTag("BlockEntityTag", tag);
                    }
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+1.2, pos.getZ()+0.5, item);
                    itemEntity.setToDefaultPickupDelay();
                    sBlockEntity.setFoodList(DefaultedList.ofSize(128, ItemStack.EMPTY));
                    world.spawnEntity(itemEntity);
                } else {
                    player.sendMessage(new TranslatableText("message.sandwichtable.topbread"), true);
                }
            }*/
            sBlockEntity.getSandwich().interact(world, new Vec3d(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5), player, hand);
            Util.sync(sBlockEntity, world);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        ejectSandwich(world, pos);
    }

    public void ejectSandwich(World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof SandwichTableBlockEntity) {
            SandwichTableBlockEntity blockEntity = (SandwichTableBlockEntity)be;
            /*if(blockEntity.getFoodListSize() > 0) {
                if(Sandwichable.BREADS.contains(blockEntity.getTopFood().getItem()) && blockEntity.getFoodListSize() > 1){
                    ItemStack item = new ItemStack(BlocksRegistry.SANDWICH);
                    CompoundTag tag = blockEntity.sandwichToTag(new CompoundTag());
                    if(!tag.isEmpty()) {
                        item.putSubTag("BlockEntityTag", tag);
                    }
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+1.2, pos.getZ()+0.5, item);
                    itemEntity.setToDefaultPickupDelay();
                    blockEntity.setFoodList(DefaultedList.ofSize(128, ItemStack.EMPTY));
                    world.spawnEntity(itemEntity);
                } else {
                    for(ItemStack stack : blockEntity.getFoodList()) {
                        if(!stack.isEmpty() && stack.getItem() != ItemsRegistry.SPREAD) {
                            ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, stack);
                            world.spawnEntity(item);
                            blockEntity.setFoodList(DefaultedList.ofSize(128, ItemStack.EMPTY));
                        }
                    }
                }
            }*/
            Util.sync(blockEntity, world);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SandwichTableBlockEntity) {
                SandwichTableBlockEntity blockEntity = (SandwichTableBlockEntity)world.getBlockEntity(pos);
                /*for(int i=0;i<blockEntity.getFoodListSize();i++) {
                    ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, blockEntity.getFoodList().get(i).getItem() instanceof SpreadItem ? ItemStack.EMPTY : blockEntity.getFoodList().get(i));
                    world.spawnEntity(item);
                }*/
                blockEntity.getSandwich().ejectSandwich(world, new Vec3d(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5));
                world.updateNeighbors(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
