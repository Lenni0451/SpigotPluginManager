package net.lenni0451.pluginmanager.pipelines.exception;

public class MissingPipelineDataException extends RuntimeException {

    public MissingPipelineDataException(final String key) {
        super("The pipeline data with key '" + key + "' is missing");
    }

}
