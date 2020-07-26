package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.blocks.entity.BasinBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.BasinContentType;
import io.github.foundationgames.sandwichable.blocks.entity.PickleJarBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.SandwichBlockEntity;
import io.github.foundationgames.sandwichable.items.PickleJarBlockItem;
import io.github.foundationgames.sandwichable.util.CheeseRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
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
