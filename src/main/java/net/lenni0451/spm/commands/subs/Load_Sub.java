package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Load_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        if (args[0].equalsIgnoreCase("*") && PluginManager.getInstance().getConfig().getBoolean("AllowBatchActions")) {
            List<String> names = new ArrayList<>();
            for (File pluginFile : PluginManager.getInstance().getPluginUtils().getPluginsDirectory().listFiles()) {
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
                    Bukkit.getConsoleSender().sendMessage("§cCould not load plugin §6" + name + "§c." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
                }
            }
            Logger.sendPrefixMessage(sender, "§aLoaded all plugins §e(" + names.size() + ")§a.");
        } else {
            if (PluginManager.getInstance().getPluginUtils().isPluginLoaded(args[0])) {
                Logger.sendPrefixMessage(sender, "§cThe plugin is already loaded.");
                return true;
            }
            try {
                PluginManager.getInstance().getPluginUtils().loadPlugin(args[0]);
                Logger.sendPrefixMessage(sender, "§aThe plugin has been loaded.");
            } catch (Throwable e) {
                Logger.sendPrefixMessage(sender, "§cThe plugin could not be enabled." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
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
        lines.add("Load a plugin which is not yet loaded.");
        lines.add("You can enter the file name (if it does not contain spaces)");
        lines.add("or the name in the plugin.yml.");
    }

}
