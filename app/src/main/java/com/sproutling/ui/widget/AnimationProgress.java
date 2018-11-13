/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sproutling.R;

/**
 * Created by bradylin on 12/8/16.
 */

public class AnimationProgress extends RelativeLayout {
    private static final int ANIMATION_TIME = 300;

    private View mView;

    private ImageView mAsteriskOne, mAsteriskOneCover, mAsteriskTwo, mAsteriskTwoCover,
            mAsteriskThree, mAsteriskThreeCover, mAsteriskFour, mAsteriskFourCover,
            mBulbHead, mBulbHeadCoverLeft, mBulbHeadCoverRight,
            mBulbMid, mBulbMidCover, mBulbLine, mBulbLineCover,
            mBulbBottom, mBulbBottomCover,
            mFaceOutline, mFaceOutlineCoverTop, mFaceOutlineCoverBottom,
            mFaceEyes, mFaceEyesCover, mFaceMouth, mFaceMouthCover;

    private AnimatorSet mAsteriskAnimatorSet, mBulbAnimatorSet, mFaceAnimatorSet, mReverseAnimatorSet;

    private ObjectAnimator a1, a2, a3, a4,
            b1l, b1r, b2, b3, b4,
            f1t, f1b, f2, f3;

    private ObjectAnimator ra1, ra2, ra3, ra4,
            rb1l, rb1r, rb2, rb3, rb4,
            rf1t, rf1b, rf2, rf3;

