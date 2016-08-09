package com.qkmoc.moc.core;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.net.Socket;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/8/9 16:19
 */
public class Send extends Thread {

    private Socket socket;
    private Context context;

    public Send(Socket socket, Context context) {
        this.socket = socket;
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
                Log.i("MOC TAG", "SENDOKOKOKOKOK");
            } catch (Exception se) {
                Log.i("MOC TAG", "SENDNULLLLLLLLLLLLLLL");
                Intent intent = new Intent();
                intent.setAction("com.qkmoc.moc.ACTION_STOP");
                context.startActivity(intent);
                //未测试
                ((Service) context).stopSelf();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
