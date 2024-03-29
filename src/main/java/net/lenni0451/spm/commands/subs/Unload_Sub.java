package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.messages.I18n;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class Unload_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        if (args[0].equalsIgnoreCase("*") && PluginManager.getInstance().getConfig().getBoolean("AllowBatchActions")) {
            List<Plugin> plugins = PluginManager.getInstance().getPluginUtils().getPluginsByLoadOrder();
            List<String> ignoredPlugins = PluginManager.getInstance().getConfig().getStringList("IgnoredPlugins");

            for (Plugin plugin : plugins) {
                if (ignoredPlugins.contains(plugin.getName())) continue;

                try {
                    PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin);
                } catch (Throwable e) {
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.unload.unloadError", plugin.getName(), e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
                }
            }

            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.unload.batchSuccess", plugins.size()));
        } else {
            if (!PluginManager.getInstance().getPluginUtils().getPlugin(args[0]).isPresent()) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.general.pluginNotFound"));
                return true;
            }
            try {
                PluginManager.getInstance().getPluginUtils().unloadPlugin(args[0]);
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.unload.success"));
            } catch (Throwable e) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.unload.unloadError", args[0], e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
            }
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
        if (args.length == 0) {
            for (Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) tabs.add(plugin.getName());
        }
    }

    @Override
    public String getUsage() {
        return "unload <Plugin>" + this.getBatchActionSuffix();
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.unload.help"));
    }

}
