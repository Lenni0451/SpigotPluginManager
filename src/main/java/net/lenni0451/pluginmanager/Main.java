package net.lenni0451.pluginmanager;

import net.lenni0451.pluginmanager.pipelines.PipelineManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }


    private PipelineManager pipelineManager;

    public Main() {
        instance = this;

    }

    @Override
    public void onEnable() {
        this.pipelineManager = new PipelineManager();
    }

    @Override
    public void onDisable() {
    }

    public PipelineManager getPipelineManager() {
        return this.pipelineManager;
    }

}
