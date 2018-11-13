/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sproutling.utils.Utils;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created on 2016/12/20.
 */

//public class StatusProgressBar extends ProgressBar{
public class StatusProgressBar extends ProgressBar {
    public static final String TAG = StatusProgressBar.class.getSimpleName();

    public static final int MODE_NORMAL = 0;
    public static final int MODE_ROUND = 900101;
    public static final int MODE_ROUND2 = 900102;
    public static final int MODE_GAP = 900201;
    //    public static final int MODE_HEART = 900301;
    public static final int MODE_LEARNING_PERIOD = 900801;
    public static final int MODE_LEARNING_PERIOD_ICON = -1;

    private int mMode = MODE_NORMAL;
    private int mProgress, mSecondaryProgress;

    private float mStartAngle = -90; //Set the starting point angle  0~360

    private Paint mPaintMain, mPaintSub, mPaintBg;
    private int mColorMain, mColorSub, mColorBg;
    private int mProgressR = 400;
    private float mProgWidth = 32;
    private int mGapCount = 60;
    private ImageView mIcon;
    private float mProgressPointX, mProgressPointY;

    private OnProgressPointChangeListener mOnProgressPointChangeListener;
    private OnCenterPChangeListener mOnCenterPChangeListener;
    private Point mCenterP;

    public StatusProgressBar(Context context) {
        super(context);
        init();
    }

    public StatusProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatusProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setStartAngle(mStartAngle);
        setMax(360);

        mPaintMain = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSub = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBg = new Paint(Paint.ANTI_ALIAS_FLAG);

        setColorMain(0xFFFFFFFF);
        setColorSub(0x99FFFFFF);
        setColorBg(0x33FFFFFF);

        setMode(mMode);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        mProgress = progress % getMax();
        invalidate();
    }

    @Override
    public synchronized void setSecondaryProgress(int secondaryProgress) {
        super.setSecondaryProgress(secondaryProgress);
        mSecondaryProgress = secondaryProgress;
        invalidate();
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
        if (mode == MODE_ROUND) {
//            setColorMain(0x01FFFFFF);
        } else if (mode == MODE_LEARNING_PERIOD_ICON) {
            setColorMain(0xFFFFFFFF);
            mPaintMain.setColor(mColorMain);
            mProgWidth = 0;
            mProgress = 360;
        } else {
            mSecondaryProgress = 0;
        }
        invalidate();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        float pointStartAngle = mStartAngle * -1;//Calculate the corresponding position
        float maxProgress = (float) getMax();

        if (mProgress >= maxProgress && mMode != MODE_LEARNING_PERIOD_ICON) {
            mProgress -= maxProgress;
        }

        float progressAngle = mProgress * 360 / maxProgress;
        float progressAngle2 = mSecondaryProgress * 360 / maxProgress;
        float progressPointAngle = pointStartAngle - progressAngle; //for draw point or icon

        Rect rectProgRegion = new Rect();
        getDrawingRect(rectProgRegion);
        mCenterP = new Point(rectProgRegion.centerX(), rectProgRegion.centerY());

        if (mOnCenterPChangeListener != null) {
            mOnCenterPChangeListener.onCenterPChange(mCenterP);
            mOnCenterPChangeListener = null;
        }

        float innerR = mProgressR - mProgWidth;
        float pointR = mProgressR - mProgWidth / 2;

//        Log.v("gggg", "mProgress: "+mProgress + ", progressAngle: "+progressAngle);

        float[] progressPoint = Utils.getPointByAngle(mCenterP.x, mCenterP.y, pointR, mStartAngle, progressAngle);
        if (mOnProgressPointChangeListener != null) {
            mOnProgressPointChangeListener.onProgressPointChange(progressPoint[0], progressPoint[1]);
        }

        RectF progRect = new RectF(mCenterP.x - mProgressR, mCenterP.y - mProgressR, mCenterP.x + mProgressR, mCenterP.y + mProgressR);
        Bitmap tempBitmap = Bitmap.createBitmap(rectProgRegion.width(), rectProgRegion.height(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);

        if (mColorBg != 0x00000000) {
            if (mMode == MODE_ROUND2 || mMode == MODE_LEARNING_PERIOD_ICON) {
                canvas.drawCircle(mCenterP.x, mCenterP.y, mProgressR, mPaintBg);
            } else {
                tempCanvas.drawCircle(mCenterP.x, mCenterP.y, mProgressR, mPaintBg);
            }
        }
        if (mMode == MODE_ROUND || mMode == MODE_ROUND2 || mMode == MODE_LEARNING_PERIOD_ICON) {
            //draw progresses
            tempCanvas.drawArc(progRect, mStartAngle, progressAngle2, true, mPaintSub);
            tempCanvas.drawArc(progRect, mStartAngle, progressAngle, true, mPaintMain);
        }
        if (mMode == MODE_ROUND || mMode == MODE_ROUND2) {
            //arc start
            drawArc(tempCanvas, mPaintMain, mCenterP, pointR, pointStartAngle, (int) (mProgWidth / 2), mStartAngle - 180, 180);
            drawArc(tempCanvas, mPaintSub, mCenterP, pointR, pointStartAngle, (int) (mProgWidth / 2), mStartAngle - 180, 180);
            //pregress arc main
            drawArc(tempCanvas, mPaintMain, mCenterP, pointR, progressPointAngle, (int) (mProgWidth / 2), mStartAngle + progressAngle, 180);
            //pregress arc sub
            drawArc(tempCanvas, mPaintSub, mCenterP, pointR, pointStartAngle - progressAngle2, (int) (mProgWidth / 2), mStartAngle + progressAngle2, 180);
        }
        //mask
        if (mProgWidth > 0 || mMode == MODE_LEARNING_PERIOD_ICON) {
            Bitmap maskBitmap = Bitmap.createBitmap(rectProgRegion.width(), rectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(maskBitmap);
            Paint paintMask = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintMask.setColor(-1); //mask must be white
            //mask circle
            maskCanvas.drawCircle(mCenterP.x, mCenterP.y, innerR, paintMask);
            //mask gap
            if (mMode == MODE_GAP) {
                float gapAngle = 180 / mGapCount;
                float gapWidth = 2 * gapAngle;
                RectF gapRect = new RectF(mCenterP.x - gapWidth, mCenterP.y - mProgressR, mCenterP.x + gapWidth, mCenterP.y + mProgressR);
                for (int i = 0; i < mGapCount; i++) {
                    maskCanvas.drawRect(gapRect, paintMask);
                    maskCanvas.rotate(gapAngle, mCenterP.x, mCenterP.y);
                }
                //ready for round gap
//                float gapAngle= 360/mGapCount;
//                float gapWidth = gapAngle;
//                RectF gapRect = new RectF(mCenterP.x-gapWidth, mCenterP.y-mProgressR, mCenterP.x+gapWidth, mCenterP.y-mProgressR+mProgWidth);
//                for(int i=0; i < mGapCount; i++){
//                    maskCanvas.drawRoundRect(gapRect, mProgWidth/2, mProgWidth/2, paintMask);
//                    maskCanvas.rotate(gapAngle, mCenterP.x, mCenterP.y);
//                }
            }

            Bitmap result = Bitmap.createBitmap(rectProgRegion.width(), rectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas resultCanvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT)); //mask
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN)); //mask
            resultCanvas.drawBitmap(tempBitmap, 0, 0, null);
            resultCanvas.drawBitmap(maskBitmap, 0, 0, paint);
            paint.setXfermode(null);

            //Draw result after performing masking
            canvas.drawBitmap(result, 0, 0, new Paint());
        } else {
            canvas.drawBitmap(tempBitmap, 0, 0, null);
        }
        //recycle
