package net.Lenni0451.SpigotPluginManager.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PluginUtils {

    /**
     * @return The plugin directory
     */
    public File getPluginsDirectory() {
        return new File(".", "plugins");
    }

    /**
     * @return The current PluginManager instance
     */
    public PluginManager getPluginManager() {
        return Bukkit.getPluginManager();
    }

    /**
     * @return The current PluginLoader instance
     */
    public PluginLoader getPluginLoader() {
        return net.Lenni0451.SpigotPluginManager.PluginManager.getInstance().getPluginLoader();
    }

    /**
     * @return The array of all loaded plugins
     */
    public Plugin[] getPlugins() {
        return Bukkit.getPluginManager().getPlugins();
    }

    /**
     * Get a plugin by its name
     *
     * @param name The name of the plugin you want to get
     * @return An optional of the plugin instance of the plugin
     */
    public Optional<Plugin> getPlugin(final String name) {
        return Arrays.stream(this.getPlugins()).filter(plugin -> plugin.getName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * @param name The name of the plugin you want to check if its loaded
     * @return If the plugin is loaded
     */
    public boolean isPluginLoaded(final String name) {
        return this.getPlugin(name).isPresent();
    }

    /**
     * @param name The name of the plugin you want to check if its enabled
     * @return If the plugin is enabled
     */
    public boolean isPluginEnabled(final String name) {
        return this.getPlugin(name).map(Plugin::isEnabled).orElse(false);
    }

    /**
     * Enable a plugin by its name
     *
     * @param name The name of the plugin you want to enable
     * @return If the plugin could be enabled (false if already enabled)
     */
    public boolean enablePlugin(final String name) {
        return this.getPlugin(name).filter(this::enablePlugin).isPresent();
    }

    /**
     * Enable a plugin by its instance
     *
     * @param plugin The plugin you want to enable
     * @return If the plugin could be enabled (false if already enabled)
     */
    public boolean enablePlugin(final Plugin plugin) {
        if (plugin.isEnabled()) {
            return false;
        } else {
            this.getPluginManager().enablePlugin(plugin);
            return true;
        }
    }

    /**
     * Disable a plugin by its name
     *
     * @param name The name of the plugin you want to disable
     * @return if the plugin could be disabled (false if already disabled)
     */
    public boolean disablePlugin(final String name) {
        return this.getPlugin(name).filter(this::disablePlugin).isPresent();
    }

    /**
     * Disable a plugin by its instance
     *
     * @param plugin The plugin you want to disable
     * @return if the plugin could be disabled (false if already disabled)
     */
    public boolean disablePlugin(final Plugin plugin) {
        if (!plugin.isEnabled()) {
            return false;
        } else {
            for (Thread thread : ThreadUtils.getAllThreadsFromClassLoader(plugin.getClass().getClassLoader())) {
                try {
                    thread.interrupt();
                    thread.join(2000);
                    if (thread.isAlive()) thread.stop();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            this.getPluginManager().disablePlugin(plugin);
            return true;
        }
    }


    /**
     * Reload the config of a plugin by its name
     *
     * @param name The name of the plugin you want the config of to be reloaded
     */
    public void reloadConfig(final String name) {
        this.getPlugin(name).ifPresent(this::reloadConfig);
    }

    /**
     * Reload the config of a plugin by its instance
     *
     * @param plugin The plugin you want the config of to be reloaded
     */
    public void reloadConfig(final Plugin plugin) {
        plugin.reloadConfig();
    }


    /**
     * Reload a plugin by unloading it and loading it again
     *
     * @param plugin The plugin you want to reload
     * @return The new loaded instance of the plugin
     */
    public Plugin reloadPlugin(final Plugin plugin) {
        this.unloadPlugin(plugin);
        return this.loadPlugin(plugin);
    }


    /**
     * Load a plugin by its old instance
     *
     * @param plugin The old plugin instance
     * @return The plugin instance if loaded
     */
    public Plugin loadPlugin(final Plugin plugin) {
        return this.loadPlugin(plugin.getName());
    }

    /**
     * Load a plugin by its name
     *
     * @param name The plugin name you want to load
     * @return The plugin instance if loaded
     * @throws IllegalStateException If something went wrong
     */
    public Plugin loadPlugin(final String name) {
        AtomicReference<File> targetFile = new AtomicReference<>(new File(this.getPluginsDirectory(), name + (name.toLowerCase().endsWith(".jar") ? "" : ".jar")));
        Plugin targetPlugin;

        if (!targetFile.get().exists()) {
            Arrays.stream(this.getPluginsDirectory().listFiles())
                    .filter(file -> file.getName().toLowerCase().endsWith(".jar") || (!file.getName().toLowerCase().endsWith(".jar") && !net.Lenni0451.SpigotPluginManager.PluginManager.getInstance().getConfig().getBoolean("IgnoreNonJarPlugins")))
                    .filter(file -> {
                        try {
                            PluginDescriptionFile desc = this.getPluginLoader().getPluginDescription(file);
                            return desc.getName().equalsIgnoreCase(name);
                        } catch (InvalidDescriptionException ignored) {
                            //Here we do not need to care about invalid plugin ymls
                        }
                        return false;
                    }).findAny().ifPresent(targetFile::set);
        }
        if (targetFile.get() == null) {
            throw new IllegalStateException("Plugin file not found");
        }

        try {
            targetPlugin = this.getPluginLoader().loadPlugin(targetFile.get());
        } catch (UnknownDependencyException e) {
            throw new IllegalStateException("Missing Dependency");
        } catch (InvalidPluginException e) {
            throw new IllegalStateException("Invalid plugin file");
        }

        targetPlugin.onLoad();
        this.enablePlugin(targetPlugin);
        try { //Get plugins list and add plugin if not already
            Field f = this.getPluginManager().getClass().getDeclaredField("plugins");
            f.setAccessible(true);
            List<Plugin> plugins = (List<Plugin>) f.get(this.getPluginManager());
            if (!plugins.contains(targetPlugin)) {
                plugins.add(targetPlugin);
            }
        } catch (Throwable e) {
            e.printStackTrace(); //We maybe even want to see why the plugin could not be added
            throw new IllegalStateException("Unable to add to plugin list");
        }
        return targetPlugin;
    }


    /**
     * Unload a plugin by its name
     *
     * @param name The name of the plugin you want to unload
     */
    public void unloadPlugin(final String name) {
        this.getPlugin(name).ifPresent(this::unloadPlugin);
    }

    /**
     * Unload a plugin by its instance
     *
     * @param plugin The plugin you want to unload
     * @throws IllegalStateException If the plugin could not be unloaded
     */
    public void unloadPlugin(final Plugin plugin) {
        this.disablePlugin(plugin);

        PluginManager pluginManager = this.getPluginManager();

        List<Plugin> plugins;
        Map<String, Plugin> lookupNames;
        SimpleCommandMap commandMap;
        Map<String, Command> knownCommands;
        Map<Event, SortedSet<RegisteredListener>> listeners;

        try { //Get plugins list
            Field f = pluginManager.getClass().getDeclaredField("plugins");
            f.setAccessible(true);
            plugins = (List<Plugin>) f.get(pluginManager);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to get plugins list");
        }
        try { //Get lookup names
            Field f = pluginManager.getClass().getDeclaredField("lookupNames");
            f.setAccessible(true);
            lookupNames = (Map<String, Plugin>) f.get(pluginManager);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to get lookup names");
        }
        try { //Get command map
            Field f = pluginManager.getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (SimpleCommandMap) f.get(pluginManager);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to get command map");
        }
        try { //Get known commands
            Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
            f.setAccessible(true);
            knownCommands = (Map<String, Command>) f.get(commandMap);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to get known commands");
        }
        try {
            Field f = pluginManager.getClass().getDeclaredField("listeners");
            f.setAccessible(true);
            listeners = (Map<Event, SortedSet<RegisteredListener>>) f.get(pluginManager);
        } catch (Throwable e) {
            listeners = null;
        }

        plugins.remove(plugin);
        lookupNames.remove(plugin.getName());
        { //Remove plugin commands
            Iterator<Map.Entry<String, Command>> iterator = knownCommands.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Command> entry = iterator.next();
                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand command = (PluginCommand) entry.getValue();
                    if (command.getPlugin().equals(plugin)) {
                        iterator.remove();
                    }
                }
            }
        }
        if (listeners != null) {
            for (Set<RegisteredListener> registeredListeners : listeners.values()) {
                registeredListeners.removeIf(registeredListener -> registeredListener.getPlugin().equals(plugin));
            }
        }

        if (plugin.getClass().getClassLoader() instanceof URLClassLoader) {
            URLClassLoader classLoader = (URLClassLoader) plugin.getClass().getClassLoader();

            try {
                classLoader.close();
            } catch (Throwable t) {
                throw new IllegalStateException("Unable to close the class loader");
            }
        } else {
            Logger.sendConsole("§cIt seems like spigot no longer uses URLClassLoader.");
            Logger.sendConsole("§cPlease report this to the plugin dev!");
        }

        System.gc(); //Hopefully remove all leftover plugin classes and references
    }


    /**
     * @return All plugins in the load order
     */
    public List<Plugin> getPluginsByLoadOrder() {
        List<String> ignoredPlugins = net.Lenni0451.SpigotPluginManager.PluginManager.getInstance().getConfig().getStringList("IgnoredPlugins");
        List<Plugin> plugins = new ArrayList<>();

        Arrays.stream(this.getPlugins()).forEach(plugin -> {
            if (!ignoredPlugins.contains(plugin.getName())) {
                plugins.add(plugin);
            }
        });


        for (int i = 0; i < plugins.size(); i++) {
            Plugin plugin = plugins.get(i);

            List<String> deps = new ArrayList<>();
            deps.addAll(plugin.getDescription().getDepend());
            deps.addAll(plugin.getDescription().getSoftDepend());
            {
                for (Plugin reloadPlugin : plugins) {
                    if (reloadPlugin.getDescription().getLoadBefore().contains(plugin.getName())) {
                        if (!deps.contains(reloadPlugin.getName()))
                            deps.add(reloadPlugin.getName());
                    }
                }
            }
            if (!deps.isEmpty()) {
                for (String dependName : deps) {
                    Optional<Plugin> depend = this.getPlugin(dependName);
                    if (depend.isPresent()) {
                        int dependPos = plugins.indexOf(depend.get());
                        int pluginPos = plugins.indexOf(plugin);

                        if (dependPos > pluginPos) {
                            plugins.remove(pluginPos);
                            plugins.add(dependPos + 1, plugin);

                            i = 0;
                        }
                    }
                }
            }
        }

        return plugins;
    }


    /**
     * Get a list with all registered commands and aliases from a plugin
     * <br>This only can get all commands registered using the standard Bukkit way
     * <br>If a plugin uses some other way of registering commands they will not show up
     *
     * @param plugin The plugin of which you want the commands from
     * @return The list of commands a plugin has registered
     */
    public List<String> getCommands(Plugin plugin) {
        List<String> commands = new ArrayList<>();
        Map<String, List<String>> aliases = new HashMap<>();

        Map<String, Map<String, Object>> commandMap = plugin.getDescription().getCommands();
        if (commandMap == null) return commands;
        for (String command : commandMap.keySet()) {
            commands.add(command.toLowerCase());

            if (commandMap.get(command).containsKey("aliases")) {
                Object aliasesObject = commandMap.get(command).get("aliases");
                List<String> commandAliases = new ArrayList<>();
                aliases.put(command, commandAliases);

                if (aliasesObject instanceof String) {
                    commandAliases.add(aliasesObject.toString().toLowerCase());
                } else if (aliasesObject instanceof Collection) {
                    for (Object alias : (Collection<?>) aliasesObject) {
                        commandAliases.add(alias.toString().toLowerCase());
                    }
                } else if (aliasesObject instanceof String[]) {
                    for (String alias : (String[]) aliasesObject) {
                        commandAliases.add(alias.toLowerCase());
                    }
                }
            }
        }

        Collections.sort(commands);
        for (int i = 0; i < commands.size(); i++) {
            String command = commands.get(i);
            List<String> commandAliases = aliases.get(command);
            if (commandAliases != null) {
                Collections.sort(commandAliases);
                for (int i2 = commandAliases.size() - 1; i2 >= 0; i2--) {
                    String alias = commandAliases.get(i2);
                    commands.add(i + 1, " " + alias);
                }
            }
        }

        return commands;
    }


    /**
     * Get the location of the plugin jar
     *
     * @param plugin The plugin you want the location from
     * @return An optional with the File if found
     */
    public Optional<File> getPluginFile(Plugin plugin) {
        try {
            Method method = JavaPlugin.class.getDeclaredMethod("getFile");
            method.setAccessible(true);
            return Optional.of((File) method.invoke(plugin));
        } catch (Throwable e) {
            for (File pluginFile : this.getPluginsDirectory().listFiles()) {
                if (pluginFile.isFile()) {
                    if (!pluginFile.getName().toLowerCase().endsWith(".jar") && net.Lenni0451.SpigotPluginManager.PluginManager.getInstance().getConfig().getBoolean("IgnoreNonJarPlugins")) {
                        continue;
                    }

                    try {
                        PluginDescriptionFile desc = this.getPluginLoader().getPluginDescription(pluginFile);
                        if (desc.getName().equalsIgnoreCase(plugin.getName())) {
                            return Optional.of(pluginFile);
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        return Optional.empty();
    }

}
