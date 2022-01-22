/*
 * MIT License
 *
 * Copyright (c) 2022 FoundationGames
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.foundationgames.sandwichable.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import me.lambdaurora.spruceui.Position;
import me.lambdaurora.spruceui.background.Background;
import me.lambdaurora.spruceui.background.DirtTexturedBackground;
import me.lambdaurora.spruceui.option.SpruceCyclingOption;
import me.lambdaurora.spruceui.option.SpruceDoubleInputOption;
import me.lambdaurora.spruceui.option.SpruceFloatInputOption;
import me.lambdaurora.spruceui.option.SpruceIntegerInputOption;
import me.lambdaurora.spruceui.option.SpruceOption;
import me.lambdaurora.spruceui.option.SpruceSeparatorOption;
import me.lambdaurora.spruceui.option.SpruceToggleBooleanOption;
import me.lambdaurora.spruceui.screen.SpruceScreen;
import me.lambdaurora.spruceui.util.RenderUtil;
import me.lambdaurora.spruceui.widget.SpruceButtonWidget;
import me.lambdaurora.spruceui.widget.SpruceLabelWidget;
import me.lambdaurora.spruceui.widget.SpruceWidget;
import me.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/*
 * You may copy this file into your own mod, as long as you
 * include the above license statement and abide by its terms.
 *
 * Code is heavily condensed to make copying easier
 */
/**
 * A single class config "library" that creates SpruceUI screens.
 *
 * @author FoundationGames
 */
