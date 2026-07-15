package com.smoothlightning.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "smooth-lighted-entities.json");

    private static final OptionInstance.CaptionBasedToString<Double> PERCENT_TO_STRING =
            (caption, value) -> Component.translatable("options.generic_value", caption, Component.literal(Math.round(value * 100.0) + "%"));

    private static OptionInstance<Double> createRangeOption(String key, double defaultValue) {
        return new OptionInstance<Double>(key, OptionInstance.noTooltip(), PERCENT_TO_STRING,
                new OptionInstance.IntRange(0, 200).xmap(i -> (double)i / 100.0, d -> (int)(d * 100.0), true),
                Codec.doubleRange(0.0, 2.0), defaultValue, v -> save());
    }

    private static OptionInstance<Double> createDarknessOption(String key, double defaultValue) {
        return new OptionInstance<Double>(key, OptionInstance.noTooltip(), PERCENT_TO_STRING,
                OptionInstance.UnitDouble.INSTANCE, defaultValue, v -> save());
    }

    // Default Values for the General Smooth Lightning
    public static final OptionInstance<Double> GRADIENT_RANGE = createRangeOption("smooth-lighted-entities.options.gradient_range", 1.0);
    public static final OptionInstance<Double> BASE_DARKNESS = createDarknessOption("smooth-lighted-entities.options.base_darkness", 0.35);
    public static final OptionInstance<Double> SOLID_DARK_HEIGHT = createRangeOption("smooth-lighted-entities.options.solid_dark_height", 0.1);

    // Smooth Shading Per Entity
    public static Map<String, EntityOverride> PER_ENTITY_OVERRIDES = new HashMap<>();

    private static long lastModifiedTime = 0;
    private static int tickCounter = 0;

    // Config JSON In-Game File Loader
    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    if (data.gradient_range != null) GRADIENT_RANGE.set(data.gradient_range);
                    if (data.base_darkness != null) BASE_DARKNESS.set(data.base_darkness);
                    if (data.solid_dark_height != null) SOLID_DARK_HEIGHT.set(data.solid_dark_height);
                    if (data.per_entity_overrides != null) {
                        PER_ENTITY_OVERRIDES = data.per_entity_overrides;
                    } else {
                        PER_ENTITY_OVERRIDES = new HashMap<>();
                    }
                }
            } catch (IOException e) { e.printStackTrace(); }
        } else {
            PER_ENTITY_OVERRIDES = createDefaultOverrides();
            save();
        }
        lastModifiedTime = CONFIG_FILE.lastModified();
    }

    public static void checkAndReload() {
        tickCounter++;
        if (tickCounter < 20) return;
        tickCounter = 0;

        if (!CONFIG_FILE.exists()) {
            PER_ENTITY_OVERRIDES = createDefaultOverrides();
            save();
            return;
        }

        if (CONFIG_FILE.lastModified() > lastModifiedTime) {
            load();
        }
    }

    public static void save() {
        ConfigData data = new ConfigData();
        data.gradient_range = GRADIENT_RANGE.get();
        data.base_darkness = BASE_DARKNESS.get();
        data.solid_dark_height = SOLID_DARK_HEIGHT.get();
        data.per_entity_overrides = PER_ENTITY_OVERRIDES;

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) { e.printStackTrace(); }

        lastModifiedTime = CONFIG_FILE.lastModified();
    }

    // Default Values of Specific Entities (These Entities have an adjusted Smooth Lightning because the Default doesn't show properly on them)
    private static Map<String, EntityOverride> createDefaultOverrides() {
        Map<String, EntityOverride> defaults = new LinkedHashMap<>(); // LinkedHashMap para mantener el orden
        defaults.put("minecraft:slime", new EntityOverride(1.0, 0.3, 0.0));
        defaults.put("minecraft:armor_stand", new EntityOverride(1.0, 0.2, 0.0));
        defaults.put("minecraft:camel", new EntityOverride(1.0, 0.2, 0.2));
        defaults.put("minecraft:frog", new EntityOverride(0.9, 0.25, 0.0));
        defaults.put("minecraft:sulfur_cube", new EntityOverride(1.0, 0.25, 0.0));
        defaults.put("minecraft:spider", new EntityOverride(1.1, 0.1, 0.1));
        defaults.put("minecraft:cave_spider", new EntityOverride(1.1, 0.1, 0.1));
        defaults.put("minecraft:camel_husk", new EntityOverride(1.0, 0.2, 0.2));
        defaults.put("minecraft:creaking", new EntityOverride(1.0, 0.1, 0.1));
        defaults.put("minecraft:panda", new EntityOverride(1.0, 0.25, 0.35));
        defaults.put("minecraft:happy_ghast", new EntityOverride(0.4, 0.2, 0.0));
        defaults.put("minecraft:ghast", new EntityOverride(0.4, 0.2, -0.1));
        defaults.put("minecraft:magma_cube", new EntityOverride(0.4, 0.1, 0.0));
        defaults.put("minecraft:squid", new EntityOverride(1.0, 0.15, 0.4));
        defaults.put("minecraft:salmon", new EntityOverride(1.0, 0.1, 0.1));
        defaults.put("minecraft:cod", new EntityOverride(1.0, 0.1, 0.1));
        defaults.put("minecraft:pufferfish", new EntityOverride(0.75, 0.1, 0.1));
        return defaults;
    }

    public static class EntityOverride {
        public double range;
        public double darkness;
        public double solidHeight;
        public EntityOverride(double range, double darkness, double solidHeight) {
            this.range = range;
            this.darkness = darkness;
            this.solidHeight = solidHeight;
        }
    }

    private static class ConfigData {
        Double gradient_range;
        Double base_darkness;
        Double solid_dark_height;
        Map<String, EntityOverride> per_entity_overrides;
    }
}