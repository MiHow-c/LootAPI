package pl.mikof.lootapi.config;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.LootTableAPI;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Manager do zarządzania konfiguracją loot tables z plików JSON
 */
public class LootConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("LootConfigManager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_DIR = "config/lootapi";
    
    public static void loadConfig(String filename) {
        try {
            Path configPath = Paths.get(CONFIG_DIR, filename);
            
            if (!Files.exists(configPath)) {
                LOGGER.warn("Config file not found: {}", configPath);
                return;
            }
            
            String json = Files.readString(configPath);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            
            if (root.has("modifications")) {
                JsonArray modifications = root.getAsJsonArray("modifications");
                parseModifications(modifications);
            }
            
            if (root.has("removals")) {
                JsonArray removals = root.getAsJsonArray("removals");
                parseRemovals(removals);
            }
            
            if (root.has("replacements")) {
                JsonArray replacements = root.getAsJsonArray("replacements");
                parseReplacements(replacements);
            }
            
            if (root.has("multipliers")) {
                JsonObject multipliers = root.getAsJsonObject("multipliers");
                parseMultipliers(multipliers);
            }
            
            LOGGER.info("Successfully loaded config from: {}", filename);
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
            
            Item item = Registries.ITEM.get(new Identifier(itemId));
            if (item != Items.AIR) {
                if (mod.has("count")) {
                    JsonObject count = mod.getAsJsonObject("count");
                    int min = count.get("min").getAsInt();
                    int max = count.get("max").getAsInt();
                    LootTableAPI.addItemWithCount(new Identifier(tableId), item, weight, min, max);
                } else {
                    LootTableAPI.addItemToTable(new Identifier(tableId), item, weight);
                }
                LOGGER.debug("Added {} to {} with weight {}", itemId, tableId, weight);
            }
        }
    }
    
    private static void parseRemovals(JsonArray removals) {
        for (JsonElement element : removals) {
            JsonObject removal = element.getAsJsonObject();
            String tableId = removal.get("table").getAsString();
            String itemId = removal.get("item").getAsString();
            
            Item item = Registries.ITEM.get(new Identifier(itemId));
            if (item != Items.AIR) {
                LootTableAPI.removeItemFromTable(new Identifier(tableId), item);
                LOGGER.debug("Removed {} from {}", itemId, tableId);
            }
        }
    }
    
    private static void parseReplacements(JsonArray replacements) {
        for (JsonElement element : replacements) {
            JsonObject replacement = element.getAsJsonObject();
            String tableId = replacement.get("table").getAsString();
            String oldItemId = replacement.get("old_item").getAsString();
            String newItemId = replacement.get("new_item").getAsString();
            
            Item oldItem = Registries.ITEM.get(new Identifier(oldItemId));
            Item newItem = Registries.ITEM.get(new Identifier(newItemId));
            
            if (oldItem != Items.AIR && newItem != Items.AIR) {
                LootTableAPI.replaceItem(new Identifier(tableId), oldItem, newItem);
                LOGGER.debug("Replacing {} with {} in {}", oldItemId, newItemId, tableId);
            }
        }
    }
    
    private static void parseMultipliers(JsonObject multipliers) {
        for (Map.Entry<String, JsonElement> entry : multipliers.entrySet()) {
            String tableId = entry.getKey();
            float multiplier = entry.getValue().getAsFloat();
            LootTableAPI.multiplyDrops(new Identifier(tableId), multiplier);
            LOGGER.debug("Set multiplier {} for {}", multiplier, tableId);
        }
    }
    
    public static void saveConfig(String filename) {
        try {
            Path configPath = Paths.get(CONFIG_DIR, filename);
            Files.createDirectories(configPath.getParent());
            
            JsonObject root = new JsonObject();
            JsonArray modifications = new JsonArray();
            root.add("modifications", modifications);
            
            JsonObject multipliers = new JsonObject();
            LootTableAPI.getModifiers().forEach((id, modifier) -> {
                Float mult = LootTableAPI.getMultiplier(id);
                if (mult != null) {
                    multipliers.addProperty(id.toString(), mult);
                }
            });
            root.add("multipliers", multipliers);
            
            String json = GSON.toJson(root);
            Files.writeString(configPath, json);
            LOGGER.info("Saved config to: {}", filename);
        } catch (Exception e) {
            LOGGER.error("Failed to save config: {}", filename, e);
        }
    }

    
    public static void loadAllConfigs() {
        try {
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                LOGGER.info("Config directory created at: {}", configDir);
                LOGGER.info("No configuration files found. Loot API will not modify any loot tables.");
                return;
            }
            
            long configCount = Files.list(configDir)
                .filter(path -> path.toString().endsWith(".json"))
                .peek(path -> loadConfig(path.getFileName().toString()))
                .count();
                
            if (configCount == 0) {
                LOGGER.info("No active configuration files found. Loot API is running in passive mode.");
                LOGGER.info("Create .json files in config/lootapi/ to configure loot table modifications.");
            } else {
                LOGGER.info("Loaded {} configuration file(s)", configCount);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load configs", e);
        }
    }
    
    public static void exportLootTable(Identifier tableId, String filename) {
        LootTableAPI.exportToJson(tableId, CONFIG_DIR + "/" + filename);
    }
    
    public static void importLootTable(String filename) {
        loadConfig(filename);
    }
}
