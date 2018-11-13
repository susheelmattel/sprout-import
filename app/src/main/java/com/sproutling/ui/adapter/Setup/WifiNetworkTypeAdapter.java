/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.adapter.Setup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment;
import com.sproutling.ui.widget.ShTextView;

import java.util.ArrayList;

/**
 * Created by subram13 on 1/23/17.
 */

public class WifiNetworkTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> mNetworkTypes;
    private LayoutInflater mLayoutInflater;
    private NetworkTypeClickListener mNetworkTypeClickListener;
    private String mSelectedNetworkType;
    private int mSelectedIndex = -1;
    private Context mContext;

    public WifiNetworkTypeAdapter(Context context, ArrayList<String> networkTypes) {
        mNetworkTypes = networkTypes;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        if (networkTypes != null) {
            mNetworkTypes = networkTypes;
        } else {
            mNetworkTypes = new ArrayList<>();
        }

    }

    public void setSelectedNetworkType(@ManualNetworkTypeFragment.NetworkSecurityType String selectedNetworkType) {
        mSelectedNetworkType = selectedNetworkType;
    }

    public void setNetworkTypeClickListener(NetworkTypeClickListener networkTypeClickListener) {
        mNetworkTypeClickListener = networkTypeClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_item_fragment_setup_wi_fi, parent, false);
        return new NetworkTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NetworkTypeViewHolder networkTypeViewHolder = (NetworkTypeViewHolder) holder;
        networkTypeViewHolder.bind(mNetworkTypes.get(position), mNetworkTypeClickListener, position == getItemCount() - 1, mSelectedNetworkType);
    }

    @Override
    public int getItemCount() {
        return mNetworkTypes.size();
    }

    private class NetworkTypeViewHolder extends RecyclerView.ViewHolder {
        private ShTextView mShTextView;
        private ImageView mImageView;
        private View mDivider;
        private View mView;

        private NetworkTypeViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            init(itemView);
        }

        private void init(View view) {
            mShTextView = (ShTextView) view.findViewById(R.id.ssid);
            mImageView = (ImageView) view.findViewById(R.id.img_exclamation);
            mDivider = view.findViewById(R.id.divider);
            mView = view;
        }

        private void bind(final String text, final NetworkTypeClickListener networkTypeClickListener, boolean isFinalItem, @ManualNetworkTypeFragment.NetworkSecurityType String selectedNetworkType) {
            setNetworkType(text);
            setSelected((!TextUtils.isEmpty(selectedNetworkType)) && selectedNetworkType.equalsIgnoreCase(text));

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (networkTypeClickListener != null) {
                        networkTypeClickListener.onClick(text);
                    }
                }
            });
            if (isFinalItem) {
                mDivider.setVisibility(View.INVISIBLE);
            } else {
                mDivider.setVisibility(View.VISIBLE);
                mShTextView.setTextColor(App.getAppContext().getResources().getColor(R.color.brownishGrey));
            }
        }

        private void setNetworkType(String text) {
            if (!TextUtils.isEmpty(text)) {
                mShTextView.setText(text);
            }
        }

        private void setSelected(boolean selected) {
            if (selected) {
                mImageView.setImageResource(R.mipmap.checkmark_connecttowifi);
            } else {
                mImageView.setImageDrawable(null);
            }
        }
    }

    public interface NetworkTypeClickListener {
        void onClick(@ManualNetworkTypeFragment.NetworkSecurityType String networkType);
    }
}
