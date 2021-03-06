package com.qkmoc.moc.monitor;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.qkmoc.moc.io.MessageWritable;


public class PhoneStateMonitor extends AbstractMonitor {
    private static final String TAG = "STFPhoneStateMonitor";

    private ServiceState state = null;

    public PhoneStateMonitor(Context context, MessageWritable writer) {
        super(context, writer);
    }

    @Override
    public void run() {
        Log.i(TAG, "Monitor starting");

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener listener = new PhoneStateListener() {
            @Override
            public void onServiceStateChanged(ServiceState newState) {
                state = newState;
                report(writer, state);
            }
        };

        tm.listen(listener, PhoneStateListener.LISTEN_SERVICE_STATE);

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

            tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void peek(MessageWritable writer) {
        if (state != null) {
            report(writer, state);
        }
    }

    private void report(MessageWritable writer, ServiceState state) {
        Log.i(TAG, String.format("Phone state is %s; %s; %s",
                stateLabel(state.getState()),
                state.getIsManualSelection() ? "manual" : "automatic",
                state.getOperatorAlphaLong() == null ? "no operator" : "operator " + state.getOperatorAlphaLong()
        ));


        writer.write(String.format("Phone state is %s; %s; %s",
        stateLabel(state.getState()),
                state.getIsManualSelection() ? "manual" : "automatic",
                state.getOperatorAlphaLong() == null ? "no operator" : "operator " + state.getOperatorAlphaLong()
        ));
    }

    private String stateLabel(int state) {
        switch (state) {
            case ServiceState.STATE_EMERGENCY_ONLY:
                return "emergency_only";
            case ServiceState.STATE_IN_SERVICE:
                return "in_service";
            case ServiceState.STATE_OUT_OF_SERVICE:
                return "out_of_service";
            case ServiceState.STATE_POWER_OFF:
                return "power_off";
            default:
                return "unknown_" + state;
        }
    }
}
