package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 2/8/18.
 */

public class UserStatusFeedbackRequestBody {
    @SerializedName("submitted_at")
    private long mSubmittedAt;
    @SerializedName("sproutling_status")
    private String mStatus;
    @SerializedName("confirm")
    private boolean mConfirm;
    @SerializedName("user_status")
    private String mUserStatus;
    @SerializedName("user_notes")
    private String mUserNotes;

    public UserStatusFeedbackRequestBody(String status, boolean confirm) {
        setValues(status, confirm);
    }

    public UserStatusFeedbackRequestBody(String status, boolean confirm, String userStatus, String userNotes) {
        setValues(status, confirm);
        mUserStatus = userStatus;
        mUserNotes = userNotes;
    }

    private void setValues(String status, boolean confirm) {
        mSubmittedAt = System.currentTimeMillis() / 1000;
        mStatus = status;
        mConfirm = confirm;
    }
}
