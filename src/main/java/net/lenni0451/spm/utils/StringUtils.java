package net.lenni0451.spm.utils;

import java.util.List;

public class StringUtils {

    public static String arrayToString(final String[] array, final int start, final String separator) {
        StringBuilder out = new StringBuilder();
        for (int i = start; i < array.length; i++) out.append((out.length() == 0) ? "" : separator).append(array[i]);
        return out.toString();
    }

    public static String listToString(final List<String> list) {
        StringBuilder out = new StringBuilder();
        for (String s : list) out.append((out.length() == 0) ? "" : ", ").append(s);
        return out.toString();
    }

}
