package net.Lenni0451.spm.commands.subs;

import net.Lenni0451.spm.PluginManager;
import net.Lenni0451.spm.commands.subs.types.ISubCommand;
import net.Lenni0451.spm.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

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
                Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
                return true;
            }

            try {
                PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin.get());
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.sendPrefixMessage(sender, "§cCould not unload the plugin §6" + plugin.get().getName() + "§c." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
                return true;
            }
            try {
                PluginManager.getInstance().getPluginUtils().loadPlugin(plugin.get());
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.sendPrefixMessage(sender, "§cCould not load the plugin §6" + plugin.get().getName() + "§c." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
                return true;
            }
            Logger.sendPrefixMessage(sender, "§aThe plugin §6" + plugin.get().getName() + " §ahas been reloaded.");
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
        lines.add("Unload and load a plugin again to release");
        lines.add("all its loaded resources and update it");
        lines.add("during runtime or try to fix some issues.");
    }

}
