package pl.mikof.lootapi;

import net.minecraft.loot.condition.*;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.world.biome.Biome;
import net.minecraft.registry.RegistryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder do tworzenia warunków dla loot tables
 */
public class LootConditionBuilder {
    private final List<LootCondition.Builder> conditions = new ArrayList<>();
    
    /**
     * Dodaje warunek Fortune enchantment
     */
    public LootConditionBuilder withFortune() {
        conditions.add(MatchToolLootCondition.builder(
            net.minecraft.predicate.item.ItemPredicate.Builder.create()
                .enchantment(new net.minecraft.predicate.item.EnchantmentPredicate(
                    Enchantments.FORTUNE, 
                    net.minecraft.predicate.NumberRange.IntRange.atLeast(1)))
        ));
        return this;
    }
    
    /**
     * Dodaje warunek Silk Touch enchantment
     */
    public LootConditionBuilder withSilkTouch() {
        conditions.add(MatchToolLootCondition.builder(
            net.minecraft.predicate.item.ItemPredicate.Builder.create()
                .enchantment(new net.minecraft.predicate.item.EnchantmentPredicate(
                    Enchantments.SILK_TOUCH, 
                    net.minecraft.predicate.NumberRange.IntRange.atLeast(1)))
        ));
        return this;
    }
    
    /**
     * Dodaje warunek konkretnego poziomu enchantu
     */
    public LootConditionBuilder withEnchantmentLevel(net.minecraft.enchantment.Enchantment enchantment, int minLevel) {
        conditions.add(MatchToolLootCondition.builder(
            net.minecraft.predicate.item.ItemPredicate.Builder.create()
                .enchantment(new net.minecraft.predicate.item.EnchantmentPredicate(
                    enchantment, 
                    net.minecraft.predicate.NumberRange.IntRange.atLeast(minLevel)))
        ));
        return this;
    }
    
    /**
     * Dodaje warunek biomu
     */
    public LootConditionBuilder inBiome(RegistryKey<Biome> biome) {
        conditions.add(LocationCheckLootCondition.builder(
            LocationPredicate.Builder.create()
                .biome(biome)
        ));
        return this;
    }
    
    /**
     * Dodaje warunek pory dnia (dzień)
     * Uwaga: Funkcja wyłączona - problemy z kompatybilnością
     */
    public LootConditionBuilder duringDay() {
        // TimeCheckLootCondition ma problemy w 1.20.1
        // Użyj custom condition jeśli potrzebujesz
        return this;
    }
    
    /**
     * Dodaje warunek pory dnia (noc)
     * Uwaga: Funkcja wyłączona - problemy z kompatybilnością
     */
    public LootConditionBuilder duringNight() {
        // TimeCheckLootCondition ma problemy w 1.20.1
        // Użyj custom condition jeśli potrzebujesz
        return this;
    }
    
    /**
     * Dodaje warunek konkretnego czasu
     * Uwaga: Funkcja wyłączona - problemy z kompatybilnością
     */
    public LootConditionBuilder atTime(int minTime, int maxTime) {
        // TimeCheckLootCondition ma problemy w 1.20.1
        // Użyj custom condition jeśli potrzebujesz
        return this;
    }
    
    /**
     * Dodaje warunek pogody (deszcz)
     */
    public LootConditionBuilder duringRain() {
        conditions.add(WeatherCheckLootCondition.create().raining(true));
        return this;
    }
    
    /**
     * Dodaje warunek pogody (burza)
     */
    public LootConditionBuilder duringThunder() {
        conditions.add(WeatherCheckLootCondition.create().thundering(true));
        return this;
    }
    
    /**
     * Dodaje warunek losowej szansy (0.0 - 1.0)
     */
    public LootConditionBuilder withChance(float chance) {
        conditions.add(RandomChanceLootCondition.builder(chance));
        return this;
    }
    
    /**
     * Dodaje warunek losowej szansy z bonusem od Looting
     */
    public LootConditionBuilder withChanceAndLooting(float baseChance, float lootingMultiplier) {
        conditions.add(RandomChanceWithLootingLootCondition.builder(baseChance, lootingMultiplier));
        return this;
    }
    
    /**
     * Dodaje warunek że gracz zabił (dla mobów)
     */
    public LootConditionBuilder killedByPlayer() {
        conditions.add(KilledByPlayerLootCondition.builder());
        return this;
    }
    
    /**
     * Dodaje warunek przeżycia eksplozji
     */
    public LootConditionBuilder survivedExplosion() {
        conditions.add(SurvivesExplosionLootCondition.builder());
        return this;
    }
    
    /**
     * Dodaje warunek wysokości (Y level)
     */
    public LootConditionBuilder atHeight(int minY, int maxY) {
        conditions.add(LocationCheckLootCondition.builder(
            LocationPredicate.Builder.create()
                .y(net.minecraft.predicate.NumberRange.FloatRange.between((float)minY, (float)maxY))
        ));
        return this;
    }
    
    /**
     * Dodaje custom warunek
     */
    public LootConditionBuilder custom(LootCondition.Builder condition) {
        conditions.add(condition);
        return this;
    }
    
    /**
     * Odwraca wszystkie warunki (NOT)
     */
    public LootConditionBuilder invert() {
        List<LootCondition.Builder> inverted = new ArrayList<>();
        for (LootCondition.Builder condition : conditions) {
            inverted.add(InvertedLootCondition.builder(condition));
        }
        conditions.clear();
        conditions.addAll(inverted);
        return this;
    }
    
    /**
     * Buduje tablicę warunków
     */
    public LootCondition.Builder[] build() {
        return conditions.toArray(new LootCondition.Builder[0]);
    }
    
    /**
     * Zwraca liczbę warunków
     */
    public int size() {
        return conditions.size();
    }
    
    /**
     * Sprawdza czy są jakieś warunki
     */
    public boolean isEmpty() {
        return conditions.isEmpty();
    }
    
    /**
     * Czyści wszystkie warunki
     */
    public void clear() {
        conditions.clear();
    }
    
    /**
     * Tworzy nowy builder
     */
    public static LootConditionBuilder create() {
        return new LootConditionBuilder();
    }
    
    // ========== PREDEFINIOWANE KOMBINACJE ==========
    
    /**
     * Warunek: Fortune enchant
     */
    public static LootCondition.Builder[] FORTUNE() {
        return create().withFortune().build();
    }
    
    /**
     * Warunek: Silk Touch enchant
     */
    public static LootCondition.Builder[] SILK_TOUCH() {
        return create().withSilkTouch().build();
    }
    
    /**
     * Warunek: Noc + burza
     */
    public static LootCondition.Builder[] STORMY_NIGHT() {
        return create().duringNight().duringThunder().build();
    }
    
    /**
     * Warunek: Zabity przez gracza + szansa
     */
    public static LootCondition.Builder[] RARE_PLAYER_KILL(float chance) {
        return create().killedByPlayer().withChance(chance).build();
    }
    
    @Override
    public String toString() {
        return String.format("LootConditionBuilder{conditions=%d}", conditions.size());
    }
}
