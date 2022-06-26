package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.blocks.entity.BasinBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.BasinContentType;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BasinBlock extends ModelBlockWithEntity {
    public static final VoxelShape SHAPE;

    public BasinBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BasinBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlocksRegistry.BASIN_BLOCKENTITY, BasinBlockEntity::tick);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return this.getOutlineShape(state, view, pos, ctx);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if(world.getBlockEntity(pos) instanceof BasinBlockEntity) {
            BasinBlockEntity be = (BasinBlockEntity)world.getBlockEntity(pos);
            if(be.getContent().getContentType() == BasinContentType.CHEESE) be.createCheeseParticle(world, pos, random, random.nextInt(2) + 1, be.getContent().getCheeseType().getParticleColorRGB());
        }
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof BasinBlockEntity) {
            return ((BasinBlockEntity)world.getBlockEntity(pos)).getContent().getContentType() == BasinContentType.CHEESE ? 15 : 0;
        }
        return super.getComparatorOutput(state, world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && world.getBlockEntity(pos)!=null) {
            if(world.getBlockEntity(pos) instanceof BasinBlockEntity) {
                BasinBlockEntity blockEntity = (BasinBlockEntity)world.getBlockEntity(pos);
                if(blockEntity.getContent().getContentType() == BasinContentType.CHEESE) {
                    ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(BasinBlockEntity.cheeseTypeToItem().get(blockEntity.getContent().getCheeseType())));
                    world.spawnEntity(item);
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.getBlockEntity(pos) instanceof BasinBlockEntity) {
            Util.sync((BasinBlockEntity)world.getBlockEntity(pos));
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
