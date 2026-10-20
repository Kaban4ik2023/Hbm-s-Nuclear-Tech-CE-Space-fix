# Hbm-s-Nuclear-Tech-CE-Space +fix

Fork of [Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space) with fixes for reported issues. See [FIXES.md](FIXES.md) for the status of each issue.

Build with JDK 25 and `./gradlew build` (Windows: `gradlew.bat build`). Minecraft tasks also require JDK 8, which Gradle can download automatically. The toolchain resolver has been updated for Gradle 9 compatibility.

The build date is added automatically using the `Asia/Yekaterinburg` time zone. Example: `NTM-Space-1.12.2+fix-0.9.2-2026-10-20.jar`. This is the standard Java 8+ JAR. The `-java25.jar` variant is for Java 25 / Cleanroom. Install one variant in place of the original Space Addon.

To rebuild with a specific date, use `./gradlew build -PbuildDate=2026-10-20`. The date is also recorded in the JAR manifest. The mod ID and registry names are preserved for world compatibility.

Definitive port of the NTM Space fork as an addon for 1.12.2 NTM:CE.
Requires [MixinBooter](https://modrinth.com/mod/mixinbooter) to run!
