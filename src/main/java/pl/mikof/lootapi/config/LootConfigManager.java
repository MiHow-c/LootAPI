package pl.mikof.lootapi.config;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.LootTableAPI;  // ✅ Correct import from parent package

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Config manager for loading loot modifications from JSON
 */
public class LootConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("LootConfigManager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_DIR = "config/lootapi";

    public static void loadAllConfigs() {
        try {
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                LOGGER.info("Config directory created at: {}", configDir);
                LOGGER.info("No configuration files found.");
                return;
            }

            long configCount = Files.list(configDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .peek(path -> loadConfig(path.getFileName().toString()))
                    .count();

            if (configCount == 0) {
                LOGGER.info("No active configuration files found.");
            } else {
                LOGGER.info("Loaded {} configuration file(s)", configCount);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load configs", e);
        }
    }

    public static void loadConfig(String filename) {
        try {
            Path configPath = Paths.get(CONFIG_DIR, filename);

            if (!Files.exists(configPath)) {
                return;
            }

            String json = Files.readString(configPath);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            if (root.has("modifications")) {
                parseModifications(root.getAsJsonArray("modifications"));
            }

            if (root.has("removals")) {
                parseRemovals(root.getAsJsonArray("removals"));
            }

            if (root.has("replacements")) {
                parseReplacements(root.getAsJsonArray("replacements"));
            }

            if (root.has("multipliers")) {
                parseMultipliers(root.getAsJsonObject("multipliers"));
            }

            LOGGER.info("Loaded config: {}", filename);
        } catch (Exception e) {
            LOGGER.error("Failed to load config: {}", filename, e);
        }
    }

    private static void parseModifications(JsonArray modifications) {
        for (JsonElement element : modifications) {
            JsonObject mod = element.getAsJsonObject();
            String tableId = mod.get("table").getAsString();
            String itemId = mod.get("item").getAsString();
            int weight = mod.has("weight") ? mod.get("weight").getAsInt() : 1;

            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
            if (item != Items.AIR) {
                if (mod.has("count")) {
                    JsonObject count = mod.getAsJsonObject("count");
                    int min = count.get("min").getAsInt();
                    int max = count.get("max").getAsInt();
                    LootTableAPI.addItemWithCount(ResourceLocation.parse(tableId), item, weight, min, max);
                } else {
                    LootTableAPI.addItemToTable(ResourceLocation.parse(tableId), item, weight);
                }
            }
        }
    }

    private static void parseRemovals(JsonArray removals) {
        for (JsonElement element : removals) {
            JsonObject removal = element.getAsJsonObject();
            String tableId = removal.get("table").getAsString();
            String itemId = removal.get("item").getAsString();

            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
            if (item != Items.AIR) {
                LootTableAPI.removeItemFromTable(ResourceLocation.parse(tableId), item);
            }
        }
    }

    private static void parseReplacements(JsonArray replacements) {
        for (JsonElement element : replacements) {
            JsonObject replacement = element.getAsJsonObject();
            String tableId = replacement.get("table").getAsString();
            String oldItemId = replacement.get("old_item").getAsString();
            String newItemId = replacement.get("new_item").getAsString();

            Item oldItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(oldItemId));
            Item newItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(newItemId));

            if (oldItem != Items.AIR && newItem != Items.AIR) {
                LootTableAPI.replaceItem(ResourceLocation.parse(tableId), oldItem, newItem);
            }
        }
    }

    private static void parseMultipliers(JsonObject multipliers) {
        for (var entry : multipliers.entrySet()) {
            String tableId = entry.getKey();
            float multiplier = entry.getValue().getAsFloat();
            LootTableAPI.multiplyDrops(ResourceLocation.parse(tableId), multiplier);
        }
    }
}