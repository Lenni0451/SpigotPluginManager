package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;
import net.Lenni0451.SpigotPluginManager.utils.Logger;

public class Load_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		if(args[0].equalsIgnoreCase("*")) {
			List<String> names = new ArrayList<>();
			for(File pluginFile : PluginManager.getInstance().getPluginUtils().getPluginDir().listFiles()) {
				if(pluginFile.isFile() && pluginFile.isFile()) {
					if(!pluginFile.getName().toLowerCase().endsWith(".jar") && PluginManager.getInstance().getConfig().getBoolean("IgnoreNonJarPlugins")) {
						continue;
					}
					try {
		                PluginDescriptionFile desc = PluginManager.getInstance().getPluginLoader().getPluginDescription(pluginFile);
		                String name = desc.getName();
		                try {
		                	PluginManager.getInstance().getPluginUtils().getPlugin(name);
						} catch (Throwable e) {
			                names.add(desc.getName());
						}
		            } catch (Throwable e) {}
				}
			}
			
			for(String name : names) {
				try {
					PluginManager.getInstance().getPluginUtils().loadPlugin(name);
				} catch (Throwable e) {
					Bukkit.getConsoleSender().sendMessage("§cCould not load plugin §6" + name + "§c.");
				}
			}
			Logger.sendPrefixMessage(sender, "§aLoaded all plugins §e(" + names.size() + ")§a.");
		} else {
			try {
				try {
					Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
					Logger.sendPrefixMessage(sender, "§cThe plugin §6" + plugin.getName() + " §cis already loaded.");
					return true;
				} catch (Throwable e) {}
				PluginManager.getInstance().getPluginUtils().loadPlugin(args[0]);
				Logger.sendPrefixMessage(sender, "§aThe plugin has been loaded.");
			} catch (IllegalStateException e) {
				Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
			} catch (Throwable e) {
				Logger.sendPrefixMessage(sender, "§cThe plugin could not be enabled.");
			}
		}
		
		return true;
	}

	@Override
	public void getTabComplete(List<String> tabs, String[] args) {}

	@Override
	public String getUsage() {
		return "load <Plugin>/*";
	}
	
}
