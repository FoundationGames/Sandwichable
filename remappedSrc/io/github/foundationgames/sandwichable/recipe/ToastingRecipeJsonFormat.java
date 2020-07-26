package io.github.foundationgames.sandwichable.recipe;

import com.google.gson.JsonObject;

public class ToastingRecipeJsonFormat {
    JsonObject input;
    String output;

    public JsonObject getInput() {
        return input;
    }
    public void setInput(JsonObject input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
