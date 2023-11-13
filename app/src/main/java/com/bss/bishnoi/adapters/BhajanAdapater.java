package com.bss.bishnoi.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bss.bishnoi.R;
import com.bss.bishnoi.database.DownloadBhajanSqlitHelper;
import com.bss.bishnoi.interfaces.BhajanDeleteListener;
import com.bss.bishnoi.interfaces.BhajanItemClickListner;
import com.bss.bishnoi.interfaces.BhajanDownloadListener;
import com.bss.bishnoi.models.BhajanModel;
import com.bss.bishnoi.utils.DBHelperDownload;
import com.bss.bishnoi.utils.MusicDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class BhajanAdapater extends RecyclerView.Adapter<BhajanAdapater.ViewHolder> {

    Context context;
    public static ArrayList<BhajanModel> bhajans;
    ArrayList<BhajanModel> filteredBhajans;
    DownloadBhajanSqlitHelper downloadBhajanSqlitHelper;

    BhajanItemClickListner bhajanItemClickListner;
    BhajanDeleteListener bhajanDeleteListener;
    int selectedPosition = RecyclerView.NO_POSITION;

    BhajanDownloadListener bhajanDownloadListener;

    public BhajanAdapater(Context context, ArrayList<BhajanModel> bhajans, BhajanItemClickListner bhajanItemClickListner, BhajanDeleteListener bhajanDeleteListener, BhajanDownloadListener bhajanDownloadListener) {
        this.context = context;
        this.bhajans = bhajans;
        this.filteredBhajans = new ArrayList<>(bhajans);
        this.bhajanItemClickListner = bhajanItemClickListner;
        this.bhajanDeleteListener = bhajanDeleteListener;
        this.bhajanDownloadListener = bhajanDownloadListener;
        downloadBhajanSqlitHelper = new DownloadBhajanSqlitHelper(context);

    }

    @NonNull
    @Override
    public BhajanAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bhajan_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BhajanAdapater.ViewHolder holder, int position) {
        BhajanModel bhajan = filteredBhajans.get(position);
        int currentPosition = position;

        holder.titleView.setText(bhajan.getTitle());
        holder.artistView.setText(bhajan.getArtist());

        if (bhajan.getImageUrl() != null){
            Picasso.get().load(bhajan.getImageUrl()).into(holder.iconView);
        }

        if (bhajan.getCredits() != null) {
            holder.creditsView.setText(bhajan.getCredits());
        } else {
            holder.creditsView.setVisibility(View.GONE);
        }

        holder.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Context wrapper = new ContextThemeWrapper(context, R.style.PopupMenuStyle);

                PopupMenu popupMenu = new PopupMenu(wrapper, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_bhajan_item, popupMenu.getMenu());

                DBHelperDownload DB = new DBHelperDownload(context);
                Boolean isDownloaded = DB.checkDataExists(bhajan.getTitle());

                if (isDownloaded) {
                    Menu menu = popupMenu.getMenu();
                    MenuItem menuItemToHide = menu.findItem(R.id.menu_item_download);
                    menuItemToHide.setVisible(false);
                } else {
                    Menu menu = popupMenu.getMenu();
                    MenuItem menuItemToHide = menu.findItem(R.id.menu_item_delete_download);
                    menuItemToHide.setVisible(false);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();

                        if (itemId == R.id.menu_item_download) {
                            bhajanDownloadListener.downloadBtnClicked();
                            MusicDownloader musicDownloader = new MusicDownloader(context, bhajan);
                            musicDownloader.startDownload();
                            return true;
                        } else if (itemId == R.id.menu_item_delete_download) {
                            String imageUrl = null, audioUrl = null;
                            Cursor cursor = DB.getSpecificData(bhajan.getTitle());
                            if (cursor != null && cursor.moveToFirst()) {
                                imageUrl = cursor.getString(1);
                                audioUrl = cursor.getString(2);
                            }
                            cursor.close();
                            bhajanDeleteListener.onBhajanDeleteListener(imageUrl, audioUrl);
                            DB.deleteDownloadData(bhajan.getTitle());
                            return true;
                        }
                        notifyDataSetChanged();
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

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
            menuBtn = itemView.findViewById(R.id.menu_btn);
            artistView = (TextView) itemView.findViewById(R.id.artistView);
            creditsView = (TextView) itemView.findViewById(R.id.creditView);
        }
    }

    public void filter(String query) {
        filteredBhajans.clear();
        if (query.isEmpty()) {
            filteredBhajans.addAll(bhajans);
        } else {
            query = query.toLowerCase(Locale.getDefault());
            for (BhajanModel bhajanModel : bhajans) {
                if (bhajanModel.getTitle().toLowerCase(Locale.getDefault()).contains(query) ||
                        bhajanModel.getArtist().toLowerCase(Locale.getDefault()).contains(query) ||
                        bhajanModel.getKeywords().toLowerCase(Locale.getDefault()).contains(query) ||
                        bhajanModel.getLyrics().toLowerCase(Locale.getDefault()).contains(query)) {
                    filteredBhajans.add(bhajanModel);
                }
            }
        }
        notifyDataSetChanged();
    }

    private String calculateFileSize(Uri uri) {
        String fileSize = null;
        try {
            File file = new File(uri.getPath());
            long length = file.length();
            fileSize = String.valueOf(length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileSize;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }
}
