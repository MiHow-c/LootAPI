package pl.mikof.lootapi.glm;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Global Loot Modifier - czyści całą tabelę loot (usuwa wszystkie dropy)
 */
public class ClearTableModifier extends LootModifier {
    public static final MapCodec<ClearTableModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).apply(inst, ClearTableModifier::new)
    );

    public ClearTableModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Zwróć pustą listę
        return new ObjectArrayList<>();
    }

    @Override
    public MapCodec<? extends ClearTableModifier> codec() {
        return CODEC;
    }
}