//        tempBitmap.recycle();
//        maskBitmap.recycle();
    }

    private void drawArc(Canvas canvas, Paint paint, Point center, float radius, float angle, int arcRadius, float arcAngleStart, float arcAngleDraw) {
        double pointRadians = Math.toRadians(angle);
        float startPointX = center.x + (float) (radius * cos(pointRadians));
        float startPointY = center.y - (float) (radius * sin(pointRadians));
        canvas.drawArc(
                new RectF(startPointX - arcRadius, startPointY - arcRadius,
                        startPointX + arcRadius, startPointY + arcRadius),
                arcAngleStart, arcAngleDraw, true, paint
        );
    }

    public void animateProgress(int targetProgressMain, int targetProgressSub, long duration, long startDelay) {
        ValueAnimator anim = ValueAnimator.ofInt(mProgress, targetProgressMain);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                setProgress(progress);
            }
        });
        ValueAnimator anim2 = ValueAnimator.ofInt(mSecondaryProgress, targetProgressSub);
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                setSecondaryProgress(progress);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim2.setInterpolator(new DecelerateInterpolator());

        anim.setDuration(duration);
        anim2.setDuration(duration);
        if (startDelay > 0) {
            anim.setStartDelay(startDelay);
            anim2.setStartDelay(startDelay);
        }
        anim.start();
        anim2.start();
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
        invalidate();
    }

    public Point getCenterP() {
        invalidate();
        return mCenterP;
    }

    public Point getCenterPP() {
        Rect rectProgRegion = new Rect();
        getDrawingRect(rectProgRegion);
        mCenterP = new Point(rectProgRegion.centerX(), rectProgRegion.centerY());
        return mCenterP;
    }

    public float getProgressPointX() {
        return mProgressPointX;
    }

    public float getProgressPointY() {
        return mProgressPointY;
    }

    public int getGapCount() {
        return mGapCount;
    }

    public void setGapCount(int gapCount) {
        mGapCount = gapCount;
    }

    public float getProgWidth() {
        return mProgWidth;
    }

    public void setProgWidth(float progWidth) {
        mProgWidth = progWidth;
        invalidate();
    }

    public int getProgressR() {
        return mProgressR;
    }

    public void setProgressR(int progressR) {
        mProgressR = progressR;
    }

    public int getColorMain() {
        return mColorMain;
    }

    public void setColorMain(int colorMain) {
        mColorMain = colorMain;
        mPaintMain.setColor(mColorMain);
    }

    public int getColorSub() {
        return mColorSub;
    }

    public void setColorSub(int colorSub) {
        mColorSub = colorSub;
        mPaintSub.setColor(mColorSub);
    }

    public int getColorBg() {
        return mColorBg;
    }

    public void setColorBg(int colorBg) {
        mColorBg = colorBg;
        mPaintBg.setColor(mColorBg);
    }

    public void setOnProgressPointChangeListener(OnProgressPointChangeListener onProgressPointChangeListener) {
        mOnProgressPointChangeListener = onProgressPointChangeListener;
    }

    public static abstract class OnProgressPointChangeListener {
        abstract public void onProgressPointChange(float x, float y);
    }

    public void setOnCenterPChangeListener(OnCenterPChangeListener onCenterPChangeListener) {
        mOnCenterPChangeListener = onCenterPChangeListener;
    }

    public abstract static class OnCenterPChangeListener {
        abstract public void onCenterPChange(Point c);
    }
}
