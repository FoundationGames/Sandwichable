package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Random;

public class ShrubBlock extends PlantBlock {

    public static final BooleanProperty SNIPPED;

    public ShrubBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SNIPPED, false));
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos) {
        Block block = floor.getBlock();
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.FARMLAND || block == Blocks.SAND;
    }

    public static boolean canGenerateOn(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        Block block = world.getBlockState(blockPos).getBlock();
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if(world.getBlockState(pos.down()).getBlock().equals(Blocks.SAND)) {
            if (!state.get(SNIPPED)) {
                Util.scatterDroppedBlockDust(world.getWorld(), pos, this, 2, 30);
                world.setBlockState(pos, this.getDefaultState().with(SNIPPED, true));
            } else if (state.get(SNIPPED)) {
                world.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState());
            }
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return Blocks.DEAD_BUSH.getOutlineShape(state, view, pos, context);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if(stack.getItem() == Items.SHEARS && !state.get(SNIPPED)) {
            stack.damage(1, player, (playerEntity) -> playerEntity.sendToolBreakStatus(hand));
            Util.scatterBlockDust(world, pos, this, 2, 30);
            world.setBlockState(pos, world.getBlockState(pos).with(SNIPPED, true));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SNIPPED);
    }

    static {
        SNIPPED = BooleanProperty.of("snipped");
    }
}
