# 🎯 Przykład użycia Loot API

Ten przykład pokazuje jak używać Loot API w swoim modzie.

## 📦 Instalacja

### 1. Dodaj do `build.gradle`:

```groovy
repositories {
    mavenLocal()
    maven { url 'https://jitpack.io/' }
}

dependencies {
    modImplementation "pl.mikof:lootapi:1.0.0"
}
```

### 2. Dodaj do `fabric.mod.json`:

```json
{
  "depends": {
    "lootapi": ">=1.0.0"
  }
}
```

## 💻 Przykłady kodu

### Przykład 1: Dodaj diamenty do coal ore

```java
import pl.mikof.lootapi.*;
import net.minecraft.item.Items;

public class YourMod implements ModInitializer {
    @Override
    public void onInitialize() {
        LootTableAPI.addItemToTable(
            LootTables.Blocks.COAL_ORE,
            Items.DIAMOND,
            5
        );
    }
}
```

### Przykład 2: Stwórz nowy ore

```java
LootTableBuilder.quickOre(
    new Identifier("yourmod", "blocks/mythril_ore"),
    YourItems.RAW_MYTHRIL,
    2, 5
);
```

### Przykład 3: Custom boss

```java
LootTableBuilder.create("yourmod", "entities/boss")
    .addItem(Items.NETHER_STAR, 100)
    .pool(5, 10)
        .item(Items.DIAMOND, 30)
        .item(Items.EMERALD, 30)
    .endPool()
    .register();
```

## 📚 Więcej przykładów

Zobacz pełną dokumentację w [README.md](README.md)
