package pl.mikof.lootapi;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.LoggerFactory;
import pl.mikof.lootapi.glm.LootModifiers;
import pl.mikof.lootapi.util.ColoredLogger;

/**
 * LootAPI - Kompletny system modyfikacji loot tables dla NeoForge
 * Wersja 3.0 - Global Loot Modifiers
 */
@Mod(LootAPI.MOD_ID)
public class LootAPI {
    public static final String MOD_ID = "lootapi";
    public static final String VERSION = "3.0";
    private static final ColoredLogger LOGGER = new ColoredLogger(LoggerFactory.getLogger("LootAPI"));

    private static LootAPI instance;

    public LootAPI(IEventBus modEventBus) {
        instance = this;

        LOGGER.box(
            "LootAPI v" + VERSION,
            "Global Loot Modifiers System",
            "Initializing..."
        );

        // Rejestracja Global Loot Modifiers
        LOGGER.init("Registering Global Loot Modifiers");
        LootModifiers.register(modEventBus);

        // Rejestracja event handlerów
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.success("LootAPI setup complete!");
            LOGGER.info("Global Loot Modifiers are ready to use");
            LOGGER.info("Use LootTableAPI or JSON files to create modifiers");
            LOGGER.separator();
        });
    }

    public static LootAPI getInstance() {
        return instance;
    }

    public static ColoredLogger getLogger() {
        return LOGGER;
    }
}