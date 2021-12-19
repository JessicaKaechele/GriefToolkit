package com.example.memorytracker;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.memorytracker.source.SourceCreator;
import com.example.memorytracker.views.AudioView;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SourceCreatorAdapter {
    Context context;
    ViewGroup parentView;
    LinkedHashMap<String, String> items;
    public SourceCreatorAdapter(Context context, ViewGroup parentView,
                                LinkedHashMap<String, String> items) {
        this.context = context;
        this.items = items;
        this.parentView = parentView;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        EditText editTextView;
        VideoView videoView;
        AudioView audioView;
    }

    public void addViews(){
        this.parentView.removeAllViews();
        for (int i = 0; i < getCount(); i++){
            this.parentView.addView(getView(i));
        }
    }

    public int getCount() {
        return this.items.size();
    }

    public Pair<String, String> getItem(int i) {
        String key = String.valueOf(this.items.keySet().toArray()[i]);
        String value = this.items.get(key);
        return new Pair(key, value);
    }

    public View getView(int position) {
        View contentView = null;
        ViewHolder holder = new ViewHolder();
        Pair<String, String> item = getItem(position);

        String content = item.first;
        String type = item.second;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        switch (type) {
            case "img":
                contentView = getImageView(holder, content, mInflater);
                break;
            case "text":
                contentView = getTextView(holder, content, mInflater, position);
                break;
            case "audio": {
                contentView = getAudioView(holder, content, mInflater);
                break;
            }
            case "video": {
                contentView = getVideoView(holder, content);
                break;
            }
        }
        return contentView;
    }

    @NonNull
    private View getImageView(ViewHolder holder, String content, LayoutInflater mInflater) {
        View contentView;
        contentView = mInflater.inflate(R.layout.input_view_img, null);
        holder.imageView = (ImageView) contentView.findViewById(R.id.imageView);
        contentView.setTag(holder);
        holder.imageView.setTag(content);
        Glide.with(this.context)
                .load(Uri.parse(content))
                .into(holder.imageView);
        return contentView;
    }

    @NonNull
    private View getTextView(ViewHolder holder, String content, LayoutInflater mInflater, int position) {
        View contentView;
        contentView = mInflater.inflate(R.layout.input_view, null);
        holder.editTextView = (EditText) contentView.findViewById(R.id.editText);
        contentView.setTag(holder);
        holder.editTextView.setText(content);
        holder.editTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                saveTextData(editable.toString(), position);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }


        });

        return contentView;
    }
    private void saveTextData(String content, int position){
        LinkedHashMap<String, String> newData = new LinkedHashMap<>();
        int i = 0;
//        for (Map.Entry entry : ((SourceCreator)this.context).sourceData.entrySet()){
//            if (i == position){
//                newData.put(content, "text");
//
//            }else{
//                newData.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
//            }
//            i++;
//        }
//        ((SourceCreator)this.context).sourceData = newData;
    }

    @NonNull
    private View getAudioView(ViewHolder holder, String content, LayoutInflater mInflater) {
        View contentView;
        contentView = mInflater.inflate(R.layout.input_view_audio, null);
        holder.audioView = (AudioView) contentView.findViewById(R.id.audioView);
        contentView.setTag(holder);
        Uri uri = Uri.parse(content);

        try {
            holder.audioView.setDataSource(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.audioView.setTag(uri.toString());
        return contentView;
    }

    @NonNull
    private View getVideoView(ViewHolder holder, String content) {
        View contentView;
        contentView = View.inflate(this.context, R.layout.input_view_vid, null);
        holder.videoView = (VideoView) contentView.findViewById(R.id.videoView);
        contentView.setTag(holder);
        Uri uri = Uri.parse(content);
        holder.videoView.setVideoURI(uri);
        holder.videoView.setTag(uri.toString());
        MediaController mediaController = new MediaController(this.context);
        holder.videoView.setMediaController(mediaController);
        return contentView;

    }
}
