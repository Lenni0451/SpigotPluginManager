package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.utils.Logger;

public class Commands_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		try {
			Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
			List<String> commands = PluginManager.getInstance().getPluginUtils().getCommands(plugin);
			if(commands.isEmpty()) {
				Logger.sendPrefixMessage(sender, "§cThe plugin §6" + plugin.getName() + " §chas no commands registered.");
			} else {
				Logger.sendPrefixMessage(sender, "§6Commands of §a" + plugin.getName() + "§6:");
				for(String command : commands) {
					if(command.startsWith(" ")) {
						sender.sendMessage("  §7- §6" + command.substring(1));
					} else {
						sender.sendMessage(" §7- §6" + command);
					}
				}
			}
		} catch (Throwable e) {
			Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
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
		return "commands <Plugin>";
	}

}
