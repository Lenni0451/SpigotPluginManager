package net.Lenni0451.spm.commands.subs;

import net.Lenni0451.spm.PluginManager;
import net.Lenni0451.spm.commands.subs.types.ISubCommand;
import net.Lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class List_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 0) return false;

        Plugin[] plugins = PluginManager.getInstance().getPluginUtils().getPlugins();
        List<Plugin> sortedList = new ArrayList<>();
        Collections.addAll(sortedList, plugins);
        sortedList.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        Logger.sendPrefixMessage(sender, "§6Plugins §e§o(" + plugins.length + ")§6:");
        for (Plugin plugin : sortedList) {
            sender.sendMessage(" §7- " + (plugin.isEnabled() ? "§a" : "§c") + plugin.getName() + " §6" + plugin.getDescription().getVersion());
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
    }

    @Override
    public String getUsage() {
        return "list";
    }

    @Override
    public void getHelp(List<String> lines) {
        lines.add("Show a list of all installed plugins");
        lines.add("and their status (enabled/disabled).");
    }

}
