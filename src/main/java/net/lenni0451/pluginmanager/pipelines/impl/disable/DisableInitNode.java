package net.lenni0451.pluginmanager.pipelines.impl.disable;

import net.lenni0451.pluginmanager.pipelines.IPipelineNode;
import net.lenni0451.pluginmanager.pipelines.PipelineContext;

import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_NEXT_STEP;
import static net.lenni0451.pluginmanager.pipelines.impl.disable.DisablePipelineNodes.GET_PLUGIN_MANAGER;
import static net.lenni0451.pluginmanager.pipelines.impl.disable.DisablePipelineNodes.INTERRUPT_THREADS;

public class DisableInitNode implements IPipelineNode<DisablePipelineNodes> {

    @Override
    public DisablePipelineNodes execute(PipelineContext context) {
        context.passAll();
        context.pass(DATA_NEXT_STEP, INTERRUPT_THREADS);
        return GET_PLUGIN_MANAGER;
    }

}
