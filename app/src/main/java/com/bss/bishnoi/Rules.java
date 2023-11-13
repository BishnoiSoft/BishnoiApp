package com.bss.bishnoi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Rules extends AppCompatActivity {

    public static final String RULES  = "तीस दिन सूतक, पांच ऋतुवन्ती न्यारो ।\n" +
            "सेरा करो स्नान, शील, सन्तोष शुची प्यारो ।।\n" +
            "द्विकाल संध्या करो, सांझ आरती गुण गावो ।\n" +
            "होम हित चित प्रीत सूं होय, वास बैकुंठे पावो ।।\n" +
            "पाणी, बांणी, ईन्धणी दूध इतना लीजै छाण ।\n" +
            "क्षमा दया हिरदै धरो, गुरु बतायो जाण ।।\n" +
            "चोरी, निन्दा, झूठ बरजियों, वाद न करणो कोय ।\n" +
            "अमावस्या व्रत राखणो, भजन विष्णु बतायो जोय ।।\n" +
            "जीव दया पालणी, रूंख लीला नहिं घावै ।\n" +
            "अजर जरैं, जीवत मरै, वे वास बैकुण्ठा पावै ।।\n" +
            "करें रसोई हाथ सूं, आन सूं पला न लावै ।\n" +
            "अमर रखावै थाट, बैल बधिया न करावें ।।\n" +
            "अमल, तमाखू, भांग, मद-मांस सूं दूर ही भागे ।\n" +
            "लील न लावै अंग, देखत दूर ही त्याग ।।\n" +
            "‘उणतीस धर्म की आखड़ी, हिरदै धरियो जोय ।\n" +
            "गुरु जाम्भोजी किरपा करी, नाम बिश्नोई होय ।”";

    TextView tvRules;
    ImageView btnBack;

    Boolean rulesNAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        setWindow();
        initViews();
        MobileAds.initialize (this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete( InitializationStatus initializationStatus ) {

                //Showing a simple Toast Message to the user when The Google AdMob Sdk Initialization is Completed
                //Toast.makeText (MainActivity.this, "AdMob Sdk Initialize "+ initializationStatus.toString(), Toast.LENGTH_LONG).show();
            }
        });
        checkAds();
    }

    private void checkAds() {
        DatabaseReference adsReference = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("ads");

        adsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rulesNAd = (Boolean) snapshot.child("rulesNAd").getValue();

                if (Boolean.TRUE.equals(rulesNAd)) {
                    loadNativeAd();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initViews() {
        tvRules = findViewById(R.id.tvRules);
        tvRules.setText(RULES);

        btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView(btnBack);
                onBackPressed();
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


    private void setWindow() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.saffron));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.shabadwani_bg));

        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();

        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }

    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

    private void animateView(View view) {
        // Load the scale animations
        Animation scaleUpAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        Animation scaleDownAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        // Apply the animations to the View
        view.startAnimation(scaleUpAnimation);
        view.startAnimation(scaleDownAnimation);
    }
}