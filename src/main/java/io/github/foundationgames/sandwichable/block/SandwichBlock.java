package io.github.foundationgames.sandwichable.block;


import io.github.foundationgames.sandwichable.block.entity.SandwichBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
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
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if(!canPlaceAt(state, world, pos)) {
            world.breakBlock(pos, true);
            dropItem(world, pos);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        if (view.getBlockEntity(pos) instanceof SandwichBlockEntity) {
            SandwichBlockEntity blockEntity = (SandwichBlockEntity)view.getBlockEntity(pos);
            double size = 10;
            if(blockEntity != null) size = (blockEntity.getSandwich().getSize() * 0.5D);
            return Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, size, 12.0D);
        }
        return Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
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
        if(!player.isCreative()) dropItem(world, pos);
        super.onBreak(world, pos, state, player);
    }

    public void dropItem(World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof SandwichBlockEntity) {
            SandwichBlockEntity blockEntity = (SandwichBlockEntity)world.getBlockEntity(pos);
            blockEntity.getSandwich().ejectSandwich(world, new Vec3d(pos.getX()+0.5, pos.getY() - 0.7, pos.getZ() + 0.5));
        }
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof SandwichBlockEntity) {
            SandwichBlockEntity blockEntity = (SandwichBlockEntity)world.getBlockEntity(pos);
            ItemStack item = new ItemStack(BlocksRegistry.SANDWICH);
            NbtCompound tag = blockEntity.getSandwich().addToTag(new NbtCompound());
            if(!tag.isEmpty()) {
                item.putSubTag("BlockEntityTag", tag);
            }
            return item;
        }
        return ItemStack.EMPTY;
    }
}
