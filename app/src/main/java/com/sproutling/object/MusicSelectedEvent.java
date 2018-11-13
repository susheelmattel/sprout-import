package com.sproutling.object;

import java.util.ArrayList;

/**
 * Created by subram13 on 3/7/18.
 */

public class MusicSelectedEvent {
    private ArrayList<String> mMusicSongs;
    private String mPlayListName;
    private boolean mIsPlayList;

    public MusicSelectedEvent(ArrayList<String> musicSongs, String playListName, boolean isPlayList) {
        mMusicSongs = musicSongs;
        mPlayListName = playListName;
        mIsPlayList = isPlayList;
    }

    public String getPlayListName() {
        return mPlayListName;
    }

    public ArrayList<String> getMusicSongs() {
        return mMusicSongs;
    }

    public boolean isPlayList() {
        return mIsPlayList;
    }
}
