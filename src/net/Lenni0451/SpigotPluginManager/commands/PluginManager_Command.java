package net.Lenni0451.SpigotPluginManager.commands;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.Lenni0451.SpigotPluginManager.commands.subs.Commands_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Disable_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Enable_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.ISubCommand;
import net.Lenni0451.SpigotPluginManager.commands.subs.Info_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.List_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Load_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Reload_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Restart_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Unload_Sub;

public class PluginManager_Command implements CommandExecutor {
	
	public static Map<String, ISubCommand> subCommands = new LinkedHashMap<>();
	
	static {
		subCommands.put("list", new List_Sub());
		subCommands.put("info", new Info_Sub());
		subCommands.put("enable", new Enable_Sub());
		subCommands.put("disable", new Disable_Sub());
		subCommands.put("restart", new Restart_Sub());
		subCommands.put("load", new Load_Sub());
		subCommands.put("unload", new Unload_Sub());
		subCommands.put("reload", new Reload_Sub());
		subCommands.put("commands", new Commands_Sub());
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("pluginmanager.commands")) {
			sender.sendMessage("§cYou are not allowed to execute this command.");
			return true;
		}
		
		if(args.length == 0) {
			sender.sendMessage("§6--------------------------------------------------");
			for(Map.Entry<String, ISubCommand> entry : subCommands.entrySet()) {
				sender.sendMessage(" §7- §6pm " + entry.getKey() + " §7| §2" + entry.getValue().getUsage());
			}
			sender.sendMessage("§6--------------------------------------------------");
		} else if(args.length >= 1) {
			String cmd = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);
			
			ISubCommand subCommand = subCommands.get(cmd.toLowerCase());
			if(subCommand == null) {
				sender.sendMessage("§cThe command could not be found.");
			} else {
				if(!sender.hasPermission("pluginmanager.commands." + cmd.toLowerCase())) {
					sender.sendMessage("§cYou are not allowed to execute this command.");
				} else if(!subCommand.execute(sender, args)) {
					sender.sendMessage("§cInvalid command usage!");
					sender.sendMessage("§aUse: §6pm " + subCommand.getUsage());
				}
			}
		}
		
		return true;
	}
	
}
