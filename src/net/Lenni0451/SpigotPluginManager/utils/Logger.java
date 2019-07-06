package net.Lenni0451.SpigotPluginManager.utils;

import org.bukkit.command.CommandSender;

public class Logger {
	
	public static final String PREFIX = "§3PM §7| §a";
	
	public static void sendMessage(CommandSender receiver, String message) {
		receiver.sendMessage(message);
	}
	
	public static void sendPrefixMessage(CommandSender receiver, String message) {
		sendMessage(receiver, PREFIX + message);
	}
	
}
