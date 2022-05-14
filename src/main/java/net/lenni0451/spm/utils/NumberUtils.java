package net.lenni0451.spm.utils;

public class NumberUtils {

    /**
     * Check if a {@link String} is an int
     *
     * @param string The {@link String} to check
     * @return {@code true} if the {@link String} is an int
     */
    public static boolean isInteger(final String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

}
