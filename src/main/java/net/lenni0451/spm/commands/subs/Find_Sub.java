package net.Lenni0451.spm.commands.subs;

import net.Lenni0451.spm.PluginManager;
import net.Lenni0451.spm.commands.subs.types.ISubCommand;
import net.Lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Find_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        List<Plugin> plugins = new ArrayList<>();
        for (Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) {
            List<String> commands = PluginManager.getInstance().getPluginUtils().getCommands(plugin);
            if (commands.contains(args[0]) || commands.contains(" " + args[0])) {
                plugins.add(plugin);
            }
        }
        if (plugins.isEmpty()) {
            Logger.sendPrefixMessage(sender, "§cThere is no plugin which has this command registered.");
        } else {
            Logger.sendPrefixMessage(sender, "§6The plugins with this command:");
            for (Plugin plugin : plugins) {
                sender.sendMessage(" §7- §6" + plugin.getName());
            }
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
    }

    @Override
    public String getUsage() {
        return "find <Command>";
    }

    @Override
    public void getHelp(List<String> lines) {
        lines.add("Find the plugin which registered a specified command.");
        lines.add("It is only possible to find commands which are registered");
        lines.add("using the \"normal\" way of adding them to the plugin.yml.");
        lines.add("All commands registered differently by eg. using events can");
        lines.add("not be listed by PluginManager!");
    }

}
