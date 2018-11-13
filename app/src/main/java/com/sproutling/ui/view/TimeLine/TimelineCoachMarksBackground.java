/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.view.TimeLine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sproutling.R;

/**
 * Created by loren.hung on 2017/3/3.
 */

public class TimelineCoachMarksBackground extends View {

    private Paint mFillPaint;
    private Path mPath;
    private int mCircleX = 0, mCircleY = 0, mCircleR = 0;
    private Context mContext;

    public TimelineCoachMarksBackground(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TimelineCoachMarksBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public TimelineCoachMarksBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mFillPaint = new Paint();
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(Color.BLACK);
        mFillPaint.setAlpha(204);
        mPath = new Path();
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

        canvas.save();
        mPath.addCircle(mCircleX, mCircleY, mCircleR, Path.Direction.CCW);
        canvas.clipPath(mPath, Region.Op.XOR);
        canvas.drawPaint(mFillPaint);
        canvas.restore();
    }


    public void setCircle(int x, int y) {
        mCircleX = x;
        mCircleY = y;
        mCircleR = (int) mContext.getResources().getDimension(R.dimen.margin6);
        invalidate();
    }
}
