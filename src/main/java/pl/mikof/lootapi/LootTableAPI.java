package pl.mikof.lootapi;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.api.LootModifierBuilder;
import pl.mikof.lootapi.api.LootModifierRegistry;
import pl.mikof.lootapi.util.ColoredLogger;

/**
 * Publiczne API do modyfikacji loot tables
 * Prosty interfejs dla innych modów - wersja 3.0 z Global Loot Modifiers
 */
public class LootTableAPI {
    private static final ColoredLogger LOGGER = new ColoredLogger(LoggerFactory.getLogger("LootTableAPI"));
    private static boolean initialized = false;
    private static boolean finalized = false;
    private static int modifierCounter = 0;

    /**
     * Inicjalizuje API (wywoływane automatycznie)
     */
    public static void init() {
        if (!initialized) {
            initialized = true;
            LootModifierRegistry.init();
            LOGGER.success("LootTableAPI v3.0 initialized with Global Loot Modifiers");
        }
    }

    // ==================== PODSTAWOWE METODY ====================

    /**
     * Dodaje przedmiot do loot table
     * @param tableId ID tabeli (np. "minecraft:blocks/diamond_ore")
     * @param item Przedmiot do dodania
     */
    public static void addItemToTable(ResourceLocation tableId, Item item) {
        addItemToTable(tableId, item, 1, 1, 1.0f);
    }

    /**
     * Dodaje przedmiot do loot table z określoną ilością
     * @param tableId ID tabeli
     * @param item Przedmiot
     * @param count Ilość
     */
    public static void addItemToTable(ResourceLocation tableId, Item item, int count) {
        addItemToTable(tableId, item, count, count, 1.0f);
    }

    /**
     * Dodaje przedmiot do loot table z zakresem ilości
     * @param tableId ID tabeli
     * @param item Przedmiot
     * @param minCount Minimalna ilość
     * @param maxCount Maksymalna ilość
     */
    public static void addItemToTable(ResourceLocation tableId, Item item, int minCount, int maxCount) {
        addItemToTable(tableId, item, minCount, maxCount, 1.0f);
    }

    /**
     * Dodaje przedmiot do loot table z zakresem ilości i szansą
     * @param tableId ID tabeli
     * @param item Przedmiot
     * @param minCount Minimalna ilość
     * @param maxCount Maksymalna ilość
     * @param chance Szansa (0.0 - 1.0)
     */
    public static void addItemToTable(ResourceLocation tableId, Item item, int minCount, int maxCount, float chance) {
        validateInputs(tableId, item, "addItemToTable");
        validateCounts(minCount, maxCount);

        String modifierId = generateModifierId("add_item");

        LootModifierBuilder builder = LootModifierBuilder.addItem(modifierId)
                .forTable(tableId)
                .withItem(item)
                .withCount(minCount, maxCount);

        if (chance < 1.0f) {
            builder.withChance(chance);
        }

        LootModifierRegistry.register(builder);
        LOGGER.action("Added {} to {} (count: {}-{}, chance: {})", item, tableId, minCount, maxCount, chance);
    }

    /**
     * Usuwa przedmiot z loot table
     * @param tableId ID tabeli
     * @param item Przedmiot do usunięcia
     */
    public static void removeItemFromTable(ResourceLocation tableId, Item item) {
        validateInputs(tableId, item, "removeItemFromTable");

        String modifierId = generateModifierId("remove_item");

        LootModifierBuilder builder = LootModifierBuilder.removeItem(modifierId)
                .forTable(tableId)
                .withItem(item);

        LootModifierRegistry.register(builder);
        LOGGER.action("Removed {} from {}", item, tableId);
    }

