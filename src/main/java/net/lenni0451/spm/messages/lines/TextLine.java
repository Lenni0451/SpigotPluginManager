package net.lenni0451.spm.messages.lines;

import net.lenni0451.spm.messages.IMessagesLine;

public class TextLine implements IMessagesLine {

    private final String text;

    public TextLine(final String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String getLine() {
        return this.text;
    }

}
