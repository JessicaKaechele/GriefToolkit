package com.example.memorytracker.source;

import static com.example.memorytracker.Utils.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.memorytracker.R;
import com.example.memorytracker.source.SourceCreator;
import com.example.memorytracker.source.SourceOverview;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Sources extends AppCompatActivity {

    public static final String SOURCE_TITLE = "com.example.memorytracker.SOURCE_TITLE";

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sources);
        ArrayList<String> sourceData = getSourceData();

        listView = findViewById(R.id.list);
        updateSourceList(sourceData);
    }

    public void openSourceCreator(View view) {
        Intent intent = new Intent(this, SourceCreator.class);
        intent.putExtra(SOURCE_TITLE, "");
        startActivity(intent);
    }

    private void updateSourceList(ArrayList<String> sourceData) {
        ArrayAdapter<String> sourceListAdapter = new ArrayAdapter<>(
                this,
                R.layout.source,
                R.id.sourceButton,
                sourceData
        );
        listView.setAdapter(sourceListAdapter);
    }

    public void onButtonClick(View view) {

        Button button = (Button) view;
        String title = (String) button.getText();

        Intent intent = new Intent(this, SourceOverview.class);
        intent.putExtra(SOURCE_TITLE, title);
        startActivity(intent);

    }

    @NonNull
    private ArrayList<String> getSourceData() {
        String sourceTitleData = null;
        try {
            String filename = "source_data.json";
            sourceTitleData = readFromFile(Sources.this, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fromJson(sourceTitleData);
    }

    private ArrayList<String> fromJson(String sourceTitleData) {
        ArrayList<String> sourceTitleList;
        if (sourceTitleData == null) {
            sourceTitleList = new ArrayList<>();
        } else {

            LinkedHashMap<String,Object> json = new Gson().fromJson(sourceTitleData, LinkedHashMap.class);
            sourceTitleList = new ArrayList<>(json.keySet());
        }
        return sourceTitleList;


    }


}