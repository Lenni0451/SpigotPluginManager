package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommandMultithread;
import net.Lenni0451.SpigotPluginManager.utils.Logger;

public class Dump_Sub implements ISubCommandMultithread {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		File dumpsFolder = new File("plugin_dumps");
		dumpsFolder.mkdirs();
		
		if(args[0].equalsIgnoreCase("*")) {
			Plugin[] plugins = PluginManager.getInstance().getPluginUtils().getPlugins();
			
			for(Plugin plugin : plugins) {
				List<String> lines = this.getPluginInfos(plugin);
				
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dumpsFolder, plugin.getName() + ".txt"), false));
					for(String line : lines) {
						writer.write(line);
						writer.newLine();
						writer.flush();
					}
					writer.close();
				} catch (Exception e) {
					Logger.sendPrefixMessage(sender, "§cThe file for §6" + plugin.getName() + " §ccould not be written. Do you have write access to the server folder?");
				}
			}
			Logger.sendPrefixMessage(sender, "Successfully dumped all Plugin infos. You can find them in the §6plugin_dumps §afolder.");
		} else {
			try {
				Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
				List<String> lines = this.getPluginInfos(plugin);

				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dumpsFolder, plugin.getName() + ".txt"), false));
					for(String line : lines) {
						writer.write(line);
						writer.newLine();
						writer.flush();
					}
					writer.close();
					Logger.sendPrefixMessage(sender, "Successfully dumped Plugin infos. You can find it in the §6plugin_dumps §afolder.");
				} catch (Exception e) {
					Logger.sendPrefixMessage(sender, "§cThe file could not be written. Do you have write access to the server folder?");
				}
			} catch (Throwable e) {
				Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
			}
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
		return "dump <Plugin>/*";
	}
	
	private List<String> getPluginInfos(final Plugin plugin) {
		PluginDescriptionFile description = plugin.getDescription();
		
		List<String> lines = new ArrayList<>();
		
		lines.add("Name: " + plugin.getName());
		if(description.getDescription() != null) {
			lines.add("Description: " + description.getDescription());
		}
		lines.add("Version: " + description.getVersion());
		{
			String authors = description.getAuthors().toString().replace("[", "").replace("]", "");
			lines.add("Author(s): " + (authors.isEmpty() ? "-" : authors));
		}
		if(description.getWebsite() != null) {
			lines.add("Website: " + description.getWebsite());
		}
		if(description.getPrefix() != null) {
			lines.add("Prefix: " + description.getPrefix());
		}
		
		List<String> commands = PluginManager.getInstance().getPluginUtils().getCommands(plugin);
		if(!commands.isEmpty()) {
			lines.add("");
			lines.add("Commands:");
			
			for(String command : commands) {
				if(command.startsWith(" ")) {
					lines.add("  -" + command);
				} else {
					lines.add(" - " + command);
				}
			}
		}
		
		List<Permission> permissions = plugin.getDescription().getPermissions();
		if(!permissions.isEmpty()) {
			lines.add("");
			lines.add("Permissions:");
			
			for(Permission permission : permissions) {
				String permName = permission.getName();
				String permDescription = permission.getDescription();
				PermissionDefault permDefault = permission.getDefault();
				
				String readablePermDefault;
				switch(permDefault) {
				case TRUE:
					readablePermDefault = "Everybody";
					break;
				case FALSE:
					readablePermDefault = "Nobody";
					break;
				case OP:
					readablePermDefault = "OPs";
					break;
				case NOT_OP:
					readablePermDefault = "Not OPs";
					break;
				default:
					readablePermDefault = "Undefined";
				}
				
				lines.add(" - " + permName);
				if(!permDescription.isEmpty()) lines.add("   Description: " + permDescription);
				lines.add("   Default: " + readablePermDefault);
			}
		}
		
		return lines;
	}

}
