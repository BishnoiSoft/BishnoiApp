package com.bss.bishnoi.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bss.bishnoi.R;
import com.bss.bishnoi.models.StoreModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder>{
    Context context;
    ArrayList<StoreModel> items;

    public StoreAdapter(Context context, ArrayList<StoreModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.ViewHolder holder, int position) {
        StoreModel item = items.get(position);

        if (item.getImageUrl() != null){
            Picasso.get().load(item.getImageUrl()).into(holder.itemImage);
        }

        holder.itemTitle.setText(item.getTitle());
        holder.itemCategory.setText(item.getCategory());

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(item.getUrl()));
                context.startActivity(intent);
            }
        });

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView.ScaleType type = holder.itemImage.getScaleType();
                if (type == ImageView.ScaleType.CENTER_CROP) {
                    holder.itemImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } else {
                    holder.itemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle, itemCategory, btnDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemCategory = itemView.findViewById(R.id.item_category);
            btnDetails = itemView.findViewById(R.id.item_details);
        }
    }
}
