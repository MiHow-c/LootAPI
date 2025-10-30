package pl.mikof.lootapi.glm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Global Loot Modifier - czyści tabelę i ustawia tylko jeden przedmiot jako drop
 */
public class SetOnlyDropModifier extends LootModifier {
    public static final MapCodec<SetOnlyDropModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).and(inst.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item),
                    Codec.INT.fieldOf("min_count").forGetter(m -> m.minCount),
                    Codec.INT.fieldOf("max_count").forGetter(m -> m.maxCount)
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
        // Wyczyść wszystko i dodaj tylko nasz przedmiot
        ObjectArrayList<ItemStack> newLoot = new ObjectArrayList<>();

        // Oblicz ilość (losowa między min a max)
        int count = minCount;
        if (maxCount > minCount) {
            count = minCount + context.getRandom().nextInt(maxCount - minCount + 1);
        }

        ItemStack stack = new ItemStack(item, count);
        newLoot.add(stack);

        return newLoot;
    }

    @Override
    public MapCodec<? extends SetOnlyDropModifier> codec() {
        return CODEC;
    }
}
