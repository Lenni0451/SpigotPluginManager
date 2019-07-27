package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;
import net.Lenni0451.SpigotPluginManager.utils.Logger;

public class Info_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		try {
			Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
			PluginDescriptionFile description = plugin.getDescription();
			
			Logger.sendPrefixMessage(sender, "§6Plugin Info:");
			sender.sendMessage(" §aName: §6" + description.getName());
			if(description.getDescription() != null) {
				sender.sendMessage(" §aDescription: §6" + description.getDescription());
			}
			sender.sendMessage(" §aVersion: §6" + description.getVersion());
			{
				String authors = description.getAuthors().toString().replace("[", "").replace("]", "");
				sender.sendMessage(" §aAuthor(s): §6" + (authors.isEmpty() ? "§4-" : authors));
			}
			sender.sendMessage(" §aThe plugin is currently " + (plugin.isEnabled() ? "§aEnabled" : "§cDisabled"));
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
		return "info <Plugin>";
	}
	
}
