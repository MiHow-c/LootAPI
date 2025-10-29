package pl.mikof.lootapi;

import net.minecraft.resources.ResourceLocation;

/**
 * Pomocnicza klasa z predefiniowanymi ścieżkami do loot tables
 * Ułatwia korzystanie z API
 */
public class LootTables {

    /**
     * Bloki
     */
    public static class Blocks {
        // Rudy
        public static final ResourceLocation COAL_ORE = ResourceLocation.withDefaultNamespace("blocks/coal_ore");
        public static final ResourceLocation DEEPSLATE_COAL_ORE = ResourceLocation.withDefaultNamespace("blocks/deepslate_coal_ore");
        public static final ResourceLocation IRON_ORE = ResourceLocation.withDefaultNamespace("blocks/iron_ore");
        public static final ResourceLocation DEEPSLATE_IRON_ORE = ResourceLocation.withDefaultNamespace("blocks/deepslate_iron_ore");
        public static final ResourceLocation GOLD_ORE = ResourceLocation.withDefaultNamespace("blocks/gold_ore");
        public static final ResourceLocation DEEPSLATE_GOLD_ORE = ResourceLocation.withDefaultNamespace("blocks/deepslate_gold_ore");
        public static final ResourceLocation DIAMOND_ORE = ResourceLocation.withDefaultNamespace("blocks/diamond_ore");
        public static final ResourceLocation DEEPSLATE_DIAMOND_ORE = ResourceLocation.withDefaultNamespace("blocks/deepslate_diamond_ore");
        public static final ResourceLocation EMERALD_ORE = ResourceLocation.withDefaultNamespace("blocks/emerald_ore");
        public static final ResourceLocation DEEPSLATE_EMERALD_ORE = ResourceLocation.withDefaultNamespace("blocks/deepslate_emerald_ore");
        public static final ResourceLocation LAPIS_ORE = ResourceLocation.withDefaultNamespace("blocks/lapis_ore");
        public static final ResourceLocation DEEPSLATE_LAPIS_ORE = ResourceLocation.withDefaultNamespace("blocks/deepslate_lapis_ore");
        public static final ResourceLocation REDSTONE_ORE = ResourceLocation.withDefaultNamespace("blocks/redstone_ore");
        public static final ResourceLocation DEEPSLATE_REDSTONE_ORE = ResourceLocation.withDefaultNamespace("blocks/deepslate_redstone_ore");
        public static final ResourceLocation COPPER_ORE = ResourceLocation.withDefaultNamespace("blocks/copper_ore");
        public static final ResourceLocation DEEPSLATE_COPPER_ORE = ResourceLocation.withDefaultNamespace("blocks/deepslate_copper_ore");

        // Rudy Netheru
        public static final ResourceLocation NETHER_GOLD_ORE = ResourceLocation.withDefaultNamespace("blocks/nether_gold_ore");
        public static final ResourceLocation NETHER_QUARTZ_ORE = ResourceLocation.withDefaultNamespace("blocks/nether_quartz_ore");
        public static final ResourceLocation ANCIENT_DEBRIS = ResourceLocation.withDefaultNamespace("blocks/ancient_debris");

        // Podstawowe bloki
        public static final ResourceLocation STONE = ResourceLocation.withDefaultNamespace("blocks/stone");
        public static final ResourceLocation COBBLESTONE = ResourceLocation.withDefaultNamespace("blocks/cobblestone");
        public static final ResourceLocation DIRT = ResourceLocation.withDefaultNamespace("blocks/dirt");
        public static final ResourceLocation GRASS_BLOCK = ResourceLocation.withDefaultNamespace("blocks/grass_block");
        public static final ResourceLocation GRAVEL = ResourceLocation.withDefaultNamespace("blocks/gravel");
        public static final ResourceLocation SAND = ResourceLocation.withDefaultNamespace("blocks/sand");

        // Drewno
        public static final ResourceLocation OAK_LOG = ResourceLocation.withDefaultNamespace("blocks/oak_log");
        public static final ResourceLocation OAK_LEAVES = ResourceLocation.withDefaultNamespace("blocks/oak_leaves");

        /**
         * Tworzy ResourceLocation dla bloku
         */
        public static ResourceLocation of(String blockName) {
            return ResourceLocation.withDefaultNamespace("blocks/" + blockName);
        }

        /**
         * Tworzy ResourceLocation dla bloku z innego moda
         */
        public static ResourceLocation modded(String modId, String blockName) {
            return ResourceLocation.fromNamespaceAndPath(modId, "blocks/" + blockName);
        }
    }

