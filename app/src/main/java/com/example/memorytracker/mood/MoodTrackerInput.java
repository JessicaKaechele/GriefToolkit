package com.example.memorytracker.mood;

import static com.example.memorytracker.Utils.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.memorytracker.R;
import com.example.memorytracker.mood.MoodTracker;
import com.google.gson.Gson;

public class MoodTrackerInput extends AppCompatActivity {
    private String DAY;
    private String MONTH;
    private String YEAR;
    private final String filename = "mood_tracker_data.json";

    Map<String, String> monthsGerman = new HashMap<String, String>() {{
        put("january", "Januar");
        put("february", "Februar");
        put("march", "März");
        put("april", "April");
        put("may", "Mai");
        put("june", "Juni");
        put("july", "Juli");
        put("august", "August");
        put("september", "September");
        put("october", "Oktober");
        put("november", "November");
        put("december", "Dezember");
    }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_tracker_input);

        Intent intent = getIntent();
        MONTH = intent.getStringExtra(MoodTracker.MONTH);
        DAY = intent.getStringExtra(MoodTracker.DAY);
        YEAR = intent.getStringExtra(MoodTracker.YEAR);

        TextView textView = findViewById(R.id.textView);
        textView.setText(String.format("%s. %s %s", DAY, monthsGerman.get(MONTH), YEAR));

            }
    public void onClick(View v) {
        Intent intent = new Intent(this, MoodTracker.class);
        String mood = getMood(v);
        HashMap<String, String> moodDataMap = getMoodData(mood);

        writeToFile(this, moodDataMap, filename);

        intent.putExtra(MoodTracker.YEAR, Integer.parseInt(YEAR));

        startActivity(intent);
        finish();
    }

    public void onDelete(View v) {
        Intent intent = new Intent(this, MoodTracker.class);
        HashMap<String, String> moodDataMap = getMoodData("0");

        writeToFile(this, moodDataMap, filename);

        intent.putExtra(MoodTracker.YEAR, Integer.parseInt(YEAR));

        startActivity(intent);
        finish();
    }

    @NonNull
    private HashMap<String, String> getMoodData(String mood) {
        String moodData = null;
        try {
            moodData = readFromFile(MoodTrackerInput.this, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, String> moodDataMap;
        if (moodData == null){
            moodDataMap = new HashMap<>();
        }else {
            moodDataMap = new Gson().fromJson(moodData, HashMap.class);
        }
        String key = String.format("%s%s%s", MONTH, DAY, YEAR);
        if (Integer.parseInt(mood) != 0) {
            moodDataMap.put(key, mood);
        }else{
            moodDataMap.remove(key);
        }

        return moodDataMap;
    }

    @NonNull
    private String getMood(View v) {
        String name = v.getResources().getResourceName(v.getId());
        return name.replace("mood","").replace("com.example.memorytracker:id/", "");
    }
}