package net.lenni0451.spm.utils;

import net.lenni0451.spm.messages.I18n;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginUtils {

    /**
     * Get the {@code plugins} directory
     *
     * @return The {@link File}
     */
    public File getPluginsDirectory() {
        return new File(".", "plugins");
    }

    /**
     * Get the update directory for plugins
     *
     * @return The {@link File}
     */
    public File getUpdateDirectory() {
        return new File(this.getPluginsDirectory(), "update");
    }

    /**
     * Get the {@link PluginManager} instance of the server
     *
     * @return The {@link PluginManager} instance
     */
    public PluginManager getPluginManager() {
        return Bukkit.getPluginManager();
    }

    /**
     * Get the {@link PluginDescriptionFile} of a plugin jar
     *
     * @param file The plugin jar
     * @return The {@link PluginDescriptionFile}
     * @throws InvalidDescriptionException If the plugin.yml is invalid
     */
    public PluginDescriptionFile getPluginDescription(final File file) throws InvalidDescriptionException {
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("plugin.yml");
            if (entry == null) throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain plugin.yml"));

            try (InputStream is = jar.getInputStream(entry)) {
                return new PluginDescriptionFile(is);
            }
        } catch (Throwable t) {
            throw new InvalidDescriptionException(t);
        }
    }

    /**
     * Get an array of all loaded {@link Plugin}s
     *
     * @return All loaded {@link Plugin}s
     */
    public Plugin[] getPlugins() {
        return this.getPluginManager().getPlugins();
    }

    /**
     * Get a {@link Plugin} instance by its name
     *
     * @param name The name of the {@link Plugin}
     * @return The {@link Plugin} instance or {@link Optional#empty()} if not found
     */
    public Optional<Plugin> getPlugin(final String name) {
        return Arrays.stream(this.getPlugins()).filter(plugin -> plugin.getName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Check if a {@link Plugin} is loaded by its name
     *
     * @param name The name of the {@link Plugin}
     * @return {@code true} if the {@link Plugin} is loaded
     */
    public boolean isPluginLoaded(final String name) {
        return this.getPlugin(name).isPresent();
    }

    /**
     * Check if a {@link Plugin} is enabled by its name
     *
     * @param name The name of the {@link Plugin}
     * @return {@code true} if the {@link Plugin} is enabled
     */
    public boolean isPluginEnabled(final String name) {
        return this.getPlugin(name).map(Plugin::isEnabled).orElse(false);
    }

    /**
     * Enable a {@link Plugin} by its name
     *
     * @param name The name of the {@link Plugin}
     * @return {@code true} if the {@link Plugin} was enabled successfully
     */
    public boolean enablePlugin(final String name) {
        return this.getPlugin(name).filter(this::enablePlugin).isPresent();
    }

    /**
     * Enable a {@link Plugin} by its instance
     *
     * @param plugin The {@link Plugin} instance
     * @return {@code true} if the {@link Plugin} was enabled successfully
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
     * Disable a {@link Plugin} by its name
     *
     * @param name The name of the {@link Plugin}
     * @return {@code true} if the {@link Plugin} was disabled successfully
     */
    public boolean disablePlugin(final String name) {
        return this.getPlugin(name).filter(this::disablePlugin).isPresent();
    }

    /**
     * Disable a {@link Plugin} by its instance
     *
     * @param plugin The {@link Plugin} instance
     * @return {@code true} if the {@link Plugin} was disabled successfully
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
     * Reload the config of a {@link Plugin} by its name
     *
     * @param name The name of the {@link Plugin}
     */
    public void reloadConfig(final String name) {
        this.getPlugin(name).ifPresent(this::reloadConfig);
    }

    /**
     * Reload the config of a {@link Plugin} by its instance
     *
     * @param plugin The {@link Plugin} instance
     */
    public void reloadConfig(final Plugin plugin) {
        plugin.reloadConfig();
    }


    /**
     * Reload a {@link Plugin} by its name<br>
     * This first unloads the {@link Plugin} and then loads it again
     *
     * @param plugin The {@link Plugin} instance
     * @return The new {@link Plugin} instance
     * @throws IllegalStateException If anything goes wrong
     */
    public Plugin reloadPlugin(final Plugin plugin) {
        this.unloadPlugin(plugin);
        return this.loadPlugin(plugin);
    }


    /**
     * Load a {@link Plugin} by its instance<br>
     * See {@link #loadPlugin(String)} for more information
     *
     * @param plugin The {@link Plugin} instance
     * @return The new {@link Plugin} instance
     * @throws IllegalStateException If anything goes wrong
     */
    public Plugin loadPlugin(final Plugin plugin) {
        return this.loadPlugin(plugin.getName());
    }

    /**
     * Load a {@link Plugin} by its name<br>
     * This tries to get the {@link Plugin} {@link File} by name
     * and if not found scans all {@link PluginDescriptionFile}s for the name
     *
     * @param name The name of the {@link Plugin}
     * @return The new {@link Plugin} instance
     * @throws IllegalStateException If anything goes wrong
     */
    public Plugin loadPlugin(final String name) {
        AtomicReference<File> targetFile = new AtomicReference<>(new File(this.getPluginsDirectory(), name + (name.toLowerCase().endsWith(".jar") ? "" : ".jar")));
        Plugin targetPlugin;

        if (!targetFile.get().exists()) {
            Arrays.stream(FileUtils.listFiles(this.getPluginsDirectory()))
                    .filter(file -> file.getName().toLowerCase().endsWith(".jar") || (!file.getName().toLowerCase().endsWith(".jar") && !net.lenni0451.spm.PluginManager.getInstance().getConfig().getBoolean("IgnoreNonJarPlugins")))
                    .filter(file -> {
                        try {
                            PluginDescriptionFile desc = this.getPluginDescription(file);
                            return desc.getName().equalsIgnoreCase(name);
                        } catch (InvalidDescriptionException ignored) {
                            //Here we do not need to care about invalid plugin ymls
                        }
                        return false;
                    }).findAny().ifPresent(targetFile::set);
        }
        if (targetFile.get() == null) {
//            throw new IllegalStateException("Plugin file not found");
            throw new IllegalStateException(I18n.t("pm.pluginutils.loadPlugin.fileNotFound"));
        }
        this.updatePlugin(targetFile.get());

        try {
            targetPlugin = this.getPluginManager().loadPlugin(targetFile.get());
        } catch (UnknownDependencyException e) {
//            throw new IllegalStateException("Missing Dependency");
            throw new IllegalStateException(I18n.t("pm.pluginutils.loadPlugin.missingDependency"));
        } catch (InvalidPluginException e) {
//            throw new IllegalStateException("Invalid plugin file");
            throw new IllegalStateException(I18n.t("pm.pluginutils.loadPlugin.invalidPluginFile"));
        } catch (InvalidDescriptionException e) {
//            throw new IllegalStateException("Invalid plugin description");
            throw new IllegalStateException(I18n.t("pm.pluginutils.loadPlugin.invalidPluginDescription"));
        }

        targetPlugin.onLoad();
        this.enablePlugin(targetPlugin);
        try { //Get plugins list and add plugin if not already
            Field f = this.getPluginManager().getClass().getDeclaredField("plugins");
            f.setAccessible(true);
            List<Plugin> plugins = (List<Plugin>) f.get(this.getPluginManager());
            if (!plugins.contains(targetPlugin)) plugins.add(targetPlugin);
        } catch (Throwable e) {
            e.printStackTrace(); //We maybe even want to see why the plugin could not be added
//            throw new IllegalStateException("Unable to add to plugin list");
            throw new IllegalStateException(I18n.t("pm.pluginutils.loadPlugin.notAdded"));
        }
        try { //Synchronize the commands between client/server on newer versions
            Method syncCommands = Bukkit.getServer().getClass().getDeclaredMethod("syncCommands");
            syncCommands.setAccessible(true);
            syncCommands.invoke(Bukkit.getServer());
        } catch (Throwable ignored) {
        }
        return targetPlugin;
    }


    /**
     * Unload a {@link Plugin} by its name<br>
     * See {@link #unloadPlugin(Plugin)} for more information
     *
     * @param name The name of the {@link Plugin}
     * @throws IllegalStateException If anything goes wrong
     */
    public void unloadPlugin(final String name) {
        this.getPlugin(name).ifPresent(this::unloadPlugin);
    }

    /**
     * Unload a {@link Plugin} by its instance<br>
     * This closes the {@link URLClassLoader} of the {@link Plugin} which causes
     * the {@link Plugin} no longer being able to load new {@link Class}es
     *
     * @param plugin The {@link Plugin} instance
     * @throws IllegalStateException If anything goes wrong
     */
    public void unloadPlugin(final Plugin plugin) {
        this.disablePlugin(plugin);

        List<Plugin> plugins;
        Map<String, Plugin> lookupNames;
        SimpleCommandMap commandMap;
        Map<String, Command> knownCommands;
        Map<Event, SortedSet<RegisteredListener>> listeners;

        Object pluginContainer = this.getPluginManager();
        try {
            Field paperPluginManager = Bukkit.getServer().getClass().getDeclaredField("paperPluginManager");
            paperPluginManager.setAccessible(true);
            pluginContainer = paperPluginManager.get(Bukkit.getServer());

            Field instanceManager = pluginContainer.getClass().getDeclaredField("instanceManager");
            instanceManager.setAccessible(true);
            pluginContainer = instanceManager.get(pluginContainer);
        } catch (Throwable ignored) {
        }
        try { //Get plugins list
            Field f = pluginContainer.getClass().getDeclaredField("plugins");
            f.setAccessible(true);
            plugins = (List<Plugin>) f.get(pluginContainer);
        } catch (Throwable e) {
            e.printStackTrace();
//            throw new IllegalStateException("Unable to get plugins list");
            throw new IllegalStateException(I18n.t("pm.pluginutils.unloadPlugin.pluginListError"));
        }
        try { //Get lookup names
            Field f = pluginContainer.getClass().getDeclaredField("lookupNames");
            f.setAccessible(true);
            lookupNames = (Map<String, Plugin>) f.get(pluginContainer);
        } catch (Throwable e) {
            e.printStackTrace();
//            throw new IllegalStateException("Unable to get lookup names");
            throw new IllegalStateException(I18n.t("pm.pluginutils.unloadPlugin.lookupNamesError"));
        }
        try { //Get command map
            Field f = pluginContainer.getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (SimpleCommandMap) f.get(pluginContainer);
        } catch (Throwable e) {
            e.printStackTrace();
//            throw new IllegalStateException("Unable to get command map");
            throw new IllegalStateException(I18n.t("pm.pluginutils.unloadPlugin.commandMapError"));
        }
        try { //Get known commands
            Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
            f.setAccessible(true);
            knownCommands = (Map<String, Command>) f.get(commandMap);
        } catch (Throwable e) {
            e.printStackTrace();
//            throw new IllegalStateException("Unable to get known commands");
            throw new IllegalStateException(I18n.t("pm.pluginutils.unloadPlugin.knownCommandsError"));
        }
        try {
            Field f = pluginContainer.getClass().getDeclaredField("listeners");
            f.setAccessible(true);
            listeners = (Map<Event, SortedSet<RegisteredListener>>) f.get(pluginContainer);
        } catch (Throwable e) {
            listeners = null;
        }

        plugins.remove(plugin);
        lookupNames.remove(plugin.getName());
        lookupNames.remove(plugin.getName().toLowerCase());
        { //Remove plugin commands
            Iterator<Map.Entry<String, Command>> iterator = knownCommands.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Command> entry = iterator.next();
                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand command = (PluginCommand) entry.getValue();
                    if (command.getPlugin().equals(plugin)) iterator.remove();
                }
            }
        }
        if (listeners != null) {
            for (Set<RegisteredListener> registeredListeners : listeners.values()) {
                registeredListeners.removeIf(registeredListener -> registeredListener.getPlugin().equals(plugin));
            }
        }
        try {
            Class<?> entryPointHandler = Class.forName("io.papermc.paper.plugin.entrypoint.LaunchEntryPointHandler");
            Object instance = entryPointHandler.getDeclaredField("INSTANCE").get(null);
            Map<?, ?> storage = (Map<?, ?>) instance.getClass().getMethod("getStorage").invoke(instance);
            for (Object providerStorage : storage.values()) {
                Iterable<?> providers = (Iterable<?>) providerStorage.getClass().getMethod("getRegisteredProviders").invoke(providerStorage);
                Iterator<?> it = providers.iterator();
                while (it.hasNext()) {
                    Object provider = it.next();
                    Object meta = provider.getClass().getMethod("getMeta").invoke(provider);
                    String metaName = (String) meta.getClass().getMethod("getName").invoke(meta);
                    if (metaName.equals(plugin.getName())) it.remove();
                }
            }
        } catch (Throwable ignored) {
        }

        if (plugin.getClass().getClassLoader() instanceof URLClassLoader) {
            URLClassLoader classLoader = (URLClassLoader) plugin.getClass().getClassLoader();

            try {
                classLoader.close();
            } catch (Throwable t) {
//                throw new IllegalStateException("Unable to close the class loader");
                throw new IllegalStateException(I18n.t("pm.pluginutils.unloadPlugin.closeClassLoaderError"));
            }
        } else {
//            Logger.sendConsole("§cIt seems like spigot no longer uses URLClassLoader.");
//            Logger.sendConsole("§cPlease report this to the plugin dev!");
            for (String s : I18n.mt("pm.pluginutils.unloadPlugin.unknownClassLoader")) Logger.sendConsole(s);
        }

        try { //Synchronize the commands between client/server on newer versions
            Method syncCommands = Bukkit.getServer().getClass().getDeclaredMethod("syncCommands");
            syncCommands.setAccessible(true);
            syncCommands.invoke(Bukkit.getServer());
        } catch (Throwable ignored) {
        }
        System.gc(); //Hopefully remove all leftover plugin classes and references
    }


    /**
     * Get a {@link List} of all {@link Plugin}s sorted by their dependencies<br>
     * Loading all {@link Plugin}s in the correct order is important for the correct functioning of the {@link Plugin}s
     *
     * @return The {@link List}
     */
    public List<Plugin> getPluginsByLoadOrder() {
        List<String> ignoredPlugins = net.lenni0451.spm.PluginManager.getInstance().getConfig().getStringList("IgnoredPlugins");
        List<Plugin> plugins = new ArrayList<>();

        Arrays.stream(this.getPlugins()).forEach(plugin -> {
            if (!ignoredPlugins.contains(plugin.getName())) plugins.add(plugin);
        });


        for (int i = 0; i < plugins.size(); i++) {
            Plugin plugin = plugins.get(i);

            List<String> deps = new ArrayList<>();
            deps.addAll(plugin.getDescription().getDepend());
            deps.addAll(plugin.getDescription().getSoftDepend());
            {
                for (Plugin reloadPlugin : plugins) {
                    if (reloadPlugin.getDescription().getLoadBefore().contains(plugin.getName())) {
                        if (!deps.contains(reloadPlugin.getName())) deps.add(reloadPlugin.getName());
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
                            plugins.add(Math.min(dependPos + 1, plugins.size()), plugin);

                            i = 0;
                        }
                    }
                }
            }
        }

        return plugins;
    }


    /**
     * Get a {@link List} of all commands registered by {@link Plugin}s<br>
     * This only works if they are registered as {@link PluginCommand}s<br>
     * Anything using events or other methods to handle commands will not work
     *
     * @param plugin The instance of the {@link Plugin}
     * @return The {@link List}
     */
    public List<String> getCommands(final Plugin plugin) {
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
                    for (String alias : (String[]) aliasesObject) commandAliases.add(alias.toLowerCase());
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
     * Get the {@link File} of the {@link Plugin} jar<br>
     * This tries calling the {@link JavaPlugin#getFile()} method first<br>
     * If that fails, all {@link PluginDescriptionFile}s are searched for the name of the {@link Plugin}
     *
     * @param plugin The instance of the {@link Plugin}
     * @return The {@link File} of the {@link Plugin} jar or {@link Optional#empty()} if not found
     */
    public Optional<File> getPluginFile(final Plugin plugin) {
        try {
            Method method = JavaPlugin.class.getDeclaredMethod("getFile");
            method.setAccessible(true);
            return Optional.of((File) method.invoke(plugin));
        } catch (Throwable e) {
            for (File pluginFile : FileUtils.listFiles(this.getPluginsDirectory())) {
                if (pluginFile.isFile()) {
                    if (!pluginFile.getName().toLowerCase().endsWith(".jar") && net.lenni0451.spm.PluginManager.getInstance().getConfig().getBoolean("IgnoreNonJarPlugins")) {
                        continue;
                    }

                    try {
                        PluginDescriptionFile desc = this.getPluginDescription(pluginFile);
                        if (desc.getName().equalsIgnoreCase(plugin.getName())) return Optional.of(pluginFile);
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Update a {@link Plugin} from the updates folder
     *
     * @param pluginFile The {@link File} of the {@link Plugin}
     * @return {@code true} if the update was successful
     */
    public boolean updatePlugin(final File pluginFile) {
        final File updateFile = new File(this.getUpdateDirectory(), pluginFile.getName());
        //TODO: Do some more advanced checking (Load plugin.yml and compare name, author, ...)
        if (!updateFile.exists()) return false;
        if (pluginFile.exists() && !pluginFile.delete()) return false;
        return updateFile.renameTo(pluginFile);
    }

}
