package com.qkmoc.moc.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.qkmoc.moc.Bean.InfoToAPPBean;
import com.qkmoc.moc.Bean.MocAPPBean;
import com.qkmoc.moc.R;
import com.qkmoc.moc.ServiceConfig;
import com.qkmoc.moc.io.MessageReader;
import com.qkmoc.moc.io.MessageRouter;
import com.qkmoc.moc.io.MessageWriter;
import com.qkmoc.moc.monitor.AbstractMonitor;
import com.qkmoc.moc.monitor.AirplaneModeMonitor;
import com.qkmoc.moc.monitor.BatteryMonitor;
import com.qkmoc.moc.monitor.ConnectivityMonitor;
import com.qkmoc.moc.monitor.MiniStateMonitor;
import com.qkmoc.moc.util.CopyUtil;
import com.qkmoc.moc.util.JsonUtil;
import com.qkmoc.moc.view.IdentityActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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

    private static final String TAG = "MOCService";
    private static final int NOTIFICATION_ID = 0x1;

    private List<AbstractMonitor> monitors = new ArrayList<AbstractMonitor>();
    private ExecutorService executor = Executors.newCachedThreadPool();
    private ServerSocket acceptor;
    private boolean started = false;
    private MessageWriter.Pool writers = new MessageWriter.Pool();

    private Context context;
    // We can only access CLIPBOARD_SERVICE from the main thread
    private static Object clipboardManager;

    public static Object getClipboardManager() {
        return clipboardManager;
    }

    private PowerManager.WakeLock wakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        // We don't support binding to this service
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
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
        if (wakeLock != null) {
            wakeLock.release();
        }
        if (acceptor != null) {
            try {
                acceptor.close();
            } catch (IOException e) {
                // We don't care
            }
        }

        try {
            executor.shutdownNow();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Too bad
        } finally {
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
                wakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
                        .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                                | PowerManager.ON_AFTER_RELEASE, TAG);
                wakeLock.acquire();
                Log.i(TAG, "Starting service");
                int port = intent.getIntExtra(EXTRA_PORT, 1100);
                int backlog = intent.getIntExtra(EXTRA_BACKLOG, 1);

                String host = intent.getStringExtra(EXTRA_HOST);
                if (host == null) {
                    host = "127.0.0.1";
                }

                try {
                    acceptor = new ServerSocket(port, backlog, InetAddress.getByName(host));


                    addMonitor(new BatteryMonitor(this, writers));
                    addMonitor(new ConnectivityMonitor(this, writers));
//                    addMonitor(new PhoneStateMonitor(this, writers));
                    addMonitor(new AirplaneModeMonitor(this, writers));
//                    addMonitor(new BrowserPackageMonitor(this, writers));
//                    addMonitor(new MiniStateMonitor(this, writers,acceptor));
                    executor.submit(new Server(acceptor));


                } catch (UnknownHostException e) {
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.w(TAG, "Service is already running");
            }
        } else if (ACTION_STOP.equals(action)) {
            stopSelf();
        } else {
            Log.e(TAG, "Unknown action " + action);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onLowMemory() {
        Log.w(TAG, "Low memory");
    }

    private void addMonitor(AbstractMonitor monitor) {
        monitors.add(monitor);
        executor.submit(monitor);
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

            } catch (IOException e) {
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
                Send send = new Send(acceptor.accept(), context);
                executor.submit(send);
                while (!isInterrupted()) {
                    Connection conn = new Connection(acceptor.accept());
                    executor.submit(conn);
                }
            } catch (IOException e) {
            } finally {
                Log.i(TAG, "Server stopping");

                try {
                    acceptor.close();
                } catch (IOException e) {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void run() {
                Log.i(TAG, "Connection started");
                MessageWriter writer = null;
                MessageRouter router = null;

                try {
                    System.out.println("1111111111111");
                    writer = new MessageWriter(socket.getOutputStream());
                    writers.add(writer);
                    MessageReader reader = new MessageReader(socket.getInputStream());
                    router = new MessageRouter(writer);

                    for (AbstractMonitor monitor : monitors) {
                        monitor.peek(writer);
                    }


                    while (!isInterrupted()) {

                        String str = reader.read();
//                        System.out.println("strnull:" + str);
                        InfoToAPPBean infoToAPPBean = JsonUtil.jsonTobean(str, InfoToAPPBean.class);
                        int id = infoToAPPBean.getId();
                        String copytext = infoToAPPBean.getCopytext();
                        if (id != 0) {
                            MocAPPBean mocBean = MocAPPBean.getInstance();
                            mocBean.setId(id);
                        } else if (copytext != null) {
                            CopyUtil.copyToClipBoard(context, copytext);
                            Looper.prepare();
                            Toast.makeText(context, "群控大师:字段已复制到手机", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }

//                        router.route(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Log.i(TAG, "Connection stopping");

                    writers.remove(writer);

                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
