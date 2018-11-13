/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sproutling.R;

/**
 * Created by moi0312 on 2017/1/25.
 */

public class FlowerDescView extends FrameLayout {

    private static final long DURATION_UPDATE = 700;

    private static LayoutParams mLayoutParams = new LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    );
    private static LinearLayout.LayoutParams mTextLayoutParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    );

    private LinearLayout mCurrentContainer, mLastContainer, mContainer1, mContainer2;
    private ShTextView mTxtTitle, mTxtDesc, mTxtTitle1, mTxtTitle2, mTxtDesc1, mTxtDesc2;
    private Resources mRes;

    private ValueAnimator mUpdateTitleAndDescAnim;
    private ValueAnimator.AnimatorUpdateListener mOnAnimUpdate = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float currentValue = (float) animation.getAnimatedValue();
            mCurrentContainer.setAlpha(currentValue);
            mLastContainer.setAlpha(1 - currentValue);
        }
    };

    public FlowerDescView(Context context) {
        super(context);
        init(null);
    }

    public FlowerDescView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FlowerDescView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public FlowerDescView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mCurrentContainer = mContainer1 = initLinearLayout();
        mLastContainer = mContainer2 = initLinearLayout();

        mRes = getContext().getResources();
        mTextLayoutParams.setMargins(0, mRes.getDimensionPixelSize(R.dimen.margin), 0, 0);

        mTxtTitle = mTxtTitle1 = initShTextView(mContainer1, true);
        mTxtDesc = mTxtDesc1 = initShTextView(mContainer1, false);
        mTxtTitle2 = initShTextView(mContainer2, true);
        mTxtDesc2 = initShTextView(mContainer2, false);

        mUpdateTitleAndDescAnim = ValueAnimator.ofFloat(0, 1);
        mUpdateTitleAndDescAnim.setDuration(DURATION_UPDATE);
        mUpdateTitleAndDescAnim.addUpdateListener(mOnAnimUpdate);
        mUpdateTitleAndDescAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animDone();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animDone();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
//		setTitleAndDesc(mRes.getString(R.string.flower_about_title), mRes.getString(R.string.flower_about_desc));
    }

    private void animDone() {
        mCurrentContainer.setAlpha(1);
        mLastContainer.setAlpha(0);
    }

    private LinearLayout initLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(mLayoutParams);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(linearLayout);
        return linearLayout;
    }

    private ShTextView initShTextView(ViewGroup parent, boolean isTitle) {
        ShTextView ShTextView = new ShTextView(getContext());
        ShTextView.setLayoutParams(mTextLayoutParams);
        ShTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        ShTextView.setTextColor(Color.WHITE);
        if (isTitle) {
            ShTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRes.getDimensionPixelSize(R.dimen.textNormal));
            ShTextView.setTextAppearance(getContext(), R.style.SubtextTwo_10);
        } else {
            ShTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRes.getDimensionPixelSize(R.dimen.textS2));
            ShTextView.setLineSpacing(mRes.getDimensionPixelSize(R.dimen.padding), 1f);
            ShTextView.setTextAppearance(getContext(), R.style.SubtextTwo_6);
        }
        parent.addView(ShTextView);
        return ShTextView;
    }

    public void setTitleAndDesc(String title, String desc) {
        if (mUpdateTitleAndDescAnim.isRunning()) {
            mUpdateTitleAndDescAnim.cancel();
        }
        if (mCurrentContainer == mContainer2) {
            mCurrentContainer = mContainer1;
            mLastContainer = mContainer2;
            mTxtTitle = mTxtTitle1;
            mTxtDesc = mTxtDesc1;

        } else {
            mCurrentContainer = mContainer2;
            mLastContainer = mContainer1;
            mTxtTitle = mTxtTitle2;
            mTxtDesc = mTxtDesc2;
        }
        mTxtTitle.setText(title);
        mTxtDesc.setText(desc);
        mUpdateTitleAndDescAnim.start();
    }
}
