package net.lenni0451.spm.commands.subs;

import com.google.gson.JsonObject;
import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommandMultithreaded;
import net.lenni0451.spm.softdepends.MVdWUpdater_Adapter;
import net.lenni0451.spm.softdepends.SoftDepends;
import net.lenni0451.spm.utils.DownloadUtils;
import net.lenni0451.spm.utils.Logger;
import net.lenni0451.spm.utils.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.util.List;

public class Download_Sub implements ISubCommandMultithreaded {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 3) return false;
        if (!args[0].equalsIgnoreCase("direct") && !args[0].equalsIgnoreCase("spigot")) return false;
        if (!sender.hasPermission("pluginmanager.commands.download." + args[0].toLowerCase())) {
            Logger.sendPermissionMessage(sender);
            return true;
        }

        args[2] = args[2].replace("/", "").replace("\\", "");

        String url = args[1];
        File file = new File(PluginManager.getInstance().getPluginUtils().getPluginsDirectory(), args[2] + (args[2].toLowerCase().endsWith(".jar") ? "" : ".jar"));
        if (args[0].equalsIgnoreCase("direct")) {
            try {
                DownloadUtils.downloadPlugin(url, file);
                Logger.sendPrefixMessage(sender, "Successfully downloaded the plugin and saved it as §6" + file.getName() + "§a.");
            } catch (Throwable e) {
                Logger.sendPrefixMessage(sender, "§cCould not download the plugin." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
            }
        } else if (args[0].equalsIgnoreCase("spigot")) {
            int id = -1;
            if (NumberUtils.isInteger(url)) {
                id = Integer.parseInt(url);
            } else {
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                String urlPart = url.substring(url.lastIndexOf("/") + 1);
                if (urlPart.contains(".")) {
                    urlPart = urlPart.substring(urlPart.lastIndexOf(".") + 1);
                    if (NumberUtils.isInteger(urlPart)) {
                        id = Integer.parseInt(urlPart);
                    }
                }
            }
            if (id <= 0) {
                Logger.sendPrefixMessage(sender, "§cCould not extract the plugin id from the url.");
                Logger.sendPrefixMessage(sender, "§aYou can try entering the id manually. You can find the id here:");
                Logger.sendPrefixMessage(sender, "§6https://www.spigotmc.org/resources/plugin-name.§aID§6/");
                return true;
            }

            try {
                JsonObject response = DownloadUtils.getSpigotMcPluginInfo(id);
                if (response == null) {
                    Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
                    return true;
                }
                if (response.has("external") && response.get("external").getAsBoolean()) {
                    Logger.sendPrefixMessage(sender, "§cThe plugin has an external download link and can not be downloaded automatically.");
                    return true;
                }
                if (response.has("file") && response.get("file").getAsJsonObject().has("type") && !response.get("file").getAsJsonObject().get("type").getAsString().equalsIgnoreCase(".jar")) {
                    Logger.sendPrefixMessage(sender, "§cThe plugin is not a jar file. File type: §6" + response.get("file").getAsJsonObject().get("type").getAsString());
                    return true;
                }
                if (response.has("premium") && response.get("premium").getAsBoolean()) {
                    if (SoftDepends.MVdWUpdater.isInstalled()) {
                        if (!MVdWUpdater_Adapter.hasResource(id)) {
                            Logger.sendPrefixMessage(sender, "§cIt seems like you don't have bought this plugin.");
                            Logger.sendPrefixMessage(sender, "§aThe price is §6" + response.get("price").getAsString() + response.get("currency").getAsString() + "§a.");
                            return true;
                        }
                    } else {
                        Logger.sendPrefixMessage(sender, "§cThe plugin is a premium resource and can not be downloaded automatically without §6MVdWUpdater§c.");
                        Logger.sendPrefixMessage(sender, "§aThe price is §6" + response.get("price").getAsString() + response.get("currency").getAsString() + "§a.");
                        return true;
                    }
                }

                boolean success;
                if (response.has("premium") && response.get("premium").getAsBoolean()) {
                    success = MVdWUpdater_Adapter.downloadPlugin(file, id);
                } else {
                    success = DownloadUtils.downloadSpigotMcPlugin(id, file);
                }
                if (success) {
                    Logger.sendPrefixMessage(sender, "Successfully downloaded the plugin and saved it as §6" + file.getName() + "§a.");
                    try {
                        PluginDescriptionFile desc = PluginManager.getInstance().getPluginLoader().getPluginDescription(file);
                        PluginManager.getInstance().getInstalledPlugins().setPlugin(desc.getName(), id, response.get("version").getAsJsonObject().get("id").getAsString(), file.getName());
                    } catch (Throwable e) {
                        Logger.sendPrefixMessage(sender, "§cCould not add plugin into config for later updates.");
                    }
                } else {
                    Logger.sendPrefixMessage(sender, "§cThe plugin could not be found or has no download.");
                }
            } catch (Exception e) {
                Logger.sendPrefixMessage(sender, "§cCould not reach the spiget api. Please try again later." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
            }
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
        if (args.length == 0) {
            tabs.add("spigot");
            tabs.add("direct");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("direct")) {
            String url = args[1];
            if (url.endsWith(".jar")) {
                tabs.add(url.substring(url.lastIndexOf("/") + 1));
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("spigot")) {
            String url = args[1];

            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            String urlPart = url.substring(url.lastIndexOf("/") + 1);
            if (urlPart.contains(".")) {
                urlPart = urlPart.substring(0, urlPart.lastIndexOf("."));
                tabs.add(urlPart + ".jar");
            }
        }
    }

    @Override
    public String getUsage() {
        return "download spigot <URL/ID> <File name>\ndownload direct <URL> <File name>";
    }

    @Override
    public void getHelp(List<String> lines) {
        lines.add("Download a plugin ingame from spigotmc or a direct link.");
        lines.add("If the plugin is downloaded from spigotmc the current");
        lines.add("version is getting saved so you can easily update it");
        lines.add("using '/pm update' in the future.");
        lines.add("For spigotmc downloads you can paste the url of the plugin");
        lines.add("so PluginManager extracts the id directly from the url");
        lines.add("but you can also just paste it there manually.");
        lines.add("The id can be found at the end of the url:");
        lines.add("https://www.spigotmc.org/resources/plugin-name._ID_/");
    }

}
