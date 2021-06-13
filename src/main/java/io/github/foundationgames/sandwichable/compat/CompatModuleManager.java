package io.github.foundationgames.sandwichable.compat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.compat.module.*;
import io.github.foundationgames.sandwichable.util.SandwichableGroupIconBuilder;
import io.github.foundationgames.sandwichable.util.Util;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompatModuleManager {
    public static final Map<String, Function<JsonObject, AbstractModuleMember>> REGISTRY = new HashMap<>();

    public static final RuntimeResourcePack ASSETS = RuntimeResourcePack.create("sandwichable:compat_assets");
    public static final RuntimeResourcePack DATA = RuntimeResourcePack.create("sandwichable:compat_data");
    public static ItemGroup SANDWICHABLE_COMPAT;
    public static CompatModule[] MODULES = new CompatModule[0];

    public static void init() throws IOException {
        registerDefaults();

        boolean isCompat = read();
        if(isCompat) SANDWICHABLE_COMPAT = FabricItemGroupBuilder.build(Util.id("sandwichable_extras"), SandwichableGroupIconBuilder::getCompatIcon);
        for(CompatModule module : MODULES) {
            module.initialize();
        }
    }

    public static void registerDefaults() {
        REGISTRY.put("basin", object -> {
            String blockName;
            Identifier parentBlock;
            String textureId;
            String ingredient;
            int outputCount = 1;
            if(object.get("block_name") != null) blockName = object.get("block_name").getAsString();
            else return new ErrorModuleMember("Missing option 'block_name' for 'basin' member");
            if(object.get("parent_block") != null) parentBlock = Identifier.tryParse(object.get("parent_block").getAsString());
            else return new ErrorModuleMember("Missing option 'parent_block' for 'basin' member");
            if(parentBlock == null) return new ErrorModuleMember("Option 'parent_block' was an invalid identifier for 'basin' member");
            if(object.get("texture") != null) textureId = object.get("texture").getAsString();
            else return new ErrorModuleMember("Missing option 'texture' for 'basin' member");
            if(object.get("recipe_ingredient") != null) ingredient = object.get("recipe_ingredient").getAsString();
            else return new ErrorModuleMember("Missing option 'recipe_ingredient' for 'basin' member");
            if(object.get("recipe_output_amount") != null) outputCount = object.get("recipe_output_amount").getAsInt();
            return new BasinModuleMember(blockName, parentBlock, textureId, ingredient, outputCount);
        });
        REGISTRY.put("cutting_board", object -> {
            String blockName;
            Identifier parentBlock;
            String textureId;
            if(object.get("block_name") != null) blockName = object.get("block_name").getAsString();
            else return new ErrorModuleMember("Missing option 'block_name' for 'cutting_board' member");
            if(object.get("parent_block") != null) parentBlock = Identifier.tryParse(object.get("parent_block").getAsString());
            else return new ErrorModuleMember("Missing option 'parent_block' for 'cutting_board' member");
            if(parentBlock == null) return new ErrorModuleMember("Option 'parent_block' was an invalid identifier for 'cutting_board' member");
            if(object.get("texture") != null) textureId = object.get("texture").getAsString();
            else return new ErrorModuleMember("Missing option 'texture' for 'cutting_board' member");
            return new CuttingBoardModuleMember(blockName, parentBlock, textureId);
        });
    }

    public static boolean read() throws IOException {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("sandwichable_compat");
        if(!Files.exists(path)) return false;
        List<CompatModule> modules = new ArrayList<>();
        boolean r = false;
        for(Path p : Files.walk(path, 1).collect(Collectors.toList())) {
            if(p.toString().endsWith(".json")) {
                r = true;
                InputStream stream = Files.newInputStream(p);
                JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(stream));
                if(parser.hasNext()) {
                    JsonElement element = parser.next();
                    if(element.isJsonObject()) {
                        Sandwichable.LOG.info("Loading compatibility module '{}'", p.getFileName().toString());
                        List<AbstractModuleMember> members = new ArrayList<>();
                        JsonObject object = element.getAsJsonObject();
                        for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
                            JsonElement v = entry.getValue();
                            String k = entry.getKey();
                            if(k.equals("lang") && v.isJsonObject()) {
                                for(Map.Entry<String, JsonElement> lEntry : v.getAsJsonObject().entrySet()) {
                                    if(lEntry.getValue().isJsonObject()) {
                                        ASSETS.addLang(Util.id(lEntry.getKey()), LangIntegration.from(lEntry.getValue().getAsJsonObject()));
                                    }
                                }
                            } else if(k.equals("members") && v.isJsonArray()) {
                                for(JsonElement el : v.getAsJsonArray()) {
                                    if(el.isJsonObject()) {
                                        JsonElement tElem = el.getAsJsonObject().get("type");
                                        if(tElem != null && tElem.getAsJsonPrimitive().isString()) {
                                            String type = tElem.getAsString();
                                            if(REGISTRY.containsKey(type)) {
                                                JsonElement oElem = el.getAsJsonObject().get("options");
                                                if(oElem != null && oElem.isJsonObject()) {
                                                    members.add(REGISTRY.get(type).apply(oElem.getAsJsonObject()));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        char[] fileName = p.getFileName().toString().toCharArray();
                        String modId = String.copyValueOf(Arrays.copyOfRange(fileName, 0, fileName.length - 5));
                        modules.add(new CompatModule(modId, members));
                    }
                }
            }
        }
        if(r) MODULES = modules.toArray(new CompatModule[]{});
        return r;
    }
}
