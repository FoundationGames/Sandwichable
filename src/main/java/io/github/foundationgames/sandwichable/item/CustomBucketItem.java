package io.github.foundationgames.sandwichable.item;

import io.github.foundationgames.sandwichable.block.BucketFluidloggable;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomBucketItem extends BucketItem {
    // THIS IS A HOT MESS, THANKS MOJANG HARDCODING WATER

    private Fluid fluid;

    public CustomBucketItem(Fluid fluid, Settings settings) {
        super(fluid, settings);
        this.fluid = fluid;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Util.appendInfoTooltip(tooltip, this.getTranslationKey());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        BlockHitResult hit = raycast(world, user, this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
        if (hit.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(stack);
        } else if (hit.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(stack);
        } else {
            BlockPos pos = hit.getBlockPos();
            Direction side = hit.getSide();
            BlockPos placePos = pos.offset(side);
            if (world.canPlayerModifyAt(user, pos) && user.canPlaceOn(placePos, side, stack)) {
                BlockState state;
                if (this.fluid == Fluids.EMPTY) {
                    state = world.getBlockState(pos);
                    if (state.getBlock() instanceof FluidDrainable) {
                        Fluid fluid = ((FluidDrainable) state.getBlock()).tryDrainFluid(world, pos, state);
                        if (fluid != Fluids.EMPTY) {
                            user.incrementStat(Stats.USED.getOrCreateStat(this));
                            user.playSound(fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                            ItemStack used = ItemUsage.exchangeStack(stack, user, new ItemStack(fluid.getBucketItem()));
                            if (!world.isClient)
                                Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity) user, new ItemStack(fluid.getBucketItem()));
                            return TypedActionResult.success(used, world.isClient());
                        }
                    }
                    return TypedActionResult.fail(stack);
                } else {
                    state = world.getBlockState(pos);
                    BlockPos npos = state.getBlock() instanceof BucketFluidloggable && ((BucketFluidloggable)state.getBlock()).isFillableWith(this.fluid) ? pos : placePos;
                    if (this.placeFluid(user, world, npos, hit)) {
                        this.onEmptied(world, stack, npos);
                        if (user instanceof ServerPlayerEntity) Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)user, npos, stack);
                        user.incrementStat(Stats.USED.getOrCreateStat(this));
                        return TypedActionResult.success(this.getEmptiedStack(stack, user), world.isClient());
                    } else return TypedActionResult.fail(stack);
                }
            } else {
                return TypedActionResult.fail(stack);
            }
        }
    }

    @Override
    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult blockHitResult) {
        if (this.fluid instanceof FlowableFluid) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            Material material = state.getMaterial();
            boolean canPour = state.canBucketPlace(this.fluid);
            boolean canPlace = state.isAir() || canPour || block instanceof FluidFillable && ((FluidFillable)block).canFillWithFluid(world, pos, state, this.fluid);
            if (!canPlace) {
                return blockHitResult != null && this.placeFluid(player, world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), null);
            } else if (world.getDimension().isUltrawarm() && this.fluid.isIn(FluidTags.WATER)) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
                for(int l = 0; l < 8; ++l) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0D, 0.0D, 0.0D);
                }
                return true;
            } else if (block instanceof BucketFluidloggable && ((BucketFluidloggable)block).isFillableWith(this.fluid)) {
                ((FluidFillable)block).tryFillWithFluid(world, pos, state, ((FlowableFluid)this.fluid).getStill(false));
                this.playEmptyingSound(player, world, pos);
                return true;
            } else {
                if (!world.isClient && canPour && !material.isLiquid()) {
                    world.breakBlock(pos, true);
                }

                if (!world.setBlockState(pos, this.fluid.getDefaultState().getBlockState(), 11) && !state.getFluidState().isStill()) {
                    return false;
                } else {
                    this.playEmptyingSound(player, world, pos);
                    return true;
                }
            }
        } else {
            return false;
        }
    }
}
