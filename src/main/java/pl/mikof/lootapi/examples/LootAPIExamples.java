package pl.mikof.lootapi.examples;

import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import pl.mikof.lootapi.*;

/**
 * Przykłady użycia wszystkich funkcji Loot API
 * WAŻNE: Te przykłady są wywoływane automatycznie przez LootAPI.onInitialize()
 */
public class LootAPIExamples {
    
    public static void registerExamples() {
        
        LootLogger.logInfo("Registering example modifications...");
        LootLogger.logEmptyLine();
        
        // ========== BLOKI - ORE DROPS ==========
        registerOreDrops();
        
        // ========== MOBY - ENTITY DROPS ==========
        registerEntityDrops();
        
        // ========== SKRZYNIE - CHEST LOOT ==========
        registerChestLoot();
        
        // ========== PODSUMOWANIE ==========
        showSummary();
    }
    
    private static void registerOreDrops() {
        LootLogger.logInfo("▸ ORE DROPS:");
        
        // 35% szansy na diament z iron ore
        LootTableAPI.addItemWithChance(
            new Identifier("minecraft", "blocks/iron_ore"),
            Items.DIAMOND,
            35
        );
        
        // Coal ore dropuje diamenty z Fortune
        LootTableAPI.addItemWithFortune(
            new Identifier("minecraft", "blocks/coal_ore"),
            Items.DIAMOND, 
            70, 1, 2
        );
        
        LootLogger.logInfo("  ✓ 2 ore modifications registered");
        LootLogger.logEmptyLine();
    }
    
    private static void registerEntityDrops() {
        LootLogger.logInfo("▸ ENTITY DROPS:");
        
        // Zombie: 50% szansy na 2-5 złota
        LootTableAPI.addItemWithChance(
            new Identifier("minecraft", "entities/zombie"),
            Items.GOLD_INGOT,
            50, 2, 5
        );
        
        // Creeper: 10% szansy na TNT
        LootTableAPI.addItemWithChance(
            new Identifier("minecraft", "entities/creeper"),
            Items.TNT,
            10
        );
        
        // Skeleton: 2x drop multiplier
        LootTableAPI.multiplyDrops(
            new Identifier("minecraft", "entities/skeleton"),
            2.0f
        );
        
        LootLogger.logInfo("  ✓ 3 entity modifications registered");
        LootLogger.logEmptyLine();
    }
    
    private static void registerChestLoot() {
        LootLogger.logInfo("▸ CHEST LOOT:");
        
        // End City treasure - grupa itemów
        WeightedItemGroup treasureGroup = new WeightedItemGroup()
            .add(Items.DIAMOND, 30)
            .add(Items.EMERALD, 20)
            .add(Items.NETHERITE_INGOT, 5);
        
        LootTableAPI.addWeightedGroup(
            new Identifier("minecraft", "chests/end_city_treasure"), 
            treasureGroup
        );
        
        LootLogger.logInfo("  ✓ 1 chest modification registered");
        LootLogger.logEmptyLine();
    }
    
    private static void showSummary() {
        LootLogger.logSeparator();
        LootLogger.logInfo("SUMMARY:");
        LootLogger.logInfo("  • Iron ore → 35% diamonds");
        LootLogger.logInfo("  • Coal ore → diamonds with Fortune");
        LootLogger.logInfo("  • Zombie → 50% for 2-5 gold");
        LootLogger.logInfo("  • Creeper → 10% TNT");
        LootLogger.logInfo("  • Skeleton → 2x drops");
        LootLogger.logInfo("  • End City → extra treasure");
        LootLogger.logSeparator();
        LootLogger.logEmptyLine();
        
        LootLogger.logHighlight("TIP: Use addItemWithChance() for exact percentages!");
        LootLogger.logEmptyLine();
    }
}
