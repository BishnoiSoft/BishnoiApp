package com.bss.bishnoi;

import static android.content.ContentValues.TAG;

import static com.bss.bishnoi.ApplicationClass.ACTION_NEXT;
import static com.bss.bishnoi.ApplicationClass.ACTION_PLAY;
import static com.bss.bishnoi.ApplicationClass.ACTION_PREVIOUS;
import static com.bss.bishnoi.ApplicationClass.CHANNEL_ID_2;
import static com.bss.bishnoi.R.mipmap.ic_jambhoji;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bss.bishnoi.interfaces.ActionPlaying;
import com.bss.bishnoi.models.BhajanModel;
import com.bss.bishnoi.utils.AudioFocusManager;
import com.bss.bishnoi.utils.ImageLoader;
import com.bss.bishnoi.utils.NotificationReceiver;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.ArrayList;

public class MusicService extends Service {
    IBinder mBinder = new MyBinder();
    ExoPlayer player;
    static ArrayList<BhajanModel> bhajans = new ArrayList<>();

    String audioUrl;
    String imageUrl;

    MediaItem mediaItem;
    int position = -1;

    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    private AudioFocusManager audioFocusManager;

    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE_TITLE = "STORED_MUSIC_TITLE";

    public static final String MUSIC_TITLE = "MUSIC_TITLE";
    public static final String MUSIC_ARTIST = "MUSIC_ARTIST";
    public static final String MUSIC_ICON_URL = "MUSIC_ICON_URL";

    public static final String MUSIC_POSITION = "MUSIC_POSITION";

    static boolean isRunning = false;

    static boolean musicOn = false;

    Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = ApplicationClass.getAppContext();
        audioFocusManager = new AudioFocusManager(context, this);

        mediaSession = new MediaSessionCompat(context, "BISHNOI_MUSIC");

        mediaSession.setCallback(mediaSessionCallback);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);



        // Set initial playback state
        playbackStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE);
        mediaSession.setPlaybackState(playbackStateBuilder.build());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: onBind method called.");
        return mBinder;
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("ServicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");
        if (myPosition != -1) {
            position = myPosition;
            playMedia(myPosition);
        }
        if (actionName != null) {
            switch (actionName) {
                case "PLAY_PAUSE":
                    playPausedBtnClicked();
                    break;
                case "NEXT":
                    nextBtnClicked();
                    break;
                case "PREVIOUS":
                    previousBtnClicked();
                    break;
            }
        }

        return START_STICKY;
    }

    private void playMedia(int startPosition) {

        bhajans = ApplicationClass.bhajanModelArrayList;
        position = startPosition;

        if (player != null) {
            player.stop();
            player.release();
            if (bhajans != null) {
                createPlayer(position);
                start();
            }
        }
        else {
            createPlayer(position);
            start();
        }
    }

    public void start() {
        if (audioFocusManager.hasAudioFocus()) {
            musicOn = true;
            player.setPlayWhenReady(true);
            showNotification(R.drawable.baseline_pause_black_24);
        } else {
            if (audioFocusManager.requestAudioFocus()) {
                musicOn = true;
                player.setPlayWhenReady(true);
                showNotification(R.drawable.baseline_pause_black_24);
            }
        }

        isRunning = true;
        setMediaMetadata(musicTitle(), musicArtist(), imageFromUrl(musicIconUrl()));
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void stop() {
        player.stop();
        showNotification(R.drawable.baseline_play_arrow_blac_24);
        musicOn = false;
    }

    public void pause() {
        player.pause();
        showNotification(R.drawable.baseline_play_arrow_blac_24);
        musicOn = false;
    }

    public void release() {
        player.release();
        musicOn = false;
    }

    public int getDuration() {
        return (int) player.getDuration();
    }

    public void seekTo(int position) {
        player.seekTo(position);
    }

    int getCurrentPosition() {
        return (int) player.getCurrentPosition();
    }

    public void createPlayer(int positionInner) {
        position = positionInner;

        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putString(MUSIC_FILE_TITLE, bhajans.get(position).getTitle());
        editor.putString(MUSIC_TITLE, bhajans.get(position).getTitle());
        editor.putString(MUSIC_ARTIST, bhajans.get(position).getArtist());
        editor.putString(MUSIC_ICON_URL, bhajans.get(position).getImageUrl());
        editor.putInt(MUSIC_POSITION, position);
        editor.apply();

        player = new SimpleExoPlayer.Builder(context).build();
        mediaItem = MediaItem.fromUri(bhajans.get(position).getAudioUrl());
        Log.d(TAG, "createPlayer: " + bhajans.get(position).getAudioUrl());

        MediaSource mediaSource = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(context))
                .createMediaSource(mediaItem);

        player.setMediaSource(mediaSource);
        player.prepare();
        onCompleted();
    }

    public void onCompleted() {
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    if (actionPlaying != null) {
                        actionPlaying.btn_nextClicked();
                    }
                    position = ((position + 1) % bhajans.size());
                    createPlayer(position);
                    start();
                }
            }
        });
    }

    int playbackState() {
        return player.getPlaybackState();
    }

    void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }

    public void showNotification(int playPauseBtn) {

        Intent intent = new Intent(this, MusicDetails.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);

        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Bitmap largeIcon = imageFromUrl(bhajans.get(position).getImageUrl());

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.baseline_play_arrow_blac_24)
                .setLargeIcon(largeIcon)
                .setContentTitle(bhajans.get(position).getTitle())
                .setContentText(bhajans.get(position).getArtist())
                .addAction(R.drawable.baseline_skip_previous_black_24, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Pause", pausePendingIntent)
                .addAction(R.drawable.baseline_skip_next_black_24, "Next", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        startForeground(1, notification);
    }

    Bitmap imageFromUrl(String url) {
        final Bitmap[] musicIcon = new Bitmap[1];
        ImageLoader.loadImageFromUrl(url, new ImageLoader.LoadImageCallback() {
            @Override
            public void onImageLoaded(Bitmap bitmap) {
                if (bitmap != null) {
                    musicIcon[0] = bitmap;
                } else {
                    musicIcon[0] = BitmapFactory.decodeResource(getResources(), ic_jambhoji);
                }
            }
        });
        return  musicIcon[0];
    }

    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            start();
        }

        @Override
        public void onPause() {
            pause();
        }

        @Override
        public void onStop() {
            pause();
        }

        @Override
        public void onSeekTo(long pos) {
            if (player != null) {
                player.seekTo((int) pos);
            }
        }
    };

    public void lowerVolume() {
        // Lower the playback volume temporarily
        if (player != null && player.isPlaying()) {
            player.setVolume(0.2f); // Example: Set the volume to 20%
        }
    }

    public String musicTitle() {
        return bhajans.get(position).getTitle();
    }

    public String musicArtist() {
        return bhajans.get(position).getArtist();
    }

    public String musicIconUrl() {
        return bhajans.get(position).getImageUrl();
    }

    public String musicDuration() {
        return bhajans.get(position).getDuration();
    }

    public void playPausedBtnClicked() {
        if (actionPlaying != null) {
            actionPlaying.btn_play_pauseClicked();
        }
    }

    public void nextBtnClicked() {
        if (actionPlaying != null) {
            actionPlaying.btn_nextClicked();
        }
    }

    public void previousBtnClicked() {
        if (actionPlaying != null) {
            actionPlaying.btn_preClicked();
        }
    }

    public static boolean isRunning() {
        return isRunning;
    }

    private void setMediaMetadata(String title, String artist, Bitmap albumArt) {
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);

        mediaSession.setMetadata(metadataBuilder.build());
    }

}
