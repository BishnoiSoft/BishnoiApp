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

import com.bss.bishnoi.adapters.StoreAdapter;
import com.bss.bishnoi.models.StoreModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class StoreActivity extends AppCompatActivity {

    ImageView iconSearch, iconStore, iconProfile, iconHelp;

    RecyclerView recyclerView;

    ArrayList<StoreModel> items = new ArrayList<>();

    StoreAdapter storeAdapater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        setWindow();
        initViews();
        getFirebaseData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        iconSearch = findViewById(R.id.icon_search);
        iconProfile = findViewById(R.id.icon_profile);
        iconStore = findViewById(R.id.icon_store);
        iconHelp = findViewById(R.id.icon_help);

        iconSearch.setVisibility(View.GONE);
        iconStore.setVisibility(View.GONE);

        iconProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, ShowProfile.class);
                intent.putExtra("activity", "main");
                startActivity(intent);
                finish();
            }
        });

        iconHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreActivity.this, ActivityHelp.class);
                startActivity(intent);
            }
        });
    }

    public void getFirebaseData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference("store");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id = dataSnapshot.child("id").getValue(String.class);
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String url = dataSnapshot.child("url").getValue(String.class);
                    String seller = dataSnapshot.child("seller").getValue(String.class);
                    Integer price = dataSnapshot.child("price").getValue(Integer.class);
                    String category = dataSnapshot.child("category").getValue(String.class);

                    StoreModel item = new StoreModel(id, title, category, price, seller, url, imageUrl);
                    items.add(item);
                }

                Collections.reverse(items);
                storeAdapater = new StoreAdapter(StoreActivity.this, items);
                recyclerView.setAdapter(storeAdapater);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
}