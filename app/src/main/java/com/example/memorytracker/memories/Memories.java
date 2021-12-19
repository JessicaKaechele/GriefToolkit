package com.example.memorytracker.memories;

import static com.example.memorytracker.Utils.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.memorytracker.R;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.LinkedHashMap;

public class Memories extends AppCompatActivity {

    private ListView listView;
    private LinkedHashMap<String, Boolean> memoryMap;
    private final String filename = "memory_data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);

        memoryMap = getMemoryMap();

        listView = findViewById(R.id.list);
        ImageButton addMemoryButton = findViewById(R.id.addQuote);

        addMemoryButton.setOnClickListener(v -> {
            AlertDialog.Builder memoryItemBuilder = new AlertDialog.Builder(Memories.this);
            memoryItemBuilder.setTitle("Füge eine Erinnerung hinzu:");
            LinearLayout linearLayout = new LinearLayout(Memories.this);
            final EditText memoryEditText = new EditText(Memories.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin= convertDpToPx(Memories.this, 20);
            params.rightMargin= convertDpToPx(Memories.this, 20);
            memoryEditText.setLayoutParams(params);
            linearLayout.addView(memoryEditText);
            memoryItemBuilder.setView(linearLayout);
            memoryItemBuilder.setPositiveButton(R.string.save, (dialogInterface, i) -> {
                String memoryInput = memoryEditText.getText().toString();
                ContentValues values = new ContentValues();
                values.clear();
                saveMemory(memoryInput);
                updateMemoryList();
            });

            memoryItemBuilder.setNegativeButton(R.string.back, null);

            memoryItemBuilder.create().show();
        });


        updateMemoryList();
    }

    private void updateMemoryList() {
        MemoryListAdapter memoryListAdapter = new MemoryListAdapter(this, R.layout.memory, memoryMap);
        listView.setAdapter(memoryListAdapter);
    }

    private void saveMemory(String memory) {
        if (!memoryMap.containsKey(memory)){
            memoryMap.put(memory, false);
            writeToFile(this, memoryMap, filename);
        }

    }

    public void onDeleteButtonClick(View view) {
        View v = (View) view.getParent();
        TextView itemTextView = v.findViewById(R.id.checkBox);
        String memoryItem = itemTextView.getText().toString();

        deleteMemory(memoryItem);

        updateMemoryList();
    }

    public void onCheckBoxClick(View view) {
        View v = (View) view.getParent();
        CheckBox itemTextView = v.findViewById(R.id.checkBox);
        String memoryItem = itemTextView.getText().toString();
        boolean isChecked = itemTextView.isChecked();
        saveCheck(memoryItem, isChecked);

        updateMemoryList();
    }

    private void saveCheck(String memory, boolean isChecked) {
        memoryMap.put(memory, isChecked);
        writeToFile(this, memoryMap, filename);
    }

    private void deleteMemory(String memory) {
        memoryMap.remove(memory);
        writeToFile(this, memoryMap, filename);
    }


    @NonNull
    private LinkedHashMap<String, Boolean> getMemoryMap() {
        String memoryData = null;
        try {
            memoryData = readFromFile(Memories.this, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fromJson(memoryData);
    }

    private LinkedHashMap<String, Boolean> fromJson(String memoryData) {
        LinkedHashMap<String, Boolean> memoryDataMap;
        if (memoryData == null){
            memoryDataMap = new LinkedHashMap();
        }else {
            memoryDataMap = new Gson().fromJson(memoryData, LinkedHashMap.class);
        }
        return memoryDataMap;
    }







}