package net.Lenni0451.SpigotPluginManager.tabcomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.Lenni0451.SpigotPluginManager.commands.PluginManager_Command;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;

public class PluginManager_TabComplete implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> tabs = new ArrayList<>();
		String lastArg = args[args.length - 1];
		
		if(args.length == 1) {
			for(String subCommandName : PluginManager_Command.subCommands.keySet()) {
				tabs.add(subCommandName);
			}
		} else if(args.length >= 2) {
			String subCommandName = args[0].toLowerCase();
			ISubCommand subCommand = PluginManager_Command.subCommands.get(subCommandName);
			if(subCommand == null) return tabs;
			args = Arrays.copyOfRange(args, 1, args.length - 1);
			
			subCommand.getTabComplete(tabs, args);
		}
		
		List<String> filteredTabs = new ArrayList<>();
		for(String tab : tabs) {
			if(tab.toLowerCase().startsWith(lastArg.toLowerCase()) && !filteredTabs.contains(tab)) {
				filteredTabs.add(tab);
			}
		}
		
		Collections.sort(filteredTabs);
		return filteredTabs;
	}
	
}
