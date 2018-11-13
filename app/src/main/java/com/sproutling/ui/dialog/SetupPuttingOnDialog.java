/*
 * Copyright (c) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sproutling.R;
import com.sproutling.object.StepPosition;
import com.sproutling.ui.widget.PagerDotView;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brady.lin on 2016/12/28.
 */

public class SetupPuttingOnDialog extends Dialog {

    private List<View> mPutOnList;
    private PagerDotView mPagerDotView;
    private int mPagePosition = -1;

    public SetupPuttingOnDialog(Context context) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dialog_setup_putting_on);
        setCanceledOnTouchOutside(true);

        LayoutInflater inflater = getLayoutInflater();

        ViewPager pager = (ViewPager) findViewById(R.id.info_pager);
        ShTextView closeBtn = findViewById(R.id.close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        pager.addOnPageChangeListener(mOnPageChangeListener);
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.dot_layout);

        View putOn1 = inflater.inflate(R.layout.fragment_setup_putting_on_1, null);
        View putOn2 = inflater.inflate(R.layout.fragment_setup_putting_on_2, null);
        View putOn3 = inflater.inflate(R.layout.fragment_setup_putting_on_3, null);
        View putOn4 = inflater.inflate(R.layout.fragment_setup_putting_on_4, null);
        View putOn5 = inflater.inflate(R.layout.fragment_setup_putting_on_5, null);

        mPutOnList = new ArrayList<>();
        mPutOnList.add(putOn1);
        mPutOnList.add(putOn2);
        mPutOnList.add(putOn3);
        mPutOnList.add(putOn4);
        mPutOnList.add(putOn5);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        pager.setAdapter(viewPagerAdapter);

        mPagerDotView = new PagerDotView(context, dotsLayout, mPutOnList.size());
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String jsonString = Utils.toJsonString(new StepPosition(mPagePosition));
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                    Utils.logEvents(LogEvents.PUTTING_ON_CLOSE, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mPagerDotView.setPosition(position);
            mPagePosition = position + 1;
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
            return mPutOnList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                container.addView(mPutOnList.get(position), 0);
            } catch (Exception ignored) {
            }
            return mPutOnList.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
