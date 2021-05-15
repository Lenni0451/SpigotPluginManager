package net.Lenni0451.spm.softdepends;

import be.maximvdw.mvdwupdater.MVdWUpdater;
import be.maximvdw.mvdwupdater.spigotsite.api.exceptions.ConnectionFailedException;
import be.maximvdw.mvdwupdater.spigotsite.api.resource.Resource;
import net.Lenni0451.spm.PluginManager;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Optional;

public class MVdWUpdater_Adapter {

    /**
     * Get the MVdWUpdater plugin instance
     *
     * @return The MVdWUpdater plugin instance
     */
    public static MVdWUpdater getPlugin() {
        Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin("MVdWUpdater");
        return (MVdWUpdater) plugin.orElse(null);
    }

    /**
     * Check if a player has bought a resource on spigotmc
     *
     * @param resourceId The id of the resource
     * @return If the player has the resource
     * @throws ConnectionFailedException If the site could not be accessed
     */
    public static boolean hasResource(final int resourceId) throws ConnectionFailedException {
        MVdWUpdater updater = getPlugin();
        return updater.hasBought(updater.getSpigotUser(), resourceId);
    }

    /**
     * Download a premium resource
     *
     * @param file       Where to save the resource
     * @param resourceId The id of the resource
     * @return If the resource could be downloaded
     */
    public static boolean downloadPlugin(final File file, final int resourceId) {
        try {
            MVdWUpdater updater = getPlugin();
            Resource premiumResource = updater.getSpigotSiteAPI().getResourceManager().getResourceById(resourceId, updater.getSpigotUser());
            premiumResource.downloadResource(updater.getSpigotUser(), file);
        } catch (Throwable ignored) {
        }
        return file.exists();
    }

}

