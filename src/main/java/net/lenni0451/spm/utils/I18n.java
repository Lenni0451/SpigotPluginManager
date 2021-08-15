package net.lenni0451.spm.utils;

import net.lenni0451.spm.PluginManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class I18n {

    private static final Map<String, String> TRANSLATIONS = new HashMap<>();
    private static final File MESSAGES_FILE = new File(PluginManager.getInstance().getDataFolder(), "messages.lang");

    private static boolean HAS_UPDATE = false;

    /**
     * Initialize the translations
     */
    public static void init() {
        try { //Load default translations to not show raw translation strings if a translation is missing
            loadFile(PluginManager.getInstance().getResource("messages.lang"));
        } catch (Throwable t) {
            throw new RuntimeException("Unable to write messages.lang", t);
        }
        updateLangFile();
        loadFile(getMessagesLang());
    }

    /**
     * Get the translation of a translation key
     *
     * @param translation The translation key
     * @param args        Arguments the translation can use
     * @return The translated string
     */
    public static String t(final String translation, final Object... args) {
        String s = TRANSLATIONS.getOrDefault(translation, translation).replace("&", "§").replace("§§", "&");
        for (int i = 0; i < args.length; i++) {
            final Object arg = args[i];
            s = s.replace("%" + (i + 1), (arg == null ? "null" : args[i].toString()));
        }
        return s;
    }

    /**
     * Get the multi line translation of a translation key
     *
     * @param translation The translation key
     * @param args        Arguments the translation can use
     * @return The multi line translated string
     */
    public static String[] mt(final String translation, final Object... args) {
        return t(translation, args).split(Pattern.quote("\\n"));
    }

    /**
     * @return If the messages.lang file has missing translations
     */
    public static boolean wasUpdated() {
        return HAS_UPDATE;
    }

    private static InputStream getMessagesLang() {
        if (!MESSAGES_FILE.exists()) {
            try {
                InputStream internal = PluginManager.getInstance().getResource("messages.lang");
                FileOutputStream fos = new FileOutputStream(MESSAGES_FILE);
                byte[] buf = new byte[1024];
                int len;
                while ((len = internal.read(buf)) != -1) fos.write(buf, 0, len);
                fos.close();
                internal.close();
            } catch (Throwable t) {
                throw new RuntimeException("Unable to write messages.lang", t);
            }
        }
        try {
            return new FileInputStream(MESSAGES_FILE);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to load messages.lang", t);
        }
    }

    private static void loadFile(final InputStream inputStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue; //Lines with comments don't get used
                String[] parts = line.split("=");
                if (parts.length <= 1) continue; //If the line does not contain a translation skip it

                TRANSLATIONS.put(parts[0], StringUtils.arrayToString(parts, 1, "="));
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to load translations", t);
        }
    }

    private static void updateLangFile() {
        if (!MESSAGES_FILE.exists()) return;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(MESSAGES_FILE), StandardCharsets.UTF_8));
            List<Tuple<String, String>> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#") && line.split("=").length >= 2) {
                    String[] parts = line.split("=");
                    lines.add(new Tuple<>(parts[0], StringUtils.arrayToString(parts, 1, "=")));
                } else {
                    lines.add(new Tuple<>(line, null));
                }
            }
            br.close();
            for (Map.Entry<String, String> entry : TRANSLATIONS.entrySet()) {
                LABEL_ADD:
                {
                    for (Tuple<String, String> tuple : lines) {
                        if (tuple.a().equals(entry.getKey()) && tuple.b() != null) break LABEL_ADD;
                    }
                    if (!HAS_UPDATE) {
                        HAS_UPDATE = true;
                        lines.add(new Tuple<>("", null));
                        lines.add(new Tuple<>("#Your lang file has missing translations:", null));
                    }
                    lines.add(new Tuple<>(entry.getKey(), entry.getValue()));
                }
            }
            if (HAS_UPDATE) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(MESSAGES_FILE), StandardCharsets.UTF_8));
                for (Tuple<String, String> tuple : lines) {
                    bw.write(tuple.a() + (tuple.b() == null ? "" : ("=" + tuple.b())));
                    bw.newLine();
                }
                bw.close();
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update messages.lang", t);
        }
    }

}