    /**
     * Zastępuje jeden przedmiot innym
     * @param tableId ID tabeli
     * @param oldItem Przedmiot do zastąpienia
     * @param newItem Nowy przedmiot
     */
    public static void replaceItem(ResourceLocation tableId, Item oldItem, Item newItem) {
        validateInputs(tableId, oldItem, "replaceItem");
        validateInputs(tableId, newItem, "replaceItem");

        String modifierId = generateModifierId("replace_item");

        LootModifierBuilder builder = LootModifierBuilder.replaceItem(modifierId)
                .forTable(tableId)
                .withOldItem(oldItem)
                .withNewItem(newItem);

        LootModifierRegistry.register(builder);
        LOGGER.action("Replaced {} with {} in {}", oldItem, newItem, tableId);
    }

    /**
     * Mnoży ilość wszystkich dropów
     * @param tableId ID tabeli
     * @param multiplier Mnożnik (np. 2.0 = podwójne dropy)
     */
    public static void multiplyDrops(ResourceLocation tableId, float multiplier) {
        checkInitialized();
        checkNotFinalized();

        if (tableId == null) {
            throw new IllegalArgumentException(
                    "Table ID cannot be null!\n" +
                    "Example: LootTables.Blocks.IRON_ORE"
            );
        }
        if (multiplier <= 0) {
            throw new IllegalArgumentException(
                    "Multiplier must be positive! Got: " + multiplier + "\n" +
                    "Example: multiplyDrops(table, 2.0f) for double drops"
            );
        }

        String modifierId = generateModifierId("multiply_drops");

        LootModifierBuilder builder = LootModifierBuilder.multiplyDrops(modifierId)
                .forTable(tableId)
                .withMultiplier(multiplier);

        LootModifierRegistry.register(builder);
        LOGGER.action("Set multiplier {}x for {}", multiplier, tableId);
    }

    /**
     * Wyłącza loot table (nic nie wypada)
     * @param tableId ID tabeli
     */
    public static void disableLootTable(ResourceLocation tableId) {
        checkInitialized();
        checkNotFinalized();

        if (tableId == null) {
            throw new IllegalArgumentException(
                    "Table ID cannot be null!\n" +
                    "Example: LootTables.Blocks.STONE or LootTables.Entities.ZOMBIE"
            );
        }

        String modifierId = generateModifierId("clear_table");

        LootModifierBuilder builder = LootModifierBuilder.clearTable(modifierId)
                .forTable(tableId);

        LootModifierRegistry.register(builder);
        LOGGER.action("Disabled loot table {}", tableId);
    }

    /**
     * Ustawia tylko jeden przedmiot jako drop
     * @param tableId ID tabeli
     * @param item Przedmiot
     * @param minCount Minimalna ilość
     * @param maxCount Maksymalna ilość
     */
    public static void setOnlyDrop(ResourceLocation tableId, Item item, int minCount, int maxCount) {
        validateInputs(tableId, item, "setOnlyDrop");
        validateCounts(minCount, maxCount);

        String modifierId = generateModifierId("set_only_drop");

        LootModifierBuilder builder = LootModifierBuilder.setOnlyDrop(modifierId)
                .forTable(tableId)
                .withItem(item)
                .withCount(minCount, maxCount);

        LootModifierRegistry.register(builder);
        LOGGER.action("Set {} as only drop in {} (count: {}-{})", item, tableId, minCount, maxCount);
    }

    /**
     * Ustawia tylko jeden przedmiot jako drop (pojedynczy)
     */
    public static void setOnlyDrop(ResourceLocation tableId, Item item) {
        setOnlyDrop(tableId, item, 1, 1);
    }

    // ==================== ALIASY (łatwiejsze nazwy) ====================

    /**
     * Usuwa wszystkie dropy z tabeli (alias dla disableLootTable)
     * @param tableId ID tabeli
     */
    public static void removeAllDrops(ResourceLocation tableId) {
        disableLootTable(tableId);
    }

