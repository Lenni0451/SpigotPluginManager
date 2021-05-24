package net.lenni0451.spm.utils;

import net.lenni0451.spm.PluginManager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class I18n {

    private static final Map<String, String> translations = new HashMap<>();

    public static void init() {
        InputStream is = getMessagesLang();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
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

    public static String t(final String translation, final Object... args) { //A short name for this method to prevent long lines of code
        String s = translations.getOrDefault(translation, translation).replace("&", "§").replace("§§", "&");
        for (int i = 0; i < args.length; i++) {
            s = s.replace("%" + (i + 1), args[i].toString());
        }
        return s;
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
