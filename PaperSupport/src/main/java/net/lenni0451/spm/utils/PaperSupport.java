package net.lenni0451.spm.utils;

import io.papermc.paper.plugin.entrypoint.Entrypoint;
import io.papermc.paper.plugin.entrypoint.EntrypointHandler;
import io.papermc.paper.plugin.provider.type.PluginFileType;
import io.papermc.paper.plugin.storage.ProviderStorage;
import net.lenni0451.spm.storage.SingularRuntimePluginProviderStorage;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.jar.JarFile;

public class PaperSupport {

    public static Plugin loadPlugin(final File targetFile) throws Exception {
        Class<?> RuntimePluginEntrypointHandler = Class.forName("io.papermc.paper.plugin.manager.RuntimePluginEntrypointHandler");

        Constructor<?> constructor = RuntimePluginEntrypointHandler.getDeclaredConstructor(ProviderStorage.class);
        constructor.setAccessible(true);
        SingularRuntimePluginProviderStorage storage = new SingularRuntimePluginProviderStorage();
        EntrypointHandler handler = (EntrypointHandler) constructor.newInstance(storage);

        JarFile file = new JarFile(targetFile);
        PluginFileType<?, ?> type = PluginFileType.guessType(file);
        type.register(handler, file, targetFile.toPath());

        handler.enter(Entrypoint.PLUGIN);
        Method getProviderStorage = RuntimePluginEntrypointHandler.getDeclaredMethod("getPluginProviderStorage");
        getProviderStorage.setAccessible(true);
        SingularRuntimePluginProviderStorage providerStorage = (SingularRuntimePluginProviderStorage) getProviderStorage.invoke(handler);
        return providerStorage.getSingleLoaded().get();
    }

}
