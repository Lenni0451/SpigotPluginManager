package net.lenni0451.spm.commands;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.utils.I18n;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reload_Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bukkit.command.reload")) {
            Logger.sendPermissionMessage(sender);
            return true;
        }
        if (PluginManager.getInstance().getConfig().getBoolean("OnlyConsole") && !Bukkit.getConsoleSender().equals(sender)) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.general.onlyConsole"));
            return true;
        }

        sender.sendMessage(I18n.t("pm.commands.reload.start"));

        List<Plugin> reloadPlugins = PluginManager.getInstance().getPluginUtils().getPluginsByLoadOrder();

        Collections.reverse(reloadPlugins);
        for (Plugin plugin : reloadPlugins) {
            try {
                PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin);
            } catch (Throwable t) {
                t.printStackTrace();
                sender.sendMessage(I18n.t("pm.commands.reload.unloadError", plugin.getName(), (t.getMessage() == null ? I18n.t("pm.general.checkConsole") : t.getMessage())));
                return true;
            }
        }
        Collections.reverse(reloadPlugins);
        List<String> pluginNames = new ArrayList<>();
        reloadPlugins.forEach((plugin) -> pluginNames.add(plugin.getName()));
        reloadPlugins.clear();

        for (String plugin : pluginNames) {
            try {
                PluginManager.getInstance().getPluginUtils().loadPlugin(plugin);
            } catch (Throwable t) {
                sender.sendMessage(I18n.t("pm.commands.reload.loadError", plugin, (t.getMessage() == null ? I18n.t("pm.general.checkConsole") : t.getMessage())));
            }
        }

        sender.sendMessage(I18n.t("pm.commands.reload.done"));

        return true;
    }

}
