package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.blocks.entity.BasinBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BasinBlock extends Block implements BlockEntityProvider {
    public static final VoxelShape SHAPE;

    public BasinBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new BasinBlockEntity();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext ctx) {
        return this.getOutlineShape(state, view, pos, ctx);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.getBlockEntity(pos) instanceof BasinBlockEntity) {
            return ((BasinBlockEntity)world.getBlockEntity(pos)).onBlockUse(player, hand);
        }
        return ActionResult.FAIL;
    }

    static {
        SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1, 0, 1, 15, 2, 15),
            Block.createCuboidShape(1, 2, 1, 15, 7, 3),
            Block.createCuboidShape(1, 2, 13, 15, 7, 15),
            Block.createCuboidShape(1, 2, 3, 3, 7, 13),
            Block.createCuboidShape(13, 2, 3, 15, 7, 13)
        );
    }
}
