package net.Lenni0451.spm.utils;

import java.util.List;

public class StringUtils {

    public static String arrayToString(final String[] array) {
        String out = "";
        for (String s : array) out += (s.isEmpty() ? "" : ", ") + s;
        return out;
    }

    public static String listToString(final List<String> list) {
        String out = "";
        for (String s : list) out += (s.isEmpty() ? "" : ", ") + s;
        return out;
    }

}
