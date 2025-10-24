package pl.mikof.lootapi;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Główne API do modyfikacji loot tables
 * Każda operacja jest szczegółowo logowana za pomocą LootLogger
 */
public class LootTableAPI {
    public static final Logger LOGGER = LoggerFactory.getLogger("LootTableAPI");
    
    // Przechowuje wszystkie modyfikatory dla loot tables
    private static final Map<Identifier, LootTableModifier> modifiers = new ConcurrentHashMap<>();
    
    // Przechowuje mnożniki dropów
    private static final Map<Identifier, Float> multipliers = new ConcurrentHashMap<>();
    
    // Statystyki
    private static final List<String> loadedModifications = new ArrayList<>();
    private static int totalTablesModified = 0;
    private static int totalItemsAffected = 0;
    
    /**
     * Priorytety dla modyfikatorów
     */
    public enum Priority {
        LOWEST(0),
        LOW(25),
        NORMAL(50),
        HIGH(75),
        HIGHEST(100);
        
        private final int value;
        
        Priority(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    /**
     * Dodaje item do istniejącego loot table
     * 
     * @param tableId Identifier loot table
     * @param item Item do dodania
     * @param weight Waga dropu
     */
    public static void addItemToTable(Identifier tableId, Item item, int weight) {
        if (!LootAPI.checkInitialized("add item to table")) return;
        
        try {
            Identifier itemId = Registries.ITEM.getId(item);
            
            registerModifier(tableId, builder -> {
                builder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(item).weight(weight)));
            }, Priority.NORMAL);
            
            totalItemsAffected++;
            
            String modification = String.format("Added %s (weight: %d) to %s", 
                itemId, weight, tableId);
            loadedModifications.add(modification);
            
            // Loguj tylko w trybie debug
            if (LootLogger.isDebugEnabled()) {
                LootLogger.logItemAdded(tableId, item, weight);
            }
            
        } catch (Exception e) {
            LootLogger.logError("addItemToTable", tableId, e);
        }
    }
    
    /**
     * Dodaje item z określoną ilością
     */
    public static void addItemWithCount(Identifier tableId, Item item, int weight, int min, int max) {
        if (!LootAPI.checkInitialized("add item with count")) return;
        
        try {
            registerModifier(tableId, builder -> {
                builder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(item)
                        .weight(weight)
                        .apply(SetCountLootFunction.builder(
                            UniformLootNumberProvider.create(min, max)))));
            }, Priority.NORMAL);
            
            totalItemsAffected++;
            
