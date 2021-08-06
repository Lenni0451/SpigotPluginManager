package net.lenni0451.spm.commands.subs;

import net.lenni0451.spm.PluginManager;
import net.lenni0451.spm.commands.subs.types.ISubCommandMultithreaded;
import net.lenni0451.spm.utils.I18n;
import net.lenni0451.spm.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Dump_Sub implements ISubCommandMultithreaded {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        File dumpsFolder = new File("plugin_dumps");
        dumpsFolder.mkdirs();

        if (args[0].equalsIgnoreCase("*") && PluginManager.getInstance().getConfig().getBoolean("AllowBatchActions")) {
            Plugin[] plugins = PluginManager.getInstance().getPluginUtils().getPlugins();

            for (Plugin plugin : plugins) {
                List<String> lines = this.getPluginInfos(plugin);

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dumpsFolder, plugin.getName() + ".txt"), false));
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                        writer.flush();
                    }
                    writer.close();
                } catch (Throwable e) {
                    Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.dump.noBatchPermission", plugin.getName(), e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
                }
            }
            Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.dump.batchSuccess"));
        } else {
            Optional<Plugin> plugin = PluginManager.getInstance().getPluginUtils().getPlugin(args[0]);
            if (!plugin.isPresent()) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.general.pluginNotFound"));
                return true;
            }
            List<String> lines = this.getPluginInfos(plugin.get());

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dumpsFolder, plugin.get().getName() + ".txt"), false));
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                }
                writer.close();
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.dump.success"));
            } catch (Exception e) {
                Logger.sendPrefixMessage(sender, I18n.t("pm.subcommands.dump.noPermission", e.getMessage() == null ? I18n.t("pm.general.checkConsole") : e.getMessage()));
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
        return "dump <Plugin>" + this.getBatchActionSuffix();
    }

    @Override
    public void getHelp(List<String> lines) {
        Collections.addAll(lines, I18n.mt("pm.subcommands.dump.help"));
    }

    private List<String> getPluginInfos(final Plugin plugin) {
        PluginDescriptionFile description = plugin.getDescription();

        List<String> lines = new ArrayList<>();

        lines.add(I18n.t("pm.subcommands.dump.name", plugin.getName()));
        if (description.getDescription() != null) {
            lines.add(I18n.t("pm.subcommands.dump.description", description.getDescription()));
        }
        lines.add(I18n.t("pm.subcommands.dump.version", description.getVersion()));
        {
            String authors = description.getAuthors().toString().replace("[", "").replace("]", "");
            lines.add(I18n.t("pm.subcommands.dump.authors", authors.isEmpty() ? "-" : authors));
        }
        if (description.getWebsite() != null) {
            lines.add(I18n.t("pm.subcommands.dump.website", description.getWebsite()));
        }
        if (description.getPrefix() != null) {
            lines.add(I18n.t("pm.subcommands.dump.prefix", description.getPrefix()));
        }

        List<String> commands = PluginManager.getInstance().getPluginUtils().getCommands(plugin);
        if (!commands.isEmpty()) {
            lines.add("");
            lines.add(I18n.t("pm.subcommands.dump.commands"));

            for (String command : commands) {
                if (command.startsWith(" ")) {
                    lines.add("  -" + command);
                } else {
                    lines.add(" - " + command);
                }
            }
        }

        List<Permission> permissions = plugin.getDescription().getPermissions();
        if (!permissions.isEmpty()) {
            lines.add("");
            lines.add(I18n.t("pm.subcommands.dump.permissions"));

            for (Permission permission : permissions) {
                String permName = permission.getName();
                String permDescription = permission.getDescription();
                PermissionDefault permDefault = permission.getDefault();

                String readablePermDefault;
                switch (permDefault) {
                    case TRUE:
                        readablePermDefault = I18n.t("pm.subcommands.permissions.everybody");
                        break;
                    case FALSE:
                        readablePermDefault = I18n.t("pm.subcommands.permissions.nobody");
                        break;
                    case OP:
                        readablePermDefault = I18n.t("pm.subcommands.permissions.ops");
                        break;
                    case NOT_OP:
                        readablePermDefault = I18n.t("pm.subcommands.permissions.notOps");
                        break;
                    default:
                        readablePermDefault = I18n.t("pm.subcommands.permissions.undefined");
                }

                lines.add(" - " + permName);
                if (!permDescription.isEmpty()) {
                    lines.add("   " + I18n.t("pm.subcommands.dump.description", permDescription));
                }
                lines.add("   " + I18n.t("pm.subcommands.dump.default", readablePermDefault));
                if (!permission.getChildren().keySet().isEmpty()) {
                    lines.add("   " + I18n.t("pm.subcommands.dump.childPermissions"));
                    for (String subPerm : permission.getChildren().keySet()) {
                        lines.add("    - " + subPerm);
                    }
                }
            }
        }

        return lines;
    }

}
