package pl.mikof.lootapi;

import net.minecraft.resources.ResourceLocation;

/**
 * Predefined loot table paths for easy access
 */
public class LootTables {

    private static ResourceLocation id(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static class Blocks {
        // Vanilla ores
        public static final ResourceLocation COAL_ORE = id("blocks/coal_ore");
        public static final ResourceLocation DEEPSLATE_COAL_ORE = id("blocks/deepslate_coal_ore");
        public static final ResourceLocation IRON_ORE = id("blocks/iron_ore");
        public static final ResourceLocation DEEPSLATE_IRON_ORE = id("blocks/deepslate_iron_ore");
        public static final ResourceLocation GOLD_ORE = id("blocks/gold_ore");
        public static final ResourceLocation DEEPSLATE_GOLD_ORE = id("blocks/deepslate_gold_ore");
        public static final ResourceLocation DIAMOND_ORE = id("blocks/diamond_ore");
        public static final ResourceLocation DEEPSLATE_DIAMOND_ORE = id("blocks/deepslate_diamond_ore");
        public static final ResourceLocation EMERALD_ORE = id("blocks/emerald_ore");
        public static final ResourceLocation DEEPSLATE_EMERALD_ORE = id("blocks/deepslate_emerald_ore");
        public static final ResourceLocation LAPIS_ORE = id("blocks/lapis_ore");
        public static final ResourceLocation DEEPSLATE_LAPIS_ORE = id("blocks/deepslate_lapis_ore");
        public static final ResourceLocation REDSTONE_ORE = id("blocks/redstone_ore");
        public static final ResourceLocation DEEPSLATE_REDSTONE_ORE = id("blocks/deepslate_redstone_ore");
        public static final ResourceLocation COPPER_ORE = id("blocks/copper_ore");
        public static final ResourceLocation DEEPSLATE_COPPER_ORE = id("blocks/deepslate_copper_ore");

        // Nether ores
        public static final ResourceLocation NETHER_GOLD_ORE = id("blocks/nether_gold_ore");
        public static final ResourceLocation NETHER_QUARTZ_ORE = id("blocks/nether_quartz_ore");
        public static final ResourceLocation ANCIENT_DEBRIS = id("blocks/ancient_debris");

        // Stone variants
        public static final ResourceLocation STONE = id("blocks/stone");
        public static final ResourceLocation COBBLESTONE = id("blocks/cobblestone");
        public static final ResourceLocation DIRT = id("blocks/dirt");
        public static final ResourceLocation GRASS_BLOCK = id("blocks/grass_block");
        public static final ResourceLocation GRAVEL = id("blocks/gravel");

        public static ResourceLocation custom(String namespace, String path) {
            return ResourceLocation.fromNamespaceAndPath(namespace, "blocks/" + path);
        }

        public static ResourceLocation of(String blockName) {
            return id("blocks/" + blockName);
        }
    }

    public static class Entities {
        // Passive mobs
        public static final ResourceLocation PIG = id("entities/pig");
        public static final ResourceLocation COW = id("entities/cow");
        public static final ResourceLocation SHEEP = id("entities/sheep");
        public static final ResourceLocation CHICKEN = id("entities/chicken");

        // Hostile mobs
        public static final ResourceLocation ZOMBIE = id("entities/zombie");
        public static final ResourceLocation SKELETON = id("entities/skeleton");
        public static final ResourceLocation CREEPER = id("entities/creeper");
        public static final ResourceLocation SPIDER = id("entities/spider");
        public static final ResourceLocation ENDERMAN = id("entities/enderman");
        public static final ResourceLocation BLAZE = id("entities/blaze");
        public static final ResourceLocation GHAST = id("entities/ghast");

        // Bosses
        public static final ResourceLocation ENDER_DRAGON = id("entities/ender_dragon");
        public static final ResourceLocation WITHER = id("entities/wither");
        public static final ResourceLocation WARDEN = id("entities/warden");

        public static ResourceLocation custom(String namespace, String path) {
            return ResourceLocation.fromNamespaceAndPath(namespace, "entities/" + path);
        }

        public static ResourceLocation of(String entityName) {
            return id("entities/" + entityName);
        }
    }

    public static class Chests {
        // Village chests
        public static final ResourceLocation VILLAGE_WEAPONSMITH = id("chests/village/village_weaponsmith");
        public static final ResourceLocation VILLAGE_TOOLSMITH = id("chests/village/village_toolsmith");
        public static final ResourceLocation VILLAGE_ARMORER = id("chests/village/village_armorer");

        // Dungeon chests
        public static final ResourceLocation SIMPLE_DUNGEON = id("chests/simple_dungeon");
        public static final ResourceLocation BURIED_TREASURE = id("chests/buried_treasure");
        public static final ResourceLocation DESERT_PYRAMID = id("chests/desert_pyramid");
        public static final ResourceLocation END_CITY_TREASURE = id("chests/end_city_treasure");

        public static ResourceLocation custom(String namespace, String path) {
            return ResourceLocation.fromNamespaceAndPath(namespace, "chests/" + path);
        }

        public static ResourceLocation of(String chestName) {
            return id("chests/" + chestName);
        }
    }

    public static ResourceLocation custom(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}