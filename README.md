<p align="center">
  <img src="assets/logo.svg" width="160" alt="LightBlockCrafting Logo">
</p>

<h1 align="center">LightBlockCrafting</h1>

<p align="center">
  Craftbare, sichtbare Lichtbl&ouml;cke in 15 Helligkeitsstufen &ndash; 100% vanilla- und survivalfreundlich.
</p>

## Was macht das Plugin?

Vanilla-Lichtbl&ouml;cke (`minecraft:light`) sind nur sichtbar und abbaubar, wenn man selbst
einen Lichtblock in der Hand h&auml;lt. Dieses Plugin l&ouml;st das:

- **Craftbar in 15 Stufen** direkt am Crafting-Tisch, kein Kommando, kein Kreativmodus n&ouml;tig.
- **Immer sichtbar & abbaubar** &ndash; auch ohne Lichtblock in der Hand. Der Marker zeigt einfach die
  Stufe als schwebende Zahl.
- **Kein Resourcepack, kein Mod** &ndash; nur vanilla Bukkit/Paper-Mechaniken (Display- & Interaction-Entities).

## Rezepte

Jede Stufe hat ihr eigenes festes Rezept, immer mit 1x Glas als Basis. Keine nachtr&auml;gliche
Anpassung m&ouml;glich.

| Stufe | Rezept |
|:-----:|--------|
| 1&ndash;8   | 1x Glas + *N*x Glowstone-Staub (N = Stufe) |
| 9&ndash;15  | 1x Glas + *N*x Glowstone-Block (N = Stufe &minus; 7) |

Stufe 0 ist absichtlich nicht craftbar. Alle Rezepte passen in ein normales 3x3-Crafting-Feld
und werden Spielern automatisch im Rezeptbuch freigeschaltet.

## Admin-Befehl

```
/lightblock give <Stufe 0-15> [Spieler]
```

Permission: `lightblockcrafting.admin` (Standard: Operatoren).

## Unterst&uuml;tzte Versionen

Ein einziges Build funktioniert unver&auml;ndert auf jedem **Paper-basierten** Server (Paper,
Purpur, Pufferfish, &hellip;) von **1.19.4 bis 26.2** &ndash; genutzt werden ausschlie&szlig;lich
seit 1.19.4 stabile Bukkit/Paper-APIs (Display- & Interaction-Entities).

## Build

Ben&ouml;tigt wird nur eine Internetverbindung f&uuml;r den ersten Build (Gradle l&auml;dt die
passende Java-Toolchain automatisch).

```
./gradlew build
```

Das fertige Plugin liegt danach unter `build/libs/LightBlockCrafting-<version>.jar` und geh&ouml;rt
in den `plugins`-Ordner eines Paper-Servers.

## Lizenz

[GPL-3.0](LICENSE)
