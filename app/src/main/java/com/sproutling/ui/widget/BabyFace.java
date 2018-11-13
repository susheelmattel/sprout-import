/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.sproutling.R;
import com.sproutling.utils.Compatible;

/**
 * Created by moi0312 on 2016/12/29.
 */

public class BabyFace extends FrameLayout {

    private static final long DURATION_ASLEEP = 3000;
    private static final long DURATION_STIRRING = 2500;
    private static final long DURATION_AWAKE = 300;
    private static final long DURATION_AWAKE2 = 3000;
    private static final long DURATION_AWAKE3 = 4000;
    private static final long DURATION_REST = 2000;

    private int mMode = StatusView.MODE_LEARNING_PERIOD;

    private ViewGroup mFace;
    private ImageView mHair;
    private ImageView mEyesAwake, mMouthAwake, mEyesAsleep, mMouthAsleep, mFaceBg;
    private int mZ1MarginL, mZ1MarginT, mZ2MarginL, mZ2MarginT, mHairMarginL,
            mAsleepEyesMarginL, mAsleepEyesMarginT, mAsleepMouthMarginL, mAsleepMouthMarginT,
            mAwakeEyesMarginL, mAwakeEyesMarginT, mAwakeMouthMarginL, mAwakeMouthMarginT;
    private ValueAnimator mRestAnimator, mAsleepAnimator,
            mAwakeAnimator, mAwakeAnimator2, mAwakeAnimator3,
            mStirringAnimator, mStirringAnimator2;
    private ValueAnimator mCurrentAnim;
    private LottieAnimationView mZLottieAnimationView;

    public BabyFace(Context context) {
        super(context);
        init(null);
    }

