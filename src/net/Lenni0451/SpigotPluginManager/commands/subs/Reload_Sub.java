package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.utils.Logger;

public class Reload_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		if(args[0].equalsIgnoreCase("*")) {
			Bukkit.dispatchCommand(sender, "pluginmanager:reload");
		} else {
			try {
				Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);

				try {
					PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin);
				} catch (Throwable e) {
					Logger.sendPrefixMessage(sender, "§cCould not unload the plugin §6" + plugin.getName() + "§c.");
					return true;
				}
				try {
					PluginManager.getInstance().getPluginUtils().loadPlugin(plugin);
				} catch (Throwable e) {
					Logger.sendPrefixMessage(sender, "§cCould not load the plugin §6" + plugin.getName() + "§c.");
					return true;
				}
				Logger.sendPrefixMessage(sender, "§aThe plugin §6" + plugin.getName() + " §ahas been reloaded.");
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
				tabs.add(plugin.getName());
			}
		}
	}

	@Override
	public String getUsage() {
		return "reload <Plugin>/*";
	}

}
