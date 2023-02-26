package net.lenni0451.pluginmanager.pipelines;

import net.lenni0451.pluginmanager.pipelines.impl.disable.DisablePipelineNodes;
import net.lenni0451.pluginmanager.pipelines.impl.disable.DisablePluginNode;
import net.lenni0451.pluginmanager.pipelines.impl.disable.InitNode;
import net.lenni0451.pluginmanager.pipelines.shared.GetPluginManagerNode;
import org.bukkit.plugin.Plugin;

import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_PLUGIN;

public class PipelineManager {

    private final Pipeline<DisablePipelineNodes> disable = new Pipeline<>("disable", DisablePipelineNodes.INIT, registry -> {
        registry.accept(DisablePipelineNodes.INIT, new InitNode());
        registry.accept(DisablePipelineNodes.GET_PLUGIN_MANAGER, new GetPluginManagerNode<>());
        registry.accept(DisablePipelineNodes.DISABLE_PLUGIN, new DisablePluginNode());
    });

    public void disable(final Plugin plugin) {
        this.disable.execute(DATA_PLUGIN, plugin);
    }

}
