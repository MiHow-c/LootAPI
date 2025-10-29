package pl.mikof.lootapi.glm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.loading.FMLPaths;
import pl.mikof.lootapi.LootAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Helper class for registering Global Loot Modifiers
 * FIXED VERSION with proper runtime paths and error handling
 */
public class GLMHelper {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Use runtime paths that work in both dev and production
    private static Path getDataDir() {
        // In production, use game directory
        Path gameDir = FMLPaths.GAMEDIR.get();
        Path datapacksDir = gameDir.resolve("world/datapacks/lootapi_generated/data/" + LootAPI.MOD_ID + "/loot_modifiers");

        // Alternative: use config directory for development
        if (!Files.exists(datapacksDir.getParent())) {
            datapacksDir = FMLPaths.CONFIGDIR.get().resolve("lootapi/generated/loot_modifiers");
        }

        return datapacksDir;
    }

    private static Path getGlobalFile() {
        Path gameDir = FMLPaths.GAMEDIR.get();
        Path globalFile = gameDir.resolve("world/datapacks/lootapi_generated/data/neoforge/loot_modifiers/global_loot_modifiers.json");

        if (!Files.exists(globalFile.getParent())) {
            globalFile = FMLPaths.CONFIGDIR.get().resolve("lootapi/generated/global_loot_modifiers.json");
        }

        return globalFile;
    }

    private static final Map<String, JsonObject> pendingModifiers = new LinkedHashMap<>();
    private static final List<String> registeredModifierIds = new ArrayList<>();
    private static int modifierCounter = 0;
    private static boolean finalized = false;

    /**
     * Registers an AddItem modifier (stores in memory until finalized)
     */
    public static void registerAddItem(ResourceLocation tableId, Item item, int weight) {
        if (finalized) {
            LootAPI.LOGGER.warn("Cannot register modifiers after finalization!");
            return;
        }

        String modifierId = generateModifierId(tableId, "add_item", item);

        JsonObject json = new JsonObject();
        json.addProperty("type", LootAPI.MOD_ID + ":add_item");
        json.add("conditions", createTableCondition(tableId));
        json.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
        json.addProperty("weight", weight);

        pendingModifiers.put(modifierId, json);
        registeredModifierIds.add(LootAPI.MOD_ID + ":" + modifierId);
    }

    /**
     * Registers an AddItemWithCount modifier
     */
    public static void registerAddItemWithCount(ResourceLocation tableId, Item item, int minCount, int maxCount) {
        if (finalized) {
            LootAPI.LOGGER.warn("Cannot register modifiers after finalization!");
            return;
        }

        String modifierId = generateModifierId(tableId, "add_count", item);

        JsonObject json = new JsonObject();
        json.addProperty("type", LootAPI.MOD_ID + ":add_item_with_count");
        json.add("conditions", createTableCondition(tableId));
        json.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
        json.addProperty("min_count", minCount);
        json.addProperty("max_count", maxCount);

        pendingModifiers.put(modifierId, json);
        registeredModifierIds.add(LootAPI.MOD_ID + ":" + modifierId);
    }

    /**
     * Registers an AddItemWithChance modifier
     */
    public static void registerAddItemWithChance(ResourceLocation tableId, Item item, float chancePercent) {
        if (finalized) {
            LootAPI.LOGGER.warn("Cannot register modifiers after finalization!");
            return;
        }

        // Validate chance
        if (chancePercent <= 0 || chancePercent > 100) {
            LootAPI.LOGGER.warn("Invalid chance percentage: {}. Must be between 0 and 100", chancePercent);
            return;
        }

        String modifierId = generateModifierId(tableId, "add_chance", item);

        JsonObject json = new JsonObject();
        json.addProperty("type", LootAPI.MOD_ID + ":add_item_with_chance");

        // Create conditions array with both table check AND chance
        JsonArray conditions = new JsonArray();

        // Table ID condition
        JsonObject tableCondition = new JsonObject();
        tableCondition.addProperty("condition", LootAPI.MOD_ID + ":loot_table_id");
        tableCondition.addProperty("loot_table", tableId.toString());
        conditions.add(tableCondition);

        // Random chance condition
        JsonObject chanceCondition = new JsonObject();
        chanceCondition.addProperty("condition", "minecraft:random_chance");
        chanceCondition.addProperty("chance", chancePercent / 100.0f);
        conditions.add(chanceCondition);

        json.add("conditions", conditions);
        json.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
        json.addProperty("count", 1);

        pendingModifiers.put(modifierId, json);
        registeredModifierIds.add(LootAPI.MOD_ID + ":" + modifierId);
    }

