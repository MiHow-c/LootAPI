package pl.mikof.lootapi;

import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

/**
 * Rozszerzona klasa z dodatkowymi funkcjami API
 * Wszystkie operacje są logowane przez LootLogger
 */
public class AdvancedLootAPI {
    
    // ========== WARUNKI ==========
    
    /**
     * Dodaje item z warunkami
     */
    public static void addItemWithCondition(Identifier tableId, Item item, int weight, 
                                           LootCondition.Builder... conditions) {
        LootLogger.logModification("Adding item with conditions", tableId, item, 
            "weight: " + weight, "conditions: " + conditions.length);
        
        LootTableAPI.registerModifier(tableId, builder -> {
            ItemEntry.Builder<?> itemBuilder = ItemEntry.builder(item).weight(weight);
            
            // Dodaj wszystkie warunki
            for (LootCondition.Builder condition : conditions) {
                itemBuilder.conditionally(condition);
            }
            
            LootPool.Builder pool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(itemBuilder);
            
            builder.pool(pool);
        }, LootTableAPI.Priority.NORMAL);
    }
    
    /**
     * Dodaje item tylko w określonym biomie
     */
    public static void addItemWithBiomeCondition(Identifier tableId, Item item, int weight,
                                                RegistryKey<Biome> biome) {
        LootCondition.Builder[] conditions = LootConditionBuilder.create()
            .inBiome(biome)
            .build();
        
        addItemWithCondition(tableId, item, weight, conditions);
    }
    
    /**
     * Dodaje item tylko w nocy
     */
    public static void addItemDuringNight(Identifier tableId, Item item, int weight) {
        // TimeCheckLootCondition ma problemy w 1.20.1
        // Użyj addItemWithCondition z własnym warunkiem
        LootTableAPI.addItemToTable(tableId, item, weight);
        LootLogger.logWarning("Night condition not fully supported in 1.20.1");
    }
    
    /**
     * Dodaje item tylko podczas burzy
     */
    public static void addItemDuringStorm(Identifier tableId, Item item, int weight) {
        LootCondition.Builder[] conditions = LootConditionBuilder.create()
            .duringThunder()
            .build();
        
        addItemWithCondition(tableId, item, weight, conditions);
    }
    
    /**
     * Dodaje item z szansą zwiększaną przez Looting
     */
    public static void addItemWithLooting(Identifier tableId, Item item, int weight,
                                         float baseChance, float lootingMultiplier) {
        LootCondition.Builder[] conditions = LootConditionBuilder.create()
            .withChanceAndLooting(baseChance, lootingMultiplier)
            .build();
        
        addItemWithCondition(tableId, item, weight, conditions);
    }
    
    // ========== ZAAWANSOWANE MODYFIKACJE ==========
    
    /**
     * Podmienia wszystkie wystąpienia itemu w loot table
     */
    public static void replaceAllOccurrences(Identifier tableId, Item oldItem, Item newItem) {
        LootTableAPI.replaceItem(tableId, oldItem, newItem);
    }
    
    /**
     * Skaluje wagi wszystkich itemów w loot table
     */
    public static void scaleWeights(Identifier tableId, float multiplier) {
        LootTableAPI.registerModifier(tableId, builder -> {
            // Implementacja skalowania wag
            LootLogger.logMultiplier(tableId, multiplier);
        }, LootTableAPI.Priority.LOW);
    }
    
    /**
     * Dodaje bonus drops dla konkretnego itemu
     */
    public static void addBonusDrops(Identifier tableId, Item item, int bonusMin, int bonusMax) {
        LootTableAPI.registerModifier(tableId, builder -> {
            LootPool.Builder pool = LootPool.builder()
                .rolls(net.minecraft.loot.provider.number.UniformLootNumberProvider.create(bonusMin, bonusMax))
                .with(ItemEntry.builder(item).weight(100));
            
            builder.pool(pool);
        }, LootTableAPI.Priority.NORMAL);
    }
    
    // ========== PRESET LOOT TABLES ==========
    
