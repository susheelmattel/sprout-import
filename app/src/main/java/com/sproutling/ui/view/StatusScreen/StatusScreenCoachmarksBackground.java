/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.view.StatusScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sproutling.R;

/**
 * Created by xylonchen on 2017/3/14.
 */

public class StatusScreenCoachmarksBackground extends View {


    private Paint mFillPaint;

    private int mCircleX = 0, mCircleY = 0, mCircleR = 0;
    private int mRectangleLeft = 0, mRectangleTop = 0, mRectangleRight = 0, mRectangleDown = 0;
    private int mCircleTextSpacing, mCircleRSpacing, mCircleTextLeftSpacing, mCircle12Spacing, mCircle612YErrorSpacing;

    private Context mContext;

    public static final int WELCOME_PAGE = 1000;
    public static final int BIG_CIRCLE_PAGE = 1001;
    public static final int SMALL_CIRCLE_PAGE = 1002;
    public static final int RECTANGLE_PAGE = 1003;
    public static final int PROGRESS_PAGE = 1004;

    public static int sToolTipsStatus = WELCOME_PAGE;

    private Paint mPaintMask;
    private Paint mPaint;
    private Paint mPaintWhite;
    private Rect mRectProgRegion;

    public StatusScreenCoachmarksBackground(Context context) {
        super(context);
        mContext = context;
        init();

    }

    public StatusScreenCoachmarksBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public StatusScreenCoachmarksBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mFillPaint = new Paint();
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(Color.BLACK);
        mFillPaint.setAlpha(153);

        mPaintMask = new Paint(Paint.ANTI_ALIAS_FLAG);//mask
        mPaintMask.setColor(-1);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        mPaintWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintWhite.setTextSize((int) mContext.getResources().getDimension(R.dimen.textM));
        mPaintWhite.setColor(Color.WHITE);


        mRectProgRegion = new Rect();

        mCircleTextSpacing = mContext.getResources().getDimensionPixelSize(R.dimen.big_circle_text_spacing);
        mCircleRSpacing = mContext.getResources().getDimensionPixelSize(R.dimen.big_circle_spacing);
        mCircleTextLeftSpacing = mContext.getResources().getDimensionPixelSize(R.dimen.big_circle_text_left_spacing);
        mCircle12Spacing = mContext.getResources().getDimensionPixelSize(R.dimen.big_circle_12_spacing);
        mCircle612YErrorSpacing = mContext.getResources().getDimensionPixelSize(R.dimen.big_circle_6_12_y_error_spacing);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        This way will case UI conflict in some device...
//
//        canvas.save();
//        Path path =new Path();
//        path.addCircle(mCircleX, mCircleY, mCircleR, Path.Direction.CCW);
//        canvas.clipPath(path, Region.Op.XOR);
//        canvas.drawPaint(mFillPaint);
//        canvas.restore();

