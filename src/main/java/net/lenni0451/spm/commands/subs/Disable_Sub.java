package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Disable_Sub implements ISubCommand {

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
                    Logger.sendPrefixMessage(sender, "§cCould not disable the plugin §6" + plugin.getName() + "§c." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
                }
            }
            Logger.sendPrefixMessage(sender, "§aDisabled all plugins §e(" + plugins.size() + ")§a.");
        } else {
            Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
            if (!plugin.isPresent()) {
                Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
                return true;
            }

            try {
                if (PluginManager.getInstance().getPluginUtils().disablePlugin(plugin.get())) {
                    Logger.sendPrefixMessage(sender, "§aThe plugin §6" + plugin.get().getName() + " §ahas been disabled.");
                } else {
                    Logger.sendPrefixMessage(sender, "§cThe plugin §6" + plugin.get().getName() + " §cis already disabled.");
                }
            } catch (Throwable e) {
                Logger.sendPrefixMessage(sender, "§cThe plugin §6" + plugin.get().getName() + " §ccould not be disabled." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
            }
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
        return "disable <Plugin>" + this.getBatchActionSuffix();
    }

    @Override
    public void getHelp(List<String> lines) {
        lines.add("Disable a plugin to stop it from executing.");
        lines.add("You can easily enable it again using '/pm enable'.");
    }

}
