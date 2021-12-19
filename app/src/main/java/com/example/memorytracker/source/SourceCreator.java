package com.example.memorytracker.source;

import static com.example.memorytracker.Utils.*;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.memorytracker.R;
import com.example.memorytracker.Utils;
import com.example.memorytracker.views.AudioView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class SourceCreator extends AppCompatActivity {

    private LinkedHashMap<String, LinkedHashMap<String, String>> sourceDataMap;
    private final String filename = "source_data.json";
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_creator);

        Intent intent = getIntent();

        title = intent.getStringExtra(Sources.SOURCE_TITLE);
        sourceDataMap = getSourceData();
        if (!title.equals("")){
            addAllTiles();
        }

        Button addSourceButton = findViewById(R.id.add_source);
        addSourceButton.setOnClickListener(v -> showBottomSheetDialog());
    }

    private void addAllTiles() {
        EditText titleView   = findViewById(R.id.titleInputEditText);
        titleView.setText(title);

        LinkedHashMap<String, String> sourceData = sourceDataMap.get(title);
        for (int i = 0; i < sourceData.size(); i++){
            String content = (new ArrayList<>(sourceData.keySet())).get(i);
            String type = (new ArrayList<>(sourceData.values())).get(i);
            addTile(content, type);

        }
    }

    private void addTile(String content, String type) {
        LinearLayout linearLayout = findViewById(R.id.listView);
        LinearLayout linearLayoutContainingContent = getContent(type, content);
        linearLayout.addView(linearLayoutContainingContent);
    }

    final ActivityResultLauncher<String[]> getMediaContent = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
            uri -> {
                String mimeType = getContentResolver().getType(uri);
                String type = "img";
                if (mimeType.contains("video")) {
                    type = "video";
                }else if (mimeType.contains("audio")){
                    type = "audio";
                }
                String filename = getFileName(this, uri);
                String newFilePath = copyFileToInternalStorage(this, uri, "sources", filename);
                addTile(newFilePath, type);
            });

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        LinearLayout text = bottomSheetDialog.findViewById(R.id.textLinearLayout);
        LinearLayout img = bottomSheetDialog.findViewById(R.id.imgLinearLayout);
        LinearLayout audio = bottomSheetDialog.findViewById(R.id.audioLinearLayout);

        text.setOnClickListener(v -> {
            addTile("", "text");
            bottomSheetDialog.dismiss();
        });

        img.setOnClickListener(v -> {
            String[] t = {"image/*", "video/*"};
            getMediaContent.launch(t);
            bottomSheetDialog.dismiss();
        });

        audio.setOnClickListener(v -> {
            String[] t = {"audio/*"};
            getMediaContent.launch(t);
            bottomSheetDialog.dismiss();
        });



        bottomSheetDialog.show();
    }

    @NonNull
    private LinearLayout getContent(String type, String content) {
        LinearLayout contentLayout = null;
        switch (type){
            case "text":
                contentLayout = getTextContent(content);
                break;
            case "img":
                contentLayout = getImgContent(content);
                break;
            case "video":
                contentLayout = getVideoContent(content);
                break;
            case "audio":
                contentLayout = getAudioContent(content);
                break;
        }
        return contentLayout;
    }

    @NonNull
    private LinearLayout getAudioContent(String content) {
        LinearLayout contentLayout;
        contentLayout = (LinearLayout) View.inflate(SourceCreator.this, R.layout.input_view_audio, null);
        AudioView audioView = (AudioView) contentLayout.findViewById(R.id.audioView);
        Uri uri = Uri.parse(content);
        try {
            audioView.setDataSource(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioView.setTag(uri.toString());
        contentLayout.setTag(uri.toString());

        return contentLayout;
    }

    @NonNull
    private LinearLayout getVideoContent(String content) {
        LinearLayout contentLayout;
        contentLayout = (LinearLayout) View.inflate(SourceCreator.this, R.layout.input_view_vid, null);
        VideoView videoView = (VideoView) contentLayout.findViewById(R.id.videoView);
        Uri uri = Uri.parse(content);
        videoView.setVideoURI(uri);
        videoView.setTag(uri.toString());
        contentLayout.setTag(uri.toString());
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        return contentLayout;
    }

    @NonNull
    private LinearLayout getImgContent(String content) {
        LinearLayout contentLayout;
        contentLayout = (LinearLayout) View.inflate(SourceCreator.this, R.layout.input_view_img, null);
        ImageView imageView = (ImageView) contentLayout.findViewById(R.id.imageView);
        Glide.with(this)
                .load(content)
                .into(imageView);
        imageView.setTag(content);
        contentLayout.setTag(content);
        return contentLayout;
    }

    @NonNull
    private LinearLayout getTextContent(String content) {
        LinearLayout contentLayout;
        contentLayout = (LinearLayout) View.inflate(SourceCreator.this, R.layout.input_view, null);
        EditText text = (EditText) contentLayout.findViewById(R.id.editText);
        text.setText(content);
        return contentLayout;
    }

    public void onSave(View v){
        EditText titleView   = findViewById(R.id.titleInputEditText);
        String title = titleView.getText().toString();
        if (!this.title.equals(title)){
            sourceDataMap.remove(this.title);
        }
        LinkedHashMap<String, String> inputList = getInputs();
        if (!title.equals("")){
            boolean keyAlreadyExists = sourceDataMap.containsKey(title);
            sourceDataMap.put(title, inputList);
            writeToFile(this, sourceDataMap, filename);

            if (keyAlreadyExists){
                Intent intent = new Intent(this, SourceOverview.class);
                intent.putExtra(Sources.SOURCE_TITLE, title);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, Sources.class);
                startActivity(intent);
            }
            finish();

        }else{
            Toast.makeText(getApplicationContext(), "Bitte gebe einen Titel ein", Toast.LENGTH_LONG).show();
        }

    }

    @NonNull
    private LinkedHashMap<String, String> getInputs() {
        LinearLayout constraintLayout = findViewById(R.id.listView);
        LinkedHashMap<String, String> relevantChildrenList = new LinkedHashMap<>();
        int childCount = constraintLayout.getChildCount();
        for (int i = 0; i < childCount; i++){
            View child = constraintLayout.getChildAt(i);
            View relevantChild = child.findViewById(R.id.editText);
            if (relevantChild != null){
                relevantChildrenList.put(((EditText)relevantChild).getText().toString(), "text");
            }
            relevantChild = child.findViewById(R.id.imageView);
            if (relevantChild != null){
                relevantChildrenList.put(relevantChild.getTag().toString(), "img");
            }
            relevantChild = child.findViewById(R.id.videoView);
            if (relevantChild != null){
                relevantChildrenList.put(relevantChild.getTag().toString(), "video");
            }
            relevantChild = child.findViewById(R.id.audioView);
            if (relevantChild != null){
                relevantChildrenList.put(relevantChild.getTag().toString(), "audio");
            }
        }
        return relevantChildrenList;
    }

    public void onDeleteButtonClick(View view) {
        LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();
        linearLayout.removeView((View) view.getParent().getParent());
        String uri = String.valueOf(linearLayout.getTag());
        deleteMediaFile(uri);
    }

    private void deleteMediaFile(String uri) {
        if (uri != null){
                List<String> list = Utils.getContentStrings(sourceDataMap);
                if (Collections.frequency(list, uri)<2){
                    Utils.deleteFile(uri);
                }
        }
    }

    @NonNull
    private LinkedHashMap<String, LinkedHashMap<String, String>> getSourceData() {
        String sourceData = null;
        try {
            sourceData = readFromFile(SourceCreator.this, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinkedHashMap<String, LinkedHashMap<String,String>> sourceDataMap;
        if (sourceData == null){
            sourceDataMap = new LinkedHashMap<>();
        }else {
            sourceDataMap = new Gson().fromJson(sourceData, new TypeToken<LinkedHashMap<String, LinkedHashMap<String, String>>>(){}.getType());
        }
        return sourceDataMap;
    }
}