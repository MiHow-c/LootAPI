package pl.mikof.lootapi;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.config.LootConfigManager;
import pl.mikof.lootapi.system.LootModificationSystem;

/**
 * LootAPI - Kompletny system modyfikacji loot tables dla NeoForge
 * Wersja 2.0 - W pełni funkcjonalna
 */
@Mod(LootAPI.MOD_ID)
public class LootAPI {
    public static final String MOD_ID = "lootapi";
    public static final Logger LOGGER = LoggerFactory.getLogger("LootAPI");

    private static LootAPI instance;

    public LootAPI(IEventBus modEventBus) {
        instance = this;

        LOGGER.info("=================================");
        LOGGER.info("  LootAPI v2.0 Initializing...  ");
        LOGGER.info("=================================");

        // Rejestracja event handlerów
        modEventBus.addListener(this::commonSetup);

        // Rejestracja do NeoForge event bus
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new LootEventHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Loading LootAPI modifications...");

            // Załaduj konfigurację
            LootConfigManager.loadAllConfigs();

            // Inicjalizuj system modyfikacji
            LootModificationSystem.initialize();

            LOGGER.info("LootAPI ready! Registered {} modifications",
                    LootModificationSystem.getModificationCount());
        });
    }

    public static LootAPI getInstance() {
        return instance;
    }

    /**
     * Event handler dla modyfikacji loot tables
     */
    public static class LootEventHandler {
        @SubscribeEvent
        public void onLootTableLoad(LootTableLoadEvent event) {
            // Aplikuj modyfikacje do ładowanej tabeli
            LootModificationSystem.applyModifications(event);
        }
    }
}