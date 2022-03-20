package com.xcityprime.myaudioplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {
    public static final String ID1 = "Channel_1";
    public static final String ID2 = "Channel_2";
    public static final String Play = "actionPlay";
    public static final String Next = "actionNext";
    public static final String PREV = "actionPrev";
    public static final String CLOSE_N = "actionClose";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(ID1, "Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel Desc...");
            NotificationChannel channel2 = new NotificationChannel(ID2, "Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("Channel 2 Desc......");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
