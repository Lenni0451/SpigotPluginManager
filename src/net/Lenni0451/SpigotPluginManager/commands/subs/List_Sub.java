package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.utils.Logger;

public class List_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 0) {
			return false;
		}
		
		Plugin[] plugins = PluginManager.getInstance().getPluginUtils().getPlugins();
		Logger.sendPrefixMessage(sender, "§6Plugins §e§o(" + plugins.length + ")§6:");
		for(Plugin plugin : plugins) {
			sender.sendMessage(" §7- " + (plugin.isEnabled() ? "§a" : "§c") + plugin.getName() + " §6" + plugin.getDescription().getVersion());
		}
		
		return true;
	}

	@Override
	public void getTabComplete(List<String> tabs, String[] args) {}

	@Override
	public String getUsage() {
		return "list";
	}

}
