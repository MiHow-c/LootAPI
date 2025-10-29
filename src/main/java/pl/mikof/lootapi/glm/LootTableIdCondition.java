package pl.mikof.lootapi.glm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import pl.mikof.lootapi.LootAPI;

import java.util.Objects;

/**
 * Custom LootItemCondition that checks if the current loot table matches a specific ID
 * IMPROVED VERSION with better context handling
 */
public class LootTableIdCondition implements LootItemCondition {

    public static final MapCodec<LootTableIdCondition> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    ResourceLocation.CODEC.fieldOf("loot_table").forGetter(c -> c.targetTable)
            ).apply(inst, LootTableIdCondition::new)
    );

    private final ResourceLocation targetTable;

    public LootTableIdCondition(ResourceLocation targetTable) {
        this.targetTable = Objects.requireNonNull(targetTable, "Target table cannot be null");
    }

    @Override
    public boolean test(LootContext lootContext) {
        if (lootContext == null) {
            LootAPI.LOGGER.warn("LootContext is null in LootTableIdCondition");
            return false;
        }

        // Get the current loot table ID from context
        ResourceLocation currentTable = lootContext.getQueriedLootTableId();

        // If no table ID in context, try to extract from params


        boolean matches = currentTable.equals(targetTable);

        if (LootAPI.LOGGER.isDebugEnabled() && matches) {
            LootAPI.LOGGER.debug("Table ID match: {} == {}", currentTable, targetTable);
        }

        return matches;
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return GLMRegistry.LOOT_TABLE_ID_CONDITION.get();
    }

    /**
     * Helper method to create a condition array with this condition
     */
    public static LootItemCondition[] forTable(ResourceLocation tableId) {
        if (tableId == null) {
            throw new IllegalArgumentException("Table ID cannot be null");
        }
        return new LootItemCondition[] { new LootTableIdCondition(tableId) };
    }

    /**
     * Builder for creating the condition
     */
    public static Builder builder(ResourceLocation tableId) {
        return new Builder(tableId);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final ResourceLocation tableId;

        public Builder(ResourceLocation tableId) {
            this.tableId = Objects.requireNonNull(tableId, "Table ID cannot be null");
        }

        @Override
        public @NotNull LootItemCondition build() {
            return new LootTableIdCondition(tableId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LootTableIdCondition that = (LootTableIdCondition) o;
        return targetTable.equals(that.targetTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetTable);
    }

    @Override
    public String toString() {
        return "LootTableIdCondition{table=" + targetTable + "}";
    }
}