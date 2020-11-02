package net.Lenni0451.SpigotPluginManager;

import net.Lenni0451.SpigotPluginManager.commands.PluginManager_Command;
import net.Lenni0451.SpigotPluginManager.commands.Reload_Command;
import net.Lenni0451.SpigotPluginManager.softdepends.SoftDepends;
import net.Lenni0451.SpigotPluginManager.tabcomplete.PluginManager_TabComplete;
import net.Lenni0451.SpigotPluginManager.utils.DownloadUtils;
import net.Lenni0451.SpigotPluginManager.utils.InstalledPluginsConfig;
import net.Lenni0451.SpigotPluginManager.utils.Logger;
import net.Lenni0451.SpigotPluginManager.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class PluginManager extends JavaPlugin {

    private static PluginManager instance;

    public static PluginManager getInstance() {
        return instance;
    }


    private final PluginUtils pluginUtils;
    private final InstalledPluginsConfig installedPluginsInfo;

    public PluginManager() {
        instance = this;

        this.saveDefaultConfig();

        this.pluginUtils = new PluginUtils();
        this.installedPluginsInfo = new InstalledPluginsConfig();
    }

    public PluginUtils getPluginUtils() {
        return this.pluginUtils;
    }

    public InstalledPluginsConfig getInstalledPlugins() {
        return this.installedPluginsInfo;
    }


    @Override
    public void onEnable() {
        this.getCommand("reload").setExecutor(new Reload_Command());
        this.getCommand("reload").setAliases(Collections.singletonList("rl"));

        this.getCommand("pluginmanager").setExecutor(new PluginManager_Command());
        this.getCommand("pluginmanager").setAliases(Collections.singletonList("pm"));
        this.getCommand("pluginmanager").setTabCompleter(new PluginManager_TabComplete());

        for (SoftDepends softDepend : SoftDepends.values()) {
            if (softDepend.isInstalled()) {
                Logger.sendPrefixMessage(Bukkit.getConsoleSender(), "§aThe soft depend §6" + softDepend.name() + " §ais installed and will be used.");
            }
        }
        if (this.getConfig().getBoolean("CheckForUpdates")) {
            this.checkUpdates();
        }
    }

    public void checkUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                String newestVersion = DownloadUtils.getNewestVersion();
                if (!newestVersion.equals(this.getDescription().getVersion())) {
                    Logger.sendPrefixMessage(Bukkit.getConsoleSender(), "A new update of §6PluginManager §ais available §e(" + this.getDescription().getVersion() + " -> " + newestVersion + ")§a.");
                    if (this.getConfig().getBoolean("AutoUpdate")) {
                        try {
                            DownloadUtils.downloadPlugin("https://github.com/Lenni0451/SpigotPluginManager/releases/latest/download/PluginManager.jar", this.getFile());
                            Logger.sendPrefixMessage(Bukkit.getConsoleSender(), "Successfully downloaded new §6PluginManager §aversion.");
                            Logger.sendPrefixMessage(Bukkit.getConsoleSender(), "PluginManager is reloading itself in some seconds...");
                            Bukkit.getScheduler().runTaskLater(this, () -> {
                                try {
                                    this.pluginUtils.unloadPlugin(this);
                                    this.pluginUtils.loadPlugin(this);
                                    Logger.sendPrefixMessage(Bukkit.getConsoleSender(), "PluginManager successfully reloaded itself!");
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }, 1);
                        } catch (Throwable e) {
                            Logger.sendPrefixMessage(Bukkit.getConsoleSender(), "§cCould not auto download the latest §6PluginManager §aversion.");
                            Logger.sendPrefixMessage(Bukkit.getConsoleSender(), "You can download it here: §6https://github.com/Lenni0451/SpigotPluginManager/releases/latest/download/PluginManager.jar");
                        }
                    } else {
                        Logger.sendPrefixMessage(Bukkit.getConsoleSender(), "You can download it here: §6https://github.com/Lenni0451/SpigotPluginManager/releases/latest/download/PluginManager.jar");
                    }
                } else {
                    Logger.sendPrefixMessage(Bukkit.getConsoleSender(), "You are using the latest version of §6PluginManager§a.");
                }
            } catch (Throwable e) {
                Logger.sendPrefixMessage(Bukkit.getConsoleSender(), "§cAn unknown error occurred whilst checking for a new §6PluginManager §cversion!.");
                e.printStackTrace();
            }
        });
    }

}
