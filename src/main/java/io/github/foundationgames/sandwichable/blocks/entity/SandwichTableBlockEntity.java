package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;

import java.util.ArrayList;
import java.util.List;

public class SandwichTableBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private DefaultedList<ItemStack> foods = DefaultedList.ofSize(128, ItemStack.EMPTY);

    public SandwichTableBlockEntity() {
        super(BlocksRegistry.SANDWICHTABLE_BLOCKENTITY);
    }

    public void addFood(ItemStack item) {
        int i=0;
        while(this.foods.get(i)!=ItemStack.EMPTY && i < this.foods.size()-1) {i++;}
        if (i < this.foods.size()-1) {
            this.foods.set(i, item);
        }
    }

    public ItemStack removeTopFood() {
        ItemStack r;
        int i=0;
        while(this.foods.get(i)!=ItemStack.EMPTY) {i++;}
        r = this.foods.get(i-1);
        this.foods.set(i-1, ItemStack.EMPTY);
        return r;
    }

    public ItemStack getTopFood() {
        ItemStack r;
        int i=0;
        while(this.foods.get(i)!=ItemStack.EMPTY) {i++;}
        r = this.foods.get(i-1);
        return r;
    }

    public DefaultedList<ItemStack> getFoodList() {
        return this.foods;
    }

    public void setFoodList(DefaultedList<ItemStack> list) {
        this.foods = list;
    }

    public int getFoodListSize() {
        int i=0;
        while(this.foods.get(i)!=ItemStack.EMPTY) {i++;}
        return i;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        DefaultedList<ItemStack> list = DefaultedList.ofSize(128, ItemStack.EMPTY);
        Inventories.fromTag(tag, list);
        setFoodList(list);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.foods);
        return tag;
    }

    public CompoundTag serializeSandwich(CompoundTag tag) {
        Inventories.toTag(tag, this.foods);
        return tag;
    }

    public void deserializeSandwich(CompoundTag tag) {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(128, ItemStack.EMPTY);
        Inventories.fromTag(tag, list);
        setFoodList(list);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }


}
