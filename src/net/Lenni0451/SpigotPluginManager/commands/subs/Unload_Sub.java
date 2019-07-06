package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;

public class Unload_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		if(args[0].equalsIgnoreCase("*")) {
			List<Plugin> plugins = PluginManager.getInstance().getPluginUtils().getPluginsByLoadOrder();
			
			for(Plugin plugin : plugins) {
				if(plugin.equals(PluginManager.getInstance())) {
					continue;
				}
				
				try {
					PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin);
				} catch (Throwable e) {
					sender.sendMessage("§cCould not unload the plugin §6" + plugin.getName() + "§c.");
				}
			}
			
			sender.sendMessage("§aUnloaded all plugins §e(" + plugins.size() + ")§a.");
		} else {
			try {
				PluginManager.getInstance().getPluginUtils().unloadPlugin(args[0]);
				sender.sendMessage("§aThe plugin has been unloaded.");
			} catch (IllegalArgumentException e) {
				sender.sendMessage("§cThe plugin could not be loaded.");
			} catch (Throwable e) {
				sender.sendMessage("§cThe plugin could not be found.");
			}
		}
		
		return true;
	}

	@Override
	public void getTabComplete(List<String> tabs, String[] args) {
		if(args.length == 0) {
			for(Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) {
				tabs.add(plugin.getName());
			}
		}
	}

	@Override
	public String getUsage() {
		return "unload <Plugin>/*";
	}
	
}
