package pl.mikof.lootapi.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.LootAPI;
import pl.mikof.lootapi.util.ColoredLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Rejestr wszystkich Global Loot Modifiers tworzonych programatycznie
 */
public class LootModifierRegistry {
    private static final ColoredLogger LOGGER = new ColoredLogger(LoggerFactory.getLogger("LootModifierRegistry"));
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, JsonObject> MODIFIERS = new LinkedHashMap<>();
    private static boolean initialized = false;

    /**
     * Inicjalizuje rejestr
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        LOGGER.init("LootModifierRegistry initialized");
    }

    /**
     * Rejestruje nowy modifier
     */
    public static void register(LootModifierBuilder builder) {
        if (builder == null) {
            LOGGER.warn("Attempted to register null modifier builder, skipping");
            return;
        }

        String id = builder.getModifierId();
        if (id == null || id.isEmpty()) {
            LOGGER.warn("Modifier has invalid ID, skipping");
            return;
        }

        if (MODIFIERS.containsKey(id)) {
            LOGGER.warn("Modifier '{}' already registered, overwriting", id);
        }

        JsonObject json = builder.build();
        MODIFIERS.put(id, json);
        LOGGER.debug("Registered loot modifier: {}", id);
    }

    /**
     * Zapisuje wszystkie modifiery do plików
     * Wywoływane podczas startu gry
     */
    public static void writeToFiles() {
        if (MODIFIERS.isEmpty()) {
            LOGGER.info("No programmatic loot modifiers to write");
            return;
        }

        LOGGER.header("Writing loot modifiers to disk...");

        try {
            // Ścieżka do wygenerowanych danych
            // WAŻNE: Global Loot Modifiers MUSZĄ być częścią resources moda!
            // W środowisku dev: zapisujemy do src/generated/resources/
            // FMLPaths.GAMEDIR.get() wskazuje na run/, więc cofamy się o poziom wyżej
            Path projectRoot = FMLPaths.GAMEDIR.get().getParent();
            Path dataPath = projectRoot
                    .resolve("src")
                    .resolve("generated")
                    .resolve("resources");

            // Utwórz katalogi dla modifierów
            // Ścieżka: src/generated/resources/data/lootapi/loot_modifiers/
            Path lootModifiersPath = dataPath
                    .resolve("data")
                    .resolve(LootAPI.MOD_ID)
                    .resolve("loot_modifiers");

            Files.createDirectories(lootModifiersPath);
            LOGGER.debug("Created modifiers directory: {}", lootModifiersPath);

            // Zapisz każdy modifier
            List<String> entries = new ArrayList<>();
            int written = 0;
            for (Map.Entry<String, JsonObject> entry : MODIFIERS.entrySet()) {
                String id = entry.getKey();
                JsonObject json = entry.getValue();

                // Zapisz plik JSON modifiera
                Path modifierFile = lootModifiersPath.resolve(id + ".json");
                Files.writeString(modifierFile, GSON.toJson(json));

                entries.add(LootAPI.MOD_ID + ":" + id);
                written++;
                LOGGER.debug("Wrote modifier: {}.json", id);
            }

            // Zapisz global_loot_modifiers.json
            writeGlobalRegistry(dataPath, entries);

            LOGGER.success("Successfully wrote {} loot modifiers to disk", written);

        } catch (IOException e) {
            LOGGER.error("Failed to write loot modifiers to disk", e);
        }

        LOGGER.separator();
    }

    /**
     * Zapisuje plik global_loot_modifiers.json
     */
    private static void writeGlobalRegistry(Path dataPath, List<String> entries) throws IOException {
        // Ścieżka: src/generated/resources/data/neoforge/loot_modifiers/global_loot_modifiers.json
        Path globalPath = dataPath
                .resolve("data")
                .resolve("neoforge")
                .resolve("loot_modifiers");

        Files.createDirectories(globalPath);

        JsonObject global = new JsonObject();
        global.addProperty("replace", false);

        JsonArray entriesArray = new JsonArray();
        for (String entry : entries) {
            entriesArray.add(entry);
        }
        global.add("entries", entriesArray);

        Path globalFile = globalPath.resolve("global_loot_modifiers.json");

        // Jeśli plik istnieje, merge'uj wpisy
        if (Files.exists(globalFile)) {
            try {
                String existing = Files.readString(globalFile);
                JsonObject existingJson = GSON.fromJson(existing, JsonObject.class);

                if (existingJson.has("entries")) {
                    JsonArray existingEntries = existingJson.getAsJsonArray("entries");
                    for (int i = 0; i < existingEntries.size(); i++) {
                        String entry = existingEntries.get(i).getAsString();
                        if (!entries.contains(entry)) {
                            entriesArray.add(entry);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to merge existing global_loot_modifiers.json, overwriting", e);
            }
        }

        Files.writeString(globalFile, GSON.toJson(global));
        LOGGER.success("Wrote global registry: {}", globalFile.getFileName());
    }

    /**
     * Czyści wszystkie zarejestrowane modifiery
     */
    public static void clear() {
        int count = MODIFIERS.size();
        MODIFIERS.clear();
        LOGGER.action("Cleared {} loot modifiers", count);
    }

    /**
     * Zwraca liczbę zarejestrowanych modifierów
     */
    public static int getModifierCount() {
        return MODIFIERS.size();
    }

    /**
     * Informacje o zarejstrowanych modifierach
     */
    public static void printDebugInfo() {
        LOGGER.separator();
        LOGGER.header("Registered Loot Modifiers");
        LOGGER.separator();

        if (MODIFIERS.isEmpty()) {
            LOGGER.info("No modifiers registered");
        } else {
            for (String id : MODIFIERS.keySet()) {
                LOGGER.info("  → {}", id);
            }
        }

        LOGGER.separator();
        LOGGER.success("Total: {} modifiers", MODIFIERS.size());
        LOGGER.separator();
    }
}
