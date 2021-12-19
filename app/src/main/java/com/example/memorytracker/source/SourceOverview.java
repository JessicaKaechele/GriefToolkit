package com.example.memorytracker.source;

import static com.example.memorytracker.Utils.*;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.memorytracker.R;
import com.example.memorytracker.Utils;
import com.example.memorytracker.views.AudioView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SourceOverview extends AppCompatActivity {

    private final String filename = "source_data.json";
    private  String title;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_overview);

        Intent intent = getIntent();
        title = intent.getStringExtra(Sources.SOURCE_TITLE);

        LinkedHashMap<String, String> sourceData = getSourceData().get(title);

        LinearLayout linearLayout = findViewById(R.id.LinearLayout);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText(title);

        for (Map.Entry<String, String> entry : sourceData.entrySet()) {
            String content = entry.getKey();
            String type = entry.getValue();
            CardView cardView = getCardView();
            View addView = null;
            switch (type) {
                case "text":
                    addView = getTextView(content);
                    break;
                case "img":
                    addView = getImgView(content);
                    break;
                case "video":
                    addView = getVideoView(content);
                    break;
                case "audio":
                    addView = getAudioView(content);
                    break;
            }
            cardView.addView(addView);
            linearLayout.addView(cardView);
        }
    }

    @NonNull
    private TextView getTextView(String text) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(getResources().getDimensionPixelSize(R.dimen.margin),getResources().getDimensionPixelSize(R.dimen.margin_top_bottom),getResources().getDimensionPixelSize(R.dimen.margin),getResources().getDimensionPixelSize(R.dimen.margin_top_bottom));
        textView.setLayoutParams(params);
        textView.setText(text);
        return textView;
    }

    @NonNull
    private ImageView getImgView(String uri) {


        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        Glide.with(this)
                .load(uri)
                .into(imageView);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }

    @NonNull
    private FrameLayout getVideoView(String uri) {
        FrameLayout frameLayout = new FrameLayout(this);
        VideoView videoView = new VideoView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(params);
        videoView.setVideoURI(Uri.parse(uri));
        videoView.setOnClickListener(view -> {
            if (videoView.isPlaying()){
                videoView.pause();
            }else{
                videoView.start();
            }
        });
        ImageButton fullScreenButton = new ImageButton(this);
        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        buttonParams.rightMargin =  Utils.convertDpToPx(this, 5);
        fullScreenButton.setBackgroundColor(Color.TRANSPARENT);
        fullScreenButton.setImageTintList(this.getResources().getColorStateList(R.color.white));


        fullScreenButton.setLayoutParams(buttonParams);
        fullScreenButton.setImageResource(R.drawable.fullscreen);

        fullScreenButton.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), VideoFullscreenActivity.class);
            int position = videoView.getCurrentPosition();
            intent.putExtra("uri", uri);
            intent.putExtra("position", String.valueOf(position));
            startActivity(intent);

        });
        frameLayout.addView(videoView);
        frameLayout.addView(fullScreenButton);

        return frameLayout;
    }

    @NonNull
    private AudioView getAudioView(String uri) {
        AudioView audioView = new AudioView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        audioView.setLayoutParams(params);
        try {
            audioView.setDataSource(Uri.parse(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioView;
    }

    @NonNull
    private CardView getCardView() {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0,getResources().getDimensionPixelSize(R.dimen.margin));
        cardView.setLayoutParams(params);
        cardView.setRadius(getResources().getDimensionPixelSize(R.dimen.rounded_corners));
        return cardView;
    }

    public void onDelete(View view){
        LinkedHashMap<String, LinkedHashMap<String, String>> data = getSourceData();
        LinkedHashMap<String, String> removedData = data.remove(title);
        deleteMediaFiles(data, removedData);
        writeToFile(this, data, filename);
        Intent intent = new Intent(this, Sources.class);
        startActivity(intent);
        finish();
    }

    private void deleteMediaFiles(LinkedHashMap<String, LinkedHashMap<String, String>> data, LinkedHashMap<String, String> removedData) {
        for(String content : removedData.keySet()) {
            try {
                List<String> list = Utils.getContentStrings(data);
                if (!list.contains(content)){
                    Uri uri = Uri.parse(content);
                    Utils.deleteFile(String.valueOf(uri));
                }
            } catch (Exception e) {
                // no uri
            }
        }
    }



    public void onModify(View v){
        Intent intent = new Intent(this, SourceCreator.class);
        intent.putExtra(Sources.SOURCE_TITLE, title);
        startActivity(intent);
        finish();
    }

    @NonNull
    private LinkedHashMap<String, LinkedHashMap<String, String>> getSourceData() {
        String sourceData = null;
        try {
            sourceData = readFromFile(SourceOverview.this, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fromJson(sourceData);
    }

    private LinkedHashMap<String, LinkedHashMap<String, String>> fromJson(String sourceTitleData) {
        LinkedHashMap<String, LinkedHashMap<String, String>> sourceMap;
        if (sourceTitleData == null) {
            sourceMap = new LinkedHashMap<>();
        } else {
            sourceMap = new Gson().fromJson(sourceTitleData, new TypeToken<LinkedHashMap<String, LinkedHashMap<String, String>>>(){}.getType());
        }
        return sourceMap;
    }

}