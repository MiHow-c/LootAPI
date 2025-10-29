package pl.mikof.lootapi.glm;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Global Loot Modifier that REPLACES one item with another
 */
public class ReplaceItemModifier extends LootModifier {

    public static final MapCodec<ReplaceItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).and(inst.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("old_item").forGetter(m -> m.oldItem),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("new_item").forGetter(m -> m.newItem),
                    com.mojang.serialization.Codec.BOOL.optionalFieldOf("preserve_count", true).forGetter(m -> m.preserveCount)
            )).apply(inst, ReplaceItemModifier::new)
    );

    private final Item oldItem;
    private final Item newItem;
    private final boolean preserveCount;

    public ReplaceItemModifier(LootItemCondition[] conditions, Item oldItem, Item newItem, boolean preserveCount) {
        super(conditions);
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.preserveCount = preserveCount;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // ✅ TRUE REPLACEMENT - replace only the specific item!
        for (int i = 0; i < generatedLoot.size(); i++) {
            ItemStack stack = generatedLoot.get(i);
            if (stack.is(oldItem)) {
                int count = preserveCount ? stack.getCount() : 1;
                generatedLoot.set(i, new ItemStack(newItem, count));
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}