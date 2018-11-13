/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sproutling.R;
import com.sproutling.ui.view.StatusProgressBar;
import com.sproutling.utils.LogEvents;

/**
 * Created by moi0312 on 2017/1/20.
 */
public class FlowerItem extends LinearLayout {

    public static final int FLOWER_ITEM_COUNT = 5;
    private static final float RING_WIDTH = 8;
    private static final long RING_DURATION = 300;
    public static int[] sIcons = new int[]{
            R.drawable.ic_flower_expect,
            R.drawable.ic_flower_prediction,
            R.drawable.flower_heart,
            R.drawable.ic_flower_duration,
            R.drawable.ic_flower_improving
    };
    public static int[] sTitles = new int[]{
            R.string.flower_whatToExpect_title,
            R.string.flower_sleepPrediction_title,
            R.string.flower_unusualHeartbeat_title,
            R.string.flower_duration_title,
            R.string.flower_improving_title
    };
    public static int[] sDescriptions = new int[]{
            R.string.flower_whatToExpect_desc,
            R.string.flower_sleepPrediction_desc,
            R.string.flower_unusualHeartbeat_desc,
            R.string.flower_duration_desc,
            R.string.flower_improving_desc
    };
    public static String[] sEventNames = new String[]{
            LogEvents.LEARNING_PERIOD_WHAT_TO_EXPECT,
            LogEvents.LEARNING_PERIOD_SLEEP_PREDICTION,
            LogEvents.LEARNING_PERIOD_UNUSUAL_HEART_BEAT,
            LogEvents.LEARNING_PERIOD_DURATION,
            LogEvents.LEARNING_PERIOD_IMPROVING
    };
    private static LinearLayout.LayoutParams sLayoutParams = new LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
    );

    private StatusProgressBar mProgressbar;
    private FrameLayout mCircleContainer;
    private ShTextView mItemTitle;
    private ImageView mIcon;
    private int mIndex = -1;
    private boolean mClicked = false;
    private ValueAnimator mCurrentAnim;
    private ValueAnimator.AnimatorUpdateListener mRingUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float currentValue = (float) animation.getAnimatedValue();
            mProgressbar.setProgWidth(currentValue);
        }
    };

    public FlowerItem(Context context) {
        super(context);
        init(null);
    }

    public FlowerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FlowerItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public FlowerItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(sLayoutParams);
        setGravity(Gravity.CENTER_HORIZONTAL);

        Resources res = getContext().getResources();
        int circleR = res.getDimensionPixelSize(R.dimen.circle_sizeSmallR);
        int circleWidth = res.getDimensionPixelSize(R.dimen.statusProgressbar_width_small);
        int iconSize = res.getDimensionPixelSize(R.dimen.flowerItem_icon_size);
        LinearLayout.LayoutParams centerLayoutParams = new LinearLayout.LayoutParams(
                2 * circleR, 2 * circleR
        );
        mCircleContainer = new FrameLayout(getContext());
        mCircleContainer.setLayoutParams(centerLayoutParams);
        addView(mCircleContainer);

        mItemTitle = new ShTextView(getContext());
        mItemTitle.setTextColor(Color.WHITE);
        mItemTitle.setTypeface(Typeface.DEFAULT_BOLD);
        mItemTitle.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        mItemTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.textS2));

        mItemTitle.setTextAppearance(getContext(), R.style.HeaderOne_4);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
        textLayoutParams.setMargins(0, res.getDimensionPixelSize(R.dimen.padding), 0, 0);
        mItemTitle.setLayoutParams(textLayoutParams);
        addView(mItemTitle);

        mProgressbar = new StatusProgressBar(getContext());
        mProgressbar.setProgressR(circleR);
        mProgressbar.setProgWidth(circleWidth);
        mProgressbar.setLayoutParams(centerLayoutParams);
        mProgressbar.setMode(StatusProgressBar.MODE_LEARNING_PERIOD_ICON);
        mCircleContainer.addView(mProgressbar);
        //
        mIcon = new ImageView(getContext());
        mIcon.setLayoutParams(new FrameLayout.LayoutParams(iconSize, iconSize, Gravity.CENTER));
    }

    public void setTitle(int resId) {
        setTitle(getResources().getString(resId));
    }

    public void setTitle(String title) {
        mItemTitle.setText(title);
    }

    public void setIcon(int resId) {
        mIcon.setImageResource(resId);
        mCircleContainer.addView(mIcon);
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
        setTitle(sTitles[index]);
        setIcon(sIcons[index]);
    }

    public void setClicked(boolean clicked) {
        if (clicked) {
            buildAnim(RING_WIDTH);
            mClicked = true;
//			Log.v("FlowerItem", "ProgWidth: " +mProgressbar.getProgWidth());
        } else if (mClicked) {
            buildAnim(0);
            mClicked = false;
        }
    }

    private void buildAnim(float ringWidth) {
        if (mCurrentAnim != null && mCurrentAnim.isRunning()) {
            mCurrentAnim.cancel();
            mCurrentAnim.removeAllUpdateListeners();
        }
        mCurrentAnim = ValueAnimator.ofFloat(mProgressbar.getProgWidth(), ringWidth);
        mCurrentAnim.setDuration(RING_DURATION);
        mCurrentAnim.addUpdateListener(mRingUpdateListener);
        mCurrentAnim.start();
    }
}
