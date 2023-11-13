package com.bss.bishnoi.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bss.bishnoi.R;
import com.bss.bishnoi.interfaces.BhajanItemClickListner;
import com.bss.bishnoi.models.BhajanModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BhajanHorizontalAdapter extends RecyclerView.Adapter<BhajanHorizontalAdapter.ViewHolder>{
    Context context;
    public static ArrayList<BhajanModel> bhajans;
    ArrayList<BhajanModel> filteredBhajans;
    BhajanItemClickListner bhajanItemClickListner;
    int selectedPosition;

    public BhajanHorizontalAdapter(Context context, ArrayList<BhajanModel> bhajans, BhajanItemClickListner bhajanItemClickListner) {
        this.context = context;
        this.bhajans = bhajans;
        this.filteredBhajans = new ArrayList<>(bhajans);
        this.bhajanItemClickListner = bhajanItemClickListner;
    }

    @NonNull
    @Override
    public BhajanHorizontalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_horizontal_bhajan_item, parent, false);
        return new BhajanHorizontalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BhajanHorizontalAdapter.ViewHolder holder, int position) {
        BhajanModel bhajan = filteredBhajans.get(position);
        int currentPosition = position;

        holder.titleView.setText(bhajan.getTitle());
        holder.artistView.setText(bhajan.getArtist());

        if (bhajan.getImageUrl() != null) {
            Picasso.get().load(bhajan.getImageUrl()).into(holder.iconView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Icon Clcked");
                if (bhajanItemClickListner != null) {
                    Log.d(TAG, "onClick: Condition Satisfied");
                    bhajanItemClickListner.onBhajanItemClick(currentPosition, bhajan.getTitle(), bhajan.getArtist(), bhajan.getAudioUrl(), bhajan.getImageUrl(), bhajan.getDuration(), bhajan.getType());
                    selectedPosition = currentPosition;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredBhajans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView, creditsView, artistView;
        ImageView iconView, menuBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.titleView);
            iconView = itemView.findViewById(R.id.iconView);
            artistView = (TextView) itemView.findViewById(R.id.artistView);
        }
    }
}
