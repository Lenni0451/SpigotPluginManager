package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.messages.I18n;
import net.lenni0451.spm.utils.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class Permissions_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
        if (!plugin.isPresent()) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.general.pluginNotFound"));
            return true;
        }

        List<Permission> permissions = plugin.get().getDescription().getPermissions();
        if (permissions.isEmpty()) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.permissions.noPermissions"));
        } else {
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.permissions.header", plugin.get().getName()));
            for (Permission permission : permissions) {
                String permName = permission.getName();
                String permDescription = permission.getDescription();
                PermissionDefault permDefault = permission.getDefault();

                String message = " §7- §6" + permName;
                String hoverMessage = "";
                if (!permDescription.isEmpty())
                    hoverMessage += "§a" + I18n.t("pm.subcommands.permissions.description") + ": §6" + permDescription + "\n";
                hoverMessage += "§a" + I18n.t("pm.subcommands.permissions.default") + ": §6";
                switch (permDefault) {
                    case TRUE:
                        hoverMessage += I18n.t("pm.subcommands.permissions.everybody");
                        break;
                    case FALSE:
                        hoverMessage += I18n.t("pm.subcommands.permissions.nobody");
                        break;
                    case OP:
                        hoverMessage += I18n.t("pm.subcommands.permissions.ops");
                        break;
                    case NOT_OP:
                        hoverMessage += I18n.t("pm.subcommands.permissions.notOps");
                        break;
                    default:
                        hoverMessage += I18n.t("pm.subcommands.permissions.undefined");
                }
                if (!permission.getChildren().keySet().isEmpty()) {
                    hoverMessage += "\n§a" + I18n.t("pm.subcommands.permissions.childPermissions") + ":";
                    for (String subPerm : permission.getChildren().keySet()) hoverMessage += "\n §7- §6" + subPerm;
                }

                if (sender instanceof Player) {
                    TextComponent textComponent = new TextComponent(message);
                    hoverMessage = "§7" + permName + ":\n" + hoverMessage;
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(hoverMessage)}));
                    ((Player) sender).spigot().sendMessage(textComponent);
                } else {
                    sender.sendMessage(message);
                    String[] lines = hoverMessage.split(Pattern.quote("\n"));
                    for (String line : lines) sender.sendMessage("   " + line);
                }
            }
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
        return "permissions <Plugin>";
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.permissions.help"));
    }

}
