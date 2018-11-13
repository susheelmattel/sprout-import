/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sproutling.R;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.AnimationBlinkView;
import com.sproutling.ui.widget.PagerDotView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by subram13 on 2/6/17.
 */

public class SettingsWifiSetUpFragment extends BaseFragment {
    public static final String TAG = "SettingsWifiSetUpFragment";

    private ViewPager mPager;
    private LinearLayout mDotsLayout;
    private List<View> mSettingList;
    private SettingsWifiSetUpFragment.ViewPagerAdapter mViewPagerAdapter;
    private PagerDotView mPagerDotView;
    private Button mBtnContinue;
    private AnimationBlinkView mBlinkView;

    private SettingsWifiSetUpFragment.WifiSettingsSetupListener mListener;

    public SettingsWifiSetUpFragment() {
    }

    public static SettingsWifiSetUpFragment newInstance() {
        return new SettingsWifiSetUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_setup, container, false);
        mPager = (ViewPager) view.findViewById(R.id.info_pager);
        mPager.addOnPageChangeListener(mOnPageChangeListener);
        mDotsLayout = (LinearLayout) view.findViewById(R.id.dot_layout);
        mBtnContinue = (Button) view.findViewById(R.id.btn_scan_wifi);
        mBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) mListener.onContinueClicked();
            }
        });
        enableContinueBtn(false);

        View setting1 = inflater.inflate(R.layout.fragment_wifi_settings_step_1, container, false);
        View setting2 = inflater.inflate(R.layout.fragment_wifi_settings_step_2, container, false);
        View setting3 = inflater.inflate(R.layout.fragment_wifi_settings_step_3, container, false);

        mBlinkView = new AnimationBlinkView(setting3.findViewById(R.id.blink));

        mSettingList = new ArrayList<>();
        mSettingList.add(setting1);
        mSettingList.add(setting2);
        mSettingList.add(setting3);

        mViewPagerAdapter = new SettingsWifiSetUpFragment.ViewPagerAdapter();
        mPager.setAdapter(mViewPagerAdapter);

        mPagerDotView = new PagerDotView(getActivity(), mDotsLayout, mSettingList.size());
        return view;
    }

    private void enableContinueBtn(boolean enable) {
        mBtnContinue.setEnabled(enable);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initListener();
    }

    private void initListener() {
        try {
            mListener = (WifiSettingsSetupListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement WifiSettingsSetupListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            enableContinueBtn(position == mSettingList.size() - 1);
            if (position == mSettingList.size() - 1) {
                mBlinkView.start();
            } else {
                mBlinkView.cancel();
            }
            mPagerDotView.setPosition(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    private class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mSettingList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                container.addView(mSettingList.get(position), 0);
            } catch (Exception e) {
            }
            return mSettingList.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((RelativeLayout) object);
        }
    }

    public interface WifiSettingsSetupListener {
        void onContinueClicked();
    }
}
