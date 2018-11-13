/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.ui.widget;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sproutling.common.R;


public class PagerDotView {

    private Context mContext;

    private int mSize;
    private int mDotWidth, mDotHeight;
    private int mMarginLeft, mMarginRight, mMarginTop, mMarginBottom;

    private ImageView[] mImageViews;
    private LinearLayout mViewPoints;

    public PagerDotView(Context context, LinearLayout view, int size) {
        mContext = context;
        mViewPoints = view;
        mImageViews = new ImageView[mSize = size];
        init();
    }

    private void init() {
        mDotWidth = mDotHeight = (int) mContext.getResources().getDimension(R.dimen.pager_dot_radius);
        mMarginLeft = mMarginRight = (int) mContext.getResources().getDimension(R.dimen.pager_dot_margin);
        mMarginTop = mMarginBottom = 0;

        for(int i = 0; i < mSize; i++){
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(mDotWidth, mDotHeight));

            mImageViews[i] = imageView;
            mImageViews[i].setBackgroundResource(i == 0 ? R.drawable.view_pager_dot_selected : R.drawable.view_pager_dot);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mDotWidth, mDotHeight);
            layoutParams.setMargins(mMarginLeft, mMarginTop, mMarginRight, mMarginBottom);
            mViewPoints.addView(mImageViews[i], layoutParams);
        }
    }

    private void update() {
        int size = mViewPoints.getChildCount();
        for (int i=0; i < size; i++) {
            View view = mViewPoints.getChildAt(i);
            view.setLayoutParams(new LinearLayout.LayoutParams(mDotWidth, mDotHeight));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mDotWidth, mDotHeight);
            layoutParams.setMargins(mMarginLeft, mMarginTop, mMarginRight, mMarginBottom);
            view.setLayoutParams(layoutParams);
        }
    }

    public void setDotRadius(int radius) {
        mDotWidth = mDotHeight = getDPSize(radius);
        update();
    }

    public void setMargin(int left, int top, int right, int bottom) {
        mMarginLeft = getDPSize(left);
        mMarginRight = getDPSize(right);
        mMarginTop = getDPSize(top);
        mMarginBottom = getDPSize(bottom);
        update();
    }

    public void setPosition(int position) {
        for(int i = 0; i < mSize; i++) {
            mImageViews[position % mSize].setBackgroundResource(R.drawable.view_pager_dot_selected);
            if (position % mSize != i) {
                mImageViews[i].setBackgroundResource(R.drawable.view_pager_dot);
            }
        }
    }

    private int getDPSize(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                size, mContext.getResources().getDisplayMetrics());
    }
}
