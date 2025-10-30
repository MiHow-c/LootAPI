package pl.mikof.lootapi.glm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Global Loot Modifier - mnoży ilość wszystkich dropów
 */
public class MultiplyDropsModifier extends LootModifier {
    public static final MapCodec<MultiplyDropsModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).and(
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
        if (multiplier <= 1.0f) {
            return generatedLoot;
        }

        ObjectArrayList<ItemStack> multipliedLoot = new ObjectArrayList<>();

        for (ItemStack stack : generatedLoot) {
            // Oblicz nową ilość
            float newAmount = stack.getCount() * multiplier;
            int baseCount = (int) newAmount;
            float fractional = newAmount - baseCount;

            // Dodaj pełne stacki
            if (baseCount > 0) {
                ItemStack multipliedStack = stack.copy();
                multipliedStack.setCount(baseCount);
                multipliedLoot.add(multipliedStack);
            }

            // Dla części ułamkowej - losuj czy dodać dodatkowy item
            if (fractional > 0 && context.getRandom().nextFloat() < fractional) {
                ItemStack extraStack = stack.copy();
                extraStack.setCount(1);
                multipliedLoot.add(extraStack);
            }
        }

        return multipliedLoot;
    }

    @Override
    public MapCodec<? extends MultiplyDropsModifier> codec() {
        return CODEC;
    }
}
