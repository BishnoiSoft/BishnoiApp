package com.bss.bishnoi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bss.bishnoi.adapters.CalendarAdapter;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private GridView calendarGridView;
    private Button prevButton, nextButton;
    private CalendarAdapter calendarAdapter;
    private Calendar calendar;

    private TextView monthView;

    int month;
    String currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        setWindow();

        calendarGridView = findViewById(R.id.calendarGridView);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        monthView = findViewById(R.id.month_name);

        calendar = Calendar.getInstance();
        calendarAdapter = new CalendarAdapter(this, calendar);
        calendarGridView.setAdapter(calendarAdapter);

        month = calendar.get(Calendar.MONTH);

        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("hi", "IN"));
        String[] monthNames = symbols.getMonths();
        currentMonth = monthNames[month];

        monthView.setText(currentMonth);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                calendarAdapter.updateCalendar(calendar);
                month = calendar.get(Calendar.MONTH);
                currentMonth = String.valueOf(monthNames[month]);

                monthView.setText(currentMonth);

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                calendarAdapter.updateCalendar(calendar);

                month = calendar.get(Calendar.MONTH);
                currentMonth = String.valueOf(monthNames[month]);

                monthView.setText(currentMonth);
            }
        });


    }

    private void setWindow() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setNavigationBarDividerColor(ContextCompat.getColor(this, R.color.white));
        }

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.saffron));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();

        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }

    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }
}