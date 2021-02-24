package io.github.foundationgames.sandwichable.block;

import io.github.foundationgames.sandwichable.block.entity.*;
import io.github.foundationgames.sandwichable.item.PickleJarBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PickleJarBlock extends Block implements BlockEntityProvider {
    public static final VoxelShape SHAPE;

    public PickleJarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.getBlockEntity(pos) instanceof PickleJarBlockEntity) {
            return ((PickleJarBlockEntity)world.getBlockEntity(pos)).onUse(world, player, hand, pos);
        }
        return ActionResult.FAIL;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if(world.getBlockEntity(pos) instanceof PickleJarBlockEntity) {
            return PickleJarBlockItem.createFromBlockEntity((PickleJarBlockEntity)world.getBlockEntity(pos));
        }
        return super.getPickStack(world, pos, state);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof PickleJarBlockEntity && !player.isCreative()) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, PickleJarBlockItem.createFromBlockEntity((PickleJarBlockEntity)world.getBlockEntity(pos)));
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof PickleJarBlockEntity) {
            return ((PickleJarBlockEntity)world.getBlockEntity(pos)).areItemsPickled() ? 15 : 0;
        }
        return super.getComparatorOutput(state, world, pos);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new PickleJarBlockEntity();
    }

    static {
        SHAPE = VoxelShapes.union(
            Block.createCuboidShape(2, 0, 2, 14, 13, 14),
            Block.createCuboidShape(3, 13, 3, 13, 16, 13)
        );
    }
}
