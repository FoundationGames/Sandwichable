package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerItemCooldownManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class SandwichBlockItem extends InfoTooltipBlockItem {
    public SandwichBlockItem(Block block) {
        super(block, new Item.Settings().food(new FoodComponent.Builder().build()).maxCount(1));
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        if (context.getPlayer().isSneaking()) {
            return super.place(context, state);
        }
        return false;
    }

    public List<ItemStack> getFoodList(ItemStack stack) {
        CompoundTag tag = stack.getSubTag("BlockEntityTag");
        DefaultedList<ItemStack> foods = DefaultedList.ofSize(128, ItemStack.EMPTY);
        Inventories.fromTag(tag, foods);
        return foods;
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
                return ((MutableText)super.getName(stack)).formatted(Formatting.AQUA);
            } else if(size <= 2) {
                return new TranslatableText("block.sandwichable.zandwich");
            } else if (hacked) {
                return new TranslatableText("block.sandwichable.hackedsandwich");
            }
        }
        return super.getName(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        SandwichableConfig config = AutoConfig.getConfigHolder(SandwichableConfig.class).getConfig();
        return config.baseSandwichEatTime + (config.slowEatingLargeSandwiches ? getFoodListSize(stack) : 0);
    }

    public int getFoodListSize(ItemStack stack) {
        CompoundTag tag = stack.getSubTag("BlockEntityTag");
        DefaultedList<ItemStack> foods = DefaultedList.ofSize(128, ItemStack.EMPTY);
        Inventories.fromTag(tag, foods);
        int i=0;
        while(foods.get(i)!=ItemStack.EMPTY) {i++;}
        return i;
    }

    @Override
    public ItemStack finishUsing(ItemStack istack, World world, LivingEntity user) {
        ItemStack stack = istack.copy();
        if(stack.getTag() != null) {
            CompoundTag tag = stack.getSubTag("BlockEntityTag");
            DefaultedList<ItemStack> foods = DefaultedList.ofSize(128, ItemStack.EMPTY);
            Inventories.fromTag(tag, foods);
            ItemStack food;
            ItemStack finishStack;
            ItemCooldownManager cooldownManager;
            for (int i = 0; i < foods.size(); i++) {
                food = foods.get(i);
                if(food.isFood()) {
                    //System.out.println(food.getItem().finishUsing(stack, world, user));
                    finishStack = food.getItem().finishUsing(food, world, user);
                    if(user instanceof PlayerEntity) {
                        if(!((PlayerEntity)user).isCreative() && !finishStack.getItem().equals(Items.AIR)) {
                            ((PlayerEntity)user).giveItemStack(finishStack);
                        }
                        cooldownManager = ((PlayerEntity)user).getItemCooldownManager();
                        if(cooldownManager.isCoolingDown(food.getItem())) {
                            cooldownManager.set(this, 20);
                        }
                    }
                    user.eatFood(world, food);
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
                    tooltip.add(((MutableText)foods.get(i).getName()).formatted(Formatting.BLUE));
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
        super.appendTooltip(stack, world, tooltip, context);
    }
}
