package net.lenni0451.spm.messages;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.messages.lines.CommentLine;
import net.lenni0451.spm.messages.lines.TextLine;
import net.lenni0451.spm.messages.lines.TranslationLine;
import net.lenni0451.spm.utils.StringUtils;

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
     * Load all translations and update the messages.lang file
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
     * Get a translation by its key
     *
     * @param translation The translation key
     * @param args        Arguments to replace in the translation
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
     * Get a multiline translation by its key
     *
     * @param translation The translation key
     * @param args        Arguments to replace in the translation
     * @return The translated string array
     */
    public static String[] mt(final String translation, final Object... args) {
        return t(translation, args).split(Pattern.quote("\\n"));
    }

    /**
     * Get if the messages.lang file has been updated
     *
     * @return {@code true} if it has been updated
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
            List<IMessagesLine> lines = new ArrayList<>();
            Map<String, List<TranslationLine>> translations = new HashMap<>();
            boolean hasDuplicates = false;
            {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();

                    if (line.startsWith("#")) {
                        lines.add(new CommentLine(line.substring(1).trim()));
                    } else if (line.split("=").length >= 2) {
                        String[] parts = line.split("=");
                        TranslationLine translationLine = new TranslationLine(parts[0], StringUtils.arrayToString(parts, 1, "="));
                        if (translations.containsKey(translationLine.getKey())) hasDuplicates = true;
                        lines.add(translationLine);
                        translations.computeIfAbsent(translationLine.getKey(), key -> new ArrayList<>()).add(translationLine);
                    } else {
                        lines.add(new TextLine(line));
                    }
                }
                br.close();
            }
            if (hasDuplicates) {
                for (Map.Entry<String, List<TranslationLine>> entry : translations.entrySet()) {
                    if (entry.getValue().size() > 1) lines.removeAll(entry.getValue());
                }
            }
            for (Map.Entry<String, String> entry : TRANSLATIONS.entrySet()) {
                LABEL_ADD:
                {
                    for (IMessagesLine line : lines) {
                        if (!(line instanceof TranslationLine)) continue;
                        TranslationLine translationLine = (TranslationLine) line;
                        if (translationLine.getKey().equals(entry.getKey())) break LABEL_ADD;
                    }
                    if (!HAS_UPDATE) {
                        HAS_UPDATE = true;
                        lines.add(new TextLine(""));
                        lines.add(new CommentLine("Your lang file has missing translations:"));
                    }
                    lines.add(new TranslationLine(entry.getKey(), entry.getValue()));
                }
            }
            if (hasDuplicates) {
                HAS_UPDATE = true;
                lines.add(new TextLine(""));
                lines.add(new CommentLine("Your lang file has duplicate translations which have been removed:"));
                for (Map.Entry<String, List<TranslationLine>> entry : translations.entrySet()) {
                    if (entry.getValue().size() > 1) {
                        lines.add(new CommentLine(entry.getKey()));
                        for (TranslationLine translationLine : entry.getValue()) {
                            lines.add(new CommentLine(" - " + translationLine.getValue()));
                        }
                    }
                }
            }
            if (HAS_UPDATE) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(MESSAGES_FILE), StandardCharsets.UTF_8));
                for (IMessagesLine line : lines) {
                    bw.write(line.getLine());
                    bw.newLine();
                }
                bw.close();
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update messages.lang", t);
        }
    }

}
