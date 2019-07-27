package net.Lenni0451.SpigotPluginManager.utils;

import org.bukkit.command.CommandSender;

public class Logger {
	
	public static final String PREFIX = "§3PM §7| §a";
	
	/**
	 * Send a message to a CommandSender
	 * 
	 * @param receiver
	 * @param message
	 */
	public static void sendMessage(CommandSender receiver, String message) {
		receiver.sendMessage(message);
	}
	
	/**
	 * Send a message with the PluginManager prefix to a command sender
	 * 
	 * @param receiver
	 * @param message
	 */
	public static void sendPrefixMessage(CommandSender receiver, String message) {
		sendMessage(receiver, PREFIX + message);
	}
	
	/**
	 * Send a CommandSender that he does not have enough permissions for a command
	 * 
	 * @param receiver
	 */
	public static void sendPermissionMessage(CommandSender receiver) {
		sendPrefixMessage(receiver, "§cI'm sorry but you don't have access to this command.");
	}
	
}
