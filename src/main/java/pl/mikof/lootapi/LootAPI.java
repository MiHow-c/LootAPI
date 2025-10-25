package pl.mikof.lootapi;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Główna klasa Loot API
 * Profesjonalny system logowania dla wszystkich operacji na loot tables
 */
public class LootAPI implements ModInitializer {
    public static final String MOD_ID = "lootapi";
    public static final String MOD_NAME = "Loot API";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static boolean initialized = false;
    
    @Override
    public void onInitialize() {
        // Inicjalizacja z profesjonalnym logowaniem
        LootLogger.logInit();
        
        try {
            // WAŻNE: Oznacz jako zainicjalizowane PRZED jakimikolwiek operacjami
            initialized = true;
            
            // Inicjalizacja komponentów (może ładować z configów!)
            initializeComponents();
            
            // Teraz można bezpiecznie ładować przykłady
            //loadExamples();
            
            LootLogger.logInitComplete();
            
        } catch (Exception e) {
            LOGGER.error("✗ Failed to initialize Loot API!", e);
            throw new RuntimeException("Loot API initialization failed", e);
        }
    }
    
    /**
     * Inicjalizuje wszystkie komponenty API
     */
    private void initializeComponents() {
        LootLogger.logInfo("Initializing core components...");
        
        // Zarejestruj event handler
        pl.mikof.lootapi.event.LootTableEventHandler.register();
        LootLogger.logSuccess("Event handlers registered");
        
        // Załaduj konfigurację (może wywoływać API methods!)
        try {
            pl.mikof.lootapi.config.LootConfigManager.loadAllConfigs();
            LootLogger.logSuccess("Configuration loaded");
        } catch (Exception e) {
            LootLogger.logWarning("No configuration found, using defaults");
        }
        
        LootLogger.logSuccess("API ready for use");
    }
    
    /**
     * Ładuje przykładowe modyfikacje
     * Wywoływane PO inicjalizacji API
     */
    //private void loadExamples() {
        //LootLogger.logInfo("Loading example modifications...");
        
        // Zakomentuj/odkomentuj linie poniżej aby włączyć/wyłączyć przykłady
        //try {
            //pl.mikof.lootapi.examples.LootAPIExamples.registerExamples();
            //LootLogger.logSuccess("Examples loaded successfully");
        //} catch (Exception e) {
            //LootLogger.logWarning("Failed to load examples: " + e.getMessage());
        //}
    //}
    
    /**
     * Sprawdza czy API zostało zainicjalizowane
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Sprawdza czy API zostało zainicjalizowane i loguje ostrzeżenie jeśli nie
     */
    public static boolean checkInitialized(String operation) {
        if (!initialized) {
            LootLogger.logWarning("Attempted to " + operation + " before API initialization!");
            return false;
        }
        return true;
    }
    
    /**
     * Resetuje liczniki dla nowej sesji
     */
    public static void resetCounters() {
        LootLogger.resetCounters();
    }
}
