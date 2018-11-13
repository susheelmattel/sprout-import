package com.sproutling.ui.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.ui.fragment.MusicSongFragmentView;

import java.util.ArrayList;

/**
 * Created by subram13 on 3/1/18.
 */

public class MusicSongPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;
    private Context mContext;
    private ArrayList<MusicSongFragmentView> mMusicSongFragmentViews = new ArrayList<>();

    public MusicSongPagerAdapter(FragmentManager fm) {
        super(fm);
        mMusicSongFragmentViews.add(MusicSongFragmentView.Companion.getInstance(MusicSongFragmentView.PLAYLISTS));
        mMusicSongFragmentViews.add(MusicSongFragmentView.Companion.getInstance(MusicSongFragmentView.MUSIC));
        mMusicSongFragmentViews.add(MusicSongFragmentView.Companion.getInstance(MusicSongFragmentView.SOUND));
    }

    @Override
    public Fragment getItem(int position) {
        return mMusicSongFragmentViews.get(position);
    }

    public ArrayList<MusicSongFragmentView> getMusicSongFragmentViews() {
        return mMusicSongFragmentViews;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return App.getInstance().getString(R.string.playlists);
            case 1:
                return App.getInstance().getString(R.string.music);
            case 2:
                return App.getInstance().getString(R.string.sound);
            default:
                return "";
        }
    }
}
