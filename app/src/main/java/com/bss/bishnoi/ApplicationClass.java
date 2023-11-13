package com.bss.bishnoi;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.bss.bishnoi.models.BhajanModel;

import java.util.ArrayList;
import java.util.List;

public class ApplicationClass extends Application {
    public static Context context;
    public static List<BhajanModel> bhajanModelList;

    public static ArrayList<BhajanModel> bhajanModelArrayList;

    public static final String CHANNEL_ID_1 = "CHANNEL_1";
    public static final String CHANNEL_ID_2 = "CHANNEL_2";
    public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PLAY = "ACTION_PLAY";

    private static ApplicationClass instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        createNotificationChannel();

    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1,
                    "Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Description");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2,
                    "Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 2 Description");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }

}
