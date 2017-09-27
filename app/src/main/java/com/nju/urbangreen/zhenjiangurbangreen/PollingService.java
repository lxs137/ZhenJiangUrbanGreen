package com.nju.urbangreen.zhenjiangurbangreen;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.SystemClock;

import com.nju.urbangreen.zhenjiangurbangreen.message.AlarmReceiver;
import com.nju.urbangreen.zhenjiangurbangreen.message.MessageListActivity;
import com.nju.urbangreen.zhenjiangurbangreen.message.Message;

import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PollingService extends Service {

    public static final String ACTION = "com.nju.urbangreen.zhenjiangurbangreen.PollingService";
    private Notification.Builder builder;
    private NotificationManager mManager;
    private List<Message> messageList = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date OldDate = sdf.parse("2000-12-31 00:00:00");

    public PollingService() throws ParseException {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        initNotifiManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    showNotification();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int waitTime = 5 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + waitTime;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        //manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, waitTime, pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    //初始化通知栏配置
    private void initNotifiManager() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new Notification.Builder(PollingService.this);
        builder.setSmallIcon(R.drawable.ic_zhengjianglvhua);
        builder.setTicker("您有一条新消息");
        builder.setContentTitle("通知");
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setAutoCancel(true);
    }

    //弹出Notification
    private void showNotification() throws ParseException {
        String username = "xk";
        String[] errMsg = new String[1];
        Message qm;
        messageList = WebServiceUtils.getAllMessages(errMsg, username, false);
        if (messageList != null) {

            int length = messageList.size();

            int id = 0;

            for (int i = 0; i < length; i++) {
                qm = messageList.get(i);
                String stringTime = getCorrectString(qm.getQM_CreateTime());
                Date createTime = sdf.parse(stringTime);
                if (createTime.after(OldDate)) {
                    builder.setWhen(System.currentTimeMillis());
                    Intent intent = new Intent(this, MessageListActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                    builder.setContentIntent(pendingIntent);

                    builder.setContentText(qm.getQuickMessage());
                    Notification notification = builder.build();
                    mManager.notify(id, notification);
                    id++;
                }
            }
            Date latestTime = sdf.parse(getCorrectString(messageList.get(0).getQM_CreateTime()));
            OldDate = latestTime;
        }

    }

    public String getCorrectString(String Datestring) {
        String NewString;
        NewString = Datestring.substring(0, 19).replace("T", " ");
        return NewString;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }
}