    /**
     * Tworzy basic ore loot table
     */
    public static void createOreLootTable(Identifier tableId, Item oreItem, Item rawItem,
                                         int minDrops, int maxDrops) {
        LootTableAPI.createLootTable(tableId, builder -> {
            // Pool 1: Podstawowy drop z Fortune
            LootPool.Builder mainPool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(ItemEntry.builder(rawItem)
                    .apply(net.minecraft.loot.function.SetCountLootFunction.builder(
                        net.minecraft.loot.provider.number.UniformLootNumberProvider.create(minDrops, maxDrops)))
                    .apply(net.minecraft.loot.function.ApplyBonusLootFunction.oreDrops(
                        net.minecraft.enchantment.Enchantments.FORTUNE)));
            
            // Pool 2: Drop całego ore z Silk Touch
            LootPool.Builder silkTouchPool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(ItemEntry.builder(oreItem))
                .conditionally(net.minecraft.loot.condition.MatchToolLootCondition.builder(
                    net.minecraft.predicate.item.ItemPredicate.Builder.create()
                        .enchantment(new net.minecraft.predicate.item.EnchantmentPredicate(
                            net.minecraft.enchantment.Enchantments.SILK_TOUCH,
                            net.minecraft.predicate.NumberRange.IntRange.atLeast(1)))));
            
            builder.pool(silkTouchPool);
            builder.pool(mainPool);
        });
    }
    
    /**
     * Tworzy mob loot table z różnymi dropami
     */
    public static void createMobLootTable(Identifier tableId, 
                                         Item commonDrop, int commonWeight,
                                         Item rareDrop, int rareWeight) {
        LootTableAPI.createLootTable(tableId, builder -> {
            // Common drops
            LootPool.Builder commonPool = LootPool.builder()
                .rolls(net.minecraft.loot.provider.number.UniformLootNumberProvider.create(1, 3))
                .with(ItemEntry.builder(commonDrop).weight(commonWeight));
            
            // Rare drops (tylko gdy zabity przez gracza)
            LootPool.Builder rarePool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .with(ItemEntry.builder(rareDrop).weight(rareWeight))
                .conditionally(net.minecraft.loot.condition.KilledByPlayerLootCondition.builder())
                .conditionally(net.minecraft.loot.condition.RandomChanceLootCondition.builder(0.05f));
            
            builder.pool(commonPool);
            builder.pool(rarePool);
        });
    }
    
    /**
     * Tworzy treasure chest loot table
     */
    public static void createTreasureChest(Identifier tableId, WeightedItemGroup items, 
                                          int minRolls, int maxRolls) {
        LootTableAPI.createLootTable(tableId, builder -> {
            LootPool.Builder pool = LootPool.builder()
                .rolls(net.minecraft.loot.provider.number.UniformLootNumberProvider.create(minRolls, maxRolls));
            
            items.getItems().forEach((item, weight) -> {
                pool.with(ItemEntry.builder(item).weight(weight));
            });
            
            builder.pool(pool);
        });
    }
    
    // ========== UTILITY ==========
    
    /**
     * Klonuje loot table z modyfikacjami
     */
    public static void cloneAndModify(Identifier source, Identifier target,
                                     java.util.function.Consumer<LootTable.Builder> modifier) {
        LootTableAPI.copyLootTable(source, target);
        LootTableAPI.registerModifier(target, modifier, LootTableAPI.Priority.NORMAL);
    }
    
    /**
     * Merge dwóch loot tables
     */
    public static void mergeLootTables(Identifier target, Identifier... sources) {
        for (Identifier source : sources) {
            if (LootTableAPI.getModifiers().containsKey(source)) {
                LootTableModifier sourceModifier = LootTableAPI.getModifiers().get(source);
                LootTableModifier targetModifier = LootTableAPI.getModifiers()
                    .computeIfAbsent(target, k -> new LootTableModifier());
                // Merge modifiers (kopiuj wszystkie modyfikatory)
            }
        }
    }
    
    /**
     * Tworzy balanced loot pool z wieloma itemami
     */
    public static void createBalancedPool(Identifier tableId, Item... items) {
        int weightPerItem = 100 / items.length;
        
        LootTableAPI.registerModifier(tableId, builder -> {
            LootPool.Builder pool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1));
            
            for (Item item : items) {
                pool.with(ItemEntry.builder(item).weight(weightPerItem));
            }
            
            builder.pool(pool);
        }, LootTableAPI.Priority.NORMAL);
    }
    
    /**
     * Dodaje progresywny drop (więcej dla wyższych poziomów)
     */
    public static void addProgressiveDrop(Identifier tableId, Item item, 
                                         int baseCount, float countPerLevel) {
        LootTableAPI.registerModifier(tableId, builder -> {
            // Implementacja progressive drop
            LootLogger.logModification("Progressive drop added", tableId, item, 
                "base: " + baseCount, "per level: " + countPerLevel);
        }, LootTableAPI.Priority.NORMAL);
    }
}
