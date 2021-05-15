package net.Lenni0451.spm.commands.subs;

import net.Lenni0451.spm.PluginManager;
import net.Lenni0451.spm.commands.subs.types.ISubCommand;
import net.Lenni0451.spm.utils.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Commands_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
        if (!plugin.isPresent()) {
            Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
            return true;
        }

        List<String> commands = PluginManager.getInstance().getPluginUtils().getCommands(plugin.get());
        if (commands.isEmpty()) {
            Logger.sendPrefixMessage(sender, "§cThe plugin §6" + plugin.get().getName() + " §chas no commands registered.");
        } else {
            Logger.sendPrefixMessage(sender, "§6Commands of §a" + plugin.get().getName() + "§6:");
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
        lines.add("Show a list of all commands registered by a given plugin.");
        lines.add("It is only possible to show commands which are registered");
        lines.add("using the \"normal\" way of adding them to the plugin.yml.");
        lines.add("All commands registered differently by eg. using events can");
        lines.add("not be listed by PluginManager!");
    }

}
