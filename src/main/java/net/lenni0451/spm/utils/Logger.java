package net.lenni0451.spm.utils;

import net.lenni0451.spm.messages.I18n;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Logger {

    public static final String PREFIX = "§3PM §7| §a";

    /**
     * Send a message to a {@link CommandSender}
     *
     * @param receiver The receiver of the message
     * @param message  The message
     */
    public static void sendMessage(final CommandSender receiver, final String message) {
        receiver.sendMessage(message);
    }

    /**
     * Send a message to a {@link CommandSender} with the PM prefix
     *
     * @param receiver The receiver of the message
     * @param message  The message
     */
    public static void sendPrefixMessage(final CommandSender receiver, final String message) {
        sendMessage(receiver, PREFIX + message);
    }

    /**
     * Send a message to a {@link CommandSender} that he has no permission
     *
     * @param receiver The receiver of the message
     */
    public static void sendPermissionMessage(final CommandSender receiver) {
        sendPrefixMessage(receiver, I18n.t("pm.general.noPermission"));
    }

    /**
     * Send a message into the console
     *
     * @param message The message
     */
    public static void sendConsole(final String message) {
        sendPrefixMessage(Bukkit.getConsoleSender(), message);
    }

}
