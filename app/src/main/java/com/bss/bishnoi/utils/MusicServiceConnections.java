package com.bss.bishnoi.utils;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.bss.bishnoi.MusicService;

public class MusicServiceConnections implements ServiceConnection {
    private MusicService musicService;
    private boolean isServiceBound = false;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
        isServiceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
        isServiceBound = false;
    }

    public MusicService getMusicService() {
        return musicService;
    }

    public boolean isServiceBound() {
        return isServiceBound;
    }
}
