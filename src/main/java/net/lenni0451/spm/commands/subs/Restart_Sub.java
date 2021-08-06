package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.utils.I18n;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Restart_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        if (args[0].equalsIgnoreCase("*") && PluginManager.getInstance().getConfig().getBoolean("AllowBatchActions")) {
            List<Plugin> plugins = PluginManager.getInstance().getPluginUtils().getPluginsByLoadOrder();
            Collections.reverse(plugins);
            List<String> ignoredPlugins = PluginManager.getInstance().getConfig().getStringList("IgnoredPlugins");

            for (Plugin plugin : plugins) {
                if (ignoredPlugins.contains(plugin.getName())) continue;

                try {
                    PluginManager.getInstance().getPluginUtils().disablePlugin(plugin);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.restart.disableError", plugin.getName(), e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
                }
            }

            Collections.reverse(plugins);

            for (Plugin plugin : plugins) {
                if (plugin.equals(PluginManager.getInstance())) {
                    continue;
                }

                try {
                    PluginManager.getInstance().getPluginUtils().enablePlugin(plugin);
                } catch (Throwable e) {
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.restart.enableError", plugin.getName(), e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
                }
            }

            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.restart.batchSuccess", plugins.size()));
        } else {
            Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
            if (!plugin.isPresent()) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.general.pluginNotFound"));
                return true;
            }

            try {
                PluginManager.getInstance().getPluginUtils().disablePlugin(plugin.get());
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.restart.disableError", plugin.get().getName(), e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
                return true;
            }
            try {
                PluginManager.getInstance().getPluginUtils().enablePlugin(plugin.get());
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.restart.enableError", plugin.get().getName(), e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
                return true;
            }
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.restart.success"));
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
        if (args.length == 0) {
            for (Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) {
                if (plugin.isEnabled()) tabs.add(plugin.getName());
            }
        }
    }

    @Override
    public String getUsage() {
        return "restart <Plugin>" + this.getBatchActionSuffix();
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.restart.help"));
    }

}
