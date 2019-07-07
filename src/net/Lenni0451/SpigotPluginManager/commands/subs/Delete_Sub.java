package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.utils.Logger;

public class Delete_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		try {
			Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
			try {
				PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin);
			} catch (Throwable e) {
				Logger.sendPrefixMessage(sender, "§cThe plugin could not be unloaded.");
				return true;
			}
			
			File file = PluginManager.getInstance().getPluginUtils().getPluginFile(plugin);
			if(file.delete()) {
				Logger.sendPrefixMessage(sender, "§aThe plugin has been deleted.");
			} else {
				file.deleteOnExit();
				Logger.sendPrefixMessage(sender, "§cThe plugin could not be deleted.");
				Logger.sendPrefixMessage(sender, "§aIt will get deleted when the server stops.");
			}
		} catch (FileNotFoundException e) {
			Logger.sendPrefixMessage(sender, "§cThe file of the plugin could not be found.");
		} catch (Throwable e) {
			Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
		}
		
		return true;
	}

	@Override
	public void getTabComplete(List<String> tabs, String[] args) {
		if(args.length == 0) {
			for(Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) {
				tabs.add(plugin.getName());
			}
		}
	}

	@Override
	public String getUsage() {
		return "delete <Plugin>";
	}
	
}
