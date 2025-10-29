package pl.mikof.lootapi;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.system.LootModificationSystem;
import pl.mikof.lootapi.system.LootModificationSystem.LootModification;

/**
 * Publiczne API do modyfikacji loot tables
 * Prosty interfejs dla innych modów
 */
public class LootTableAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger("LootTableAPI");
    private static boolean initialized = false;

    /**
     * Inicjalizuje API (wywoływane automatycznie)
     */
    public static void init() {
        if (!initialized) {
            initialized = true;
            LOGGER.info("LootTableAPI initialized");
        }
    }

    // ==================== PODSTAWOWE METODY ====================

    /**
     * Dodaje przedmiot do loot table
     * @param tableId ID tabeli (np. "minecraft:blocks/diamond_ore")
     * @param item Przedmiot do dodania
     * @param weight Waga (1-100, wyższa = częściej wypada)
     */
    public static void addItemToTable(ResourceLocation tableId, Item item, int weight) {
        validateInputs(tableId, item, "addItemToTable");

        LootModificationSystem.addModification(tableId,
                LootModification.addItem(item, weight));

        LOGGER.info("Added {} to {} with weight {}", item, tableId, weight);
    }

    /**
     * Dodaje przedmiot z określoną ilością
     * @param tableId ID tabeli
     * @param item Przedmiot
     * @param weight Waga
     * @param minCount Minimalna ilość
     * @param maxCount Maksymalna ilość
     */
    public static void addItemWithCount(ResourceLocation tableId, Item item, int weight, int minCount, int maxCount) {
        validateInputs(tableId, item, "addItemWithCount");
        validateCounts(minCount, maxCount);

        LootModificationSystem.addModification(tableId,
                LootModification.addItemWithCount(item, minCount, maxCount));

        LOGGER.info("Added {} to {} with count {}-{}", item, tableId, minCount, maxCount);
    }

    /**
     * Usuwa przedmiot z loot table
     * @param tableId ID tabeli
     * @param item Przedmiot do usunięcia
     */
    public static void removeItemFromTable(ResourceLocation tableId, Item item) {
        validateInputs(tableId, item, "removeItemFromTable");

        LootModificationSystem.addModification(tableId,
                LootModification.removeItem(item));

        LOGGER.info("Removed {} from {}", item, tableId);
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

        LootModificationSystem.addModification(tableId,
                LootModification.replaceItem(oldItem, newItem));

        LOGGER.info("Replaced {} with {} in {}", oldItem, newItem, tableId);
    }

    /**
     * Mnoży ilość wszystkich dropów
     * @param tableId ID tabeli
     * @param multiplier Mnożnik (np. 2.0 = podwójne dropy)
     */
    public static void multiplyDrops(ResourceLocation tableId, float multiplier) {
        if (tableId == null) {
            throw new IllegalArgumentException("Table ID cannot be null");
        }
        if (multiplier <= 0) {
            throw new IllegalArgumentException("Multiplier must be positive");
        }

        LootModificationSystem.addModification(tableId,
                LootModification.multiplyDrops(multiplier));

        LOGGER.info("Set multiplier {}x for {}", multiplier, tableId);
    }

    /**
     * Wyłącza loot table (nic nie wypada)
     * @param tableId ID tabeli
     */
    public static void disableLootTable(ResourceLocation tableId) {
        if (tableId == null) {
            throw new IllegalArgumentException("Table ID cannot be null");
        }

        LootModificationSystem.addModification(tableId,
                LootModification.clearTable());

        LOGGER.info("Disabled loot table {}", tableId);
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

        LootModificationSystem.addModification(tableId,
                LootModification.setOnlyDrop(item, minCount, maxCount));

        LOGGER.info("Set {} as only drop in {} (count: {}-{})", item, tableId, minCount, maxCount);
    }

    /**
     * Ustawia tylko jeden przedmiot jako drop (pojedynczy)
     */
    public static void setOnlyDrop(ResourceLocation tableId, Item item) {
        setOnlyDrop(tableId, item, 1, 1);
    }

    // ==================== METODY POMOCNICZE ====================

    /**
     * Czyści wszystkie modyfikacje
     */
    public static void clearAllModifications() {
        LootModificationSystem.clearModifications();
        LOGGER.info("Cleared all loot modifications");
    }

    /**
     * Zwraca liczbę aktywnych modyfikacji
     */
    public static int getModificationCount() {
        return LootModificationSystem.getModificationCount();
    }

    // ==================== WALIDACJA ====================

    private static void validateInputs(ResourceLocation tableId, Item item, String operation) {
        if (tableId == null) {
            throw new IllegalArgumentException(operation + ": Table ID cannot be null");
        }
        if (item == null || item == Items.AIR) {
            throw new IllegalArgumentException(operation + ": Item cannot be null or AIR");
        }
    }

    private static void validateCounts(int minCount, int maxCount) {
        if (minCount <= 0) {
            throw new IllegalArgumentException("Min count must be positive");
        }
        if (maxCount < minCount) {
            throw new IllegalArgumentException("Max count must be >= min count");
        }
        if (maxCount > 64) {
            LOGGER.warn("Max count {} exceeds stack size", maxCount);
        }
    }
}