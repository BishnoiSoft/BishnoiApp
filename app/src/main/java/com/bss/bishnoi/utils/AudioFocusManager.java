package com.bss.bishnoi.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import com.bss.bishnoi.MusicService;

public class AudioFocusManager {
    private final AudioManager audioManager;
    private final AudioFocusChangeListener audioFocusChangeListener;
    private AudioFocusRequest audioFocusRequest;
    private boolean hasAudioFocus = false;

    MusicService musicService;

    public AudioFocusManager(Context context, MusicService musicService) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioFocusChangeListener = new AudioFocusChangeListener();

        this.musicService = musicService;
    }

    public boolean requestAudioFocus() {
        if (hasAudioFocus) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .build();

            int result = audioManager.requestAudioFocus(audioFocusRequest);
            hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = audioManager.requestAudioFocus(audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }

        return hasAudioFocus;
    }

    public void abandonAudioFocus() {
        if (hasAudioFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            } else {
                audioManager.abandonAudioFocus(audioFocusChangeListener);
            }
            hasAudioFocus = false;
        }
    }

    public boolean hasAudioFocus() {
        return hasAudioFocus;
    }

    private class AudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    hasAudioFocus = true;
                    if (musicService != null) {
                        musicService.start();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    hasAudioFocus = false;
                    if (musicService!= null) {
                        musicService.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    hasAudioFocus = true;
                    if (musicService!= null) {
                        musicService.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    hasAudioFocus = true;
                    if (musicService!= null) {
                        musicService.lowerVolume();
                    }
                    break;
            }
        }
    }
}

