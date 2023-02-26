package net.lenni0451.pluginmanager.pipelines;

public interface IPipelineNode<T> {

    T execute(final PipelineContext context);

}
