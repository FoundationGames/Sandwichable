package io.github.foundationgames.sandwichable.recipe;

import com.google.gson.JsonObject;

public class CuttingRecipeJsonFormat {

    JsonObject input;
    String outputItem;
    int outputCount;

    public void setInput(JsonObject inputObj) {
        input = inputObj;
    }

    public JsonObject getInput() {
        return input;
    }

    public void setOutput(String itemId, int count) {
        outputItem = itemId;
        outputCount = count;
    }

    public String getOutputItemId() {
        return outputItem;
    }
    public int getOutputCount() {
        return outputCount;
    }
}
