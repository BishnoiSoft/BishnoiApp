package com.bss.bishnoi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bss.bishnoi.R;
import com.bss.bishnoi.interfaces.OnItemClickListener;

public class ShabadAdapter extends RecyclerView.Adapter<ShabadAdapter.ViewHolder> {
    private String[] shabads;
    private String[] shabadDescriptions;

    private OnItemClickListener mListener;

    public ShabadAdapter(String[] shabads, String[] shabadDescriptions, OnItemClickListener listener) {
        this.shabads = shabads;
        this.shabadDescriptions = shabadDescriptions;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_shabad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String country = shabads[position];
        holder.shabadView.setText(country);
    }

    @Override
    public int getItemCount() {
        return shabads.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView shabadView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shabadView = itemView.findViewById(R.id.shabadText);
            shabadView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(getAdapterPosition());
        }
    }
}
