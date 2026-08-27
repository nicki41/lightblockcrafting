<p align="center">
  <img src="assets/logo.svg" width="160" alt="LightBlockCrafting Logo">
</p>

<h1 align="center">LightBlockCrafting</h1>

<p align="center">
  Craftable, visible light blocks in 15 brightness levels &ndash; 100% vanilla- and survival-friendly.
</p>

## What does this plugin do?

Vanilla light blocks (`minecraft:light`) are only visible and breakable if you hold a light
block in your own hand. This plugin fixes that:

- **Craftable in 15 levels** right at the crafting table, no command, no creative mode needed.
- **Always breakable** &ndash; even without a light block in hand, unlike vanilla.
- **Level number on demand** &ndash; a floating number shows the block's level, but only to
  players who are themselves holding a light block, just like vanilla's own indicator.
- **No resource pack, no mod** &ndash; only vanilla Bukkit/Paper mechanics (Display & Interaction entities).

## Recipes

Every level has its own fixed recipe, always based on 1x glass. No way to adjust it afterwards.

| Level | Recipe |
|:-----:|--------|
| 1&ndash;8   | 1x Glass + *N*x Glowstone Dust (N = level) |
| 9&ndash;15  | 1x Glass + *N*x Glowstone Block (N = level &minus; 7) |

Level 0 is deliberately not craftable. All recipes fit in a normal 3x3 crafting grid and are
automatically unlocked in players' recipe books.

## Admin command

```
/lightblock give <level 0-15> [player]
```

Permission: `lightblockcrafting.admin` (default: operators).

## Supported versions

A single build works unmodified on any **Paper-based** server (Paper, Purpur, Pufferfish, &hellip;)
from **1.19.4 to 26.2** &ndash; it only uses Bukkit/Paper APIs that have been stable since 1.19.4
(Display & Interaction entities).

## Build

Only an internet connection is needed for the first build (Gradle downloads the matching Java
toolchain automatically).

```
./gradlew build
```

The finished plugin ends up at `build/libs/LightBlockCrafting-<version>.jar` and belongs in the
`plugins` folder of a Paper server.

## License

[GPL-3.0](LICENSE)
