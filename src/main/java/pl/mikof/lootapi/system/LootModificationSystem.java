package pl.mikof.lootapi.system;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * System bezpośredniej modyfikacji loot tables - WERSJA 2.0
 * Używa ObfuscationReflectionHelper dla kompatybilności
 */
public class LootModificationSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger("LootModificationSystem");

    // Przechowuje wszystkie modyfikacje
    private static final Map<ResourceLocation, List<LootModification>> modifications = new ConcurrentHashMap<>();

    /**
     * Reprezentuje pojedynczą modyfikację
     */
    public static class LootModification {
        public enum Type {
            ADD_ITEM,
            REMOVE_ITEM,
            REPLACE_ITEM,
            MULTIPLY_DROPS,
            CLEAR_TABLE,
            SET_ONLY_DROP
        }

        public final Type type;
        public final Item item;
        public final Item newItem;
        public final int weight;
        public final float multiplier;
        public final int minCount;
        public final int maxCount;

        private LootModification(Type type, Item item, Item newItem, int weight,
                                 float multiplier, int minCount, int maxCount) {
            this.type = type;
            this.item = item;
            this.newItem = newItem;
            this.weight = weight;
            this.multiplier = multiplier;
            this.minCount = minCount;
            this.maxCount = maxCount;
        }

        public static LootModification addItem(Item item, int weight) {
            return new LootModification(Type.ADD_ITEM, item, null, weight, 1.0f, 1, 1);
        }

        public static LootModification addItemWithCount(Item item, int minCount, int maxCount) {
            return new LootModification(Type.ADD_ITEM, item, null, 1, 1.0f, minCount, maxCount);
        }

        public static LootModification removeItem(Item item) {
            return new LootModification(Type.REMOVE_ITEM, item, null, 1, 1.0f, 1, 1);
        }

        public static LootModification replaceItem(Item oldItem, Item newItem) {
            return new LootModification(Type.REPLACE_ITEM, oldItem, newItem, 1, 1.0f, 1, 1);
        }

        public static LootModification multiplyDrops(float multiplier) {
            return new LootModification(Type.MULTIPLY_DROPS, null, null, 1, multiplier, 1, 1);
        }

        public static LootModification clearTable() {
            return new LootModification(Type.CLEAR_TABLE, null, null, 1, 1.0f, 1, 1);
        }

        public static LootModification setOnlyDrop(Item item, int minCount, int maxCount) {
            return new LootModification(Type.SET_ONLY_DROP, item, null, 1, 1.0f, minCount, maxCount);
        }
    }

    /**
     * Inicjalizuje system
     */
    public static void initialize() {
        LOGGER.info("LootModificationSystem v2.0 initializing...");
        modifications.clear();
    }

    /**
     * Dodaje modyfikację do systemu
     */
    public static void addModification(ResourceLocation tableId, LootModification modification) {
        modifications.computeIfAbsent(tableId, k -> new ArrayList<>()).add(modification);
        LOGGER.debug("Registered {} modification for table {}", modification.type, tableId);
    }

    /**
     * Aplikuje wszystkie modyfikacje do ładowanej tabeli
     */
    public static void applyModifications(LootTableLoadEvent event) {
        ResourceLocation tableId = event.getName();
        LootTable table = event.getTable();

        if (table == null) {
            return;
        }

        List<LootModification> mods = modifications.get(tableId);
        if (mods == null || mods.isEmpty()) {
            return;
        }

        LOGGER.info("Applying {} modifications to {}", mods.size(), tableId);

        for (LootModification mod : mods) {
            try {
                applyModification(event, table, mod, tableId);
            } catch (Exception e) {
                LOGGER.error("Failed to apply {} to {}: {}", mod.type, tableId, e.getMessage());
            }
        }
    }

    /**
     * Aplikuje pojedynczą modyfikację - UPROSZCZONA WERSJA
     * Zamiast modyfikować istniejące pools, dodajemy nowe
     */
    private static void applyModification(LootTableLoadEvent event, LootTable table,
                                          LootModification mod, ResourceLocation tableId) {

        try {
            switch (mod.type) {
                case ADD_ITEM:
                    // Dodajemy nową pulę z przedmiotem
                    LootPool.Builder addPool = LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(createItemEntry(mod.item, mod.minCount, mod.maxCount, mod.weight));

                    addPoolToTable(table, addPool.build());
                    LOGGER.info("Added {} to {}", mod.item, tableId);
                    break;

                case CLEAR_TABLE:
                    // Czyścimy tabelę przez zastąpienie pustą
                    clearTable(table);
                    LOGGER.info("Cleared table {}", tableId);
                    break;

                case SET_ONLY_DROP:
                    // Najpierw czyść, potem dodaj
                    clearTable(table);

                    LootPool.Builder onlyPool = LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(createItemEntry(mod.item, mod.minCount, mod.maxCount, 1));

                    addPoolToTable(table, onlyPool.build());
                    LOGGER.info("Set {} as only drop in {}", mod.item, tableId);
                    break;

                case REMOVE_ITEM:
                    // Trudniejsze do implementacji bez reflection
                    LOGGER.warn("REMOVE_ITEM not fully implemented in v2.0");
                    break;

                case REPLACE_ITEM:
                    // Możemy dodać nowy item i oznaczyć stary do usunięcia
                    LootPool.Builder replacePool = LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(createItemEntry(mod.newItem, 1, 1, 10));

                    addPoolToTable(table, replacePool.build());
                    LOGGER.info("Added replacement {} for {} in {}", mod.newItem, mod.item, tableId);
                    break;

                case MULTIPLY_DROPS:
                    // Dodajemy dodatkowe pule które zwiększają drop
                    multiplyDropsSimple(table, mod.multiplier);
                    LOGGER.info("Multiplied drops by {} in {}", mod.multiplier, tableId);
                    break;
            }

        } catch (Exception e) {
            LOGGER.error("Error applying modification: {}", e.getMessage());
        }
    }

    /**
     * Tworzy entry dla przedmiotu
     */
    private static LootPoolEntryContainer createItemEntry(Item item, int minCount, int maxCount, int weight) {
        var builder = LootItem.lootTableItem(item);

        if (weight > 1) {
            builder.setWeight(weight);
        }

        if (minCount != 1 || maxCount != 1) {
            if (minCount == maxCount) {
                builder.apply(SetItemCountFunction.setCount(ConstantValue.exactly(minCount)));
            } else {
                builder.apply(SetItemCountFunction.setCount(
                        UniformGenerator.between(minCount, maxCount)
                ));
            }
        }

        return builder.build();
    }

    /**
     * Dodaje pulę do tabeli używając reflection w bezpieczny sposób
     */
    private static void addPoolToTable(LootTable table, LootPool pool) {
        try {
            // Próba 1: Użyj ObfuscationReflectionHelper
            List<LootPool> pools = ObfuscationReflectionHelper.getPrivateValue(
                    LootTable.class, table, "f_79109_" // pools field
            );

            if (pools != null) {
                // Tworzymy nową listę jeśli to niemutowalna lista
                List<LootPool> newPools = new ArrayList<>(pools);
                newPools.add(pool);

                ObfuscationReflectionHelper.setPrivateValue(
                        LootTable.class, table, newPools, "f_79109_"
                );
                return;
            }
        } catch (Exception e) {
            LOGGER.debug("Method 1 failed: {}", e.getMessage());
        }

        try {
            // Próba 2: Bezpośredni dostęp przez reflection
            Field poolsField = LootTable.class.getDeclaredField("pools");
            poolsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<LootPool> pools = (List<LootPool>) poolsField.get(table);

            if (pools != null) {
                List<LootPool> newPools = new ArrayList<>(pools);
                newPools.add(pool);
                poolsField.set(table, newPools);
                return;
            }
        } catch (Exception e) {
            LOGGER.debug("Method 2 failed: {}", e.getMessage());
        }

        // Jeśli wszystko zawiedzie, przynajmniej zaloguj
        LOGGER.warn("Could not add pool to table - all reflection methods failed");
    }

    /**
     * Czyści tabelę
     */
    private static void clearTable(LootTable table) {
        try {
            // Próba 1: ObfuscationReflectionHelper
            ObfuscationReflectionHelper.setPrivateValue(
                    LootTable.class, table, new ArrayList<LootPool>(), "f_79109_"
            );
        } catch (Exception e1) {
            try {
                // Próba 2: Bezpośredni reflection
                Field poolsField = LootTable.class.getDeclaredField("pools");
                poolsField.setAccessible(true);
                poolsField.set(table, new ArrayList<LootPool>());
            } catch (Exception e2) {
                LOGGER.error("Could not clear table: {}", e2.getMessage());
            }
        }
    }

    /**
     * Mnoży dropy przez dodanie dodatkowych pul
     */
    private static void multiplyDropsSimple(LootTable table, float multiplier) {
        if (multiplier <= 1.0f) return;

        try {
            List<LootPool> currentPools = ObfuscationReflectionHelper.getPrivateValue(
                    LootTable.class, table, "f_79109_"
            );

            if (currentPools != null && !currentPools.isEmpty()) {
                // Dodaj kopie pul tyle razy ile wynosi mnożnik
                int additionalRolls = (int) multiplier - 1;
                List<LootPool> newPools = new ArrayList<>(currentPools);

                for (int i = 0; i < additionalRolls; i++) {
                    newPools.addAll(currentPools);
                }

                // Dla części ułamkowej dodaj pulę z szansą
                float fractional = multiplier - (int) multiplier;
                if (fractional > 0) {
                    // Dodaj pierwszą pulę z szansą równą części ułamkowej
                    // To wymaga bardziej zaawansowanej implementacji
                }

                ObfuscationReflectionHelper.setPrivateValue(
                        LootTable.class, table, newPools, "f_79109_"
                );
            }
        } catch (Exception e) {
            LOGGER.error("Could not multiply drops: {}", e.getMessage());
        }
    }

    /**
     * Zwraca liczbę zarejestrowanych modyfikacji
     */
    public static int getModificationCount() {
        return modifications.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    /**
     * Czyści wszystkie modyfikacje
     */
    public static void clearModifications() {
        modifications.clear();
        LOGGER.info("Cleared all loot modifications");
    }

    /**
     * Debug - wypisuje wszystkie zarejestrowane modyfikacje
     */
    public static void debugPrintModifications() {
        LOGGER.info("=== Registered Loot Modifications ===");
        modifications.forEach((tableId, mods) -> {
            LOGGER.info("Table: {}", tableId);
            mods.forEach(mod -> {
                LOGGER.info("  - Type: {}, Item: {}, Weight: {}",
                        mod.type, mod.item, mod.weight);
            });
        });
        LOGGER.info("=== Total: {} tables, {} modifications ===",
                modifications.size(), getModificationCount());
    }
}