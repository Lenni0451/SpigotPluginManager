package net.lenni0451.pluginmanager.pipelines;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Pipeline<T> {

    private final String name;
    private final T initialNode;
    private final Map<T, IPipelineNode<T>> nodes;

    public Pipeline(final String name, final T initialNode, final Consumer<BiConsumer<T, IPipelineNode<T>>> registerNodes) {
        this.name = name;
        this.initialNode = initialNode;
        this.nodes = new HashMap<>();

        registerNodes.accept(this.nodes::put);
    }

    public String getName() {
        return this.name;
    }

    public Map<T, IPipelineNode<T>> getNodes() {
        return Collections.unmodifiableMap(this.nodes);
    }

    public void execute(Object... data) {
        if (data.length % 2 != 0) throw new IllegalArgumentException("The data array must have an even length (key -> value)");
        Map<String, Object> passedData = new HashMap<>();
        for (int i = 0; i < data.length; i += 2) {
            Object key = data[i];
            if (!(key instanceof String)) throw new IllegalArgumentException("The key at index " + i + " is not a string");
            Object value = data[i + 1];

            passedData.put((String) key, value);
        }

        T nextNode = this.initialNode;
        do {
            PipelineContext context = new PipelineContext();
            context.data.putAll(passedData);
            passedData.clear();

            nextNode = this.nodes.get(nextNode).execute(context);
            passedData.putAll(context.passedData);
        } while (nextNode != null);
    }

}
