package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.messages.I18n;
import net.lenni0451.spm.utils.Logger;
import net.lenni0451.spm.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Info_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
        if (!plugin.isPresent()) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.general.pluginNotFound"));
            return true;
        }
        PluginDescriptionFile description = plugin.get().getDescription();

        Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.info.pluginInfo"));
        sender.sendMessage(" " + I18n.t("pm.subcommands.info.name", description.getName()));
        if (description.getDescription() != null) {
            sender.sendMessage(" " + I18n.t("pm.subcommands.info.description", description.getDescription()));
        }
        sender.sendMessage(" " + I18n.t("pm.subcommands.info.version", description.getVersion()));
        String authors = StringUtils.listToString(description.getAuthors());
        sender.sendMessage(" " + I18n.t("pm.subcommands.info.authors", authors.isEmpty() ? "§4-" : authors));
        if (plugin.get().isEnabled()) sender.sendMessage(" " + I18n.t("pm.subcommands.info.pluginEnabled"));
        else sender.sendMessage(" " + I18n.t("pm.subcommands.info.pluginDisabled"));

        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
        if (args.length == 0) {
            for (Plugin plugin : PluginManager.getInstance().getPluginUtils().getPlugins()) tabs.add(plugin.getName());
        }
    }

    @Override
    public String getUsage() {
        return "info <Plugin>";
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.info.help"));
    }

}
