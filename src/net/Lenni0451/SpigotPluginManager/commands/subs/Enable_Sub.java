package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;

public class Enable_Sub implements ISubCommand {

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
					PluginManager.getInstance().getPluginUtils().enablePlugin(plugin);
				} catch (Throwable e) {
					sender.sendMessage("§cCould not enable the plugin §6" + plugin.getName() + "§c.");
				}
			}
			sender.sendMessage("§aEnabled all plugins §e(" + plugins.size() + ")§a.");
		} else {
			try {
				Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
				
				try {
					if(PluginManager.getInstance().getPluginUtils().disablePlugin(plugin)) {
						sender.sendMessage("§aThe plugin §6" + plugin.getName() + " §ahas been enabled.");
					} else {
						sender.sendMessage("§cThe plugin §6" + plugin.getName() + " §cis already enabled.");
					}
				} catch (Throwable e) {
					sender.sendMessage("§cThe plugin §6" + plugin.getName() + " §ccould not be enabled.");
				}
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
		return "enable <Plugin>/*";
	}
	
}
