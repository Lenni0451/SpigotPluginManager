package net.Lenni0451.SpigotPluginManager;

import java.util.Arrays;

import org.bukkit.plugin.java.JavaPlugin;

import net.Lenni0451.SpigotPluginManager.commands.PluginManager_Command;
import net.Lenni0451.SpigotPluginManager.commands.Reload_Command;
import net.Lenni0451.SpigotPluginManager.tabcomplete.PluginManager_TabComplete;
import net.Lenni0451.SpigotPluginManager.utils.PluginUtils;

public class PluginManager extends JavaPlugin {
	
	private static PluginManager instance;
	
	public static PluginManager getInstance() {
		return instance;
	}
	
	
	private final PluginUtils pluginUtils;
	
	public PluginManager() {
		instance = this;
		
		this.saveDefaultConfig();
		
		this.pluginUtils = new PluginUtils();
	}
	
	public PluginUtils getPluginUtils() {
		return this.pluginUtils;
	}
	
	
	@Override
	public void onEnable() {
		this.getCommand("reload").setExecutor(new Reload_Command());
		this.getCommand("reload").setAliases(Arrays.asList("rl"));
		
		this.getCommand("pluginmanager").setExecutor(new PluginManager_Command());
		this.getCommand("pluginmanager").setAliases(Arrays.asList("pm"));
		this.getCommand("pluginmanager").setTabCompleter(new PluginManager_TabComplete());
	}
	
}
