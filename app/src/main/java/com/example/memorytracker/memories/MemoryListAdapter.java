package com.example.memorytracker.memories;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.example.memorytracker.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryListAdapter extends BaseAdapter {

    private final int resourceLayout;
    private final Context mContext;
    final ArrayList items;

    public MemoryListAdapter(Context context, int resource, LinkedHashMap<String, Boolean> items) {
        this.resourceLayout = resource;
        this.mContext = context;
        this.items = new ArrayList();
        this.items.addAll(items.entrySet());
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Map.Entry getItem(int position) {
        return (Map.Entry) this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        Map.Entry item = (Map.Entry) this.items.get(position);
        return item.getKey().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Map.Entry item = getItem(position);

        if (item != null) {
            CheckBox checkBox = v.findViewById(R.id.checkBox);

            if (checkBox != null) {
                checkBox.setText((String) item.getKey());
                checkBox.setChecked((Boolean) item.getValue());
            }
        }
        return v;
    }
}