    private float mDistance = 75;
    private Animator.AnimatorListener mAsteriskAnimatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            hideAsterisks();
            mBulbAnimatorSet.start();
        }
    };
    private Animator.AnimatorListener mBulbAnimatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            hideBulbs();
            mFaceAnimatorSet.start();
        }
    };
    private Animator.AnimatorListener mFaceAnimatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mReverseAnimatorSet.start();
        }
    };
    private Animator.AnimatorListener mReverseAnimatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            showAsterisks();
            showBulbs();
            mAsteriskAnimatorSet.start();
        }
    };

    public AnimationProgress(Context context) {
        super(context);
        initView();
    }

    public AnimationProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AnimationProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public AnimationProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.progressbar_animation, this);
        mAsteriskOne = (ImageView) findViewById(R.id.asterisk_one);
        mAsteriskOneCover = (ImageView) findViewById(R.id.asterisk_one_cover);
        mAsteriskTwo = (ImageView) findViewById(R.id.asterisk_two);
        mAsteriskTwoCover = (ImageView) findViewById(R.id.asterisk_two_cover);
        mAsteriskThree = (ImageView) findViewById(R.id.asterisk_three);
        mAsteriskThreeCover = (ImageView) findViewById(R.id.asterisk_three_cover);
        mAsteriskFour = (ImageView) findViewById(R.id.asterisk_four);
        mAsteriskFourCover = (ImageView) findViewById(R.id.asterisk_four_cover);
        mBulbHead = (ImageView) findViewById(R.id.bulb_head);
        mBulbHeadCoverLeft = (ImageView) findViewById(R.id.bulb_head_cover_left);
        mBulbHeadCoverRight = (ImageView) findViewById(R.id.bulb_head_cover_right);
        mBulbMid = (ImageView) findViewById(R.id.bulb_middle);
        mBulbMidCover = (ImageView) findViewById(R.id.bulb_middle_cover);
        mBulbLine = (ImageView) findViewById(R.id.bulb_line);
        mBulbLineCover = (ImageView) findViewById(R.id.bulb_line_cover);
        mBulbBottom = (ImageView) findViewById(R.id.bulb_bottom);
        mBulbBottomCover = (ImageView) findViewById(R.id.bulb_bottom_cover);
        mFaceOutline = (ImageView) findViewById(R.id.face_outline);
        mFaceOutlineCoverTop = (ImageView) findViewById(R.id.face_outline_cover_top);
        mFaceOutlineCoverBottom = (ImageView) findViewById(R.id.face_outline_cover_bottom);
        mFaceEyes = (ImageView) findViewById(R.id.face_eyes);
        mFaceEyesCover = (ImageView) findViewById(R.id.face_eyes_cover);
        mFaceMouth = (ImageView) findViewById(R.id.face_mouth);
        mFaceMouthCover = (ImageView) findViewById(R.id.face_mouth_cover);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDistance = getMeasuredHeight();
        init();
        initRev();
        start();
    }

    private void init() {
        mAsteriskAnimatorSet = new AnimatorSet();
        mBulbAnimatorSet = new AnimatorSet();
        mFaceAnimatorSet = new AnimatorSet();

        a1 = ObjectAnimator.ofFloat(mAsteriskOneCover, "translationX", 0f, mDistance);
        a2 = ObjectAnimator.ofFloat(mAsteriskTwoCover, "translationY", 0f, mDistance);
        a3 = ObjectAnimator.ofFloat(mAsteriskThreeCover, "translationX", 0f, mDistance);
        a4 = ObjectAnimator.ofFloat(mAsteriskFourCover, "translationX", 0f, -mDistance);

        b1l = ObjectAnimator.ofFloat(mBulbHeadCoverLeft, "translationY", 0f, -mDistance);
        b1r = ObjectAnimator.ofFloat(mBulbHeadCoverRight, "translationY", 15f, mDistance);
        b2 = ObjectAnimator.ofFloat(mBulbMidCover, "translationX", 0f, mDistance);
        b3 = ObjectAnimator.ofFloat(mBulbLineCover, "translationY", 0f, mDistance);
        b4 = ObjectAnimator.ofFloat(mBulbBottomCover, "translationX", 0f, mDistance);

        f1t = ObjectAnimator.ofFloat(mFaceOutlineCoverTop, "translationX", 0f, mDistance);
        f1b = ObjectAnimator.ofFloat(mFaceOutlineCoverBottom, "translationX", -15f, -mDistance);
        f2 = ObjectAnimator.ofFloat(mFaceEyesCover, "translationX", 0f, mDistance);
        f3 = ObjectAnimator.ofFloat(mFaceMouthCover, "translationX", 0f, mDistance);

        a1.setDuration(ANIMATION_TIME);
        a2.setDuration(ANIMATION_TIME);
        a3.setDuration(ANIMATION_TIME);
        a4.setDuration(ANIMATION_TIME);

        b1l.setDuration(ANIMATION_TIME);
        b1r.setDuration(150);
        b2.setDuration(ANIMATION_TIME);
        b3.setDuration(ANIMATION_TIME);
        b4.setDuration(ANIMATION_TIME);

        f1t.setDuration(ANIMATION_TIME);
        f1b.setDuration(150);
        f2.setDuration(ANIMATION_TIME);
        f3.setDuration(ANIMATION_TIME);

        mAsteriskAnimatorSet.play(a1);
        mAsteriskAnimatorSet.play(a2).after(a1);
        mAsteriskAnimatorSet.play(a3).after(a2);
        mAsteriskAnimatorSet.play(a4).after(a3);

        mBulbAnimatorSet.play(b1l);
        mBulbAnimatorSet.play(b1r).after(b1l);
        mBulbAnimatorSet.play(b2).after(b1r);
        mBulbAnimatorSet.play(b3).after(b2);
        mBulbAnimatorSet.play(b4).after(b3);

        mFaceAnimatorSet.play(f1t);
        mFaceAnimatorSet.play(f1b).after(f1t);
        mFaceAnimatorSet.play(f2).after(f1b);
        mFaceAnimatorSet.play(f3).after(f2);

        mAsteriskAnimatorSet.addListener(mAsteriskAnimatorListener);
        mBulbAnimatorSet.addListener(mBulbAnimatorListener);
        mFaceAnimatorSet.addListener(mFaceAnimatorListener);
    }

    private void initRev() {
        mReverseAnimatorSet = new AnimatorSet();

        ra1 = ObjectAnimator.ofFloat(mAsteriskOneCover, "translationX", mDistance, 0f);
        ra2 = ObjectAnimator.ofFloat(mAsteriskTwoCover, "translationY", mDistance, 0f);
        ra3 = ObjectAnimator.ofFloat(mAsteriskThreeCover, "translationX", mDistance, 0f);
        ra4 = ObjectAnimator.ofFloat(mAsteriskFourCover, "translationX", -mDistance, 0f);

        rb1l = ObjectAnimator.ofFloat(mBulbHeadCoverLeft, "translationY", -mDistance, 0f);
        rb1r = ObjectAnimator.ofFloat(mBulbHeadCoverRight, "translationY", mDistance, 0f);
        rb2 = ObjectAnimator.ofFloat(mBulbMidCover, "translationX", mDistance, 0f);
        rb3 = ObjectAnimator.ofFloat(mBulbLineCover, "translationY", mDistance, 0f);
        rb4 = ObjectAnimator.ofFloat(mBulbBottomCover, "translationX", mDistance, 0f);

        rf1t = ObjectAnimator.ofFloat(mFaceOutlineCoverTop, "translationX", mDistance, 0f);
        rf1b = ObjectAnimator.ofFloat(mFaceOutlineCoverBottom, "translationX", -mDistance, 0f);
        rf2 = ObjectAnimator.ofFloat(mFaceEyesCover, "translationX", mDistance, 0f);
        rf3 = ObjectAnimator.ofFloat(mFaceMouthCover, "translationX", mDistance, 0f);

        ra1.setDuration(10);
        ra2.setDuration(10);
        ra3.setDuration(10);
        ra4.setDuration(10);

        rb1l.setDuration(10);
        rb1r.setDuration(10);
        rb2.setDuration(10);
        rb3.setDuration(10);
        rb4.setDuration(10);

        rf1t.setDuration(10);
        rf1b.setDuration(10);
        rf2.setDuration(10);
        rf3.setDuration(10);

        mReverseAnimatorSet.play(ra1).with(ra2).with(ra3).with(ra4)
                .with(rb1l).with(rb1r).with(rb2).with(rb3).with(rb4)
                .with(rf1t).with(rf1b).with(rf2).with(rf3);

        mReverseAnimatorSet.addListener(mReverseAnimatorListener);
    }

    private void showAsterisks() {
        mAsteriskOne.setVisibility(View.VISIBLE);
        mAsteriskOneCover.setVisibility(View.VISIBLE);
        mAsteriskTwo.setVisibility(View.VISIBLE);
        mAsteriskTwoCover.setVisibility(View.VISIBLE);
        mAsteriskThree.setVisibility(View.VISIBLE);
        mAsteriskThreeCover.setVisibility(View.VISIBLE);
        mAsteriskFour.setVisibility(View.VISIBLE);
        mAsteriskFourCover.setVisibility(View.VISIBLE);
    }

    private void hideAsterisks() {
        mAsteriskOne.setVisibility(View.INVISIBLE);
        mAsteriskOneCover.setVisibility(View.INVISIBLE);
        mAsteriskTwo.setVisibility(View.INVISIBLE);
        mAsteriskTwoCover.setVisibility(View.INVISIBLE);
        mAsteriskThree.setVisibility(View.INVISIBLE);
        mAsteriskThreeCover.setVisibility(View.INVISIBLE);
        mAsteriskFour.setVisibility(View.INVISIBLE);
        mAsteriskFourCover.setVisibility(View.INVISIBLE);
    }

    private void showBulbs() {
        mBulbHead.setVisibility(View.VISIBLE);
        mBulbHeadCoverLeft.setVisibility(View.VISIBLE);
        mBulbHeadCoverRight.setVisibility(View.VISIBLE);
        mBulbMid.setVisibility(View.VISIBLE);
        mBulbMidCover.setVisibility(View.VISIBLE);
        mBulbLine.setVisibility(View.VISIBLE);
        mBulbLineCover.setVisibility(View.VISIBLE);
        mBulbBottom.setVisibility(View.VISIBLE);
        mBulbBottomCover.setVisibility(View.VISIBLE);
    }

    private void hideBulbs() {
        mBulbHead.setVisibility(View.INVISIBLE);
        mBulbHeadCoverLeft.setVisibility(View.INVISIBLE);
        mBulbHeadCoverRight.setVisibility(View.INVISIBLE);
        mBulbMid.setVisibility(View.INVISIBLE);
        mBulbMidCover.setVisibility(View.INVISIBLE);
        mBulbLine.setVisibility(View.INVISIBLE);
        mBulbLineCover.setVisibility(View.INVISIBLE);
        mBulbBottom.setVisibility(View.INVISIBLE);
        mBulbBottomCover.setVisibility(View.INVISIBLE);
    }

