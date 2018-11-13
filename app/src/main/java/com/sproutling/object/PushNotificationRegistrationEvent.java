package com.sproutling.object;

import com.sproutling.pojos.CreateHandheldResponse;

/**
 * Created by subram13 on 8/1/17.
 */

public class PushNotificationRegistrationEvent {
    private boolean mIsSuccess;
    private CreateHandheldResponse mCreateHandheldResponse;
    private Throwable mThrowable;

    public PushNotificationRegistrationEvent(boolean isSuccess, CreateHandheldResponse createHandheldResponse, Throwable throwable) {
        mIsSuccess = isSuccess;
        mCreateHandheldResponse = createHandheldResponse;
        mThrowable = throwable;
    }

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public CreateHandheldResponse getCreateHandheldResponse() {
        return mCreateHandheldResponse;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
