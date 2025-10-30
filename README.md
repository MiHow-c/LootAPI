# LootAPI v3.0

**Kompletne API do modyfikacji loot tables dla Minecraft NeoForge 1.21.1**

## 🌟 Co nowego w wersji 3.0?

- ✅ **Global Loot Modifiers (GLM)** - nowoczesna, oficjalna metoda modyfikacji loot tables
- ✅ **Pełna kompatybilność** z innymi modami używającymi GLM
- ✅ **Wszystkie funkcje zaimplementowane** - ADD, REMOVE, REPLACE, MULTIPLY, CLEAR, SET_ONLY
- ✅ **Prosty interfejs API** - łatwe w użyciu dla programistów
- ✅ **Wsparcie JSON** - możliwość konfiguracji przez pliki
- ✅ **Bez przestarzałych mechanizmów** - usuniętyo LootTableLoadEvent i reflection

## 📦 Instalacja

### Dla użytkowników modów

1. Pobierz najnowszą wersję `lootapi-3.0.0.jar`
2. Umieść w folderze `mods/`
3. Uruchom grę

### Dla twórców modów

Dodaj LootAPI jako zależność w swoim `build.gradle`:

```gradle
dependencies {
    implementation files("libs/lootapi-3.0.0.jar")
}
```

## 🚀 Szybki start

### Przykład podstawowy

```java
import pl.mikof.lootapi.LootTableAPI;
import pl.mikof.lootapi.LootTables;
import net.minecraft.world.item.Items;

public class YourMod {
    public void setupLoot(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Inicjalizuj API
            LootTableAPI.init();

            // Dodaj diamenty do coal ore
            LootTableAPI.addItemToTable(
                LootTables.Blocks.COAL_ORE,
                Items.DIAMOND,
                1, 3,  // 1-3 sztuki
                0.1f   // 10% szansa
            );

            // WAŻNE: Wywołaj na końcu!
            LootTableAPI.finalizeModifiers();
        });
    }
}
```

## 📖 Pełna dokumentacja API

### 1. Dodawanie przedmiotów

```java
// Podstawowe użycie - dodaje 1 przedmiot z 100% szansą
LootTableAPI.addItemToTable(tableId, Items.DIAMOND);

// Z określoną ilością
LootTableAPI.addItemToTable(tableId, Items.DIAMOND, 5);

// Z zakresem ilości
LootTableAPI.addItemToTable(tableId, Items.DIAMOND, 1, 3);

// Z zakresem i szansą
LootTableAPI.addItemToTable(tableId, Items.DIAMOND, 1, 3, 0.25f); // 25% szansa
```

### 2. Usuwanie przedmiotów

```java
// Usuwa wszystkie wystąpienia przedmiotu z tabeli
LootTableAPI.removeItemFromTable(
    LootTables.Entities.ZOMBIE,
    Items.ROTTEN_FLESH
);
```

### 3. Zamienianie przedmiotów

```java
// Zamienia jeden przedmiot na inny
LootTableAPI.replaceItem(
    LootTables.Blocks.GRASS_BLOCK,
    Items.DIRT,      // stary przedmiot
    Items.DIAMOND    // nowy przedmiot
);
```

### 4. Mnożenie dropów

```java
// Podwaja wszystkie dropy
LootTableAPI.multiplyDrops(
    LootTables.Blocks.DIAMOND_ORE,
    2.0f
);

// Potrójna ilość dropów
LootTableAPI.multiplyDrops(
    LootTables.Blocks.IRON_ORE,
    3.0f
);
```

### 5. Czyszczenie tabel

```java
// Wyłącza tabelę (nic nie dropuje)
LootTableAPI.disableLootTable(
    LootTables.Blocks.STONE
);
```

### 6. Ustawianie tylko jednego dropu

```java
// Czyści tabelę i dodaje tylko jeden przedmiot
LootTableAPI.setOnlyDrop(
    LootTables.Blocks.GRAVEL,
    Items.FLINT,
    2, 5  // 2-5 sztuk
);

// Pojedynczy przedmiot
LootTableAPI.setOnlyDrop(
    LootTables.Blocks.GRAVEL,
    Items.FLINT
);
```

## 🎯 Zaawansowane użycie

### Wiele tabel jednocześnie

```java
var builder = LootTableAPI.createCustomModifier("add", "my_custom_modifier")
    .forTables(
        LootTables.Blocks.COAL_ORE,
        LootTables.Blocks.IRON_ORE,
        LootTables.Blocks.GOLD_ORE
    )
    .withItem(Items.EMERALD)
    .withCount(1, 2)
    .withChance(0.15f);

LootTableAPI.registerModifier(builder);
```

### Niestandardowe tabele (dla modów)

```java
// Bloki z innych modów
LootTableAPI.addItemToTable(
    LootTables.Blocks.modded("yourmod", "custom_ore"),
    Items.DIAMOND,
    1, 5
);

// Moby z innych modów
LootTableAPI.addItemToTable(
    LootTables.Entities.modded("yourmod", "custom_mob"),
    Items.GOLDEN_APPLE
);

// Własne ResourceLocation
ResourceLocation customTable = ResourceLocation.fromNamespaceAndPath(
    "yourmod", "chests/custom_chest"
);
LootTableAPI.addItemToTable(customTable, Items.DIAMOND);
```

