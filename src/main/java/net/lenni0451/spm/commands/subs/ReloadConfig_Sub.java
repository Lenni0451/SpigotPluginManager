package net.Lenni0451.spm.commands.subs;

import net.Lenni0451.spm.PluginManager;
import net.Lenni0451.spm.commands.subs.types.ISubCommand;
import net.Lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;

public class ReloadConfig_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        if (args[0].equalsIgnoreCase("*") && PluginManager.getInstance().getConfig().getBoolean("AllowBatchActions")) {
            Plugin[] plugins = PluginManager.getInstance().getPluginUtils().getPlugins();
            List<String> ignoredPlugins = PluginManager.getInstance().getConfig().getStringList("IgnoredPlugins");

            for (Plugin plugin : plugins) {
                if (ignoredPlugins.contains(plugin.getName())) continue;

                plugin.reloadConfig();
            }
            Logger.sendPrefixMessage(sender, "§aReloaded the config of all plugins §e(" + plugins.length + ")§a.");
        } else {
            Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
            if (!plugin.isPresent()) {
                Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
                return true;
            }

            plugin.get().reloadConfig();
            Logger.sendPrefixMessage(sender, "§aThe config of the plugin §6" + plugin.get().getName() + " §ahas been reloaded.");
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
        return "reloadconfig <Plugin>" + this.getBatchActionSuffix();
    }

    @Override
    public void getHelp(List<String> lines) {
        lines.add("Reload the config of a given plugin.");
        lines.add("You can easily reload the config of other plugins");
        lines.add("even if they do not have an own reload config command.");
        lines.add("This may have no effect on some plugins if they");
        lines.add("cache config values or need other things executed on");
        lines.add("a config reload.");
    }

}
