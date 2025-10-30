package pl.mikof.lootapi.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import pl.mikof.lootapi.LootAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder do tworzenia Global Loot Modifiers programatycznie
 */
public class LootModifierBuilder {
    private final String modifierId;
    private final String modifierType;
    private final List<ResourceLocation> targetTables = new ArrayList<>();
    private final JsonObject data = new JsonObject();

    private LootModifierBuilder(String modifierId, String modifierType) {
        if (modifierId == null || modifierId.isEmpty()) {
            throw new IllegalArgumentException("Modifier ID cannot be null or empty");
        }
        if (modifierType == null || modifierType.isEmpty()) {
            throw new IllegalArgumentException("Modifier type cannot be null or empty");
        }
        this.modifierId = modifierId;
        this.modifierType = modifierType;
    }

    /**
     * Tworzy builder do dodawania przedmiotu
     */
    public static LootModifierBuilder addItem(String modifierId) {
        return new LootModifierBuilder(modifierId, "lootapi:add_item");
    }

    /**
     * Tworzy builder do usuwania przedmiotu
     */
    public static LootModifierBuilder removeItem(String modifierId) {
        return new LootModifierBuilder(modifierId, "lootapi:remove_item");
    }

    /**
     * Tworzy builder do zamiany przedmiotu
     */
    public static LootModifierBuilder replaceItem(String modifierId) {
        return new LootModifierBuilder(modifierId, "lootapi:replace_item");
    }

    /**
     * Tworzy builder do mnożenia dropów
     */
    public static LootModifierBuilder multiplyDrops(String modifierId) {
        return new LootModifierBuilder(modifierId, "lootapi:multiply_drops");
    }

    /**
     * Tworzy builder do czyszczenia tabeli
     */
    public static LootModifierBuilder clearTable(String modifierId) {
        return new LootModifierBuilder(modifierId, "lootapi:clear_table");
    }

    /**
     * Tworzy builder do ustawiania tylko jednego dropu
     */
    public static LootModifierBuilder setOnlyDrop(String modifierId) {
        return new LootModifierBuilder(modifierId, "lootapi:set_only_drop");
    }

    /**
     * Dodaje tabelę docelową
     */
    public LootModifierBuilder forTable(ResourceLocation tableId) {
        if (tableId == null) {
            throw new IllegalArgumentException("Table ID cannot be null");
        }
        this.targetTables.add(tableId);
        return this;
    }

    /**
     * Dodaje tablice docelowe
     */
    public LootModifierBuilder forTables(ResourceLocation... tableIds) {
        if (tableIds == null || tableIds.length == 0) {
            throw new IllegalArgumentException("Table IDs cannot be null or empty");
        }
        for (ResourceLocation tableId : tableIds) {
            if (tableId != null) {
                this.targetTables.add(tableId);
            }
        }
        return this;
    }

    /**
     * Ustawia przedmiot (dla add_item, remove_item, set_only_drop)
     */
    public LootModifierBuilder withItem(Item item) {
        if (item == null || item == Items.AIR) {
            throw new IllegalArgumentException("Item cannot be null or AIR");
        }
        ResourceLocation itemId = getItemId(item);
        data.addProperty("item", itemId.toString());
        return this;
    }

    /**
     * Ustawia stary przedmiot (dla replace_item)
     */
    public LootModifierBuilder withOldItem(Item item) {
        if (item == null || item == Items.AIR) {
            throw new IllegalArgumentException("Old item cannot be null or AIR");
        }
        ResourceLocation itemId = getItemId(item);
        data.addProperty("old_item", itemId.toString());
        return this;
    }

    /**
     * Ustawia nowy przedmiot (dla replace_item)
     */
    public LootModifierBuilder withNewItem(Item item) {
        if (item == null || item == Items.AIR) {
            throw new IllegalArgumentException("New item cannot be null or AIR");
        }
        ResourceLocation itemId = getItemId(item);
        data.addProperty("new_item", itemId.toString());
        return this;
    }