        if (sToolTipsStatus == WELCOME_PAGE) {
            canvas.drawPaint(mFillPaint);
        } else if (sToolTipsStatus == BIG_CIRCLE_PAGE) {

            getDrawingRect(mRectProgRegion);

            Bitmap tempBitmap = Bitmap.createBitmap(mRectProgRegion.width(), mRectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawPaint(mFillPaint);

            Bitmap maskBitmap = Bitmap.createBitmap(mRectProgRegion.width(), mRectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(maskBitmap);
            maskCanvas.drawCircle(mCircleX, mCircleY, mCircleR, mPaintMask);

            Bitmap result = Bitmap.createBitmap(mRectProgRegion.width(), mRectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas resultCanvas = new Canvas(result);


            resultCanvas.drawBitmap(tempBitmap, 0, 0, null);
            resultCanvas.drawBitmap(maskBitmap, 0, 0, mPaint);

            canvas.drawBitmap(result, 0, 0, new Paint());

            mCircleX = mCircleX - mContext.getResources().getDimensionPixelSize(R.dimen.big_circle_text_left_spacing);

            canvas.drawText("3", mCircleX + mCircleR + mCircleTextSpacing, mCircleY, mPaintWhite);
            canvas.drawText("6", mCircleX, mCircleY + mCircleR + mCircleTextSpacing * 1.2f + mCircle612YErrorSpacing, mPaintWhite);
            canvas.drawText("9", mCircleX - mCircleR - mCircleTextSpacing, mCircleY, mPaintWhite);
            canvas.drawText("12", mCircleX - mCircle12Spacing, mCircleY - mCircleR - mCircleTextSpacing * 1.2f + mCircle612YErrorSpacing, mPaintWhite);
        } else if (sToolTipsStatus == SMALL_CIRCLE_PAGE) {
//            Path path =new Path();
//            path.addCircle(mCircleX, mCircleY, mCircleR, Path.Direction.CCW);
//            canvas.clipPath(path, Region.Op.XOR);
//            canvas.drawPaint(mFillPaint);

            getDrawingRect(mRectProgRegion);

            Bitmap tempBitmap = Bitmap.createBitmap(mRectProgRegion.width(), mRectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawPaint(mFillPaint);

            Bitmap maskBitmap = Bitmap.createBitmap(mRectProgRegion.width(), mRectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(maskBitmap);
            maskCanvas.drawCircle(mCircleX, mCircleY, mCircleR, mPaintMask);

            Bitmap result = Bitmap.createBitmap(mRectProgRegion.width(), mRectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas resultCanvas = new Canvas(result);


            resultCanvas.drawBitmap(tempBitmap, 0, 0, null);
            resultCanvas.drawBitmap(maskBitmap, 0, 0, mPaint);

            canvas.drawBitmap(result, 0, 0, new Paint());
        } else if (sToolTipsStatus == RECTANGLE_PAGE) {

            getDrawingRect(mRectProgRegion);

            Bitmap tempBitmap = Bitmap.createBitmap(mRectProgRegion.width(), mRectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawPaint(mFillPaint);

            Bitmap maskBitmap = Bitmap.createBitmap(mRectProgRegion.width(), mRectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(maskBitmap);

            Drawable shape = mContext.getResources().getDrawable(R.drawable.shape_tooltips_retangle_btn);

            shape.setBounds(mRectangleLeft, mRectangleTop, mRectangleRight, mRectangleDown);
            shape.draw(maskCanvas);

            Bitmap result = Bitmap.createBitmap(mRectProgRegion.width(), mRectProgRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas resultCanvas = new Canvas(result);

            resultCanvas.drawBitmap(tempBitmap, 0, 0, null);
            resultCanvas.drawBitmap(maskBitmap, 0, 0, mPaint);

            canvas.drawBitmap(result, 0, 0, new Paint());

        } else if (sToolTipsStatus == PROGRESS_PAGE) {
            mPaintWhite.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawPaint(mFillPaint);
            canvas.drawText("4PM", mCircleX + mCircleR - mContext.getResources().getDimensionPixelSize(R.dimen.progress_text_left_spacing1), mCircleY - mContext.getResources().getDimensionPixelSize(R.dimen.progress_text_down_spacing), mPaintWhite);
            canvas.drawText("6PM", mCircleX - mContext.getResources().getDimensionPixelSize(R.dimen.progress_text_left_spacing2), mCircleY + mCircleR - mContext.getResources().getDimensionPixelSize(R.dimen.progress_text_down_spacing), mPaintWhite);
        }
    }

    public void setCircle(int x, int y) {
        mCircleX = x;
        mCircleY = y;

        if (sToolTipsStatus == BIG_CIRCLE_PAGE) {
            mCircleR = mContext.getResources().getDimensionPixelSize(R.dimen.circle_sizeNormal) + mCircleRSpacing;
        } else if (sToolTipsStatus == PROGRESS_PAGE) {
            mCircleR = mContext.getResources().getDimensionPixelSize(R.dimen.circle_sizeNormal);
        } else if (sToolTipsStatus == SMALL_CIRCLE_PAGE) {
            mCircleR = (int) mContext.getResources().getDimension(R.dimen.margin3);
        }
        invalidate();
    }

    public void setWelcomePage() {
        sToolTipsStatus = WELCOME_PAGE;
        invalidate();
    }

    public void setBigCirclePage(int x, int y) {
        sToolTipsStatus = BIG_CIRCLE_PAGE;
        setCircle(x, y);
    }

    public void setSmallCirclePage(int x, int y) {
        sToolTipsStatus = SMALL_CIRCLE_PAGE;
        setCircle(x, y);
    }

    public void setRetanglePage(int left, int top, int right, int down) {
        sToolTipsStatus = RECTANGLE_PAGE;
        mRectangleLeft = left;
        mRectangleTop = top;
        mRectangleRight = right;
        mRectangleDown = down;
        invalidate();
    }

    public void setProgressPage(int x, int y) {
        sToolTipsStatus = PROGRESS_PAGE;
        setCircle(x, y);
    }
}
