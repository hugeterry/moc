package com.qkmoc.moc.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.qkmoc.moc.Bean.MocAPPBean;
import com.qkmoc.moc.ServiceConfig;
import com.qkmoc.moc.core.Service;
import com.qkmoc.moc.io.MessageWritable;
import com.qkmoc.moc.util.JsonUtil;
import com.qkmoc.moc.util.RunShellUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MiniStateMonitor extends AbstractMonitor {
    private static final String TAG = "STFBatteryMonitor";
    private static final String[] minitouch_cmd = {"ps", "|", "grep", "minitouch"};
    private static final String[] minicap_cmd = {"ps", "|", "grep", "minicap"};
    String minitouch_state = null;
    String minicap_state = null;
    Context context;
    MessageWritable writer;
    Socket socket;

    public MiniStateMonitor(Context context, MessageWritable writer) {
        super(context, writer);
    }

    public MiniStateMonitor(Context context, MessageWritable writer, ServerSocket socket) {
        super(context, writer);
        try {
            this.socket = socket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Log.i(TAG, "Monitor starting");
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            report(writer);

        }
    }

    @Override
    public void peek(MessageWritable writer) {
        report(writer);
    }

    private void report(MessageWritable writer) {

        minitouch_state = RunShellUtils.run(minitouch_cmd).trim();
        minicap_state = RunShellUtils.run(minicap_cmd).trim();

        if (!minitouch_state.endsWith("minitouch")) {
            MocAPPBean mocBean = MocAPPBean.getInstance();
            mocBean.setMinitouchState(false);
            String gsonString = JsonUtil.beanToJson(mocBean);
            writer.write(gsonString);
            mocBean.setMinitouchState(true);
            Log.i("moc", "gsonString:" + gsonString);
        } else Log.i("moc", "minitouch shell:" + minitouch_state);

        if (!minicap_state.endsWith("minicap")) {
            MocAPPBean mocBean = MocAPPBean.getInstance();
            mocBean.setMinicapState(false);
            String gsonString = JsonUtil.beanToJson(mocBean);
            writer.write(gsonString);
            mocBean.setMinicapState(true);
            Log.i("moc", "gsonString:" + gsonString);
        } else Log.i("moc", "minicap shell:" + minicap_state);

        try {

            socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            Log.i("MOC TAG", "SENDOKOKOKOKOK");
        } catch (Exception se) {
            Log.i("MOC TAG", "SENDNULLLLLLLLLLLLLLL");
            if (!ServiceConfig.socketFirstStart ) {
                ((Service) context).stopSelf();
            }
        }
        System.out.println(ServiceConfig.socketFirstStart);
        ServiceConfig.socketFirstStart = false;
    }


}