    /**
     * Moby
     */
    public static class Entities {
        // Zwierzęta
        public static final ResourceLocation PIG = ResourceLocation.withDefaultNamespace("entities/pig");
        public static final ResourceLocation COW = ResourceLocation.withDefaultNamespace("entities/cow");
        public static final ResourceLocation SHEEP = ResourceLocation.withDefaultNamespace("entities/sheep");
        public static final ResourceLocation CHICKEN = ResourceLocation.withDefaultNamespace("entities/chicken");
        public static final ResourceLocation RABBIT = ResourceLocation.withDefaultNamespace("entities/rabbit");

        // Potwory
        public static final ResourceLocation ZOMBIE = ResourceLocation.withDefaultNamespace("entities/zombie");
        public static final ResourceLocation SKELETON = ResourceLocation.withDefaultNamespace("entities/skeleton");
        public static final ResourceLocation CREEPER = ResourceLocation.withDefaultNamespace("entities/creeper");
        public static final ResourceLocation SPIDER = ResourceLocation.withDefaultNamespace("entities/spider");
        public static final ResourceLocation ENDERMAN = ResourceLocation.withDefaultNamespace("entities/enderman");
        public static final ResourceLocation WITCH = ResourceLocation.withDefaultNamespace("entities/witch");
        public static final ResourceLocation PHANTOM = ResourceLocation.withDefaultNamespace("entities/phantom");

        // Nether
        public static final ResourceLocation BLAZE = ResourceLocation.withDefaultNamespace("entities/blaze");
        public static final ResourceLocation GHAST = ResourceLocation.withDefaultNamespace("entities/ghast");
        public static final ResourceLocation MAGMA_CUBE = ResourceLocation.withDefaultNamespace("entities/magma_cube");
        public static final ResourceLocation PIGLIN = ResourceLocation.withDefaultNamespace("entities/piglin");
        public static final ResourceLocation ZOMBIFIED_PIGLIN = ResourceLocation.withDefaultNamespace("entities/zombified_piglin");
        public static final ResourceLocation WITHER_SKELETON = ResourceLocation.withDefaultNamespace("entities/wither_skeleton");

        // Bossy
        public static final ResourceLocation ENDER_DRAGON = ResourceLocation.withDefaultNamespace("entities/ender_dragon");
        public static final ResourceLocation WITHER = ResourceLocation.withDefaultNamespace("entities/wither");
        public static final ResourceLocation WARDEN = ResourceLocation.withDefaultNamespace("entities/warden");
        public static final ResourceLocation ELDER_GUARDIAN = ResourceLocation.withDefaultNamespace("entities/elder_guardian");

        /**
         * Tworzy ResourceLocation dla moba
         */
        public static ResourceLocation of(String entityName) {
            return ResourceLocation.withDefaultNamespace("entities/" + entityName);
        }

        /**
         * Tworzy ResourceLocation dla moba z innego moda
         */
        public static ResourceLocation modded(String modId, String entityName) {
            return ResourceLocation.fromNamespaceAndPath(modId, "entities/" + entityName);
        }
    }

    /**
     * Skrzynie
     */
    public static class Chests {
        // Wioski
        public static final ResourceLocation VILLAGE_WEAPONSMITH = ResourceLocation.withDefaultNamespace("chests/village/village_weaponsmith");
        public static final ResourceLocation VILLAGE_TOOLSMITH = ResourceLocation.withDefaultNamespace("chests/village/village_toolsmith");
        public static final ResourceLocation VILLAGE_ARMORER = ResourceLocation.withDefaultNamespace("chests/village/village_armorer");
        public static final ResourceLocation VILLAGE_CARTOGRAPHER = ResourceLocation.withDefaultNamespace("chests/village/village_cartographer");
        public static final ResourceLocation VILLAGE_MASON = ResourceLocation.withDefaultNamespace("chests/village/village_mason");
        public static final ResourceLocation VILLAGE_SHEPHERD = ResourceLocation.withDefaultNamespace("chests/village/village_shepherd");
        public static final ResourceLocation VILLAGE_BUTCHER = ResourceLocation.withDefaultNamespace("chests/village/village_butcher");
        public static final ResourceLocation VILLAGE_FLETCHER = ResourceLocation.withDefaultNamespace("chests/village/village_fletcher");
        public static final ResourceLocation VILLAGE_FISHER = ResourceLocation.withDefaultNamespace("chests/village/village_fisher");
        public static final ResourceLocation VILLAGE_TANNERY = ResourceLocation.withDefaultNamespace("chests/village/village_tannery");
        public static final ResourceLocation VILLAGE_TEMPLE = ResourceLocation.withDefaultNamespace("chests/village/village_temple");

