package pl.mikof.lootapi.examples;

import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import pl.mikof.lootapi.LootTableAPI;
import pl.mikof.lootapi.LootTables;

/**
 * PRZYKŁAD UŻYCIA LootAPI
 *
 * To jest przykładowy mod pokazujący jak używać LootAPI.
 * Skopiuj ten kod do swojego moda i dostosuj do swoich potrzeb.
 *
 * WAŻNE: Ten plik jest tylko przykładem i nie jest używany przez samo API!
 */
public class ExampleMod {

    // W swoim modzie użyj tego w klasie głównej z @Mod
    public static void setupLootModifications(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Inicjalizuj API
            LootTableAPI.init();

            // ========== PRZYKŁAD 1: Dodawanie przedmiotów ==========

            // Dodaj diamenty do coal ore (10% szans na 1-3 diamenty)
            LootTableAPI.addItemToTable(
                    LootTables.Blocks.COAL_ORE,
                    Items.DIAMOND,
                    1, 3,
                    0.1f
            );

            // Dodaj złote jabłko do zombie (zawsze 1)
            LootTableAPI.addItemToTable(
                    LootTables.Entities.ZOMBIE,
                    Items.GOLDEN_APPLE
            );

            // ========== PRZYKŁAD 2: Usuwanie przedmiotów ==========

            // Usuń rotten flesh z zombie
            LootTableAPI.removeItemFromTable(
                    LootTables.Entities.ZOMBIE,
                    Items.ROTTEN_FLESH
            );

            // ========== PRZYKŁAD 3: Zamienianie przedmiotów ==========

            // Zamień dirt na diamond z grass block
            LootTableAPI.replaceItem(
                    LootTables.Blocks.GRASS_BLOCK,
                    Items.DIRT,
                    Items.DIAMOND
            );

            // ========== PRZYKŁAD 4: Mnożenie dropów ==========

            // Podwójne dropy z iron ore
            LootTableAPI.multiplyDrops(
                    LootTables.Blocks.IRON_ORE,
                    2.0f
            );

            // Potrójne dropy z diamond ore
            LootTableAPI.multiplyDrops(
                    LootTables.Blocks.DIAMOND_ORE,
                    3.0f
            );

            // ========== PRZYKŁAD 5: Czyszczenie tabel ==========

            // Wyłącz dropy z stone (kamień nie będzie dropował cobblestone)
            // UWAGA: W vanilla Minecraft, stone naturalnie dropuje cobblestone, nie stone!
            // Ten kod wyłączy wszystkie dropy z stone, więc nie dostaniesz cobblestone
            LootTableAPI.disableLootTable(
                    LootTables.Blocks.STONE
            );

            // ALTERNATYWNIE: Jeśli chcesz tylko usunąć cobblestone ale zostawić inne dropy:
            // LootTableAPI.removeItemFromTable(
            //         LootTables.Blocks.STONE,
            //         Items.COBBLESTONE
            // );

            // ========== PRZYKŁAD 6: Ustawianie tylko jednego dropu ==========

            // Gravel dropuje tylko flinty (2-5 sztuk)
            LootTableAPI.setOnlyDrop(
                    LootTables.Blocks.GRAVEL,
                    Items.FLINT,
                    2, 5
            );

            // ========== PRZYKŁAD 7: Wiele tabel jednocześnie ==========

            // Dodaj emeraldy do wszystkich rud deepslate
            var builder = LootTableAPI.createCustomModifier("add", "emerald_from_deepslate")
                    .forTables(
                            LootTables.Blocks.DEEPSLATE_COAL_ORE,
                            LootTables.Blocks.DEEPSLATE_IRON_ORE,
                            LootTables.Blocks.DEEPSLATE_GOLD_ORE,
                            LootTables.Blocks.DEEPSLATE_DIAMOND_ORE
                    )
                    .withItem(Items.EMERALD)
                    .withCount(1, 2)
                    .withChance(0.15f);

            LootTableAPI.registerModifier(builder);

            // ========== PRZYKŁAD 8: Niestandardowe tabele ==========

            // Dla modów z custom blokami
            LootTableAPI.addItemToTable(
                    LootTables.Blocks.modded("yourmod", "custom_ore"),
                    Items.DIAMOND,
                    1, 5
            );

            // ========== FINALIZACJA ==========

            // To zapisze wszystkie modifikacje do plików JSON
            // WAŻNE: Wywołaj to na końcu, po wszystkich modyfikacjach!
            LootTableAPI.finalizeModifiers();

            // Debug - wypisz informacje
            LootTableAPI.printDebugInfo();
        });
    }
}
