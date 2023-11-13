package com.bss.bishnoi.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageLoader {
    public static void loadImageFromUrl(String imageUrl, final LoadImageCallback callback) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Image loaded successfully, invoke the callback with the bitmap
                callback.onImageLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                // Image failed to load, invoke the callback with null
                callback.onImageLoaded(null);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // No action needed
            }
        };

        Picasso.get()
                .load(imageUrl)
                .into(target);
    }

    public interface LoadImageCallback {
        void onImageLoaded(Bitmap bitmap);
    }
}
