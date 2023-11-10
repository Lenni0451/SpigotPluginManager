package net.lenni0451.pluginmanager.pipelines;

import net.lenni0451.pluginmanager.pipelines.impl.disable.DisableInitNode;
import net.lenni0451.pluginmanager.pipelines.impl.disable.DisablePipelineNodes;
import net.lenni0451.pluginmanager.pipelines.impl.disable.DisablePluginNode;
import net.lenni0451.pluginmanager.pipelines.impl.enable.EnableInitNode;
import net.lenni0451.pluginmanager.pipelines.impl.enable.EnablePipelineNodes;
import net.lenni0451.pluginmanager.pipelines.impl.enable.EnablePluginNode;
import net.lenni0451.pluginmanager.pipelines.shared.GetPluginManagerNode;
import org.bukkit.plugin.Plugin;

import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_PLUGIN;

public class PipelineManager {

    private final Pipeline<EnablePipelineNodes> enable = new Pipeline<>("enable", EnablePipelineNodes.INIT, registry -> {
        registry.accept(EnablePipelineNodes.INIT, new EnableInitNode());
        registry.accept(EnablePipelineNodes.GET_PLUGIN_MANAGER, new GetPluginManagerNode<>());
        registry.accept(EnablePipelineNodes.ENABLE_PLUGIN, new EnablePluginNode());
    });
    private final Pipeline<DisablePipelineNodes> disable = new Pipeline<>("disable", DisablePipelineNodes.INIT, registry -> {
        registry.accept(DisablePipelineNodes.INIT, new DisableInitNode());
        registry.accept(DisablePipelineNodes.GET_PLUGIN_MANAGER, new GetPluginManagerNode<>());
        registry.accept(DisablePipelineNodes.DISABLE_PLUGIN, new DisablePluginNode());
    });

    public void enable(final Plugin plugin) {
        this.enable.execute(DATA_PLUGIN, plugin);
    }

    public void disable(final Plugin plugin) {
        this.disable.execute(DATA_PLUGIN, plugin);
    }

}
