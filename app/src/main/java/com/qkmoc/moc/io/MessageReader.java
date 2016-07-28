package com.qkmoc.moc.io;

import java.io.IOException;
import java.io.InputStream;


public class MessageReader {
    private InputStream in;

    public MessageReader(InputStream in) {
        this.in = in;
    }

    public String read() throws IOException {
        byte[] buffer=new byte[1024];
        in.read(buffer);//读取

        String s =new String(buffer);
        return s;
    }
}
