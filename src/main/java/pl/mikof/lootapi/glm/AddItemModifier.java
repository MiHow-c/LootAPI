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
 * Global Loot Modifier - dodaje przedmiot do dropu
 */
public class AddItemModifier extends LootModifier {
    public static final MapCodec<AddItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).and(inst.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item),
                    Codec.INT.fieldOf("min_count").forGetter(m -> m.minCount),
                    Codec.INT.fieldOf("max_count").forGetter(m -> m.maxCount),
                    Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(m -> m.chance)
            )).apply(inst, AddItemModifier::new)
    );

    private final Item item;
    private final int minCount;
    private final int maxCount;
    private final float chance;

    public AddItemModifier(LootItemCondition[] conditions, Item item, int minCount, int maxCount, float chance) {
        super(conditions);
        this.item = item;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.chance = chance;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Sprawdź szansę
        if (context.getRandom().nextFloat() > chance) {
            return generatedLoot;
        }

        // Oblicz ilość (losowa między min a max)
        int count = minCount;
        if (maxCount > minCount) {
            count = minCount + context.getRandom().nextInt(maxCount - minCount + 1);
        }

        // Dodaj przedmiot
        ItemStack stack = new ItemStack(item, count);
        generatedLoot.add(stack);

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends AddItemModifier> codec() {
        return CODEC;
    }
}
