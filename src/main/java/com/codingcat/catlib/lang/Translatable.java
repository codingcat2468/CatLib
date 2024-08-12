package com.codingcat.catlib.lang;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Translatable {
    private static final String LANG_PATH = "lang";
    @Nullable
    private static JavaPlugin plugin;
    @Nullable
    private static String currentLanguage;
    @Nullable
    private static HashMap<String, String> data;

    public static void init(JavaPlugin plugin, String language) {
        Translatable.plugin = plugin;
        Translatable.loadLanguage(language);
    }

    public static void loadLanguage(String langName) {
        if (plugin == null) throw new TranslatableNotInitializedException();

        String path = String.format("%s/%s.json", LANG_PATH, langName);
        try {
            JsonObject jsonData = loadLangFile(path);
            Map<String, String> dataMap = jsonData.asMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getAsString()));

            Translatable.data = new HashMap<>(dataMap);
            Translatable.currentLanguage = langName;
            plugin.getLogger().info(String.format("Loaded language \"%s\"!", currentLanguage));
        } catch (FileNotFoundException e) {
            throw new UnknownLanguageException(langName);
        } catch (JsonParseException | UnsupportedOperationException e) {
            throw new JsonParseException(String.format("Failed to parse language file for %s", langName), e);
        }
    }

    public static void reloadLanguage() {
        if (plugin == null) throw new TranslatableNotInitializedException();

        plugin.getLogger().info("Reloading language...");
        loadLanguage(getCurrentLanguage());
    }

    public static String text(String langKey) {
        return (data != null) && data.containsKey(langKey) ?
                data.get(langKey) : langKey;
    }

    public static TextColor color(String colorKey) {
        return TextColor.fromHexString(Translatable.text(colorKey));
    }

    public static Component component(String langKey) {
        return Component.text(Translatable.text(langKey));
    }

    public static Component component(String langKey, String colorKey) {
        return Translatable.component(langKey).color(Translatable.color(colorKey));
    }

    public static Component component(String langKey, String colorKey, String... format) {
        return Component.text(String.format(Translatable.text(langKey), (Object[]) format)).color(Translatable.color(colorKey));
    }

    public static Component componentDynamicKey(String langKey, String colorKey, String... format) {
        return Component.text(Translatable.text(String.format(langKey, (Object[]) format))).color(Translatable.color(colorKey.formatted((Object[]) format)));
    }

    public static Component componentDynamicKey(String langKey, String colorKey, String[] keyFormat, String... format) {
        return Component.text(String.format(Translatable.text(String.format(langKey, (Object[]) keyFormat)), (Object[]) format)).color(Translatable.color(String.format(colorKey, (Object[]) keyFormat)));
    }

    public static @Nullable String getCurrentLanguage() {
        return currentLanguage;
    }

    private static JsonObject loadLangFile(String filePath) throws FileNotFoundException {
        assert plugin != null;
        InputStream langFile = plugin.getResource(filePath);
        if (langFile == null)
            throw new FileNotFoundException(filePath);

        InputStreamReader reader = new InputStreamReader(langFile);
        JsonObject data = new Gson().fromJson(reader, JsonObject.class);
        if (data == null)
            throw new FileNotFoundException(filePath);
        return data;
    }
}
