package net.lenni0451.spm.utils;

import java.util.List;

public class StringUtils {

    /**
     * Convert an array of {@link String}s to a {@link String} with the given separator
     *
     * @param array     The array of {@link String}s
     * @param start     The start index
     * @param separator The separator
     * @return The combined {@link String}
     */
    public static String arrayToString(final String[] array, final int start, final String separator) {
        StringBuilder out = new StringBuilder();
        for (int i = start; i < array.length; i++) out.append((out.length() == 0) ? "" : separator).append(array[i]);
        return out.toString();
    }

    /**
     * Convert a {@link List} of {@link String}s to a {@link String} separated by spaces
     *
     * @param list The {@link List} of {@link String}s
     * @return The combined {@link String}
     */
    public static String listToString(final List<String> list) {
        StringBuilder out = new StringBuilder();
        for (String s : list) out.append((out.length() == 0) ? "" : ", ").append(s);
        return out.toString();
    }

}
