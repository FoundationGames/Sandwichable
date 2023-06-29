package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.blocks.entity.DesalinatorBlockEntity;
import io.github.foundationgames.sandwichable.fluids.FluidsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class DesalinatorBlock extends ModelBlockWithEntity implements Waterloggable, BucketFluidloggable {

    public static final VoxelShape SHAPE;
    public static final BooleanProperty ON;
    public static final EnumProperty<FluidType> FLUID = BlockProperties.FLUID;

    public DesalinatorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ON, false).with(FLUID, FluidType.NONE));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DesalinatorBlockEntity) {
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DesalinatorBlockEntity) {
                ItemScatterer.spawn(world, pos, ((DesalinatorBlockEntity)blockEntity));
                world.updateNeighbors(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return state.get(FLUID) == FluidType.NONE;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.get(FLUID) == FluidType.NONE && (fluidState.getFluid() == Fluids.WATER || fluidState.getFluid() == FluidsRegistry.PICKLE_BRINE)) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(FLUID, fluidState.getFluid() == Fluids.WATER ? FluidType.WATER : FluidType.PICKLE_BRINE), 3);
                world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        if (state.get(FLUID) == FluidType.WATER) {
            world.setBlockState(pos, state.with(FLUID, FluidType.NONE), 3);
            return new ItemStack(Items.WATER_BUCKET);
        } else {
            return ItemStack.EMPTY;
        }
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
    public FluidState getFluidState(BlockState state) {
        return state.get(FLUID).state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(ON, FLUID);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DesalinatorBlockEntity) {
                ((DesalinatorBlockEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DesalinatorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlocksRegistry.DESALINATOR_BLOCKENTITY, DesalinatorBlockEntity::tick);
    }

    static {
        SHAPE = createCuboidShape(1, 0, 1, 15, 16, 15);
        ON = BlockProperties.ON;
    }

    @Override
    public boolean isFillableWith(Fluid fluid) {
        return FluidsRegistry.PICKLE_BRINE.matchesType(fluid);
    }

    public enum FluidType implements StringIdentifiable {
        NONE("none", Fluids.EMPTY.getDefaultState()), WATER("water", Fluids.WATER.getDefaultState()), PICKLE_BRINE("brine", FluidsRegistry.PICKLE_BRINE.getDefaultState());
        private final String name;
        public final FluidState state;

        FluidType(String name, FluidState state) {
            this.name = name;
            this.state = state;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
