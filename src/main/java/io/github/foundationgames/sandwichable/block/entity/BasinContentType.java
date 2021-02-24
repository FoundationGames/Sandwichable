package io.github.foundationgames.sandwichable.block.entity;

public enum BasinContentType {
    EMPTY(false), MILK(true), FERMENTING_MILK(true), CHEESE(false);

    public final boolean isLiquid;

    BasinContentType(boolean isLiquid) {
        this.isLiquid = isLiquid;
    }
}
