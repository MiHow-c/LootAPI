package pl.mikof.lootapi.system;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.LootAPI;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * System bezpośredniej modyfikacji loot tables
 * Używa reflection i event handling zamiast GLM
 */
public class LootModificationSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger("LootModificationSystem");

    // Przechowuje wszystkie modyfikacje
    private static final Map<ResourceLocation, List<LootModification>> modifications = new ConcurrentHashMap<>();

    // Cache dla reflection
    private static Field poolsField;
    private static Field entriesField;
    private static Field compositeEntriesField;

    static {
        try {
            // Przygotuj reflection fields
            poolsField = LootTable.class.getDeclaredField("pools");
            poolsField.setAccessible(true);

            entriesField = LootPool.class.getDeclaredField("entries");
            entriesField.setAccessible(true);

            // Dla composite entries (alternatywna nazwa pola)
            try {
                compositeEntriesField = LootPool.class.getDeclaredField("compositeEntries");
                compositeEntriesField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                // Pole może nie istnieć w niektórych wersjach
                compositeEntriesField = null;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize reflection fields", e);
        }
    }

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
        public final float chance;

        private LootModification(Type type) {
            this.type = type;
            this.item = null;
            this.newItem = null;
            this.weight = 1;
            this.multiplier = 1.0f;
            this.minCount = 1;
            this.maxCount = 1;
            this.chance = 1.0f;
        }

        public static LootModification addItem(Item item, int weight) {
            LootModification mod = new LootModification(Type.ADD_ITEM);
            return new LootModification(Type.ADD_ITEM) {
                public final Item item = item;
                public final int weight = weight;
            };
        }

        public static LootModification addItemWithCount(Item item, int minCount, int maxCount) {
            LootModification mod = new LootModification(Type.ADD_ITEM);
            return new LootModification(Type.ADD_ITEM) {
                public final Item item = item;
                public final int minCount = minCount;
                public final int maxCount = maxCount;
            };
        }

        public static LootModification removeItem(Item item) {
            LootModification mod = new LootModification(Type.REMOVE_ITEM);
            return new LootModification(Type.REMOVE_ITEM) {
                public final Item item = item;
            };
        }

        public static LootModification replaceItem(Item oldItem, Item newItem) {
            LootModification mod = new LootModification(Type.REPLACE_ITEM);
            return new LootModification(Type.REPLACE_ITEM) {
                public final Item item = oldItem;
                public final Item newItem = newItem;
            };
        }

        public static LootModification multiplyDrops(float multiplier) {
            return new LootModification(Type.MULTIPLY_DROPS) {
                public final float multiplier = multiplier;
            };
        }

        public static LootModification clearTable() {
            return new LootModification(Type.CLEAR_TABLE);
        }

        public static LootModification setOnlyDrop(Item item, int minCount, int maxCount) {
            return new LootModification(Type.SET_ONLY_DROP) {
                public final Item item = item;
                public final int minCount = minCount;
                public final int maxCount = maxCount;
            };
        }
    }

    /**
     * Inicjalizuje system
     */
    public static void initialize() {
        LOGGER.info("LootModificationSystem initializing...");
        modifications.clear();
    }

    /**
     * Dodaje modyfikację do systemu
     */
    public static void addModification(ResourceLocation tableId, LootModification modification) {
        modifications.computeIfAbsent(tableId, k -> new ArrayList<>()).add(modification);
        LOGGER.debug("Added {} modification for table {}", modification.type, tableId);
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

        LOGGER.debug("Applying {} modifications to table {}", mods.size(), tableId);

        try {
            for (LootModification mod : mods) {
                applyModification(table, mod, tableId);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to apply modifications to table {}", tableId, e);
        }
    }

    /**
     * Aplikuje pojedynczą modyfikację
     */
    @SuppressWarnings("unchecked")
    private static void applyModification(LootTable table, LootModification mod, ResourceLocation tableId) {
        try {
            List<LootPool> pools = (List<LootPool>) poolsField.get(table);

            if (pools == null || pools.isEmpty()) {
                // Jeśli nie ma puli, stwórz nową
                if (mod.type == LootModification.Type.ADD_ITEM ||
                        mod.type == LootModification.Type.SET_ONLY_DROP) {
                    pools = new ArrayList<>();
                    pools.add(createNewPool(mod));
                    poolsField.set(table, pools);
                    LOGGER.debug("Created new pool for table {}", tableId);
                }
                return;
            }

            switch (mod.type) {
                case ADD_ITEM:
                    addItemToPool(pools.get(0), mod.item, mod.minCount, mod.maxCount, mod.weight);
                    LOGGER.debug("Added {} to {}", mod.item, tableId);
                    break;

                case REMOVE_ITEM:
                    removeItemFromPools(pools, mod.item);
                    LOGGER.debug("Removed {} from {}", mod.item, tableId);
                    break;

                case REPLACE_ITEM:
                    replaceItemInPools(pools, mod.item, mod.newItem);
                    LOGGER.debug("Replaced {} with {} in {}", mod.item, mod.newItem, tableId);
                    break;

                case MULTIPLY_DROPS:
                    multiplyDropsInPools(pools, mod.multiplier);
                    LOGGER.debug("Multiplied drops by {} in {}", mod.multiplier, tableId);
                    break;

                case CLEAR_TABLE:
                    pools.clear();
                    LOGGER.debug("Cleared table {}", tableId);
                    break;

                case SET_ONLY_DROP:
                    pools.clear();
                    pools.add(createPoolForItem(mod.item, mod.minCount, mod.maxCount));
                    LOGGER.debug("Set {} as only drop in {}", mod.item, tableId);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to apply {} modification to {}", mod.type, tableId, e);
        }
    }

    /**
     * Tworzy nową pulę dla przedmiotu
     */
    private static LootPool createNewPool(LootModification mod) {
        return createPoolForItem(mod.item, mod.minCount, mod.maxCount);
    }

    /**
     * Tworzy pulę z pojedynczym przedmiotem
     */
    private static LootPool createPoolForItem(Item item, int minCount, int maxCount) {
        LootPool.Builder poolBuilder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1));

        LootPoolSingletonContainer.Builder<?> entryBuilder = LootItem.lootTableItem(item);

        if (minCount != 1 || maxCount != 1) {
            if (minCount == maxCount) {
                entryBuilder.apply(SetItemCountFunction.setCount(ConstantValue.exactly(minCount)));
            } else {
                entryBuilder.apply(SetItemCountFunction.setCount(UniformGenerator.between(minCount, maxCount)));
            }
        }

        poolBuilder.add(entryBuilder);
        return poolBuilder.build();
    }

    /**
     * Dodaje przedmiot do puli
     */
    private static void addItemToPool(LootPool pool, Item item, int minCount, int maxCount, int weight) {
        try {
            // Pobierz obecne entries
            Field field = entriesField != null ? entriesField : compositeEntriesField;
            if (field == null) {
                LOGGER.error("Cannot find entries field in LootPool");
                return;
            }

            List<LootPoolEntryContainer> entries = (List<LootPoolEntryContainer>) field.get(pool);
            if (entries == null) {
                entries = new ArrayList<>();
            }

            // Stwórz nowy entry
            LootPoolSingletonContainer.Builder<?> entryBuilder = LootItem.lootTableItem(item)
                    .setWeight(weight);

            if (minCount != 1 || maxCount != 1) {
                if (minCount == maxCount) {
                    entryBuilder.apply(SetItemCountFunction.setCount(ConstantValue.exactly(minCount)));
                } else {
                    entryBuilder.apply(SetItemCountFunction.setCount(UniformGenerator.between(minCount, maxCount)));
                }
            }

            // Dodaj do listy
            List<LootPoolEntryContainer> newEntries = new ArrayList<>(entries);
            newEntries.add(entryBuilder.build());
            field.set(pool, newEntries);

        } catch (Exception e) {
            LOGGER.error("Failed to add item to pool", e);
        }
    }

    /**
     * Usuwa przedmiot ze wszystkich puli
     */
    @SuppressWarnings("unchecked")
    private static void removeItemFromPools(List<LootPool> pools, Item itemToRemove) {
        for (LootPool pool : pools) {
            try {
                Field field = entriesField != null ? entriesField : compositeEntriesField;
                if (field == null) continue;

                List<LootPoolEntryContainer> entries = (List<LootPoolEntryContainer>) field.get(pool);
                if (entries == null) continue;

                // Filtruj entries
                List<LootPoolEntryContainer> filtered = new ArrayList<>();
                for (LootPoolEntryContainer entry : entries) {
                    if (!isEntryForItem(entry, itemToRemove)) {
                        filtered.add(entry);
                    }
                }

                field.set(pool, filtered);
            } catch (Exception e) {
                LOGGER.error("Failed to remove item from pool", e);
            }
        }
    }

    /**
     * Zastępuje przedmiot w pulach
     */
    @SuppressWarnings("unchecked")
    private static void replaceItemInPools(List<LootPool> pools, Item oldItem, Item newItem) {
        for (LootPool pool : pools) {
            try {
                Field field = entriesField != null ? entriesField : compositeEntriesField;
                if (field == null) continue;

                List<LootPoolEntryContainer> entries = (List<LootPoolEntryContainer>) field.get(pool);
                if (entries == null) continue;

                List<LootPoolEntryContainer> newEntries = new ArrayList<>();
                for (LootPoolEntryContainer entry : entries) {
                    if (isEntryForItem(entry, oldItem)) {
                        // Zastąp przedmiot
                        newEntries.add(LootItem.lootTableItem(newItem).build());
                    } else {
                        newEntries.add(entry);
                    }
                }

                field.set(pool, newEntries);
            } catch (Exception e) {
                LOGGER.error("Failed to replace item in pool", e);
            }
        }
    }

    /**
     * Mnoży ilość dropów w pulach
     */
    private static void multiplyDropsInPools(List<LootPool> pools, float multiplier) {
        for (LootPool pool : pools) {
            try {
                // Modyfikuj rolls count
                Field rollsField = LootPool.class.getDeclaredField("rolls");
                rollsField.setAccessible(true);

                Object rolls = rollsField.get(pool);
                if (rolls instanceof ConstantValue) {
                    float value = ((ConstantValue) rolls).value();
                    rollsField.set(pool, ConstantValue.exactly(Math.round(value * multiplier)));
                }
            } catch (Exception e) {
                LOGGER.error("Failed to multiply drops in pool", e);
            }
        }
    }

    /**
     * Sprawdza czy entry jest dla danego przedmiotu
     */
    private static boolean isEntryForItem(LootPoolEntryContainer entry, Item item) {
        if (entry instanceof LootItem) {
            try {
                Field itemField = LootItem.class.getDeclaredField("item");
                itemField.setAccessible(true);
                Item entryItem = (Item) itemField.get(entry);
                return entryItem == item;
            } catch (Exception e) {
                LOGGER.error("Failed to check entry item", e);
            }
        }
        return false;
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
}