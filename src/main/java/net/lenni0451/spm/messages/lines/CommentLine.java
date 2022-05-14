package net.lenni0451.spm.messages.lines;

import net.lenni0451.spm.messages.IMessagesLine;

public class CommentLine implements IMessagesLine {

    private final String comment;

    public CommentLine(final String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    @Override
    public String getLine() {
        return "# " + this.comment;
    }

}
