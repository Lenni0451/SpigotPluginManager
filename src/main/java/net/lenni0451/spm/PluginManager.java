package net.lenni0451.spm;

import com.tchristofferson.configupdater.ConfigUpdater;
import net.lenni0451.spm.commands.PluginManager_Command;
import net.lenni0451.spm.commands.Reload_Command;
import net.lenni0451.spm.messages.I18n;
import net.lenni0451.spm.softdepends.SoftDepends;
import net.lenni0451.spm.tabcomplete.PluginManager_TabComplete;
import net.lenni0451.spm.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
        try {
            ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder(), "config.yml"), new ArrayList<>());
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update config to the latest version", t);
        }
        I18n.init();

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
                Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.softdepend.found", softDepend.name()));
            }
        }
        if (this.getConfig().getBoolean("CheckForUpdates")) {
            this.checkUpdates();
        }
        if (I18n.wasUpdated()) {
            Bukkit.getScheduler().runTask(PluginManager.getInstance(), () -> {
                for (String translationLine : I18n.mt("pm.updater.missingTranslations")) {
                    Logger.sendConsole("§a" + translationLine);
                }
            });
        }
    }

    public void checkUpdates() {
        final String downloadURL = "https://github.com/Lenni0451/SpigotPluginManager/releases/latest/download/PluginManager.jar";
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                final String newestVersion = DownloadUtils.getNewestVersion();
                if (!newestVersion.equals(this.getDescription().getVersion())) {
                    Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.updater.found", this.getDescription().getVersion(), newestVersion));
                    if (this.getConfig().getBoolean("AutoUpdate")) {
                        try {
                            { //Load all classes needed for the PluginUtils here because as soon as we overwrite the plugin jar we are no longer able to load classes
                                Class.forName(ThreadUtils.class.getName());
                                Class.forName(ReflectionUtils.class.getName());
                                Class.forName(FileOutputStream.class.getName());
                            }

                            final File pluginFile = this.getFile();
                            final byte[] newData = DownloadUtils.download(downloadURL);
                            Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.updater.downloadSuccess"));
                            Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.updater.checkChangelog", "https://github.com/Lenni0451/SpigotPluginManager/releases/tag/" + newestVersion));
                            Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.updater.selfReload"));
                            Bukkit.getScheduler().runTaskLater(this, () -> {
                                try {
                                    this.pluginUtils.unloadPlugin(this);
                                    //Here the ClassLoader is already closed. There is no going back now
                                    {
                                        FileOutputStream fos = new FileOutputStream(pluginFile);
                                        fos.write(newData);
                                        fos.close();
                                    }
                                    this.pluginUtils.loadPlugin(this);
                                    Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.updater.reloadSuccess"));
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }, 1);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.updater.downloadFail"));
                            Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.updater.downloadHere", downloadURL));
                        }
                    } else {
                        Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.updater.downloadHere", downloadURL));
                    }
                } else {
                    Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.updater.latestVersion"));
                }
            } catch (Throwable e) {
                Logger.sendPrefixMessage(Bukkit.getConsoleSender(), I18n.t("pm.updater.error"));
                e.printStackTrace();
            }
        });
    }

}
