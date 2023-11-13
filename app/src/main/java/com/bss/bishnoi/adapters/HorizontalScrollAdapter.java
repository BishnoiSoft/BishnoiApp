package com.bss.bishnoi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bss.bishnoi.R;
import com.bss.bishnoi.interfaces.UpReItemClickListner;

import java.util.ArrayList;

public class HorizontalScrollAdapter extends RecyclerView.Adapter<HorizontalScrollAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> dataArray;

    private UpReItemClickListner mClickListener;

    public int selectedPosition = RecyclerView.NO_POSITION;

    public HorizontalScrollAdapter(Context context, ArrayList<String> dataArray, UpReItemClickListner upReItemClickListner) {
        this.dataArray = dataArray;
        this.context = context;
        this.mClickListener = upReItemClickListner;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.upper_item_text);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String clickedItem = dataArray.get(position);
            selectedPosition = position;
            mClickListener.onItemClick(position, clickedItem);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_upper_scroll, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String data = dataArray.get(position);
        holder.textView.setText(data);

        if (selectedPosition == position) {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.item_selected_up_recycler));
            holder.textView.setTextColor(ContextCompat.getColor(holder.textView.getContext(), R.color.white));
        } else {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.item_border));
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return dataArray.size();
    }
}
