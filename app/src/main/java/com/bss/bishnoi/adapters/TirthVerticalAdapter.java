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
import com.bss.bishnoi.models.TirthModel;
import com.bss.bishnoi.utils.LocationUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TirthVerticalAdapter extends RecyclerView.Adapter<TirthVerticalAdapter.ViewHolder> {

    Context context;
    private ArrayList<TirthModel> tirthModels;

    int selectedPosition = RecyclerView.NO_POSITION;

    private double longitude;
    private double latitude;

    public TirthVerticalAdapter(Context context, ArrayList<TirthModel> tirthModels, double longitude, double latitude) {
        this.context = context;
        this.tirthModels = tirthModels;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tirth_item, parent, false);
        return new TirthVerticalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TirthModel tirthModel = tirthModels.get(position);
        holder.tvTitle.setText(tirthModel.getTitle());
        String distance = calculateDistance(Integer.parseInt(tirthModel.getLongitude()), Integer.parseInt(tirthModel.getLatitude()));
        holder.tvDistance.setText(distance);

        if (tirthModel.getImageUrl() != null) {
            Picasso.get().load(tirthModel.getImageUrl()).into(holder.iconView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return tirthModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDistance;
        ImageView iconView, btnForward;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            iconView = itemView.findViewById(R.id.iconView);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            btnForward = itemView.findViewById(R.id.forward);
        }
    }

    private String calculateDistance(int tLongitude, int tLatitude) {
        double distance = LocationUtils.calculateDistance(latitude, longitude, tLatitude, tLongitude);
        Log.d(TAG, "calculateDistance: " + "Distance between the two locations: " + distance + " kilometers");
        return distance + " kms";
    }
}
