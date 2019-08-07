package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;
import net.Lenni0451.SpigotPluginManager.utils.Logger;

public class Restart_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		if(args[0].equalsIgnoreCase("*")) {
			List<Plugin> plugins = PluginManager.getInstance().getPluginUtils().getPluginsByLoadOrder();
			Collections.reverse(plugins);
			List<String> ignoredPlugins = PluginManager.getInstance().getConfig().getStringList("IgnoredPlugins");
			
			for(Plugin plugin : plugins) {
				if(ignoredPlugins.contains(plugin.getName())) {
					continue;
				}
				
				try {
					PluginManager.getInstance().getPluginUtils().disablePlugin(plugin);
				} catch (Throwable e) {
					Logger.sendPrefixMessage(sender, "§cThe plugin §6" + plugin.getName() + " §acould not be disabled.");
				}
			}
			
			Collections.reverse(plugins);
			
			for(Plugin plugin : plugins) {
				if(plugin.equals(PluginManager.getInstance())) {
					continue;
				}
				
				try {
					PluginManager.getInstance().getPluginUtils().enablePlugin(plugin);
				} catch (Throwable e) {
					Logger.sendPrefixMessage(sender, "§cThe plugin §6" + plugin.getName() + " §acould not be enabled.");
				}
			}
			
			Logger.sendPrefixMessage(sender, "§aRestarted all plugins §e(" + plugins.size() + ")§a.");
		} else {
			try {
				Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
				
				try {
					PluginManager.getInstance().getPluginUtils().disablePlugin(plugin);
				} catch (Throwable e) {
					Logger.sendPrefixMessage(sender, "§cThe plugin §6" + plugin.getName() + " §acould not be disabled.");
					return true;
				}
				try {
					PluginManager.getInstance().getPluginUtils().enablePlugin(plugin);
				} catch (Throwable e) {
					Logger.sendPrefixMessage(sender, "§cThe plugin §6" + plugin.getName() + " §acould not be enabled.");
					return true;
				}
				Logger.sendPrefixMessage(sender, "§aThe plugin §6" + plugin.getName() + " §ahas been restarted.");
			} catch (Throwable e) {
				Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
			}
		}
		
		return true;
	}

	@Override
	public void getTabComplete(List<String> tabs, String[] args) {
		if(args.length == 0) {
			for(Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) {
				if(plugin.isEnabled()) tabs.add(plugin.getName());
			}
		}
	}

	@Override
	public String getUsage() {
		return "restart <Plugin>/*";
	}
	
}
