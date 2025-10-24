package pl.mikof.lootapi;

import net.minecraft.util.Identifier;

/**
 * Klasa pomocnicza z predefiniowanymi ścieżkami do loot tables
 * Ułatwia dostęp - zamiast new Identifier("minecraft", "blocks/diamond_ore")
 * można użyć LootTables.Blocks.DIAMOND_ORE
 */
public class LootTables {
    
    // ========== BLOKI - RUDY ==========
    public static class Blocks {
        // Vanilla ores
        public static final Identifier COAL_ORE = id("blocks/coal_ore");
        public static final Identifier DEEPSLATE_COAL_ORE = id("blocks/deepslate_coal_ore");
        public static final Identifier IRON_ORE = id("blocks/iron_ore");
        public static final Identifier DEEPSLATE_IRON_ORE = id("blocks/deepslate_iron_ore");
        public static final Identifier GOLD_ORE = id("blocks/gold_ore");
        public static final Identifier DEEPSLATE_GOLD_ORE = id("blocks/deepslate_gold_ore");
        public static final Identifier DIAMOND_ORE = id("blocks/diamond_ore");
        public static final Identifier DEEPSLATE_DIAMOND_ORE = id("blocks/deepslate_diamond_ore");
        public static final Identifier EMERALD_ORE = id("blocks/emerald_ore");
        public static final Identifier DEEPSLATE_EMERALD_ORE = id("blocks/deepslate_emerald_ore");
        public static final Identifier LAPIS_ORE = id("blocks/lapis_ore");
        public static final Identifier DEEPSLATE_LAPIS_ORE = id("blocks/deepslate_lapis_ore");
        public static final Identifier REDSTONE_ORE = id("blocks/redstone_ore");
        public static final Identifier DEEPSLATE_REDSTONE_ORE = id("blocks/deepslate_redstone_ore");
        public static final Identifier COPPER_ORE = id("blocks/copper_ore");
        public static final Identifier DEEPSLATE_COPPER_ORE = id("blocks/deepslate_copper_ore");
        
        // Nether ores
        public static final Identifier NETHER_GOLD_ORE = id("blocks/nether_gold_ore");
        public static final Identifier NETHER_QUARTZ_ORE = id("blocks/nether_quartz_ore");
        public static final Identifier ANCIENT_DEBRIS = id("blocks/ancient_debris");
        
        // Stone variants
        public static final Identifier STONE = id("blocks/stone");
        public static final Identifier COBBLESTONE = id("blocks/cobblestone");
        public static final Identifier GRANITE = id("blocks/granite");
        public static final Identifier DIORITE = id("blocks/diorite");
        public static final Identifier ANDESITE = id("blocks/andesite");
        public static final Identifier DEEPSLATE = id("blocks/deepslate");
        
        // Dirt & Grass
        public static final Identifier DIRT = id("blocks/dirt");
        public static final Identifier GRASS_BLOCK = id("blocks/grass_block");
        public static final Identifier GRAVEL = id("blocks/gravel");
        public static final Identifier SAND = id("blocks/sand");
        public static final Identifier CLAY = id("blocks/clay");
        
        // Trees
        public static final Identifier OAK_LEAVES = id("blocks/oak_leaves");
        public static final Identifier SPRUCE_LEAVES = id("blocks/spruce_leaves");
        public static final Identifier BIRCH_LEAVES = id("blocks/birch_leaves");
        public static final Identifier JUNGLE_LEAVES = id("blocks/jungle_leaves");
        public static final Identifier ACACIA_LEAVES = id("blocks/acacia_leaves");
        public static final Identifier DARK_OAK_LEAVES = id("blocks/dark_oak_leaves");
        public static final Identifier AZALEA_LEAVES = id("blocks/azalea_leaves");
        public static final Identifier FLOWERING_AZALEA_LEAVES = id("blocks/flowering_azalea_leaves");
        public static final Identifier MANGROVE_LEAVES = id("blocks/mangrove_leaves");
        public static final Identifier CHERRY_LEAVES = id("blocks/cherry_leaves");
        
        // Crops
        public static final Identifier WHEAT = id("blocks/wheat");
        public static final Identifier CARROTS = id("blocks/carrots");
        public static final Identifier POTATOES = id("blocks/potatoes");
        public static final Identifier BEETROOTS = id("blocks/beetroots");
        public static final Identifier MELON = id("blocks/melon");
        public static final Identifier PUMPKIN = id("blocks/pumpkin");
        
        // Special
        public static final Identifier GLOWSTONE = id("blocks/glowstone");
        public static final Identifier SEA_LANTERN = id("blocks/sea_lantern");
        public static final Identifier BOOKSHELF = id("blocks/bookshelf");
        public static final Identifier SPAWNER = id("blocks/spawner");
        
        /**
         * Tworzy custom identifier dla bloku
         */
        public static Identifier custom(String namespace, String path) {
            return new Identifier(namespace, "blocks/" + path);
        }
        
