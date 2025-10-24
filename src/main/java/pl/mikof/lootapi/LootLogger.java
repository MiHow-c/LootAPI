package pl.mikof.lootapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Profesjonalny system logowania dla Loot API
 * Z kolorowymi logami i szczegółowymi informacjami
 */
public class LootLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("LootAPI");
    
    // Emoji/Symbole
    private static final String CHECK = "✓";
    private static final String CROSS = "✗";
    private static final String ARROW = "→";
    private static final String STAR = "★";
    private static final String PLUS = "+";
    private static final String MINUS = "-";
    private static final String INFO = "ℹ";
    
    private static boolean detailedLogging = false;  // Wyłącz szczegółowe logi domyślnie
    private static int modificationCount = 0;
    private static int errorCount = 0;
    
    // ========== INICJALIZACJA ==========
    
    public static void logInit() {
        LOGGER.info("");
        LOGGER.info("╔════════════════════════════════════════╗");
        LOGGER.info("║                                        ║");
        LOGGER.info("║  LOOT API v1.0.0                       ║");
        LOGGER.info("║  by MiKoF                              ║");
        LOGGER.info("║                                        ║");
        LOGGER.info("╚════════════════════════════════════════╝");
        LOGGER.info("");
    }
    
    public static void logInitComplete() {
        LOGGER.info(CHECK + " Loot API initialized successfully!");
        LOGGER.info(INFO + " Ready to modify loot tables...");
        LOGGER.info("");
    }
    
    // ========== MODYFIKACJE ==========
    
    public static void logModification(String operation, net.minecraft.util.Identifier tableId, Object... details) {
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
    
    public static void logItemAdded(net.minecraft.util.Identifier tableId, net.minecraft.item.Item item, int weight) {
        modificationCount++;
        LOGGER.info(PLUS + " Added " + tableId + " " + ARROW + " " + item + " (weight: " + weight + ")");
    }
    
    public static void logItemRemoved(net.minecraft.util.Identifier tableId, net.minecraft.item.Item item) {
        modificationCount++;
        LOGGER.info(MINUS + " Removed " + tableId + " " + ARROW + " " + item);
    }
    
    public static void logItemReplaced(net.minecraft.util.Identifier tableId, 
                                       net.minecraft.item.Item oldItem, 
                                       net.minecraft.item.Item newItem) {
        modificationCount++;
        LOGGER.info("↔ Replaced " + tableId + " " + ARROW + " " + oldItem + " → " + newItem);
    }
    
    public static void logMultiplier(net.minecraft.util.Identifier tableId, float multiplier) {
        modificationCount++;
        LOGGER.info("× Multiplier " + tableId + " " + ARROW + " " + multiplier + "x");
    }
    
    public static void logNewLootTable(net.minecraft.util.Identifier tableId, String type) {
        modificationCount++;
        LOGGER.info(STAR + " Created " + tableId + " (" + type + ")");
    }
    
    public static void logBulkOperation(String pattern, int affected) {
        modificationCount += affected;
        LOGGER.info("⚡ Bulk operation pattern: " + pattern + " " + ARROW + " " + affected + " tables modified");
    }
    
    // ========== APLIKACJA MODYFIKACJI ==========
    
    public static void logApplyingModifications() {
        LOGGER.info("");
        LOGGER.info("════════════════════════════════════════");
        LOGGER.info("  Applying loot table modifications...");
        LOGGER.info("════════════════════════════════════════");
    }
    
    public static void logTableModified(net.minecraft.util.Identifier tableId, int modifierCount) {
        if (detailedLogging) {
            LOGGER.info(CHECK + " " + tableId + " " + ARROW + " " + modifierCount + " modifier" + (modifierCount != 1 ? "s" : "") + " applied");
        }
    }
    
    public static void logTableSkipped(net.minecraft.util.Identifier tableId, String reason) {
        if (detailedLogging) {
            LOGGER.info("⊘ Skipped " + tableId + " (" + reason + ")");
        }
    }
    
    // ========== PODSUMOWANIE ==========
    
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
    
    // ========== BŁĘDY ==========
    
    public static void logError(String operation, net.minecraft.util.Identifier tableId, Exception e) {
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
    
    // ========== DEBUG ==========
    
    public static void logDebug(net.minecraft.util.Identifier tableId) {
        LOGGER.info("");
        LOGGER.info("╔════════════════════════════════════════╗");
        LOGGER.info("║  DEBUG: " + tableId);
        LOGGER.info("╚════════════════════════════════════════╝");
    }
    
    public static void logDebugInfo(String key, Object value) {
        LOGGER.info("  " + key + ": " + value);
    }
    
    // ========== CONFIG ==========
    
    public static void logConfigLoaded(String filename, int modifications) {
        LOGGER.info(CHECK + " Config loaded " + filename + " (" + modifications + " modifications)");
    }
    
    public static void logConfigSaved(String filename) {
        LOGGER.info(CHECK + " Config saved " + filename);
    }
    
    // ========== UTILITIES ==========
    
    public static void setDetailedLogging(boolean enabled) {
        detailedLogging = enabled;
        LOGGER.info((enabled ? "Enabled" : "Disabled") + " detailed logging");
    }
    
    public static void resetCounters() {
        modificationCount = 0;
        errorCount = 0;
    }
    
    public static int getModificationCount() {
        return modificationCount;
    }
    
    public static int getErrorCount() {
        return errorCount;
    }
    
    public static boolean isDebugEnabled() {
        return detailedLogging;
    }
    
    // ========== SEPERATORY ==========
    
    public static void logSeparator() {
        LOGGER.info("────────────────────────────────────────");
    }
    
    public static void logDoubleSeparator() {
        LOGGER.info("════════════════════════════════════════");
    }
    
    public static void logEmptyLine() {
        LOGGER.info("");
    }
    
    // ========== CUSTOM ==========
    
    public static void logSuccess(String message) {
        LOGGER.info(CHECK + " " + message);
    }
    
    public static void logInfo(String message) {
        LOGGER.info(INFO + " " + message);
    }
    
    public static void logHighlight(String message) {
        LOGGER.info(STAR + " " + message);
    }
}
