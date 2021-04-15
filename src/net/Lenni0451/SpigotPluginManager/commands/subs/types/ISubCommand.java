package net.Lenni0451.SpigotPluginManager.commands.subs.types;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ISubCommand {

    boolean execute(CommandSender sender, String[] args);

    void getTabComplete(List<String> tabs, String[] args);

    String getUsage();

    void getHelp(List<String> lines);


    default String getBatchActionSuffix() {
        return (PluginManager.getInstance().getConfig().getBoolean("AllowBatchActions") ? "/*" : "");
    }

}
