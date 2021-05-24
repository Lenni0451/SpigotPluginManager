package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.utils.I18n;
import net.lenni0451.spm.utils.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Commands_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
        if (!plugin.isPresent()) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.general.pluginNotFound"));
            return true;
        }

        List<String> commands = PluginManager.getInstance().getPluginUtils().getCommands(plugin.get());
        if (commands.isEmpty()) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.commands.noCommands", plugin.get().getName()));
        } else {
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.commands.commandsOf", plugin.get().getName()));
            for (String command : commands) {
                String message;
                if (command.startsWith(" ")) {
                    message = "  §7- §6" + command.substring(1);
                } else {
                    message = " §7- §6" + command;
                }

                if (sender instanceof Player) {
                    TextComponent textComponent = new TextComponent(message);
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command.replace(" ", "")));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§b/" + command.replace(" ", ""))}));
                    ((Player) sender).spigot().sendMessage(textComponent);
                } else {
                    sender.sendMessage(message);
                }
            }
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
        if (args.length == 0) {
            Arrays.stream(PluginManager.getInstance().getPluginUtils().getPlugins()).forEach(plugin -> tabs.add(plugin.getName()));
        }
    }

    @Override
    public String getUsage() {
        return "commands <Plugin>";
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.commands.help"));
    }

}
