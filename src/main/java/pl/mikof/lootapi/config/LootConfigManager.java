package pl.mikof.lootapi.config;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.LootTableAPI;
import pl.mikof.lootapi.util.ColoredLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Manager konfiguracji - ładuje modyfikacje z plików JSON
 */
public class LootConfigManager {
    private static final ColoredLogger LOGGER = new ColoredLogger(LoggerFactory.getLogger("LootConfigManager"));
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static int loadedModifications = 0;

    /**
     * Ładuje wszystkie pliki konfiguracyjne
     */
    public static void loadAllConfigs() {
        Path configDir = FMLPaths.CONFIGDIR.get().resolve("lootapi");
        loadedModifications = 0;

        LOGGER.header("Loading LootAPI configurations...");

        try {
            // Stwórz katalog jeśli nie istnieje
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                createExampleConfig(configDir);
                LOGGER.init("Created config directory: {}", configDir);
            }

            // Załaduj wszystkie pliki .json
            long fileCount = Files.list(configDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .peek(LootConfigManager::loadConfigFile)
                    .count();

            if (fileCount == 0) {
                LOGGER.info("No configuration files found in {}", configDir);
                LOGGER.info("Create .json files to add loot modifications");
            } else {
                LOGGER.success("Loaded {} modifications from {} config files", loadedModifications, fileCount);
            }

        } catch (IOException e) {
            LOGGER.error("Failed to load configs", e);
        }

        LOGGER.separator();
    }

