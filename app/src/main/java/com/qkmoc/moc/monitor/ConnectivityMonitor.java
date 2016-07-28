package com.qkmoc.moc.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.qkmoc.moc.io.MessageWritable;


public class ConnectivityMonitor extends AbstractMonitor {
    private static final String TAG = "STFConnectivityMonitor";

    private ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    public ConnectivityMonitor(Context context, MessageWritable writer) {
        super(context, writer);
    }

    @Override
    public void run() {
        Log.i(TAG, "Monitor starting");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                peek();
            }
        };

        context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        try {
            synchronized (this) {
                while (!isInterrupted()) {
                    wait();
                }
            }
        }
        catch (InterruptedException e) {
            // Okay
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Log.i(TAG, "Monitor stopping");

            context.unregisterReceiver(receiver);
        }
    }

    @Override
    public void peek(MessageWritable writer) {
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null) {
            Log.i(TAG, String.format("Network %s/%s is %s; %s; %s",
                    info.getTypeName(),
                    info.getSubtypeName(),
                    info.isConnected() ? "connected" : "not connected",
                    info.isFailover() ? "failover" : "not failover",
                    info.isRoaming() ? "roaming" : "not roaming"
            ));

            writer.write(String.format("Network %s/%s is %s; %s; %s",
            info.getTypeName(),
                    info.getSubtypeName(),
                    info.isConnected() ? "connected" : "not connected",
                    info.isFailover() ? "failover" : "not failover",
                    info.isRoaming() ? "roaming" : "not roaming"
            ));
        }
        else {
            Log.i(TAG, "No active network");

            writer.write("No active network");
        }
    }
}
