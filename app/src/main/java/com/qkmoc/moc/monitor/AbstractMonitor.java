package com.qkmoc.moc.monitor;

import android.content.Context;

import com.qkmoc.moc.io.MessageWritable;

abstract public class AbstractMonitor extends Thread {
    Context context;
    MessageWritable writer;

    public AbstractMonitor(Context context, MessageWritable writer) {
        this.context = context;
        this.writer = writer;
    }

    public void peek() {
        peek(writer);
    }

    abstract public void peek(MessageWritable writer);
}
