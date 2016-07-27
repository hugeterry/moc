package com.qkmoc.moc.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.qkmoc.moc.R;
import com.qkmoc.moc.view.IdentityActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/27 15:38
 */

public class Service extends android.app.Service {
    public static final String ACTION_START = "com.qkmoc.moc.ACTION_START";
    public static final String ACTION_STOP = "com.qkmoc.moc.ACTION_STOP";
    public static final String EXTRA_PORT = "com.qkmoc.moc.EXTRA_PORT";
    public static final String EXTRA_HOST = "com.qkmoc.moc.EXTRA_HOST";
    public static final String EXTRA_BACKLOG = "com.qkmoc.moc.EXTRA_BACKLOG";

    private static final String TAG = "STFService";
    private static final int NOTIFICATION_ID = 0x1;

    private ExecutorService executor = Executors.newCachedThreadPool();
    private ServerSocket acceptor;
    private boolean started = false;

    private static Object clipboardManager;

    public static Object getClipboardManager() {
        return clipboardManager;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't support binding to this service
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE);

        Intent notificationIntent = new Intent(this, IdentityActivity.class);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setTicker(getString(R.string.service_ticker))
                .setContentTitle(getString(R.string.service_title))
                .setContentText(getString(R.string.service_text))
                .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .setWhen(System.currentTimeMillis())
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "Stopping service");

        stopForeground(true);

        if (acceptor != null) {
            try {
                acceptor.close();
            }
            catch (IOException e) {
                // We don't care
            }
        }

        try {
            executor.shutdownNow();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            // Too bad
        }
        finally {
            started = false;

            Process.killProcess(Process.myPid());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            if (!started) {
                Log.i(TAG, "Starting service");

                int port = intent.getIntExtra(EXTRA_PORT, 1100);
                int backlog = intent.getIntExtra(EXTRA_BACKLOG, 1);

                String host = intent.getStringExtra(EXTRA_HOST);
                if (host == null) {
                    host = "127.0.0.1";
                }

                try {
                    acceptor = new ServerSocket(port, backlog, InetAddress.getByName(host));



                    executor.submit(new Server(acceptor));

                    started = true;
                }
                catch (UnknownHostException e) {
                    Log.e(TAG, e.getMessage());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.w(TAG, "Service is already running");
            }
        }
        else if (ACTION_STOP.equals(action)) {
            stopSelf();
        }
        else {
            Log.e(TAG, "Unknown action " + action);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onLowMemory() {
        Log.w(TAG, "Low memory");
    }


    class Server extends Thread {
        private ServerSocket acceptor;
        private ExecutorService executor = Executors.newCachedThreadPool();

        public Server(ServerSocket acceptor) {
            this.acceptor = acceptor;
        }

        @Override
        public void interrupt() {
            super.interrupt();

            try {
                acceptor.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Log.i(TAG, String.format("Server listening on %s:%d",
                    acceptor.getInetAddress().toString(),
                    acceptor.getLocalPort()
            ));

            try {
                while (!isInterrupted()) {
                    Connection conn = new Connection(acceptor.accept());
                    executor.submit(conn);
                }
            }
            catch (IOException e) {
            }
            finally {
                Log.i(TAG, "Server stopping" );

                try {
                    acceptor.close();
                }
                catch (IOException e) {
                }

                stopSelf();
            }
        }

        class Connection extends Thread {
            private Socket socket;

            public Connection(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void interrupt() {
                super.interrupt();

                try {
                    socket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void run() {}
        }
    }
}
