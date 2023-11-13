package com.bss.bishnoi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bss.bishnoi.models.BhajanModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadMusic extends AppCompatActivity {
    public static final int REQUEST_IMAGE_PICK = 1;
    public static final int REQUEST_AUDIO_PICK = 2;

    EditText etTitle, etArtist, etDuration, etType, etSize, etLyrics, etKeywords;
    ImageView chooseImage, chooseAudio;
    TextView audioFileName;
    Button btnUpload;

    Uri imageUri, audioUri;
    
    String imageUrl;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private List<File> bitrateFiles;
    private List<File> segmentationFiles;
    private List<String> playlistUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_music);

        setWindow();
        initViews();

        databaseReference = FirebaseDatabase.getInstance().getReference("music");
        storageReference = FirebaseStorage.getInstance().getReference("music");

        List<BhajanModel> playlist = generatePlaylist();

        // Upload playlist and store URLs in Firebase
        uploadPlaylist(playlist);
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etArtist = findViewById(R.id.etArtist);
        etDuration = findViewById(R.id.etDuration);
        etType = findViewById(R.id.etType);
        etSize = findViewById(R.id.etSize);
        etLyrics = findViewById(R.id.etLyrics);
        etKeywords = findViewById(R.id.etKeywords);

        audioFileName = findViewById(R.id.tvAudioFileName);

        chooseAudio = findViewById(R.id.etChooseMusic);
        chooseImage = findViewById(R.id.etChooseImage);

        btnUpload = findViewById(R.id.btnUpload);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });

        chooseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAudioFromGallery();
            }
        });
    }

    private void setWindow() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setNavigationBarDividerColor(ContextCompat.getColor(this, R.color.white));
        }

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.saffron));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();

        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void chooseAudioFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_AUDIO_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Upload the selected image to Firebase Storage
            Log.d(TAG, "onActivityResult: " + imageUri.toString());
            chooseImage.setImageURI(imageUri);
        }

        if (requestCode == REQUEST_AUDIO_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            audioUri = data.getData();
            // Upload the selected image to Firebase Storage
            Log.d(TAG, "onActivityResult: " + audioUri.toString());
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("music_images");
            StorageReference fileRef = imagesRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            imageUrl = downloadUrl;
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e);
                        }
                    });
        } else {
            Log.d(TAG, "uploadFile: No Uri Found");
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private List<BhajanModel> generatePlaylist() {
        // Create a list of BhajanModel objects representing the playlist
        List<BhajanModel> playlist = new ArrayList<>();

        // Add different bitrates and segmentations to the playlist
        // Replace the example URLs with the actual URLs of your audio files

//        // 128kbps
//        BhajanModel audio128kbps = new BhajanModel("Audio 128kbps", "https://example.com/audio_128kbps.mp3");
//        playlist.add(audio128kbps);
//
//        // 256kbps
//        BhajanModel audio256kbps = new BhajanModel("Audio 256kbps", "https://example.com/audio_256kbps.mp3");
//        playlist.add(audio256kbps);
//
//        // 512kbps
//        BhajanModel audio512kbps = new BhajanModel("Audio 512kbps", "https://example.com/audio_512kbps.mp3");
//        playlist.add(audio512kbps);

        // Return the generated playlist
        return playlist;
    }

    private void uploadPlaylist(List<BhajanModel> playlist) {
        // Upload each audio file in the playlist to Firebase Storage and store the URLs in the Realtime Database
        for (BhajanModel audio : playlist) {
            // Get a reference to the audio file in Firebase Storage
            StorageReference audioRef = storageReference.child(audio.getTitle() + ".mp3");

            // Upload the audio file to Firebase Storage
            audioRef.putFile(Uri.parse(audio.getAudioUrl()))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded audio file
                            Task<Uri> downloadUrlTask = audioRef.getDownloadUrl();
                            downloadUrlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        // Store the download URL in the Realtime Database
                                        Uri downloadUrl = task.getResult();
                                        if (downloadUrl != null) {
                                            audio.setAudioUrl(downloadUrl.toString());
                                            databaseReference.child(audio.getTitle()).setValue(audio);
                                        }
                                    } else {
                                        Log.e(TAG, "Failed to get download URL: " + task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    });
        }
    }
}