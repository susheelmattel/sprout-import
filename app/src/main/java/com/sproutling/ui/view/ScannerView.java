/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sproutling.utils.Utils;

/**
 * Created by bradylin on 2/2/17.
 */

public class ScannerView extends ImageView {

    private static final float DEF_FRAME_WIDTH = 4.0f;
    private static final int DEFAULT_AREA_SIZE = 200;

    private Paint mRectPaint, mCirclePaint;
    private Paint mRoundPaint;
    private RectF mRectF;

    private int mClearAreaSize;

    public ScannerView(Context context) {
        super(context);
        init();
    }

    public ScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setColor(0xB0000000);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCirclePaint.setColor(0xffffffff);
        mRoundPaint = new Paint();
        mRoundPaint.setColor(0xffffffff);
        mRoundPaint.setStyle(Paint.Style.STROKE);
        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setStrokeWidth(DEF_FRAME_WIDTH);
        mClearAreaSize = Utils.dpToPx(DEFAULT_AREA_SIZE);
    }

    public void setClearAreaSize(int size) {
        mClearAreaSize = size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mRectF == null || mRectF.isEmpty()) mRectF = new RectF(0, 0, getWidth(), getHeight());
        int sc = canvas.saveLayer(mRectF, null, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(mRectF, mRectPaint);
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int offset = mClearAreaSize / 2;
        canvas.drawRect(cx - offset, cy - offset, cx + offset, cy + offset, mCirclePaint);
        canvas.restoreToCount(sc);
        canvas.drawRect(cx - offset, cy - offset, cx + offset, cy + offset, mRoundPaint);
    }
}