        /**
         * Tworzy minecraft identifier dla bloku
         */
        public static Identifier of(String blockName) {
            return id("blocks/" + blockName);
        }
    }
    
    // ========== MOBY ==========
    public static class Entities {
        // Passive mobs
        public static final Identifier PIG = id("entities/pig");
        public static final Identifier COW = id("entities/cow");
        public static final Identifier SHEEP = id("entities/sheep");
        public static final Identifier CHICKEN = id("entities/chicken");
        public static final Identifier RABBIT = id("entities/rabbit");
        public static final Identifier HORSE = id("entities/horse");
        public static final Identifier DONKEY = id("entities/donkey");
        public static final Identifier LLAMA = id("entities/llama");
        public static final Identifier MOOSHROOM = id("entities/mooshroom");
        public static final Identifier PANDA = id("entities/panda");
        public static final Identifier FOX = id("entities/fox");
        public static final Identifier CAT = id("entities/cat");
        public static final Identifier PARROT = id("entities/parrot");
        public static final Identifier WOLF = id("entities/wolf");
        public static final Identifier POLAR_BEAR = id("entities/polar_bear");
        
        // Hostile mobs
        public static final Identifier ZOMBIE = id("entities/zombie");
        public static final Identifier SKELETON = id("entities/skeleton");
        public static final Identifier CREEPER = id("entities/creeper");
        public static final Identifier SPIDER = id("entities/spider");
        public static final Identifier CAVE_SPIDER = id("entities/cave_spider");
        public static final Identifier ENDERMAN = id("entities/enderman");
        public static final Identifier WITCH = id("entities/witch");
        public static final Identifier BLAZE = id("entities/blaze");
        public static final Identifier GHAST = id("entities/ghast");
        public static final Identifier MAGMA_CUBE = id("entities/magma_cube");
        public static final Identifier SLIME = id("entities/slime");
        public static final Identifier SILVERFISH = id("entities/silverfish");
        public static final Identifier GUARDIAN = id("entities/guardian");
        public static final Identifier ELDER_GUARDIAN = id("entities/elder_guardian");
        public static final Identifier SHULKER = id("entities/shulker");
        public static final Identifier PHANTOM = id("entities/phantom");
        public static final Identifier DROWNED = id("entities/drowned");
        public static final Identifier HUSK = id("entities/husk");
        public static final Identifier STRAY = id("entities/stray");
        public static final Identifier WITHER_SKELETON = id("entities/wither_skeleton");
        public static final Identifier ZOMBIE_VILLAGER = id("entities/zombie_villager");
        public static final Identifier PILLAGER = id("entities/pillager");
        public static final Identifier VINDICATOR = id("entities/vindicator");
        public static final Identifier EVOKER = id("entities/evoker");
        public static final Identifier RAVAGER = id("entities/ravager");
        public static final Identifier VEX = id("entities/vex");
        
        // Bosses
        public static final Identifier ENDER_DRAGON = id("entities/ender_dragon");
        public static final Identifier WITHER = id("entities/wither");
        public static final Identifier WARDEN = id("entities/warden");
        
        // Water mobs
        public static final Identifier SQUID = id("entities/squid");
        public static final Identifier GLOW_SQUID = id("entities/glow_squid");
        public static final Identifier DOLPHIN = id("entities/dolphin");
        public static final Identifier TURTLE = id("entities/turtle");
        public static final Identifier COD = id("entities/cod");
        public static final Identifier SALMON = id("entities/salmon");
        public static final Identifier TROPICAL_FISH = id("entities/tropical_fish");
        public static final Identifier PUFFERFISH = id("entities/pufferfish");
        
        /**
         * Tworzy custom identifier dla moba
         */
        public static Identifier custom(String namespace, String path) {
            return new Identifier(namespace, "entities/" + path);
        }
        
        /**
         * Tworzy minecraft identifier dla moba
         */
        public static Identifier of(String entityName) {
            return id("entities/" + entityName);
        }
    }
    
    // ========== SKRZYNIE ==========
    public static class Chests {
        // Village chests
        public static final Identifier VILLAGE_ARMORER = id("chests/village/village_armorer");
        public static final Identifier VILLAGE_BUTCHER = id("chests/village/village_butcher");
        public static final Identifier VILLAGE_CARTOGRAPHER = id("chests/village/village_cartographer");
        public static final Identifier VILLAGE_DESERT_HOUSE = id("chests/village/village_desert_house");
        public static final Identifier VILLAGE_FISHER = id("chests/village/village_fisher");
        public static final Identifier VILLAGE_FLETCHER = id("chests/village/village_fletcher");
        public static final Identifier VILLAGE_MASON = id("chests/village/village_mason");
        public static final Identifier VILLAGE_PLAINS_HOUSE = id("chests/village/village_plains_house");
        public static final Identifier VILLAGE_SAVANNA_HOUSE = id("chests/village/village_savanna_house");
        public static final Identifier VILLAGE_SHEPHERD = id("chests/village/village_shepherd");
        public static final Identifier VILLAGE_SNOWY_HOUSE = id("chests/village/village_snowy_house");
        public static final Identifier VILLAGE_TAIGA_HOUSE = id("chests/village/village_taiga_house");
        public static final Identifier VILLAGE_TANNERY = id("chests/village/village_tannery");
        public static final Identifier VILLAGE_TEMPLE = id("chests/village/village_temple");
        public static final Identifier VILLAGE_TOOLSMITH = id("chests/village/village_toolsmith");
        public static final Identifier VILLAGE_WEAPONSMITH = id("chests/village/village_weaponsmith");
        
