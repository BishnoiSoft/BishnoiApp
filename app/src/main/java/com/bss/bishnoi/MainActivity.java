package com.bss.bishnoi;

import static android.content.ContentValues.TAG;

import static com.bss.bishnoi.MusicService.MUSIC_ARTIST;
import static com.bss.bishnoi.MusicService.MUSIC_FILE_TITLE;
import static com.bss.bishnoi.MusicService.MUSIC_ICON_URL;
import static com.bss.bishnoi.MusicService.MUSIC_LAST_PLAYED;
import static com.bss.bishnoi.MusicService.MUSIC_POSITION;
import static com.bss.bishnoi.MusicService.MUSIC_TITLE;
import static com.bss.bishnoi.R.drawable.baseline_pause_white_24;
import static com.bss.bishnoi.R.drawable.baseline_play_arrow_white_24;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bss.bishnoi.adapters.BhajanHorizontalAdapter;
import com.bss.bishnoi.adapters.HorizontalScrollAdapter;
import com.bss.bishnoi.interfaces.ActionPlaying;
import com.bss.bishnoi.interfaces.BhajanItemClickListner;
import com.bss.bishnoi.interfaces.UpReItemClickListner;
import com.bss.bishnoi.models.BhajanModel;
import com.bss.bishnoi.utils.DBBhajanHelper;
import com.bss.bishnoi.utils.DBHelperDownload;
import com.bss.bishnoi.utils.NetworkUtils;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements UpReItemClickListner, BhajanItemClickListner, ServiceConnection, ActionPlaying {
    private RecyclerView upperRecyclerView;
    private HorizontalScrollAdapter horizontalScrollAdapter;
    TextView tDate, tTithi;
    ImageView profile_photo, btn_store;
    String uPhone, fProfileUrl;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    // From Astroapi
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String URL = "https://json.freeastrologyapi.com/complete-panchang";
    private static final String API_KEY = "IKqbf8p7yk1MLBqsE4cR011bdxON5JeZ5NOuuQ6D";

    // for Location
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationManager locationManager;
    private double longitude;
    private double latitude;

    private RecyclerView bhajanRecyclerView, artiRecyclerView;
    private TextView seeAllArti, seeAllBhajan;
    private BhajanHorizontalAdapter bhajanHorizontalAdapter, artiHorizontalAdapter, downloadHorizontalAdapter;

    ArrayList<BhajanModel> bhajans = new ArrayList<>();
    ArrayList<BhajanModel> artis = new ArrayList<>();
    ArrayList<BhajanModel> downloads = new ArrayList<>();

    int currentPosition;

    // For Bottom Music playing Layout
    ImageView bottomIconView, bottomPlayBtn;
    TextView bottomTitleView, bottomArtistView;
    RelativeLayout layoutButtonPlay, bottomMusicLayout;
    ProgressBar bProgressBar;

    Context context = MainActivity.this;
    private Runnable progressUpdateRunnable;

    MusicService musicService;

    private Handler handler = new Handler();

    boolean SHOW_MINI_PLAYER = false;

    private boolean backPressedOnce = false;

    CardView panchangCard, cardAmavysya, cardRules, cardShabadwani, cardUpdate, cardDownload, cardJambhaniSahitya;
    Button btnUpdate;
    FirebaseDatabase updateDatabase;
    DatabaseReference updateReference;

    ImageView searchIcon, helpIcon;

    TextView btnShareWhatsapp;

    String first_line = "";
    String second_line = "";
    String third_line = "";

    TextView btvTerms, btvAbout, btvDisclaimer;

    private ReviewInfo reviewInfo;
    private ReviewManager reviewManager;

    int daysSinceCreation;
    String panchangHTML, dpTithi, dpMonth, dpVikaramSamvat, dpPaksha;
    RelativeLayout artiLayout, bhajanLayout;

    DBBhajanHelper DB;

    private Context mContext;

    Boolean mainNAd = false;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        setWindow();
        initViews();
        setUpperIcons();

        // Initializing the Google Admob SDK
        MobileAds.initialize (this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete( InitializationStatus initializationStatus ) {

                //Showing a simple Toast Message to the user when The Google AdMob Sdk Initialization is Completed
                //Toast.makeText (MainActivity.this, "AdMob Sdk Initialize "+ initializationStatus.toString(), Toast.LENGTH_LONG).show();
            }
        });
        checkAds();

        DB = new DBBhajanHelper(MainActivity.this);

        initMusicLayout();

        setUpperRecyclerView();

        checkInterentState();

        setUpAd();
    }

    private void checkAds() {
        DatabaseReference adsReference = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("ads");

        adsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mainNAd = (Boolean) snapshot.child("mainNAd").getValue();
                //Toast.makeText(MainActivity.this, "mainNAd" + mainNAd, Toast.LENGTH_SHORT).show();
                if (Boolean.TRUE.equals(mainNAd)) {
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

    private void checkInterentState() {
        if (!NetworkUtils.isInternetConnected(this)) {
            Toast.makeText(this, "Internet is Not Connected", Toast.LENGTH_SHORT).show();
            artiLayout.setVisibility(View.GONE);
            bhajanLayout.setVisibility(View.GONE);
            cardAmavysya.setVisibility(View.GONE);
            NetworkUtils.showNoInternetDialog(this);
        } else {
            Log.d(TAG, "checkInterentState: Internet is connected!!");
            getUserLocation();

            getCurrentUserInfo();
            setUpUpdate();

            Thread panchang = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Dainik Panchang");

                    try {
//                    Log.i(TAG, "Dainik Panchang" );

                        Document doc = Jsoup.connect("https://www.drikpanchang.com/").timeout(6000).get();
//                    Log.i(TAG, "Dainik Panchang" + doc.toString());

                        Elements hTithi = doc.select(".dpPHeaderLeftContent");
                        Element hMonth = doc.select(".dpPHeaderLeftTitle").get(0);

                        Log.i(TAG, "Dainik Panchang" + hMonth.toString());

                        panchangHTML = hTithi.html();
                        dpMonth = hMonth.html().split(" ")[1];

                    } catch (IOException e) {
                        Log.i(TAG, "Dainik Panchang" + e);

                        //throw new RuntimeException(e);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {


                                Log.d(TAG, "Dainik Panchang: \n" + panchangHTML);
                                String regex = "<div>";
                                panchangHTML = panchangHTML.replaceAll(regex, "");

                                String regex1 = "</div>";
                                panchangHTML = panchangHTML.replaceAll(regex1, "");

                                panchangHTML = panchangHTML.split(dpMonth)[1];

                                Log.d(TAG, "PanchangHTML: " + panchangHTML.length());
                                panchangHTML = panchangHTML.replaceAll("  ", "");
                                panchangHTML = panchangHTML.replaceAll("\n", "");

//                        Log.d(TAG, "Panchang : " + panchangHTML.split("\\s+")[0] + "\n" + panchangHTML.split("\\s+")[1] +
//                                "\n" + panchangHTML.split("\\s+")[2] +
//                                "\n" + panchangHTML.split("\\s+")[3] +
//                                "\n" + panchangHTML.split("\\s+")[4]);

                                dpPaksha = panchangHTML.split("\\s+")[1];
                                dpTithi = panchangHTML.split("\\s+")[3];
                                dpVikaramSamvat = panchangHTML.split("\\s+")[4];

                                setDainikPanchangText(dpMonth, dpTithi, dpPaksha, dpVikaramSamvat);
                            } catch (Exception e) {
                                Log.d(TAG, "Panchang Error : " + e);
                            }
                        }
                    });

                }

            });
            panchang.start();

            activeReviewInfo();
            checkUserCreationDays();
            startReviewFlow();
            checkForUpdate();
        }
    }

    public void activeReviewInfo() {
        reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> managerInfoTask = reviewManager.requestReviewFlow();
        managerInfoTask.addOnCompleteListener((task)->
        {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            } else {
                Log.d(TAG,"Review Fails to Start");
            }
        });
    }

    public void startReviewFlow() {

        if (daysSinceCreation % 10 == 0) {
            if (reviewInfo != null) {
                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);

                flow.addOnCompleteListener(task -> {
                    Toast.makeText(this, "Rating has completed.", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    public void checkUserCreationDays() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Retrieve the custom creation date attribute.
            Object creationDateObject = user.getMetadata().getCreationTimestamp();
            if (creationDateObject != null) {
                long creationDate = Long.parseLong(creationDateObject.toString());

                // Calculate the number of days since creation date.
                long currentTime = System.currentTimeMillis();
                daysSinceCreation = (int) ((currentTime - creationDate) / (1000 * 60 * 60 * 24));

                // Now, 'daysSinceCreation' contains the number of days since user creation.
            }
        }
    }

    private void setUpperIcons() {
        searchIcon = findViewById(R.id.icon_search);
        helpIcon = findViewById(R.id.icon_help);

        searchIcon.setVisibility(View.GONE);
        helpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityHelp.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        panchangCard = findViewById(R.id.panchangCard);
        profile_photo = findViewById(R.id.icon_profile);
        tTithi = findViewById(R.id.tithi);
        tDate = findViewById(R.id.date);
        btnShareWhatsapp = findViewById(R.id.btn_share_whatsapp);

        cardUpdate = findViewById(R.id.card_update);
        btnUpdate = findViewById(R.id.btnUpdate);

        bhajanLayout = findViewById(R.id.bhajan_layout);
        artiLayout = findViewById(R.id.aarti_layout);

        cardAmavysya = findViewById(R.id.cardAmavysya);
        cardShabadwani = findViewById(R.id.cardShabadwani);
        cardRules = findViewById(R.id.cardRules);
        cardDownload = findViewById(R.id.cardDownload);
        cardJambhaniSahitya = findViewById(R.id.cardSahitya);
        setCardClick();

        btvDisclaimer = findViewById(R.id.btv_disclaimer);
        btvAbout = findViewById(R.id.btv_about);
        btvTerms = findViewById(R.id.btv_terms);
        setFooterClick();

        btn_store = findViewById(R.id.icon_store);
        btn_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShowProfile.class);
                intent.putExtra("activity", "main");
                startActivity(intent);
                finish();
            }
        });


        seeAllArti = findViewById(R.id.text_see_all_arti);
        seeAllArti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                intent.putExtra("QUERY", " आरती ");
                startActivity(intent);
            }
        });

        seeAllBhajan = findViewById(R.id.text_see_all_bhajan);
        seeAllBhajan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                intent.putExtra("QUERY", " भजन ");
                startActivity(intent);
            }
        });

        artiRecyclerView = findViewById(R.id.artiRecyclerView);
        bhajanRecyclerView = findViewById(R.id.bhajanRecyclerView);

        artiRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bhajanRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        boolean databaseExists = DBBhajanHelper.doesDatabaseExist(context);
