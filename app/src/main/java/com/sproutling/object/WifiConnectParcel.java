/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by subram13 on 1/16/17.
 */

public class WifiConnectParcel implements Parcelable {
    private int mSecurity=1;
    private int mSsidLength;
    private String mSsidName;
    private int mPassPhraseLength;
    private String mPassPhrase;

    public WifiConnectParcel(String ssidName, String passPhrase) {
        mSsidName = ssidName;
        mPassPhrase = passPhrase;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mSecurity);
        dest.writeInt(mSsidLength);
        dest.writeString(mSsidName);
        dest.writeInt(mPassPhraseLength);
        dest.writeString(mPassPhrase);
    }

    protected WifiConnectParcel(Parcel in) {
        mSecurity = in.readInt();
        mSsidLength = in.readInt();
        mSsidName = in.readString();
        mPassPhraseLength = in.readInt();
        mPassPhrase = in.readString();
    }

    public static final Parcelable.Creator<WifiConnectParcel> CREATOR = new Parcelable.Creator<WifiConnectParcel>() {
        @Override
        public WifiConnectParcel createFromParcel(Parcel source) {
            return new WifiConnectParcel(source);
        }

        @Override
        public WifiConnectParcel[] newArray(int size) {
            return new WifiConnectParcel[size];
        }
    };
}
