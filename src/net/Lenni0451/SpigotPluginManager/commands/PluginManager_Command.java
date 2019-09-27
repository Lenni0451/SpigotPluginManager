package net.Lenni0451.SpigotPluginManager.commands;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.commands.subs.Commands_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Delete_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Disable_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Download_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Dump_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Enable_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Find_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Gui_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Info_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.List_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Load_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Permissions_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Reload_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Restart_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Unload_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.Update_Sub;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommandMultithread;
import net.Lenni0451.SpigotPluginManager.utils.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PluginManager_Command implements CommandExecutor {
	
	public static Map<String, ISubCommand> subCommands = new TreeMap<>();
	
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
		subCommands.put("find", new Find_Sub());
		subCommands.put("download", new Download_Sub());
		subCommands.put("gui", new Gui_Sub());
		subCommands.put("delete", new Delete_Sub());
		subCommands.put("permissions", new Permissions_Sub());
		subCommands.put("dump", new Dump_Sub());
		subCommands.put("update", new Update_Sub());
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("pluginmanager.commands")) {
			Logger.sendPermissionMessage(sender);
			return true;
		}
		
		if(args.length == 0) {
			sender.sendMessage("§6--------------------------------------------------");
			for(Map.Entry<String, ISubCommand> entry : subCommands.entrySet()) {
				if(PluginManager.getInstance().getConfig().getBoolean("HideNoPermissionCommands")) {
					if(!sender.hasPermission("pluginmanager.commands." + entry.getKey().toLowerCase())) {
						continue;
					}
				}
				for(String usage : entry.getValue().getUsage().split(Pattern.quote("\n"))) {
					String message = " §7- §6pm " + entry.getKey() + " §7| §2" + usage;
					if(sender instanceof Player) {
						TextComponent textComponent = new TextComponent(message);
						textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pm " + entry.getKey()));
						textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("§b/pm " + entry.getKey())}));
						((Player) sender).spigot().sendMessage(textComponent);
					} else {
						sender.sendMessage(message);
					}
				}
			}
			sender.sendMessage("§6--------------------------------------------------");
		} else if(args.length >= 1) {
			String cmd = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);
			
			ISubCommand subCommand = subCommands.get(cmd.toLowerCase());
			if(subCommand == null) {
				Logger.sendPrefixMessage(sender, "§cThe command could not be found.");
			} else {
				final String[] _args = args;
				Runnable executeRun = () -> {
					if(!sender.hasPermission("pluginmanager.commands." + cmd.toLowerCase())) {
						Logger.sendPermissionMessage(sender);
					} else if(!subCommand.execute(sender, _args)) {
						Logger.sendPrefixMessage(sender, "§cInvalid command usage!");
						for(String usage : subCommand.getUsage().split(Pattern.quote("\n"))) {
							Logger.sendPrefixMessage(sender, "§aUse: §6pm " + usage);
						}
					}
				};
				if(subCommand instanceof ISubCommandMultithread) {
					Bukkit.getScheduler().runTaskAsynchronously(PluginManager.getInstance(), executeRun);
				} else {
					executeRun.run();
				}
			}
		}
		
		return true;
	}
	
}