        // Dungeon chests
        public static final Identifier SIMPLE_DUNGEON = id("chests/simple_dungeon");
        public static final Identifier ABANDONED_MINESHAFT = id("chests/abandoned_mineshaft");
        public static final Identifier BURIED_TREASURE = id("chests/buried_treasure");
        public static final Identifier DESERT_PYRAMID = id("chests/desert_pyramid");
        public static final Identifier JUNGLE_TEMPLE = id("chests/jungle_temple");
        public static final Identifier IGLOO_CHEST = id("chests/igloo_chest");
        public static final Identifier PILLAGER_OUTPOST = id("chests/pillager_outpost");
        public static final Identifier SHIPWRECK_MAP = id("chests/shipwreck_map");
        public static final Identifier SHIPWRECK_SUPPLY = id("chests/shipwreck_supply");
        public static final Identifier SHIPWRECK_TREASURE = id("chests/shipwreck_treasure");
        public static final Identifier UNDERWATER_RUIN_BIG = id("chests/underwater_ruin_big");
        public static final Identifier UNDERWATER_RUIN_SMALL = id("chests/underwater_ruin_small");
        public static final Identifier WOODLAND_MANSION = id("chests/woodland_mansion");
        
        // Stronghold chests
        public static final Identifier STRONGHOLD_CORRIDOR = id("chests/stronghold_corridor");
        public static final Identifier STRONGHOLD_CROSSING = id("chests/stronghold_crossing");
        public static final Identifier STRONGHOLD_LIBRARY = id("chests/stronghold_library");
        
        // Nether chests
        public static final Identifier BASTION_BRIDGE = id("chests/bastion_bridge");
        public static final Identifier BASTION_HOGLIN_STABLE = id("chests/bastion_hoglin_stable");
        public static final Identifier BASTION_OTHER = id("chests/bastion_other");
        public static final Identifier BASTION_TREASURE = id("chests/bastion_treasure");
        public static final Identifier NETHER_BRIDGE = id("chests/nether_bridge");
        
        // End chests
        public static final Identifier END_CITY_TREASURE = id("chests/end_city_treasure");
        
        /**
         * Tworzy custom identifier dla skrzyni
         */
        public static Identifier custom(String namespace, String path) {
            return new Identifier(namespace, "chests/" + path);
        }
        
        /**
         * Tworzy minecraft identifier dla skrzyni
         */
        public static Identifier of(String chestName) {
            return id("chests/" + chestName);
        }
    }
    
    // ========== FISHING ==========
    public static class Fishing {
        public static final Identifier FISH = id("gameplay/fishing/fish");
        public static final Identifier TREASURE = id("gameplay/fishing/treasure");
        public static final Identifier JUNK = id("gameplay/fishing/junk");
        
        /**
         * Tworzy custom fishing loot table
         */
        public static Identifier custom(String namespace, String path) {
            return new Identifier(namespace, "gameplay/fishing/" + path);
        }
    }
    
    // ========== ARCHAEOLOGY ==========
    public static class Archaeology {
        public static final Identifier DESERT_PYRAMID = id("archaeology/desert_pyramid");
        public static final Identifier DESERT_WELL = id("archaeology/desert_well");
        public static final Identifier OCEAN_RUIN_COLD = id("archaeology/ocean_ruin_cold");
        public static final Identifier OCEAN_RUIN_WARM = id("archaeology/ocean_ruin_warm");
        public static final Identifier TRAIL_RUINS_COMMON = id("archaeology/trail_ruins_common");
        public static final Identifier TRAIL_RUINS_RARE = id("archaeology/trail_ruins_rare");
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Tworzy Identifier z namespace "minecraft"
     */
    private static Identifier id(String path) {
        return new Identifier("minecraft", path);
    }
    
    /**
     * Tworzy custom loot table identifier
     */
    public static Identifier custom(String namespace, String path) {
        return new Identifier(namespace, path);
    }
    
    /**
     * Sprawdza czy ścieżka pasuje do patternu (wildcard support)
     */
    public static boolean matches(Identifier id, String pattern) {
        String idString = id.toString();
        String regex = pattern.replace("*", ".*").replace("?", ".");
        return idString.matches(regex);
    }
}
