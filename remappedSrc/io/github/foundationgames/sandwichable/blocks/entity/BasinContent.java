package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.items.CheeseType;
import io.github.foundationgames.sandwichable.util.CheeseRegistry;

public enum BasinContent {

    AIR("air", BasinContentType.EMPTY, CheeseType.NONE),
    MILK("milk", BasinContentType.MILK, CheeseType.NONE),
    FERMENTING_MILK_REGULAR("fermenting_milk_regular", BasinContentType.FERMENTING_MILK, CheeseType.REGULAR),
    CHEESE_REGULAR("cheese_regular", BasinContentType.CHEESE, CheeseType.REGULAR),
    FERMENTING_MILK_CREAMY("fermenting_milk_creamy", BasinContentType.FERMENTING_MILK, CheeseType.CREAMY),
    CHEESE_CREAMY("cheese_creamy", BasinContentType.CHEESE, CheeseType.CREAMY),
    FERMENTING_MILK_INTOXICATING("fermenting_milk_intoxicating", BasinContentType.FERMENTING_MILK, CheeseType.INTOXICATING),
    CHEESE_INTOXICATING("cheese_intoxicating", BasinContentType.CHEESE, CheeseType.INTOXICATING),
    FERMENTING_MILK_SOUR("fermenting_milk_sour", BasinContentType.FERMENTING_MILK, CheeseType.SOUR),
    CHEESE_SOUR("cheese_sour", BasinContentType.CHEESE, CheeseType.SOUR);

    private CheeseType cheeseType;
    private String name;
    private BasinContentType contentType;

    BasinContent(String name, BasinContentType contentType, CheeseType cheeseType) {
        this.cheeseType = cheeseType;
        this.name = name;
        this.contentType = contentType;
        CheeseRegistry.INSTANCE.register(this);
    }

    public BasinContentType getContentType() {
        return this.contentType;
    }
    public CheeseType getCheeseType() {
        return this.cheeseType;
    }
    @Override
    public String toString() {
        return name;
    }
}
