package com.qkmoc.moc.io;


public class MessageRouter {
    private MessageWriter writer;

    public MessageRouter(MessageWriter writer) {
        this.writer = writer;
    }

    public boolean route(String str) {
        writer.write(str);
        return true;
    }

    public void cleanup() {

    }
}
