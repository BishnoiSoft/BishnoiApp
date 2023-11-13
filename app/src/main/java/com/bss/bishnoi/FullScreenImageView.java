package com.bss.bishnoi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullScreenImageView extends AppCompatActivity {

    private ImageView fullScreenImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_view);

        fullScreenImageView = findViewById(R.id.fullScreenImageView);

        String imageUrl = getIntent().getStringExtra("PROFILE_URL");
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(fullScreenImageView);
        }

        fullScreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}