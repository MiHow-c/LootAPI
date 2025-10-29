package pl.mikof.lootapi;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.config.LootConfigManager;
import pl.mikof.lootapi.glm.GLMRegistry;

/**
 * Main NeoForge mod class for Loot API
 * Features Global Loot Modifiers for complete loot table control
 */
@Mod(LootAPI.MOD_ID)
public class LootAPI {
    public static final String MOD_ID = "lootapi";
    public static final String MOD_NAME = "Loot API";
    public static final String VERSION = "1.0.0-neoforge";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public LootAPI(IEventBus modEventBus) {
        LootLogger.logInit();

        // Register GLM serializers
        GLMRegistry.GLOBAL_LOOT_MODIFIERS.register(modEventBus);

        // Register custom loot conditions
        GLMRegistry.LOOT_CONDITIONS.register(modEventBus);

        // Setup event
        modEventBus.addListener(this::commonSetup);

        LootLogger.logSuccess("Loot API for NeoForge loading...");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                LootLogger.logInfo("Initializing core components...");

                // Load configuration
                LootConfigManager.loadAllConfigs();

                // Finalize all modifications and create JSON files
                LootTableAPI.finalizeModifications();

                LootLogger.logInitComplete();
                LootLogger.logInfo("NeoForge Global Loot Modifiers: READY");
                LootLogger.logInfo("All limitations from Fabric version are now REMOVED!");

            } catch (Exception e) {
                LOGGER.error("Failed to initialize Loot API!", e);
                throw new RuntimeException("Loot API initialization failed", e);
            }
        });
    }
}