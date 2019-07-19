package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;
import net.Lenni0451.SpigotPluginManager.utils.Logger;

public class Enable_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		if(args[0].equalsIgnoreCase("*")) {
			List<Plugin> plugins = PluginManager.getInstance().getPluginUtils().getPluginsByLoadOrder();
			List<String> ignoredPlugins = PluginManager.getInstance().getConfig().getStringList("IgnoredPlugins");
			
			for(Plugin plugin : plugins) {
				if(ignoredPlugins.contains(plugin.getName())) {
					continue;
				}
				
				try {
					PluginManager.getInstance().getPluginUtils().enablePlugin(plugin);
				} catch (Throwable e) {
					Logger.sendPrefixMessage(sender, "�cCould not enable the plugin �6" + plugin.getName() + "�c.");
				}
			}
			Logger.sendPrefixMessage(sender, "�aEnabled all plugins �e(" + plugins.size() + ")�a.");
		} else {
			try {
				Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
				
				try {
					if(PluginManager.getInstance().getPluginUtils().enablePlugin(plugin)) {
						Logger.sendPrefixMessage(sender, "�aThe plugin �6" + plugin.getName() + " �ahas been enabled.");
					} else {
						Logger.sendPrefixMessage(sender, "�cThe plugin �6" + plugin.getName() + " �cis already enabled.");
					}
				} catch (Throwable e) {
					Logger.sendPrefixMessage(sender, "�cThe plugin �6" + plugin.getName() + " �ccould not be enabled.");
				}
			} catch (Throwable e) {
				Logger.sendPrefixMessage(sender, "�cThe plugin could not be found.");
			}
		}
		
		return true;
	}

	@Override
	public void getTabComplete(List<String> tabs, String[] args) {
		if(args.length == 0) {
			for(Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) {
				if(!plugin.isEnabled()) tabs.add(plugin.getName());
			}
		}
	}

	@Override
	public String getUsage() {
		return "enable <Plugin>/*";
	}
	
}
