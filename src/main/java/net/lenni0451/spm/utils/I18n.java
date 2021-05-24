package net.lenni0451.spm.utils;

import net.lenni0451.spm.PluginManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class I18n {

    private static final Map<String, String> translations = new HashMap<>();

    /**
     * Initialize the translations
     */
    public static void init() {
        InputStream is = getMessagesLang();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue; //Lines with comments don't get used
                String[] parts = line.split("=");
                if (parts.length <= 1) continue; //If the line does not contain a translation skip it

                translations.put(parts[0], StringUtils.arrayToString(parts, 1, "="));
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to read messages.lang", t);
        }
    }

    /**
     * Get the translation of a translation key
     *
     * @param translation The translation key
     * @param args        Arguments the translation can use
     * @return The translated string
     */
    public static String t(final String translation, final Object... args) {
        String s = translations.getOrDefault(translation, translation).replace("&", "§").replace("§§", "&");
        for (int i = 0; i < args.length; i++) {
            final Object arg = args[i];
            s = s.replace("%" + (i + 1), (arg == null ? "null" : args[i].toString()));
        }
        return s;
    }

    /**
     * Get the multi line translation of a translation key
     *
     * @param translations The translation key
     * @param args         Arguments the translation can use
     * @return The multi line translated string
     */
    public static String[] mt(final String translations, final Object... args) {
        return t(translations, args).split("\\n");
    }

    private static InputStream getMessagesLang() {
        File messagesFile = new File(PluginManager.getInstance().getDataFolder(), "messages.lang");
        if (!messagesFile.exists()) {
            try {
                InputStream internal = PluginManager.getInstance().getResource("messages.lang");
                FileOutputStream fos = new FileOutputStream(messagesFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = internal.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.close();
                internal.close();
            } catch (Throwable t) {
                throw new RuntimeException("Unable to write messages.lang", t);
            }
        }
        try {
            return new FileInputStream(messagesFile);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to load messages.lang", t);
        }
    }

}
