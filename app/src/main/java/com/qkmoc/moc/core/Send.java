package com.qkmoc.moc.core;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.qkmoc.moc.ServiceConfig;

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
        while (true) {

            try {

                socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
                Log.i("MOC TAG", "SENDOKOKOKOKOK");
            } catch (Exception se) {
                Log.i("MOC TAG", "SENDNULLLLLLLLLLLLLLL");
                if (!ServiceConfig.socketFirstStart) {
                    ((Service) context).stopSelf();
                }
            }
            System.out.println(ServiceConfig.socketFirstStart);
            ServiceConfig.socketFirstStart = false;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
    }
}
