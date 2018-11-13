/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.services;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sproutling.common.utils.Utils;


/**
 * Created by subram13 on 4/10/17.
 */

public class SHFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = SHFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application sInstance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        Utils.sendRegistrationToServer(this, refreshedToken);
    }
}
