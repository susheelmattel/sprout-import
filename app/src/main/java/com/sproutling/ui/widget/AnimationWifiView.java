/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;

import com.sproutling.R;

/**
 * Created by bradylin on 12/14/16.
 */

public class AnimationWifiView {
    private static final int ANIMATION_FADE_IN_DURATION = 300;
    private static final int ANIMATION_FADE_OUT_DURATION = 150;
    private boolean mIsAnimationRunning = false;

    private ImageView mWifiTopView, mWifiMidView, mWifiBottomView;

    private AnimatorSet mAnimatorSet;

    public AnimationWifiView(View view) {
        mWifiTopView = (ImageView) view.findViewById(R.id.wifi_top);
        mWifiMidView = (ImageView) view.findViewById(R.id.wifi_mid);
        mWifiBottomView = (ImageView) view.findViewById(R.id.wifi_bottom);

        init();
    }

    private void init() {
        ObjectAnimator firstFadeOutBot = ObjectAnimator.ofFloat(mWifiBottomView, "alpha", 1, 0);
        ObjectAnimator firstFadeOutMid = ObjectAnimator.ofFloat(mWifiMidView, "alpha", 1, 0);
        ObjectAnimator firstFadeOutTop = ObjectAnimator.ofFloat(mWifiTopView, "alpha", 1, 0);
        firstFadeOutBot.setDuration(ANIMATION_FADE_OUT_DURATION);
        firstFadeOutMid.setDuration(ANIMATION_FADE_OUT_DURATION);
        firstFadeOutTop.setDuration(ANIMATION_FADE_OUT_DURATION);

        ObjectAnimator fadeInBot = ObjectAnimator.ofFloat(mWifiBottomView, "alpha", 0, 1);
        ObjectAnimator fadeInMid = ObjectAnimator.ofFloat(mWifiMidView, "alpha", 0, 1);
        ObjectAnimator fadeInTop = ObjectAnimator.ofFloat(mWifiTopView, "alpha", 0, 1);
        fadeInBot.setDuration(ANIMATION_FADE_IN_DURATION);
        fadeInMid.setDuration(ANIMATION_FADE_IN_DURATION);
        fadeInTop.setDuration(ANIMATION_FADE_IN_DURATION);

        ObjectAnimator fadeOutBot = ObjectAnimator.ofFloat(mWifiBottomView, "alpha", 1, 0);
        ObjectAnimator fadeOutMid = ObjectAnimator.ofFloat(mWifiMidView, "alpha", 1, 0);
        ObjectAnimator fadeOutTop = ObjectAnimator.ofFloat(mWifiTopView, "alpha", 1, 0);
        fadeOutBot.setDuration(ANIMATION_FADE_OUT_DURATION);
        fadeOutMid.setDuration(ANIMATION_FADE_OUT_DURATION);
        fadeOutTop.setDuration(ANIMATION_FADE_OUT_DURATION);

        ObjectAnimator finalFadeInBot = ObjectAnimator.ofFloat(mWifiBottomView, "alpha", 0, 1);
        ObjectAnimator finalFadeInMid = ObjectAnimator.ofFloat(mWifiMidView, "alpha", 0, 1);
        ObjectAnimator finalFadeInTop = ObjectAnimator.ofFloat(mWifiTopView, "alpha", 0, 1);
        finalFadeInBot.setDuration(ANIMATION_FADE_IN_DURATION);
        finalFadeInMid.setDuration(ANIMATION_FADE_IN_DURATION);
        finalFadeInTop.setDuration(ANIMATION_FADE_IN_DURATION);

        mAnimatorSet = new AnimatorSet();

        mAnimatorSet.play(firstFadeOutBot).with(firstFadeOutMid).with(firstFadeOutTop);
        mAnimatorSet.play(fadeInBot).after(firstFadeOutBot);
        mAnimatorSet.play(fadeInMid).after(fadeInBot);
        mAnimatorSet.play(fadeInTop).after(fadeInMid);
        mAnimatorSet.play(fadeOutBot).after(fadeInTop).after(ANIMATION_FADE_OUT_DURATION);
        mAnimatorSet.play(fadeOutMid).after(fadeOutBot);
        mAnimatorSet.play(fadeOutTop).after(fadeOutMid);
        mAnimatorSet.play(finalFadeInBot).with(finalFadeInMid).with(finalFadeInTop).after(fadeOutTop).after(ANIMATION_FADE_OUT_DURATION);

        mAnimatorSet.addListener(mAnimatorListener);
    }

    public void start() {
        mIsAnimationRunning = true;
        mAnimatorSet.start();
    }

    private Animator.AnimatorListener mAnimatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            if (mIsAnimationRunning)
                mAnimatorSet.start();
        }
    };

    public void endAnimation() {
        mIsAnimationRunning = false;
        mAnimatorSet.end();
        mAnimatorSet.cancel();
    }
}
