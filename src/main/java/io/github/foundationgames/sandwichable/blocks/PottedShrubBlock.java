package io.github.foundationgames.sandwichable.blocks;



import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PottedShrubBlock extends FlowerPotBlock {

    public static final BooleanProperty SNIPPED;

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return super.getOutlineShape(state, view, pos, context);
    }

    public PottedShrubBlock(Block content, Settings settings) {
        super(content, settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SNIPPED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if(item instanceof ShearsItem && !state.get(SNIPPED)) {
            itemStack.damage(1, player, (playerEntity) -> playerEntity.sendToolBreakStatus(hand));
            Util.scatterBlockDust(world, pos.add(0.5, 0.5, 0.5), BlocksRegistry.SHRUB, 1, 30);
            world.setBlockState(pos, world.getBlockState(pos).with(SNIPPED, true));
            return ActionResult.SUCCESS;
        }
        if(state.get(SNIPPED) && itemStack.isEmpty()) {
            world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 3);
            player.setStackInHand(hand, new ItemStack(Items.STICK, 2));
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SNIPPED);
    }

    static {
        SNIPPED = BlockProperties.SNIPPED;
    }
}