//    public void setVisibility(int visibility) {
//        mView.setVisibility(visibility);
//    }

    private void showFaces() {
        mFaceOutline.setVisibility(View.VISIBLE);
        mFaceOutlineCoverTop.setVisibility(View.VISIBLE);
        mFaceOutlineCoverBottom.setVisibility(View.VISIBLE);
        mFaceEyes.setVisibility(View.VISIBLE);
        mFaceEyesCover.setVisibility(View.VISIBLE);
        mFaceMouth.setVisibility(View.VISIBLE);
        mFaceMouthCover.setVisibility(View.VISIBLE);
    }

    private void hideFaces() {
        mFaceOutline.setVisibility(View.INVISIBLE);
        mFaceOutlineCoverTop.setVisibility(View.INVISIBLE);
        mFaceOutlineCoverBottom.setVisibility(View.INVISIBLE);
        mFaceEyes.setVisibility(View.INVISIBLE);
        mFaceEyesCover.setVisibility(View.INVISIBLE);
        mFaceMouth.setVisibility(View.INVISIBLE);
        mFaceMouthCover.setVisibility(View.INVISIBLE);
    }

    public void start() {
        mAsteriskAnimatorSet.start();
    }

    public void stop() {
        mAsteriskAnimatorSet.cancel();
        mBulbAnimatorSet.cancel();
        mFaceAnimatorSet.cancel();
        mReverseAnimatorSet.cancel();
    }
}
