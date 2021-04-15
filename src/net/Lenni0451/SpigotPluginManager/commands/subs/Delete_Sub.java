package net.Lenni0451.SpigotPluginManager.commands.subs;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;
import net.Lenni0451.SpigotPluginManager.utils.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class Delete_Sub implements ISubCommand {

    public Delete_Sub() {
        try { //Delete all safely which could not be directly delete before
            for (File file : PluginManager.getInstance().getPluginUtils().getPluginsDirectory().listFiles()) {
                if (file.getName().toLowerCase().endsWith(".jar") && file.length() == 0) {
                    FileUtils.deleteQuietly(file);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
        if (!plugin.isPresent()) {
            Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
            return true;
        }
        try {
            PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin.get());
        } catch (Throwable e) {
            Logger.sendPrefixMessage(sender, "§cThe plugin could not be unloaded." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
            return true;
        }

        Optional<File> file = PluginManager.getInstance().getPluginUtils().getPluginFile(plugin.get());
        if (!file.isPresent()) {
            Logger.sendPrefixMessage(sender, "§cThe file of the plugin could not be found.");
            return true;
        }
        try {
            PluginManager.getInstance().getInstalledPlugins().removePlugin(plugin.get().getName());
            FileUtils.writeByteArrayToFile(file.get(), new byte[0]);
            FileUtils.forceDelete(file.get());
        } catch (Throwable ignored) {
        }
        if (!file.get().exists()) {
            Logger.sendPrefixMessage(sender, "§aThe plugin has been deleted.");
        } else {
            Logger.sendPrefixMessage(sender, "§cThe plugin could not be deleted.");
            Logger.sendPrefixMessage(sender, "§aIt will get deleted on the next restart.");
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
        if (args.length == 0) {
            for (Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) {
                tabs.add(plugin.getName());
            }
        }
    }

    @Override
    public String getUsage() {
        return "delete <Plugin>";
    }

}
