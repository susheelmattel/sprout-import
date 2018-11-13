package com.sproutling.ui.widget;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.sproutling.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by subram13 on 10/9/17.
 */

public class BabyLottieFace extends FrameLayout {
    private static final String BOUNCE = "bounce.json";
    private static final String IDLE = "idle.json";
    private static final String LAUGH = "laugh.json";
    private static final String LOOK = "look.json";
    private static final String SLEEPING = "sleeping.json";
    private static final String SLEEPING_SUCKING = "sleepingSucking.json";
    private static final String STIRRING = "stirring.json";
    private static final String TAG = BabyLottieFace.class.getSimpleName();

    private int mMode = -1;
    private LottieAnimationView mLottieAnimationView;
    private FrameLayout mFrameLayout;
    private OnClickListener mOnClickListener;

    private RandomSelector mAwakeRandomSelector = new RandomSelector(new ArrayList<LottieAnimationItem>() {
        {
            add(new LottieAnimationItem(60, IDLE));
            add(new LottieAnimationItem(15, LAUGH));
            add(new LottieAnimationItem(15, LOOK));
            add(new LottieAnimationItem(10, BOUNCE));
        }
    });
    private RandomSelector mAsleepRandomSelector = new RandomSelector(new ArrayList<LottieAnimationItem>() {
        {
            add(new LottieAnimationItem(70, SLEEPING_SUCKING));
            add(new LottieAnimationItem(30, SLEEPING));
        }
    });

    public BabyLottieFace(@NonNull Context context) {
        super(context);
        init();
    }

    public BabyLottieFace(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BabyLottieFace(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BabyLottieFace(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_lottie_baby_face, null);
        mFrameLayout = (FrameLayout) view.findViewById(R.id.root_layout);
//        mLottieAnimationView = (LottieAnimationView) view.findViewById(R.id.animation_view);
//        mLottieAnimationView.setAnimation(getAwakeAnimation().getName());
//        mLottieAnimationView.loop(true);
//        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                Log.d(TAG, "onAnimationStart");
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                Log.d(TAG, "onAnimationEnd");
//                mLottieAnimationView.setAnimation(getAnimationFileName(mMode));
//                mLottieAnimationView.playAnimation();
////                setMode(mMode);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//                Log.d(TAG, "onAnimationRepeat");
//
//            }
//        });

//        mLottieAnimationView.playAnimation();
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        view.setLayoutParams(layoutParams);
        addView(view);
    }

    private void loadLottie(final int mode) {
        Log.d(TAG, "Loading new Lottie Animation..");
        mLottieAnimationView = new LottieAnimationView(getContext());
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mLottieAnimationView.setLayoutParams(layoutParams);

        mLottieAnimationView.loop(false);
        mLottieAnimationView.setAnimation(getAnimationFileName(mode));
        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            private boolean isCancelled = false;

            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "onAnimationEnd");
                if (!isCancelled) {
                    Log.d(TAG, "onAnimationEnd: Recursive loadLottie");
                    loadLottie(mode);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "onAnimationCancel");
                isCancelled = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(TAG, "onAnimationRepeat");
            }
        });
//        mLottieAnimationView.setOnClickListener(mOnClickListener);

        int childViewCount = mFrameLayout.getChildCount();

        mFrameLayout.removeAllViews();
        mFrameLayout.addView(mLottieAnimationView);

//        childViewCount = mFrameLayout.getChildCount();
//        Log.d(TAG, "childViewCount:" + String.valueOf(childViewCount));
//        int currentViewPosition = mFrameLayout.indexOfChild(mLottieAnimationView);
//        Log.d(TAG, "currentViewPosition before removing views:" + String.valueOf(currentViewPosition));
//        if (mFrameLayout.getChildCount() >= 1) {
//            for (int i = 0; i < childViewCount - 1; i++) {
//                mFrameLayout.removeViewAt(i);
//                Log.d(TAG, "Removed child view from position:" + String.valueOf(i));
//            }
//        }
//         currentViewPosition = mFrameLayout.indexOfChild(mLottieAnimationView);
//        Log.d(TAG, "currentViewPosition after removing views:" + String.valueOf(currentViewPosition));
        mLottieAnimationView.playAnimation();
    }

    public void setMode(int mode) {
        if (mode != mMode) {
            Log.d(TAG, "New MODE : " + String.valueOf(mode));
            mMode = mode;
            stopAnimation();
            Log.d(TAG, "setMode -> new load Lottie");
            mFrameLayout.removeAllViews();
            loadLottie(mode);
        }
    }

    private String getAnimationFileName(int mode) {
        String jsonAnimationFile;
        switch (mode) {
            case StatusView.MODE_AWAKE:
                jsonAnimationFile = getAwakeAnimation().getName();
                Log.d(TAG, "setMode -> Mode : Awake");
                break;
            case StatusView.MODE_STIRRING:
                jsonAnimationFile = getStirringAnimation();
                Log.d(TAG, "setMode -> Mode : Stirring" + jsonAnimationFile);
                break;
            case StatusView.MODE_ASLEEP:
                jsonAnimationFile = getAsleepAnimation().getName();
                Log.d(TAG, "setMode -> Mode : Asleep" + jsonAnimationFile);
                break;
            default:
                jsonAnimationFile = getAwakeAnimation().getName();
                Log.d(TAG, "setMode -> Mode : default: " + mMode);
                break;
        }
        Log.d(TAG, "getAnimationFileName -> Mode file :" + jsonAnimationFile);
        return jsonAnimationFile;
    }

    public void stopAnimation() {
        if (mLottieAnimationView != null)
            mLottieAnimationView.cancelAnimation();
    }

    public void startAnimation() {
        if (mLottieAnimationView != null)
            mLottieAnimationView.playAnimation();
    }

    public void setFaceOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    private LottieAnimationItem getAwakeAnimation() {
        return mAwakeRandomSelector.getRandom();
    }

    private String getStirringAnimation() {
        return STIRRING;
    }

    private LottieAnimationItem getAsleepAnimation() {
        return mAsleepRandomSelector.getRandom();
    }

    private class LottieAnimationItem {
        private int mRelativeProb;
        private String mName;


        public LottieAnimationItem(int relativeProb, String name) {
            this.mRelativeProb = relativeProb;
            this.mName = name;
        }

        public int getRelativeProb() {
            return mRelativeProb;
        }

        public void setRelativeProb(int relativeProb) {
            this.mRelativeProb = relativeProb;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            this.mName = name;
        }
    }


    private class RandomSelector {
        List<LottieAnimationItem> mItems = new ArrayList<LottieAnimationItem>();
        Random rand = new Random();
        int totalSum = 0;

        public RandomSelector(List<LottieAnimationItem> items) {
            mItems = items;
            for (LottieAnimationItem item : mItems) {
                totalSum = totalSum + item.mRelativeProb;
            }
        }

        public LottieAnimationItem getRandom() {

            int index = rand.nextInt(totalSum);
            int sum = 0;
            int i = 0;
            while (sum < index) {
                sum = sum + mItems.get(i++).mRelativeProb;
            }
            int pos = Math.max(0, i - 1);
            Log.d(TAG, "getRandom -> position : " + String.valueOf(pos));
            return mItems.get(pos);
//            return mItems.get(0);
        }
    }
}
