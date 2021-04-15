package net.Lenni0451.SpigotPluginManager.commands.subs;

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;
import net.Lenni0451.SpigotPluginManager.utils.VersionMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class Gui_Sub implements ISubCommand, Listener {

    public Gui_Sub() {
        Bukkit.getPluginManager().registerEvents(this, PluginManager.getInstance());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 0) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou have to run this command as a player.");
            return true;
        }

        this.openGui((Player) sender, 0);
        return true;
    }

    @Override
    public void getTabComplete(List<String> tabs, String[] args) {
    }

    @Override
    public String getUsage() {
        return "gui";
    }

    @Override
    public void getHelp(List<String> lines) {
        lines.add("Show an easy to use gui to execute all plugin");
        lines.add("specific commands without actually executing them.");
    }

    public void openGui(final Player player, final int currentPage) {
        Plugin[] allPlugins = PluginManager.getInstance().getPluginUtils().getPlugins();
        final int pageCount = Double.valueOf(Math.ceil((double) allPlugins.length / (5 * 9))).intValue();
        Plugin[] plugins = allPlugins;
        if (allPlugins.length > 5 * 9) {
            plugins = Arrays.copyOfRange(allPlugins, 5 * 9 * currentPage, Math.min(allPlugins.length, 5 * 9 * (currentPage + 1)));
        }
        final int rowCount = Double.valueOf(Math.ceil((double) plugins.length / 9D)).intValue() + 1;

        this.openGui(player, currentPage, rowCount, plugins, currentPage > 0, currentPage < pageCount - 1);
    }

    public void openGui(final Player player, final int currentPage, final int rowCount, final Plugin[] plugins, final boolean hasPageBefore, final boolean hasPageAfter) {
        Inventory inv = Bukkit.createInventory(null, rowCount * 9, "§3PluginManager §2Page " + (currentPage + 1));

        for (int i = 0; i < inv.getSize() - 9 && plugins.length > i; i++) {
            final Plugin plugin = plugins[i];

            Material wool = VersionMaterial.getMaterial("WOOL");
            ItemStack stack;
            if (wool != null) {
                stack = new ItemStack(wool, 1, (byte) (plugin.isEnabled() ? 5 : 14));
            } else {
                Material greenWool = VersionMaterial.getMaterial("LIME_WOOL");
                Material redWool = VersionMaterial.getMaterial("RED_WOOL");
                stack = new ItemStack(plugin.isEnabled() ? greenWool : redWool);
            }
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName("§a" + plugin.getName());
            String authors = plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", "");
            meta.setLore(Arrays.asList("§2Name: §6" + plugin.getName(), "§2Version: §6" + plugin.getDescription().getVersion(), "§2Author(s): §6" + (authors.isEmpty() ? "§4-" : authors)));
            stack.setItemMeta(meta);

            inv.setItem(i, stack);
        }

        for (int i = 0; i < 9; i++) {
            Material glassPane = VersionMaterial.getMaterial("STAINED_GLASS_PANE");
            ItemStack spaceFiller;
            if (glassPane != null) {
                spaceFiller = new ItemStack(glassPane, 1, (byte) 7);
            } else {
                spaceFiller = new ItemStack(VersionMaterial.getMaterial("GRAY_STAINED_GLASS_PANE"));
            }
            ItemMeta meta = spaceFiller.getItemMeta();
            meta.setDisplayName(" ");
            spaceFiller.setItemMeta(meta);

            inv.setItem(i + inv.getSize() - 9, spaceFiller);
        }
        if (hasPageBefore) {
            ItemStack backArrow = new ItemStack(Material.ARROW, currentPage);
            ItemMeta meta = backArrow.getItemMeta();
            meta.setDisplayName("§7Back to page §6" + currentPage);
            backArrow.setItemMeta(meta);

            inv.setItem(inv.getSize() - 9, backArrow);
        }
        if (hasPageAfter) {
            ItemStack forwardArrow = new ItemStack(Material.ARROW, currentPage + 2);
            ItemMeta meta = forwardArrow.getItemMeta();
            meta.setDisplayName("§7Go to page §6" + (currentPage + 2));
            forwardArrow.setItemMeta(meta);

            inv.setItem(inv.getSize() - 1, forwardArrow);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getView().getTitle() == null) return;

        if (event.getView().getTitle().startsWith("§3PluginManager")) {
            event.setCancelled(true);
            if (event.getSlot() >= event.getInventory().getSize() || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
                return;
            }
//			event.getWhoClicked().closeInventory();

            String itemName = event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§.", "");
            if (itemName.equalsIgnoreCase(" ")) {
                event.setCancelled(true);
                return;
            } else {
                if (itemName.startsWith("Back to page ")) {
                    this.openGui((Player) event.getWhoClicked(), Integer.parseInt(itemName.substring(itemName.length() - 1)) - 1);
                    return;
                } else if (itemName.startsWith("Go to page ")) {
                    this.openGui((Player) event.getWhoClicked(), Integer.parseInt(itemName.substring(itemName.length() - 1)) - 1);
                    return;
                }
            }

            try {
                Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(itemName).get();

                Inventory inv = Bukkit.createInventory(null, 9, "§3PM §6" + plugin.getName());
                {
                    Material wool = VersionMaterial.getMaterial("WOOL");
                    ItemStack stack;
                    if (wool != null) {
                        stack = new ItemStack(wool, 1, (byte) (plugin.isEnabled() ? 14 : 5));
                    } else {
                        Material greenWool = VersionMaterial.getMaterial("LIME_WOOL");
                        Material redWool = VersionMaterial.getMaterial("RED_WOOL");
                        stack = new ItemStack(plugin.isEnabled() ? greenWool : redWool);
                    }
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(plugin.isEnabled() ? "§cDisable" : "§aEnable");
                    stack.setItemMeta(meta);

                    inv.addItem(stack);
                }
                {
                    ItemStack stack = new ItemStack(Material.BARRIER);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName("§aUnload");
                    stack.setItemMeta(meta);

                    inv.addItem(stack);
                }
                {
                    ItemStack stack = new ItemStack(Material.BUCKET);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName("§aReload");
                    stack.setItemMeta(meta);

                    inv.addItem(stack);
                }
                {
                    ItemStack stack = new ItemStack(Material.HOPPER);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName("§aRestart");
                    stack.setItemMeta(meta);

                    inv.addItem(stack);
                }
                {
                    ItemStack stack = new ItemStack(VersionMaterial.getMaterial("COMMAND", "COMMAND_BLOCK"));
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName("§aCommands");
                    stack.setItemMeta(meta);

                    inv.addItem(stack);
                }
                {
                    ItemStack stack = new ItemStack(VersionMaterial.getMaterial("BOOK_AND_QUILL", "WRITABLE_BOOK"));
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName("§aPermissions");
                    stack.setItemMeta(meta);

                    inv.addItem(stack);
                }
                {
                    ItemStack stack = new ItemStack(Material.ARROW);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName("§aBack");
                    stack.setItemMeta(meta);

                    inv.setItem(8, stack);
                }

                event.getWhoClicked().openInventory(inv);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else if (event.getView().getTitle().startsWith("§3PM §6")) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            if (event.getSlot() >= event.getInventory().getSize() || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
                event.setCancelled(true);
                return;
            }

            String pluginName = event.getView().getTitle().substring(7);

            try {
                String action = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
                if (action.equalsIgnoreCase("back")) {
                    this.execute(event.getWhoClicked(), new String[]{});
                    return;
                }

                String cmd = PluginManager.getInstance().getName() + ":pm " + action.toLowerCase() + " " + pluginName;
                Bukkit.dispatchCommand(event.getWhoClicked(), cmd);
                this.execute(event.getWhoClicked(), new String[]{});
            } catch (Throwable ignored) {
            }
        }
    }

}