    /**
     * Registers a RemoveItem modifier
     */
    public static void registerRemoveItem(ResourceLocation tableId, Item item) {
        if (finalized) {
            LootAPI.LOGGER.warn("Cannot register modifiers after finalization!");
            return;
        }

        String modifierId = generateModifierId(tableId, "remove", item);

        JsonObject json = new JsonObject();
        json.addProperty("type", LootAPI.MOD_ID + ":remove_item");
        json.add("conditions", createTableCondition(tableId));
        json.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());

        pendingModifiers.put(modifierId, json);
        registeredModifierIds.add(LootAPI.MOD_ID + ":" + modifierId);
    }

    /**
     * Registers a ReplaceItem modifier
     */
    public static void registerReplaceItem(ResourceLocation tableId, Item oldItem, Item newItem, boolean preserveCount) {
        if (finalized) {
            LootAPI.LOGGER.warn("Cannot register modifiers after finalization!");
            return;
        }

        String modifierId = generateModifierId(tableId, "replace", oldItem);

        JsonObject json = new JsonObject();
        json.addProperty("type", LootAPI.MOD_ID + ":replace_item");
        json.add("conditions", createTableCondition(tableId));
        json.addProperty("old_item", BuiltInRegistries.ITEM.getKey(oldItem).toString());
        json.addProperty("new_item", BuiltInRegistries.ITEM.getKey(newItem).toString());
        json.addProperty("preserve_count", preserveCount);

        pendingModifiers.put(modifierId, json);
        registeredModifierIds.add(LootAPI.MOD_ID + ":" + modifierId);
    }

    /**
     * Registers a MultiplyDrops modifier
     */
    public static void registerMultiplyDrops(ResourceLocation tableId, float multiplier) {
        if (finalized) {
            LootAPI.LOGGER.warn("Cannot register modifiers after finalization!");
            return;
        }

        if (multiplier <= 0) {
            LootAPI.LOGGER.warn("Invalid multiplier: {}. Must be positive", multiplier);
            return;
        }

        String modifierId = generateModifierId(tableId, "multiply", String.valueOf(multiplier));

        JsonObject json = new JsonObject();
        json.addProperty("type", LootAPI.MOD_ID + ":multiply_drops");
        json.add("conditions", createTableCondition(tableId));
        json.addProperty("multiplier", multiplier);

        pendingModifiers.put(modifierId, json);
        registeredModifierIds.add(LootAPI.MOD_ID + ":" + modifierId);
    }

    /**
     * Registers a ClearLootTable modifier
     */
    public static void registerClearLootTable(ResourceLocation tableId) {
        if (finalized) {
            LootAPI.LOGGER.warn("Cannot register modifiers after finalization!");
            return;
        }

        String modifierId = generateModifierId(tableId, "clear", "all");

        JsonObject json = new JsonObject();
        json.addProperty("type", LootAPI.MOD_ID + ":clear_loot_table");
        json.add("conditions", createTableCondition(tableId));

        pendingModifiers.put(modifierId, json);
        registeredModifierIds.add(LootAPI.MOD_ID + ":" + modifierId);
    }

    /**
     * Registers a SetOnlyDrop modifier
     */
    public static void registerSetOnlyDrop(ResourceLocation tableId, Item item, int minCount, int maxCount) {
        if (finalized) {
            LootAPI.LOGGER.warn("Cannot register modifiers after finalization!");
            return;
        }

        if (minCount <= 0 || maxCount < minCount) {
            LootAPI.LOGGER.warn("Invalid count range: {}-{}. Must be positive and min <= max", minCount, maxCount);
            return;
        }

        String modifierId = generateModifierId(tableId, "set_only", item);

        JsonObject json = new JsonObject();
        json.addProperty("type", LootAPI.MOD_ID + ":set_only_drop");
        json.add("conditions", createTableCondition(tableId));
        json.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
        json.addProperty("min_count", minCount);
        json.addProperty("max_count", maxCount);

        pendingModifiers.put(modifierId, json);
        registeredModifierIds.add(LootAPI.MOD_ID + ":" + modifierId);
    }

    // ========== HELPER METHODS ==========

    /**
     * Creates a table ID condition JSON
     */
    private static JsonArray createTableCondition(ResourceLocation tableId) {
        JsonArray conditions = new JsonArray();
        JsonObject condition = new JsonObject();
        condition.addProperty("condition", LootAPI.MOD_ID + ":loot_table_id");
        condition.addProperty("loot_table", tableId.toString());
        conditions.add(condition);
        return conditions;
    }

    /**
     * Generates a unique modifier ID
     */
    private static String generateModifierId(ResourceLocation tableId, String operation, Object detail) {
        modifierCounter++;
        String tablePart = tableId.getPath()
                .replace("/", "_")
                .replace(":", "_")
                .replaceAll("[^a-zA-Z0-9_]", "_"); // Sanitize special characters

        String detailPart = detail instanceof Item ?
                BuiltInRegistries.ITEM.getKey((Item) detail).getPath() :
                detail.toString().replaceAll("[^a-zA-Z0-9_]", "_");

        return tablePart + "_" + operation + "_" + detailPart + "_" + modifierCounter;
    }

    /**
     * Creates modifier JSON files (called during finalization)
     */
    private static void createModifierFiles() {
        Path dataDir = getDataDir();

        try {
            Files.createDirectories(dataDir);

            for (Map.Entry<String, JsonObject> entry : pendingModifiers.entrySet()) {
                String modifierId = entry.getKey();
                JsonObject json = entry.getValue();

                Path filePath = dataDir.resolve(modifierId + ".json");
                Files.writeString(filePath, GSON.toJson(json),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                LootAPI.LOGGER.debug("Created modifier file: {}", modifierId);
            }

            LootAPI.LOGGER.info("✓ Created {} modifier files", pendingModifiers.size());

        } catch (IOException e) {
            LootAPI.LOGGER.error("✗ Failed to create modifier files", e);
            throw new RuntimeException("Failed to create Loot API data files", e);
        }
    }

    /**
     * Creates the global_loot_modifiers.json file
     * THIS MUST BE CALLED after all modifiers are registered!
     */
    public static void createGlobalModifiersFile() {
        if (finalized) {
            LootAPI.LOGGER.warn("Modifiers already finalized!");
            return;
        }

        if (registeredModifierIds.isEmpty()) {
            LootAPI.LOGGER.info("No modifiers registered, skipping file creation");
            finalized = true;
            return;
        }

        try {
            // First create all modifier files
            createModifierFiles();

            // Then create global file
            Path globalFile = getGlobalFile();
            Files.createDirectories(globalFile.getParent());

            JsonObject json = new JsonObject();
            json.addProperty("replace", false);

            JsonArray entries = new JsonArray();
            for (String modifierId : registeredModifierIds) {
                entries.add(modifierId);
            }
            json.add("entries", entries);

            Files.writeString(globalFile, GSON.toJson(json),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            LootAPI.LOGGER.info("✓ Created global_loot_modifiers.json with {} modifiers",
                    registeredModifierIds.size());

            // Create pack.mcmeta for the datapack
            createPackMeta();

            finalized = true;

        } catch (IOException e) {
            LootAPI.LOGGER.error("✗ Failed to create global_loot_modifiers.json", e);
            throw new RuntimeException("Failed to create global modifiers file", e);
        }
    }

    /**
     * Creates pack.mcmeta for the generated datapack
     */
    private static void createPackMeta() {
        try {
            Path gameDir = FMLPaths.GAMEDIR.get();
            Path packMetaPath = gameDir.resolve("world/datapacks/lootapi_generated/pack.mcmeta");

            if (!Files.exists(packMetaPath.getParent())) {
                packMetaPath = FMLPaths.CONFIGDIR.get().resolve("lootapi/generated/pack.mcmeta");
            }

            JsonObject packMeta = new JsonObject();
            JsonObject pack = new JsonObject();
            pack.addProperty("description", "Loot API Generated Data");
            pack.addProperty("pack_format", 18); // For MC 1.20.4+
            packMeta.add("pack", pack);

            Files.createDirectories(packMetaPath.getParent());
            Files.writeString(packMetaPath, GSON.toJson(packMeta),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            LootAPI.LOGGER.debug("Created pack.mcmeta");

        } catch (IOException e) {
            LootAPI.LOGGER.warn("Could not create pack.mcmeta", e);
        }
    }

    /**
     * Gets all registered modifier IDs
     */
    public static List<String> getRegisteredModifierIds() {
        return new ArrayList<>(registeredModifierIds);
    }

    /**
     * Clears all registered modifiers
     */
    public static void clearRegistrations() {
        if (finalized) {
            LootAPI.LOGGER.warn("Cannot clear registrations after finalization!");
            return;
        }

        pendingModifiers.clear();
        registeredModifierIds.clear();
        modifierCounter = 0;
    }

    /**
     * Gets the total number of registered modifiers
     */
    public static int getModifierCount() {
        return registeredModifierIds.size();
    }

    /**
     * Checks if the system has been finalized
     */
    public static boolean isFinalized() {
        return finalized;
    }

    /**
     * Resets the finalization state (for development/testing only)
     */
    public static void resetFinalization() {
        finalized = false;
        LootAPI.LOGGER.warn("Reset finalization state - use only for development!");
    }
}