package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.messages.I18n;
import net.lenni0451.spm.utils.FileUtils;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
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
            Logger.sendPrefixMessage(sender, I18n.t("pm.general.pluginNotFound"));
            return true;
        }
        try {
            PluginManager.getInstance().getPluginUtils().unloadPlugin(plugin.get());
        } catch (Throwable e) {
            e.printStackTrace();
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.delete.unloadError", e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
            return true;
        }

        Optional<File> file = PluginManager.getInstance().getPluginUtils().getPluginFile(plugin.get());
        File dataDir = new File("plugins/" + plugin.get().getDescription().getName());
        if (!file.isPresent()) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.delete.fileNotFound"));
            return true;
        }
        PluginManager.getInstance().getInstalledPlugins().removePlugin(plugin.get().getName());
        try {
            if (!file.get().delete()) file.get().deleteOnExit();
            else FileUtils.delete(dataDir);
        } catch (Throwable ignored) {
        }
        if (!file.get().exists()) {
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.delete.success"));
        } else {
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.delete.deleteError"));
            try {
                try (FileOutputStream fos = new FileOutputStream(file.get())) {
                    fos.write(new byte[0]);
                }
                if (file.get().length() != 0)
                    throw new IllegalStateException(I18n.t("pm.subcommands.delete.overwriteError"));
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.delete.nextStartDelete"));
            } catch (Throwable t) {
                for (String s : I18n.mt("pm.subcommands.delete.manualDelete")) {
                    Logger.sendPrefixMessage(sender, s);
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
        return "delete <Plugin>";
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.delete.help"));
    }

}
