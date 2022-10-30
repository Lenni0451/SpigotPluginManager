package net.lenni0451.spm.commands.subs;

import com.google.gson.JsonObject;
import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommandMultithreaded;
import net.lenni0451.spm.messages.I18n;
import net.lenni0451.spm.utils.DownloadUtils;
import net.lenni0451.spm.utils.Logger;
import net.lenni0451.spm.utils.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Download_Sub implements ISubCommandMultithreaded {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 3) return false;
        if (!args[0].equalsIgnoreCase("direct") && !args[0].equalsIgnoreCase("spigot")) return false;
        if (!sender.hasPermission("pluginmanager.commands.download." + args[0].toLowerCase())) {
            Logger.sendPermissionMessage(sender);
            return true;
        }

        String filename = args[2];
        if(args.length > 3 && filename.startsWith("\"") && args[args.length-1].endsWith("\"")) {
        	for(int i = 3; i < args.length; i++) {
        		filename += args[i];
        	}
        }
        args[2] = args[2].replace("/", "").replace("\\", "");

        String url = args[1];
        File file = new File(PluginManager.getInstance().getPluginUtils().getPluginsDirectory(), args[2] + (args[2].toLowerCase().endsWith(".jar") ? "" : ".jar"));
        if (args[0].equalsIgnoreCase("direct")) {
            try {
                DownloadUtils.downloadPlugin(url, file);
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.download.success", file.getName()));
            } catch (Throwable e) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.download.downloadError", e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
            }
        } else if (args[0].equalsIgnoreCase("spigot")) {
            int id = -1;
            if (NumberUtils.isInteger(url)) {
                id = Integer.parseInt(url);
            } else {
                if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
                String urlPart = url.substring(url.lastIndexOf("/") + 1);
                if (urlPart.contains(".")) {
                    urlPart = urlPart.substring(urlPart.lastIndexOf(".") + 1);
                    if (NumberUtils.isInteger(urlPart)) id = Integer.parseInt(urlPart);
                }
            }
            if (id <= 0) {
                for (String s : I18n.mt("pm.subcommands.download.idExtractError")) Logger.sendPrefixMessage(sender, s);
                return true;
            }

            try {
                JsonObject response = DownloadUtils.getSpigotMcPluginInfo(id);
                if (response == null) {
                    Logger.sendPrefixMessage(sender, I18n.t("pm.general.pluginNotFound"));
                    return true;
                }
                if (response.has("external") && response.get("external").getAsBoolean()) {
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.download.externalLink"));
                    return true;
                }
                if (response.has("file") && response.get("file").getAsJsonObject().has("type") && !response.get("file").getAsJsonObject().get("type").getAsString().equalsIgnoreCase(".jar")) {
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.download.notJar", response.get("file").getAsJsonObject().get("type").getAsString()));
                    return true;
                }
                if (response.has("premium") && response.get("premium").getAsBoolean()) {
                    for (String s : I18n.mt("pm.subcommands.download.isPremium", response.get("price").getAsString() + response.get("currency").getAsString())) {
                        Logger.sendPrefixMessage(sender, s);
                    }
                    return true;
                }

                boolean success = DownloadUtils.downloadSpigotMcPlugin(id, file);
                if (success) {
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.download.success", file.getName()));
                    try {
                        PluginDescriptionFile desc = PluginManager.getInstance().getPluginLoader().getPluginDescription(file);
                        PluginManager.getInstance().getInstalledPlugins().setPlugin(desc.getName(), id, response.get("version").getAsJsonObject().get("id").getAsString(), file.getName());
                    } catch (Throwable e) {
                        Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.download.updateConfigError"));
                    }
                } else {
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.download.noDownload"));
                }
            } catch (Exception e) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.download.spigetError", e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("direct") || args[0].equalsIgnoreCase("spigot"))) {
	        try {
	            for (File file : PluginManager.getInstance().getPluginUtils().getPluginsDirectory().listFiles()) {
	            	if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
	            		String filename= file.getName();
	            		if (filename.contains(" ")) {
	            			filename = "\"" + filename + "\"";
	            		}
	            		tabs.add(filename);
	            	}
	            }
	        } catch(Throwable ignored) {
	        }
        }
    }

    @Override
    public String getUsage() {
        return "download spigot <URL/ID> <File name>\ndownload direct <URL> <File name>";
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.download.help"));
    }

}
