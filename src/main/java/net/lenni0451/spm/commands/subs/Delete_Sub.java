package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.utils.FileUtils;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

public class Delete_Sub implements ISubCommand {

    public Delete_Sub() {
        try { //Delete all safely which could not be directly delete before
            for (File file : FileUtils.listFiles(PluginManager.getInstance().getPluginUtils().getPluginsDirectory())) {
                if (file.getName().toLowerCase().endsWith(".jar") && file.length() == 0) {
                    if (!file.delete()) file.deleteOnExit();
                }
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
        if (!plugin.isPresent()) {
            Logger.sendPrefixMessage(sender, "§cThe plugin could not be found.");
            return true;
        }
        try {
            PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin.get());
        } catch (Throwable e) {
            Logger.sendPrefixMessage(sender, "§cThe plugin could not be unloaded." + (e.getMessage() != null ? (" §7(" + e.getMessage() + ")") : ""));
            return true;
        }

        Optional<File> file = PluginManager.getInstance().getPluginUtils().getPluginFile(plugin.get());
        if (!file.isPresent()) {
            Logger.sendPrefixMessage(sender, "§cThe file of the plugin could not be found.");
            return true;
        }
        PluginManager.getInstance().getInstalledPlugins().removePlugin(plugin.get().getName());
        try {
            if (!file.get().delete()) file.get().deleteOnExit();
        } catch (Throwable ignored) {
        }
        if (!file.get().exists()) {
            Logger.sendPrefixMessage(sender, "§aThe plugin has been deleted.");
        } else {
            Logger.sendPrefixMessage(sender, "§cThe plugin could not be deleted.");
            try {
                try (FileOutputStream fos = new FileOutputStream(file.get())) {
                    fos.write(new byte[0]);
                }
                if (file.get().length() != 0) throw new IllegalStateException("Plugin could not be overwritten");
                Logger.sendPrefixMessage(sender, "§aIt will get deleted on the next restart.");
            } catch (Throwable t) {
                Logger.sendPrefixMessage(sender, "§cPluginManger tried to overwrite it but this failed too.");
                Logger.sendPrefixMessage(sender, "§cYou sadly have to delete it manually.");
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
        return "delete <Plugin>";
    }

    @Override
    public void getHelp(List<String> lines) {
        lines.add("Unload and delete a plugin directly from the server.");
        lines.add("If the plugin could not be deleted because of access");
        lines.add("restrictions it will be overwritten with an empty");
        lines.add("file and get deleted the next time if possible.");
        lines.add("If this fails too there is no way for PluginManager");
        lines.add("to delete the plugin so you have to do it manually.");
    }

}
