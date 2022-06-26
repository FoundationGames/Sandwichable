package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.blocks.BucketFluidloggable;
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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class CustomBucketItem extends BucketItem {
    private final Fluid fluid;
    private final Predicate<World> evaporates;

    public static final Predicate<World> HOT_DIMENSION = world -> world.getDimension().ultrawarm();

    public CustomBucketItem(Fluid fluid, Settings settings, Predicate<World> evaporates) {
        super(fluid, settings);
        this.fluid = fluid;
        this.evaporates = evaporates;
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
                    if (state.getBlock() instanceof FluidDrainable fluidBlock) {
                        ItemStack bucket = fluidBlock.tryDrainFluid(world, pos, state);
                        if (!bucket.isEmpty()) {
                            user.incrementStat(Stats.USED.getOrCreateStat(this));
                            fluidBlock.getBucketFillSound().ifPresent(sound -> user.playSound(sound, 1.0F, 1.0F));
                            world.emitGameEvent(user, GameEvent.FLUID_PICKUP, pos);
                            ItemStack bucketResult = ItemUsage.exchangeStack(stack, user, bucket);
                            if (!world.isClient) {
                                Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)user, bucket);
                            }
                            return TypedActionResult.success(bucketResult, world.isClient());
                        }
                    }
                    return TypedActionResult.fail(stack);
                } else {
                    state = world.getBlockState(pos);
                    BlockPos npos = state.getBlock() instanceof BucketFluidloggable && ((BucketFluidloggable)state.getBlock()).isFillableWith(this.fluid) ? pos : placePos;
                    if (this.placeFluid(user, world, npos, hit)) {
                        this.onEmptied(user, world, stack, npos);
                        if (user instanceof ServerPlayerEntity) Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)user, npos, stack);
                        user.incrementStat(Stats.USED.getOrCreateStat(this));
                        return TypedActionResult.success(getEmptiedStack(stack, user), world.isClient());
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
            } else if (evaporates.test(world)) {
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
