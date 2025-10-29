package pl.mikof.lootapi.glm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Global Loot Modifier that MULTIPLIES all drops
 */
public class MultiplyDropsModifier extends LootModifier {

    public static final MapCodec<MultiplyDropsModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).and(
                    Codec.FLOAT.fieldOf("multiplier").forGetter(m -> m.multiplier)
            ).apply(inst, MultiplyDropsModifier::new)
    );

    private final float multiplier;

    public MultiplyDropsModifier(LootItemCondition[] conditions, float multiplier) {
        super(conditions);
        this.multiplier = multiplier;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // ✅ MULTIPLY DROPS - works perfectly!
        for (ItemStack stack : generatedLoot) {
            int newCount = Math.max(1, Math.round(stack.getCount() * multiplier));
            stack.setCount(newCount);
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}