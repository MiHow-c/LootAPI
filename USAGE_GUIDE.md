# 🚀 LootAPI v3.0 - Przewodnik Szybkiego Startu

## ✅ Dla użytkowników, którzy chcą używać API w swoim modzie

### Krok 1: Dodaj LootAPI do projektu

Skopiuj plik `lootapi-3.0.0.jar` do folderu `libs/` w swoim projekcie.

Dodaj do `build.gradle`:
```gradle
dependencies {
    implementation files("libs/lootapi-3.0.0.jar")
}
```

### Krok 2: Użyj w swoim modzie

W głównej klasie swojego moda:

```java
package com.example.yourmod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.world.item.Items;
import pl.mikof.lootapi.LootTableAPI;
import pl.mikof.lootapi.LootTables;

@Mod("yourmod")
public class YourMod {
    public YourMod(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 1. Inicjalizuj API
            LootTableAPI.init();

            // 2. Dodaj swoje modyfikacje

            // Przykład: Dodaj diamenty do coal ore
            LootTableAPI.addItemToTable(
                LootTables.Blocks.COAL_ORE,  // Tabela
                Items.DIAMOND,                // Przedmiot
                1, 3,                         // 1-3 sztuki
                0.1f                          // 10% szansa
            );

            // Przykład: Usuń rotten flesh z zombie
            LootTableAPI.removeItemFromTable(
                LootTables.Entities.ZOMBIE,
                Items.ROTTEN_FLESH
            );

            // Przykład: Podwój dropy z iron ore
            LootTableAPI.multiplyDrops(
                LootTables.Blocks.IRON_ORE,
                2.0f
            );

            // 3. WAŻNE: Finalizuj na końcu!
            LootTableAPI.finalizeModifiers();
        });
    }
}
```

### Krok 3: Uruchom grę

Wszystko powinno działać automatycznie!

## 📋 Najpopularniejsze przypadki użycia

### Dodawanie przedmiotów do bloków

```java
// Prosty przykład - zawsze dropuje 1 przedmiot
LootTableAPI.addItemToTable(
    LootTables.Blocks.STONE,
    Items.DIAMOND
);

// Z szansą
LootTableAPI.addItemToTable(
    LootTables.Blocks.DIRT,
    Items.EMERALD,
    1, 1,    // 1 sztuka
    0.05f    // 5% szansa
);

// Z zakresem
LootTableAPI.addItemToTable(
    LootTables.Blocks.GRAVEL,
    Items.GOLD_NUGGET,
    1, 5,    // 1-5 sztuk
    0.25f    // 25% szansa
);
```

### Dodawanie przedmiotów do mobów

```java
// Golden apple z zombie (100% szansa)
LootTableAPI.addItemToTable(
    LootTables.Entities.ZOMBIE,
    Items.GOLDEN_APPLE
);

// Diamenty ze szkieleta (10% szansa na 1-3)
LootTableAPI.addItemToTable(
    LootTables.Entities.SKELETON,
    Items.DIAMOND,
    1, 3,
    0.1f
);
```

### Usuwanie niechcianych dropów

```java
// Usuń rotten flesh
LootTableAPI.removeItemFromTable(
    LootTables.Entities.ZOMBIE,
    Items.ROTTEN_FLESH
);

// Usuń flint
LootTableAPI.removeItemFromTable(
    LootTables.Blocks.GRAVEL,
    Items.FLINT
);
```

### Zamiana przedmiotów

```java
// Dirt zmienia się w diamond
LootTableAPI.replaceItem(
    LootTables.Blocks.GRASS_BLOCK,
    Items.DIRT,      // Stary
    Items.DIAMOND    // Nowy
);
```

### Mnożenie dropów (dla serwerów typu "x2 drops")

```java
// Podwój wszystkie dropy z rud
LootTableAPI.multiplyDrops(LootTables.Blocks.IRON_ORE, 2.0f);
LootTableAPI.multiplyDrops(LootTables.Blocks.GOLD_ORE, 2.0f);
LootTableAPI.multiplyDrops(LootTables.Blocks.DIAMOND_ORE, 2.0f);

// Potrójna ilość z emerald ore
LootTableAPI.multiplyDrops(LootTables.Blocks.EMERALD_ORE, 3.0f);
```

