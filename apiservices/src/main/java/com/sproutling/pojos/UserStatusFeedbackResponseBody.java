package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by subram13 on 2/8/18.
 */

public class UserStatusFeedbackResponseBody extends UserStatusFeedbackRequestBody {

    @SerializedName("id")
    private String mId;

    @SerializedName("user_id")
    private String mUserID;

    @SerializedName("created_at")
    private Date mCreatedAt;

    @SerializedName("updated_at")
    private Date mUpdatedAt;

    public UserStatusFeedbackResponseBody(String status, boolean confirm) {
        super(status, confirm);
    }

    public UserStatusFeedbackResponseBody(String status, boolean confirm, String userStatus, String userNotes) {
        super(status, confirm, userStatus, userNotes);
    }
}
