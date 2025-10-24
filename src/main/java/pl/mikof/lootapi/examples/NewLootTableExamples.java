package pl.mikof.lootapi.examples;

import net.minecraft.item.Items;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;
import pl.mikof.lootapi.*;

/**
 * Przykłady tworzenia CAŁKOWICIE NOWYCH loot tables
 */
public class NewLootTableExamples {
    
    public static void registerNewLootTables() {
        
        // ========== SPOSÓB 1: SZYBKI (Quick Methods) ==========
        
        // 1. Pojedynczy item - super proste!
        LootTableBuilder.quickSingle(
            new Identifier("yourmod", "blocks/simple_ore"),
            Items.DIAMOND
        );
        
        // 2. Wiele itemów z równymi szansami
        LootTableBuilder.quickBalanced(
            new Identifier("yourmod", "chests/starter_chest"),
            Items.IRON_SWORD,
            Items.IRON_PICKAXE,
            Items.BREAD,
            Items.TORCH
        );
        
        // 3. Ore z określoną ilością dropów
        LootTableBuilder.quickOre(
            new Identifier("yourmod", "blocks/copper_ore"),
            Items.RAW_COPPER,
            2,  // min drops
            5   // max drops
        );
        
        // ========== SPOSÓB 2: BUILDER (Bardziej kontroli) ==========
        
        // 4. Custom ore z różnymi dropami
        LootTableBuilder.create("yourmod", "blocks/mythril_ore")
            .addItem(Items.DIAMOND, 70)
            .addItem(Items.EMERALD, 20)
            .addItem(Items.NETHERITE_SCRAP, 10)
            .register();
        
        // 5. Chest z wieloma itemami i ilościami
        LootTableBuilder.create("yourmod", "chests/treasure_chest")
            .addItem(Items.DIAMOND, 50, 1, 3)
            .addItem(Items.GOLD_INGOT, 80, 5, 10)
            .addItem(Items.EMERALD, 30, 2, 5)
            .register();
        
        // 6. Mob loot z pool'ami
        LootTableBuilder.create("yourmod", "entities/custom_mob")
            .pool(1)
                .item(Items.ROTTEN_FLESH, 80)
                .item(Items.BONE, 20)
            .endPool()
            .pool(1)
                .item(Items.DIAMOND, 5)
                .item(Items.EMERALD, 10)
                .item(Items.GOLD_INGOT, 85)
            .endPool()
            .register();
        
        // 7. Chest z losową liczbą itemów
        LootTableBuilder.create("yourmod", "chests/random_chest")
            .pool(3, 7)
                .item(Items.IRON_INGOT, 40)
                .item(Items.GOLD_INGOT, 30)
                .item(Items.DIAMOND, 20)
                .item(Items.EMERALD, 10)
            .endPool()
            .register();
        
        // ========== SPOSÓB 3: Z GRUPAMI ==========
        
        // 8. Użyj WeightedItemGroup
        WeightedItemGroup commonItems = new WeightedItemGroup()
            .add(Items.IRON_INGOT, 50)
            .add(Items.GOLD_INGOT, 30)
            .add(Items.COAL, 20);
        
        WeightedItemGroup rareItems = new WeightedItemGroup()
            .add(Items.DIAMOND, 60)
            .add(Items.EMERALD, 30)
            .add(Items.NETHERITE_SCRAP, 10);
        
        LootTableBuilder.create("yourmod", "chests/epic_chest")
            .addGroup(commonItems, 5)
            .addGroup(rareItems, 2)
            .register();
        
        // ========== SPOSÓB 4: ZAAWANSOWANY ==========
        
        // 9. Custom pool z warunkami
        LootTableBuilder.create("yourmod", "entities/night_mob")
            .pool(1)
                .item(Items.ENDER_PEARL, 100)
            .endPool()
            .customPool(pool -> {
                pool.rolls(ConstantLootNumberProvider.create(2))
                    .with(ItemEntry.builder(Items.PHANTOM_MEMBRANE).weight(50))
                    .with(ItemEntry.builder(Items.GHAST_TEAR).weight(50));
            })
            .register();
        
        // 10. Fishing loot table
        LootTableBuilder.create("yourmod", "gameplay/fishing/custom_fishing")
            .pool(1)
                .item(Items.COD, 60)
                .item(Items.SALMON, 25)
                .item(Items.TROPICAL_FISH, 10)
                .item(Items.PUFFERFISH, 5)
            .endPool()
            .register();
        
        // 11. Boss mob
        LootTableBuilder.create("yourmod", "entities/custom_boss")
            .addItem(Items.NETHER_STAR, 100)
            .addItem(Items.DRAGON_HEAD, 50)
            .addItem(Items.ELYTRA, 25, 1, 1)
            .pool(5, 10)
                .item(Items.DIAMOND, 30)
                .item(Items.EMERALD, 30)
                .item(Items.NETHERITE_INGOT, 20)
                .item(Items.ENCHANTED_GOLDEN_APPLE, 20)
            .endPool()
            .register();
    }
    
    // ========== HELPER METHODS ==========
    
    public static void createCustomOre(String modId, String oreName, 
                                       net.minecraft.item.Item rawItem, 
                                       int minDrops, int maxDrops) {
        LootTableBuilder.create(modId, "blocks/" + oreName)
            .pool(1)
                .item(rawItem, 100, minDrops, maxDrops)
            .endPool()
            .register();
    }
    
    public static void createCustomMob(String modId, String mobName, 
                                       net.minecraft.item.Item commonDrop, 
                                       net.minecraft.item.Item rareDrop) {
        LootTableBuilder.create(modId, "entities/" + mobName)
            .addItem(commonDrop, 80, 0, 2)
            .addItem(rareDrop, 20, 1, 1)
            .register();
    }
    
    public static void createCustomChest(String modId, String chestName, 
                                         WeightedItemGroup items) {
        LootTableBuilder.create(modId, "chests/" + chestName)
            .addGroup(items, 5)
            .register();
    }
}
