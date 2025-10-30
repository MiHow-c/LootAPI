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
 * Global Loot Modifier - usuwa określony przedmiot z dropu
 */
public class RemoveItemModifier extends LootModifier {
    public static final MapCodec<RemoveItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).and(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item)
            ).apply(inst, RemoveItemModifier::new)
    );

    private final Item item;

    public RemoveItemModifier(LootItemCondition[] conditions, Item item) {
        super(conditions);
        this.item = item;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Usuń wszystkie ItemStacki z tym przedmiotem
        generatedLoot.removeIf(stack -> stack.getItem() == item);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends RemoveItemModifier> codec() {
        return CODEC;
    }
}
