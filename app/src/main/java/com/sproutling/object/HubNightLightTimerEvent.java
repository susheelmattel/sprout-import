package com.sproutling.object;

/**
 * Created by subram13 on 3/14/18.
 */

public class HubNightLightTimerEvent {
    private int mTimerSeconds;

    public HubNightLightTimerEvent(int timerSeconds) {
        mTimerSeconds = timerSeconds;
    }

    public int getTimerSeconds() {
        return mTimerSeconds;
    }
}