### Wyłączanie dropów

```java
// Stone nic nie dropuje
LootTableAPI.disableLootTable(LootTables.Blocks.STONE);

// Cobblestone nic nie dropuje
LootTableAPI.disableLootTable(LootTables.Blocks.COBBLESTONE);
```

### Ustawianie tylko jednego dropu

```java
// Gravel ZAWSZE dropuje tylko flint (2-5 sztuk)
LootTableAPI.setOnlyDrop(
    LootTables.Blocks.GRAVEL,
    Items.FLINT,
    2, 5
);

// Zombie ZAWSZE dropuje tylko golden apple (1)
LootTableAPI.setOnlyDrop(
    LootTables.Entities.ZOMBIE,
    Items.GOLDEN_APPLE
);
```

## 🎯 Zaawansowane: Wiele tabel jednocześnie

```java
// Dodaj emeraldy do WSZYSTKICH deepslate ores
var builder = LootTableAPI.createCustomModifier("add", "emerald_bonus")
    .forTables(
        LootTables.Blocks.DEEPSLATE_COAL_ORE,
        LootTables.Blocks.DEEPSLATE_IRON_ORE,
        LootTables.Blocks.DEEPSLATE_GOLD_ORE,
        LootTables.Blocks.DEEPSLATE_DIAMOND_ORE,
        LootTables.Blocks.DEEPSLATE_EMERALD_ORE,
        LootTables.Blocks.DEEPSLATE_LAPIS_ORE
    )
    .withItem(Items.EMERALD)
    .withCount(1, 2)
    .withChance(0.15f);  // 15% szansa

LootTableAPI.registerModifier(builder);
```

## 🌐 Dla modów z custom przedmiotami/blokami

```java
import net.minecraft.resources.ResourceLocation;

// Twoje custom bloki
ResourceLocation customOre = ResourceLocation.fromNamespaceAndPath("yourmod", "blocks/custom_ore");
LootTableAPI.addItemToTable(customOre, Items.DIAMOND, 1, 5);

// Lub użyj helpera
LootTableAPI.addItemToTable(
    LootTables.Blocks.modded("yourmod", "custom_ore"),
    Items.DIAMOND,
    1, 5
);

// Dla custom mobów
LootTableAPI.addItemToTable(
    LootTables.Entities.modded("yourmod", "custom_boss"),
    Items.NETHER_STAR,
    1, 3
);
```

## ❗ Najczęstsze błędy

### 1. Zapomniałem wywołać finalizeModifiers()

```java
// ❌ ŹLE
LootTableAPI.init();
LootTableAPI.addItemToTable(...);
// Brak finalizeModifiers() - modyfikacje nie będą działać!

// ✅ DOBRZE
LootTableAPI.init();
LootTableAPI.addItemToTable(...);
LootTableAPI.finalizeModifiers(); // WAŻNE!
```

### 2. Wywołuję poza enqueueWork()

```java
// ❌ ŹLE
private void commonSetup(FMLCommonSetupEvent event) {
    LootTableAPI.init(); // Za wcześnie!
}

// ✅ DOBRZE
private void commonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
        LootTableAPI.init(); // Tutaj!
    });
}
```

### 3. Nieprawidłowe ResourceLocation

```java
// ❌ ŹLE
ResourceLocation wrong = new ResourceLocation("diamond_ore");

// ✅ DOBRZE
ResourceLocation correct = ResourceLocation.withDefaultNamespace("blocks/diamond_ore");
// lub
ResourceLocation correct = LootTables.Blocks.DIAMOND_ORE;
```

## 🐛 Debugging

```java
// Wypisz informacje o zarejestrowanych modifierach
LootTableAPI.printDebugInfo();

// Sprawdź liczbę
System.out.println("Modifiers: " + LootTableAPI.getModificationCount());
```

