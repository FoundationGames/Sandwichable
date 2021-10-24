package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.blocks.entity.BasinBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.BottleCrateBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.CuttingBoardBlockEntity;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CuttingBoardBlock extends ModelBlockWithEntity {
    public static final VoxelShape[] SHAPES;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public CuttingBoardBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING, POWERED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        switch(state.get(Properties.HORIZONTAL_FACING)) {
            case NORTH:
            case SOUTH:
                return Block.createCuboidShape(1.0D, 0.0D, 2.0D, 15.0D, 1.0D, 14.0D);
            case EAST:
            case WEST:
                return Block.createCuboidShape(2.0D, 0.0D, 1.0D, 14.0D, 1.0D, 15.0D);
            default:
                return VoxelShapes.fullCube();
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return this.getOutlineShape(state, view, pos, context);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof CuttingBoardBlockEntity) {
                CuttingBoardBlockEntity be = (CuttingBoardBlockEntity)world.getBlockEntity(pos);
                ItemScatterer.spawn(world, pos, new SimpleInventory(be.getItem(), be.getKnife()));
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.getBlockEntity(pos) instanceof CuttingBoardBlockEntity) {
            CuttingBoardBlockEntity blockEntity = (CuttingBoardBlockEntity) world.getBlockEntity(pos);
            ActionResult r = blockEntity.onUse(state, world, pos, player, hand, hit);
            blockEntity.update();
            return r;
        }
        return ActionResult.PASS;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof CuttingBoardBlockEntity) {
            CuttingBoardBlockEntity blockEntity = (CuttingBoardBlockEntity) world.getBlockEntity(pos);
            return blockEntity.getItem().getCount() / blockEntity.getItem().getMaxCount();
        }
        return super.getComparatorOutput(state, world, pos);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        boolean pwr = world.isReceivingRedstonePower(pos);
        if(pwr != state.get(POWERED)) {
            if(world.getBlockEntity(pos) instanceof CuttingBoardBlockEntity && pwr) {
                ((CuttingBoardBlockEntity) world.getBlockEntity(pos)).trySliceWithKnife();
            }
            if(!world.isClient()) world.setBlockState(pos, state.with(POWERED, pwr));
        }
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing());
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardBlockEntity(pos, state);
    }

    static {
        SHAPES = new VoxelShape[]{Block.createCuboidShape(1.0D, 0.0D, 2.0D, 15.0D, 1.0D, 14.0D), Block.createCuboidShape(2.0D, 0.0D, 1.0D, 14.0D, 1.0D, 15.0D)};
    }
}
