package net.lenni0451.spm.utils;

import java.util.List;

public class StringUtils {

    public static String arrayToString(final String[] array) {
        StringBuilder out = new StringBuilder();
        for (String s : array) out.append(s.isEmpty() ? "" : ", ").append(s);
        return out.toString();
    }

    public static String listToString(final List<String> list) {
        StringBuilder out = new StringBuilder();
        for (String s : list) out.append(s.isEmpty() ? "" : ", ").append(s);
        return out.toString();
    }

}
