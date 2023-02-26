package net.lenni0451.pluginmanager.pipelines;

import net.lenni0451.pluginmanager.pipelines.exception.MissingPipelineDataException;

import java.util.HashMap;
import java.util.Map;

public class PipelineContext {

    final Map<String, Object> data = new HashMap<>();
    final Map<String, Object> passedData = new HashMap<>();

    public void require(final String key) {
        if (!this.data.containsKey(key)) throw new MissingPipelineDataException(key);
    }

    @Deprecated
    public <T> T get(final String key) {
        this.require(key);
        return (T) this.data.get(key);
    }

    public <T> T consume(final String key) {
        this.require(key);
        return (T) this.data.remove(key);
    }

    public void pass(final String key) {
        this.passedData.put(key, this.data.get(key));
    }

    public void pass(final String key, final Object value) {
        this.passedData.put(key, value);
    }

    public void passAll() {
        this.passedData.putAll(this.data);
    }

}
