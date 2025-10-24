package pl.mikof.lootapi;

import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

/**
 * Builder do łatwego tworzenia nowych loot tables
 */
public class LootTableBuilder {
    private final Identifier tableId;
    private final LootTable.Builder builder;
    
    private LootTableBuilder(Identifier tableId) {
        this.tableId = tableId;
        this.builder = LootTable.builder();
    }
    
    /**
     * Rozpoczyna tworzenie nowego loot table
     */
    public static LootTableBuilder create(Identifier tableId) {
        return new LootTableBuilder(tableId);
    }
    
    /**
     * Rozpoczyna tworzenie nowego loot table z łatwiejszym identyfikatorem
     */
    public static LootTableBuilder create(String namespace, String path) {
        return new LootTableBuilder(new Identifier(namespace, path));
    }
    
    /**
     * Dodaje prosty pool z jednym itemem
     * @param item Item do dodania
     * @param weight Waga (szansa)
     * @return this builder
     */
    public LootTableBuilder addItem(Item item, int weight) {
        LootPool.Builder pool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1))
            .with(ItemEntry.builder(item).weight(weight));
        builder.pool(pool);
        return this;
    }
    
    /**
     * Dodaje item z określoną ilością
     * @param item Item do dodania
     * @param weight Waga
     * @param minCount Minimalna ilość
     * @param maxCount Maksymalna ilość
     * @return this builder
     */
    public LootTableBuilder addItem(Item item, int weight, int minCount, int maxCount) {
        LootPool.Builder pool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1))
            .with(ItemEntry.builder(item)
                .weight(weight)
                .apply(net.minecraft.loot.function.SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(minCount, maxCount))));
        builder.pool(pool);
        return this;
    }
    
    /**
     * Rozpoczyna nowy pool z wieloma itemami
     * @param rolls Liczba losowań (ile itemów wypadnie)
     * @return PoolBuilder
     */
    public PoolBuilder pool(int rolls) {
        return new PoolBuilder(this, rolls);
    }
    
    /**
     * Rozpoczyna nowy pool z losową liczbą losowań
     * @param minRolls Minimalna liczba losowań
     * @param maxRolls Maksymalna liczba losowań
     * @return PoolBuilder
     */
    public PoolBuilder pool(int minRolls, int maxRolls) {
        return new PoolBuilder(this, minRolls, maxRolls);
    }
    
    /**
     * Dodaje grupę itemów jako pool
     * @param group Grupa itemów z wagami
     * @return this builder
     */
    public LootTableBuilder addGroup(WeightedItemGroup group) {
        LootPool.Builder pool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1));
        
        group.getItems().forEach((item, weight) -> {
            pool.with(ItemEntry.builder(item).weight(weight));
        });
        
        builder.pool(pool);
        return this;
    }
    
    /**
     * Dodaje grupę itemów z określoną liczbą losowań
     * @param group Grupa itemów
     * @param rolls Liczba losowań
     * @return this builder
     */
    public LootTableBuilder addGroup(WeightedItemGroup group, int rolls) {
        LootPool.Builder pool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(rolls));
        
        group.getItems().forEach((item, weight) -> {
            pool.with(ItemEntry.builder(item).weight(weight));
        });
        
        builder.pool(pool);
        return this;
    }
    
    /**
     * Dodaje custom pool używając Consumer
     * @param poolModifier Funkcja modyfikująca pool
     * @return this builder
     */
    public LootTableBuilder customPool(java.util.function.Consumer<LootPool.Builder> poolModifier) {
        LootPool.Builder pool = LootPool.builder();
        poolModifier.accept(pool);
        builder.pool(pool);
        return this;
    }
    
    /**
     * Rejestruje loot table w API
     */
    public void register() {
        LootTableAPI.createLootTable(tableId, b -> {
            // Kopiuj wszystkie pool'e z naszego buildera
        });
        LootTableAPI.registerModifier(tableId, b -> builder.build());
        LootTableAPI.LOGGER.info("Registered new loot table: {}", tableId);
    }
    
    /**
     * Zwraca zbudowany loot table (bez rejestracji)
     */
    public LootTable build() {
        return builder.build();
    }
    
    /**
     * Zwraca ID tego loot table
     */
    public Identifier getId() {
        return tableId;
    }
    
    // ========== POOL BUILDER ==========
    
    /**
     * Builder do tworzenia pool'i z wieloma itemami
     */
    public static class PoolBuilder {
        private final LootTableBuilder parent;
        private final LootPool.Builder pool;
        
        private PoolBuilder(LootTableBuilder parent, int rolls) {
            this.parent = parent;
            this.pool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(rolls));
        }
        
        private PoolBuilder(LootTableBuilder parent, int minRolls, int maxRolls) {
            this.parent = parent;
            this.pool = LootPool.builder()
                .rolls(UniformLootNumberProvider.create(minRolls, maxRolls));
        }
        
        /**
         * Dodaje item do pool'a
         */
        public PoolBuilder item(Item item, int weight) {
            pool.with(ItemEntry.builder(item).weight(weight));
            return this;
        }
        
        /**
         * Dodaje item z określoną ilością do pool'a
         */
        public PoolBuilder item(Item item, int weight, int minCount, int maxCount) {
            pool.with(ItemEntry.builder(item)
                .weight(weight)
                .apply(net.minecraft.loot.function.SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(minCount, maxCount))));
            return this;
        }
        
        /**
         * Dodaje warunek do pool'a
         */
        public PoolBuilder condition(net.minecraft.loot.condition.LootCondition.Builder condition) {
            pool.conditionally(condition);
            return this;
        }
        
        /**
         * Kończy tworzenie pool'a i wraca do głównego buildera
         */
        public LootTableBuilder endPool() {
            parent.builder.pool(pool);
            return parent;
        }
    }
    
    // ========== QUICK BUILD METHODS ==========
    
    /**
     * Szybko tworzy loot table z pojedynczym itemem
     */
    public static void quickSingle(Identifier tableId, Item item) {
        create(tableId)
            .addItem(item, 100)
            .register();
    }
    
    /**
     * Szybko tworzy loot table z wieloma itemami (równe szanse)
     */
    public static void quickBalanced(Identifier tableId, Item... items) {
        LootTableBuilder builder = create(tableId);
        int weightPerItem = 100 / items.length;
        
        for (Item item : items) {
            builder.addItem(item, weightPerItem);
        }
        
        builder.register();
    }
    
    /**
     * Szybko tworzy ore loot table
     */
    public static void quickOre(Identifier tableId, Item rawOre, int minDrops, int maxDrops) {
        create(tableId)
            .pool(1)
                .item(rawOre, 100, minDrops, maxDrops)
            .endPool()
            .register();
    }
}
