package com.bss.bishnoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bss.bishnoi.R;

public class SliderAdapter extends PagerAdapter {

    private Context mContext;
    private int[] mImageIds = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground, R.drawable.item_border};
    private String[] mTitles = {"Slide 1", "Slide 2", "Slide 3"};

    public SliderAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_slider_ma_1, container, false);

        ImageView imageView = view.findViewById(R.id.image_view);
        TextView titleView = view.findViewById(R.id.title_view);

        imageView.setImageResource(mImageIds[position]);
        titleView.setText(mTitles[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}