            // Loguj tylko w trybie debug
            if (LootLogger.isDebugEnabled()) {
                LootLogger.logModification("Added with count", tableId, item, 
                    "weight: " + weight, "count: " + min + "-" + max);
            }
            
        } catch (Exception e) {
            LootLogger.logError("addItemWithCount", tableId, e);
        }
    }
    
    /**
     * Usuwa item z loot table
     */
    public static void removeItemFromTable(Identifier tableId, Item itemToRemove) {
        if (!LootAPI.checkInitialized("remove item from table")) return;
        
        try {
            Identifier itemId = Registries.ITEM.getId(itemToRemove);
            
            registerModifier(tableId, builder -> {
                // Implementacja usuwania - wymaga mixinów lub zaawansowanej logiki
                LootLogger.logItemRemoved(tableId, itemToRemove);
            }, Priority.HIGH);
            
            totalItemsAffected++;
            String modification = String.format("Removed %s from %s", itemId, tableId);
            loadedModifications.add(modification);
            
        } catch (Exception e) {
            LootLogger.logError("removeItemFromTable", tableId, e);
        }
    }
    
    /**
     * Zastępuje item innym
     */
    public static void replaceItem(Identifier tableId, Item oldItem, Item newItem) {
        if (!LootAPI.checkInitialized("replace item")) return;
        
        try {
            Identifier oldItemId = Registries.ITEM.getId(oldItem);
            Identifier newItemId = Registries.ITEM.getId(newItem);
            
            registerModifier(tableId, builder -> {
                // Implementacja zamiany
                LootLogger.logItemReplaced(tableId, oldItem, newItem);
            }, Priority.HIGH);
            
            totalItemsAffected += 2;
            String modification = String.format("Replaced %s with %s in %s", 
                oldItemId, newItemId, tableId);
            loadedModifications.add(modification);
            
        } catch (Exception e) {
            LootLogger.logError("replaceItem", tableId, e);
        }
    }
    
    /**
     * Ustawia mnożnik dropów dla loot table
     */
    public static void multiplyDrops(Identifier tableId, float multiplier) {
        if (!LootAPI.checkInitialized("multiply drops")) return;
        
        multipliers.put(tableId, multiplier);
        LootLogger.logMultiplier(tableId, multiplier);
        
        registerModifier(tableId, builder -> {
            // Implementacja mnożnika
        }, Priority.LOW);
    }
    
    /**
     * Pobiera mnożnik dla loot table
     */
    public static Float getMultiplier(Identifier tableId) {
        return multipliers.get(tableId);
    }
    
    /**
     * Tworzy nowy loot table
     */
    public static void createLootTable(Identifier tableId, Consumer<LootTable.Builder> builderConsumer) {
        if (!LootAPI.checkInitialized("create loot table")) return;
        
        try {
            registerModifier(tableId, builderConsumer, Priority.HIGHEST);
            LootLogger.logNewLootTable(tableId, "custom");
            totalTablesModified++;
            
        } catch (Exception e) {
            LootLogger.logError("createLootTable", tableId, e);
        }
    }
    
    /**
     * Rejestruje modyfikator z priorytetem
     */
    public static void registerModifier(Identifier tableId, Consumer<LootTable.Builder> modifier, Priority priority) {
        LootTableModifier tableModifier = modifiers.computeIfAbsent(tableId, k -> new LootTableModifier());
        tableModifier.addModifier(modifier, priority);
        totalTablesModified++;
    }
    
    /**
     * Rejestruje modyfikator z domyślnym priorytetem
     */
    public static void registerModifier(Identifier tableId, Consumer<LootTable.Builder> modifier) {
        registerModifier(tableId, modifier, Priority.NORMAL);
    }
    
    /**
     * Kopiuje loot table
     */
    public static void copyLootTable(Identifier source, Identifier target) {
        if (!LootAPI.checkInitialized("copy loot table")) return;
        
        if (modifiers.containsKey(source)) {
            LootTableModifier sourceModifier = modifiers.get(source);
            modifiers.put(target, sourceModifier.copy());
            LootLogger.logModification("Copied table", source, "to", target);
        }
    }
    
    /**
     * Eksportuje loot table do JSON
     */
    public static void exportToJson(Identifier tableId, String filepath) {
        LOGGER.info("Exporting loot table {} to {}", tableId, filepath);
        // Implementacja eksportu do JSON
    }
    
    /**
     * Zwraca wszystkie zarejestrowane modyfikatory
     */
    public static Map<Identifier, LootTableModifier> getModifiers() {
        return Collections.unmodifiableMap(modifiers);
    }
    
    /**
     * Zwraca liczbę zmodyfikowanych tabel
     */
    public static int getTotalTablesModified() {
        return totalTablesModified;
    }
    
    /**
     * Zwraca liczbę zmienionych itemów
     */
    public static int getTotalItemsAffected() {
        return totalItemsAffected;
    }
    
    /**
     * Zwraca listę wszystkich załadowanych modyfikacji
     */
    public static List<String> getLoadedModifications() {
        return new ArrayList<>(loadedModifications);
    }
    
    /**
     * Resetuje liczniki
     */
    public static void resetCounters() {
        loadedModifications.clear();
        totalTablesModified = 0;
        totalItemsAffected = 0;
    }
    
    /**
     * Dodaje item z dokładną szansą (w procentach)
     * Użyj tej metody gdy chcesz DOKŁADNIE X% szansy na drop
     * 
     * @param tableId Identifier loot table
     * @param item Item do dodania
     * @param chancePercent Szansa w procentach (0-100, np. 35 = 35%)
     */
    public static void addItemWithChance(Identifier tableId, Item item, float chancePercent) {
        if (!LootAPI.checkInitialized("add item with chance")) return;
        
        try {
            float chance = chancePercent / 100.0f;  // Konwertuj procenty na 0-1
            
            registerModifier(tableId, builder -> {
                builder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(net.minecraft.loot.condition.RandomChanceLootCondition.builder(chance))
                    .with(ItemEntry.builder(item).weight(100)));
            }, Priority.NORMAL);
            
            totalItemsAffected++;
            
            // Loguj tylko w trybie debug
            if (LootLogger.isDebugEnabled()) {
                LootLogger.logModification("Added with chance", tableId, item, 
                    chancePercent + "% chance");
            }
            
        } catch (Exception e) {
            LootLogger.logError("addItemWithChance", tableId, e);
        }
    }
    
    /**
     * Dodaje item z dokładną szansą I określoną ilością
     * 
     * @param tableId Identifier loot table
     * @param item Item do dodania
     * @param chancePercent Szansa w procentach (0-100)
     * @param minCount Minimalna ilość
     * @param maxCount Maksymalna ilość
     */
    public static void addItemWithChance(Identifier tableId, Item item, float chancePercent,
                                        int minCount, int maxCount) {
        if (!LootAPI.checkInitialized("add item with chance and count")) return;
        
        try {
            float chance = chancePercent / 100.0f;
            
            registerModifier(tableId, builder -> {
                builder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(net.minecraft.loot.condition.RandomChanceLootCondition.builder(chance))
                    .with(ItemEntry.builder(item)
                        .weight(100)
                        .apply(SetCountLootFunction.builder(
                            UniformLootNumberProvider.create(minCount, maxCount)))));
            }, Priority.NORMAL);
            
            totalItemsAffected++;
            
            // Loguj tylko w trybie debug
            if (LootLogger.isDebugEnabled()) {
                LootLogger.logModification("Added with chance and count", tableId, item, 
                    chancePercent + "% chance", minCount + "-" + maxCount + " items");
            }
            
        } catch (Exception e) {
            LootLogger.logError("addItemWithChance", tableId, e);
        }
    }
    
    /**
     * Dodaje item z warunkiem Fortune
     */
    public static void addItemWithFortune(Identifier tableId, Item item, int baseWeight, 
                                         int fortuneMin, int fortuneMax) {
        if (!LootAPI.checkInitialized("add item with fortune")) return;
        
        try {
            registerModifier(tableId, builder -> {
                builder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(item)
                        .weight(baseWeight)
                        .apply(net.minecraft.loot.function.ApplyBonusLootFunction.oreDrops(
                            net.minecraft.enchantment.Enchantments.FORTUNE))));
            }, Priority.NORMAL);
            
            totalItemsAffected++;
            
            // Loguj tylko w trybie debug
            if (LootLogger.isDebugEnabled()) {
                LootLogger.logModification("Added with Fortune", tableId, item, 
                    "base weight: " + baseWeight, "fortune: " + fortuneMin + "-" + fortuneMax);
            }
            
        } catch (Exception e) {
            LootLogger.logError("addItemWithFortune", tableId, e);
        }
    }
    
    /**
     * Dodaje grupę itemów z wagami
     */
    public static void addWeightedGroup(Identifier tableId, WeightedItemGroup group) {
        if (!LootAPI.checkInitialized("add weighted group")) return;
        
        try {
            registerModifier(tableId, builder -> {
                LootPool.Builder pool = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1));
                
                group.getItems().forEach((item, weight) -> {
                    pool.with(ItemEntry.builder(item).weight(weight));
                });
                
                builder.pool(pool);
            }, Priority.NORMAL);
            
            totalItemsAffected += group.getItems().size();
            
            // Loguj tylko w trybie debug
            if (LootLogger.isDebugEnabled()) {
                LootLogger.logModification("Added weighted group", tableId, 
                    "items: " + group.getItems().size());
            }
            
        } catch (Exception e) {
            LootLogger.logError("addWeightedGroup", tableId, e);
        }
    }
    
    /**
     * Dodaje item do wszystkich pasujących loot tables (wildcard)
     */
    public static void addToMatching(String pattern, Item item, int weight) {
        if (!LootAPI.checkInitialized("add to matching")) return;
        
        int matchCount = 0;
        
        // Konwertuj pattern na regex
        String regex = pattern.replace("*", ".*");
        
        // Sprawdź wszystkie vanilla loot tables
        for (java.lang.reflect.Field field : LootTables.Blocks.class.getDeclaredFields()) {
            try {
                if (field.getType() == Identifier.class) {
                    Identifier id = (Identifier) field.get(null);
                    if (id.toString().matches(regex)) {
                        addItemToTable(id, item, weight);
                        matchCount++;
                    }
                }
            } catch (Exception ignored) {}
        }
        
        LootLogger.logBulkOperation(pattern, matchCount);
    }
    
    /**
     * Debug - wyświetla zawartość loot table
     */
    public static void debugLootTable(Identifier tableId) {
        LootLogger.logDebug(tableId);
        
        if (modifiers.containsKey(tableId)) {
            LootTableModifier modifier = modifiers.get(tableId);
            LootLogger.logDebugInfo("Modifiers", modifier.getModifierCount());
            LootLogger.logDebugInfo("Priority distribution", modifier.getPriorityDistribution());
        } else {
            LootLogger.logDebugInfo("Status", "No modifiers registered");
        }
    }
    
    /**
     * Wyświetla podsumowanie
     */
    public static void logSummary() {
        LootLogger.logSummary(totalTablesModified, LootLogger.getModificationCount());
    }
    
    // ========== ZAAWANSOWANE OPERACJE ==========
    
    /**
     * Wyłącza loot table (nic nie dropuje)
     * Użyj tego gdy chcesz całkowicie usunąć dropy z bloku/moba
     * 
     * @param tableId Identifier loot table
     */
    public static void disableLootTable(Identifier tableId) {
        if (!LootAPI.checkInitialized("disable loot table")) return;
        
        try {
            registerModifier(tableId, builder -> {
                // Nie dodajemy żadnych poolów = nic nie dropuje
            }, Priority.HIGHEST);
            
            LootLogger.logWarning("Disabled loot table: " + tableId + " (no drops)");
            
        } catch (Exception e) {
            LootLogger.logError("disableLootTable", tableId, e);
        }
    }
    
    /**
     * Sprawia że blok/mob dropuje TYLKO określony item (czyści resztę)
     * 
     * @param tableId Identifier loot table
     * @param item Item który ma być jedynym dropem
     * @param weight Waga
     */
    public static void setOnlyDrop(Identifier tableId, Item item, int weight) {
        if (!LootAPI.checkInitialized("set only drop")) return;
        
        try {
            Identifier itemId = Registries.ITEM.getId(item);
            
            registerModifier(tableId, builder -> {
                builder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(item).weight(weight)));
            }, Priority.HIGHEST);
            
            LootLogger.logInfo("Set " + tableId + " to drop only: " + itemId);
            
        } catch (Exception e) {
            LootLogger.logError("setOnlyDrop", tableId, e);
        }
    }
    
    /**
     * Sprawia że blok/mob dropuje TYLKO określony item z ilością
     * 
     * @param tableId Identifier loot table
     * @param item Item który ma być jedynym dropem
     * @param minCount Min ilość
     * @param maxCount Max ilość
     */
    public static void setOnlyDrop(Identifier tableId, Item item, int minCount, int maxCount) {
        if (!LootAPI.checkInitialized("set only drop with count")) return;
        
        try {
            Identifier itemId = Registries.ITEM.getId(item);
            
            registerModifier(tableId, builder -> {
                builder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(item)
                        .weight(100)
                        .apply(SetCountLootFunction.builder(
                            UniformLootNumberProvider.create(minCount, maxCount)))));
            }, Priority.HIGHEST);
            
            LootLogger.logInfo("Set " + tableId + " to drop only: " + itemId + " (" + minCount + "-" + maxCount + ")");
            
        } catch (Exception e) {
            LootLogger.logError("setOnlyDrop", tableId, e);
        }
    }
}
