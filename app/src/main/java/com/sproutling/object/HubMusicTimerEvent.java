package com.sproutling.object;

/**
 * Created by subram13 on 3/14/18.
 */

public class HubMusicTimerEvent {
    private int mTimerSeconds;

    public HubMusicTimerEvent(int timerSeconds) {
        mTimerSeconds = timerSeconds;
    }

    public int getTimerSeconds() {
        return mTimerSeconds;
    }
}
