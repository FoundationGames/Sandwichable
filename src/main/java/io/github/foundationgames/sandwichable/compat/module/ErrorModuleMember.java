package io.github.foundationgames.sandwichable.compat.module;

import io.github.foundationgames.sandwichable.Sandwichable;

public class ErrorModuleMember extends AbstractModuleMember {
    private final String error;

    public ErrorModuleMember(String error) {
        this.error = error;
    }

    @Override
    public void onInitialized() {
        Sandwichable.LOG.error(error);
    }
}
