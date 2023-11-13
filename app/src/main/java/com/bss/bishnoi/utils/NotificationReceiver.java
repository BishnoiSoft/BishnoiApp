package com.bss.bishnoi.utils;

import static android.content.ContentValues.TAG;
import static com.bss.bishnoi.ApplicationClass.ACTION_NEXT;
import static com.bss.bishnoi.ApplicationClass.ACTION_PLAY;
import static com.bss.bishnoi.ApplicationClass.ACTION_PREVIOUS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.bss.bishnoi.MusicService;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);
        if (actionName != null) {
            switch (actionName) {
                case ACTION_PLAY:
                    serviceIntent.putExtra("ActionName", "PLAY_PAUSE");
                    context.startService(serviceIntent);
                    break;
                case ACTION_NEXT:
                    serviceIntent.putExtra("ActionName", "NEXT");
                    context.startService(serviceIntent);
                    break;
                case ACTION_PREVIOUS:
                    Toast.makeText(context, "Action Previous Clicked", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onReceive: Action Previous Received from the notification.");
                    serviceIntent.putExtra("ActionName", "PREVIOUS");
                    context.startService(serviceIntent);
                    break;
            }
        }
    }
}
