package com.qkmoc.moc.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.qkmoc.moc.Bean.MocAPPBean;
import com.qkmoc.moc.io.MessageWritable;
import com.qkmoc.moc.util.JsonUtil;


public class AirplaneModeMonitor extends AbstractMonitor {
    private static final String TAG = "STFAirplaneModeMonitor";

    public AirplaneModeMonitor(Context context, MessageWritable writer) {
        super(context, writer);
    }

    @Override
    public void run() {
        Log.i(TAG, "Monitor starting");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                report(writer, intent.getBooleanExtra("state", false));
            }
        };

        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));

        try {
            synchronized (this) {
                while (!isInterrupted()) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            // Okay
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.i(TAG, "Monitor stopping");

            context.unregisterReceiver(receiver);
        }
    }

    @Override
    public void peek(MessageWritable writer) {
        if (Build.VERSION.SDK_INT >= 17) {
            report(writer, Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1);
        } else {
            report(writer, Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1);
        }
    }

    private void report(MessageWritable writer, boolean enabled) {
        Log.i(TAG, String.format("Airplane:%s", enabled ? "on" : "off"));
        MocAPPBean mocBean = MocAPPBean.getInstance();
        mocBean.setAirplane(enabled ? "on" : "off");
        String gsonString = JsonUtil.beanToJson(mocBean);
        writer.write(gsonString);
    }
}
