/*
 * Copyright (c) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bradylin on 8/16/17.
 */

public class WrapContentHeightViewPager extends ViewPager {

    private static final String TAG = "ViewPager";
    private int mHeight = 0;

    public WrapContentHeightViewPager(Context context) {
        super(context);
    }

    public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight() + 40;
            if (mHeight == 0) mHeight = h;
        }


//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.AT_MOST);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);

//        heightMeasureSpec = (int) (heightMeasureSpec * .75);
//        widthMeasureSpec = (int) (widthMeasureSpec * .75);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}