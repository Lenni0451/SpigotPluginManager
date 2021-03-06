package net.lenni0451.spm.utils;

public class NumberUtils {

    /**
     * Get if an string is an integer
     *
     * @param string The string you want to check
     * @return If the given string represents a integer
     */
    public static boolean isInteger(final String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

}
