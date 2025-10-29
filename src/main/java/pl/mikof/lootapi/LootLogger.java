package pl.mikof.lootapi;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Professional logging system for Loot API
 */

public class LootLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("LootAPI");

    // Symbols
    private static final String CHECK = "✓";
    private static final String CROSS = "✗";
    private static final String ARROW = "→";
    private static final String STAR = "★";
    private static final String PLUS = "+";
    private static final String MINUS = "-";
    private static final String INFO = "ℹ";

    private static boolean detailedLogging = false;
    private static int modificationCount = 0;
    private static int errorCount = 0;

    public static void logInit() {
        LOGGER.info("");
        LOGGER.info("╔════════════════════════════════════════╗");
        LOGGER.info("║                                        ║");
        LOGGER.info("║  LOOT API v1.0.0 - NeoForge            ║");
        LOGGER.info("║  by MiKoF                              ║");
        LOGGER.info("║                                        ║");
        LOGGER.info("╚════════════════════════════════════════╝");
        LOGGER.info("");
    }

    public static void logInitComplete() {
        LOGGER.info(CHECK + " Loot API initialized successfully!");
        LOGGER.info(INFO + " Global Loot Modifiers: ACTIVE");
        LOGGER.info(INFO + " Ready to modify loot tables...");
        LOGGER.info("");
    }

    public static void logItemAdded(ResourceLocation tableId, Item item, int weight) {
        modificationCount++;
        LOGGER.info(PLUS + " Added " + tableId + " " + ARROW + " " + item + " (weight: " + weight + ")");
    }

    public static void logItemRemoved(ResourceLocation tableId, Item item) {
        modificationCount++;
        LOGGER.info(MINUS + " Removed " + tableId + " " + ARROW + " " + item);
    }

    public static void logItemReplaced(ResourceLocation tableId, Item oldItem, Item newItem) {
        modificationCount++;
        LOGGER.info("↔ Replaced " + tableId + " " + ARROW + " " + oldItem + " → " + newItem);
    }

    public static void logMultiplier(ResourceLocation tableId, float multiplier) {
        modificationCount++;
        LOGGER.info("× Multiplier " + tableId + " " + ARROW + " " + multiplier + "x");
    }

    public static void logModification(String operation, ResourceLocation tableId, Object... details) {
        modificationCount++;
        StringBuilder message = new StringBuilder();
        message.append(PLUS).append(" ");
        message.append(operation).append(" ");
        message.append(tableId);

        if (details.length > 0) {
            message.append(" ").append(ARROW).append(" ");
            for (int i = 0; i < details.length; i++) {
                message.append(details[i]);
                if (i < details.length - 1) {
                    message.append(", ");
                }
            }
        }

        LOGGER.info(message.toString());
    }

    public static void logError(String operation, ResourceLocation tableId, Exception e) {
        errorCount++;
        LOGGER.error(CROSS + " ERROR " + operation + " " + tableId);
        LOGGER.error("  Reason: " + e.getMessage());

        if (detailedLogging) {
            LOGGER.error("  Stack trace:", e);
        }
    }

    public static void logWarning(String message) {
        LOGGER.warn("⚠ " + message);
    }

    public static void logSuccess(String message) {
        LOGGER.info(CHECK + " " + message);
    }

    public static void logInfo(String message) {
        LOGGER.info(INFO + " " + message);
    }

    public static void setDetailedLogging(boolean enabled) {
        detailedLogging = enabled;
    }

    public static boolean isDebugEnabled() {
        return detailedLogging;
    }

    public static int getModificationCount() {
        return modificationCount;
    }

    public static int getErrorCount() {
        return errorCount;
    }

    public static void resetCounters() {
        modificationCount = 0;
        errorCount = 0;
    }

    public static void logSummary(int tablesModified, int totalModifiers) {
        LOGGER.info("");
        LOGGER.info("════════════════════════════════════════");
        LOGGER.info("  " + CHECK + " Loot API Summary");
        LOGGER.info("════════════════════════════════════════");
        LOGGER.info("  Tables modified: " + tablesModified);
        LOGGER.info("  Total modifiers: " + totalModifiers);
        LOGGER.info("  Operations:      " + modificationCount);

        if (errorCount > 0) {
            LOGGER.info("  Errors:          " + errorCount);
        }

        LOGGER.info("════════════════════════════════════════");
        LOGGER.info("");

        if (errorCount == 0) {
            LOGGER.info("  All modifications applied successfully! " + CHECK);
        } else {
            LOGGER.warn("  Some modifications had errors. Check logs above.");
        }

        LOGGER.info("");
    }
}