## 📝 Konfiguracja przez JSON

LootAPI wspiera również konfigurację przez pliki JSON w katalogu `config/lootapi/`:

```json
{
  "modifications": [
    {
      "table": "minecraft:blocks/diamond_ore",
      "item": "minecraft:emerald",
      "count": {
        "min": 1,
        "max": 3
      },
      "chance": 0.1
    }
  ],
  "removals": [
    {
      "table": "minecraft:entities/zombie",
      "item": "minecraft:rotten_flesh"
    }
  ],
  "replacements": [
    {
      "table": "minecraft:blocks/grass_block",
      "old_item": "minecraft:dirt",
      "new_item": "minecraft:diamond"
    }
  ],
  "multipliers": [
    {
      "table": "minecraft:blocks/iron_ore",
      "multiplier": 2.0
    }
  ],
  "disabled_tables": [
    "minecraft:blocks/stone"
  ]
}
```

## 🗂️ Dostępne tabele loot

LootAPI zawiera klasę `LootTables` z predefiniowanymi ścieżkami:

### Bloki
```java
LootTables.Blocks.DIAMOND_ORE
LootTables.Blocks.IRON_ORE
LootTables.Blocks.COAL_ORE
// ... i wiele więcej
```

### Moby
```java
LootTables.Entities.ZOMBIE
LootTables.Entities.SKELETON
LootTables.Entities.CREEPER
// ... i wiele więcej
```

### Skrzynie
```java
LootTables.Chests.BURIED_TREASURE
LootTables.Chests.DESERT_PYRAMID
LootTables.Chests.END_CITY_TREASURE
// ... i wiele więcej
```

### Wędkarstwo
```java
LootTables.Fishing.FISHING
LootTables.Fishing.FISHING_TREASURE
LootTables.Fishing.FISHING_JUNK
```

## 🔧 Wymagania

- Minecraft 1.21.1
- NeoForge 21.1.213 lub nowszy
- Java 21

## 📚 Architektura

LootAPI v3.0 używa **Global Loot Modifiers (GLM)** - oficjalnego systemu NeoForge do modyfikacji loot tables:

```
┌─────────────────┐
│  LootTableAPI   │ ← Prosty interfejs dla użytkowników
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ LootModifier-   │ ← Builder do tworzenia modifierów
│    Builder      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ LootModifier-   │ ← Rejestr i zapis do JSON
│    Registry     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  GLM Classes    │ ← Implementacja modifierów
│  - AddItem      │
│  - RemoveItem   │
│  - ReplaceItem  │
│  - MultiplyDrops│
│  - ClearTable   │
│  - SetOnlyDrop  │
└─────────────────┘
```

## ⚠️ Ważne uwagi

1. **Zawsze wywołuj `LootTableAPI.finalizeModifiers()`** na końcu konfiguracji
2. Modyfikacje wykonuj w fazie `FMLCommonSetupEvent.enqueueWork()`
3. GLM są **stackowane** - wiele modów może modyfikować te same tabele
4. Kolejność ma znaczenie - modifiery są aplikowane w kolejności rejestracji

## 🐛 Debugging

```java
// Wypisz informacje o wszystkich zarejestrowanych modifierach
LootTableAPI.printDebugInfo();

// Sprawdź liczbę modifierów
int count = LootTableAPI.getModificationCount();
System.out.println("Registered " + count + " modifiers");
```

## 📜 Licencja

MIT License - możesz swobodnie używać w swoich projektach

## 🤝 Wsparcie

Jeśli masz problemy lub pytania:
- Przeczytaj pełną dokumentację w `ExampleMod.java`
- Sprawdź logi gry - LootAPI loguje wszystkie operacje
- Upewnij się, że wywołujesz `finalizeModifiers()`

## 🔄 Changelog

### v3.0.0 (2025-10-30)
- ✨ Kompletna refaktoryzacja na Global Loot Modifiers
- ✨ Wszystkie funkcje w pełni zaimplementowane
- ✨ Nowy system builderów
- ✨ Wsparcie dla konfiguracji JSON
- ✨ Usunięto przestarzałe mechanizmy (LootTableLoadEvent, reflection)
- ✨ Pełna kompatybilność z NeoForge 1.21.1

### v2.0 (poprzednia wersja)
- ⚠️ Używała przestarzałych mechanizmów
- ⚠️ Problemy z kompatybilnością

## 🎉 Przykład kompletnej konfiguracji

```java
@Mod("yourmod")
public class YourMod {
    public YourMod(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LootTableAPI.init();

            // Dodaj diamenty do rud
            LootTableAPI.addItemToTable(
                LootTables.Blocks.COAL_ORE, Items.DIAMOND, 1, 2, 0.05f
            );

            // Usuń rotten flesh
            LootTableAPI.removeItemFromTable(
                LootTables.Entities.ZOMBIE, Items.ROTTEN_FLESH
            );

            // Podwój dropy z iron ore
            LootTableAPI.multiplyDrops(
                LootTables.Blocks.IRON_ORE, 2.0f
            );

            // WAŻNE: Finalizuj!
            LootTableAPI.finalizeModifiers();
        });
    }
}
```

---

**Made with ❤️ for the Minecraft modding community**