public abstract class ConfigInABarrel {
    private static final Logger LOG = LogManager.getLogger("ConfigInABarrel (Included)");
    private static final Map<Class<?>, Config> CONFIGS = new HashMap<>();
    private static final Gson GSON = new Gson();
    private static Config load(Class<? extends ConfigInABarrel> cls, Config config) {
        if (!Files.exists(config.path)) { save(cls, config); } else {
            try (BufferedReader reader = Files.newBufferedReader(config.path)) {
                config.config = GSON.fromJson(reader, cls);
            } catch (IOException e) { LOG.error("Failed to load config \""+config.name+"\"", e); }
        }
        config.config.afterLoad();
        return config;
    }
    private static void save(Class<?> cls, Config config) {
        try (JsonWriter writer = GSON.newJsonWriter(Files.newBufferedWriter(config.path))) {
            writer.setIndent("    ");
            GSON.toJson(GSON.toJsonTree(config.config, cls), writer);
        } catch (IOException e) { LOG.error("Failed to save config \""+config.name+"\"", e); }
    }
    /**
     * Initializes and/or retrieves an instance of your config class.
     * @param cls a config class
     * @param cfg a method reference to the config class's constructor
     * @param <T> the type of the config class
     * @return the initialized instance of the config class
     */
    @SuppressWarnings("unchecked")
    public static <T extends ConfigInABarrel> T config(String name, Class<T> cls, Supplier<T> cfg) {
        if (!CONFIGS.containsKey(cls)) {
            T cfgObj = cfg.get();
            CONFIGS.put(cls, load(cls, new Config(name, cfgObj)));
            return cfgObj;
        }
        return (T) (CONFIGS.get(cls).config);
    }
    /**
     * Creates a SpruceUI configuration screen for your config.
     * @param cls    the config class
     * @param parent the parent screen to open when this screen is closed
     * @param <T>    the type of the config class
     */
    public static <T extends ConfigInABarrel> Screen screen(Class<T> cls, Screen parent) {
        return new ConfigScreen(parent, cls, CONFIGS.get(cls));
    }
    protected void afterLoad() {}
    protected Background background() { return DirtTexturedBackground.DARKENED; }
    private static class LabelOption extends SpruceOption {
        public LabelOption(String key) { super(key); }
        @Override
        public SpruceWidget createWidget(Position position, int width) {
            return new SpruceLabelWidget(position, new TranslatableText(key), width, w -> {}, false);
        }
    }
    private static class ConfigScreen extends SpruceScreen {
        private final Screen parent;
        private final Class<? extends ConfigInABarrel> cfgCls;
        private final Config config;
        private SpruceOptionListWidget optionsWidget;
        protected <T extends ConfigInABarrel> ConfigScreen(Screen parent, Class<T> cls, Config config) {
            super(new TranslatableText("cfgbarrel."+config.name+".screen"));
            this.cfgCls = cls; this.config = config;
            this.parent = parent;
        }
        private void addFields(String namespace, @Nullable List<String> parents, Class<?> cls, Object obj) {
            String pKey = "cfgbarrel."+namespace+".option";
            if (parents != null) { pKey += "."+parents.stream().reduce((a, b) -> a + "." + b).orElse("error"); }
            for (Field field : cls.getDeclaredFields()) {
                Class<?> fc = field.getType();
                String key = pKey+"."+field.getName();
                int min = 0; int max = 0;
                boolean gui = true;
                for (Annotation ann : field.getAnnotations())
                    { if (ann instanceof Value) { Value v = ((Value) ann); min = v.min(); max = v.max(); gui = v.gui(); } }
                if (!gui) { continue; }
                if (fc.equals(int.class)) {
                    final int fmn = min; final int fmx = max;
                    this.optionsWidget.addSingleOptionEntry(new SpruceIntegerInputOption(key,
                            () -> { try { return (Integer) field.get(obj); } catch (IllegalAccessException ignored) {} return 0; },
                            (i) -> { try { field.set(obj, fmn - fmx == 0 ? i : MathHelper.clamp(i, fmn, fmx)); } catch (IllegalAccessException ignored) {} }, null));
                } else if (fc.equals(float.class)) {
                    this.optionsWidget.addSingleOptionEntry(new SpruceFloatInputOption(key,
                            () -> { try { return (Float) field.get(obj); } catch (IllegalAccessException ignored) {} return 0f; },
                            (f) -> { try { field.set(obj, f); } catch (IllegalAccessException ignored) {} }, null));
                } else if (fc.equals(double.class)) {
                    this.optionsWidget.addSingleOptionEntry(new SpruceDoubleInputOption(key,
                            () -> { try { return (Double) field.get(obj); } catch (IllegalAccessException ignored) {} return 0d; },
                            (d) -> { try { field.set(obj, d); } catch (IllegalAccessException ignored) {} }, null));
                } else if (fc.equals(boolean.class)) {
                    this.optionsWidget.addSingleOptionEntry(new SpruceToggleBooleanOption(key,
                            () -> { try { return (Boolean) field.get(obj); } catch (IllegalAccessException ignored) {} return false; },
                            (b) -> { try { field.set(obj, b); } catch (IllegalAccessException ignored) {} }, null));
                } else if (fc.isEnum()) {
                    this.optionsWidget.addOptionEntry(new LabelOption(key), new SpruceCyclingOption(key,
                            (i) -> { try {
                                int s = fc.getEnumConstants().length;
                                field.set(obj, fc.getEnumConstants()[Math.floorMod(((Enum<?>) field.get(obj)).ordinal() + 1, s)]);
                            } catch (IllegalAccessException ignored) {} },
                            opt -> { try {
                                return new TranslatableText("cfgbarrel."+namespace+".enum." + ((Enum<?>) field.get(obj)).name());
                            } catch (IllegalAccessException ignored) {} return LiteralText.EMPTY; }, null));
                } else {
                    this.optionsWidget.addSingleOptionEntry(new SpruceSeparatorOption(key, true, null));
                    List<String> nParents = new ArrayList<>();
                    if (parents != null) { nParents.addAll(parents); }
                    nParents.add(field.getName());
                    try { this.addFields(namespace, nParents, fc, field.get(obj)); } catch (IllegalAccessException ignored) {}
                    this.optionsWidget.addSingleOptionEntry(new SpruceSeparatorOption("cfgbarrel.empty", false, null));
                }
            }
        }
        @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderUtil.renderBackgroundTexture(client, 0, 0, this.width, this.height, 0, 64, 64, 64, 255);
            super.render(matrices, mouseX, mouseY, delta);
            drawCenteredText(matrices, this.textRenderer, this.getTitle(), this.width / 2, 8, 0xFFFFFF);
        }
        @Override public void onClose() { this.client.openScreen(this.parent); }
        @Override protected void init() {
            super.init();
            this.optionsWidget = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - (35 + 22));
            this.optionsWidget.setBackground(this.config.config.background());
            this.addFields(this.config.name, null, this.cfgCls, this.config.config);
            this.addChild(optionsWidget);
            int bottomCenter = this.width / 2 - 65;
            this.addChild(new SpruceButtonWidget(Position.of(bottomCenter - 69, this.height - 27), 130, 20, ScreenTexts.CANCEL, button -> this.onClose()));
            this.addChild(new SpruceButtonWidget(Position.of(bottomCenter + 69, this.height - 27), 130, 20, ScreenTexts.DONE, button -> { save(cfgCls, config); onClose(); }));
        }
    }
    private static class Config {
        ConfigInABarrel config; final String name; final Path path;
        Config(String name, ConfigInABarrel config) {
            this.config = config; this.name = name;
            this.path = FabricLoader.getInstance().getConfigDir().toAbsolutePath().resolve(name + ".json");
        }
    }
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Value {
        int min() default 0;
        int max() default 0;
        boolean gui() default true;
    }
}
