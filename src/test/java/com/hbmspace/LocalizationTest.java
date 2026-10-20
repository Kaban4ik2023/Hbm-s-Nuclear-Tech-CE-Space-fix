package com.hbmspace;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class LocalizationTest {
    private static final Pattern FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([s%])");

    @Test
    void bothLanguagesHaveTheSameKeysAndCompatibleFormats() throws Exception {
        Map<String, String> english = readLanguage("hbmspace", "en_us");
        Map<String, String> russian = readLanguage("hbmspace", "ru_ru");
        assertEquals(english.keySet(), russian.keySet());
        for (String key : english.keySet()) {
            assertEquals(arguments(english.get(key)), arguments(russian.get(key)), key);
            assertFalse(english.get(key).matches(".*[А-Яа-яЁё].*"), "Russian text in English: " + key);
        }
        assertEquals("Alex превратился в лужицу кипящей плоти.",
                String.format(Locale.ROOT, russian.get("death.attack.eve"), "Alex"));
    }

    @Test
    void referencedAddonKeysAndCelestialBodiesHaveTranslations() throws Exception {
        Pattern references = Pattern.compile(
                "(?:resolveKey(?:Array)?|I18n\\.format|TextComponentTranslation|CommandException|nextTranslation|startTranslation)\\(\\s*\"([\\w.]+)\"(?!\\s*\\+)");
        Pattern commandMessages = Pattern.compile("showMessage\\(sender,\\s*\"([\\w.]+)\"");
        Pattern bodies = Pattern.compile("new CelestialBody\\(\"(\\w+)\"");
        Pattern fluids = Pattern.compile("space\\$createFixed\\(\"([A-Z_0-9]+)\"");
        Pattern entities = Pattern.compile("@AutoRegister\\(name = \"(\\w+)\"");
        Pattern usages = Pattern.compile("return \"(commands\\.[\\w.]+)\";");
        Path sources = Path.of("src/main/java");
        for (String locale : new String[] {"en_us", "ru_ru"}) {
            Map<String, String> translations = readLanguage("hbm", locale);
            translations.putAll(readLanguage("hbmspace", locale));
            List<String> missing = new ArrayList<>();
            try (var files = Files.walk(sources)) {
                for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                    String source = Files.readString(file);
                    for (Pattern pattern : new Pattern[] {references, commandMessages, usages}) {
                        Matcher matcher = pattern.matcher(source);
                        while (matcher.find()) {
                            String key = matcher.group(1);
                            // These keys are provided by vanilla, not the addon or NTM CE.
                            if (key.equals("container.inventory") || key.equals("commands.time.usage")) continue;
                            if (!translations.containsKey(key)) missing.add(key + " in " + file);
                        }
                    }
                    if (file.getFileName().toString().equals("SolarSystem.java")) {
                        Matcher matcher = bodies.matcher(source);
                        while (matcher.find()) {
                            if (!translations.containsKey("body." + matcher.group(1))) missing.add("body." + matcher.group(1));
                        }
                    }
                    Matcher fluid = fluids.matcher(source);
                    while (fluid.find()) {
                        String key = "hbmfluid." + fluid.group(1).toLowerCase(Locale.ROOT);
                        if (!translations.containsKey(key)) missing.add(key);
                    }
                    Matcher entity = entities.matcher(source);
                    while (entity.find()) {
                        String key = "entity." + entity.group(1) + ".name";
                        if (!translations.containsKey(key)) missing.add(key);
                    }
                }
            }
            assertTrue(missing.isEmpty(), locale + ": " + missing);
        }
    }

    private static List<Integer> arguments(String value) {
        List<Integer> args = new ArrayList<>();
        Matcher matcher = FORMAT.matcher(value);
        int implicit = 1;
        int position = 0;
        while (matcher.find()) {
            assertFalse(value.substring(position, matcher.start()).contains("%"), value);
            if (!matcher.group(2).equals("%")) {
                args.add(matcher.group(1) == null ? implicit++ : Integer.parseInt(matcher.group(1)));
            }
            position = matcher.end();
        }
        assertFalse(value.substring(position).contains("%"), value);
        if (!args.isEmpty()) {
            Object[] samples = new Object[Collections.max(args)];
            java.util.Arrays.fill(samples, "sample");
            assertDoesNotThrow(() -> String.format(Locale.ROOT, value, samples), value);
        }
        Collections.sort(args);
        return args;
    }

    private static Map<String, String> readLanguage(String domain, String locale) throws Exception {
        String resource = "/assets/" + domain + "/lang/" + locale + ".lang";
        Map<String, String> entries = new LinkedHashMap<>();
        try (InputStream stream = LocalizationTest.class.getResourceAsStream(resource)) {
            assertNotNull(stream, resource);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                for (String line; (line = reader.readLine()) != null;) {
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int equals = line.indexOf('=');
                    assertTrue(equals > 0, resource + ": " + line);
                    String key = line.substring(0, equals);
                    String value = line.substring(equals + 1);
                    if (domain.equals("hbmspace")) {
                        assertFalse(value.trim().isEmpty(), key);
                        assertFalse(value.contains("\uFFFD") || value.contains("Â"), key);
                        assertFalse(entries.containsKey(key), "Duplicate key: " + key);
                    }
                    entries.put(key, value);
                }
            }
        }
        return entries;
    }
}