    /**
     * Ustawia ilość (dla add_item, set_only_drop)
     */
    public LootModifierBuilder withCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive, got: " + count);
        }
        if (count > 64) {
            LootAPI.getLogger().warn("Count {} exceeds stack size (64), this may cause issues", count);
        }
        data.addProperty("min_count", count);
        data.addProperty("max_count", count);
        return this;
    }

    /**
     * Ustawia zakres ilości (dla add_item, set_only_drop)
     */
    public LootModifierBuilder withCount(int min, int max) {
        if (min <= 0) {
            throw new IllegalArgumentException("Min count must be positive, got: " + min);
        }
        if (max < min) {
            throw new IllegalArgumentException(
                "Max count (" + max + ") must be >= min count (" + min + ")"
            );
        }
        if (max > 64) {
            LootAPI.getLogger().warn("Max count {} exceeds stack size (64), this may cause issues", max);
        }
        data.addProperty("min_count", min);
        data.addProperty("max_count", max);
        return this;
    }

    /**
     * Ustawia szansę (dla add_item)
     */
    public LootModifierBuilder withChance(float chance) {
        if (chance < 0.0f || chance > 1.0f) {
            throw new IllegalArgumentException(
                "Chance must be between 0.0 and 1.0, got: " + chance
            );
        }
        data.addProperty("chance", chance);
        return this;
    }

    /**
     * Ustawia mnożnik (dla multiply_drops)
     */
    public LootModifierBuilder withMultiplier(float multiplier) {
        if (multiplier <= 0.0f) {
            throw new IllegalArgumentException(
                "Multiplier must be positive, got: " + multiplier
            );
        }
        if (multiplier > 100.0f) {
            LootAPI.getLogger().warn("Extremely high multiplier: {}x - are you sure?", multiplier);
        }
        data.addProperty("multiplier", multiplier);
        return this;
    }

    /**
     * Buduje JSON dla tego modifiera
     */
    public JsonObject build() {
        // Walidacja przed budowaniem
        if (targetTables.isEmpty()) {
            LootAPI.getLogger().warn("Modifier '{}' has no target tables - it will affect ALL loot tables!", modifierId);
        }

        // Sprawdź czy wymagane pola są ustawione w zależności od typu
        validateModifierData();

        JsonObject modifier = new JsonObject();
        modifier.addProperty("type", modifierType);

        // Dodaj warunki dla tabel
        JsonArray conditions = new JsonArray();
        if (!targetTables.isEmpty()) {
            for (ResourceLocation tableId : targetTables) {
                JsonObject condition = new JsonObject();
                condition.addProperty("condition", "neoforge:loot_table_id");
                condition.addProperty("loot_table_id", tableId.toString());
                conditions.add(condition);
            }
        }
        modifier.add("conditions", conditions);

        // Dodaj pozostałe dane
        for (String key : data.keySet()) {
            modifier.add(key, data.get(key));
        }

        return modifier;
    }

    /**
     * Waliduje dane modifiera w zależności od typu
     */
    private void validateModifierData() {
        switch (modifierType) {
            case "lootapi:add_item":
            case "lootapi:set_only_drop":
                if (!data.has("item")) {
                    throw new IllegalStateException("Modifier type " + modifierType + " requires an item");
                }
                break;
            case "lootapi:remove_item":
                if (!data.has("item")) {
                    throw new IllegalStateException("remove_item modifier requires an item");
                }
                break;
            case "lootapi:replace_item":
                if (!data.has("old_item") || !data.has("new_item")) {
                    throw new IllegalStateException("replace_item modifier requires both old_item and new_item");
                }
                break;
            case "lootapi:multiply_drops":
                if (!data.has("multiplier")) {
                    throw new IllegalStateException("multiply_drops modifier requires a multiplier");
                }
                break;
            case "lootapi:clear_table":
                // No additional data required
                break;
            default:
                LootAPI.getLogger().warn("Unknown modifier type: {}", modifierType);
        }
    }

    /**
     * Zwraca ID modifiera
     */
    public String getModifierId() {
        return modifierId;
    }

    /**
     * Pomocnicza metoda do pobierania ID przedmiotu
     */
    private ResourceLocation getItemId(Item item) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
    }
}
