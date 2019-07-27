package net.Lenni0451.SpigotPluginManager.softdepends;

import java.io.File;

import be.maximvdw.mvdwupdater.MVdWUpdater;
import be.maximvdw.mvdwupdater.spigotsite.api.exceptions.ConnectionFailedException;
import be.maximvdw.mvdwupdater.spigotsite.api.resource.Resource;
import net.Lenni0451.SpigotPluginManager.PluginManager;

public class MVdWUpdater_Adapter {
	
	/**
	 * Get the MVdWUpdater plugin instance
	 * 
	 * @return the MVdWUpdater plugin instance
	 */
	public static MVdWUpdater getPlugin() {
		return (MVdWUpdater) PluginManager.getInstance().getPluginUtils().getPlugin("MVdWUpdater");
	}
	
	/**
	 * Check if a player has bought a resource on spigotmc
	 * 
	 * @param resourceId
	 * @return if the player has the resource
	 * @throws ConnectionFailedException if the site could not be accessed
	 */
	public static boolean hasResource(final int resourceId) throws ConnectionFailedException {
		MVdWUpdater updater = getPlugin();
		return updater.hasBought(updater.getSpigotUser(), resourceId);
	}
	
	/**
	 * Download a premium resource
	 * 
	 * @param file where to save the resource
	 * @param resourceId
	 * @return if the resource could be downloaded
	 * @throws ConnectionFailedException if the site could not be accesse
	 */
	public static boolean downloadPlugin(final File file, final int resourceId) throws ConnectionFailedException {
		try {
			MVdWUpdater updater = getPlugin();
			Resource premiumResource = updater.getSpigotSiteAPI().getResourceManager().getResourceById(resourceId, updater.getSpigotUser());
			premiumResource.downloadResource(updater.getSpigotUser(), file);
		} catch (Throwable e) {}
		return file.exists();
	}
	
}

