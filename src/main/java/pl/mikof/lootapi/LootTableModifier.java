package pl.mikof.lootapi;

import net.minecraft.loot.LootTable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Klasa przechowująca listę modyfikatorów dla pojedynczego loot table
 * z obsługą priorytetów
 */
public class LootTableModifier {
    private final List<PrioritizedModifier> modifiers = new ArrayList<>();
    
    /**
     * Dodaje nowy modyfikator do listy z określonym priorytetem
     */
    public void addModifier(Consumer<LootTable.Builder> modifier, LootTableAPI.Priority priority) {
        modifiers.add(new PrioritizedModifier(modifier, priority));
        // Sortuj po priorytecie (wyższy = wcześniej)
        modifiers.sort(Comparator.comparingInt(m -> -m.priority.getValue()));
    }
    
    /**
     * Dodaje modyfikator z domyślnym priorytetem
     */
    public void addModifier(Consumer<LootTable.Builder> modifier) {
        addModifier(modifier, LootTableAPI.Priority.NORMAL);
    }
    
    /**
     * Aplikuje wszystkie modyfikatory do buildera w kolejności priorytetów
     */
    public void apply(LootTable.Builder builder) {
        for (PrioritizedModifier mod : modifiers) {
            mod.modifier.accept(builder);
        }
    }
    
    /**
     * Zwraca liczbę zarejestrowanych modyfikatorów
     */
    public int getModifierCount() {
        return modifiers.size();
    }
    
    /**
     * Sprawdza czy są jakieś modyfikatory
     */
    public boolean hasModifiers() {
        return !modifiers.isEmpty();
    }
    
    /**
     * Czyści wszystkie modyfikatory
     */
    public void clear() {
        modifiers.clear();
    }
    
    /**
     * Zwraca modyfikatory o określonym priorytecie
     */
    public List<Consumer<LootTable.Builder>> getModifiersByPriority(LootTableAPI.Priority priority) {
        return modifiers.stream()
            .filter(m -> m.priority == priority)
            .map(m -> m.modifier)
            .collect(Collectors.toList());
    }
    
    /**
     * Usuwa modyfikatory o określonym priorytecie
     */
    public void removeByPriority(LootTableAPI.Priority priority) {
        modifiers.removeIf(m -> m.priority == priority);
    }
    
    /**
     * Tworzy głęboką kopię modyfikatora
     */
    public LootTableModifier copy() {
        LootTableModifier copy = new LootTableModifier();
        for (PrioritizedModifier mod : this.modifiers) {
            copy.modifiers.add(new PrioritizedModifier(mod.modifier, mod.priority));
        }
        return copy;
    }
    
    /**
     * Zwraca mapę priorytetów i ich liczby modyfikatorów
     */
    public Map<LootTableAPI.Priority, Integer> getPriorityDistribution() {
        Map<LootTableAPI.Priority, Integer> distribution = new EnumMap<>(LootTableAPI.Priority.class);
        for (PrioritizedModifier mod : modifiers) {
            distribution.merge(mod.priority, 1, Integer::sum);
        }
        return distribution;
    }
    
    /**
     * Wewnętrzna klasa przechowująca modyfikator z priorytetem
     */
    private static class PrioritizedModifier {
        final Consumer<LootTable.Builder> modifier;
        final LootTableAPI.Priority priority;
        
        PrioritizedModifier(Consumer<LootTable.Builder> modifier, LootTableAPI.Priority priority) {
            this.modifier = modifier;
            this.priority = priority;
        }
    }
    
    @Override
    public String toString() {
        return String.format("LootTableModifier{modifiers=%d, priorities=%s}", 
            modifiers.size(), 
            getPriorityDistribution());
    }
}
