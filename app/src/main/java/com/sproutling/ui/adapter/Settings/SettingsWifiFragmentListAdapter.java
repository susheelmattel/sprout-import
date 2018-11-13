/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.adapter.Settings;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradylin on 12/14/16.
 */

public class SettingsWifiFragmentListAdapter extends BaseAdapter {

    private List<ScanResult> mItems;
    private LayoutInflater mLayoutInflater;
    private int mSelectedIndex = -1;

    public SettingsWifiFragmentListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mItems = new ArrayList<>();
    }

    public void setSelectedIndex(int index) {
        mSelectedIndex = index;
        notifyDataSetChanged();
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setItems(List<ScanResult> list) {
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
            view = mLayoutInflater.inflate(R.layout.list_item_fragment_setup_wi_fi, parent, false);
            view.setTag(new ViewHolder(view));
        }

        ScanResult item = mItems.get(position);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.mShTextView.setText(item.SSID);
        if (mSelectedIndex == position)
            viewHolder.mImageView.setImageResource(R.mipmap.checkmark_connecttowifi);
        else viewHolder.mImageView.setImageDrawable(null);
        return view;
    }

    private static class ViewHolder {
        ShTextView mShTextView;
        ImageView mImageView;

        private ViewHolder(View view) {
            mShTextView = (ShTextView) view.findViewById(R.id.ssid);
            mImageView = (ImageView) view.findViewById(R.id.check);
        }
    }
}
