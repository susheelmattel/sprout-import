package com.sproutling.events;

/**
 * Created by 322511 on 8/14/18.
 */

public class ServiceNetworkEvent {

    private boolean mNetworkStatus;

    public ServiceNetworkEvent(boolean networkStatus) {
        mNetworkStatus = networkStatus;
    }

    public boolean getNetworkStatus() {
        return mNetworkStatus;
    }
}

