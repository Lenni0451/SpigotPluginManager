package net.Lenni0451.spm.tabcomplete;

import net.Lenni0451.spm.commands.PluginManager_Command;
import net.Lenni0451.spm.commands.subs.types.ISubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PluginManager_TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tabs = new ArrayList<>();
        String lastArg = args[args.length - 1];

        if (args.length == 1) {
            tabs.addAll(PluginManager_Command.subCommands.keySet());
        } else {
            String subCommandName = args[0].toLowerCase();
            ISubCommand subCommand = PluginManager_Command.subCommands.get(subCommandName);
            if (subCommand == null) return tabs;
            args = Arrays.copyOfRange(args, 1, args.length - 1);

            subCommand.getTabComplete(tabs, args);
        }

        List<String> filteredTabs = new ArrayList<>();
        for (String tab : tabs) {
            if (tab.toLowerCase().startsWith(lastArg.toLowerCase()) && !filteredTabs.contains(tab)) {
                filteredTabs.add(tab);
            }
        }

        Collections.sort(filteredTabs);
        return filteredTabs;
    }

}