//        if (databaseExists) {
//            Log.d(TAG, "setUpLocalMusic: Getting data from Local database.");
//            getLocalMusicDetails();
//        } else {
//            getFirebaseMusic();
//        }
        getFirebaseMusic();
    }

    private void getCurrentUserInfo() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent_login = new Intent(MainActivity.this, Login.class);
            startActivity(intent_login);
            finish();
        }

        try {
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
        } catch (Exception e){
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }

    }

    private void setFooterClick() {
        btvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(btvTerms);
                Intent intent = new Intent(context, TermsConditions.class);
                startActivity(intent);
            }
        });


        btvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(btvAbout);
                Intent intent = new Intent(context, ActivityAbout.class);
                startActivity(intent);
            }
        });


        btvDisclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(btvDisclaimer);
                Intent intent = new Intent(context, ActivityDisclaimer.class);
                startActivity(intent);
            }
        });
    }

    private void setUpUpdate() {
        cardUpdate.setVisibility(View.GONE);

        updateDatabase = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app/");
        updateReference = updateDatabase.getReference("update");

        updateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String updateAvailable = (String) snapshot.child("updateAvailable").getValue();
                String playStoreUrl = (String) snapshot.child("playStoreUrl").getValue();
                if (updateAvailable.equals("true")) {
                    cardUpdate.setVisibility(View.VISIBLE);
                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl));

                            // Verify that the Play Store app is available on the device before starting the intent
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setCardClick() {
        cardAmavysya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(cardAmavysya);
                Intent intent = new Intent(context, AmavysyaJankari.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        cardRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(cardRules);
                Intent intent = new Intent(context, Rules.class);
                intent.putExtra("QUERY", " 29 नियम ");
                startActivity(intent);
            }
        });

        cardShabadwani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(cardShabadwani);
                Intent intent = new Intent(context, Shabadwani.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        cardJambhaniSahitya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(cardJambhaniSahitya);
                Intent intent = new Intent(context, SahityaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        cardDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(cardDownload);
                Intent intent = new Intent(context, MusicActivity.class);
                intent.putExtra("QUERY", " डाउनलोड ");
                startActivity(intent);
            }
        });

        btnShareWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(btnShareWhatsapp);
                String text = "Download the Bishnoi app from play store : https://play.google.com/store/apps/details?id=com.bss.bishnoi";

                Uri imageUri = shareImageUri(first_line, second_line, third_line);

                Log.d(TAG, "onClick: " + imageUri);

                int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                String packageName = "com.whatsapp"; // Replace this with the package name of the receiving app
                getApplicationContext().grantUriPermission(packageName, imageUri, flags);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.setPackage("com.whatsapp");

                // Verify that WhatsApp is installed before starting the intent
                try {
                    Log.d(TAG, "onClick: Package manager is not null");
                    startActivity(Intent.createChooser(shareIntent, "Share Image on WhatsApp Status"));
                } catch (Exception e){
                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initMusicLayout() {
        bottomIconView = findViewById(R.id.bottomIconView);
        bottomTitleView = findViewById(R.id.bottomTitleView);
        bottomArtistView = findViewById(R.id.bottomArtistView);
        bottomPlayBtn = findViewById(R.id.bottomPlayButton);
        layoutButtonPlay = findViewById(R.id.layout_button_play);
        bProgressBar = findViewById(R.id.currentMusicProgress);
        bProgressBar.setVisibility(View.GONE);

        handler = new Handler();
        progressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateProgressBar();
            }
        };

        bottomMusicLayout = findViewById(R.id.bottom_play_layout);
        bottomLayoutOnClick();
    }

    private void bottomLayoutOnClick() {
        bottomMusicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicDetails.class);
                intent.putExtra("POSITION", currentPosition);
                startActivityForResult(intent, 1);
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

    private void animateView(View view) {
        // Load the scale animations
        Animation scaleUpAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        Animation scaleDownAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        // Apply the animations to the View
        view.startAnimation(scaleUpAnimation);
        view.startAnimation(scaleDownAnimation);
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

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateProgressBar();
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void setWindow() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.saffron));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.saffron));

        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();

        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }

    private void setUpperRecyclerView() {
        ArrayList<String> upper_scroll_items = new ArrayList<>();
        upper_scroll_items.add(" शब्दवाणी ");
        upper_scroll_items.add(" अमावस्या जानकारी ");
        upper_scroll_items.add(" भजन ");
        upper_scroll_items.add(" आरती ");
        upper_scroll_items.add(" 29 नियम ");
//        upper_scroll_items.add(" तीर्थ स्थल ");
        upper_scroll_items.add(" डाउनलोड ");
        upper_scroll_items.add(" जाम्भाणी साहित्य ");
//        upper_scroll_items.add(" Upload ");

        upperRecyclerView = findViewById(R.id.upper_recycler_view);
        upperRecyclerView.setHasFixedSize(true);
        upperRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Using Recycler View
        horizontalScrollAdapter = new HorizontalScrollAdapter(MainActivity.this, upper_scroll_items, this);
        upperRecyclerView.setAdapter(horizontalScrollAdapter);
    }

    @Override
    public void onItemClick(int position, String clickedItem) {
        Context context = getApplicationContext();

        if (clickedItem.equals(" शब्दवाणी ")) {
            Intent intent = new Intent(context, Shabadwani.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (clickedItem.equals(" अमावस्या जानकारी ")) {
            Intent intent = new Intent(context, AmavysyaJankari.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (clickedItem.equals(" भजन ")) {
            Intent intent = new Intent(context, MusicActivity.class);
            intent.putExtra("QUERY", " भजन ");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (clickedItem.equals(" आरती ")) {
            Intent intent = new Intent(context, MusicActivity.class);
            intent.putExtra("QUERY", " आरती ");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (clickedItem.equals(" 29 नियम ")) {
            Intent intent = new Intent(context, Rules.class);
            intent.putExtra("QUERY", " 29 नियम ");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else if (clickedItem.equals(" तीर्थ स्थल ")) {
            Intent intent = new Intent(context, TirthSthals.class);
            intent.putExtra("QUERY", " तीर्थ स्थल ");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else if (clickedItem.equals(" डाउनलोड ")) {
            Intent intent = new Intent(context, MusicActivity.class);
            intent.putExtra("QUERY", " डाउनलोड ");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (clickedItem.equals(" Upload ")) {
            Intent intent = new Intent(context, UploadMusic.class);
            intent.putExtra("QUERY", " Upload ");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (clickedItem.equals(" जाम्भाणी साहित्य ")) {
            Intent intent = new Intent(context, SahityaActivity.class);
            intent.putExtra("QUERY", " Upload ");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void getUserLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            // Update the TextView with the latitude and longitude
            // locationTextView.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            // Called when the user enables the location provider
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            // Called when the user disables the location provider
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Called when the status of the location provider changes
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                // Location permissions not granted, handle accordingly
                // ...
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    public void setDainikPanchangText(String dpMonth, String dpTithi, String dpPaksha, String dpVikaramSamvat) {

        switch (dpMonth.toLowerCase()) {
            case "chaitra":
               dpMonth = "चैत्र";
               break;
            case "vaishakha":
                dpMonth = "वैशाख";
                break;
            case "jyeshtha":
                dpMonth = "ज्येष्ठ";
                break;
            case "ashadha":
                dpMonth = "आषाढ़";
                break;
            case "sharvana":
                dpMonth = "श्रावण";
                break;
            case "bhadrapada":
                dpMonth = "भाद्रपद";
                break;
            case "ashwina":
                dpMonth = "आश्विन";break;
            case "kartika":
                dpMonth = "कार्तिक";break;
            case "margashirsha":
                dpMonth = "मार्गशीर्ष";break;
            case "pausha":
                dpMonth = "पौष";break;
            case "magha":
                dpMonth = "माघ";break;
            case "phalguna":
                dpMonth = "फाल्गुन";break;
        }

        switch (dpTithi.toLowerCase()) {
            case "pratipada":
                dpTithi = "प्रतिपदा";break;
            case "dwitiya":
                dpTithi = "द्वितीया";break;
            case "tritiya":
                dpTithi = "तृतीया";break;
            case "chaturthi":
                dpTithi = "चतुर्थी";break;
            case "panchami":
                dpTithi = "पंचमी";break;
            case "shashthi":
                dpTithi = "षष्ठी";break;
            case "saptami":
                dpTithi = "सप्तमी";break;
            case "ashtami":
                dpTithi = "अष्टमी";break;
            case "navami":
                dpTithi = "नवमी";break;
            case "dashami":
                dpTithi = "दशमी";break;
            case "ekadashi":
                dpTithi = "एकादशी";break;
            case "dwadashi":
                dpTithi = "द्वादशी";break;
            case "trayodashi":
                dpTithi = "त्रयोदशी";break;
            case "chaturdashi":
                dpTithi = "चतुर्दशी";break;
            case "purnima":
                dpTithi = "पूर्णिमा";break;
            case "amavasya":
                dpTithi = "अमावस्या";break;
        }

        switch (dpPaksha.toLowerCase()) {
            case "krishna":
                dpPaksha = "कृष्ण पक्ष";break;
            case "shukla":
                dpPaksha = "शुक्ल पक्ष";break;
        }

        first_line = " विक्रम संवत " + dpVikaramSamvat;
        second_line = dpMonth + " माह ";
        third_line =  dpPaksha + " " + dpTithi;
        String todayTithi = first_line + " \n" + second_line + "\n" + third_line;
        Log.d(TAG, "onResponse: " + todayTithi);
        tTithi.setText(todayTithi);
        panchangCard.setVisibility(View.VISIBLE);

    }

    private void setPanchangText(String vikaramSamvat, int lunarMonthNumber, int tithiNumber) {

        String[] hindiMonths = {
                "चैत्र", "वैशाख", "ज्येष्ठ", "आषाढ़", "श्रावण", "भाद्रपद",
                "आश्विन", "कार्तिक", "अग्रहायण", "पौष", "माघ", "फाल्गुन"
        };

        String[] tithiNames = {
                "प्रतिपदा", "द्वितीया", "तृतीया", "चतुर्थी", "पंचमी", "षष्ठी",
                "सप्तमी", "अष्टमी", "नवमी", "दशमी", "एकादशी", "द्वादशी",
                "त्रयोदशी", "चतुर्दशी", "पूर्णिमा", "प्रतिपदा", "द्वितीया",
                "तृतीया", "चतुर्थी", "पंचमी", "षष्ठी", "सप्तमी", "अष्टमी",
                "नवमी", "दशमी", "एकादशी", "द्वादशी", "त्रयोदशी", "चतुर्दशी", "अमावस्या"
        };


        String paksha = "";
        tithiNumber = tithiNumber - 15;

        if (tithiNumber < 15 && tithiNumber > 0) {
            paksha = "कृष्ण पक्ष";
            lunarMonthNumber = lunarMonthNumber+1;
        } else if (tithiNumber < 0) {
            tithiNumber = -tithiNumber % 15;
            paksha = "शुक्ल पक्ष";
        }

        String lunarMonthName = hindiMonths[lunarMonthNumber-1];
        String lunarDayName = tithiNames[tithiNumber-1];

        first_line = " विक्रम संवत " + vikaramSamvat;
        second_line = lunarMonthName + " माह ";
        third_line =  paksha + " " + lunarDayName;
        String todayTithi = first_line + " \n" + second_line + "\n" + third_line;
        Log.d(TAG, "onResponse: " + todayTithi);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tTithi.setText(todayTithi);
            }
        });
    }

    public void setUpAd() {

    }

    public Uri shareImageUri(String first_line, String second_line, String third_line) {
        Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.panchang_bg);
        Bitmap bitmap = Bitmap.createBitmap(backgroundImage.getWidth(), backgroundImage.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        canvas.drawBitmap(backgroundImage, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffa500"));
        paint.setTextSize((float) (2*40*2.16));
        paint.setTextAlign(Paint.Align.CENTER);

        float x = canvas.getWidth()/2f;

        canvas.drawText("-:" + " आज का पंचांग " + ":-", x, (float) (200*2.16), paint);
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setTextSize((float) (2*40*2.16));
        canvas.drawText(first_line, x, (float) (2*180*2.16), paint);
        canvas.drawText(second_line, x, (float) (2*260*2.16), paint);
        canvas.drawText(third_line, x, (float) (2*340*2.16), paint);
        paint.setColor(Color.parseColor("#FFC857"));
        paint.setTextSize((float) (2*40*2.16));
        canvas.drawText("!! निवण प्रणाम !!", x, (float) (2*440*2.16), paint);
        paint.setColor(Color.parseColor("#FFC857"));
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setTextSize((float) (1.5*40*2.16));
        canvas.drawText("Bishnoi App", x, (float) (2*440*2.16 + 500), paint);

        // Get the cache directory
        File cacheDir = getCacheDir();

        // Create a new image file with a unique file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        File imageFile = new File(cacheDir, imageFileName);

        try {
            // Create the file
            if (imageFile.createNewFile()) {
                Log.d("MainActivity", "Image file created: " + imageFile.getAbsolutePath());
            } else {
                Log.e("MainActivity", "Image file creation failed!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri imageUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName()+".provider", imageFile);
        Log.d(TAG, "shareImageUri: " + imageUri);
        return imageUri;
    }

    public void getFirebaseMusic() {
        Log.d(TAG, "SetupLocalMusic: Getting data from firebase.....");
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference("music");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bhajans.clear();
                artis.clear();

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

                    Log.d(TAG, "FirebaseData:  "+title + imageUrl + audioUrl+artist+credits+lyrics);

                    int downloads;
                    try {
                        downloads = (int) dataSnapshot.child("downloads").getValue();
                    } catch (NullPointerException e){
                        Log.d(TAG, "onDataChange: "+e);
                        downloads = 0;
                    }

                    BhajanModel bhajan = new BhajanModel(id, title, imageUrl, audioUrl, artist, duration, type, lyrics, keywords,size, downloads);

//                    Log.d(TAG, "SetupLocalMusic: Inserting data in local database");
//                    DBBhajanHelper DB = new DBBhajanHelper(context);
//
//                    Boolean checkInsertData = DB.insertBhajanData(bhajan);
//                    if (checkInsertData) {
//                        Log.d(TAG, "SaveData: " + "Entry Inserted \n" + bhajan.getImageUrl() + "\n" + bhajan.getAudioUrl());
//                    } else {
//                        Log.d(TAG, "SaveData: " + "Some error occured.");
//                    }

                    if (type.equals(" भजन ")) {
                        bhajans.add(bhajan);
                    } else if (type.equals(" आरती ")) {
                        artis.add(bhajan);
                    }
                }
                setAdapters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLocalMusicDetails() {
        bhajans.clear();
        Log.d(TAG, "getLocalMusicDetails: Getting data from local database....");

        DB = new DBBhajanHelper(MainActivity.this);

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
            if (type.equals(" भजन ")) {
                bhajans.add(bhajan);
            } else if (type.equals(" आरती ")) {
                artis.add(bhajan);
            }
        }
        Log.d(TAG, "getLocalMusicDetails: Bhajan Lenght" + bhajans.size());
        setAdapters();
    }

    @Override
    public void onBhajanItemClick(int position, String title, String artist, String audioUrl, String iamgeUrl, String duration, String type) {
        currentPosition = position;
        if (type.trim().equals("भजन")) {
            ApplicationClass.bhajanModelArrayList = bhajans;
        } else if (type.trim().equals("आरती")) {
            ApplicationClass.bhajanModelArrayList = artis;
        }

        Log.e(TAG, "Recieved in the bhajan Item Click");

        Intent intent = new Intent(this, MusicDetails.class);
        intent.putExtra("POSITION", currentPosition);
        startActivity(intent);
        Log.e(TAG, "Music Details Activity Started");
    }

    private void setAdapters() {

        ArrayList<BhajanModel> bhajanList;
        if (bhajans.size() >= 5) {
            ArrayList<BhajanModel> tempBhajanList = new ArrayList<>(bhajans);
            long seed = System.nanoTime();
            Collections.shuffle(tempBhajanList, new Random(seed)); // Shuffle the list randomly
            bhajanList = new ArrayList<>(tempBhajanList.subList(0,5));
        } else {
           bhajanList  = bhajans;
        }
        bhajanHorizontalAdapter = new BhajanHorizontalAdapter(this, bhajanList, this);
        bhajanRecyclerView.setAdapter(bhajanHorizontalAdapter);

        ArrayList<BhajanModel> artiList;
        if (artis.size() >= 5) {
            ArrayList<BhajanModel> tempArtiList = new ArrayList<>(artis);
            long seed1 = System.nanoTime();
            Collections.shuffle(tempArtiList, new Random(seed1)); // Shuffle the list randomly
            artiList = new ArrayList<>(tempArtiList.subList(0, 5));
        } else {
            artiList = artis;
        }
        artiHorizontalAdapter = new BhajanHorizontalAdapter(this, artiList, this);
        artiRecyclerView.setAdapter(artiHorizontalAdapter);
        downloadHorizontalAdapter = new BhajanHorizontalAdapter(this, downloads, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

//        Toast.makeText(this, "" + title + artist + imageUrl, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);

        if (title != null && artist != null && imageUrl != null && position != -1 && MusicService.isRunning()) {
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

    @Override
    protected void onPause() {
        super.onPause();
        if (musicService!= null) {
            unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
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
    public void onBackPressed() {
        if (backPressedOnce) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
        else {
            backPressedOnce = true;
            Toast.makeText(context, "Press Again to Exit", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkForUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.

//                appUpdateManager.startUpdateFlowForResult(
//                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                        appUpdateInfo,
//                        // an activity result launcher registered via registerForActivityResult
//                        activityResultLauncher,
//                        // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
//                        // flexible updates.
//                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}