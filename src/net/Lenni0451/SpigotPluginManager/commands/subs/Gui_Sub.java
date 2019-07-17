package net.Lenni0451.SpigotPluginManager.commands.subs;

import java.util.Arrays;
import java.util.List;

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

import net.Lenni0451.SpigotPluginManager.PluginManager;
import net.Lenni0451.SpigotPluginManager.commands.subs.types.ISubCommand;

public class Gui_Sub implements ISubCommand, Listener {
	
	public Gui_Sub() {
		Bukkit.getPluginManager().registerEvents(this, PluginManager.getInstance());
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length != 0) {
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cYou have to run this command as a player.");
			return true;
		}
		
		Plugin[] plugins = PluginManager.getInstance().getPluginUtils().getPlugins();
		int rowCount = Double.valueOf(Math.ceil((double) plugins.length / 9D)).intValue();
		int slotCount = rowCount * 9;
		
		Inventory inv = Bukkit.createInventory(null, slotCount, "§3PluginManager");
		
		for(Plugin plugin : plugins) {
			ItemStack stack = new ItemStack(Material.WOOL, 1, (byte) (plugin.isEnabled() ? 5 : 14));
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName("§a" + plugin.getName());
			String authors = plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", "");
			
			meta.setLore(Arrays.asList("§2Name: §6" + plugin.getName(), "§2Version: §6" + plugin.getDescription().getVersion(), "§2Author(s): §6" + (authors.isEmpty() ? "§4-" : authors)));
			
			stack.setItemMeta(meta);
			
			inv.addItem(stack);
		}
		
		((Player) sender).openInventory(inv);
		
		return true;
	}

	@Override
	public void getTabComplete(List<String> tabs, String[] args) {}

	@Override
	public String getUsage() {
		return "gui";
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getCurrentItem() == null) {
			return;
		}
		
		if(event.getInventory().getTitle().equalsIgnoreCase("§3PluginManager")) {
			event.setCancelled(true);
			if(event.getSlot() >= event.getInventory().getSize() || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
				event.setCancelled(true);
				return;
			}
//			event.getWhoClicked().closeInventory();
			
			String pluginName = event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§.", "");
			
			try {
				Plugin plugin = PluginManager.getInstance().getPluginUtils().getPlugin(pluginName);
				
				Inventory inv = Bukkit.createInventory(null, 9, "§3PM §6" + plugin.getName());
				{
					ItemStack stack = new ItemStack(Material.WOOL, 1, (byte) (plugin.isEnabled() ? 14 : 5));
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
					ItemStack stack = new ItemStack(Material.COMMAND);
					ItemMeta meta = stack.getItemMeta();
					meta.setDisplayName("§aCommands");
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
			} catch (Throwable e) {}
		} else if(event.getInventory().getTitle().startsWith("§3PM §6")) {
			event.setCancelled(true);
			event.getWhoClicked().closeInventory();
			if(event.getSlot() >= event.getInventory().getSize() || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
				event.setCancelled(true);
				return;
			}
			
			String pluginName = event.getInventory().getTitle().substring(7);
			
			try {
				String action = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
				if(action.equalsIgnoreCase("back")) {
					this.execute(event.getWhoClicked(), new String[] {});
					return;
				}
				
				String cmd = PluginManager.getInstance().getName() + ":pm " + action.toLowerCase() + " " + pluginName;
				Bukkit.dispatchCommand(event.getWhoClicked(), cmd);
				this.execute(event.getWhoClicked(), new String[] {});
			} catch (Throwable e) {}
		}
	}

}
