# 🎯 Loot API dla Fabric 1.20.1

**Najłatwiejsze i najpotężniejsze API do zarządzania loot tables w Minecraft!**

[![Fabric](https://img.shields.io/badge/Fabric-1.20.1-blue)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-17+-orange)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

---

## 🎯 O projekcie

**Loot API** to kompleksowa biblioteka dla modderów Fabric, która **DRASTYCZNIE** upraszcza zarządzanie loot tables.

### ⚡ Dlaczego to API?

**PRZED:**
```java
new Identifier("minecraft", "blocks/diamond_ore") // Długie i męczące
LootTable.builder().pool(...) // Skomplikowane
```

**PO:**
```java
LootTables.Blocks.DIAMOND_ORE // Proste!
LootTableBuilder.quickSingle(...) // Super łatwe!
```

---

## ✨ Wszystkie funkcje

### 🎨 Nowe funkcje (specjalnie dla Ciebie!):

✅ **LootTables** - Predefiniowane ścieżki (300+ bloków/mobów/skrzyń!)  
✅ **LootTableBuilder** - Łatwe tworzenie nowych loot tables  
✅ **Quick Methods** - Pojedyncza linia kodu!  

### 🔧 Podstawowe:

✅ Dodawanie itemów  
✅ Usuwanie itemów  
✅ Zamiana itemów  
✅ Mnożniki dropów  
✅ Kopiowanie loot tables  

### ⚙️ Zaawansowane:

✅ Fortune & Silk Touch  
✅ Warunki (pogoda, biomy, wysokość)  
✅ NBT & Enchanty  
✅ Grupy itemów (WeightedItemGroup)  
✅ Bulk operations (wildcards)  
✅ Priorytety (5 poziomów)  
✅ Callbacki  
✅ Config Manager (JSON)  
✅ Debug tools  

---

## 🚀 Instalacja

### Dla modderów (jako dependency)

**build.gradle:**
```groovy
repositories {
    // Twoje repo lub local
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    modImplementation "pl.mikof:lootapi:1.0.0"
    include "pl.mikof:lootapi:1.0.0"
}
```

**fabric.mod.json:**
```json
"depends": {
  "lootapi": ">=1.0.0"
}
```

---

## 📖 Szybki Start

### 1. Modyfikowanie istniejących loot tables

```java
import pl.mikof.lootapi.*;
import net.minecraft.item.Items;

public class YourMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // SUPER PROSTE - użyj predefiniowanych ścieżek!
        
        // Dodaj diament do coal ore
        LootTableAPI.addItemToTable(
            LootTables.Blocks.COAL_ORE,  // Łatwe!
            Items.DIAMOND,
            5
        );
        
        // Zombie dropuje złoto
        LootTableAPI.addItemWithCount(
            LootTables.Entities.ZOMBIE,
            Items.GOLD_INGOT,
            10, 2, 5
        );
        
        // Podwój dropy ze szkieleta
        LootTableAPI.multiplyDrops(
            LootTables.Entities.SKELETON,
            2.0f
        );
    }
}
```

### 2. Tworzenie nowych loot tables

```java
// SPOSÓB 1: Quick method - pojedyncza linia!
LootTableBuilder.quickSingle(
    new Identifier("yourmod", "blocks/mythril_ore"),
    Items.DIAMOND
);

// SPOSÓB 2: Builder - więcej kontroli
LootTableBuilder.create("yourmod", "chests/treasure")
    .addItem(Items.DIAMOND, 70)
    .addItem(Items.EMERALD, 20)
    .addItem(Items.NETHERITE_INGOT, 10)
    .register();

// SPOSÓB 3: Z pool'ami - profesjonalne
LootTableBuilder.create("yourmod", "entities/boss")
    .pool(1)
        .item(Items.NETHER_STAR, 100)
        .item(Items.DRAGON_HEAD, 50)
    .endPool()
    .pool(5, 10)  // 5-10 random itemów
        .item(Items.DIAMOND, 30)
        .item(Items.EMERALD, 30)
        .item(Items.NETHERITE_INGOT, 40)
    .endPool()
    .register();
```

---

## 📚 Dokumentacja

### LootTables - Predefiniowane ścieżki

Zamiast pisać `new Identifier("minecraft", "blocks/diamond_ore")` użyj:

```java
LootTables.Blocks.DIAMOND_ORE
LootTables.Entities.ZOMBIE
LootTables.Chests.END_CITY_TREASURE
```

**Dostępne kategorie:**
- `LootTables.Blocks.*` - Wszystkie bloki (rudy, kamienie, drzewa, rośliny)
- `LootTables.Entities.*` - Wszystkie moby (pasywne, wrogie, bossy)
- `LootTables.Chests.*` - Wszystkie skrzynie (wioski, dungeons, struktury)
- `LootTables.Fishing.*` - Wędkowanie
- `LootTables.Archaeology.*` - Archeologia

**Przykłady:**
```java
// Rudy
LootTables.Blocks.COAL_ORE
LootTables.Blocks.IRON_ORE
LootTables.Blocks.DIAMOND_ORE
LootTables.Blocks.ANCIENT_DEBRIS

// Moby
LootTables.Entities.ZOMBIE
LootTables.Entities.CREEPER
LootTables.Entities.ENDER_DRAGON
LootTables.Entities.WARDEN

// Skrzynie
LootTables.Chests.END_CITY_TREASURE
LootTables.Chests.BURIED_TREASURE
LootTables.Chests.VILLAGE_WEAPONSMITH
```

### Podstawowe operacje

```java
// Dodaj item
LootTableAPI.addItemToTable(tableId, item, weight);

// Dodaj item z ilością
LootTableAPI.addItemWithCount(tableId, item, weight, min, max);

// Usuń item
LootTableAPI.removeItemFromTable(tableId, item);

// Zamień item
LootTableAPI.replaceItem(tableId, oldItem, newItem);

// Mnożnik dropów
LootTableAPI.multiplyDrops(tableId, multiplier);

// Kopiuj loot table
LootTableAPI.copyLootTable(source, target);
```

### Wildcards (bulk operations)

```java
// Dodaj emerald do WSZYSTKICH rud
LootTableAPI.addToMatching(
    "minecraft:blocks/*_ore",
    Items.EMERALD,
    3
);

// Działa z dowolnym wzorcem
LootTableAPI.addToMatching("minecraft:entities/*", item, weight);
LootTableAPI.addToMatching("minecraft:chests/village/*", item, weight);
```

### WeightedItemGroup

```java
WeightedItemGroup group = new WeightedItemGroup()
    .add(Items.DIAMOND, 10)    // 10% szans
    .add(Items.EMERALD, 20)    // 20% szans
    .add(Items.GOLD_INGOT, 70); // 70% szans

LootTableAPI.addWeightedGroup(tableId, group);

// Możesz też:
group.normalize(100);  // Normalizuj wagi do 100
group.merge(otherGroup);  // Połącz grupy
double chance = group.getChance(Items.DIAMOND);  // Sprawdź szansę
```

### Warunki

```java
// Fortune
LootTableAPI.addItemWithFortune(tableId, item, weight, minBonus, maxBonus);

// Silk Touch
LootTableAPI.addItemRequiringSilkTouch(tableId, item, weight);

// Pogoda
AdvancedLootAPI.addItemDuringStorm(tableId, item, weight);

// Biom
AdvancedLootAPI.addItemWithBiomeCondition(tableId, item, weight, biome);

// Custom warunki
LootConditionBuilder conditions = LootConditionBuilder.create()
    .withFortune()
    .duringThunder()
    .withChance(0.1f)
    .killedByPlayer();

AdvancedLootAPI.addItemWithCondition(tableId, item, weight, conditions.build());
```

### Priorytety

```java
// Modyfikatory wykonują się w kolejności priorytetów
LootTableAPI.registerModifier(tableId, builder -> {
    // Twoja modyfikacja
}, LootTableAPI.Priority.HIGHEST);

// Dostępne: LOWEST, LOW, NORMAL, HIGH, HIGHEST
```

### Config (JSON)

Możesz tworzyć pliki konfiguracyjne w `config/lootapi/`:

```json
{
  "modifications": [
    {
      "table": "minecraft:blocks/coal_ore",
      "item": "minecraft:diamond",
      "weight": 5
    },
    {
      "table": "minecraft:entities/zombie",
      "item": "minecraft:gold_ingot",
      "weight": 10,
      "count": {
        "min": 2,
        "max": 5
      }
    }
  ],
  "removals": [
    {
      "table": "minecraft:blocks/diamond_ore",
      "item": "minecraft:diamond"
    }
  ],
  "replacements": [
    {
      "table": "minecraft:entities/creeper",
      "old_item": "minecraft:gunpowder",
      "new_item": "minecraft:tnt"
    }
  ],
  "multipliers": {
    "minecraft:entities/zombie": 2.0
  }
}
```

---

## 🎨 Przykłady

### Przykład 1: Modyfikacja vanilla dropów

```java
// Diamenty z każdej rudy!
LootTableAPI.addItemToTable(LootTables.Blocks.COAL_ORE, Items.DIAMOND, 5);
LootTableAPI.addItemToTable(LootTables.Blocks.IRON_ORE, Items.DIAMOND, 5);
LootTableAPI.addItemToTable(LootTables.Blocks.GOLD_ORE, Items.DIAMOND, 5);

// Lub użyj wildcard:
LootTableAPI.addToMatching("minecraft:blocks/*_ore", Items.DIAMOND, 5);
```

### Przykład 2: Custom ore

```java
// Prosty sposób
LootTableBuilder.quickOre(
    new Identifier("yourmod", "blocks/mythril_ore"),
    Items.DIAMOND,
    2, 5
);

// Zaawansowany sposób
LootTableBuilder.create("yourmod", "blocks/mythril_ore")
    .pool(1)
        .item(Items.DIAMOND, 70, 2, 5)
        .item(Items.EMERALD, 30, 1, 3)
    .endPool()
    .register();
```

### Przykład 3: Custom boss

```java
LootTableBuilder.create("yourmod", "entities/custom_boss")
    .addItem(Items.NETHER_STAR, 100)  // Zawsze dropuje
    .addItem(Items.DRAGON_HEAD, 50)   // 50% szans
    .pool(5, 10)  // 5-10 losowych itemów
        .item(Items.DIAMOND, 30)
        .item(Items.EMERALD, 30)
        .item(Items.NETHERITE_INGOT, 40)
    .endPool()
    .register();
```

### Przykład 4: Custom chest

```java
WeightedItemGroup loot = new WeightedItemGroup()
    .add(Items.DIAMOND, 20)
    .add(Items.EMERALD, 30)
    .add(Items.GOLD_INGOT, 50);

LootTableBuilder.create("yourmod", "chests/treasure")
    .addGroup(loot, 5)  // 5 itemów z grupy
    .register();
```

---

## 🐛 Debug

```java
// Wyświetl informacje o loot table
LootTableAPI.debugLootTable(tableId);

// Export do JSON
LootTableAPI.exportToJson(tableId, "config/lootapi/debug.json");
```

---

## 📄 Licencja

MIT License - możesz swobodnie używać w swoich projektach!

---

## 🤝 Wsparcie

Jeśli napotkasz problemy:
1. Sprawdź logi - API loguje wszystkie operacje
2. Upewnij się że używasz poprawnych Identifier'ów
3. Sprawdź czy Fabric API jest zainstalowane

---

## 💡 Tips & Tricks

1. **Używaj LootTables.*** zamiast `new Identifier()` - szybsze i bez błędów!
2. **Wildcards** są potężne - `*_ore` dopasuje wszystkie rudy
3. **WeightedItemGroup** świetnie nadaje się do skrzyń
4. **LootTableBuilder** ma quick methods dla typowych przypadków
5. **Priorytety** pozwalają kontrolować kolejność modyfikacji
6. **Config JSON** pozwala użytkownikom customizować dropy

---

**Stworzone z ❤️ przez Mikof dla społeczności Fabric!**
