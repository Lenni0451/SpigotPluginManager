package net.Lenni0451.SpigotPluginManager.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;

public class Reload_Command implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("bukkit.command.reload")) {
			sender.sendMessage("§cYou are not allowed to execute this command.");
			return true;
		}
		
		sender.sendMessage("§aReloading all plugins...");
		
		List<Plugin> reloadPlugins = PluginManager.getInstance().getPluginUtils().getPluginsByLoadOrder();
		
		Collections.reverse(reloadPlugins);
		for(Plugin plugin : reloadPlugins) {
			PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin);
		}
		Collections.reverse(reloadPlugins);
		List<String> pluginNames = new ArrayList<>();
		reloadPlugins.forEach((plugin) -> pluginNames.add(plugin.getName()));
		reloadPlugins.clear();
		
		for(String plugin : pluginNames) {
			try {
				PluginManager.getInstance().getPluginUtils().getPlugin(plugin);
				Bukkit.getConsoleSender().sendMessage("§cThe plugin §6" + plugin + " §cis already loaded!");
				continue;
			} catch (Throwable e) {}
			try {
				PluginManager.getInstance().getPluginUtils().loadPlugin(plugin);
			} catch (Throwable e) {
				Bukkit.getConsoleSender().sendMessage("§cCould not load plugin §6" + plugin + "§c.");
			}
		}
		
		sender.sendMessage("§aThe plugins have been reloaded.");
		
		return true;
	}

}
