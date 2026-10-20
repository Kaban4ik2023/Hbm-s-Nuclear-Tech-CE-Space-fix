# +fix issue status

Review of open issues as of 2026-10-20. Based on `Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space`, commit `dadae98`. Space version: 0.9.2. NTM CE dependency: 2.6.1.0. Upstream issues have not been closed or modified.

Fork version 0.9.2.1 also includes the [English and Russian localization audit](LOCALIZATION.md). Its build and dedicated server check passed with all 10 regression tests.

| Issue | Findings and changes |
| --- | --- |
| [#106](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/106) | Fixed overlapping early and late mixin packages. The early configuration no longer owns the late RTG accessor package. Added a regression test for package separation and the presence of listed classes. |
| [#105](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/105) | Added an explicit recipe: 9 Semtex fragments produce 1 Semtex. Only the conflicting recipe that turns these fragments into `nothing` is removed. |
| [#104](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/104) | Flights now require fuel, including empty return flights. Empty pads no longer send the rocket back and forth. A loaded rocket stays on its pad when the destination cannot accept cargo. Fixed item NBT preservation, rejected insertions, restoration of links to other pads, and transfers of small fluid amounts. A receiving tank containing a different fluid is no longer overwritten. |
| [#103](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/103) | Fixed the surface texture path used by sunset rendering, including on Minmus. The reporter already confirmed that the original Mun problem was resolved upstream. A later log also contains a native JVM `EXCEPTION_ACCESS_VIOLATION` and an NEI/JEI conflict; the texture fix does not establish that those crashes are resolved. |
| [#102](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/102) | The first-join flag is copied when the player is cloned after death or a dimension change. Respawning no longer clears this flag and should no longer trigger the initial teleport to Duna again. |
| [#100](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/100) | Spawn lists are now mutable and persistent, including lists for additional creature types. This addresses the `UnsupportedOperationException` during Multi Mob registration. Space biomes still start with empty spawn lists. |
| [#98](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/98) | Added Remote Reactor Sensor handling: use it on a StarDar to store its core coordinates and dimension, then on the console to link the dish. Sneaking is supported. Added dimension validation and link restoration when the target chunk loads. |
| [#97](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/97) | Transporter pad proxies now expose the item handler capability to inserters. Existing proxies saved with `inv=false` are migrated when their chunk loads. |
| [#70](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/70) | Current upstream `TileEntityDishControl` no longer overrides the final `getUpdatePacket` method identified in the old crash report. Basic dedicated server startup passed. |
| [#49](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/49) | Current upstream already cancels `inWall` damage for passengers of `EntityRideableRocket`. The specific multiplayer station scenario still needs confirmation. |
| [#25](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/25) | According to the maintainers' comments, most missing recipes were already fixed upstream. Current code includes vacuum refinery processing for Tekto crude oil. The full Woodemium chain remains unfinished content; no arbitrary recipes or balance changes were added. |
| [#19](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/19) | Fixed a missing superclass unload call in Discharger: an unloaded machine no longer remains marked as loaded to networks and proxies. Also fixed restoration of stale transporter pad and console links. These changes do not confirm that every machine failure covered by the broad issue description is resolved. |
| [#13](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/13) | Added an optional mixin that prevents BuildCraft oil generation in the orbital dimension. It applies only when BuildCraft Energy is installed. Existing oil blocks are not removed. Integration with an installed BuildCraft instance has not been tested separately. |
| [#3](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/3) | The main fixes are already present upstream: Eve `f5e43c2`, fog `fb99a0d`, Ike animals `dba7236`, and Dres ore variants `bb93cfe`. The reported exception spam cannot be diagnosed without a complete stack trace. |
| [#107](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/107) | The issue contains no log, affected save, or precise reproduction steps. A control block survived saving, server shutdown, and a subsequent reload. The reported world reset has not been reproduced and is not claimed as fixed. |
| [#108](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space/issues/108) | A feature request for configurable armor airtightness rather than a bug report; not included in this fix set. |

## Validation

- `gradlew.bat clean build runServer`: passed.
- 8 regression tests passed. Coverage includes Discharger unloading, first-join flag NBT persistence, spawn lists, the texture resource, mixin package separation, transported item NBT, rejected insertions, and input slot boundaries.
- Dedicated server with Forge / Java 8 / MixinBooter 10.7: reached `Done`, saved a control block, and shut down cleanly.
- Dedicated server with Forge / Java 8 / MixinBooter 11.17: reached `Done`, reloaded the world, confirmed the saved control block, and shut down cleanly.
- Existing transporter proxy migration: `inv=0` was verified in the save; after reloading, `testforblock` confirmed `inv=1`. The control diamond block was also preserved.
- The final standard JAR was checked for Java 8 bytecode (major version 52), its build date manifest entry, the refmap, and the new Reactor Sensor handler's mapping.
- Full client testing of flights, multiplayer stations, Multi Mob, and BuildCraft has not been completed.

Build filenames and manifests contain the date in `YYYY-MM-DD` format. For standard Forge, use the JAR without an additional `-dev`, `-java25`, `-sources`, or `-downgraded` classifier.

The JVMDowngrader API preparation order was also fixed: a declared task restores the library after `clean`, rather than copying it only during Gradle configuration. Previously, `clean build` deleted the required API JAR before bytecode downgrading.
