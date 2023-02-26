package net.lenni0451.pluginmanager.pipelines.impl.disable;

import net.lenni0451.pluginmanager.pipelines.IPipelineNode;
import net.lenni0451.pluginmanager.pipelines.PipelineContext;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_PLUGIN;
import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_PLUGIN_MANAGER;

public class DisablePluginNode implements IPipelineNode<DisablePipelineNodes> {

    @Override
    public DisablePipelineNodes execute(PipelineContext context) {
        Plugin plugin = context.consume(DATA_PLUGIN);
        PluginManager pluginManager = context.consume(DATA_PLUGIN_MANAGER);

        pluginManager.disablePlugin(plugin);
        return null;
    }

}
