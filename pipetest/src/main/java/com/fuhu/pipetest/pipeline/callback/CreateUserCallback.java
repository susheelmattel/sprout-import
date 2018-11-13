package com.fuhu.pipetest.pipeline.callback;

import android.util.Log;

import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipetest.pipeline.object.SSUserItem;

public class CreateUserCallback implements IPipeCallback {
    private static final String TAG = CreateUserCallback.class.getSimpleName();
    /**
     * When pipeline is finished.
     * @param responseObject output data
     */
    public void onResult(final Object responseObject) {
        Log.d(TAG, "onResult: " + responseObject.getClass().getSimpleName());
        if (responseObject instanceof SSUserItem) {
            SSUserItem ssUser = (SSUserItem) responseObject;

            Log.d(TAG, "id: " + ssUser.getId());
            Log.d(TAG, "account id: " + ssUser.getAccountId());
            Log.d(TAG, "create at: " + ssUser.getCreatedAt());
        }
    }

    /**
     * This callback is invoked when there is a pipeline execution error.
     * @param status error
     * @param pipeItem errorItem
     */
    public void onError(final int status, final IPipeItem pipeItem) {
        if (pipeItem != null) {
            Log.d(TAG, "Error: " + pipeItem.getErrorMessage());
        }
    }
}