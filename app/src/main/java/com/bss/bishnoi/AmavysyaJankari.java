package com.bss.bishnoi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bss.bishnoi.adapters.HorizontalScrollAdapter;
import com.bss.bishnoi.databinding.ActivityAmavysyaJankariBinding;
import com.bss.bishnoi.databinding.ActivityMusicDetailsBinding;
import com.bss.bishnoi.interfaces.UpReItemClickListner;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AmavysyaJankari extends AppCompatActivity implements UpReItemClickListner {

    ImageView profile_photo;
    String uPhone, fProfileUrl;
    int currentMonth;
    Calendar calendar;

    FirebaseFirestore firebaseFirestore;
    CollectionReference usersRef;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    TextView title, hindiMonth, englishMonth, startingTime, endingTime, btnShare, btnCopy, btnShareWhatsapp, textStartingTime, textEndingTime;

    private RecyclerView upperRecyclerView;
    private HorizontalScrollAdapter horizontalScrollAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    ImageView searchIcon, helpIcon, btn_store;

    private ActivityAmavysyaJankariBinding mBinding;
    private Context mContext;

    Boolean amavsyaNAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amavysya_jankari);

        mContext = this;

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


        horizontalScrollAdapter.selectedPosition = 1;

    }

    private void checkAds() {
        DatabaseReference adsReference = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("ads");

        adsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amavsyaNAd = (Boolean) snapshot.child("amavsyaNAd").getValue();

                if (Boolean.TRUE.equals(amavsyaNAd)) {
                    loadNativeAd();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void initViews() {
        profile_photo = findViewById(R.id.icon_profile);
        hindiMonth = findViewById(R.id.hindi_month);
        englishMonth = findViewById(R.id.english_month);
        startingTime = findViewById(R.id.startingTime);
        endingTime = findViewById(R.id.endingTime);
        btnShare = findViewById(R.id.btn_share);
        btnShareWhatsapp = findViewById(R.id.btn_share_whatsapp);
        btnCopy = findViewById(R.id.btn_copy);
        textStartingTime = findViewById(R.id.text_startingtime);
        textEndingTime = findViewById(R.id.text_endingTime);
        title = findViewById(R.id.title);

        calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);

        btn_store = findViewById(R.id.icon_store);
        btn_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AmavysyaJankari.this, StoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AmavysyaJankari.this, ShowProfile.class);
                intent.putExtra("activity", "main");
                startActivity(intent);
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent_login = new Intent(AmavysyaJankari.this, Login.class);
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


        firebaseDatabase = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference("amavysya");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int month = ((Long) snapshot.child("month").getValue()).intValue();

                String english_month = (String) snapshot.child("englishMonth").getValue();
                String hindi_month = (String) snapshot.child("hindiMonth").getValue();
                String starting_time = snapshot.child("startingTime").getValue().toString().replace("_b", "\n");
                String ending_time = snapshot.child("endingTime").getValue().toString().replace("_b", "\n");

                englishMonth.setText(english_month);
                hindiMonth.setText(hindi_month);
                startingTime.setText(starting_time);
                endingTime.setText(ending_time);

                String data = english_month + "\n" + hindi_month + "\n" + textStartingTime.getText() + ":- " + "\n" + starting_time + "\n" + textEndingTime.getText() + ":- " + "\n" + ending_time;

                btnCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        animateView(btnCopy);
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(" अमावस्या जानकारी ", data + " \n " + "Download the Bishnoi app from play store : https://play.google.com/store/apps/details?id=com.bss.bishnoi");
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(AmavysyaJankari.this, "जानकारी कॉपी की गई", Toast.LENGTH_SHORT).show();
                    }
                });

                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        animateView(btnShare);
                        String text = "Download the Bishnoi app from play store : https://play.google.com/store/apps/details?id=com.bss.bishnoi";
                        Uri imageUri = shareImageUri(english_month, hindi_month, starting_time, ending_time);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                        startActivity(Intent.createChooser(shareIntent, "Share using"));
                    }
                });

                btnShareWhatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(AmavysyaJankari.this, "Share Using Whatsapp", Toast.LENGTH_SHORT).show();
                        animateView(btnShareWhatsapp);
                        String text = "Download the Bishnoi app from play store : https://play.google.com/store/apps/details?id=com.bss.bishnoi";
                        Uri imageUri = shareImageUri(english_month, hindi_month, starting_time, ending_time);

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
                        if (shareIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(Intent.createChooser(shareIntent, "Share Image on WhatsApp Status"));
                        }
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpperIcons() {
        searchIcon = findViewById(R.id.icon_search);
        helpIcon = findViewById(R.id.icon_help);

        searchIcon.setVisibility(View.GONE);
        helpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AmavysyaJankari.this, ActivityHelp.class);
                startActivity(intent);
            }
        });
    }

    private void setWindow() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.saffron));

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

        upperRecyclerView = findViewById(R.id.upper_recycler_view);
        upperRecyclerView.setHasFixedSize(true);
        upperRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Using Recycler View
        horizontalScrollAdapter = new HorizontalScrollAdapter(AmavysyaJankari.this, upper_scroll_items, this);
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

    public Uri shareImageUri(String english_month, String hindi_month, String starting_time, String ending_time) {
        Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.panchang_bg);
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        canvas.drawBitmap(backgroundImage, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));
        paint.setTextSize((float) (50*2.3));
        paint.setTextAlign(Paint.Align.CENTER);

        float x = canvas.getWidth()/2f;

//        canvas.drawText((String) title.getText(), x, (float) 2*(height/14), paint);
//        paint.setTextSize((float) (50*2.16));
        paint.setColor(Color.parseColor("#FFC764"));
        canvas.drawText(english_month, x, (float) 3*(height/14), paint);
        canvas.drawText(hindi_month, x, (float) 4*(height/14), paint);
        paint.setColor(Color.parseColor("#FFFFFF"));
        canvas.drawText((String) textStartingTime.getText() + ":- ", x, (float) 5*(height/14), paint);

        paint.setColor(Color.parseColor("#FFC764"));
//        paint.setTextSize((float) (50*2.16));
        canvas.drawText(starting_time.split("\n")[0], x, (float) 6*(height/14), paint);
        canvas.drawText(starting_time.split("\n")[1], x, (float) 7*(height/14), paint);

        paint.setColor(Color.parseColor("#FFFFFF"));
//        paint.setTextSize(60);
        canvas.drawText((String) textEndingTime.getText() + ":- ", x, (float) 8*(height/14), paint);
        paint.setColor(Color.parseColor("#FFC764"));
//        paint.setTextSize(50);
        canvas.drawText(ending_time.split("\n")[0], x, (float) 9*(height/14), paint);
        canvas.drawText(ending_time.split("\n")[1], x, (float) 10*(height/14), paint);
//        paint.setTextSize(70);
        canvas.drawText("!! निवण प्रणाम !!", x, (float) 11*(height/14), paint);

        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setTextSize((float) (80));
        canvas.drawText("Bishnoi App", x, (float) (13*(height/14)), paint);

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

        return imageUri;
    }

    private void animateView(View view) {
        // Load the scale animations
        Animation scaleUpAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        Animation scaleDownAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        // Apply the animations to the View
        view.startAnimation(scaleUpAnimation);
        view.startAnimation(scaleDownAnimation);
    }

    // Implementing the Native Ad
    private void loadNativeAd() {

        AdLoader adLoader = new AdLoader.Builder(this, getResources().getString(R.string.admob_native_ad_medium_id))
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
    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }


    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }
}