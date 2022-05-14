package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.commands.PluginManager_Command;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.messages.I18n;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Help_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        ISubCommand subCommand = PluginManager_Command.subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.commands.pluginmanager.subNotFound"));
            return true;
        }

        List<String> lines = new ArrayList<>();
        subCommand.getHelp(lines);
        Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.help.header", args[0].toLowerCase()));
        for (String usage : subCommand.getUsage().split(Pattern.quote("\n"))) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.commands.pluginmanager.correctUsage", usage));
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
        Collections.addAll(lines, I18n.mt("pm.subcommands.help.help"));
    }

}
