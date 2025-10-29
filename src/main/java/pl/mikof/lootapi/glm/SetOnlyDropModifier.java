package pl.mikof.lootapi.glm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Global Loot Modifier that clears all drops and sets only one specific item
 */
public class SetOnlyDropModifier extends LootModifier {

    public static final MapCodec<SetOnlyDropModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).and(inst.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item),
                    Codec.INT.optionalFieldOf("min_count", 1).forGetter(m -> m.minCount),
                    Codec.INT.optionalFieldOf("max_count", 1).forGetter(m -> m.maxCount)
            )).apply(inst, SetOnlyDropModifier::new)
    );

    private final Item item;
    private final int minCount;
    private final int maxCount;

    public SetOnlyDropModifier(LootItemCondition[] conditions, Item item, int minCount, int maxCount) {
        super(conditions);
        this.item = item;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Clear everything and add only our item
        generatedLoot.clear();

        RandomSource random = context.getRandom();
        int count = minCount == maxCount ? minCount : random.nextIntBetweenInclusive(minCount, maxCount);

        generatedLoot.add(new ItemStack(item, count));
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}