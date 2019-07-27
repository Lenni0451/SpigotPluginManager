package net.Lenni0451.SpigotPluginManager.utils;

public class NumberUtils {
	
	/**
	 * Get if an string is an integer
	 * 
	 * @param string the string you want to check
	 * @return if the given string is a integer
	 */
	public static boolean isInteger(final String string) {
		try {
			Integer.valueOf(string);
			return true;
		} catch (Exception e) {}
		return false;
	}
	
}
