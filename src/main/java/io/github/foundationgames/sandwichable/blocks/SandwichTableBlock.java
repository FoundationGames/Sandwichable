package io.github.foundationgames.sandwichable.blocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.entity.SandwichBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.SandwichTableBlockEntity;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.spread.SpreadItem;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SandwichTableBlock extends Block implements BlockEntityProvider {
    public SandwichTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new SandwichTableBlockEntity();
    }

    public ImmutableList<BlockState> getStates() {
        return this.stateManager.getStates();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof SandwichTableBlockEntity) {
            if(player.getStackInHand(hand).getItem().equals(BlocksRegistry.SANDWICH.asItem()) && player.getStackInHand(hand).getTag() != null && ((SandwichTableBlockEntity)blockEntity).getFoodListSize() == 0) {
                DefaultedList<ItemStack> sandwichlist = DefaultedList.ofSize(128, ItemStack.EMPTY);
                CompoundTag tag = player.getStackInHand(hand).getSubTag("BlockEntityTag");
                Inventories.fromTag(tag, sandwichlist);
                player.getStackInHand(hand).decrement(1);
                ((SandwichTableBlockEntity)blockEntity).setFoodList(sandwichlist);
            } else if(!player.getStackInHand(hand).isEmpty() && player.getStackInHand(hand).isFood() && player.getStackInHand(hand).getItem() != BlocksRegistry.SANDWICH.asItem()) {
                if (Sandwichable.BREADS.contains(((SandwichTableBlockEntity)blockEntity).getFoodList().get(0).getItem()) || Sandwichable.BREADS.contains(player.getStackInHand(hand).getItem())) {
                    ItemStack foodToBeAdded = player.getStackInHand(hand);
                    ((SandwichTableBlockEntity) blockEntity).addFood(player, foodToBeAdded);
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
                    SandwichTableBlockEntity sBlockEntity = (SandwichTableBlockEntity)world.getBlockEntity(pos);
                    ItemStack item = new ItemStack(BlocksRegistry.SANDWICH);
                    CompoundTag tag = sBlockEntity.serializeSandwich(new CompoundTag());
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
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SandwichTableBlockEntity) {
                SandwichTableBlockEntity blockEntity = (SandwichTableBlockEntity)world.getBlockEntity(pos);
                for(int i=0;i<blockEntity.getFoodListSize();i++) {
                    ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, blockEntity.getFoodList().get(i).getItem() instanceof SpreadItem ? ItemStack.EMPTY : blockEntity.getFoodList().get(i));
                    world.spawnEntity(item);
                }
                world.updateNeighbors(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
