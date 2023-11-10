package net.lenni0451.pluginmanager.pipelines.impl.enable;

import net.lenni0451.pluginmanager.pipelines.IPipelineNode;
import net.lenni0451.pluginmanager.pipelines.PipelineContext;
import org.bukkit.plugin.Plugin;

import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_NEXT_STEP;
import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_PLUGIN;
import static net.lenni0451.pluginmanager.pipelines.impl.enable.EnablePipelineNodes.ENABLE_PLUGIN;
import static net.lenni0451.pluginmanager.pipelines.impl.enable.EnablePipelineNodes.GET_PLUGIN_MANAGER;

public class EnableInitNode implements IPipelineNode<EnablePipelineNodes> {

    @Override
    public EnablePipelineNodes execute(PipelineContext context) {
        context.passAll();
        Plugin plugin = context.consume(DATA_PLUGIN);
        if (plugin.isEnabled()) {
            return null;
        } else {
            context.pass(DATA_NEXT_STEP, ENABLE_PLUGIN);
            return GET_PLUGIN_MANAGER;
        }
    }

}
