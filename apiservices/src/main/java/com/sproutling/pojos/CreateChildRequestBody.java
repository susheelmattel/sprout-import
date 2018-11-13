package com.sproutling.pojos;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by subram13 on 3/6/17.
 */

public class CreateChildRequestBody {
    public static final String MALE = "M";
    public static final String FEMALE = "F";
    @SerializedName("first_name")
    private String mFirstname;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("birth_date")
    private String mBirthday;
    @SerializedName("due_date")
    private String mDueDate;
    @SerializedName("twin_id")
    private String mTwinId;
    @SerializedName("gender")
    private String mGender;

    public CreateChildRequestBody(String firstName, String lastName, String birthday, String dueDate, String twinId, @Gender String gender) {
        mFirstname = firstName;
        mLastName = lastName;
        mBirthday = birthday;
        mDueDate = dueDate;
        mTwinId = twinId;
        mGender = gender;
    }

    public String getFirstname() {
        return mFirstname;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public String getDueDate() {
        return mDueDate;
    }

    public String getTwinId() {
        return mTwinId;
    }

    public String getGender() {
        return mGender;
    }

    @StringDef({MALE, FEMALE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Gender {

    }
}