    /**
     * Dodaje przedmiot z określoną szansą (1 sztuka)
     * @param tableId ID tabeli
     * @param item Przedmiot
     * @param chance Szansa (0.0 - 1.0)
     */
    public static void addItemWithChance(ResourceLocation tableId, Item item, float chance) {
        addItemToTable(tableId, item, 1, 1, chance);
    }

    // ==================== BULK OPERATIONS (wiele tabel naraz) ====================

    /**
     * Dodaje przedmiot do wielu tabel jednocześnie
     * @param item Przedmiot do dodania
     * @param tables Tabele docelowe
     */
    public static void addItemToTables(Item item, ResourceLocation... tables) {
        addItemToTables(item, 1, 1, 1.0f, tables);
    }

    /**
     * Dodaje przedmiot do wielu tabel z określoną ilością i szansą
     */
    public static void addItemToTables(Item item, int minCount, int maxCount, float chance, ResourceLocation... tables) {
        if (tables == null || tables.length == 0) {
            throw new IllegalArgumentException("Must provide at least one table");
        }
        for (ResourceLocation table : tables) {
            addItemToTable(table, item, minCount, maxCount, chance);
        }
        LOGGER.success("Added {} to {} tables", item, tables.length);
    }

    /**
     * Mnoży dropy dla wielu tabel jednocześnie
     * @param multiplier Mnożnik
     * @param tables Tabele docelowe
     */
    public static void multiplyDropsForTables(float multiplier, ResourceLocation... tables) {
        if (tables == null || tables.length == 0) {
            throw new IllegalArgumentException("Must provide at least one table");
        }
        for (ResourceLocation table : tables) {
            multiplyDrops(table, multiplier);
        }
        LOGGER.success("Set {}x multiplier for {} tables", multiplier, tables.length);
    }

    /**
     * Usuwa wszystkie dropy z wielu tabel jednocześnie
     * @param tables Tabele docelowe
     */
    public static void removeAllDropsFrom(ResourceLocation... tables) {
        if (tables == null || tables.length == 0) {
            throw new IllegalArgumentException("Must provide at least one table");
        }
        for (ResourceLocation table : tables) {
            disableLootTable(table);
        }
        LOGGER.success("Disabled {} loot tables", tables.length);
    }

    /**
     * Usuwa określony przedmiot z wielu tabel jednocześnie
     * @param item Przedmiot do usunięcia
     * @param tables Tabele docelowe
     */
    public static void removeItemFromTables(Item item, ResourceLocation... tables) {
        if (tables == null || tables.length == 0) {
            throw new IllegalArgumentException("Must provide at least one table");
        }
        for (ResourceLocation table : tables) {
            removeItemFromTable(table, item);
        }
        LOGGER.success("Removed {} from {} tables", item, tables.length);
    }

    // ==================== ZAAWANSOWANE METODY ====================

    /**
     * Tworzy niestandardowy modifier builder
     */
    public static LootModifierBuilder createCustomModifier(String type, String modifierId) {
        // Metoda pozwalająca na tworzenie bardziej złożonych modifierów
        return switch (type.toLowerCase()) {
            case "add" -> LootModifierBuilder.addItem(modifierId);
            case "remove" -> LootModifierBuilder.removeItem(modifierId);
            case "replace" -> LootModifierBuilder.replaceItem(modifierId);
            case "multiply" -> LootModifierBuilder.multiplyDrops(modifierId);
            case "clear" -> LootModifierBuilder.clearTable(modifierId);
            case "setonly" -> LootModifierBuilder.setOnlyDrop(modifierId);
            default -> throw new IllegalArgumentException("Unknown modifier type: " + type);
        };
    }

    /**
     * Rejestruje niestandardowy modifier
     */
    public static void registerModifier(LootModifierBuilder builder) {
        LootModifierRegistry.register(builder);
    }

    // ==================== METODY POMOCNICZE ====================