        // Struktury
        public static final ResourceLocation SIMPLE_DUNGEON = ResourceLocation.withDefaultNamespace("chests/simple_dungeon");
        public static final ResourceLocation ABANDONED_MINESHAFT = ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft");
        public static final ResourceLocation BURIED_TREASURE = ResourceLocation.withDefaultNamespace("chests/buried_treasure");
        public static final ResourceLocation DESERT_PYRAMID = ResourceLocation.withDefaultNamespace("chests/desert_pyramid");
        public static final ResourceLocation JUNGLE_TEMPLE = ResourceLocation.withDefaultNamespace("chests/jungle_temple");
        public static final ResourceLocation STRONGHOLD_CORRIDOR = ResourceLocation.withDefaultNamespace("chests/stronghold_corridor");
        public static final ResourceLocation STRONGHOLD_CROSSING = ResourceLocation.withDefaultNamespace("chests/stronghold_crossing");
        public static final ResourceLocation STRONGHOLD_LIBRARY = ResourceLocation.withDefaultNamespace("chests/stronghold_library");

        // End
        public static final ResourceLocation END_CITY_TREASURE = ResourceLocation.withDefaultNamespace("chests/end_city_treasure");

        // Nether
        public static final ResourceLocation NETHER_BRIDGE = ResourceLocation.withDefaultNamespace("chests/nether_bridge");
        public static final ResourceLocation BASTION_TREASURE = ResourceLocation.withDefaultNamespace("chests/bastion_treasure");
        public static final ResourceLocation BASTION_OTHER = ResourceLocation.withDefaultNamespace("chests/bastion_other");
        public static final ResourceLocation BASTION_BRIDGE = ResourceLocation.withDefaultNamespace("chests/bastion_bridge");
        public static final ResourceLocation BASTION_HOGLIN_STABLE = ResourceLocation.withDefaultNamespace("chests/bastion_hoglin_stable");
        public static final ResourceLocation RUINED_PORTAL = ResourceLocation.withDefaultNamespace("chests/ruined_portal");

        // Ocean
        public static final ResourceLocation SHIPWRECK_MAP = ResourceLocation.withDefaultNamespace("chests/shipwreck_map");
        public static final ResourceLocation SHIPWRECK_SUPPLY = ResourceLocation.withDefaultNamespace("chests/shipwreck_supply");
        public static final ResourceLocation SHIPWRECK_TREASURE = ResourceLocation.withDefaultNamespace("chests/shipwreck_treasure");
        public static final ResourceLocation UNDERWATER_RUIN_BIG = ResourceLocation.withDefaultNamespace("chests/underwater_ruin_big");
        public static final ResourceLocation UNDERWATER_RUIN_SMALL = ResourceLocation.withDefaultNamespace("chests/underwater_ruin_small");

        // Pozostałe
        public static final ResourceLocation WOODLAND_MANSION = ResourceLocation.withDefaultNamespace("chests/woodland_mansion");
        public static final ResourceLocation PILLAGER_OUTPOST = ResourceLocation.withDefaultNamespace("chests/pillager_outpost");
        public static final ResourceLocation ANCIENT_CITY = ResourceLocation.withDefaultNamespace("chests/ancient_city");
        public static final ResourceLocation ANCIENT_CITY_ICE_BOX = ResourceLocation.withDefaultNamespace("chests/ancient_city_ice_box");

        /**
         * Tworzy ResourceLocation dla skrzyni
         */
        public static ResourceLocation of(String chestName) {
            return ResourceLocation.withDefaultNamespace("chests/" + chestName);
        }

        /**
         * Tworzy ResourceLocation dla skrzyni z innego moda
         */
        public static ResourceLocation modded(String modId, String chestName) {
            return ResourceLocation.fromNamespaceAndPath(modId, "chests/" + chestName);
        }
    }

    /**
     * Wędkowanie
     */
    public static class Fishing {
        public static final ResourceLocation FISHING = ResourceLocation.withDefaultNamespace("gameplay/fishing");
        public static final ResourceLocation FISHING_FISH = ResourceLocation.withDefaultNamespace("gameplay/fishing/fish");
        public static final ResourceLocation FISHING_TREASURE = ResourceLocation.withDefaultNamespace("gameplay/fishing/treasure");
        public static final ResourceLocation FISHING_JUNK = ResourceLocation.withDefaultNamespace("gameplay/fishing/junk");
    }

    /**
     * Tworzy dowolną ResourceLocation
     */
    public static ResourceLocation custom(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    /**
     * Tworzy ResourceLocation dla vanilla Minecraft
     */
    public static ResourceLocation minecraft(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }
}