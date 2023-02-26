package net.lenni0451.pluginmanager.pipelines.impl.enable;

import net.lenni0451.pluginmanager.pipelines.IPipelineNode;
import net.lenni0451.pluginmanager.pipelines.PipelineContext;

import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_NEXT_STEP;
import static net.lenni0451.pluginmanager.pipelines.impl.enable.EnablePipelineNodes.DISABLE_PLUGIN;
import static net.lenni0451.pluginmanager.pipelines.impl.enable.EnablePipelineNodes.GET_PLUGIN_MANAGER;

public class EnableInitNode implements IPipelineNode<EnablePipelineNodes> {

    @Override
    public EnablePipelineNodes execute(PipelineContext context) {
        context.passAll();
        context.pass(DATA_NEXT_STEP, DISABLE_PLUGIN);
        return GET_PLUGIN_MANAGER;
    }

}
