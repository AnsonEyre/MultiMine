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

## Supported versions

| Platform | Minecraft | Jar |
|----------|-----------|-----|
| Paper / Purpur / Folia | 1.21.11 and up (tested 1.21.11 – 26.3) | `MultiMine-<version>+1.21.11-26.3.jar` |
| Fabric (needs Fabric API) | 26.1 and up (tested 26.1.2 – 26.3) | `MultiMine-fabric-<version>+26.1-26.3.jar` |
| NeoForge | 26.1 and up (tested 26.1.2 – 26.3) | `MultiMine-neoforge-<version>+26.1-26.3.jar` |

The repo only holds code for the newest Minecraft version. Jars for older versions (such as the
1.21.11 Fabric and NeoForge builds) are on Modrinth.

The Paper plugin only uses the public Paper API, so one jar covers every version.

## Building

One Gradle build produces every platform's jar:

```
./gradlew build collectJars
```

Jars land in `build/libs/`.

Gradle downloads the JDKs it needs (21 for Paper, 25 for the mod loaders) on first run.

| Module     | What it is |
|------------|------------|
| `core`     | Plain Java, no Minecraft dependency. All the mining/fade logic (`DamageTracker`). Used by both the plugin and the mod. |
| `paper`    | Paper plugin. Forwards Bukkit block events to the tracker. Public Paper API only, compiled against the oldest supported version. |
| `common`   | [Architectury](https://docs.architectury.dev/) common module. The Minecraft-side mod code every loader shares: the `ServerPlayerGameMode` mixin, the platform implementation, config loading and the tick scheduler. `core` is compiled into it. |
| `fabric`   | Fabric entrypoint. Hooks server tick and stopping events. |
| `neoforge` | NeoForge entrypoint. Same, with NeoForge events. |

The mod modules use Architectury Loom's no-remap variant, since Minecraft 26.x ships unobfuscated.
Minecraft, loader and plugin versions are in `gradle.properties`.

Dev servers: `./gradlew :fabric:runServer`, `:neoforge:runServer` (accept the EULA in `<loader>/run/eula.txt` first), or `:paper:runServer` (pick the version with `-Ppaper_run_version=26.3`).

### Updating to a new Minecraft version

1. Bump `minecraft_version`, `fabric_api_version`, `neoforge_version` and `mod_jar_mc_range` in `gradle.properties`, and widen `paper_jar_mc_range` once the Paper jar is tested on it.
2. If older versions dropped out, raise the minimum in `fabric.mod.json` and `neoforge.mods.toml`.
