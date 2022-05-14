package net.lenni0451.spm.messages.lines;

import net.lenni0451.spm.messages.IMessagesLine;

public class TranslationLine implements IMessagesLine {

    private final String key;
    private final String value;

    public TranslationLine(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String getLine() {
        return this.key + "=" + this.value;
    }

}
