package com.bss.bishnoi;

import static android.content.ContentValues.TAG;

import static com.bss.bishnoi.R.drawable.baseline_pause_white_24;
import static com.bss.bishnoi.R.drawable.baseline_play_arrow_white_24;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bss.bishnoi.adapters.BhajanAdapater;
import com.bss.bishnoi.adapters.HorizontalScrollAdapter;
import com.bss.bishnoi.interfaces.ActionPlaying;
import com.bss.bishnoi.interfaces.BhajanDeleteListener;
import com.bss.bishnoi.interfaces.BhajanDownloadListener;
import com.bss.bishnoi.interfaces.BhajanItemClickListner;
import com.bss.bishnoi.interfaces.UpReItemClickListner;
import com.bss.bishnoi.models.BhajanModel;
import com.bss.bishnoi.utils.DBBhajanHelper;
import com.bss.bishnoi.utils.DBHelperDownload;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class MusicActivity extends AppCompatActivity implements UpReItemClickListner, BhajanItemClickListner, BhajanDeleteListener, BhajanDownloadListener, ServiceConnection, ActionPlaying {

    private RecyclerView upperRecyclerView, recyclerView;
    private HorizontalScrollAdapter horizontalScrollAdapter;
    public static final String CHANNEL_ID = "download_channel";

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    ImageView profile_photo;

    String uPhone, fProfileUrl;

    BhajanAdapater bhajanAdapater;

    String query = "";

    static ArrayList<BhajanModel> bhajans;
    ActivityResultLauncher<String> storageReadPermissionLauncher;
    ActivityResultLauncher<String> storageWritePermissionLauncher;
    final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    // For Bottom Music playing Layout
    ImageView bottomIconView, bottomPlayBtn;
    TextView bottomTitleView, bottomArtistView;
    RelativeLayout layoutButtonPlay, bottomMusicLayout;
    ProgressBar bProgressBar;

    Context context = MusicActivity.this;
    private Runnable progressUpdateRunnable;

    int currentPosition;

    DBHelperDownload DB;

    MusicService musicService;

    private Handler handler = new Handler();


    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE_TITLE = "STORED_MUSIC_TITLE";

    public static final String MUSIC_TITLE = "MUSIC_TITLE";
    public static final String MUSIC_ARTIST = "MUSIC_ARTIST";
    public static final String MUSIC_ICON_URL = "MUSIC_ICON_URL";
    public static final String MUSIC_POSITION = "MUSIC_POSITION";

    public static boolean SHOW_MINI_PLAYER = false;

    ImageView searchIcon, helpIcon;
    RelativeLayout layoutTop;
    LinearLayout layoutSearch;
    EditText etSearch;

    DBBhajanHelper BhajanDB;

    ImageView btn_store;

    // For interstitial Ad
    private InterstitialAd mInterstitialAd;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private Context mContext;

    boolean isActive = false;

    Boolean musicNAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ApplicationClass.context = context;
        mContext = this;
        isActive = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Download Channel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        getPermissions();
        bhajans = new ArrayList<>();
        DB = new DBHelperDownload(MusicActivity.this);
        setWindow();
        initViews();
        setUpperIcons();
        setUpperRecyclerView();
        MobileAds.initialize (this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete( InitializationStatus initializationStatus ) {

                //Showing a simple Toast Message to the user when The Google AdMob Sdk Initialization is Completed
                //Toast.makeText (MainActivity.this, "AdMob Sdk Initialize "+ initializationStatus.toString(), Toast.LENGTH_LONG).show();
            }
        });

        checkAds();
        loadInterstitialAd();

        bottomLayoutOnClick();
        getIntentMethod();

    }

    private void checkAds() {
        DatabaseReference adsReference = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("ads");

        adsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                musicNAd = (Boolean) snapshot.child("musicNAd").getValue();

                if (Boolean.TRUE.equals(musicNAd)) {
                    loadNativeAd();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadNativeAd() {
        AdLoader adLoader = new AdLoader.Builder(this, getResources().getString(R.string.admob_native_ad_id))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        TemplateView template = findViewById(R.id.nativeTemplateView);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                        template.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getResources().getString(R.string.admob_interstitial_ad_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }

    private void showInterstitialAd() {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MusicActivity.this);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        mInterstitialAd = null;
                        loadInterstitialAd();
                        showInterstitialAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        mInterstitialAd = null;
                        loadInterstitialAd();
                        showInterstitialAd();

                    }
                });
            }
    }

    private void getPermissions() {
        storageReadPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (!granted) {
                userResponse();
            }
        });

        storageWritePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (!granted) {
                userResponse();
            }
        });

        // launch Storage Permission
        storageReadPermissionLauncher.launch(read_permission);
        storageWritePermissionLauncher.launch(write_permission);
    }

    private void userResponse() {
        if (ContextCompat.checkSelfPermission(this, read_permission) == PackageManager.PERMISSION_GRANTED) {

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(read_permission) || shouldShowRequestPermissionRationale(write_permission)) {
                // show an education ui to user explaining the user
                new AlertDialog.Builder(this)
                        .setTitle("Requesting Permission")
                        .setMessage("Allow us to Download and Fetch the Bhajans")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // request permission
                                storageReadPermissionLauncher.launch(read_permission);
                                storageWritePermissionLauncher.launch(write_permission);
                            }
                        })
                        .show();
            }
        }
    }

    private void initViews() {
        upperRecyclerView = findViewById(R.id.upper_recycler_view);
        profile_photo = findViewById(R.id.icon_profile);

        profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicActivity.this, ShowProfile.class);
                intent.putExtra("activity", "music");
                startActivity(intent);
            }
        });


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent_login = new Intent(MusicActivity.this, Login.class);
            startActivity(intent_login);
            finish();
        }

        uPhone = currentUser.getPhoneNumber();
        CollectionReference usersRef = firebaseFirestore.collection("users");

        DocumentReference userDocRef = usersRef.document(uPhone);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    fProfileUrl = (String) documentSnapshot.get("fProfileUrl");
                    Log.d("Profile Photo Url", "" + fProfileUrl);
                    if (fProfileUrl != null) {
                        Picasso.get().load(fProfileUrl).into(profile_photo);
                    }
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomIconView = findViewById(R.id.bottomIconView);
        bottomTitleView = findViewById(R.id.bottomTitleView);
        bottomArtistView = findViewById(R.id.bottomArtistView);
        bottomPlayBtn = findViewById(R.id.bottomPlayButton);
        layoutButtonPlay = findViewById(R.id.layout_button_play);
        bProgressBar = findViewById(R.id.currentMusicProgress);
        bProgressBar.setVisibility(View.GONE);

        btn_store = findViewById(R.id.icon_store);
        btn_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicActivity.this, StoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        handler = new Handler();
        progressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateProgressBar();
            }
        };

        bottomMusicLayout = findViewById(R.id.bottom_play_layout);

    }

    private void getLocalMusicDetails() {
        bhajans.clear();
        Log.d(TAG, "getLocalMusicDetails: Getting data from local database....");

        BhajanDB = new DBBhajanHelper(MusicActivity.this);

        Cursor cursor = DB.getData();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Downloads Available.", Toast.LENGTH_SHORT).show();
        }

        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String imageUrl = cursor.getString(2);
            String audioUrl = cursor.getString(3);
            String artist = cursor.getString(4);
            String duration = cursor.getString(5);
            String type = cursor.getString(6);
            String lyrics = cursor.getString(7);
            String keywords = cursor.getString(8);
            String size = cursor.getString(9);
            int downloads = cursor.getInt(10);

            BhajanModel bhajan = new BhajanModel(id, title, imageUrl, audioUrl, artist, duration, type, lyrics, keywords, size, downloads);
            bhajans.add(bhajan);

        }
        Log.d(TAG, "getLocalMusicDetails: Bhajan Lenght" + bhajans.size());
        setAdapter(bhajans);
    }

    private void setUpperIcons() {
        layoutTop = findViewById(R.id.layout_title_bar);
        layoutSearch = findViewById(R.id.layout_search);
        searchIcon = findViewById(R.id.icon_search);
        helpIcon = findViewById(R.id.icon_help);
        etSearch = findViewById(R.id.etSearch);

        helpIcon.setVisibility(View.GONE);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTop.setVisibility(View.GONE);
                layoutSearch.setVisibility(View.VISIBLE);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed.
                // You can perform any pre-processing here if needed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is changed.
                // You can perform any action based on the current text here.
                String text = s.toString();
                bhajanAdapater.filter(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getIntentMethod() {
        query = getIntent().getStringExtra("QUERY");
        if (query != null) {
            if (query.equals(" डाउनलोड ")) {
                setDownloadMusic();
            } else {
                getFirebaseData(query);
            }
        }
    }

    private void bottomLayoutOnClick() {
        bottomMusicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicActivity.this, MusicDetails.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void setUpperRecyclerView() {
        ArrayList<String> upper_scroll_items = new ArrayList<>();
        upper_scroll_items.add(" सभी ");
        upper_scroll_items.add(" भजन ");
        upper_scroll_items.add(" आरती ");
        upper_scroll_items.add(" साखी ");
        upper_scroll_items.add(" हनुमान चालीसा ");
        upper_scroll_items.add(" डाउनलोड ");

        upperRecyclerView = findViewById(R.id.upper_recycler_view);
        upperRecyclerView.setHasFixedSize(true);
        upperRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Using Recycler View
        horizontalScrollAdapter = new HorizontalScrollAdapter(MusicActivity.this, upper_scroll_items, this);
        upperRecyclerView.setAdapter(horizontalScrollAdapter);
    }

    @Override
    public void onItemClick(int position, String clickedItem) {
        Context context = getApplicationContext();

        if (clickedItem.equals(" आरती ")) {
            query = " आरती ";
            getFirebaseData(query);
        } else if (clickedItem.equals(" भजन ")) {
            query = " भजन ";
            getFirebaseData(query);
        } else if (clickedItem.equals(" डाउनलोड ")) {
            query = " डाउनलोड ";
            setDownloadMusic();
        } else if (clickedItem.equals(" साखी ")) {
            query = " साखी ";
            getFirebaseData(query);
        } else if (clickedItem.equals(" हनुमान चालीसा ")) {
            query = " हनुमान चालीसा ";
            getFirebaseData(query);
        } else if (clickedItem.equals(" सभी ")) {
            query = null;
            getFirebaseData(null);
        }
    }

    public void getFirebaseData(String eType) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference("music");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bhajans.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String audioUrl = dataSnapshot.child("audioUrl").getValue(String.class);
                    String artist = dataSnapshot.child("artist").getValue(String.class);
                    String credits = dataSnapshot.child("credits").getValue(String.class);
                    String lyrics = dataSnapshot.child("lyrics").getValue(String.class);
                    String keywords = dataSnapshot.child("keywords").getValue(String.class);
                    String type = dataSnapshot.child("type").getValue(String.class);
                    String duration = dataSnapshot.child("duration").getValue(String.class);
                    String size = (String) dataSnapshot.child("size").getValue();
                    String id = (String) dataSnapshot.child("id").getValue();

                    Log.d(TAG, "FirebaseData:  " + title + imageUrl + audioUrl + artist + credits + lyrics);

                    int downloads = 0;
                    try {
                        downloads = (int) dataSnapshot.child("downloads").getValue();
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onDataChange: " + e);
                        downloads = 0;
                    }
                    BhajanModel bhajan = new BhajanModel(id, title, imageUrl, audioUrl, artist, duration, type, lyrics, keywords, size, downloads);
                    if (eType == null) {
                        bhajans.add(bhajan);
                    } else if (eType.equals(type)) {
                        bhajans.add(bhajan);
                    }
                }
                if (bhajans.size() >= 5) {
                    ArrayList<BhajanModel> tempBhajanList = new ArrayList<>(bhajans);
                    long seed = System.nanoTime();
                    Collections.shuffle(tempBhajanList, new Random(seed)); // Shuffle the list randomly
                    bhajans = new ArrayList<>(tempBhajanList.subList(0,bhajans.size()));
                }
                setAdapter(bhajans);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setDownloadMusic() {
        bhajans.clear();
        DB.closeDatabase();
        DB = new DBHelperDownload(MusicActivity.this);

        Cursor cursor = DB.getData();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Downloads Available.", Toast.LENGTH_SHORT).show();
        }

        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String imageUrl = cursor.getString(2);
            String audioUrl = cursor.getString(3);
            String artist = cursor.getString(4);
            String duration = cursor.getString(5);
            String type = cursor.getString(6);
            String lyrics = cursor.getString(7);
            String keywords = cursor.getString(8);
            String size = cursor.getString(9);
            int downloads = cursor.getInt(10);

            BhajanModel bhajan = new BhajanModel(id, title, imageUrl, audioUrl, artist, duration, type, lyrics, keywords, size, downloads);
            bhajans.add(bhajan);
        }

        setAdapter(bhajans);
    }

    private void setAdapter(ArrayList<BhajanModel> bhajans) {
        ApplicationClass.bhajanModelArrayList = bhajans;
        bhajanAdapater = new BhajanAdapater(MusicActivity.this, bhajans, MusicActivity.this, MusicActivity.this, MusicActivity.this);
        recyclerView.setAdapter(bhajanAdapater);
    }
    private void setWindow() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.saffron));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();

        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }

    @Override
    public void onBhajanItemClick(int position, String title, String artist, String audioUrl, String imageUrl, String duration, String type) {
        currentPosition = position;
        ApplicationClass.bhajanModelArrayList = bhajans;

        Log.e(TAG, "Recieved in the bhajan Item Click");

        Intent intent = new Intent(this, MusicDetails.class);
        intent.putExtra("POSITION", position);
        startActivity(intent);
        Log.e(TAG, "Music Details Activity Started");
    }

    public void setMusicDetails(String title, String artist, String imageUrl) {
        bottomMusicLayout.setVisibility(View.VISIBLE);

        bottomTitleView.setText(title);
        bottomArtistView.setText(artist);
        Picasso.get().load(imageUrl).into(bottomIconView);

        if (MusicService.musicOn) {
            bottomPlayBtn.setImageResource(baseline_pause_white_24);
        } else {
            bottomPlayBtn.setImageResource(baseline_play_arrow_white_24);
        }

        bottomPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService != null) {
                    animateView(bottomPlayBtn);
                    musicService.playPausedBtnClicked();
                    if (musicService.isPlaying()) {
                        bottomPlayBtn.setImageResource(baseline_pause_white_24);
                    } else {
                        bottomPlayBtn.setImageResource(baseline_play_arrow_white_24);
                    }
                    updateProgressBar();
                }
            }
        });

        bProgressBar.setVisibility(View.VISIBLE);
        updateProgressBar();

        MusicActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateProgressBar();
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void updateProgressBar() {
        if (musicService != null && musicService.player != null) {
            long currentPosition = musicService.getCurrentPosition();
            long duration = musicService.getDuration();
            long currentMinutes = TimeUnit.MILLISECONDS.toMinutes(currentPosition);
            long currentSeconds = TimeUnit.MILLISECONDS.toSeconds(currentPosition) % 60;

            // Calculate the progress as a percentage
            int progress = (int) (currentPosition * 100 / duration);

            // Update the progress bar
            bProgressBar.setProgress(progress);

            // Schedule the next update after a delay (e.g., every second)
            handler.postDelayed(progressUpdateRunnable, 1000);
        }
    }

    @Override
    public void onBhajanDeleteListener(String imageUrl, String audioUrl) {
        Log.d(TAG, "deleteDownload: " + imageUrl);
        Log.d(TAG, "deleteDownload: " + audioUrl);
        if (imageUrl != null && audioUrl != null) {
            audioUrl = audioUrl.replace("file://", "");
            imageUrl = imageUrl.replace("file://", "");
            File imageFile = new File(imageUrl);
            File audioFile = new File(audioUrl);

            if (imageFile.exists() || audioFile.exists()) {
                boolean deletedImage = false;
                boolean deletedAudio = false;
                try {
                    deletedImage = imageFile.delete();
                    deletedAudio = audioFile.delete();
                } catch (Exception e) {
                    Toast.makeText(context, " " + e, Toast.LENGTH_SHORT).show();
                }

                if (deletedImage && deletedAudio) {
                    Toast.makeText(context, "File Deleted Successfully.", Toast.LENGTH_SHORT).show();
                } else if (deletedImage || deletedAudio) {
                    Toast.makeText(context, "Either one is removed.", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(this, MusicActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setWindow();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (musicService!= null) {
            unbindService(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);

        String valueTitle = sharedPreferences.getString(MUSIC_FILE_TITLE, null);
        String title = sharedPreferences.getString(MUSIC_TITLE, null);
        String artist = sharedPreferences.getString(MUSIC_ARTIST, null);
        String imageUrl = sharedPreferences.getString(MUSIC_ICON_URL, null);
        int position = sharedPreferences.getInt(MUSIC_POSITION, -1);

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);

        if (title != null && artist != null && imageUrl != null && position != -1 && MusicService.isRunning) {
            Log.e(TAG, "onResume: " + "Music Activity Resumed (Shared preferences are not null) ");
            SHOW_MINI_PLAYER = true;
            currentPosition = position;

            Log.e(TAG, "onResume: Calling setMusicDetails");
            if (MusicService.isRunning()) {
                bottomPlayBtn.setImageResource(baseline_pause_white_24);
                setMusicDetails(title, artist, imageUrl);
            }

        } else {
            SHOW_MINI_PLAYER = false;
        }
    }

    private void animateView(View view) {
        // Load the scale animations
        Animation scaleUpAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        Animation scaleDownAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        // Apply the animations to the View
        view.startAnimation(scaleUpAnimation);
        view.startAnimation(scaleDownAnimation);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        //Toast.makeText(this, "Music Service Connected.", Toast.LENGTH_SHORT).show();
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
        musicService.setCallBack(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

    @Override
    public void btn_play_pauseClicked() {
        if (musicService.isPlaying()) {
            musicService.pause();
            bottomPlayBtn.setImageResource(baseline_play_arrow_white_24);
            setMusicDetails(musicService.musicTitle(), musicService.musicArtist(), musicService.musicIconUrl());
        } else {
            musicService.start();
            bottomPlayBtn.setImageResource(baseline_pause_white_24);
            setMusicDetails(musicService.musicTitle(), musicService.musicArtist(), musicService.musicIconUrl());
        }
    }

    @Override
    public void btn_preClicked() {
        if (musicService!= null) {
            currentPosition = musicService.position;
            int position = currentPosition;
            if (musicService.isPlaying()) {
                Log.d(TAG, "btn_preClicked: ");
                musicService.stop();
                musicService.release();

                if (position > 0) {
                    position = position - 1;
                }

                musicService.createPlayer(position);

                musicService.start();
                setMusicDetails(musicService.musicTitle(), musicService.musicArtist(), musicService.musicIconUrl());
            } else {
                musicService.stop();
                musicService.release();

                if (position > 0) {
                    position = position - 1;
                }
                musicService.createPlayer(position);

                musicService.pause();
                setMusicDetails(musicService.musicTitle(), musicService.musicArtist(), musicService.musicIconUrl());
            }
        }
    }

    @Override
    public void btn_nextClicked() {
        if (musicService!= null) {
            int position = musicService.position;
            if (musicService.isPlaying()) {
                musicService.stop();
                musicService.release();

                if (position < bhajans.size() - 1) {
                    position = (position + 1);
                }

                musicService.createPlayer(position);
                musicService.start();

                setMusicDetails(musicService.musicTitle(), musicService.musicArtist(), musicService.musicIconUrl());
            } else {
                musicService.stop();
                musicService.release();

                if (position < bhajans.size() - 1) {
                    position = position + 1;
                }
                musicService.createPlayer(position);

                musicService.pause();
                setMusicDetails(musicService.musicTitle(), musicService.musicArtist(), musicService.musicIconUrl());
            }
        }
    }

    @Override
    public void downloadBtnClicked() {
//        Toast.makeText(mContext, "Download Btn Clicked", Toast.LENGTH_SHORT).show();
//        loadInterstitialAd();
        showInterstitialAd();
    }

    @Override
    public void onBackPressed() {
        if (layoutSearch.getVisibility() == View.VISIBLE) {
            layoutSearch.setVisibility(View.GONE);
            layoutTop.setVisibility(View.VISIBLE);
        } else {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
        }
    }
}