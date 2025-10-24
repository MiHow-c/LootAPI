package pl.mikof.lootapi.event;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.util.Identifier;
import pl.mikof.lootapi.LootLogger;
import pl.mikof.lootapi.LootTableAPI;
import pl.mikof.lootapi.LootTableModifier;

import java.util.*;

/**
 * Event handler dla modyfikacji loot tables
 * Używa Fabric API zamiast mixinów
 */
public class LootTableEventHandler {
    
    private static boolean registered = false;
    private static int totalTablesProcessed = 0;
    private static int totalModifiersApplied = 0;
    
    public static void register() {
        if (registered) {
            return;
        }
        
        LootLogger.logInfo("Registering loot table event handler...");
        
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            Map<Identifier, LootTableModifier> modifiers = LootTableAPI.getModifiers();
            
            if (modifiers.containsKey(id)) {
                LootTableModifier modifier = modifiers.get(id);
                
                if (modifier.hasModifiers()) {
                    try {
                        modifier.apply(tableBuilder);
                        totalTablesProcessed++;
                        totalModifiersApplied += modifier.getModifierCount();
                        
                        // Loguj tylko w trybie debug (zamiast zawsze)
                        if (LootLogger.isDebugEnabled()) {
                            LootLogger.logTableModified(id, modifier.getModifierCount());
                        }
                    } catch (Exception e) {
                        LootLogger.logError("apply modifiers", id, e);
                    }
                }
            }
        });
        
        registered = true;
        LootLogger.logSuccess("Loot table event handler registered!");
    }
    
    /**
     * Wyświetla podsumowanie przetworzonych tabel
     */
    public static void logSummary() {
        if (totalTablesProcessed > 0) {
            LootLogger.logInfo("Processed " + totalTablesProcessed + " loot tables with " + 
                             totalModifiersApplied + " total modifiers");
        }
    }
    
    public static int getTotalTablesProcessed() {
        return totalTablesProcessed;
    }
    
    public static int getTotalModifiersApplied() {
        return totalModifiersApplied;
    }
}