    public BabyFace(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BabyFace(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BabyFace(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_baby_face, null);
        mFace = (ViewGroup) view.findViewById(R.id.face);
        mHair = (ImageView) view.findViewById(R.id.hair);
        mEyesAwake = (ImageView) view.findViewById(R.id.eyesAwake);
        mMouthAwake = (ImageView) view.findViewById(R.id.mouthAwake);
        mEyesAsleep = (ImageView) view.findViewById(R.id.eyesAsleep);
        mMouthAsleep = (ImageView) view.findViewById(R.id.mouthAsleep);
        mZLottieAnimationView =(LottieAnimationView)view.findViewById(R.id.z_animation_view);
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        view.setLayoutParams(layoutParams);
        addView(view);

        int faceSize = getResources().getDimensionPixelSize(R.dimen.faceBgSize);
        Bitmap faceBitmap = Bitmap.createBitmap(2 * faceSize, 2 * faceSize, Bitmap.Config.ARGB_8888);
        Canvas faceCanvas = new Canvas(faceBitmap);
        Paint facePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        facePaint.setColor(Compatible.getColor(getContext(), R.color.transparent));
        faceCanvas.drawCircle(faceSize, faceSize, faceSize, facePaint);
        mFaceBg = new ImageView(getContext());
        mFaceBg.setLayoutParams(layoutParams);
        mFaceBg.setImageBitmap(faceBitmap);
        addView(mFaceBg, 0);

        Resources res = getResources();
        mZ1MarginL = res.getDimensionPixelOffset(R.dimen.face_z1_marginLeft);
        mZ1MarginT = res.getDimensionPixelOffset(R.dimen.face_z1_marginTop);
        mZ2MarginL = res.getDimensionPixelOffset(R.dimen.face_z2_marginLeft);
        mZ2MarginT = res.getDimensionPixelOffset(R.dimen.face_z2_marginTop);
        mAsleepEyesMarginL = res.getDimensionPixelOffset(R.dimen.face_asleep_eyesMarginLeft);
        mAsleepEyesMarginT = res.getDimensionPixelOffset(R.dimen.face_asleep_eyesMarginTop);
        mAsleepMouthMarginL = res.getDimensionPixelOffset(R.dimen.face_asleep_mouthMarginLeft);
        mAsleepMouthMarginT = res.getDimensionPixelOffset(R.dimen.face_asleep_mouthMarginTop);
        mHairMarginL = res.getDimensionPixelOffset(R.dimen.face_hairMarginLeft);
        mAwakeEyesMarginL = res.getDimensionPixelOffset(R.dimen.face_awake_eyesMarginLeft);
        mAwakeEyesMarginT = res.getDimensionPixelOffset(R.dimen.face_awake_eyesMarginTop);
        mAwakeMouthMarginL = res.getDimensionPixelOffset(R.dimen.face_awake_mouthMarginLeft);
        mAwakeMouthMarginT = res.getDimensionPixelOffset(R.dimen.face_awake_mouthMarginTop);

        initRestAnimator();
        initAwakeAnimator();
        initStirringAnimator();
        initAsleepAnimator();

        resetFace();
        setMode(mMode);
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;

        boolean isSleepFace = true;
        if (mCurrentAnim != null) {
            mCurrentAnim.cancel();
            mCurrentAnim = null;
        }
        switch (mode) {
            case StatusView.MODE_LEARNING_PERIOD:
                isSleepFace = false;
                break;
            case StatusView.MODE_AWAKE:
                isSleepFace = false;
                mCurrentAnim = mAwakeAnimator;
                break;
            case StatusView.MODE_STIRRING:
                mCurrentAnim = mStirringAnimator;
                break;
            case StatusView.MODE_ASLEEP:
                mCurrentAnim = mAsleepAnimator;
                break;
        }
        if (isSleepFace) {
            mEyesAwake.setVisibility(View.GONE);
            mMouthAwake.setVisibility(View.GONE);
            mEyesAsleep.setVisibility(View.VISIBLE);
            mMouthAsleep.setVisibility(View.VISIBLE);
            if (mode == StatusView.MODE_STIRRING) {
                mZLottieAnimationView.setVisibility(View.GONE);
            } else {
                mZLottieAnimationView.setVisibility(View.VISIBLE);
            }
        } else {
            mEyesAwake.setVisibility(View.VISIBLE);
            mMouthAwake.setVisibility(View.VISIBLE);
            mEyesAsleep.setVisibility(View.GONE);
            mMouthAsleep.setVisibility(View.GONE);
            mZLottieAnimationView.setVisibility(View.GONE);
//            mZ1.setVisibility(View.GONE);
//            mZ2.setVisibility(View.GONE);
        }
        if (mCurrentAnim != null) {
            mCurrentAnim.start();
        }
    }

    private void resetFace() {
        mMouthAsleep.setScaleX(1);
        mMouthAsleep.setScaleY(1);
        mEyesAwake.setScaleY(1);

        ViewGroup.MarginLayoutParams eyesAsleepLayoutParams = (ViewGroup.MarginLayoutParams) mEyesAsleep.getLayoutParams();
        eyesAsleepLayoutParams.setMargins(mAsleepEyesMarginL, mAsleepEyesMarginT, 0, 0);
        mEyesAsleep.setLayoutParams(eyesAsleepLayoutParams);

        ViewGroup.MarginLayoutParams mouthAsleepLayoutParams = (ViewGroup.MarginLayoutParams) mMouthAsleep.getLayoutParams();
        mouthAsleepLayoutParams.setMargins(mAsleepMouthMarginL, mAsleepMouthMarginT, 0, 0);
        mMouthAsleep.setLayoutParams(mouthAsleepLayoutParams);

        ViewGroup.MarginLayoutParams eyesAwakeLayoutParams = (ViewGroup.MarginLayoutParams) mEyesAwake.getLayoutParams();
        eyesAwakeLayoutParams.setMargins(mAwakeEyesMarginL, mAsleepEyesMarginT, 0, 0);
        mEyesAwake.setLayoutParams(eyesAwakeLayoutParams);

        ViewGroup.MarginLayoutParams hairLayoutParams = (ViewGroup.MarginLayoutParams) mHair.getLayoutParams();
        hairLayoutParams.setMargins(mHairMarginL, 0, 0, 0);
        mHair.setLayoutParams(hairLayoutParams);
    }

    private void initAsleepAnimator() {
        mAsleepAnimator = ValueAnimator.ofFloat(1, 0.5f, 1);
        mAsleepAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAsleepAnimator.setDuration(DURATION_ASLEEP);
        mAsleepAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                mMouthAsleep.setScaleX(currentValue);
                mMouthAsleep.setScaleY(currentValue);
            }
        });
        mAsleepAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mZLottieAnimationView.loop(true);
                mZLottieAnimationView.playAnimation();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
//                mZAnimator.end();
                mZLottieAnimationView.cancelAnimation();
                resetFace();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void initStirringAnimator() {
        TimeInterpolator interpolator = new BounceInterpolator();
        mStirringAnimator = ValueAnimator.ofFloat(0, 0.9f, 1, 0);
        mStirringAnimator2 = ValueAnimator.ofFloat(0, -0.9f, -1, 0);
        mStirringAnimator.setStartDelay(1000);
        mStirringAnimator2.setStartDelay(1000);
        mStirringAnimator.setInterpolator(interpolator);
        mStirringAnimator2.setInterpolator(interpolator);
        mStirringAnimator.setDuration(DURATION_STIRRING);
        mStirringAnimator2.setDuration(DURATION_STIRRING);
        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                float movement = 12 * currentValue;

                ViewGroup.MarginLayoutParams eyesAsleepLayoutParams = (ViewGroup.MarginLayoutParams) mEyesAsleep.getLayoutParams();
                eyesAsleepLayoutParams.setMargins(
                        (int) (mAsleepEyesMarginL + movement), mAsleepEyesMarginT, 0, 0);
                mEyesAsleep.setLayoutParams(eyesAsleepLayoutParams);

                ViewGroup.MarginLayoutParams mouthAsleepLayoutParams = (ViewGroup.MarginLayoutParams) mMouthAsleep.getLayoutParams();
                mouthAsleepLayoutParams.setMargins(
                        (int) (mAsleepMouthMarginL + movement), mAsleepMouthMarginT, 0, 0);
                mMouthAsleep.setLayoutParams(mouthAsleepLayoutParams);

                ViewGroup.MarginLayoutParams hairLayoutParams = (ViewGroup.MarginLayoutParams) mHair.getLayoutParams();
                hairLayoutParams.setMargins(
                        (int) (mHairMarginL + (-1 * movement)), 0, 0, 0);
                mHair.setLayoutParams(hairLayoutParams);
            }
        };
        mStirringAnimator.addUpdateListener(updateListener);
        mStirringAnimator2.addUpdateListener(updateListener);

        Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                mZ1.setVisibility(View.GONE);
