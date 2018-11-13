/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fuhu.states.payloads.Payload;
import com.sproutling.R;
import com.sproutling.states.Actions;
import com.sproutling.ui.activity.StatusActivity;
import com.sproutling.ui.view.StatusProgressBar;
import com.sproutling.ui.view.StatusProgressBar.OnProgressPointChangeListener;
import com.sproutling.utils.Compatible;

import java.util.Calendar;

import static com.sproutling.ui.activity.StatusActivity.SCENE_LEARNING_PERIOD;
import static com.sproutling.ui.activity.StatusActivity.SCENE_STATUS;

/**
 * Created by moi0312 on 2017/1/3.
 */

public class StatusView extends FrameLayout {

    public static final int MODE_LEARNING_PERIOD = 0;
    public static final int MODE_LEARNING_PERIOD_INFO = 9;
    public static final int MODE_AWAKE = 10;
    public static final int MODE_STIRRING = 20;
    public static final int MODE_ASLEEP = 30;
    public static final int MODE_HEART_RATE = 40;
    public static final int MODE_UNUSUAL_HEARTBEAT = 50;
    public static final int MODE_ROLLOVER = 60;
    public static final int MODE_TOOLTIP = 70;

    private static final long DURATION_HEART_RATE = 300;
    private static final long DURATION_UNUSUAL_HEARTBEAT_CENTER = 1000;
    private static final long DURATION_UNUSUAL_HEARTBEAT = 200;
    private static final long DURATION_LEARNING_PERIOD_INFO = 500;
    private static final long DURATION_RIPPLE = 500;
    private static final long DURATION_RIPPLE_DELAY = 300;

    private static final float LEARNING_PERIOD_SCALE = 0.6f;

