package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.utils.I18n;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Reload_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        if (args[0].equalsIgnoreCase("*") && PluginManager.getInstance().getConfig().getBoolean("AllowBatchActions")) {
            Bukkit.dispatchCommand(sender, PluginManager.getInstance().getName() + ":reload"); //Lazy way to call the /reload command. TODO: Maybe find a better way here?
        } else {
            Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
            if (!plugin.isPresent()) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.general.pluginNotFound"));
                return true;
            }

            try {
                PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin.get());
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.unload.unloadError", plugin.get().getName(), e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
                return true;
            }
            try {
                PluginManager.getInstance().getPluginUtils().loadPlugin(plugin.get());
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.load.loadError", plugin.get().getName(), e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
                return true;
            }
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.reload.success", plugin.get().getName()));
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
        if (args.length == 0) {
            for (Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) {
                tabs.add(plugin.getName());
            }
        }
    }

    @Override
    public String getUsage() {
        return "reload <Plugin>" + this.getBatchActionSuffix();
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.reload.help"));
    }

}
