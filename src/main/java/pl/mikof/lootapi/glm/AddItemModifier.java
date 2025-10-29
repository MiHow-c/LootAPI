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
 * Global Loot Modifier that adds an item to the loot table
 */
public class AddItemModifier extends LootModifier {

    public static final MapCodec<AddItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).and(inst.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item),
                    Codec.INT.fieldOf("weight").forGetter(m -> m.weight)
            )).apply(inst, AddItemModifier::new)
    );

    private final Item item;
    private final int weight;

    public AddItemModifier(LootItemCondition[] conditions, Item item, int weight) {
        super(conditions);
        this.item = item;
        this.weight = weight;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // ✅ Weight implementation: higher weight = more likely to add the item
        // We use a simple probability system: weight out of 100
        RandomSource random = context.getRandom();

        // For weights > 100, we add the item multiple times
        int guaranteedAdds = weight / 100;
        int remainingWeight = weight % 100;

        // Add guaranteed items
        for (int i = 0; i < guaranteedAdds; i++) {
            generatedLoot.add(new ItemStack(item));
        }

        // Roll for the remaining weight
        if (remainingWeight > 0 && random.nextInt(100) < remainingWeight) {
            generatedLoot.add(new ItemStack(item));
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}