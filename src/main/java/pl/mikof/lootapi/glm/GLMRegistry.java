package pl.mikof.lootapi.glm;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import pl.mikof.lootapi.LootAPI;

import java.util.function.Supplier;

/**
 * Registry for Global Loot Modifier serializers AND custom conditions
 */
public class GLMRegistry {

    // Register GLM serializers
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, LootAPI.MOD_ID);

    // Register custom loot conditions
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, LootAPI.MOD_ID);

    // ========== LOOT CONDITIONS ==========

    public static final Supplier<LootItemConditionType> LOOT_TABLE_ID_CONDITION =
            LOOT_CONDITIONS.register("loot_table_id",
                    () -> new LootItemConditionType(LootTableIdCondition.CODEC));

    // ========== GLOBAL LOOT MODIFIERS ==========

    // Add Item Modifier
    public static final Supplier<MapCodec<AddItemModifier>> ADD_ITEM =
            GLOBAL_LOOT_MODIFIERS.register("add_item", () -> AddItemModifier.CODEC);

    // Remove Item Modifier
    public static final Supplier<MapCodec<RemoveItemModifier>> REMOVE_ITEM =
            GLOBAL_LOOT_MODIFIERS.register("remove_item", () -> RemoveItemModifier.CODEC);

    // Replace Item Modifier
    public static final Supplier<MapCodec<ReplaceItemModifier>> REPLACE_ITEM =
            GLOBAL_LOOT_MODIFIERS.register("replace_item", () -> ReplaceItemModifier.CODEC);

    // Multiply Drops Modifier
    public static final Supplier<MapCodec<MultiplyDropsModifier>> MULTIPLY_DROPS =
            GLOBAL_LOOT_MODIFIERS.register("multiply_drops", () -> MultiplyDropsModifier.CODEC);

    // Add Item with Count
    public static final Supplier<MapCodec<AddItemWithCountModifier>> ADD_ITEM_WITH_COUNT =
            GLOBAL_LOOT_MODIFIERS.register("add_item_with_count", () -> AddItemWithCountModifier.CODEC);

    // Add Item with Chance
    public static final Supplier<MapCodec<AddItemWithChanceModifier>> ADD_ITEM_WITH_CHANCE =
            GLOBAL_LOOT_MODIFIERS.register("add_item_with_chance", () -> AddItemWithChanceModifier.CODEC);

    // Clear Loot Table
    public static final Supplier<MapCodec<ClearLootTableModifier>> CLEAR_LOOT_TABLE =
            GLOBAL_LOOT_MODIFIERS.register("clear_loot_table", () -> ClearLootTableModifier.CODEC);

    // Set Only Drop
    public static final Supplier<MapCodec<SetOnlyDropModifier>> SET_ONLY_DROP =
            GLOBAL_LOOT_MODIFIERS.register("set_only_drop", () -> SetOnlyDropModifier.CODEC);
}