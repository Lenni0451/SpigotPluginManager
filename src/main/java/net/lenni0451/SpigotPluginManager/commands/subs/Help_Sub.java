package net.Lenni0451.SpigotPluginManager.commands.subs;

import net.Lenni0451.SpigotPluginManager.commands.PluginManager_Command;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;
import net.Lenni0451.SpigotPluginManager.utils.Logger;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Help_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        ISubCommand subCommand = PluginManager_Command.subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            Logger.sendPrefixMessage(sender, "§cThe sub command could not be found");
            return true;
        }

        List<String> lines = new ArrayList<>();
        subCommand.getHelp(lines);
        Logger.sendPrefixMessage(sender, "§6---------- Help for §a" + args[0].toLowerCase() + " §6----------");
        for (String usage : subCommand.getUsage().split(Pattern.quote("\n"))) {
            Logger.sendPrefixMessage(sender, "§aUsage: §6pm " + usage);
        }
        sender.sendMessage(" ");
        for (String line : lines) {
            sender.sendMessage(" §a" + line);
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
        if (args.length == 0) tabs.addAll(PluginManager_Command.subCommands.keySet());
    }

    @Override
    public String getUsage() {
        return "help <Sub Command>";
    }

    @Override
    public void getHelp(List<String> lines) {
        lines.add("Show detailed information about all sub commands so");
        lines.add("you can see all important aspects directly without");
        lines.add("specifically searching for them.");
    }

}
