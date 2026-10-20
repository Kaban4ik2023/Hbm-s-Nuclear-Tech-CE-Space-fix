# English and Russian localization audit

Space +fix 0.9.2.1, 2026-10-20. This update includes the earlier issue fixes described in [FIXES.md](FIXES.md).

Both addon language files now contain **855 matching keys**. Compared with the previous +fix release, English gained 228 entries and Russian gained 226. Existing values were corrected in 22 English entries and 53 Russian entries. No existing keys were removed.

## Changes

- Fixed the malformed Russian `%1$s` player-name placeholder in Eve's death message and the damaged copyright symbol in NITAN's name.
- Corrected spelling and terminology in both languages, including Einsteinium, lanthanum, milk, Watz isotope labels, and machine descriptions. Translated the remaining generic `Plastic` and custom warhead labels in Russian.
- Added missing station controls, station command responses, celestial bodies, entities, items, blocks, and lithium and hydrogen fluids.
- Moved hardcoded text into language files: StarDar navigation and landing controls, rocket construction and launch checks, drive processing, satellites, transporter linking, armor tooltips, atmosphere tools, Dyson machines, thrusters, and the orbital weapons console.
- Server chat, invasion announcements, and boss-bar names use translation components so each client chooses its own language. Drive processing and assembly errors carry translation keys across the network and are resolved on the client.
- Kept full translated fluid names in launch checks instead of truncating them to the last word.
- Added width fitting for StarDar status rows so longer translations fit before the panel buttons.

Names such as StarDar, PLSS, NITAN, engine model codes, and music titles remain proper names or abbreviations. Logs, script/API responses, configuration comments, user-entered names, and text embedded in artwork are outside this language-file and interface audit. This is not a complete visual playthrough of every screen.

## Automated checks

`LocalizationTest` checks duplicate and empty entries, damaged encoding, matching key sets, compatible format arguments, actual formatting of the repaired death message, and Cyrillic text accidentally placed in English values. It also checks literal translation references and generated names for celestial bodies, registered entities, and addon fluids against the addon and NTM CE language resources.

Validation passed with `gradlew.bat build runServer`: 10 tests passed with no failures, including the 8 existing fix regressions. The dedicated Forge server started on Java 8, reloaded the existing test world, confirmed the saved transporter proxy and diamond control block, saved, and shut down cleanly. Both packaged languages contain 855 entries. The standard JAR retains Java 8 bytecode and the Reactor Sensor refmap entry.

Full visual verification of every interface in both languages has not been completed.
