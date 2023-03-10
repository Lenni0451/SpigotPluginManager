package net.lenni0451.pluginmanager.i18n;

import net.lenni0451.pluginmanager.Main;
import net.lenni0451.pluginmanager.utils.YamlFormatter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public class TranslationsConfig {

    private static final File I18N_FILE = new File(Main.getInstance().getDataFolder(), "translations.yml");
    private static final Yaml YAML = new Yaml();

    public static void load() {
        if (!I18N_FILE.exists()) {
            save();
        } else if (!I18N_FILE.isFile()) {
            throw new IllegalStateException("The i18n file is not a file! Please delete it and restart the server.");
        } else {
            boolean[] hasError = {false};
            boolean[] hasMissing = {false};
            try (FileInputStream fis = new FileInputStream(I18N_FILE)) {
                Map<String, Object> translations = YAML.loadAs(fis, LinkedHashMap.class);
                TranslationsSerializer.deserialize(path -> {
                    Map<String, Object> currentTranslations = translations;
                    String[] parts = path.split("\\.");
                    for (int i = 0; i < parts.length; i++) {
                        String part = parts[i];
                        if (!currentTranslations.containsKey(part)) {
                            hasMissing[0] = true;
                            return null;
                        }
                        Object value = currentTranslations.get(part);
                        if (i == parts.length - 1) {
                            try {
                                return (String) value;
                            } catch (ClassCastException e) {
                                hasError[0] = true;
                                return null;
                            }
                        } else {
                            try {
                                currentTranslations = (Map<String, Object>) value;
                            } catch (ClassCastException e) {
                                hasError[0] = true;
                                return null;
                            }
                        }
                    }
                    return null;
                });
            } catch (Throwable t) {
                Main.getInstance().getLogger().log(Level.SEVERE, "Failed to load the I18n file. Translations are not loaded!", t);
            }
            if (hasError[0]) {
                Main.getInstance().getLogger().log(Level.WARNING, "The I18n file contains errors. All invalid translations are resetted.");
                save();
            }
            if (hasMissing[0]) {
                Main.getInstance().getLogger().log(Level.WARNING, "The I18n file is missing translations. All missing translations are added with the default value.");
                save();
            }
        }
    }

    public static void save() {
        I18N_FILE.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(I18N_FILE)) {
            Map<String, String> translations = new LinkedHashMap<>();
            TranslationsSerializer.serialize(translations::put);
            fos.write(YamlFormatter.format(YamlFormatter.buildYamlStyle(translations)).getBytes(StandardCharsets.UTF_8));
        } catch (Throwable t) {
            Main.getInstance().getLogger().log(Level.SEVERE, "Failed to save the I18n file. Translations are not saved!", t);
        }
    }

}
