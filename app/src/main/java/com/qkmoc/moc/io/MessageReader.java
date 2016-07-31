package com.qkmoc.moc.io;

import java.io.IOException;
import java.io.InputStream;


public class MessageReader {
    private InputStream in;

    public MessageReader(InputStream in) {
        this.in = in;
    }

    public String read() {
        String s = null;
        int len = 0;
        try {
            while (len == 0) {
                len = in.available();
            }
            if (len != 0) {
                byte[] buffer = new byte[len];
                in.read(buffer);//读取
                s = new String(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("str:::::::" + e);
        }

        return s;
    }
}
