package pl.mikof.lootapi.config;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.LootTableAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Manager konfiguracji - ładuje modyfikacje z plików JSON
 */
public class LootConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("LootConfigManager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Ładuje wszystkie pliki konfiguracyjne
     */
    public static void loadAllConfigs() {
        Path configDir = FMLPaths.CONFIGDIR.get().resolve("lootapi");

        try {
            // Stwórz katalog jeśli nie istnieje
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                createExampleConfig(configDir);
                LOGGER.info("Created config directory: {}", configDir);
            }

            // Załaduj wszystkie pliki .json
            Files.list(configDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(LootConfigManager::loadConfigFile);

        } catch (IOException e) {
            LOGGER.error("Failed to load configs", e);
        }
    }

    /**
     * Ładuje pojedynczy plik konfiguracyjny
     */
    private static void loadConfigFile(Path configFile) {
        try {
            String json = Files.readString(configFile);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            LOGGER.info("Loading config: {}", configFile.getFileName());

            // Przetwórz modyfikacje
            if (root.has("modifications")) {
                processModifications(root.getAsJsonArray("modifications"));
            }

            // Przetwórz usunięcia
            if (root.has("removals")) {
                processRemovals(root.getAsJsonArray("removals"));
            }

            // Przetwórz zastąpienia
            if (root.has("replacements")) {
                processReplacements(root.getAsJsonArray("replacements"));
            }

            // Przetwórz mnożniki
            if (root.has("multipliers")) {
                processMultipliers(root.getAsJsonArray("multipliers"));
            }

            // Przetwórz wyłączenia
            if (root.has("disabled_tables")) {
                processDisabledTables(root.getAsJsonArray("disabled_tables"));
            }

        } catch (Exception e) {
            LOGGER.error("Failed to load config: {}", configFile, e);
        }
    }

    /**
     * Przetwarza modyfikacje (dodawanie przedmiotów)
     */
    private static void processModifications(JsonArray modifications) {
        for (JsonElement element : modifications) {
            try {
                JsonObject mod = element.getAsJsonObject();

                String tableId = mod.get("table").getAsString();
                String itemId = mod.get("item").getAsString();

                ResourceLocation table = ResourceLocation.parse(tableId);
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));

                if (item == Items.AIR) {
                    LOGGER.warn("Unknown item: {}", itemId);
                    continue;
                }

                // Sprawdź czy ma określoną ilość
                if (mod.has("count")) {
                    JsonObject count = mod.getAsJsonObject("count");
                    int min = count.get("min").getAsInt();
                    int max = count.get("max").getAsInt();
                    int weight = mod.has("weight") ? mod.get("weight").getAsInt() : 1;

                    LootTableAPI.addItemWithCount(table, item, weight, min, max);
                } else {
                    int weight = mod.has("weight") ? mod.get("weight").getAsInt() : 1;
                    LootTableAPI.addItemToTable(table, item, weight);
                }

            } catch (Exception e) {
                LOGGER.error("Failed to process modification", e);
            }
        }
    }

    /**
     * Przetwarza usunięcia przedmiotów
     */
    private static void processRemovals(JsonArray removals) {
        for (JsonElement element : removals) {
            try {
                JsonObject removal = element.getAsJsonObject();

                String tableId = removal.get("table").getAsString();
                String itemId = removal.get("item").getAsString();

                ResourceLocation table = ResourceLocation.parse(tableId);
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));

                if (item == Items.AIR) {
                    LOGGER.warn("Unknown item: {}", itemId);
                    continue;
                }

                LootTableAPI.removeItemFromTable(table, item);

            } catch (Exception e) {
                LOGGER.error("Failed to process removal", e);
            }
        }
    }

    /**
     * Przetwarza zastąpienia przedmiotów
     */
    private static void processReplacements(JsonArray replacements) {
        for (JsonElement element : replacements) {
            try {
                JsonObject replacement = element.getAsJsonObject();

                String tableId = replacement.get("table").getAsString();
                String oldItemId = replacement.get("old_item").getAsString();
                String newItemId = replacement.get("new_item").getAsString();

                ResourceLocation table = ResourceLocation.parse(tableId);
                Item oldItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(oldItemId));
                Item newItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(newItemId));

                if (oldItem == Items.AIR || newItem == Items.AIR) {
                    LOGGER.warn("Unknown item in replacement");
                    continue;
                }

                LootTableAPI.replaceItem(table, oldItem, newItem);

            } catch (Exception e) {
                LOGGER.error("Failed to process replacement", e);
            }
        }
    }

    /**
     * Przetwarza mnożniki dropów
     */
    private static void processMultipliers(JsonArray multipliers) {
        for (JsonElement element : multipliers) {
            try {
                JsonObject mult = element.getAsJsonObject();

                String tableId = mult.get("table").getAsString();
                float multiplier = mult.get("multiplier").getAsFloat();

                ResourceLocation table = ResourceLocation.parse(tableId);
                LootTableAPI.multiplyDrops(table, multiplier);

            } catch (Exception e) {
                LOGGER.error("Failed to process multiplier", e);
            }
        }
    }

    /**
     * Przetwarza wyłączone tabele
     */
    private static void processDisabledTables(JsonArray disabledTables) {
        for (JsonElement element : disabledTables) {
            try {
                String tableId = element.getAsString();
                ResourceLocation table = ResourceLocation.parse(tableId);
                LootTableAPI.disableLootTable(table);

            } catch (Exception e) {
                LOGGER.error("Failed to process disabled table", e);
            }
        }
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

            LOGGER.info("Created example config: {}", exampleFile);
            LOGGER.info("Rename to .json to enable");

        } catch (IOException e) {
            LOGGER.error("Failed to create example config", e);
        }
    }
}