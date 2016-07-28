package com.qkmoc.moc.query;

import android.content.Context;

abstract public class AbstractResponder {
    Context context;

    protected AbstractResponder(Context context) {
        this.context = context;
    }

    abstract public String respond(String message);
    abstract public void cleanup();
}
