package io.github.foundationgames.sandwichable.villager;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DefaultedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SandwichGenerator {
    //Value, Index, Object
    private Table<Integer, Integer, Item> ingredientTable = HashBasedTable.create();
    private final Random random;

    SandwichGenerator(long seed) {
        this.random = new Random(seed);
    }
    SandwichGenerator() {
        this.random = new Random();
    }

    public void addIngredient(int value, Item item) {
        int i=0; while(ingredientTable.contains(value, i)) {
            i++;
        }
        ingredientTable.put(value, i, item);
    }
    public void addIngredients(int value, Item... items) {
        for (int i = 0; i < items.length; i++) {
            int j=0; while(ingredientTable.contains(value, j)) {
                j++;
            }
            ingredientTable.put(value, i, items[i]);
        }
    }

    private Map<Integer, Item> findClosestAvailableRow(int row, int maxDistance) {
        int offset = 0;
        int x = 0;
        while(x < maxDistance) {
            if(ingredientTable.containsRow(row-offset)) {
                return ingredientTable.row(row-offset);
            } else if(ingredientTable.containsRow(row+offset)) {
                return ingredientTable.row(row+offset);
            }
        }
        return null;
    }
    public int getIngredientCountForValue(int value) {
        int i=0; while(ingredientTable.contains(value, i)) {
            i++;
        }
        return i;
    }

    public DefaultedList<ItemStack> generateSandwichItems(Item breadItem, int totalValue, int size) {
        int ingredientValue = totalValue/20;
        int valueRoom = totalValue/10;
        List<Item> usedItems = new ArrayList<>();
        Map<Integer, Item> entries;
        DefaultedList<ItemStack> returnList = DefaultedList.ofSize(128, ItemStack.EMPTY);
        returnList.set(0, new ItemStack(breadItem, 1));
        Item item = Items.AIR;
        int exhaustion = 0;
        int s = 0;
        int offset = 0;
        while(s < size) {
            entries = this.findClosestAvailableRow(ingredientValue+offset, valueRoom);
            item = entries.get(random.nextInt(getIngredientCountForValue(ingredientValue)));
            if(!usedItems.contains(item)) {
                usedItems.add(item);
                int p = 0;
                while(returnList.get(p) != ItemStack.EMPTY) { p++; }
                returnList.set(p, new ItemStack(item, 1));
                s++;
            } else {
                if(exhaustion > 5) {
                    offset++;
                    exhaustion = 0;
                } else {
                    exhaustion++;
                }
            }
        }
        int p = 0;
        while(returnList.get(p) != ItemStack.EMPTY) { p++; }
        returnList.set(p, new ItemStack(breadItem, 1));
        return returnList;
    }
}
