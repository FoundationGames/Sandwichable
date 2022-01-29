package io.github.foundationgames.sandwichable.util;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.entity.*;
import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import io.github.foundationgames.sandwichable.items.CheeseCultureItem;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtraDispenserBehaviorRegistry {
    public static final Map<ItemConvertible, List<DispenserBehavior>> ENTRIES = new HashMap<>();

    public static void register(ItemConvertible item, DispenserBehavior behavior) {
        if(!ENTRIES.containsKey(item)) ENTRIES.put(item, new ArrayList<>());
        ENTRIES.get(item).add(behavior);
    }

    public static void initDefaults() {
        DispenserBehavior foodBehavior = new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                BlockPos pos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                World world = pointer.getWorld();
                Sandwich sandwich = null;
                Runnable sync = () -> {};
                if(world.getBlockEntity(pos) instanceof SandwichTableBlockEntity) {
                    sandwich = ((SandwichTableBlockEntity)world.getBlockEntity(pos)).getSandwich();
                    sync = () -> Util.sync(((SandwichTableBlockEntity)world.getBlockEntity(pos)));
                } else {
                    List<SandwichTableMinecartEntity> list = pointer.getWorld().getEntitiesByClass(SandwichTableMinecartEntity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR);
                    if(list.size() > 0) {
                        sandwich = list.get(0).getSandwich();
                        sync = list.get(0)::sync;
                    }
                }
                if(sandwich != null) {
                    if(!sandwich.hasBreadBottom() && !Sandwichable.isBread(stack.getItem())) return null;
                    ItemStack r = sandwich.tryAddTopFoodFrom(world, stack);
                    if(r != null) {
                        sync.run();
                        if(!r.isEmpty() && pointer.getWorld().getBlockEntity(pointer.getPos()) instanceof DispenserBlockEntity) {
                            DispenserBlockEntity be = (DispenserBlockEntity)pointer.getWorld().getBlockEntity(pointer.getPos());
                            int a = be.addToFirstFreeSlot(r);
                            if(a < 0) return null;
                        }
                        return stack;
                    }
                }
                return null;
            }
        };
        for(ItemConvertible item : Registry.ITEM) {
            if((item.asItem().isFood() || SpreadRegistry.INSTANCE.itemHasSpread(item)) && item.asItem() != BlocksRegistry.SANDWICH.asItem()) {
                register(item, foodBehavior);
            }
            if(item instanceof CheeseCultureItem) {
                register(item, (pointer, stack) -> {
                    BlockPos pos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                    ServerWorld world = pointer.getWorld();
                    if(world.getBlockEntity(pos) instanceof BasinBlockEntity) {
                        BasinBlockEntity be = (BasinBlockEntity)world.getBlockEntity(pos);
                        if(be.getContent().getContentType() == BasinContentType.MILK) {
                            return be.addCheeseCulture(stack);
                        }
                    }
                    return null;
                });
            }
        }
        ItemDispenserBehavior milkBehavior = new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                BlockPos pos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                ServerWorld world = pointer.getWorld();
                if(world.getBlockEntity(pos) instanceof BasinBlockEntity) {
                    BasinBlockEntity be = (BasinBlockEntity)world.getBlockEntity(pos);
                    if(be.getContent() == BasinContent.AIR) {
                        return be.insertMilk(stack);
                    }
                }
                return null;
            }
        };
        register(Items.MILK_BUCKET, milkBehavior);
        register(ItemsRegistry.FERMENTING_MILK_BUCKET, milkBehavior);
        register(Items.BUCKET, (pointer, stack) -> {
            BlockPos pos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            ServerWorld world = pointer.getWorld();
            if(world.getBlockEntity(pos) instanceof BasinBlockEntity) {
                BasinBlockEntity be = (BasinBlockEntity)world.getBlockEntity(pos);
                if(be.getContent().getContentType().isLiquid) {
                    return be.extractMilk();
                }
            }
            return null;
        });
        register(Items.BUCKET, (pointer, stack) -> {
            BlockPos pos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            ServerWorld world = pointer.getWorld();
            if(world.getBlockEntity(pos) instanceof PickleJarBlockEntity) {
                PickleJarBlockEntity be = (PickleJarBlockEntity)world.getBlockEntity(pos);
                if(be.getFluid() == PickleJarFluid.WATER) {
                    be.emptyFluid(true);
                    return new ItemStack(Items.WATER_BUCKET);
                }
            }
            return null;
        });
        register(Items.WATER_BUCKET, (pointer, stack) -> {
            BlockPos pos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            ServerWorld world = pointer.getWorld();
            if(world.getBlockEntity(pos) instanceof PickleJarBlockEntity) {
                PickleJarBlockEntity be = (PickleJarBlockEntity)world.getBlockEntity(pos);
                if(be.getFluid() == PickleJarFluid.AIR) {
                    be.fillWater(true);
                    return new ItemStack(Items.BUCKET);
                }
            }
            return null;
        });
        register(ItemsRegistry.SALT, (pointer, stack) -> {
            BlockPos pos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            ServerWorld world = pointer.getWorld();
            if(world.getBlockEntity(pos) instanceof PickleJarBlockEntity) {
                PickleJarBlockEntity be = (PickleJarBlockEntity)world.getBlockEntity(pos);
                if(be.getFluid() == PickleJarFluid.WATER && be.getItemCount() > 0) {
                    be.startPickling();
                    stack.decrement(1);
                    be.update();
                    return stack;
                }
            }
            return null;
        });
    }
}
