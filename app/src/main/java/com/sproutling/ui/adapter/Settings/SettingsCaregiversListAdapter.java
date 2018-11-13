/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.adapter.Settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sproutling.R;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.widget.ShTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradylin on 1/9/17.
 */

public class SettingsCaregiversListAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_CAREGIVER = 0;
    private static final int VIEW_TYPE_INVITE = 1;

    private Context mContext;
    private List<ListItem> mItems;
    private LayoutInflater mLayoutInflater;

    public SettingsCaregiversListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mItems = new ArrayList<>();
    }

    public void setItems(List<SSManagement.UserAccountInfo> items) {
        mItems = new ArrayList<>();
        for (SSManagement.UserAccountInfo userAccountInfo : items) {
            mItems.add(new CaregiverItem(userAccountInfo));
        }
        mItems.add(new SettingsCaregiversListAdapter.InviteItem(mContext.getResources().getString(R.string.settings_caregivers_invite)));
        notifyDataSetChanged();
    }

    public void addItem(ListItem item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ListItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        int itemType = getItemViewType(position);
        if (view == null) {
            if (itemType == VIEW_TYPE_CAREGIVER) {
                view = mLayoutInflater.inflate(R.layout.list_item_fragment_settings_caregivers_person, parent, false);
                view.setTag(new CaregiverHolder(view));
            } else if (itemType == VIEW_TYPE_INVITE) {
                view = mLayoutInflater.inflate(R.layout.list_item_fragment_settings_caregivers_invite, parent, false);
                view.setTag(new InviteHolder(view));
            }
        }
        if (itemType == VIEW_TYPE_CAREGIVER) {
            CaregiverItem item = (CaregiverItem) mItems.get(position);
            CaregiverHolder itemHolder = (CaregiverHolder) view.getTag();
            itemHolder.mNameView.setText(item.caregiver.firstName + " " + item.caregiver.lastName);
        } else if (itemType == VIEW_TYPE_INVITE) {
            InviteItem item = (InviteItem) mItems.get(position);
            InviteHolder itemHolder = (InviteHolder) view.getTag();
            itemHolder.mNameView.setText(item.name);
        }
        return view;
    }

    private static class CaregiverHolder {
        ShTextView mNameView;
        ImageView mArrowView;

        private CaregiverHolder(View view) {
            mNameView = (ShTextView) view.findViewById(R.id.name);
            mArrowView = (ImageView) view.findViewById(R.id.arrow);
        }
    }

    private static class InviteHolder {
        ShTextView mNameView;

        private InviteHolder(View view) {
            mNameView = (ShTextView) view.findViewById(R.id.name);
        }
    }

    public static abstract class ListItem {
        public int type;
    }

    public static class CaregiverItem extends ListItem {
        public SSManagement.UserAccountInfo caregiver;

        private CaregiverItem() {
            type = VIEW_TYPE_CAREGIVER;
        }

        private CaregiverItem(SSManagement.UserAccountInfo caregiver) {
            this();
            this.caregiver = caregiver;
        }
    }

    public static class InviteItem extends ListItem {
        public String name;

        private InviteItem() {
            type = VIEW_TYPE_INVITE;
        }

        private InviteItem(String name) {
            this();
            this.name = name;
        }
    }
}
