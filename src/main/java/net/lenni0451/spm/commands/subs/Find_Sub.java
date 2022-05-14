package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.messages.I18n;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Find_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        List<Plugin> plugins = new ArrayList<>();
        for (Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) {
            List<String> commands = PluginManager.getInstance().getPluginUtils().getCommands(plugin);
            if (commands.contains(args[0]) || commands.contains(" " + args[0])) plugins.add(plugin);
        }
        if (plugins.isEmpty()) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.find.noPlugin"));
        } else {
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.find.listHeader"));
            for (Plugin plugin : plugins) sender.sendMessage(" §7- §6" + plugin.getName());
        }

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
    }

    @Override
    public String getUsage() {
        return "find <Command>";
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.find.help"));
    }

}
