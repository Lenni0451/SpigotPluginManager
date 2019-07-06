package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.utils.Logger;

public class Find_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		List<Plugin> plugins = new ArrayList<>();
		for(Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) {
			List<String> commands = PluginManager.getInstance().getPluginUtils().getCommands(plugin);
			if(commands.contains(args[0]) || commands.contains(" " + args[0])) {
				plugins.add(plugin);
			}
		}
		if(plugins.isEmpty()) {
			Logger.sendPrefixMessage(sender, "§cThere is no plugin which has this command registered.");
		} else {
			Logger.sendPrefixMessage(sender, "§6The plugins with this command:");
			for(Plugin plugin : plugins) {
				sender.sendMessage(" §7- §6" + plugin.getName());
			}
		}
		
		return true;
	}

	@Override
	public void getTabComplete(List<String> tabs, String[] args) {}

	@Override
	public String getUsage() {
		return "find <Command>";
	}

}
