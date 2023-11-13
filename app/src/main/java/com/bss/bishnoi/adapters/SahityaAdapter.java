package com.bss.bishnoi.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bss.bishnoi.PDFViewActivity;
import com.bss.bishnoi.R;
import com.bss.bishnoi.models.SahityaModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SahityaAdapter extends RecyclerView.Adapter<SahityaAdapter.ViewHolder>{

    Context context;
    ArrayList<SahityaModel> sahityaModels;

    public SahityaAdapter(Context context, ArrayList<SahityaModel> sahityaModels) {
        this.context = context;
        this.sahityaModels = sahityaModels;
    }


    @NonNull
    @Override
    public SahityaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_sahitya, parent, false);
        return new SahityaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SahityaAdapter.ViewHolder holder, int position) {
        int currentPosition = position;
        SahityaModel sahitya = sahityaModels.get(currentPosition);
        holder.titleView.setText(sahitya.getBookTitle());
        holder.artistView.setText(sahitya.getBookAuthor());

        holder.noViews.setText(sahitya.getViews() + "\nViews");

        Picasso.get().load(sahitya.getIconUrl()).into(holder.iconView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PDFViewActivity.class);
                intent.putExtra("PDF_URL", sahitya.getPdfUrl());
                intent.putExtra("TITLE", sahitya.getBookTitle());

                notifyItemChanged(currentPosition); // Notify adapter of the data change

                // Update the view count in Firebase Realtime Database
                updateViewsInFirebase(sahitya.getKey(), sahitya.getViews()+1);

                context.startActivity(intent);
            }
        });

        holder.titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PDFViewActivity.class);
                intent.putExtra("PDF_URL", sahitya.getPdfUrl());
                intent.putExtra("TITLE", sahitya.getBookTitle());

                notifyItemChanged(currentPosition); // Notify adapter of the data change

                // Update the view count in Firebase Realtime Database
                updateViewsInFirebase(sahitya.getKey(), sahitya.getViews()+1);

                context.startActivity(intent);
            }
        });

        holder.artistView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PDFViewActivity.class);
                intent.putExtra("PDF_URL", sahitya.getPdfUrl());
                intent.putExtra("TITLE", sahitya.getBookTitle());

                notifyItemChanged(currentPosition); // Notify adapter of the data change

                // Update the view count in Firebase Realtime Database
                updateViewsInFirebase(sahitya.getKey(), sahitya.getViews()+1);

                context.startActivity(intent);
            }
        });

        holder.iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PDFViewActivity.class);
                intent.putExtra("PDF_URL", sahitya.getPdfUrl());
                intent.putExtra("TITLE", sahitya.getBookTitle());

                notifyItemChanged(currentPosition); // Notify adapter of the data change

                // Update the view count in Firebase Realtime Database
                updateViewsInFirebase(sahitya.getKey(), sahitya.getViews()+1);

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return sahityaModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView, artistView, noViews;
        ImageView iconView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
            artistView = itemView.findViewById(R.id.artistView);
            noViews = itemView.findViewById(R.id.views_text);
            iconView = itemView.findViewById(R.id.iconView);
        }
    }

    private void updateViewsInFirebase(String itemId, int views) {
        Log.d(TAG, "updateViewsInFirebase: " + itemId + views);
        DatabaseReference itemRef = FirebaseDatabase.getInstance("https://bishnoi-e84fa-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("jambhaniSahitya")
                .child(itemId)
                .child("views");

        // Update the view count in Firebase Realtime Database
        itemRef.setValue(views);
    }

}
