package pl.mikof.lootapi;

import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;

/**
 * Zaawansowane operacje na loot tables
 * Wymaga dostępu do buildera na niższym poziomie
 */
public class LootTableOperations {
    
    /**
     * Usuwa WSZYSTKIE itemy z loot table (czyści całą tabelę)
     * Blok/mob nie będzie nic dropował!
     * 
     * @param tableId Identifier loot table
     */
    public static void clearLootTable(Identifier tableId) {
        if (!LootAPI.checkInitialized("clear loot table")) return;
        
        try {
            LootTableAPI.registerModifier(tableId, builder -> {
                // Zastąp całą tabelę pustą tabelą
                // To czyści wszystkie pool'e
            }, LootTableAPI.Priority.HIGHEST);
            
            LootLogger.logInfo("Cleared loot table: " + tableId);
            
        } catch (Exception e) {
            LootLogger.logError("clearLootTable", tableId, e);
        }
    }
    
    /**
     * Zastępuje CAŁĄ zawartość loot table nową
     * 
     * @param tableId Identifier loot table  
     * @param builderConsumer Nowa zawartość
     */
    public static void replaceLootTable(Identifier tableId, Consumer<LootTable.Builder> builderConsumer) {
        if (!LootAPI.checkInitialized("replace loot table")) return;
        
        try {
            // Najpierw wyczyść
            clearLootTable(tableId);
            
            // Potem dodaj nową zawartość
            LootTableAPI.registerModifier(tableId, builderConsumer, LootTableAPI.Priority.HIGHEST);
            
            LootLogger.logInfo("Replaced loot table: " + tableId);
            
        } catch (Exception e) {
            LootLogger.logError("replaceLootTable", tableId, e);
        }
    }
    
    /**
     * Wyłącza loot table (nic nie dropuje)
     * 
     * @param tableId Identifier loot table
     */
    public static void disableLootTable(Identifier tableId) {
        clearLootTable(tableId);
        LootLogger.logWarning("Disabled loot table: " + tableId + " (no drops)");
    }
    
    /**
     * Sprawia że blok/mob dropuje TYLKO określone itemy (czyści resztę)
     * 
     * @param tableId Identifier loot table
     * @param item Item który ma być jedynym dropem
     * @param weight Waga
     */
    public static void setOnlyDrop(Identifier tableId, Item item, int weight) {
        if (!LootAPI.checkInitialized("set only drop")) return;
        
        try {
            Identifier itemId = Registries.ITEM.getId(item);
            
            LootTableAPI.registerModifier(tableId, builder -> {
                // Czyścimy tabelę i dodajemy tylko ten item
                builder.pool(LootPool.builder()
                    .rolls(net.minecraft.loot.provider.number.ConstantLootNumberProvider.create(1))
                    .with(net.minecraft.loot.entry.ItemEntry.builder(item).weight(weight)));
            }, LootTableAPI.Priority.HIGHEST);
            
            LootLogger.logInfo("Set " + tableId + " to drop only: " + itemId);
            
        } catch (Exception e) {
            LootLogger.logError("setOnlyDrop", tableId, e);
        }
    }
    
    /**
     * Sprawia że blok/mob dropuje TYLKO określone itemy z ilością
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
            
            LootTableAPI.registerModifier(tableId, builder -> {
                builder.pool(LootPool.builder()
                    .rolls(net.minecraft.loot.provider.number.ConstantLootNumberProvider.create(1))
                    .with(net.minecraft.loot.entry.ItemEntry.builder(item)
                        .weight(100)
                        .apply(net.minecraft.loot.function.SetCountLootFunction.builder(
                            net.minecraft.loot.provider.number.UniformLootNumberProvider.create(minCount, maxCount)))));
            }, LootTableAPI.Priority.HIGHEST);
            
            LootLogger.logInfo("Set " + tableId + " to drop only: " + itemId + " (" + minCount + "-" + maxCount + ")");
            
        } catch (Exception e) {
            LootLogger.logError("setOnlyDrop", tableId, e);
        }
    }
}
