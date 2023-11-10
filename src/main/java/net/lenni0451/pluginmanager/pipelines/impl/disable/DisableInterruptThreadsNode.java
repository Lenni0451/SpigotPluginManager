package net.lenni0451.pluginmanager.pipelines.impl.disable;

import net.lenni0451.pluginmanager.Main;
import net.lenni0451.pluginmanager.pipelines.IPipelineNode;
import net.lenni0451.pluginmanager.pipelines.PipelineContext;
import net.lenni0451.pluginmanager.utils.ThreadUtils;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

import static net.lenni0451.pluginmanager.pipelines.PipelineConstants.DATA_PLUGIN;

public class DisableInterruptThreadsNode implements IPipelineNode<DisablePipelineNodes> {

    @Override
    public DisablePipelineNodes execute(PipelineContext context) {
        context.passAll();
        Plugin plugin = context.consume(DATA_PLUGIN);
        for (Thread thread : ThreadUtils.getAllThreadsFrom(plugin.getClass().getClassLoader())) {
            try {
                thread.interrupt();
                thread.join(2000);
                if (thread.isAlive()) {
                    Main.getInstance().getLogger().warning("Thread '" + thread.getName() + "' from plugin '" + plugin.getName() + "' is still alive after 2 seconds");
                }
            } catch (Throwable t) {
                Main.getInstance().getLogger().log(Level.SEVERE, "Exception trying to interrupt thread '" + thread.getName() + "' from plugin '" + plugin.getName() + "'", t);
            }
        }
        return DisablePipelineNodes.DISABLE_PLUGIN;
    }

}
