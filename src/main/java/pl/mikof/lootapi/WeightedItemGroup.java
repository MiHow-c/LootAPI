package pl.mikof.lootapi;

import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Klasa reprezentująca grupę itemów z różnymi wagami
 */
public class WeightedItemGroup {
    private final Map<Item, Integer> items = new HashMap<>();
    private int totalWeight = 0;
    
    /**
     * Dodaje item do grupy z określoną wagą
     */
    public WeightedItemGroup add(Item item, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        
        items.put(item, weight);
        totalWeight += weight;
        return this;
    }
    
    /**
     * Usuwa item z grupy
     */
    public WeightedItemGroup remove(Item item) {
        Integer weight = items.remove(item);
        if (weight != null) {
            totalWeight -= weight;
        }
        return this;
    }
    
    /**
     * Zwraca mapę itemów i ich wag
     */
    public Map<Item, Integer> getItems() {
        return new HashMap<>(items);
    }
    
    /**
     * Zwraca sumę wszystkich wag
     */
    public int getTotalWeight() {
        return totalWeight;
    }
    
    /**
     * Zwraca wagę określonego itemu
     */
    public int getWeight(Item item) {
        return items.getOrDefault(item, 0);
    }
    
    /**
     * Losuje item z grupy na podstawie wag
     */
    public Item selectRandom(Random random) {
        if (items.isEmpty()) {
            return null;
        }
        
        int randomWeight = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            currentWeight += entry.getValue();
            if (randomWeight < currentWeight) {
                return entry.getKey();
            }
        }
        
        // Fallback (nie powinno się zdarzyć)
        return items.keySet().iterator().next();
    }
    
    /**
     * Zwraca szansę procentową na wylosowanie danego itemu
     */
    public double getChance(Item item) {
        if (totalWeight == 0) {
            return 0.0;
        }
        return (double) items.getOrDefault(item, 0) / totalWeight * 100.0;
    }
    
    /**
     * Sprawdza czy grupa zawiera dany item
     */
    public boolean contains(Item item) {
        return items.containsKey(item);
    }
    
    /**
     * Zwraca liczbę itemów w grupie
     */
    public int size() {
        return items.size();
    }
    
    /**
     * Sprawdza czy grupa jest pusta
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Czyści wszystkie itemy z grupy
     */
    public void clear() {
        items.clear();
        totalWeight = 0;
    }
    
    /**
     * Tworzy kopię grupy
     */
    public WeightedItemGroup copy() {
        WeightedItemGroup copy = new WeightedItemGroup();
        items.forEach(copy::add);
        return copy;
    }
    
    /**
     * Normalizuje wagi do określonej sumy
     */
    public WeightedItemGroup normalize(int targetSum) {
        if (totalWeight == 0 || targetSum <= 0) {
            return this;
        }
        
        double scale = (double) targetSum / totalWeight;
        Map<Item, Integer> newItems = new HashMap<>();
        
        items.forEach((item, weight) -> {
            int newWeight = Math.max(1, (int) Math.round(weight * scale));
            newItems.put(item, newWeight);
        });
        
        items.clear();
        totalWeight = 0;
        newItems.forEach(this::add);
        
        return this;
    }
    
    /**
     * Scala z inną grupą
     */
    public WeightedItemGroup merge(WeightedItemGroup other) {
        other.items.forEach((item, weight) -> {
            int currentWeight = items.getOrDefault(item, 0);
            items.put(item, currentWeight + weight);
            totalWeight += weight;
        });
        return this;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WeightedItemGroup{");
        sb.append("total=").append(totalWeight).append(", items=[");
        
        items.forEach((item, weight) -> {
            sb.append(item).append(":").append(weight)
              .append("(").append(String.format("%.1f%%", getChance(item))).append("), ");
        });
        
        if (!items.isEmpty()) {
            sb.setLength(sb.length() - 2); // Usuń ostatni przecinek
        }
        sb.append("]}");
        
        return sb.toString();
    }
}
