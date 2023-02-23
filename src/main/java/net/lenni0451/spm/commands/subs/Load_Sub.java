package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.messages.I18n;
import net.lenni0451.spm.utils.FileUtils;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Load_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        if (args[0].equalsIgnoreCase("*") && PluginManager.getInstance().getConfig().getBoolean("AllowBatchActions")) {
            List<String> names = new ArrayList<>();
            for (File pluginFile : FileUtils.listFiles(PluginManager.getInstance().getPluginUtils().getPluginsDirectory())) {
                if (pluginFile.isFile() && pluginFile.isFile()) {
                    if (!pluginFile.getName().toLowerCase().endsWith(".jar") && PluginManager.getInstance().getConfig().getBoolean("IgnoreNonJarPlugins")) {
                        continue;
                    }
                    try {
                        PluginDescriptionFile desc = PluginManager.getInstance().getPluginLoader().getPluginDescription(pluginFile);
                        String name = desc.getName();
                        if (!PluginManager.getInstance().getPluginUtils().getPlugin(name).isPresent()) {
                            names.add(desc.getName());
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }

            for (String name : names) {
                try {
                    PluginManager.getInstance().getPluginUtils().loadPlugin(name);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.load.loadError", name, e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
                }
            }
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.load.batchSuccess", names.size()));
        } else {
            if (PluginManager.getInstance().getPluginUtils().isPluginLoaded(args[0])) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.load.alreadyLoaded"));
                return true;
            }
            try {
                PluginManager.getInstance().getPluginUtils().loadPlugin(args[0]);
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.load.success"));
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.load.loadError", args[0], e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
            }
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
    }

    @Override
    public String getUsage() {
        return "load <Plugin>" + this.getBatchActionSuffix();
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.load.help"));
    }

}
