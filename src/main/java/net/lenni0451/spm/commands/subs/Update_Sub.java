package net.lenni0451.spm.commands.subs;

import com.google.gson.JsonObject;
import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommand;
import net.lenni0451.spm.utils.DownloadUtils;
import net.lenni0451.spm.utils.I18n;
import net.lenni0451.spm.utils.Logger;
import net.lenni0451.spm.utils.PluginInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Update_Sub implements ISubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        if (args[0].equalsIgnoreCase("*") && PluginManager.getInstance().getConfig().getBoolean("AllowBatchActions")) {
            List<Plugin> plugins = PluginManager.getInstance().getPluginUtils().getPluginsByLoadOrder();
            List<String> ignoredPlugins = PluginManager.getInstance().getConfig().getStringList("IgnoredPlugins");

            for (Plugin plugin : plugins) {
                if (ignoredPlugins.contains(plugin.getName())) continue;

                try {
                    this.checkForUpdate(sender, true, plugin);
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.batchSuccess", plugin.getName()));
                } catch (AlreadyUpToDateException e) {
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.batchUpToDate", plugin.getName()));
                } catch (IllegalArgumentException e) {
                    //Do nothing as it would cause spam with batch actions
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.batchSpigetError", plugin.getName()));
                } catch (UpdatedFailedException e) {
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.batchWriteError", plugin.getName()));
                } catch (Throwable e) {
                    e.printStackTrace();
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.error", plugin.getName(), e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
                }
            }
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.batchSuccess"));
        } else {
            Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
            if (!plugin.isPresent()) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.general.pluginNotFound"));
                return true;
            }

            try {
                this.checkForUpdate(sender, false, plugin.get());
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.success"));
            } catch (AlreadyUpToDateException e) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.upToDate"));
            } catch (IllegalArgumentException e) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.notInConfig", plugin.get().getName()));
//					Logger.sendPrefixMessage(sender, "§aYou can add it using §6/pm addupdater <Plugin> <Plugin Id>§a."); //TODO: Add command in the future
            } catch (IOException e) {
                e.printStackTrace();
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.spigetError"));
            } catch (UpdatedFailedException e) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.writeError"));
            } catch (Throwable e) {
                e.printStackTrace();
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.update.error", plugin.get().getName(), e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
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
        return "update <Plugin>" + this.getBatchActionSuffix();
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.update.help"));
    }

    private void checkForUpdate(final CommandSender messageReceiver, final boolean sendPluginName, final Plugin plugin) throws IOException {
        PluginInfo info = PluginManager.getInstance().getInstalledPlugins().getPluginInfo(plugin.getName());
        if (info == null) throw new IllegalArgumentException();

        JsonObject response = DownloadUtils.getSpigotMcPluginInfo(info.getId());
        if (response == null) throw new IOException();
        if (!response.get("version").getAsJsonObject().get("id").getAsString().equalsIgnoreCase(info.getInstalledVersion())) {
            if (sendPluginName) {
                Logger.sendPrefixMessage(messageReceiver, I18n.t("pm.subcommands.update.nameUpdateAvailable", plugin.getName()));
            } else {
                Logger.sendPrefixMessage(messageReceiver, I18n.t("pm.subcommands.update.updateAvailable"));
            }
            try {
                DownloadUtils.downloadSpigotMcPlugin(info.getId(), new File("plugins", info.getFileName()));
                PluginManager.getInstance().getInstalledPlugins().setPlugin(info.getName(), info.getId(), response.get("version").getAsJsonObject().get("id").getAsString(), info.getFileName());
                if (PluginManager.getInstance().getConfig().getBoolean("AutoReloadUpdated")) {
                    PluginManager.getInstance().getPluginUtils().reloadPlugin(plugin);
                }
            } catch (Throwable e) {
                throw new UpdatedFailedException();
            }
        } else {
            throw new AlreadyUpToDateException();
        }
    }


    private static final class UpdatedFailedException extends RuntimeException {
    }

    private static final class AlreadyUpToDateException extends RuntimeException {
    }

}