    /**
     * Ładuje pojedynczy plik konfiguracyjny
     */
    private static void loadConfigFile(Path configFile) {
        try {
            LOGGER.action("Loading config: {}", configFile.getFileName());

            // Walidacja pliku
            if (!Files.exists(configFile)) {
                LOGGER.error("Config file does not exist: {}", configFile);
                return;
            }

            if (!Files.isReadable(configFile)) {
                LOGGER.error("Config file is not readable: {}", configFile);
                return;
            }

            String json = Files.readString(configFile);

            if (json == null || json.trim().isEmpty()) {
                LOGGER.warn("Config file is empty: {}", configFile.getFileName());
                return;
            }

            JsonObject root;
            try {
                root = JsonParser.parseString(json).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                LOGGER.error("Invalid JSON syntax in {}: {}", configFile.getFileName(), e.getMessage());
                return;
            }

            int modificationsInFile = 0;

            // Przetwórz modyfikacje
            if (root.has("modifications")) {
                modificationsInFile += processModifications(root.getAsJsonArray("modifications"));
            }

            // Przetwórz usunięcia
            if (root.has("removals")) {
                modificationsInFile += processRemovals(root.getAsJsonArray("removals"));
            }

            // Przetwórz zastąpienia
            if (root.has("replacements")) {
                modificationsInFile += processReplacements(root.getAsJsonArray("replacements"));
            }

            // Przetwórz mnożniki
            if (root.has("multipliers")) {
                modificationsInFile += processMultipliers(root.getAsJsonArray("multipliers"));
            }

            // Przetwórz wyłączenia
            if (root.has("disabled_tables")) {
                modificationsInFile += processDisabledTables(root.getAsJsonArray("disabled_tables"));
            }

            if (modificationsInFile > 0) {
                LOGGER.success("Loaded {} modifications from {}", modificationsInFile, configFile.getFileName());
            } else {
                LOGGER.warn("No modifications found in {}", configFile.getFileName());
            }

        } catch (IOException e) {
            LOGGER.error("Failed to read config file: {}", configFile.getFileName(), e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error loading config: {}", configFile.getFileName(), e);
        }
    }

    /**
     * Przetwarza modyfikacje (dodawanie przedmiotów)
     */
    private static int processModifications(JsonArray modifications) {
        int count = 0;
        for (JsonElement element : modifications) {
            try {
                if (!element.isJsonObject()) {
                    LOGGER.warn("Invalid modification entry (not an object), skipping");
                    continue;
                }

                JsonObject mod = element.getAsJsonObject();

                // Walidacja wymaganych pól
                if (!mod.has("table")) {
                    LOGGER.warn("Modification missing 'table' field, skipping");
                    continue;
                }
                if (!mod.has("item")) {
                    LOGGER.warn("Modification missing 'item' field, skipping");
                    continue;
                }

                String tableId = mod.get("table").getAsString();
                String itemId = mod.get("item").getAsString();

                // Walidacja ID
                if (tableId == null || tableId.isEmpty()) {
                    LOGGER.warn("Invalid table ID (empty), skipping");
                    continue;
                }
                if (itemId == null || itemId.isEmpty()) {
                    LOGGER.warn("Invalid item ID (empty), skipping");
                    continue;
                }

                ResourceLocation table = ResourceLocation.parse(tableId);
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));

                if (item == Items.AIR) {
                    LOGGER.warn("Unknown item: {}", itemId);
                    continue;
                }

                // Sprawdź czy ma określoną ilość
                if (mod.has("count")) {
                    JsonObject countObj = mod.getAsJsonObject("count");
                    int min = countObj.has("min") ? countObj.get("min").getAsInt() : 1;
                    int max = countObj.has("max") ? countObj.get("max").getAsInt() : 1;
                    float chance = mod.has("chance") ? mod.get("chance").getAsFloat() : 1.0f;

                    // Walidacja wartości
                    if (min <= 0 || max <= 0) {
                        LOGGER.warn("Invalid count values (min: {}, max: {}), skipping", min, max);
                        continue;
                    }
                    if (chance < 0.0f || chance > 1.0f) {
                        LOGGER.warn("Invalid chance value: {} (must be 0.0-1.0), skipping", chance);
                        continue;
                    }

                    LootTableAPI.addItemToTable(table, item, min, max, chance);
                } else {
                    int singleCount = mod.has("count_single") ? mod.get("count_single").getAsInt() : 1;
                    if (singleCount <= 0) {
                        LOGGER.warn("Invalid count value: {}, skipping", singleCount);
                        continue;
                    }
                    LootTableAPI.addItemToTable(table, item, singleCount);
                }

                count++;
                loadedModifications++;

            } catch (JsonSyntaxException e) {
                LOGGER.error("JSON syntax error in modification: {}", e.getMessage());
            } catch (Exception e) {
                LOGGER.error("Failed to process modification: {}", e.getMessage());
            }
        }
        return count;
    }

    /**
     * Przetwarza usunięcia przedmiotów
     */
    private static int processRemovals(JsonArray removals) {
        int count = 0;
        for (JsonElement element : removals) {
            try {
                if (!element.isJsonObject()) {
                    LOGGER.warn("Invalid removal entry (not an object), skipping");
                    continue;
                }

                JsonObject removal = element.getAsJsonObject();

                if (!removal.has("table") || !removal.has("item")) {
                    LOGGER.warn("Removal missing required fields, skipping");
                    continue;
                }

                String tableId = removal.get("table").getAsString();
                String itemId = removal.get("item").getAsString();

                if (tableId == null || tableId.isEmpty() || itemId == null || itemId.isEmpty()) {
                    LOGGER.warn("Invalid table or item ID (empty), skipping");
                    continue;
                }

                ResourceLocation table = ResourceLocation.parse(tableId);
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));

                if (item == Items.AIR) {
                    LOGGER.warn("Unknown item: {}", itemId);
                    continue;
                }

                LootTableAPI.removeItemFromTable(table, item);
                count++;
                loadedModifications++;

            } catch (Exception e) {
                LOGGER.error("Failed to process removal: {}", e.getMessage());
            }
        }
        return count;
    }

    /**
     * Przetwarza zastąpienia przedmiotów
     */
    private static int processReplacements(JsonArray replacements) {
        int count = 0;
        for (JsonElement element : replacements) {
            try {
                if (!element.isJsonObject()) {
                    LOGGER.warn("Invalid replacement entry (not an object), skipping");
                    continue;
                }

                JsonObject replacement = element.getAsJsonObject();

                if (!replacement.has("table") || !replacement.has("old_item") || !replacement.has("new_item")) {
                    LOGGER.warn("Replacement missing required fields, skipping");
                    continue;
                }

                String tableId = replacement.get("table").getAsString();
                String oldItemId = replacement.get("old_item").getAsString();
                String newItemId = replacement.get("new_item").getAsString();

                if (tableId == null || tableId.isEmpty() ||
                    oldItemId == null || oldItemId.isEmpty() ||
                    newItemId == null || newItemId.isEmpty()) {
                    LOGGER.warn("Invalid IDs in replacement (empty), skipping");
                    continue;
                }

                ResourceLocation table = ResourceLocation.parse(tableId);
                Item oldItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(oldItemId));
                Item newItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(newItemId));

                if (oldItem == Items.AIR) {
                    LOGGER.warn("Unknown old item: {}", oldItemId);
                    continue;
                }
                if (newItem == Items.AIR) {
                    LOGGER.warn("Unknown new item: {}", newItemId);
                    continue;
                }

                LootTableAPI.replaceItem(table, oldItem, newItem);
                count++;
                loadedModifications++;

            } catch (Exception e) {
                LOGGER.error("Failed to process replacement: {}", e.getMessage());
            }
        }
        return count;
    }

    /**
     * Przetwarza mnożniki dropów
     */
    private static int processMultipliers(JsonArray multipliers) {
        int count = 0;
        for (JsonElement element : multipliers) {
            try {
                if (!element.isJsonObject()) {
                    LOGGER.warn("Invalid multiplier entry (not an object), skipping");
                    continue;
                }

                JsonObject mult = element.getAsJsonObject();

                if (!mult.has("table") || !mult.has("multiplier")) {
                    LOGGER.warn("Multiplier missing required fields, skipping");
                    continue;
                }

                String tableId = mult.get("table").getAsString();
                float multiplier = mult.get("multiplier").getAsFloat();

                if (tableId == null || tableId.isEmpty()) {
                    LOGGER.warn("Invalid table ID (empty), skipping");
                    continue;
                }

                if (multiplier <= 0.0f) {
                    LOGGER.warn("Invalid multiplier value: {} (must be positive), skipping", multiplier);
                    continue;
                }

                if (multiplier > 100.0f) {
                    LOGGER.warn("Extremely high multiplier: {}x - are you sure?", multiplier);
                }

                ResourceLocation table = ResourceLocation.parse(tableId);
                LootTableAPI.multiplyDrops(table, multiplier);
                count++;
                loadedModifications++;

            } catch (Exception e) {
                LOGGER.error("Failed to process multiplier: {}", e.getMessage());
            }
        }
        return count;
    }

    /**
     * Przetwarza wyłączone tabele
     */
    private static int processDisabledTables(JsonArray disabledTables) {
        int count = 0;
        for (JsonElement element : disabledTables) {
            try {
                if (!element.isJsonPrimitive()) {
                    LOGGER.warn("Invalid disabled table entry (not a string), skipping");
                    continue;
                }

                String tableId = element.getAsString();

                if (tableId == null || tableId.isEmpty()) {
                    LOGGER.warn("Invalid table ID (empty), skipping");
                    continue;
                }

                ResourceLocation table = ResourceLocation.parse(tableId);
                LootTableAPI.disableLootTable(table);
                count++;
                loadedModifications++;

            } catch (Exception e) {
                LOGGER.error("Failed to process disabled table: {}", e.getMessage());
            }
        }
        return count;
    }

    /**
     * Tworzy przykładowy plik konfiguracyjny
     */
    private static void createExampleConfig(Path configDir) {
        try {
            JsonObject example = new JsonObject();

            // Przykładowe modyfikacje
            JsonArray modifications = new JsonArray();

            JsonObject mod1 = new JsonObject();
            mod1.addProperty("table", "minecraft:blocks/diamond_ore");
            mod1.addProperty("item", "minecraft:emerald");
            mod1.addProperty("weight", 10);
            mod1.addProperty("_comment", "10% szansa na emerald z diamond ore");
            modifications.add(mod1);

            JsonObject mod2 = new JsonObject();
            mod2.addProperty("table", "minecraft:entities/zombie");
            mod2.addProperty("item", "minecraft:iron_ingot");
            JsonObject count = new JsonObject();
            count.addProperty("min", 1);
            count.addProperty("max", 3);
            mod2.add("count", count);
            mod2.addProperty("_comment", "Zombie dropią 1-3 iron ingots");
            modifications.add(mod2);

            example.add("modifications", modifications);

            // Przykładowe usunięcia
            JsonArray removals = new JsonArray();

            JsonObject rem1 = new JsonObject();
            rem1.addProperty("table", "minecraft:entities/zombie");
            rem1.addProperty("item", "minecraft:rotten_flesh");
            rem1.addProperty("_comment", "Usuń rotten flesh z zombie");
            removals.add(rem1);

            example.add("removals", removals);

            // Przykładowe zastąpienia
            JsonArray replacements = new JsonArray();

            JsonObject rep1 = new JsonObject();
            rep1.addProperty("table", "minecraft:blocks/grass_block");
            rep1.addProperty("old_item", "minecraft:grass_block");
            rep1.addProperty("new_item", "minecraft:dirt");
            rep1.addProperty("_comment", "Grass block dropuje dirt zamiast siebie");
            replacements.add(rep1);

            example.add("replacements", replacements);

            // Przykładowe mnożniki
            JsonArray multipliers = new JsonArray();

            JsonObject mult1 = new JsonObject();
            mult1.addProperty("table", "minecraft:blocks/iron_ore");
            mult1.addProperty("multiplier", 2.0);
            mult1.addProperty("_comment", "Podwójne dropy z iron ore");
            multipliers.add(mult1);

            example.add("multipliers", multipliers);

            // Przykładowe wyłączenia
            JsonArray disabled = new JsonArray();
            disabled.add("minecraft:blocks/coal_ore");
            example.add("disabled_tables", disabled);
            example.addProperty("_disabled_comment", "Coal ore nic nie dropuje");

            // Zapisz przykład
            Path exampleFile = configDir.resolve("example.json.disabled");
            Files.writeString(exampleFile, GSON.toJson(example));

            LOGGER.success("Created example config: {}", exampleFile.getFileName());
            LOGGER.info("Rename to .json to enable the example configuration");

        } catch (IOException e) {
            LOGGER.error("Failed to create example config", e);
        }
    }
}