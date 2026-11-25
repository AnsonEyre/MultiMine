<p align="center">
    A Minecraft Server Plugin/Mod
</p>
<p align="center">
  <img src="https://cdn.modrinth.com/data/fJ90SEuf/0e67fd5b9554f975e604766acb0ea563b59e926e_96.webp" alt="Logo">
  <h1 align="center">Multi Mine</h1>
</p>

<p align="center">
    You don't have to mine nonstop on a block to finish it off. You can pause, and come back later.
</p>

You don't have to point nonstop on a Block, never letting go of the mouse button, to finish it off. You can pause, walk away, mine another Block ... whatever you want.

![Demo](https://i.imgur.com/Xjt79tF.gif)

Works with multiple players too, so you can mine the same block together and get speed! (The more players, the more speed)

This mod is entirely server-side. Clients do not need it to play on a server. This also works on single player

Inspired by [this mod](https://www.curseforge.com/minecraft/mc-mods/multi-mine)

## Building

One Gradle build produces every platform's jar:

```
./gradlew build collectJars
```

Jars land in `build/libs/` (`MultiMine-<version>.jar` for Paper/Purpur/Folia, `MultiMine-fabric-<version>.jar` for Fabric).
Gradle downloads the JDKs it needs (21 for Paper, 25 for Fabric) on first run.

| Module   | What it is |
|----------|------------|
| `common` | Plain Java, no Minecraft dependency. All the mining/fade logic (`DamageTracker`). |
| `paper`  | Paper plugin. Forwards Bukkit block events to the tracker. |
| `fabric` | Fabric mod. Mixins on `ServerPlayerGameMode` forward the same events. |

Minecraft and loader versions for each platform are in `gradle.properties`.
To add another loader (NeoForge, or an Architectury setup), add a module that depends on `:common`, implements `MultiMinePlatform`, and calls the four `DamageTracker` methods.
