package pl.mikof.lootapi;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import pl.mikof.lootapi.glm.GLMHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Main API for Loot Table manipulation on NeoForge
 * IMPROVED VERSION with thread safety and validation
 */
public class LootTableAPI {

    // Track registered modifiers for management (thread-safe)
    private static final Map<ResourceLocation, List<ModifierInfo>> registeredModifiers = new ConcurrentHashMap<>();
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    private static int totalModifiersRegistered = 0;
    private static boolean apiEnabled = true;

    /**
     * Priority for modifier registration
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

    // ========== API CONTROL ==========

    /**
     * Enables or disables the API
     */
    public static void setEnabled(boolean enabled) {
        lock.writeLock().lock();
        try {
            apiEnabled = enabled;
            LootLogger.logInfo("Loot API " + (enabled ? "enabled" : "disabled"));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Checks if the API is enabled
     */
    public static boolean isEnabled() {
        lock.readLock().lock();
        try {
            return apiEnabled;
        } finally {
            lock.readLock().unlock();
        }
    }

    // ========== ADD ITEM METHODS ==========

    /**
     * Adds an item to a loot table
     * @param tableId The loot table to modify
     * @param item The item to add (must not be AIR)
     * @param weight The weight (1-1000 recommended)
     */
    public static void addItemToTable(ResourceLocation tableId, Item item, int weight) {
        if (!validateBasicInputs(tableId, item, "addItemToTable")) return;
        if (!validateWeight(weight)) return;

        lock.writeLock().lock();
        try {
            GLMHelper.registerAddItem(tableId, item, weight);

            trackModifier(tableId, "add_item", item, "weight: " + weight);
            totalModifiersRegistered++;

            if (LootLogger.isDebugEnabled()) {
                LootLogger.logItemAdded(tableId, item, weight);
            }

        } catch (Exception e) {
            LootLogger.logError("addItemToTable", tableId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Adds an item with count range
     * @param tableId The loot table to modify
     * @param item The item to add
     * @param weight The weight for the drop
     * @param minCount Minimum items to drop (must be positive)
     * @param maxCount Maximum items to drop (must be >= minCount)
     */
    public static void addItemWithCount(ResourceLocation tableId, Item item, int weight, int minCount, int maxCount) {
        if (!validateBasicInputs(tableId, item, "addItemWithCount")) return;
        if (!validateWeight(weight)) return;
        if (!validateCounts(minCount, maxCount)) return;

        lock.writeLock().lock();
        try {
            GLMHelper.registerAddItemWithCount(tableId, item, minCount, maxCount);

            trackModifier(tableId, "add_item_with_count", item,
                    "count: " + minCount + "-" + maxCount);
            totalModifiersRegistered++;

            if (LootLogger.isDebugEnabled()) {
                LootLogger.logModification("Added with count", tableId, item,
                        "count: " + minCount + "-" + maxCount);
            }

        } catch (Exception e) {
            LootLogger.logError("addItemWithCount", tableId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Adds an item with specific chance (percentage)
     * @param tableId The loot table to modify
     * @param item The item to add
     * @param chancePercent The chance in percent (0.01 to 100)
     */
    public static void addItemWithChance(ResourceLocation tableId, Item item, float chancePercent) {
        if (!validateBasicInputs(tableId, item, "addItemWithChance")) return;
        if (!validateChance(chancePercent)) return;

        lock.writeLock().lock();
        try {
            GLMHelper.registerAddItemWithChance(tableId, item, chancePercent);

            trackModifier(tableId, "add_item_with_chance", item, chancePercent + "%");
            totalModifiersRegistered++;

            if (LootLogger.isDebugEnabled()) {
                LootLogger.logModification("Added with chance", tableId, item, chancePercent + "%");
            }

        } catch (Exception e) {
            LootLogger.logError("addItemWithChance", tableId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ========== REMOVE ITEM ==========

    /**
     * Removes an item from a loot table
     * @param tableId The loot table to modify
     * @param itemToRemove The item to remove
     */
    public static void removeItemFromTable(ResourceLocation tableId, Item itemToRemove) {
        if (!validateBasicInputs(tableId, itemToRemove, "removeItemFromTable")) return;

        lock.writeLock().lock();
        try {
            GLMHelper.registerRemoveItem(tableId, itemToRemove);

            trackModifier(tableId, "remove_item", itemToRemove);
            totalModifiersRegistered++;

            LootLogger.logItemRemoved(tableId, itemToRemove);

        } catch (Exception e) {
            LootLogger.logError("removeItemFromTable", tableId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ========== REPLACE ITEM ==========

    /**
     * Replaces one item with another
     * @param tableId The loot table to modify
     * @param oldItem The item to replace
     * @param newItem The replacement item
     */
    public static void replaceItem(ResourceLocation tableId, Item oldItem, Item newItem) {
        if (!validateBasicInputs(tableId, oldItem, "replaceItem (old)")) return;
        if (!validateBasicInputs(tableId, newItem, "replaceItem (new)")) return;

        lock.writeLock().lock();
        try {
            GLMHelper.registerReplaceItem(tableId, oldItem, newItem, true);

            trackModifier(tableId, "replace_item", oldItem, "→", newItem);
            totalModifiersRegistered++;

            LootLogger.logItemReplaced(tableId, oldItem, newItem);

        } catch (Exception e) {
            LootLogger.logError("replaceItem", tableId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ========== MULTIPLY DROPS ==========

    /**
     * Multiplies all drops from a loot table
     * @param tableId The loot table to modify
     * @param multiplier The multiplier (must be positive, 0.5 = half, 2.0 = double)
     */
    public static void multiplyDrops(ResourceLocation tableId, float multiplier) {
        if (!validateTableId(tableId, "multiplyDrops")) return;
        if (!validateMultiplier(multiplier)) return;

        lock.writeLock().lock();
        try {
            GLMHelper.registerMultiplyDrops(tableId, multiplier);

            trackModifier(tableId, "multiply_drops", multiplier + "x");
            totalModifiersRegistered++;

            LootLogger.logMultiplier(tableId, multiplier);

        } catch (Exception e) {
            LootLogger.logError("multiplyDrops", tableId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ========== ADVANCED OPERATIONS ==========

    /**
     * Disables a loot table (no drops)
     * @param tableId The loot table to disable
     */
    public static void disableLootTable(ResourceLocation tableId) {
        if (!validateTableId(tableId, "disableLootTable")) return;

        lock.writeLock().lock();
        try {
            GLMHelper.registerClearLootTable(tableId);

            trackModifier(tableId, "clear");
            totalModifiersRegistered++;

            LootLogger.logWarning("Disabled loot table: " + tableId + " (no drops)");

        } catch (Exception e) {
            LootLogger.logError("disableLootTable", tableId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Sets only one specific drop (clears everything else)
     * @param tableId The loot table to modify
     * @param item The only item that should drop
     * @param weight Not used in this version, kept for compatibility
     */
    public static void setOnlyDrop(ResourceLocation tableId, Item item, int weight) {
        setOnlyDrop(tableId, item, 1, 1);
    }

    /**
     * Sets only one specific drop with count range
     * @param tableId The loot table to modify
     * @param item The only item that should drop
     * @param minCount Minimum count
     * @param maxCount Maximum count
     */
    public static void setOnlyDrop(ResourceLocation tableId, Item item, int minCount, int maxCount) {
        if (!validateBasicInputs(tableId, item, "setOnlyDrop")) return;
        if (!validateCounts(minCount, maxCount)) return;

        lock.writeLock().lock();
        try {
            GLMHelper.registerSetOnlyDrop(tableId, item, minCount, maxCount);

            trackModifier(tableId, "set_only", item);
            totalModifiersRegistered++;

            LootLogger.logInfo("Set " + tableId + " to drop only: " + BuiltInRegistries.ITEM.getKey(item));

        } catch (Exception e) {
            LootLogger.logError("setOnlyDrop", tableId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ========== VALIDATION METHODS ==========

    private static boolean validateBasicInputs(ResourceLocation tableId, Item item, String operation) {
        if (!apiEnabled) {
            LootLogger.logWarning("Loot API is disabled. Operation ignored: " + operation);
            return false;
        }

        if (!validateTableId(tableId, operation)) {
            return false;
        }

        if (item == null || item == Items.AIR) {
            LootLogger.logError(operation, tableId,
                    new IllegalArgumentException("Item cannot be null or AIR"));
            return false;
        }

        return true;
    }

    private static boolean validateTableId(ResourceLocation tableId, String operation) {
        if (!apiEnabled) {
            LootLogger.logWarning("Loot API is disabled. Operation ignored: " + operation);
            return false;
        }

        if (tableId == null) {
            LootLogger.logError(operation, ResourceLocation.withDefaultNamespace("unknown"),
                    new IllegalArgumentException("Table ID cannot be null"));
            return false;
        }

        return true;
    }

    private static boolean validateWeight(int weight) {
        if (weight <= 0) {
            LootLogger.logWarning("Invalid weight: " + weight + ". Must be positive. Using default: 1");
            return true; // Continue with default
        }

        if (weight > 10000) {
            LootLogger.logWarning("Weight " + weight + " is very high. Consider using values < 1000");
        }

        return true;
    }

    private static boolean validateCounts(int minCount, int maxCount) {
        if (minCount <= 0) {
            LootLogger.logWarning("Invalid minCount: " + minCount + ". Must be positive");
            return false;
        }

        if (maxCount < minCount) {
            LootLogger.logWarning("Invalid count range: " + minCount + "-" + maxCount +
                    ". Max must be >= min");
            return false;
        }

        if (maxCount > 64) {
            LootLogger.logWarning("maxCount " + maxCount + " exceeds stack size. May cause issues");
        }

        return true;
    }

    private static boolean validateChance(float chance) {
        if (chance <= 0 || chance > 100) {
            LootLogger.logWarning("Invalid chance: " + chance + "%. Must be between 0 and 100");
            return false;
        }

        return true;
    }

    private static boolean validateMultiplier(float multiplier) {
        if (multiplier <= 0) {
            LootLogger.logWarning("Invalid multiplier: " + multiplier + ". Must be positive");
            return false;
        }

        if (multiplier > 100) {
            LootLogger.logWarning("Multiplier " + multiplier + " is very high. May cause issues");
        }

        return true;
    }

    // ========== UTILITY METHODS ==========

    private static void trackModifier(ResourceLocation tableId, String operation, Object... details) {
        registeredModifiers.computeIfAbsent(tableId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new ModifierInfo(operation, details));
    }

    public static int getTotalModifiersRegistered() {
        lock.readLock().lock();
        try {
            return totalModifiersRegistered;
        } finally {
            lock.readLock().unlock();
        }
    }

    public static Map<ResourceLocation, List<ModifierInfo>> getRegisteredModifiers() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableMap(new HashMap<>(registeredModifiers));
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void clearAllModifications() {
        lock.writeLock().lock();
        try {
            if (GLMHelper.isFinalized()) {
                LootLogger.logWarning("Cannot clear modifications after finalization!");
                return;
            }

            registeredModifiers.clear();
            totalModifiersRegistered = 0;
            GLMHelper.clearRegistrations();
            LootLogger.logWarning("Cleared all modifications");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Finalizes all modifications and creates the global modifiers file
     * MUST be called after all modifications are registered!
     */
    public static void finalizeModifications() {
        lock.writeLock().lock();
        try {
            if (GLMHelper.isFinalized()) {
                LootLogger.logWarning("Modifications already finalized!");
                return;
            }

            GLMHelper.createGlobalModifiersFile();

            int tablesModified = registeredModifiers.size();
            int totalModifiers = GLMHelper.getModifierCount();

            LootLogger.logSummary(tablesModified, totalModifiers);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ========== INFO CLASS ==========

    public static class ModifierInfo {
        public final String operation;
        public final Object[] details;
        public final long timestamp;

        public ModifierInfo(String operation, Object... details) {
            this.operation = operation;
            this.details = details;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return operation + ": " + Arrays.toString(details);
        }
    }
}