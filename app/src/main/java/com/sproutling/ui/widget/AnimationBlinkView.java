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

public class AnimationBlinkView {

    private ImageView mBlinkView;

    private AnimatorSet mAnimatorSet;

    public AnimationBlinkView(View view) {
        mBlinkView = (ImageView) view.findViewById(R.id.blink);

        init();
    }

    void init() {
        ObjectAnimator fadeOutBot = ObjectAnimator.ofFloat(mBlinkView, "alpha", 1, 0);
        fadeOutBot.setDuration(1500);

        ObjectAnimator fadeInBot = ObjectAnimator.ofFloat(mBlinkView, "alpha", 0, 1);
        fadeInBot.setDuration(1500);

        mAnimatorSet = new AnimatorSet();

        mAnimatorSet.play(fadeOutBot);
        mAnimatorSet.play(fadeInBot).after(fadeOutBot);

        mAnimatorSet.addListener(mAnimatorListener);
    }

    public void start() {
        if (mAnimatorSet.isStarted()) {
            mAnimatorSet.cancel();
        }
        mAnimatorSet.addListener(mAnimatorListener);
        mAnimatorSet.start();
    }

    public void end() {
        mAnimatorSet.end();
    }

    public void cancel() {
        mAnimatorSet.cancel();
    }

    public boolean isStarted() {
        return mAnimatorSet.isStarted();
    }

    private Animator.AnimatorListener mAnimatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mAnimatorSet.start();
        }
    };
}