    /**
     * Finalizuje wszystkie modifiery i zapisuje do plików
     * Wywoływane automatycznie podczas startu gry
     */
    public static void finalizeModifiers() {
        if (finalized) {
            LOGGER.warn("finalizeModifiers() already called! Ignoring duplicate call.");
            return;
        }
        if (!initialized) {
            throw new IllegalStateException(
                    "LootTableAPI not initialized! Call LootTableAPI.init() first.\n" +
                    "Make sure you call init() in your mod's commonSetup event."
            );
        }

        LootModifierRegistry.writeToFiles();
        finalized = true;
        LOGGER.success("Finalized {} loot modifiers - ready to use!", LootModifierRegistry.getModifierCount());
    }

    /**
     * Czyści wszystkie modyfikacje
     */
    public static void clearAllModifications() {
        LootModifierRegistry.clear();
        modifierCounter = 0;
        LOGGER.info("Cleared all loot modifications");
    }

    /**
     * Zwraca liczbę aktywnych modyfikacji
     */
    public static int getModificationCount() {
        return LootModifierRegistry.getModifierCount();
    }

    /**
     * Debug - wypisuje informacje o modifierach
     */
    public static void printDebugInfo() {
        LootModifierRegistry.printDebugInfo();
    }

    // ==================== WALIDACJA ====================

    /**
     * Sprawdza czy API zostało zainicjalizowane
     */
    private static void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException(
                    "\n" +
                    "========================================\n" +
                    " LootTableAPI ERROR: Not Initialized!\n" +
                    "========================================\n" +
                    "You must call LootTableAPI.init() first!\n\n" +
                    "Add this to your mod's commonSetup event:\n" +
                    "  event.enqueueWork(() -> {\n" +
                    "      LootTableAPI.init();\n" +
                    "      // your modifications here\n" +
                    "      LootTableAPI.finalizeModifiers();\n" +
                    "  });\n" +
                    "========================================\n"
            );
        }
    }

    /**
     * Sprawdza czy modyfikacje nie zostały już sfinalizowane
     */
    private static void checkNotFinalized() {
        if (finalized) {
            throw new IllegalStateException(
                    "\n" +
                    "========================================\n" +
                    " LootTableAPI ERROR: Already Finalized!\n" +
                    "========================================\n" +
                    "You cannot add modifications after calling\n" +
                    "finalizeModifiers()!\n\n" +
                    "Make sure all your modifications are done\n" +
                    "BEFORE calling finalizeModifiers().\n" +
                    "========================================\n"
            );
        }
    }

    private static void validateInputs(ResourceLocation tableId, Item item, String operation) {
        checkInitialized();
        checkNotFinalized();

        if (tableId == null) {
            throw new IllegalArgumentException(
                    operation + ": Table ID cannot be null!\n" +
                    "Example: LootTables.Blocks.DIAMOND_ORE or ResourceLocation.fromNamespaceAndPath(\"minecraft\", \"blocks/diamond_ore\")"
            );
        }
        if (item == null || item == Items.AIR) {
            throw new IllegalArgumentException(
                    operation + ": Item cannot be null or AIR!\n" +
                    "Example: Items.DIAMOND, Items.EMERALD, etc."
            );
        }
    }

    private static void validateCounts(int minCount, int maxCount) {
        if (minCount <= 0) {
            throw new IllegalArgumentException(
                    "Min count must be positive! Got: " + minCount + "\n" +
                    "Example: addItemToTable(table, item, 1, 5) for 1-5 items"
            );
        }
        if (maxCount < minCount) {
            throw new IllegalArgumentException(
                    "Max count (" + maxCount + ") must be >= min count (" + minCount + ")!\n" +
                    "Example: addItemToTable(table, item, 1, 5) NOT addItemToTable(table, item, 5, 1)"
            );
        }
        if (maxCount > 64) {
            LOGGER.warn("Max count {} exceeds stack size (64) - this may cause issues!", maxCount);
        }
    }

    private static String generateModifierId(String prefix) {
        return prefix + "_" + (modifierCounter++);
    }
}
