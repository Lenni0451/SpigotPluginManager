package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;
import net.Lenni0451.SpigotPluginManager.utils.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Permissions_Sub implements ISubCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			return false;
		}
		
		try {
			Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
			
			List<Permission> permissions = plugin.getDescription().getPermissions();
			if(permissions.isEmpty()) {
				Logger.sendPrefixMessage(sender, "§cThe plugin does not have permissions registered.");
			} else {
				Logger.sendPrefixMessage(sender, "§6Permissions of §a" + plugin.getName() + "§6:");
				for(Permission permission : permissions) {
					String permName = permission.getName();
					String permDescription = permission.getDescription();
					PermissionDefault permDefault = permission.getDefault();
					
					String message = " §7- §6" + permName;
					String hoverMessage = "";
					if(!permDescription.isEmpty()) hoverMessage += "§aDescription: §6" + permDescription + "\n";
					hoverMessage += "§aDefault: §6";
					switch(permDefault) {
					case TRUE:
						hoverMessage += "Everybody";
						break;
					case FALSE:
						hoverMessage += "Nobody";
						break;
					case OP:
						hoverMessage += "OPs";
						break;
					case NOT_OP:
						hoverMessage += "Not OPs";
					}
					
					if(sender instanceof Player) {
						TextComponent textComponent = new TextComponent(message);
						{
							hoverMessage = "§7" + permName + ":\n" + hoverMessage;
							textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(hoverMessage)}));
						}
						((Player) sender).spigot().sendMessage(textComponent);
					} else {
						sender.sendMessage(message);
						String[] lines = hoverMessage.split(Pattern.quote("\n"));
						for(String line : lines) {
							sender.sendMessage("   " + line);
						}
					}
				}
			}
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
		return "permissions <Plugin>";
	}

}
