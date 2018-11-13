/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.adapter.Settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sproutling.R;
import com.sproutling.object.FragmentItem;
import com.sproutling.ui.widget.ShTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradylin on 12/12/16.
 */

public class SettingsListAdapter extends BaseAdapter {

    private List<FragmentItem> mItems;
    private LayoutInflater mLayoutInflater;

    public SettingsListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mItems = new ArrayList<>();
    }

    public void setItems(List<FragmentItem> list) {
        mItems = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.list_item_activity_settings_main, parent, false);
            view.setTag(new ViewHolder(view));
        }

        String item = mItems.get(position).getName();
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.mShTextView.setText(item);
        return view;
    }

    private static class ViewHolder {
        ShTextView mShTextView;
        ImageView mImageView;

        private ViewHolder(View view) {
            mShTextView = (ShTextView) view.findViewById(R.id.name);
            mImageView = (ImageView) view.findViewById(R.id.arrow);
        }
    }
}
