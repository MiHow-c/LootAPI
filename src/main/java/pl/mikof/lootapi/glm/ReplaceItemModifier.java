package pl.mikof.lootapi.glm;

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
 * Global Loot Modifier - zamienia jeden przedmiot na inny
 */
public class ReplaceItemModifier extends LootModifier {
    public static final MapCodec<ReplaceItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).and(inst.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("old_item").forGetter(m -> m.oldItem),
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("new_item").forGetter(m -> m.newItem)
            )).apply(inst, ReplaceItemModifier::new)
    );

    private final Item oldItem;
    private final Item newItem;

    public ReplaceItemModifier(LootItemCondition[] conditions, Item oldItem, Item newItem) {
        super(conditions);
        this.oldItem = oldItem;
        this.newItem = newItem;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Zamień wszystkie wystąpienia starego przedmiotu na nowy
        for (int i = 0; i < generatedLoot.size(); i++) {
            ItemStack stack = generatedLoot.get(i);
            if (stack.getItem() == oldItem) {
                // Zachowaj ilość z oryginalnego stacka
                ItemStack newStack = new ItemStack(newItem, stack.getCount());
                generatedLoot.set(i, newStack);
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends ReplaceItemModifier> codec() {
        return CODEC;
    }
}
