package io.github.foundationgames.sandwichable.compat.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.devtech.arrp.json.lang.JLang;
import net.minecraft.client.resource.language.I18n;

import java.util.Map;

public class LangIntegration {
    public static JLang from(JsonObject object) {
        JLang lang = new JLang();
        for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
            JsonElement v = entry.getValue();
            if(v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
                lang.entry(entry.getKey(), v.getAsString());
            }
        }
        return lang;
    }
}
