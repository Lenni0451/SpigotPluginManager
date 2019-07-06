package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;

public class Restart_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		if(args[0].equalsIgnoreCase("*")) {
			List<Plugin> plugins = PluginManager.getInstance().getPluginUtils().getPluginsByLoadOrder();
			Collections.reverse(plugins);
			
			for(Plugin plugin : plugins) {
				if(plugin.equals(PluginManager.getInstance())) {
					continue;
				}
				
				try {
					PluginManager.getInstance().getPluginUtils().disablePlugin(plugin);
				} catch (Throwable e) {
					sender.sendMessage("§cThe plugin §6" + plugin.getName() + " §acould not be disabled.");
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
					sender.sendMessage("§cThe plugin §6" + plugin.getName() + " §acould not be enabled.");
				}
			}
			
			sender.sendMessage("§aRestarted all plugins §e(" + plugins.size() + ")§a.");
		} else {
			try {
				Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
				
				try {
					PluginManager.getInstance().getPluginUtils().disablePlugin(plugin);
				} catch (Throwable e) {
					sender.sendMessage("§cThe plugin §6" + plugin.getName() + " §acould not be disabled.");
					return true;
				}
				try {
					PluginManager.getInstance().getPluginUtils().enablePlugin(plugin);
				} catch (Throwable e) {
					sender.sendMessage("§cThe plugin §6" + plugin.getName() + " §acould not be enabled.");
					return true;
				}
				sender.sendMessage("§aThe plugin §6" + plugin.getName() + " §ahas been restarted.");
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
		return "restart <Plugin>/*";
	}
	
}
