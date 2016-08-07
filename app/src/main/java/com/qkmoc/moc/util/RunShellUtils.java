package com.qkmoc.moc.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/8/6 20:08
 */
public class RunShellUtils {
    /**
     * 执行一个shell命令，并返回字符串值
     *
     * @param cmd 命令名称&参数组成的数组（例如：{"/system/bin/cat", "/proc/version"}）
     * @return 执行结果组成的字符串
     * @throws IOException
     */

    public static synchronized String run(String[] cmd) {
        String s = null;
        InputStream in = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            in = p.getInputStream();
            int len = 0;
            while (len == 0) {
                len = in.available();
            }
            if (len != 0) {
                byte[] buffer = new byte[len];
                in.read(buffer);//读取
                s = new String(buffer);
            }
            if (in != null) {
                in.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return s;
    }
}
