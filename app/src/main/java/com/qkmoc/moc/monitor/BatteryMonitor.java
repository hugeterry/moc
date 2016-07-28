package com.qkmoc.moc.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.qkmoc.moc.Bean.MocBean;
import com.qkmoc.moc.io.MessageWritable;
import com.qkmoc.moc.util.GsonUtil;


public class BatteryMonitor extends AbstractMonitor {
    private static final String TAG = "STFBatteryMonitor";

    private BatteryState state = null;

    public BatteryMonitor(Context context, MessageWritable writer) {
        super(context, writer);
    }

    @Override
    public void run() {
        Log.i(TAG, "Monitor starting");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                state = new BatteryState(intent);
                report(writer, state);
            }
        };

        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

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
        if (state != null) {
            report(writer, state);
        }
    }

    private void report(MessageWritable writer, BatteryState state) {
        Log.i(TAG, String.format("Battery is %s (%s health); connected via %s; level at %d/%d; temp %.1fC@%.3fV",
                statusLabel(state.status),
                healthLabel(state.health),
                sourceLabel(state.source),
                state.level,
                state.scale,
                state.temp / 10.0,
                state.voltage / 1000.0
        ));

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        MocBean mocBean = MocBean.getInstance();
        mocBean.setWIFI(String.valueOf(wm.isWifiEnabled()));
        mocBean.setBatteryLevel(state.level);
        mocBean.setBatterySourceLabel(sourceLabel(state.source));
        mocBean.setImei(tm.getDeviceId());
        String gsonString = GsonUtil.GsonString(mocBean);
        System.out.println(gsonString);
        writer.write(gsonString);
//        try {
//            //反序列化
//            Wire.BatteryEvent newBe = Wire.BatteryEvent.parseFrom(be.toByteString());
////            System.out.println(newBe);
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }


    }

    private String healthLabel(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "cold";
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "good";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "over_voltage";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "overheat";
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                return "unknown";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "unspecified_failure";
            default:
                return "unknown_" + health;
        }
    }

    private String sourceLabel(int source) {
        switch (source) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "ac";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "usb";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "wireless";
            default:
                return "unknown_" + source;
        }
    }

    private String statusLabel(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "discharging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "not_charging";
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                return "unknown";
            default:
                return "unknown_" + status;
        }
    }

    private static class BatteryState {
        private int health;
        private int level;
        private int source;
        private int scale;
        private int status;
        private String tech;
        private int temp;
        private int voltage;

        public BatteryState(Intent intent) {
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            source = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        }
    }
}
