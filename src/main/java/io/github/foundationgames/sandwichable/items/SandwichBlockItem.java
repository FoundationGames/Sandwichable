package io.github.foundationgames.sandwichable.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class SandwichBlockItem extends BlockItem {
    public SandwichBlockItem(Block block) {
        super(block, new Item.Settings().food(new FoodComponent.Builder().build()));
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        if (context.getPlayer().isSneaking()) {
            return super.place(context, state);
        }
        return false;
    }

    @Override
    public Text getName(ItemStack stack) {
        if(stack.getTag() != null) {
            CompoundTag tag = stack.getSubTag("BlockEntityTag");
            DefaultedList<ItemStack> list = DefaultedList.ofSize(128, ItemStack.EMPTY);
            Inventories.fromTag(tag, list);
            int size = 0;
            boolean hacked = false;
            while(list.get(size)!=ItemStack.EMPTY) { size++; if(!list.get(size).isFood() && !list.get(size).isEmpty()) { hacked = true; } }
            if(size <= 2 && hacked) {
                return new TranslatableText("block.sandwichable.hackedzandwich");
            } else if(size >= 127 && hacked) {
                return new TranslatableText("block.sandwichable.hackedsandwich").formatted(Formatting.AQUA);
            } else if(size >= 127) {
                return super.getName(stack).formatted(Formatting.AQUA);
            } else if(size <= 2) {
                return new TranslatableText("block.sandwichable.zandwich");
            } else if (hacked) {
                return new TranslatableText("block.sandwichable.hackedsandwich");
            }
        }
        return super.getName(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(stack.getTag() != null) {
            CompoundTag tag = stack.getSubTag("BlockEntityTag");
            DefaultedList<ItemStack> foods = DefaultedList.ofSize(128, ItemStack.EMPTY);
            Inventories.fromTag(tag, foods);
            for (int i = 0; i < foods.size(); i++) {
                ItemStack food = foods.get(i);
                if(food.isFood()) {
                    user.eatFood(world, food);
                    if(user instanceof PlayerEntity) {
                        if(!((PlayerEntity)user).isCreative()) {
                            ((PlayerEntity)user).giveItemStack(food.getItem().finishUsing(stack, world, user));
                        } else {
                            food.getItem().finishUsing(stack, world, user);
                        }
                    } else {
                        food.getItem().finishUsing(stack, world, user);
                    }
                }
            }
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if(stack.getTag() != null) {
            DefaultedList<ItemStack> foods = DefaultedList.ofSize(128, ItemStack.EMPTY);
            Inventories.fromTag(Objects.requireNonNull(stack.getSubTag("BlockEntityTag")), foods);
            int a = 0; while(foods.get(a) != ItemStack.EMPTY) { a++; }
            int i = 0; while(i < a && i < 5) {
                if(i != 4) {
                    tooltip.add(foods.get(i).getName().formatted(Formatting.BLUE));
                } else {
                    tooltip.add(new TranslatableText("sandwich.tooltip.ellipsis").formatted(Formatting.BLUE));
                }
                i++;
            }
            int size = 0;
            boolean hacked = false;
            while(foods.get(size)!=ItemStack.EMPTY) {
                size++; if (!foods.get(size).isFood() && !foods.get(size).isEmpty()) {
                    hacked = true;
                }
            }
            if(size <= 2) {
                tooltip.add(new TranslatableText("sandwich.tooltip.zandwich").formatted(Formatting.BLUE));
            }
            if(size >= 127) {
                tooltip.add(new TranslatableText("sandwich.tooltip.bigsandwich").formatted(Formatting.BLUE));
            }
            if(hacked) {
                tooltip.add(new TranslatableText("sandwich.tooltip.hacked").formatted(Formatting.DARK_PURPLE));
            }
        }
    }
}
