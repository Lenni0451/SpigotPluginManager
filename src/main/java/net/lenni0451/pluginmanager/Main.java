package net.lenni0451.pluginmanager;

import net.lenni0451.pluginmanager.i18n.TranslationsConfig;
import net.lenni0451.pluginmanager.pipelines.PipelineManager;
import net.lenni0451.pluginmanager.ui.ScreenManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }


    private ScreenManager screenManager;
    private PipelineManager pipelineManager;

    public Main() {
        instance = this;
    }

    @Override
    public void onEnable() {
        TranslationsConfig.load();
        this.screenManager = new ScreenManager();
        this.pipelineManager = new PipelineManager();
    }

    @Override
    public void onDisable() {
        this.screenManager.close();
    }

    public ScreenManager getScreenManager() {
        return this.screenManager;
    }

    public PipelineManager getPipelineManager() {
        return this.pipelineManager;
    }

}
