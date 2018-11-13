package com.sproutling.object;

import java.util.ArrayList;

/**
 * Created by subram13 on 3/7/18.
 */

public class CustomPlaylist {
    private ArrayList<String> mMusicSongs;

    public CustomPlaylist(ArrayList<String> musicSongs) {
        mMusicSongs = musicSongs;
    }

    public ArrayList<String> getMusicSongs() {
        return mMusicSongs;
    }
}
