package com.bss.bishnoi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bss.bishnoi.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarAdapter extends BaseAdapter {

    private Context context;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    public CalendarAdapter(Context context, Calendar calendar) {
        this.context = context;
        this.calendar = calendar;
        this.dateFormatter = new SimpleDateFormat("dd", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.calendar_item, parent, false);
        }

        TextView dateTextView = convertView.findViewById(R.id.dateTextView);

        // Set the date for each cell
        calendar.set(Calendar.DAY_OF_MONTH, position + 1);
        Date date = calendar.getTime();
        String dateString = dateFormatter.format(date);
        dateTextView.setText(dateString);

        // Set the color for the current date
        Calendar today = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            dateTextView.setTextColor(Color.RED);
        } else {
            dateTextView.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    // Update the calendar and refresh the GridView
    public void updateCalendar(Calendar calendar) {
        this.calendar = calendar;
        notifyDataSetChanged();
    }
}
