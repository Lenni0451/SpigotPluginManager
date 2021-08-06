package net.lenni0451.spm.utils;

import com.google.common.collect.Lists;
import net.lenni0451.spm.PluginManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InstalledPluginsConfig {

    private final File file;
    private final YamlConfiguration installedPluginsFile;

    public InstalledPluginsConfig() {
        this.file = new File(PluginManager.getInstance().getDataFolder(), "installed.yml");
        this.file.getParentFile().mkdirs();
        try {
            this.file.createNewFile();
        } catch (Exception e) {
            new Exception(I18n.t("pm.installedpluginsconfig.createError"), e).printStackTrace();
        }

        this.installedPluginsFile = new YamlConfiguration();
        this.load();
    }

    public void load() {
        try {
            this.installedPluginsFile.load(this.file);

            List<PluginInfo> installedPlugins = this.getInstalledPlugins();
            Iterator<PluginInfo> iterator = installedPlugins.iterator();
            while (iterator.hasNext()) {
                PluginInfo info;
                if (!new File("plugins", (info = iterator.next()).getFileName()).exists()) {
                    iterator.remove();

                    Logger.sendConsole("§cThe plugin §6" + info.getName() + " §chas been removed from the config because the file could not be found anymore.");
                }
            }
            this.setInstalledPlugins(installedPlugins);
        } catch (Exception e) {
            new Exception(I18n.t("pm.installedpluginsconfig.loadError"), e).printStackTrace();
        }
    }

    public void save() {
        try {
            this.installedPluginsFile.save(this.file);
        } catch (Exception e) {
            new Exception(I18n.t("pm.installedpluginsconfig.saveError"), e).printStackTrace();
        }
    }


    public List<PluginInfo> getInstalledPlugins() {
        List<PluginInfo> installedPluginsInfos = Lists.newArrayList();
        List<String> installedPlugins = (List<String>) this.installedPluginsFile.getList("PluginNames");
        if (installedPlugins == null) {
            installedPlugins = Lists.newArrayList();
        }

        for (String installedPlugin : installedPlugins) {
            if (this.installedPluginsFile.contains("Plugins." + installedPlugin)) {
                String key = "Plugins." + installedPlugin;
                if (this.installedPluginsFile.contains(key + ".Id") && this.installedPluginsFile.contains(key + ".InstalledVersion")) {
                    final int id = this.installedPluginsFile.getInt(key + ".Id");
                    final String installedVersion = this.installedPluginsFile.getString(key + ".InstalledVersion");
                    final String fileName = this.installedPluginsFile.getString(key + ".FileName");
                    installedPluginsInfos.add(new PluginInfo(installedPlugin, id, installedVersion, fileName));
                }
            }
        }

        return installedPluginsInfos;
    }

    public void setInstalledPlugins(final List<PluginInfo> plugins) {
        List<String> installedPlugins = Lists.newArrayList();
        this.installedPluginsFile.set("Plugins", new HashMap<>());
        for (PluginInfo info : plugins) {
            this.installedPluginsFile.set("Plugins." + info.getName() + ".Id", info.getId());
            this.installedPluginsFile.set("Plugins." + info.getName() + ".InstalledVersion", info.getInstalledVersion());
            this.installedPluginsFile.set("Plugins." + info.getName() + ".FileName", info.getFileName());

            installedPlugins.add(info.getName());
        }
        this.installedPluginsFile.set("PluginNames", installedPlugins);
        this.save();
    }

    public void setPlugin(final String name, final Integer id, final String currentVersion, final String fileName) {
        List<PluginInfo> installedPlugins = this.getInstalledPlugins();
        installedPlugins.removeIf(pluginInfo -> pluginInfo.getName().equalsIgnoreCase(name));
        installedPlugins.add(new PluginInfo(name, id, currentVersion, fileName));
        this.setInstalledPlugins(installedPlugins);
    }

    public boolean removePlugin(final String name) {
        List<PluginInfo> installedPlugins = this.getInstalledPlugins();
        boolean removed = false;
        {
            Iterator<PluginInfo> iterator = installedPlugins.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getName().equalsIgnoreCase(name)) {
                    iterator.remove();
                    removed = true;
                }
            }
        }

        return removed;
    }

    public PluginInfo getPluginInfo(final String name) {
        for (PluginInfo info : this.getInstalledPlugins()) {
            if (info.getName().equalsIgnoreCase(name)) {
                return info;
            }
        }
        return null;
    }

}
