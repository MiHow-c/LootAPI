package pl.mikof.lootapi.glm;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import pl.mikof.lootapi.LootAPI;

import java.util.function.Supplier;

/**
 * Rejestracja wszystkich Global Loot Modifiers
 */
public class LootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, LootAPI.MOD_ID);

    // Rejestracja wszystkich modifierów
    public static final Supplier<MapCodec<AddItemModifier>> ADD_ITEM =
            GLM_CODECS.register("add_item", () -> AddItemModifier.CODEC);

    public static final Supplier<MapCodec<RemoveItemModifier>> REMOVE_ITEM =
            GLM_CODECS.register("remove_item", () -> RemoveItemModifier.CODEC);

    public static final Supplier<MapCodec<ReplaceItemModifier>> REPLACE_ITEM =
            GLM_CODECS.register("replace_item", () -> ReplaceItemModifier.CODEC);

    public static final Supplier<MapCodec<MultiplyDropsModifier>> MULTIPLY_DROPS =
            GLM_CODECS.register("multiply_drops", () -> MultiplyDropsModifier.CODEC);

    public static final Supplier<MapCodec<ClearTableModifier>> CLEAR_TABLE =
            GLM_CODECS.register("clear_table", () -> ClearTableModifier.CODEC);

    public static final Supplier<MapCodec<SetOnlyDropModifier>> SET_ONLY_DROP =
            GLM_CODECS.register("set_only_drop", () -> SetOnlyDropModifier.CODEC);

    /**
     * Rejestruje wszystkie GLM do event bus
     */
    public static void register(IEventBus modEventBus) {
        GLM_CODECS.register(modEventBus);
        LootAPI.getLogger().success("Registered {} Global Loot Modifiers", GLM_CODECS.getEntries().size());
    }
}
