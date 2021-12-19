package com.example.memorytracker.mood;

import static com.example.memorytracker.Utils.*;

import com.example.memorytracker.R;
import com.example.memorytracker.Utils;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Calendar;
import java.util.List;


public class MoodTracker extends AppCompatActivity {
    public static final String MONTH = "com.example.memorytracker.MONTH";
    public static final String DAY = "com.example.memorytracker.DAY";
    public static final String YEAR = "com.example.memorytracker.YEAR";


    private int year = 2021;

    final private String months[] = new String[] {
            "january", "february", "march", "april", "may", "june", "july", "august", "september",
            "october", "november", "december" };

    final private List<Integer> longMonths = Arrays.asList(1,3,5,7,8,10,12);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_tracker);

        year = getIntent().getIntExtra(YEAR, 0);
        if (year == 0){
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        HashMap<String, String> moodDataMap = getMoodData();

        TextView yearView = findViewById(R.id.year);
        yearView.setText(String.valueOf(year));

        TableLayout table = (TableLayout) findViewById(R.id.mood_table);

        for (int day = 1; day <=31; day++) {
            TableRow dayRow = new TableRow(this);
            setRowParams(dayRow);
            for (int month = 0; month <= 12; month++) {
                ContextThemeWrapper circleStyle = new ContextThemeWrapper(this, R.style.mood_tracker_style_circles);
                TextView dayView = new TextView(circleStyle);
                if (month == 0) {
                    dayView = new TextView(this);
                    setParamsFirstColumn(dayView);
                    dayView.setText(String.valueOf(day));
                }else{
                    setParamsDay(dayView);
                    if (isLastDaysOfShortMonths(day, month) || isLeapYear(day, month) ^ isLastDaysOfFebruary(day, month)) {
                        TypedValue typedValue = new TypedValue();
                        Resources.Theme theme = this.getTheme();
                        theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
                        int color = typedValue.data;
                        dayView.setBackgroundColor(color);

                    }else{
                        String monthName = months[month - 1];
                        String tag = String.format("%s.%s.%s", monthName, day, year);
                        dayView.setTag(tag);

                        dayView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MoodTracker.this, MoodTrackerInput.class);
                                String name = String.valueOf(view.getTag());
                                String[] nameParts = name.split("\\.");

                                String day = nameParts[1];
                                String month = nameParts[0];
                                String year = nameParts[2];

                                intent.putExtra(MONTH, month);
                                intent.putExtra(DAY, day);
                                intent.putExtra(YEAR, String.valueOf(year));

                                startActivity(intent);
                                finish();
                            }
                        });
                        String key = String.format("%s%s%s", monthName, day, year);
                        if (moodDataMap.containsKey(key)) {
                            colorCircle(moodDataMap, dayView, key);
                        }
                    }
                }
                dayRow.addView(dayView);
            }
            table.addView(dayRow);
        }
    }

    private boolean isLastDaysOfShortMonths(int day, int month) {
        return day == 31 && !longMonths.contains(month);
    }

    private boolean isLastDaysOfFebruary(int day, int month) {
        return day>28 && month==2;
    }

    private boolean isLeapYear(int day, int month) {
        return year%4==0 && day==29 && month==2;
    }

    private void colorCircle(HashMap<String, String> moodDataMap, TextView dayView, String key) {
        String moodValue = moodDataMap.get(key);

        dayView.setBackgroundTintList(getResources().getColorStateList(getResources().getIdentifier("mood" + moodValue, "color", getPackageName())));
    }

    private void setParamsDay(TextView dayView) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(convertDpToPx(this, 1), convertDpToPx(this, 1),convertDpToPx(this, 1),convertDpToPx(this, 1));
        params.weight = 1;
        dayView.setLayoutParams(params);
        dayView.setBackground(getResources().getDrawable(R.drawable.table_border));
        dayView.setTextSize(1);
    }

    private void setParamsFirstColumn(TextView dayView) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        dayView.setLayoutParams(params);
        dayView.setTextSize(12);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorOnSecondary, typedValue, true);
        int color = typedValue.data;
        dayView.setTextColor(color);
    }

    private void setRowParams(TableRow dayRow) {
        dayRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        dayRow.setGravity(Gravity.CENTER_VERTICAL);
    }

    public void nextYear(View v) {
        year++;
        Intent intent = new Intent(this, MoodTracker.class);
        intent.putExtra(YEAR, year);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    public void lastYear(View v) {
        year--;
        Intent intent = new Intent(this, MoodTracker.class);
        intent.putExtra(YEAR, year);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        finish();
    }

    @NonNull
    private HashMap<String, String> getMoodData() {
        String mood_data = null;
        try {
            String filename = "mood_tracker_data.json";
            mood_data = readFromFile(MoodTracker.this, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, String> moodDataMap;
        if (mood_data == null){
            moodDataMap = new HashMap<>();
        }else {
            moodDataMap = new Gson().fromJson(mood_data, HashMap.class);
        }
        return moodDataMap;
    }



}