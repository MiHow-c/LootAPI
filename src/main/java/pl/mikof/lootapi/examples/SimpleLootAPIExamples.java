package pl.mikof.lootapi.examples;

import net.minecraft.item.Items;
import pl.mikof.lootapi.*;

/**
 * Przykłady użycia Loot API z nową klasą LootTables
 * Pokazuje jak łatwo używać predefiniowanych ścieżek
 */
public class SimpleLootAPIExamples {
    
    public static void registerExamples() {
        
        // ========== ŁATWY SPOSÓB - UŻYJ LootTables ==========
        
        // 1. Dodaj diament do coal ore - ŁATWY SPOSÓB!
        LootTableAPI.addItemToTable(
            LootTables.Blocks.COAL_ORE,  // Zamiast: new Identifier("minecraft", "blocks/coal_ore")
            Items.DIAMOND,
            5
        );
        
        // 2. Zombie dropuje złoto
        LootTableAPI.addItemWithCount(
            LootTables.Entities.ZOMBIE,  // Łatwe!
            Items.GOLD_INGOT,
            10, 2, 5
        );
        
        // 3. Creeper dropuje TNT
        LootTableAPI.addItemToTable(
            LootTables.Entities.CREEPER,
            Items.TNT,
            25
        );
        
        // 4. Skrzynia w End City
        LootTableAPI.addItemToTable(
            LootTables.Chests.END_CITY_TREASURE,
            Items.ELYTRA,
            5
        );
        
        // 5. Podwój dropy ze szkieleta
        LootTableAPI.multiplyDrops(
            LootTables.Entities.SKELETON,
            2.0f
        );
        
        // 6. Zamień węgiel na diamenty
        LootTableAPI.replaceItem(
            LootTables.Blocks.COAL_ORE,
            Items.COAL,
            Items.DIAMOND
        );
        
        // 7. Dodaj emerald do wszystkich rud (wildcard!)
        LootTableAPI.addToMatching(
            "minecraft:blocks/*_ore",  // Wszystkie rudy!
            Items.EMERALD,
            3
        );
        
        // 8. Grupa itemów dla skrzyni
        WeightedItemGroup treasureGroup = new WeightedItemGroup()
            .add(Items.DIAMOND, 10)
            .add(Items.EMERALD, 20)
            .add(Items.NETHERITE_INGOT, 5);
        
        LootTableAPI.addWeightedGroup(
            LootTables.Chests.BURIED_TREASURE,
            treasureGroup
        );

        
        // 10. Custom mod ore - też łatwe!
        LootTableAPI.addItemToTable(
            LootTables.Blocks.custom("yourmod", "mythril_ore"),
            Items.DIAMOND,
            50
        );
        
        // ========== PORÓWNANIE ==========
        
        // STARY SPOSÓB (nadal działa):
        // new Identifier("minecraft", "blocks/diamond_ore")
        
        // NOWY SPOSÓB (łatwiejszy):
        // LootTables.Blocks.DIAMOND_ORE
        
        // ========== WIĘCEJ PRZYKŁADÓW ==========
        
        // Wszystkie liście dropują jabłka
        LootTableAPI.addItemToTable(LootTables.Blocks.OAK_LEAVES, Items.APPLE, 10);
        LootTableAPI.addItemToTable(LootTables.Blocks.BIRCH_LEAVES, Items.APPLE, 10);
        LootTableAPI.addItemToTable(LootTables.Blocks.SPRUCE_LEAVES, Items.APPLE, 10);
        
        // Lub użyj bulk operations:
        LootTableAPI.addToMatching(
            "minecraft:blocks/*_leaves",
            Items.GOLDEN_APPLE,
            1
        );
        
        // Boss dropy
        LootTableAPI.addItemToTable(
            LootTables.Entities.ENDER_DRAGON,
            Items.DRAGON_EGG,
            100
        );
        
        LootTableAPI.addItemToTable(
            LootTables.Entities.WITHER,
            Items.NETHER_STAR,
            100
        );
        
        // Fishing treasure
        LootTableAPI.addItemToTable(
            LootTables.Fishing.TREASURE,
            Items.TRIDENT,
            5
        );
        
        // Village chests
        LootTableAPI.addItemToTable(
            LootTables.Chests.VILLAGE_WEAPONSMITH,
            Items.NETHERITE_SWORD,
            2
        );
        
        // Nether fortress
        LootTableAPI.addItemToTable(
            LootTables.Chests.NETHER_BRIDGE,
            Items.ANCIENT_DEBRIS,
            3
        );
    }
}
