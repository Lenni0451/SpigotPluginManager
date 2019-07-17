package net.Lenni0451.SpigotPluginManager.commands.subs.types;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface ISubCommand {
	
	public boolean execute(CommandSender sender, String[] args);
	public void getTabComplete(List<String> tabs, String[] args);
	public String getUsage();
	
}
