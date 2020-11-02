package net.Lenni0451.SpigotPluginManager.commands;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.utils.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reload_Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bukkit.command.reload")) {
            Logger.sendPermissionMessage(sender);
            return true;
        }

        sender.sendMessage("§aReloading all plugins...");

        List<Plugin> reloadPlugins = PluginManager.getInstance().getPluginUtils().getPluginsByLoadOrder();

        Collections.reverse(reloadPlugins);
        for (Plugin plugin : reloadPlugins) {
            try {
                PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin);
            } catch (Throwable t) {
                t.printStackTrace();
                sender.sendMessage("§cCould not unload plugin §6" + plugin.getName() + "§c." + (t.getMessage() != null ? (" §7(" + t.getMessage() + ")") : ""));
                return true;
            }
        }
        Collections.reverse(reloadPlugins);
        List<String> pluginNames = new ArrayList<>();
        reloadPlugins.forEach((plugin) -> pluginNames.add(plugin.getName()));
        reloadPlugins.clear();

        for (String plugin : pluginNames) {
            try {
                PluginManager.getInstance().getPluginUtils().loadPlugin(plugin);
            } catch (Throwable e) {
                sender.sendMessage("§cCould not load plugin §6" + plugin + "§c." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
            }
        }

        sender.sendMessage("§aAll plugins have been reloaded.");

        return true;
    }

}
