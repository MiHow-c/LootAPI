package pl.mikof.lootapi.util;

import org.slf4j.Logger;

/**
 * Pomocnicza klasa do kolorowych logów
 * Używa ANSI escape codes dla lepszej czytelności
 */
public class ColoredLogger {
    // ANSI Color Codes
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String RED = "\u001B[31m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String GRAY = "\u001B[90m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";

    private final Logger logger;
    private final boolean colorsEnabled;

    public ColoredLogger(Logger logger) {
        this.logger = logger;
        // Zawsze włączaj kolory w Minecraft - są wspierane
        this.colorsEnabled = true;
    }

    /**
     * Konstruktor z możliwością wyłączenia kolorów
     */
    public ColoredLogger(Logger logger, boolean enableColors) {
        this.logger = logger;
        this.colorsEnabled = enableColors;
    }

    /**
     * Log sukcesu (zielony)
     */
    public void success(String message, Object... args) {
        if (colorsEnabled) {
            logger.info(GREEN + "✓ " + message + RESET, args);
        } else {
            logger.info("✓ " + message, args);
        }
    }

    /**
     * Log akcji (cyan)
     */
    public void action(String message, Object... args) {
        if (colorsEnabled) {
            logger.info(CYAN + "→ " + message + RESET, args);
        } else {
            logger.info("→ " + message, args);
        }
    }

    /**
     * Log informacji (niebieski)
     */
    public void info(String message, Object... args) {
        if (colorsEnabled) {
            logger.info(BLUE + message + RESET, args);
        } else {
            logger.info(message, args);
        }
    }

    /**
     * Log ostrzeżenia (żółty)
     */
    public void warn(String message, Object... args) {
        if (colorsEnabled) {
            logger.warn(YELLOW + "⚠ " + message + RESET, args);
        } else {
            logger.warn("⚠ " + message, args);
        }
    }

    /**
     * Log nagłówka (bold cyan)
     */
    public void header(String message) {
        if (colorsEnabled) {
            logger.info(BOLD + CYAN + message + RESET);
        } else {
            logger.info(message);
        }
    }

    /**
     * Zwykły logger bez kolorów
     */
    public void plain(String message, Object... args) {
        logger.info(message, args);
    }

    /**
     * Error (czerwony) - przekierowuje do standardowego error
     */
    public void error(String message, Object... args) {
        logger.error(RED + "✗ " + message + RESET, args);
    }

    /**
     * Error z wyjątkiem
     */
    public void error(String message, Throwable throwable) {
        logger.error(RED + "✗ " + message + RESET, throwable);
    }

    /**
     * Log debugowania (szary, przygaszony)
     */
    public void debug(String message, Object... args) {
        if (colorsEnabled) {
            logger.debug(GRAY + message + RESET, args);
        } else {
            logger.debug(message, args);
        }
    }

    /**
     * Log inicjalizacji (magenta/fioletowy)
     */
    public void init(String message, Object... args) {
        if (colorsEnabled) {
            logger.info(MAGENTA + "⚙ " + message + RESET, args);
        } else {
            logger.info("⚙ " + message, args);
        }
    }

    /**
     * Wypisuje separator (linia)
     */
    public void separator() {
        if (colorsEnabled) {
            logger.info(GRAY + "═══════════════════════════════════════" + RESET);
        } else {
            logger.info("═══════════════════════════════════════");
        }
    }

    /**
     * Box header (nagłówek w ramce)
     */
    public void box(String... lines) {
        if (colorsEnabled) {
            logger.info(BOLD + CYAN + "╔═══════════════════════════════════════╗" + RESET);
            for (String line : lines) {
                // Wycentruj tekst
                int padding = (39 - line.length()) / 2;
                String paddedLine = " ".repeat(Math.max(0, padding)) + line;
                logger.info(BOLD + CYAN + "║ " + paddedLine + " ".repeat(Math.max(0, 39 - paddedLine.length())) + "║" + RESET);
            }
            logger.info(BOLD + CYAN + "╚═══════════════════════════════════════╝" + RESET);
        } else {
            logger.info("╔═══════════════════════════════════════╗");
            for (String line : lines) {
                int padding = (39 - line.length()) / 2;
                String paddedLine = " ".repeat(Math.max(0, padding)) + line;
                logger.info("║ " + paddedLine + " ".repeat(Math.max(0, 39 - paddedLine.length())) + "║");
            }
            logger.info("╚═══════════════════════════════════════╝");
        }
    }

    /**
     * Zwraca surowy logger (dla kompatybilności)
     */
    public Logger getRawLogger() {
        return logger;
    }
}
