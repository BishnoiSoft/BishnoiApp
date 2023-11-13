package com.bss.bishnoi.utils;

import static android.content.ContentValues.TAG;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bss.bishnoi.MusicActivity;
import com.bss.bishnoi.R;
import com.bss.bishnoi.models.BhajanModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MusicDownloader {
    private Context context;
    private BhajanModel bhajan;
    private long audioDownloadId;
    private long imageDownloadId;

    private static final String CHANNEL_ID = "BHAJAN_DOWNLOAD_CHANNEL";
    private static final int NOTIFICATION_ID = 1;

    private String audioPath;
    private String imagePath;

    public MusicDownloader(Context context, BhajanModel bhajan) {
        this.context = context;
        this.bhajan = bhajan;
    }

    public void startDownload() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(bhajan.getAudioUrl()));
        request.setTitle(bhajan.getTitle());
        request.setDescription("Downloading...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        // Saving the file in app specific Directory
        try {
            File downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (downloadDir != null && !downloadDir.exists()) {
                downloadDir.mkdirs();
            }

            audioPath = downloadDir.getAbsolutePath() + File.separator + bhajan.getTitle() + ".mp3";
            request.setDestinationUri(Uri.fromFile(new File(audioPath)));

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            audioDownloadId = downloadManager.enqueue(request);

            registerDownloadReceiver();
            downloadImage();
        } catch (Exception e) {
            Log.e(TAG, "startDownload: " + e);
        }
    }

    public void downloadImage() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(bhajan.getImageUrl()));
        request.setTitle(bhajan.getTitle());
        request.setDescription("Downloading Icon...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        // Saving the file in app specific Directory
        File downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (downloadDir != null && !downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        imagePath = downloadDir.getAbsolutePath() + File.separator + bhajan.getTitle() + ".jpeg";
        request.setDestinationUri(Uri.fromFile(new File(imagePath)));

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        imageDownloadId = downloadManager.enqueue(request);
    }

    private void enterDownloads() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("downloads");

        DatabaseReference usersReference = databaseReference.child(bhajan.getTitle().trim() + "- " + bhajan.getArtist());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("download", 1);

        usersReference.child(currentUser.getPhoneNumber()).setValue(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data successfully added
                        // You can perform any additional tasks here if needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add data
                        // Handle the error here
                    }
                });
    }

    private void registerDownloadReceiver() {
        BroadcastReceiver onCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == audioDownloadId) {
                    createNotification();
                    insertDownloadData();
                }
            }
        };

        context.registerReceiver(onCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void createNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Download";
            String description = "Download completed";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        Intent contentIntent = new Intent(context, MusicActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentIntent.putExtra("QUERY", " डाउनलोड ");

        PendingIntent pendingContentIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(bhajan.getTitle())
                .setSmallIcon(R.drawable.baseline_file_download_24)
                .setContentText("Download completed")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setAutoCancel(true)
                .setContentIntent(pendingContentIntent);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void insertDownloadData() {
        enterDownloads();
        DBHelperDownload DB = new DBHelperDownload(context);

        BhajanModel downloadBhajan = new BhajanModel(bhajan.getId(), bhajan.getTitle(), imagePath, audioPath, bhajan.getArtist(), bhajan.getDuration(), bhajan.getType(), bhajan.getLyrics(), bhajan.getKeywords(), bhajan.getSize(), bhajan.getDownloads());
        Boolean checkInsertData = DB.insertDownloadData(downloadBhajan);
        if (checkInsertData) {
            Toast.makeText(context, "Entry Inserted \n" + imagePath + "\n" + audioPath, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Some Error Occurred.", Toast.LENGTH_SHORT).show();
        }
    }
}

