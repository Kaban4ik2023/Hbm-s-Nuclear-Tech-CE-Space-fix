# Hbm-s-Nuclear-Tech-CE-Space +fix

Форк [Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space](https://github.com/Warfactory-Official/Hbm-s-Nuclear-Tech-CE-Space) с исправлениями открытых issues. Подробный статус каждого сообщения — в [FIXES.md](FIXES.md).

Сборка: JDK 25 и `./gradlew build` (Windows: `gradlew.bat build`). Для задач Minecraft также нужен JDK 8; Gradle может загрузить его автоматически. Исправлена совместимость загрузчика JDK с Gradle 9.

Дата сборки добавляется автоматически по часовому поясу `Asia/Yekaterinburg`. Например: `NTM-Space-1.12.2+fix-0.9.2-2026-10-20.jar`. Это обычный JAR для Java 8+. Вариант с суффиксом `-java25.jar` предназначен для Java 25 / Cleanroom. Устанавливайте один вариант вместо исходного Space Addon.

Для повторной сборки с заданной датой: `./gradlew build -PbuildDate=2026-10-20`. Дата также записывается в манифест JAR. ID мода и имена объектов сохранены для совместимости миров.

Definitive port of the NTM Space fork in a form of the addon for 1.12.2 NTM:CE.  
Requires [MixinBooter](https://modrinth.com/mod/mixinbooter) to run!