//                mZ2.setVisibility(View.GONE);
                mZLottieAnimationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation == mStirringAnimator) {
                    mStirringAnimator2.start();
                } else {
//					mStirringAnimator.start();
                    mRestAnimator.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mStirringAnimator.end();
                mStirringAnimator2.end();
                if (animation == mStirringAnimator) {
                    resetFace();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
        mStirringAnimator.addListener(animatorListener);
        mStirringAnimator2.addListener(animatorListener);
    }

    private void initRestAnimator() {
        mRestAnimator = ValueAnimator.ofInt(0, 1);
        mRestAnimator.setDuration(DURATION_REST);
        mRestAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCurrentAnim != null && mCurrentAnim != mAwakeAnimator) {
                    mCurrentAnim.start();
                } else {
                    randomAwakeAnim();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void randomAwakeAnim() {
        int randomValue = (int) (Math.random() * 3);

        switch (randomValue) {
            case 0:
                mAwakeAnimator.start();
                break;
            case 1:
                mAwakeAnimator2.start();
                break;
            case 2:
                mAwakeAnimator3.start();
                break;
        }
    }

    private void initAwakeAnimator() {
        mAwakeAnimator = ValueAnimator.ofFloat(1, 0, 1);
//		mAwakeAnimator.setRepeatCount(1);
        mAwakeAnimator.setDuration(DURATION_AWAKE);
        mAwakeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                mEyesAwake.setScaleY(currentValue);
            }
        });
        mAwakeAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRestAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mRestAnimator.cancel();
                mAwakeAnimator2.cancel();
                mAwakeAnimator3.cancel();
                resetFace();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mAwakeAnimator2 = ValueAnimator.ofFloat(0, 1, 1, 1, 0);
        mAwakeAnimator2.setDuration(DURATION_AWAKE2);
        mAwakeAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                float movement = 30 * currentValue;

                ViewGroup.MarginLayoutParams eyesAwakeLayoutParams = (ViewGroup.MarginLayoutParams) mEyesAwake.getLayoutParams();
                eyesAwakeLayoutParams.setMargins(mAwakeEyesMarginL, (int) (mAwakeEyesMarginT - movement), 0, 0);
                mEyesAwake.setLayoutParams(eyesAwakeLayoutParams);

                ViewGroup.MarginLayoutParams hairLayoutParams = (ViewGroup.MarginLayoutParams) mHair.getLayoutParams();
                hairLayoutParams.setMargins(mHairMarginL, (int) (0.3 * movement), 0, 0);
                mHair.setLayoutParams(hairLayoutParams);
            }
        });
        mAwakeAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRestAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mAwakeAnimator3 = ValueAnimator.ofFloat(0, 1, 1, 0, -1, -1, 0);
//		mAwakeAnimator3.setInterpolator(accelerateDecelerateInterpolator);
        mAwakeAnimator3.setDuration(DURATION_AWAKE3);
        mAwakeAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                float movement = 20 * currentValue;

                ViewGroup.MarginLayoutParams eyesAwakeLayoutParams = (ViewGroup.MarginLayoutParams) mEyesAwake.getLayoutParams();
                eyesAwakeLayoutParams.setMargins(
                        (int) (mAwakeEyesMarginL + movement), mAwakeEyesMarginT, 0, 0);
                mEyesAwake.setLayoutParams(eyesAwakeLayoutParams);

                ViewGroup.MarginLayoutParams mouthAwakeLayoutParams = (ViewGroup.MarginLayoutParams) mMouthAwake.getLayoutParams();
                mouthAwakeLayoutParams.setMargins(
                        (int) (mAwakeMouthMarginL + movement * 0.7f), mAwakeMouthMarginT, 0, 0);
                mMouthAwake.setLayoutParams(mouthAwakeLayoutParams);

                ViewGroup.MarginLayoutParams hairLayoutParams = (ViewGroup.MarginLayoutParams) mHair.getLayoutParams();
                hairLayoutParams.setMargins(
                        (int) (mHairMarginL + (-1 * movement)), 0, 0, 0);
                mHair.setLayoutParams(hairLayoutParams);
            }
        });
        mAwakeAnimator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRestAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void setFaceOnClickListener(OnClickListener listener) {
        mFace.setOnClickListener(listener);
    }
}
