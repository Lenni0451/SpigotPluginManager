package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

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
                    Logger.sendPrefixMessage(sender, "§cCould not unload the plugin §6" + plugin.getName() + "§c." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
                }
            }

            Logger.sendPrefixMessage(sender, "§aUnloaded all plugins §e(" + plugins.size() + ")§a.");
        } else {
            if (!PluginManager.getInstance().getPluginUtils().getPlugin(args[0]).isPresent()) {
                Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
                return true;
            }
            try {
                PluginManager.getInstance().getPluginUtils().unloadPlugin(args[0]);
                Logger.sendPrefixMessage(sender, "§aThe plugin has been unloaded.");
            } catch (Throwable e) {
                Logger.sendPrefixMessage(sender, "§cThe plugin could not be unloaded." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
            }
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
        return "unload <Plugin>" + this.getBatchActionSuffix();
    }

    @Override
    public void getHelp(List<String> lines) {
        lines.add("Unload a plugin and release all its loaded resources.");
        lines.add("If you are testing new plugins you can just unload");
        lines.add("them again if they do not fit your needs.");
    }

}
