package com.example.memorytracker.memorabilia;

import com.example.memorytracker.R;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

import java.util.List;

public class CustomListViewAdapter extends ArrayAdapter<String> {


    Context context;
    int resourceId;
    int imageViewResourceId;
    List<String> items;
    private  boolean zoomOut = false;


    public CustomListViewAdapter(Context context, int resourceId, int imageViewResourceId,
                                 List<String> items) {
        super(context, resourceId, imageViewResourceId, items);
        this.context = context;
        this.resourceId = resourceId;
        this.imageViewResourceId = imageViewResourceId;
        this.items = items;
    }

    private class ViewHolder {
        ImageView imageView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        String uri = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(this.resourceId, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(this.imageViewResourceId);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.imageView.setTag(uri);
        Glide.with(this.context)
                .load(uri)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openFullscreen(v, uri);
            }
        });
        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openFullscreen(View v, String uri) {
        new PhotoFullPopupWindow(context, v, uri);
    }
}  