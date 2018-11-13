package com.fuhu.pipetest.pipeline.callback;

import android.util.Log;

import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipetest.App;
import com.fuhu.pipetest.pipeline.object.LoginResponse;
import com.fuhu.pipetest.services.AccountManagement;

public class LoginCallback implements IPipeCallback {
    private static final String TAG = LoginCallback.class.getSimpleName();
    /**
     * When pipeline is finished.
     * @param responseObject output data
     */
    public void onResult(final Object responseObject) {
        Log.d(TAG, "onResult: " + responseObject.getClass().getSimpleName());
        if (responseObject instanceof LoginResponse) {
            LoginResponse loginInfo = (LoginResponse) responseObject;

            Log.d(TAG, "Access token: " + loginInfo.getAccessToken());
            AccountManagement.getInstance(App.getAppContext()).writeLoginInfo(loginInfo);
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