    private static LayoutParams sGeneralLayoutParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.CENTER);
    private static LayoutParams sCenterIconLayoutParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private int mMode = MODE_LEARNING_PERIOD;

    private StatusProgressBar mProgressbar;
    private View mCenterIcon;
    private int mCircleInsideR;
    private boolean mDuringAnim = false;

    private BabyLottieFace mBabyFace;
    private ImageView mProgressIcon, mHeartIcon, mRolledOverIcon, mRippleImg1, mRippleImg2;

    private ValueAnimator mCenterIconAnim, mIconAnim, mLPInfoAnimator, mRippleAnimator1, mRippleAnimator2;
    private ValueAnimator.AnimatorUpdateListener mCenterIconAnimUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float currentValue = (float) animation.getAnimatedValue();
            mCenterIcon.setAlpha(currentValue);
        }
    };
    private ValueAnimator.AnimatorUpdateListener mIconAnimUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float currentValue = (float) animation.getAnimatedValue();
            setViewScale(mProgressIcon, currentValue);
        }
    };
    private OnProgressPointChangeListener mOnProgressPointChangeListener;
    private int mSleepPredictionStartAngle;
    private int mSleepPredictionPredictAngle;

    public StatusView(Context context) {
        super(context);
        init(null);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Resources res = getResources();
        int circleR = res.getDimensionPixelSize(R.dimen.circle_sizeNormal);
        int circleWidth = res.getDimensionPixelOffset(R.dimen.statusProgressbar_width);
        mCircleInsideR = circleR - circleWidth;

        mProgressbar = new StatusProgressBar(getContext());
        mProgressbar.setProgressR(circleR);
        mProgressbar.setProgWidth(circleWidth);
        mProgressbar.setLayoutParams(sGeneralLayoutParams);
        addView(mProgressbar);

        mIconAnim = ValueAnimator.ofFloat(0.9f, 1);
        mIconAnim.setDuration(DURATION_HEART_RATE);
        mIconAnim.setRepeatCount(-1);
        mIconAnim.setRepeatMode(ValueAnimator.REVERSE);
        mIconAnim.setInterpolator(new DecelerateInterpolator());
        mIconAnim.addUpdateListener(mIconAnimUpdateListener);

        mCenterIconAnim = ValueAnimator.ofFloat(0.5f, 1);
        mCenterIconAnim.setDuration(DURATION_UNUSUAL_HEARTBEAT_CENTER);
        mCenterIconAnim.setRepeatCount(-1);
        mCenterIconAnim.setRepeatMode(ValueAnimator.REVERSE);
        mCenterIconAnim.setInterpolator(new DecelerateInterpolator());
        mCenterIconAnim.addUpdateListener(mCenterIconAnimUpdateListener);

        mOnProgressPointChangeListener = new OnProgressPointChangeListener() {
            @Override
            public void onProgressPointChange(float x, float y) {
                setIconPosition(x, y);
            }
        };

        sCenterIconLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mHeartIcon = new ImageView(getContext());
        mHeartIcon.setImageResource(R.drawable.ic_unusual_heartbeat);
        mHeartIcon.setLayoutParams(sCenterIconLayoutParams);
        mRolledOverIcon = new ImageView(getContext());
        mRolledOverIcon.setImageResource(R.drawable.ic_rollover);
        mRolledOverIcon.setLayoutParams(sCenterIconLayoutParams);

        mBabyFace = new BabyLottieFace(getContext());
        setCenterIcon(mBabyFace);
        ImageView progressIcon = new ImageView(getContext());

        progressIcon.setImageBitmap(getGrayMode(getBitmap(getContext(), R.drawable.ic_sc_heart)));
        setProgressIcon(progressIcon);
        initLearningPeriodAnimation();
        reset();
    }

    private static Bitmap getBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            if (vectorDrawable == null) return null;
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }

    public static Bitmap getGrayMode(Bitmap bitmap) {
        if (bitmap == null) return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap iconBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(iconBitmap);

        Paint paint = new Paint();
        Paint paint2 = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(Color.argb(0x33, 0x00, 0x00, 0x00), PorterDuff.Mode.SRC_IN));
        paint.setMaskFilter(new BlurMaskFilter(100, BlurMaskFilter.Blur.SOLID));

        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        return iconBitmap;
    }


    private ValueAnimator.AnimatorUpdateListener mRippleAnimatorListener1 = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float currentValue = (float) animation.getAnimatedValue();
            setViewScale(mRippleImg1, currentValue);
        }
    };

    private ValueAnimator.AnimatorUpdateListener mRippleAnimatorListener2 = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float currentValue = (float) animation.getAnimatedValue();
            setViewScale(mRippleImg2, currentValue);
        }
    };

    private void setViewScale(View view, float scale) {
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    public void initLearningPeriodAnimation() {
        Bitmap bitmap = Bitmap.createBitmap(2 * mCircleInsideR, 2 * mCircleInsideR, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Compatible.getColor(getContext(), R.color.white_A50));
        canvas.drawCircle(mCircleInsideR, mCircleInsideR, mCircleInsideR, paint);
        mRippleImg1 = new ImageView(getContext());
        mRippleImg2 = new ImageView(getContext());
        mRippleImg1.setLayoutParams(sCenterIconLayoutParams);
        mRippleImg2.setLayoutParams(sCenterIconLayoutParams);
        mRippleImg1.setImageBitmap(bitmap);
        mRippleImg2.setImageBitmap(bitmap);
        resetRippleImg(mRippleImg1);
        resetRippleImg(mRippleImg2);
        addView(mRippleImg2, 0);
        addView(mRippleImg1, 0);

        mRippleAnimator1 = ValueAnimator.ofFloat(0.6f, 1);
        mRippleAnimator2 = ValueAnimator.ofFloat(0.6f, 1);
        mRippleAnimator1.setDuration(DURATION_RIPPLE);
        mRippleAnimator2.setDuration(DURATION_RIPPLE);
        mRippleAnimator2.setStartDelay(DURATION_RIPPLE_DELAY);
        mRippleAnimator1.addUpdateListener(mRippleAnimatorListener1);
        mRippleAnimator2.addUpdateListener(mRippleAnimatorListener2);
        mRippleAnimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setViewScale(mRippleImg2, 0.6f);
                mRippleImg1.setVisibility(View.VISIBLE);
                mRippleImg2.setVisibility(View.VISIBLE);
                mRippleImg1.setAlpha(1.0f);
                mRippleImg2.setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                resetRippleImg(mRippleImg1);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mRippleAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetRippleImg(mRippleImg1);
                resetRippleImg(mRippleImg2);
                Context context = getContext();
                if (context instanceof StatusActivity) {
                    ((StatusActivity) context).disPatchAction(Actions.SWITCH_SCENE, new Payload().put(Actions.Key.SCENE, SCENE_LEARNING_PERIOD));
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                resetRippleImg(mRippleImg2);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void resetRippleImg(ImageView rippleImg) {
        setViewScale(rippleImg, 0);
        rippleImg.setAlpha(0f);
        rippleImg.setVisibility(View.GONE);
        mDuringAnim = false;
    }

    public void learningPeriodInfoAnim(final boolean isIn) {
        if (isIn) {
            if (!mDuringAnim) {
                mDuringAnim = true;
                mRippleAnimator1.start();
                mRippleAnimator2.start();
            }
        } else {
            learningPeriodInfo(false);
        }
    }

    public void learningPeriodInfo(final boolean isIn) {
        if (!mDuringAnim) {
            mDuringAnim = true;

            mLPInfoAnimator = ValueAnimator.ofFloat(0, 1);
            mLPInfoAnimator.setDuration(DURATION_LEARNING_PERIOD_INFO);
            mLPInfoAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (float) animation.getAnimatedValue();

                    if (isIn) {
                        mProgressbar.setAlpha(1 - currentValue);
                        mRippleImg1.setAlpha(1 - currentValue);
                        mRippleImg2.setAlpha(1 - currentValue);
                        if (mProgressIcon != null) mProgressIcon.setAlpha(1 - currentValue);
                        float scale = 1 - (1 - LEARNING_PERIOD_SCALE) * currentValue;
                        setViewScale(mBabyFace, scale);
                    } else {
                        mProgressbar.setAlpha(currentValue);
                        if (mProgressIcon != null) mProgressIcon.setAlpha(currentValue);
                        float scale2 = LEARNING_PERIOD_SCALE + (1 - LEARNING_PERIOD_SCALE) * currentValue;
                        setViewScale(mBabyFace, scale2);
                    }
                }
            });
            mLPInfoAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mProgressIcon != null) mProgressIcon.setVisibility(View.VISIBLE);
                    mProgressbar.setVisibility(View.VISIBLE);

                    mRippleImg1.setVisibility(View.VISIBLE);
                    mRippleImg2.setVisibility(View.VISIBLE);
                    mRippleImg1.setAlpha(1.0f);
                    mRippleImg2.setAlpha(1.0f);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isIn) {
                        Context context = getContext();
                        if (context instanceof StatusActivity) {
                            ((StatusActivity) context).disPatchAction(Actions.SWITCH_SCENE, new Payload().put(Actions.Key.SCENE, SCENE_STATUS));
                        }
                    } else {
                        if (mProgressIcon != null) mProgressIcon.setVisibility(View.GONE);
                        mProgressbar.setVisibility(View.GONE);
                    }
                    mProgressbar.setAlpha(0f);
                    mRippleImg1.setAlpha(0f);
                    mRippleImg2.setAlpha(0f);
                    if (mProgressIcon != null) mProgressIcon.setAlpha(0f);

                    mDuringAnim = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    resetRippleImg(mRippleImg2);
                    mDuringAnim = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            mLPInfoAnimator.start();
        }
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        if (mMode == mode) {
            return;
        }
        mMode = mode;
        reset();
        switch (mMode) {

            case MODE_LEARNING_PERIOD_INFO:
            case MODE_LEARNING_PERIOD:
                mBabyFace.setMode(MODE_LEARNING_PERIOD);
                mProgressbar.setProgress(0);
                if (mCenterIcon != mBabyFace) {
                    setCenterIcon(mBabyFace);
                }
                mIconAnim.setDuration(DURATION_HEART_RATE);
                mIconAnim.start();
                break;
            case MODE_AWAKE:
                mBabyFace.setMode(MODE_AWAKE);
                if (mCenterIcon != mBabyFace) {
                    setCenterIcon(mBabyFace);
                }
                mIconAnim.setDuration(DURATION_HEART_RATE);
                mIconAnim.start();
                break;
            case MODE_STIRRING:
                mBabyFace.setMode(MODE_STIRRING);
                if (mCenterIcon != mBabyFace) {
                    setCenterIcon(mBabyFace);
                }
                mIconAnim.setDuration(DURATION_HEART_RATE);
                mIconAnim.start();
                break;
            case MODE_ASLEEP:
                mBabyFace.setMode(MODE_ASLEEP);
                if (mCenterIcon != mBabyFace) {
                    setCenterIcon(mBabyFace);
                }
                mIconAnim.setDuration(DURATION_HEART_RATE);
                mIconAnim.start();
                break;
            case MODE_UNUSUAL_HEARTBEAT:
                if (mCenterIcon != mHeartIcon) {
                    setCenterIcon(mHeartIcon);
                }
                mCenterIconAnim.setDuration(DURATION_UNUSUAL_HEARTBEAT_CENTER);
                mCenterIconAnim.start();
                mIconAnim.setDuration(DURATION_UNUSUAL_HEARTBEAT);
                mIconAnim.start();
                break;
            case MODE_HEART_RATE:
                if (mCenterIcon != mHeartIcon) {
                    setCenterIcon(mHeartIcon);
                }
                mIconAnim.setDuration(DURATION_HEART_RATE);
                mIconAnim.start();
                break;
            case MODE_ROLLOVER:
                if (mCenterIcon != mRolledOverIcon) {
                    setCenterIcon(mRolledOverIcon);
                }
                break;
            case MODE_TOOLTIP:
                if (mCenterIcon != null) {
                    removeView(mCenterIcon);
                }
//				mCenterIcon = null;
                //
                mProgressbar.setMode(StatusProgressBar.MODE_ROUND);
                mProgressbar.setColorBg(Color.TRANSPARENT);
                break;
        }
        if (mMode == MODE_LEARNING_PERIOD_INFO) {
            learningPeriodInfo(true);
        }
    }

    private void reset() {
        mIconAnim.end();
        mCenterIconAnim.end();
        mProgressbar.setMode(StatusProgressBar.MODE_NORMAL);
        mProgressbar.setAlpha(1);
    }

    public StatusProgressBar getProgressbar() {
        return mProgressbar;
    }

    public Point getCenterPoint() {
        return mProgressbar.getCenterP();
    }

    public View getCenterIcon() {
        return mCenterIcon;
    }

    public void setCenterIcon(View centerIcon) {
        if (mCenterIcon != null) {
            removeView(mCenterIcon);
        }
        mCenterIcon = centerIcon;
        if (mCenterIcon.getLayoutParams() == null) {
            mCenterIcon.setLayoutParams(sGeneralLayoutParams);
//		} else {
//			((FrameLayout.LayoutParams) mCenterIcon.getLayoutParams()).gravity = Gravity.CENTER;
        }
        addView(centerIcon);
    }

    public void setIconAnimation(boolean play) {
        if (play) {
            mIconAnim.start();
        } else {
            mIconAnim.end();
            setViewScale(mProgressIcon, 1);
        }
    }

    public void setBabyFaceClickListener(OnClickListener listener) {
        if (mBabyFace != null) {
            mBabyFace.setFaceOnClickListener(listener);
        }
    }

    public View getProgressIcon() {
        return mProgressIcon;
    }

    public void setProgressIcon(ImageView icon) {
        mProgressIcon = icon;
        if (mProgressIcon.getLayoutParams() == null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            mProgressIcon.setLayoutParams(layoutParams);
        }
        mProgressbar.setOnProgressPointChangeListener(mOnProgressPointChangeListener);
        addView(mProgressIcon);
    }

    private void setIconPosition(float x, float y) {
        if (mProgressIcon != null) {
            mProgressIcon.setX(x - mProgressIcon.getWidth() / 2);
            mProgressIcon.setY(y - mProgressIcon.getHeight() / 2);
        }
    }

    public static int timeToAngle(Calendar calendar) {
        return timeToAngle(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public static int timeToAngle(int h, int m) {
        int min = h * 60 + m;
        return timeToAngle(min);
    }

    public static int timeToAngle(int m) {
        return (m % 720) / 2;
    }

    public void setIconCurrentPosition(int h, int m) {
        setIconCurrentPosition(timeToAngle(h, m));
    }

    public void setIconCurrentPosition(int angle) {
        if (mProgressbar.getMode() == StatusProgressBar.MODE_ROUND) {
            setSleepPrediction(mSleepPredictionStartAngle, angle, mSleepPredictionPredictAngle);
//			setSleepPrediction(angle, mSleepPredictionPredictAngle);
        } else {
            mProgressbar.setProgress(0);
            setStartTime(angle);
        }
    }

    public void setStartTime(int h, int m) {
        setStartTime(timeToAngle(h, m));
    }

    public void setStartTime(int angle) {
        mProgressbar.setStartAngle(angle - 90);
    }

    public void setSleepPrediction(int nowAngle, int predictAngle) {
        setSleepPrediction(nowAngle, nowAngle, predictAngle);
    }

    public void setSleepPrediction(Calendar start, Calendar now, Calendar predict) {
        setSleepPrediction(timeToAngle(start), timeToAngle(now), timeToAngle(predict));
    }

    public void setSleepPrediction(int startAngle, int nowAngle, int predictAngle) {
        if (predictAngle < startAngle) {
            if (predictAngle < nowAngle) {
                nowAngle -= 360;
            }
            startAngle -= 360;
        }
        if (mSleepPredictionStartAngle != startAngle) {
            mSleepPredictionStartAngle = startAngle;
        }
        mSleepPredictionPredictAngle = predictAngle;
//        Payload payload = (Payload) App.getInstance().getStore().getStateData();
//        boolean showSleepPrediction = States.StatusValue.ASLEEP.equals(payload.getString(States.Key.CURRENT_STATUS));
//        if (showSleepPrediction) {
            mProgressbar.setMode(StatusProgressBar.MODE_ROUND);
            setStartTime(startAngle);
            int progress = (predictAngle - startAngle);
            mProgressbar.setSecondaryProgress(progress);
            mProgressbar.setProgress(nowAngle - startAngle);
//        } else {
//            mProgressbar.setMode(StatusProgressBar.MODE_NORMAL);
//            mProgressbar.setSecondaryProgress(0);
//            setIconCurrentPosition(nowAngle);
//        }
    }

    public void setTooltipsPrediction(int startAngle, int nowAngle, int predictAngle) {
        if (predictAngle < startAngle) {
            predictAngle += 360;
            if (nowAngle < startAngle) {
                nowAngle += 360;
            }
        }
        if (mSleepPredictionStartAngle != startAngle) {
            mSleepPredictionStartAngle = startAngle;
        }
        mSleepPredictionPredictAngle = predictAngle;

        mProgressbar.setMode(StatusProgressBar.MODE_ROUND);
        setStartTime(startAngle);
        int progress = (predictAngle - startAngle);
        mProgressbar.setSecondaryProgress(progress);
        mProgressbar.setProgress(nowAngle - startAngle);
    }

}
