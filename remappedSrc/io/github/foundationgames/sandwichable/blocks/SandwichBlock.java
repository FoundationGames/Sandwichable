package io.github.foundationgames.sandwichable.blocks;


import io.github.foundationgames.sandwichable.blocks.entity.SandwichBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SandwichBlock extends Block implements BlockEntityProvider {

    public SandwichBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if(world.getBlockState(pos.down()).getBlock().equals(BlocksRegistry.SANDWICH_TABLE)) {
            return false;
        }
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        if (view.getBlockEntity(pos) instanceof SandwichBlockEntity) {
            SandwichBlockEntity blockEntity = (SandwichBlockEntity)view.getBlockEntity(pos);
            double size = blockEntity.getFoodListSize() * 0.6D;
            return Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, size, 12.0D);
        }
        return Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return Block.createCuboidShape(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new SandwichBlockEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof SandwichBlockEntity && !player.isCreative()) {
            SandwichBlockEntity blockEntity = (SandwichBlockEntity)world.getBlockEntity(pos);
            ItemStack item = new ItemStack(BlocksRegistry.SANDWICH);
            CompoundTag tag = blockEntity.serializeSandwich(new CompoundTag());
            if(!tag.isEmpty()) {
                item.putSubTag("BlockEntityTag", tag);
            }
            ItemEntity itemEntity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, item);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof SandwichBlockEntity) {
            SandwichBlockEntity blockEntity = (SandwichBlockEntity)world.getBlockEntity(pos);
            ItemStack item = new ItemStack(BlocksRegistry.SANDWICH);
            CompoundTag tag = blockEntity.serializeSandwich(new CompoundTag());
            if(!tag.isEmpty()) {
                item.putSubTag("BlockEntityTag", tag);
            }
            return item;
        }
        return ItemStack.EMPTY;
    }
}