Sprawdź logi gry - LootAPI loguje wszystkie operacje:
```
[LootAPI]: Added minecraft:diamond to minecraft:blocks/coal_ore (count: 1-3, chance: 0.1)
[LootAPI]: Finalized 5 loot modifiers
```

## ❓ Często zadawane pytania (FAQ)

### Q: Wyłączyłem dropy ze stone, ale nadal dropuje cobblestone. Dlaczego?

**A:** To częsty problem wynikający z niezrozumienia mechaniki Minecraft!

**Ważne:** W vanilla Minecraft, gdy kopiesz blok **stone** (kamień), naturalnie dropuje **cobblestone** (bruk), NIE stone!

```java
// ✅ Aby wyłączyć cobblestone ze stone, użyj:
LootTableAPI.disableLootTable(LootTables.Blocks.STONE);

// To wyłączy WSZYSTKIE dropy z stone, włącznie z cobblestone
```

**Wyjaśnienie:**
- Blok stone w grze ma loot table `minecraft:blocks/stone`
- Ta loot table zawiera cobblestone jako normalny drop
- `disableLootTable()` wyłącza całą tabelę, więc cobblestone NIE będzie dropował
- Jeśli nadal dropuje, sprawdź czy mod się załadował poprawnie (patrz logi)

**Alternatywa** - usuń tylko cobblestone, ale zostaw inne dropy:
```java
LootTableAPI.removeItemFromTable(
    LootTables.Blocks.STONE,
    Items.COBBLESTONE
);
```

### Q: Mod się nie ładuje - widzę błąd "has no @SubscribeEvent methods"

**A:** To był błąd w wersji 2.0, który został naprawiony w wersji 3.0.

Rozwiązanie:
1. Upewnij się, że używasz najnowszego kodu
2. Wykonaj clean build: `./gradlew clean build`
3. Sprawdź logi czy widzisz "LootAPI v3.0 Initializing"

### Q: Moje modyfikacje nie działają w grze

**A:** Sprawdź kolejno:

1. **Czy wywołałeś `finalizeModifiers()`?**
   ```java
   LootTableAPI.init();
   // ... twoje modyfikacje ...
   LootTableAPI.finalizeModifiers(); // To MUSI być na końcu!
   ```

2. **Czy kod jest w `event.enqueueWork()`?**
   ```java
   private void commonSetup(FMLCommonSetupEvent event) {
       event.enqueueWork(() -> {
           // Tutaj twój kod
       });
   }
   ```

3. **Sprawdź logi:**
   - Szukaj `[LootAPI]` w logach gry
   - Powinno być: "LootAPI v3.0 Initializing..."
   - Powinno być: "Finalized X loot modifiers"

4. **Zrestartuj świat/grę** - modyfikacje są generowane przy starcie

### Q: Jak modyfikować loot tables z innych modów?

**A:** Użyj `LootTables.Blocks.modded()` lub `LootTables.Entities.modded()`:

```java
// Bloki z innego moda
LootTableAPI.addItemToTable(
    LootTables.Blocks.modded("iceandfire", "silver_ore"),
    Items.DIAMOND
);

// Moby z innego moda
LootTableAPI.addItemToTable(
    LootTables.Entities.modded("twilightforest", "lich"),
    Items.NETHER_STAR
);
```

## 📦 Pełna lista dostępnych tabel

Zobacz klasę `LootTables` w API:
- `LootTables.Blocks.*` - wszystkie bloki (rudy, stone, dirt, etc.)
- `LootTables.Entities.*` - wszystkie moby (zombie, skeleton, creeper, etc.)
- `LootTables.Chests.*` - wszystkie skrzynie (dungeons, temples, etc.)
- `LootTables.Fishing.*` - wędkarstwo

## 🎉 Gotowe!

Twój mod teraz używa LootAPI! System jest:
- ✅ W pełni kompatybilny z innymi modami
- ✅ Używa oficjalnego systemu NeoForge (GLM)
- ✅ Wydajny i stabilny
- ✅ Łatwy w użyciu

Jeśli masz pytania, sprawdź `README.md` lub przykład w `ExampleMod.java`!
