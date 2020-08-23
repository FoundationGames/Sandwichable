package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.blocks.entity.BasinBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.CuttingBoardBlockEntity;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Optional;

public class CuttingBoardBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final VoxelShape[] SHAPES;

    public CuttingBoardBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        switch(state.get(FACING)) {
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
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof CuttingBoardBlockEntity) {
            CuttingBoardBlockEntity blockEntity = (CuttingBoardBlockEntity)world.getBlockEntity(pos);
            ItemEntity item = new ItemEntity((World)world, pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, blockEntity.getItem());
            world.spawnEntity(item);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if(world.getBlockEntity(pos) instanceof CuttingBoardBlockEntity && !stack.getItem().equals(BlocksRegistry.SANDWICH.asItem())) {
            CuttingBoardBlockEntity blockEntity = (CuttingBoardBlockEntity) world.getBlockEntity(pos);
            if(stack.getItem() == ItemsRegistry.KITCHEN_KNIFE || player.getStackInHand(Hand.OFF_HAND).getItem() == ItemsRegistry.KITCHEN_KNIFE && blockEntity.getItem() != ItemStack.EMPTY) {
                SimpleInventory inv = new SimpleInventory(blockEntity.getItem());
                Optional<CuttingRecipe> match = world.getRecipeManager().getFirstMatch(CuttingRecipe.Type.INSTANCE, inv, world);

                if (match.isPresent()) {
                    ItemStack result = match.get().getOutput().copy();
                    ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, result);
                    world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, blockEntity.getItem()), pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, 0.0D, 0.0D, 0.0D);
                    blockEntity.setItem(ItemStack.EMPTY);
                    world.playSound(player, pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 0.7f, 0.8f);
                    world.spawnEntity(item);
                }
                if(player.getStackInHand(Hand.OFF_HAND).getItem() == ItemsRegistry.KITCHEN_KNIFE) { player.swingHand(Hand.OFF_HAND); return ActionResult.CONSUME; }
            } else if(blockEntity.getItem() == ItemStack.EMPTY && !stack.isEmpty()) {
                ItemStack stack1 = player.getStackInHand(hand).copy();
                stack1.setCount(1);
                blockEntity.setItem(stack1);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            } else if (blockEntity.getItem() != ItemStack.EMPTY){
                ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, blockEntity.getItem());
                blockEntity.setItem(ItemStack.EMPTY);
                if(!player.isCreative()) {
                    item.setToDefaultPickupDelay();
                    world.spawnEntity(item);
                }
            }
            blockEntity.markDirty();
            Util.sync(blockEntity, world);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if(world.getBlockEntity(pos) instanceof CuttingBoardBlockEntity) {
            CuttingBoardBlockEntity blockEntity = (CuttingBoardBlockEntity)world.getBlockEntity(pos);
            if(blockEntity.getItem() != ItemStack.EMPTY) {
                return blockEntity.getItem();
            }
        }
        return super.getPickStack(world, pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CuttingBoardBlockEntity) {
                CuttingBoardBlockEntity blockEntity = (CuttingBoardBlockEntity)world.getBlockEntity(pos);
                ItemEntity item = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, blockEntity.getItem());
                world.spawnEntity(item);
                world.updateNeighbors(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    static {
        SHAPES = new VoxelShape[]{Block.createCuboidShape(1.0D, 0.0D, 2.0D, 15.0D, 1.0D, 14.0D), Block.createCuboidShape(2.0D, 0.0D, 1.0D, 14.0D, 1.0D, 15.0D)};
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new CuttingBoardBlockEntity();
    }
}
