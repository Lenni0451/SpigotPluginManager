package net.lenni0451.pluginmanager;

import com.tchristofferson.configupdater.ConfigUpdater;
import net.lenni0451.pluginmanager.i18n.I18n;
import net.lenni0451.pluginmanager.i18n.TranslationsConfig;
import net.lenni0451.pluginmanager.pipelines.PipelineManager;
import net.lenni0451.pluginmanager.ui.ScreenManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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
        if (!this.loadConfig()) {
            this.setEnabled(false);
            return;
        }

        this.screenManager = new ScreenManager();
        this.pipelineManager = new PipelineManager();
    }

    private boolean loadConfig() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.saveDefaultConfig();
        } else {
            try {
                ConfigUpdater.update(this, "config.yml", configFile);
            } catch (IOException e) {
                this.getLogger().severe(I18n.Config.FailedToLoad);
                this.getLogger().severe(I18n.Config.TryDeletingConfig);
                return false;
            }
            this.reloadConfig();
        }
        return true;
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
