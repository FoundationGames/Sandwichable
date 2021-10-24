package io.github.foundationgames.sandwichable.items;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CustomMinecartItem extends InfoTooltipItem {
    private static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
        private final ItemDispenserBehavior defaultBehavior = new ItemDispenserBehavior();

        public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            Direction dir = pointer.getBlockState().get(DispenserBlock.FACING);
            World world = pointer.getWorld();
            double x = pointer.getX() + (double)dir.getOffsetX() * 1.125D;
            double y = Math.floor(pointer.getY()) + (double)dir.getOffsetY();
            double z = pointer.getZ() + (double)dir.getOffsetZ() * 1.125D;
            BlockPos pos = pointer.getPos().offset(dir);
            BlockState state = world.getBlockState(pos);
            RailShape shape = state.getBlock() instanceof AbstractRailBlock ? state.get(((AbstractRailBlock)state.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double offset;
            if (state.isIn(BlockTags.RAILS)) {
                if (shape.isAscending()) {
                    offset = 0.6D;
                } else {
                    offset = 0.1D;
                }
            } else {
                if (!state.isAir() || !world.getBlockState(pos.down()).isIn(BlockTags.RAILS)) {
                    return this.defaultBehavior.dispense(pointer, stack);
                }

                BlockState downState = world.getBlockState(pos.down());
                RailShape downShape = downState.getBlock() instanceof AbstractRailBlock ? downState.get(((AbstractRailBlock)downState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                if (dir != Direction.DOWN && downShape.isAscending()) {
                    offset = -0.4D;
                } else {
                    offset = -0.9D;
                }
            }
            AbstractMinecartEntity minecart = ((CustomMinecartItem)stack.getItem()).minecartType.create(world);
            minecart.setPos(x, y + offset, z);
            if (stack.hasCustomName()) {
                minecart.setCustomName(stack.getName());
            }
            world.spawnEntity(minecart);
            stack.decrement(1);
            return stack;
        }
        protected void playSound(BlockPointer pointer) {
            pointer.getWorld().syncWorldEvent(1000, pointer.getPos(), 0);
        }
    };

    private final EntityType<? extends AbstractMinecartEntity> minecartType;

    public CustomMinecartItem(EntityType<? extends AbstractMinecartEntity> type, Settings settings) {
        super(settings);
        minecartType = type;
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (!state.isIn(BlockTags.RAILS)) {
            return ActionResult.FAIL;
        } else {
            ItemStack stack = context.getStack();
            if (!world.isClient) {
                RailShape shape = state.getBlock() instanceof AbstractRailBlock ? state.get(((AbstractRailBlock)state.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                double offset = 0.0D;
                if (shape.isAscending()) offset = 0.5D;
                AbstractMinecartEntity minecart = minecartType.create(world);
                minecart.setPos(pos.getX() + 0.5, pos.getY() + 0.0625 + offset, pos.getZ() + 0.5);
                if (stack.hasCustomName()) {
                    minecart.setCustomName(stack.getName());
                }
                world.spawnEntity(minecart);
            }

            stack.decrement(1);
            return ActionResult.success(world.isClient);
        }
    }
}
