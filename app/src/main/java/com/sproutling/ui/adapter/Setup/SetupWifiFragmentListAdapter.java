/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.adapter.Setup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.helpshift.support.Support;
import com.sproutling.R;
import com.sproutling.object.WifiItem;
import com.sproutling.services.SHBluetoothLeService;
import com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment;
import com.sproutling.ui.widget.ShAlertView;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.Utils;

import java.util.ArrayList;

/**
 * Created by bradylin on 11/16/16.
 */

public class SetupWifiFragmentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int SSID = 0;
    private static final int OTHER_NETWORK = 1;
    private static final int FOOTER = 2;
    private static final int FAQ_PUBLISH_ID = 211;
    private ArrayList<WifiItem> mItems;
    private LayoutInflater mLayoutInflater;
    private SsidClickListener mSsidClickListener;
    private int mSelectedIndex = -1;
    private Context mContext;

    public SetupWifiFragmentListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mItems = new ArrayList<>();
    }

    public void setSelectedIndex(int index) {
        mSelectedIndex = index;
        notifyDataSetChanged();
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setItems(ArrayList<WifiItem> list) {
        mItems = list;
        notifyDataSetChanged();
    }

    public void setSsidClickListener(SsidClickListener ssidClickListener) {
        mSsidClickListener = ssidClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != FOOTER) {
            View view = mLayoutInflater.inflate(R.layout.list_item_fragment_setup_wi_fi, parent, false);
            return new WifiItemHolder(view);
        } else {
            View view = mLayoutInflater.inflate(R.layout.list_item_footer_wifi_setup, parent, false);
            return new FooterItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WifiItemHolder wifiItemHolder;
        if (position == getItemCount() - 2) {
            wifiItemHolder = (WifiItemHolder) holder;
            WifiItem wifiItem = new WifiItem(mContext.getString(R.string.setup_wifi_other), 0, SHBluetoothLeService.SECURITY_WPA);
            wifiItemHolder.bind(wifiItem, mSsidClickListener, true);
        } else if (position == getItemCount() - 1) {
        } else {
            wifiItemHolder = (WifiItemHolder) holder;
            wifiItemHolder.bind(mItems.get(position), mSsidClickListener, false);
        }
    }

    public void clearItems() {
        mItems.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER;
        } else if (position == getItemCount() - 2) {
            return OTHER_NETWORK;
        } else {
            return SSID;
        }
    }

    private class FooterItemHolder extends RecyclerView.ViewHolder {
        private ShTextView mShTextView;

        public FooterItemHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View view) {
            mShTextView = (ShTextView) view.findViewById(R.id.tv_dont_see_ur_network);
            mShTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Support.showSingleFAQ((Activity) mContext, String.valueOf(FAQ_PUBLISH_ID));
                }
            });
        }
    }

    private class WifiItemHolder extends RecyclerView.ViewHolder {
        private ShTextView mShTextView;
        private ImageView mImageView;
        private View mDivider;
        private View mView;

        private WifiItemHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View view) {
            mShTextView = (ShTextView) view.findViewById(R.id.ssid);
            mImageView = (ImageView) view.findViewById(R.id.img_exclamation);
            mDivider = view.findViewById(R.id.divider);
            mView = view;
        }

        private void bind(final WifiItem wifiItem, final SsidClickListener ssidClickListener, boolean isOtherNetwork) {
            setSsid(wifiItem.getSsid());
            if (!wifiItem.getNetworkSecurityType().equalsIgnoreCase(ManualNetworkTypeFragment.WEP)) {
                if (!Utils.isValidWifiSSID(wifiItem.getSsid())) {
                    showExclamation(true, false);
                    mDivider.setVisibility(View.VISIBLE);
                    mShTextView.setTextColor(Utils.getColor(mContext, R.color.grey30));
                } else {
                    showExclamation(false, false);
                    mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ssidClickListener != null) {
                                ssidClickListener.onClick(wifiItem);
                            }
                        }
                    });
                    if (isOtherNetwork) {
                        mDivider.setVisibility(View.INVISIBLE);
                        mShTextView.setTextColor(Utils.getColor(mContext, R.color.tealish));
                    } else {
                        mDivider.setVisibility(View.VISIBLE);
                        mShTextView.setTextColor(Utils.getColor(mContext, R.color.brownishGrey));
                    }
                }
            } else {
                showExclamation(true, true);
                mDivider.setVisibility(View.VISIBLE);
                mShTextView.setTextColor(Utils.getColor(mContext, R.color.grey30));
            }
        }

        public void setSsid(String text) {
            if (!TextUtils.isEmpty(text)) {
                mShTextView.setText(text);
            }
        }

        public void showExclamation(boolean show, final boolean isWepNetwork) {
            mImageView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            if (show) {
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showExclamationPopup(isWepNetwork);
                    }
                });
            }
        }
    }

    private void showExclamationPopup(boolean isWepNetwork) {
        final Dialog dialog = new Dialog(mContext, R.style.DialogFragmentPopup);

        ShAlertView shAlertView = new ShAlertView(mContext);
        shAlertView.setTitle(mContext.getString(R.string.network_not_supported));
        if (isWepNetwork) {
            shAlertView.setMessage(mContext.getString(R.string.network_not_supported_msg));
        } else {
            shAlertView.setMessage(mContext.getString(R.string.invalid_ssid_characters_msg));
        }
        shAlertView.setButtonText(mContext.getString(R.string.got_it));
        shAlertView.setImgAlert(R.drawable.ic_wep_alert_popup);

        shAlertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public interface SsidClickListener {
        void onClick(WifiItem wifiItem);
    }

}
