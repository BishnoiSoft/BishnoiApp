package com.bss.bishnoi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bss.bishnoi.adapters.SahityaAdapter;
import com.bss.bishnoi.models.SahityaModel;
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

import java.util.ArrayList;

public class SahityaActivity extends AppCompatActivity {
    ImageView closeBtn, backBtn;

    SahityaAdapter sahityaAdapter;
    ArrayList<SahityaModel> sahityaModels = new ArrayList<>();
    RecyclerView recyclerView;

    Boolean sahityaNAd = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sahitya);

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
        getFirebaseData();
    }

    private void checkAds() {
        DatabaseReference adsReference = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("ads");

        adsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sahityaNAd = (Boolean) snapshot.child("sahityaNAd").getValue();

                if (Boolean.TRUE.equals(sahityaNAd)) {
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


    private void getFirebaseData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference("jambhaniSahitya");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sahityaModels.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String author = dataSnapshot.child("author").getValue(String.class);
                    String iconUrl = dataSnapshot.child("iconUrl").getValue(String.class);
                    String pdfUrl = dataSnapshot.child("pdfUrl").getValue(String.class);
                    int views = dataSnapshot.child("views").getValue(Integer.class);
                    String key = dataSnapshot.getKey();

                    SahityaModel sahityaModel = new SahityaModel(title, author, pdfUrl, iconUrl, views, key);
                    sahityaModels.add(sahityaModel);
                }
                sahityaAdapter = new SahityaAdapter(SahityaActivity.this, sahityaModels);
                recyclerView.setAdapter(sahityaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initViews() {
        backBtn = findViewById(R.id.btn_back);
        closeBtn = findViewById(R.id.close_btn);
        recyclerView = findViewById(R.id.recyclerView);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SahityaActivity.super.onBackPressed();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
}