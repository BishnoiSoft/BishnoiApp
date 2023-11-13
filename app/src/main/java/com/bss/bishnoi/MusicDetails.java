package com.bss.bishnoi;

import static android.content.ContentValues.TAG;
import static com.bss.bishnoi.ApplicationClass.context;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bss.bishnoi.databinding.ActivityMusicBinding;
import com.bss.bishnoi.databinding.ActivityMusicDetailsBinding;
import com.bss.bishnoi.interfaces.ActionPlaying;
import com.bss.bishnoi.models.BhajanModel;
import com.bss.bishnoi.utils.DBHelperDownload;
import com.bss.bishnoi.utils.MusicDownloader;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MusicDetails extends AppCompatActivity implements ActionPlaying, ServiceConnection {

    TextView tvTitle, tvArtist, duration_played, duration_total;
    ImageView bhajan_icon, nextBtn, prevBtn, downloadBtn, likeBtn, closeBtn, listBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<BhajanModel> bhajans = new ArrayList<>();

    MusicService musicService;

    private final Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;

    DBHelperDownload dbHelperDownload;

    Boolean isLiked = false;
    int downloadClick;
    String currentBhajanTitle;

    ConstraintLayout parent_layout;

    // For interstitial Ad

    private InterstitialAd mInterstitialAd;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    long timeDelay = 120;

    boolean isActive = false;

    private ActivityMusicDetailsBinding mBinding;
    private Context mContext;

    private NativeAd mNativeAd;

    Boolean musicDetailsIAd = false;
    Boolean musicDetailsNAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_details);

        isActive = true;
        mContext = this;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        downloadClick = 1;

        setWindow();
        initViews();
        getIntentMethod();

        checkAds();

        dbHelperDownload = new DBHelperDownload(this);
        setOnClickListeners();
        checkDownload();

        handleSeekBar();
        MusicDetails.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null) {
                    long currentPosition = musicService.getCurrentPosition();
                    long totalDuration = musicService.getDuration();
                    seekBar.setProgress((int) (currentPosition * 100 / totalDuration));
                    duration_played.setText(formattedTime(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void checkAds() {
        Log.e(TAG, "ADManagement: Posiiton 1");
        DatabaseReference adsReference = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("ads");

        adsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                musicDetailsIAd = (Boolean) snapshot.child("musicDetailsIAd").getValue();
                musicDetailsNAd = (Boolean) snapshot.child("musicDetailsNAd").getValue();

                if (Boolean.TRUE.equals(musicDetailsNAd)) {
                    loadNativeAd();
                }
                if (Boolean.TRUE.equals(musicDetailsIAd)) {
                    loadInterstitialAd();
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
                        Log.e(TAG, "ADManagement: Calling Show Interstitial Ad 1");
                        showInterstitialAd();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                        loadInterstitialAd();
                    }
                });
    }

    private void showInterstitialAd() {
        Log.e(TAG, "ADManagement: Showing Intersitial Ad");
        if (mInterstitialAd != null) {
            Log.e(TAG, "ADManagement: Intersitital Ad is not null");
            mInterstitialAd.show(MusicDetails.this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mInterstitialAd = null;
//                    loadInterstitialAd();
//                    Log.e(TAG, "ADManagement: Calling Show Intersittial Ad 2");
//                    showInterstitialAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    mInterstitialAd = null;
                    loadInterstitialAd();
                    Log.e(TAG, "ADManagement: Calling Show Intersittial Ad 3");
                    showInterstitialAd();

                }
            });
        } else {
            loadInterstitialAd();
        }
    }

    public void showInstantInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MusicDetails.this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mInterstitialAd = null;
                    loadInterstitialAd();
                    Log.e(TAG, "ADManagement: Calling Show Intersittial Ad 4");
                    showInterstitialAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    mInterstitialAd = null;
                    loadInterstitialAd();
                    Log.e(TAG, "ADManagement: Calling Show Intersittial Ad 5");
                    showInterstitialAd();
                }
            });
        }
    }


    private void checkDownload() {
        if (dbHelperDownload.checkDataExists(bhajans.get(position).getTitle())) {
            downloadBtn.setVisibility(View.INVISIBLE);
        } else {
            downloadBtn.setVisibility(View.VISIBLE);
        }
    }

    private String formattedTime(long timestamp) {
        String totalOut = "";
        String totalNew = "";
        long seconds = timestamp / 1000;
        long minutes = seconds / 60;

        int remainderSeconds = (int) (seconds % 60);
        int remainderMinutes = (int) (minutes % 60);

        totalOut = remainderMinutes + ":" + remainderSeconds;
        totalNew = remainderMinutes + ":" + "0" + remainderSeconds;

        if (String.valueOf(remainderSeconds).length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }

    private void getIntentMethod() {

        position = getIntent().getIntExtra("POSITION", -1);

//        try {
//            currentBhajanTitle = getIntent().getStringExtra("BHAJAN_TITLE");
//        }

        Log.e(TAG, "getIntentMethod: Get Intent Method Run");
        bhajans = ApplicationClass.bhajanModelArrayList;
        
        if (bhajans != null) {
            playPauseBtn.setImageResource(R.drawable.baseline_pause_saffron_24);
        }

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("ServicePosition", position);
        startService(intent);
        Log.e(TAG, "Music Service Started");
    }

    public void initViews() {
        parent_layout = findViewById(R.id.main_layout);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        bhajan_icon = findViewById(R.id.musicIcon);
        nextBtn = findViewById(R.id.skip_next);
        prevBtn = findViewById(R.id.skip_prev);
        downloadBtn = findViewById(R.id.download);
        likeBtn = findViewById(R.id.like);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);

        closeBtn = findViewById(R.id.close_btn);
        listBtn = findViewById(R.id.list_btn);

//        Log.d(TAG, "initViews: Views Initiated.");
        int[] colors = generateGradientColors(200, "#FFD3A578", "#FFD700", "#000000"); // Generate 200 rainbow colors

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, colors);

        parent_layout.setBackground(gradientDrawable);
    }

    private int[] generateGradientColors(int numberOfColors, String startColor, String middleColor, String endColor) {
        int[] colors = new int[numberOfColors];

        float[] startHSV = new float[3];
        float[] middleHSV = new float[3];
        float[] endHSV = new float[3];

        android.graphics.Color.colorToHSV(android.graphics.Color.parseColor(startColor), startHSV);
        android.graphics.Color.colorToHSV(android.graphics.Color.parseColor(middleColor), middleHSV);
        android.graphics.Color.colorToHSV(android.graphics.Color.parseColor(endColor), endHSV);

        for (int i = 0; i < numberOfColors; i++) {
            float ratio = (float) i / (float) (numberOfColors - 1);
            float[] resultHSV = new float[3];

            // Interpolate the HSV values at the current position
            for (int j = 0; j < 3; j++) {
                resultHSV[j] = interpolate(startHSV[j], middleHSV[j], endHSV[j], ratio);
            }

            colors[i] = android.graphics.Color.HSVToColor(resultHSV);
        }

        return colors;
    }

    private float interpolate(float a, float b, float c, float ratio) {
        return a + ratio * (c - a);
    }

    public void setOnClickListeners() {
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(closeBtn);
                Intent mainIntent = new Intent(MusicDetails.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(listBtn);
                Intent musicListIntent = new Intent(MusicDetails.this, MusicActivity.class);
                startActivity(musicListIntent);
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(downloadBtn);
                //showInstantInterstitialAd();
                if (!dbHelperDownload.checkDataExists(bhajans.get(position).getTitle()) && downloadClick ==1) {

                    MusicDownloader musicDownloader = new MusicDownloader(MusicDetails.this, bhajans.get(position));
                    musicDownloader.startDownload();
                    downloadClick = 0;
                    downloadBtn.setVisibility(View.INVISIBLE);
                    Toast.makeText(MusicDetails.this, "Download Started", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MusicDetails.this, "Download Already Exists.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(likeBtn);
                if (!isLiked) {
                    likeBtn.setImageResource(R.drawable.baseline_thumb_up_alt_white_24);
                    isLiked = true;
                } else {
                    likeBtn.setImageResource(R.drawable.baseline_thumb_up_off_alt_white_24);
                    isLiked = false;
                }
            }
        });
    }

    public void handleSeekBar() {
        seekBar.setMax(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (musicService != null) {
                        progress = (int) (progress * musicService.getDuration())/100;
                        musicService.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }
        });
    }

    public void setUpData() {
        tvTitle.setText(musicService.musicTitle());
        tvArtist.setText(musicService.musicArtist());
        Picasso.get().load(musicService.musicIconUrl()).into(bhajan_icon);
        duration_total.setText(musicService.musicDuration());

        if (MusicService.musicOn) {
            playPauseBtn.setImageResource(R.drawable.baseline_pause_saffron_24);
        } else {
            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_saffron_24);
        }
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_play_pauseClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        animateView(nextBtn);
                        btn_nextClicked();
                        checkDownload();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        animateView(prevBtn);
                        btn_preClicked();
                        checkDownload();
                    }
                });
            }
        };
        prevThread.start();
    }

    @Override
    public void btn_play_pauseClicked() {
        if (musicService.isPlaying()) {

            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_saffron_24);
            musicService.pause();

            seekBar.setMax(100);

            MusicDetails.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        long currentPosition = musicService.getCurrentPosition();
                        long totalDuration = musicService.getDuration();
                        seekBar.setProgress((int) (currentPosition * 100 /totalDuration));
                        duration_played.setText(formattedTime(currentPosition));

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            
        } else {
            playPauseBtn.setImageResource(R.drawable.baseline_pause_saffron_24);
            musicService.start();

//            musicService.showNotification(R.drawable.baseline_pause_black_24);
            seekBar.setMax(100);

            MusicDetails.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        long currentPosition = musicService.getCurrentPosition();
                        long totalDuration = musicService.getDuration();
                        seekBar.setProgress((int) (currentPosition * 100 /totalDuration));
                        duration_played.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            
        }
    }

    @Override
    public void btn_preClicked() {
        if (musicService.isPlaying()) {
            Log.d(TAG, "btn_preClicked: ");
            musicService.stop();
            musicService.release();

            if (position > 0) {
                position = position - 1;
            }

            musicService.createPlayer(position);

            setUpData();
            seekBar.setMax(100);

            playPauseBtn.setBackgroundResource(R.drawable.baseline_pause_saffron_24);
            musicService.start();

            MusicDetails.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        long currentPosition = musicService.getCurrentPosition();
                        long totalDuration = musicService.getDuration();
                        seekBar.setProgress((int) (currentPosition * 100 /totalDuration));
                        duration_played.setText(formattedTime(currentPosition));

                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
        else {
            musicService.stop();
            musicService.release();

            if (position > 0) {
                position = position - 1;
            }
            musicService.createPlayer(position);

            setUpData();
            seekBar.setMax(100);

            playPauseBtn.setBackgroundResource(R.drawable.baseline_play_arrow_saffron_24);
            musicService.pause();

            MusicDetails.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        long currentPosition = musicService.getCurrentPosition();
                        long totalDuration = musicService.getDuration();
                        seekBar.setProgress((int) (currentPosition * 100 /totalDuration));
                        duration_played.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });


        }
    }

    @Override
    public void btn_nextClicked() {
        if (musicService!= null) {
            if (musicService.isPlaying()) {
                musicService.stop();
                musicService.release();

                if (position < bhajans.size() - 1) {
                    position = (position + 1);
                }

                musicService.createPlayer(position);

                setUpData();
                seekBar.setMax(100);

                musicService.start();
                playPauseBtn.setBackgroundResource(R.drawable.baseline_pause_saffron_24);

                MusicDetails.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (musicService != null) {
                            long currentPosition = musicService.getCurrentPosition();
                            long totalDuration = musicService.getDuration();
                            seekBar.setProgress((int) (currentPosition * 100 /totalDuration));
                            duration_played.setText(formattedTime(currentPosition));
                        }
                        handler.postDelayed(this, 1000);
                    }
                });

            } else {
                musicService.stop();
                musicService.release();

                if (position < bhajans.size() - 1) {
                    position = position + 1;
                }
                musicService.createPlayer(position);

                setUpData();
                seekBar.setMax(100);

                playPauseBtn.setBackgroundResource(R.drawable.baseline_play_arrow_saffron_24);
                musicService.pause();

                MusicDetails.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (musicService != null) {
                            long currentPosition = musicService.getCurrentPosition();
                            long totalDuration = musicService.getDuration();
                            seekBar.setProgress((int) (currentPosition * 100 /totalDuration));
                            duration_played.setText(formattedTime(currentPosition));
                        }
                        handler.postDelayed(this, 1000);
                    }
                });

            }
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
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallBack(this);

        setUpData();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

    private void setWindow() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.music_details_top));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();

        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    protected void onResume() {
        isActive = true;
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicService.stop();
    }

    @Override
    public void onBackPressed() {
        Intent musicListIntent = new Intent(MusicDetails.this, MainActivity.class);
        startActivity(musicListIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }
}