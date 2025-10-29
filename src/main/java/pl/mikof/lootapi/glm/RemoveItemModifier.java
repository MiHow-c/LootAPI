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
 * Global Loot Modifier that REMOVES an item from the loot table
 *
 */
public class RemoveItemModifier extends LootModifier {

    public static final MapCodec<RemoveItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).and(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.itemToRemove)
            ).apply(inst, RemoveItemModifier::new)
    );

    private final Item itemToRemove;

    public RemoveItemModifier(LootItemCondition[] conditions, Item itemToRemove) {
        super(conditions);
        this.itemToRemove = itemToRemove;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // ✅ FINALLY! We can remove items from loot!
        generatedLoot.removeIf(stack -> stack.is(itemToRemove));
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}