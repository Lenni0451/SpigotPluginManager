package net.lenni0451.pluginmanager.pipelines.shared;

import net.lenni0451.pluginmanager.pipelines.IPipelineNode;
import net.lenni0451.pluginmanager.pipelines.PipelineContext;
import org.bukkit.Bukkit;

import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_NEXT_STEP;
import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_PLUGIN_MANAGER;

public class GetPluginManagerNode<T> implements IPipelineNode<T> {

    @Override
    public T execute(PipelineContext context) {
        T nextStep = context.consume(DATA_NEXT_STEP);
        context.passAll();
        context.pass(DATA_PLUGIN_MANAGER, Bukkit.getPluginManager());
        return nextStep;
    }

}
