package com.sproutling.object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 5/23/17.
 */

public class MixpanelUserProfileData {
    public static final String BABY_AGE_IN_MONTHS = "Baby Age In Months";
    @SerializedName("$first_name")
    private String mFirstName;
    @SerializedName("$last_name")
    private String mLastName;
    @SerializedName("$name")
    private String mFullName;
    @SerializedName("$email")
    private String mEmail;
    @SerializedName("Joined Date")
    private String mJoinedDate;

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
        updateFullName();
    }

    private void updateFullName() {
        mFullName = mFirstName + " " + mLastName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
        updateFullName();
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getJoinedDate() {
        return mJoinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        mJoinedDate = joinedDate;
    }

    public String getFullName() {
        return mFullName;
    }
}
