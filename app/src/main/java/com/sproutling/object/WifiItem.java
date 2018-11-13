/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment;

/**
 * Created by subram13 on 1/25/17.
 */

public class WifiItem implements Parcelable {
    private String mSsid;
    private int mRange;

    @ManualNetworkTypeFragment.NetworkSecurityType
    private String mNetworkSecurityType;

    public WifiItem(String ssid, int range, int networkSecurityType) {
        mSsid = ssid;
        mRange = range;
        switch (networkSecurityType) {
            case 1:
                mNetworkSecurityType = ManualNetworkTypeFragment.NONE;
                break;
            case 2:
                mNetworkSecurityType = ManualNetworkTypeFragment.WPA;
                break;
            case 3:
                mNetworkSecurityType = ManualNetworkTypeFragment.WEP;
                break;
            default:
                mNetworkSecurityType = ManualNetworkTypeFragment.WPA;
        }

    }

    public String getSsid() {
        return mSsid;
    }

    public int getRange() {
        return mRange;
    }

    public  @ManualNetworkTypeFragment.NetworkSecurityType String getNetworkSecurityType() {
        return mNetworkSecurityType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSsid);
        dest.writeInt(mRange);
        dest.writeString(mNetworkSecurityType);
    }

    protected WifiItem(Parcel in) {
        mSsid = in.readString();
        mRange = in.readInt();
        mNetworkSecurityType = in.readString();
    }

    public static final Parcelable.Creator<WifiItem> CREATOR = new Parcelable.Creator<WifiItem>() {
        @Override
        public WifiItem createFromParcel(Parcel source) {
            return new WifiItem(source);
        }

        @Override
        public WifiItem[] newArray(int size) {
            return new WifiItem[size];
        }
    };
}
