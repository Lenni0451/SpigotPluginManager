package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;

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
				sender.sendMessage("§cThe plugin §6" + plugin.getName() + " §chas no commands registered.");
			} else {
				sender.sendMessage("§6Commands of §a" + plugin.getName() + "§6:");
				commands.forEach((command) -> sender.sendMessage(" §7- §6" + command));
			}
		} catch (Throwable e) {
			sender.sendMessage("§cThe plugin could not be found.");
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
