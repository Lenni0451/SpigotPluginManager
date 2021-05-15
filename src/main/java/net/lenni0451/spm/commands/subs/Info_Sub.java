package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.utils.Logger;
import net.lenni0451.spm.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;
import java.util.Optional;

public class Info_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
        if (!plugin.isPresent()) {
            Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
            return true;
        }
        PluginDescriptionFile description = plugin.get().getDescription();

        Logger.sendPrefixMessage(sender, "§6Plugin Info:");
        sender.sendMessage(" §aName: §6" + description.getName());
        if (description.getDescription() != null) {
            sender.sendMessage(" §aDescription: §6" + description.getDescription());
        }
        sender.sendMessage(" §aVersion: §6" + description.getVersion());
        String authors = StringUtils.listToString(description.getAuthors());
        sender.sendMessage(" §aAuthor(s): §6" + (authors.isEmpty() ? "§4-" : authors));
        sender.sendMessage(" §aThe plugin is currently " + (plugin.get().isEnabled() ? "§aEnabled" : "§cDisabled"));

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
        return "info <Plugin>";
    }

    @Override
    public void getHelp(List<String> lines) {
        lines.add("View some infos about a give command.");
        lines.add("Following things get shown:");
        lines.add(" - Name");
        lines.add(" - Description");
        lines.add(" - Version");
        lines.add(" - Author(s)");
        lines.add(" - Plugin status (enabled/disabled)");
    }

}
