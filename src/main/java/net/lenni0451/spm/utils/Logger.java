package net.Lenni0451.spm.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Logger {

    public static final String PREFIX = "§3PM §7| §a";

    /**
     * Send a message to a CommandSender
     *
     * @param receiver The receiver of the message
     * @param message  The message
     */
    public static void sendMessage(CommandSender receiver, String message) {
        receiver.sendMessage(message);
    }

    /**
     * Send a message with the PluginManager prefix to a command sender
     *
     * @param receiver The receiver of the message
     * @param message  The message
     */
    public static void sendPrefixMessage(CommandSender receiver, String message) {
        sendMessage(receiver, PREFIX + message);
    }

    /**
     * Send a CommandSender that he does not have enough permissions for a command
     *
     * @param receiver The receiver of the message
     */
    public static void sendPermissionMessage(CommandSender receiver) {
        sendPrefixMessage(receiver, "§cI'm sorry but you don't have access to this command.");
    }

    /**
     * Send a message into the console
     *
     * @param message The message
     */
    public static void sendConsole(String message) {
        sendPrefixMessage(Bukkit.getConsoleSender(), message);
    }

}
