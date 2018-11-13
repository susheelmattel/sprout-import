/*
 * Copyright (c) 2017 Fuhu, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.sproutling.R;

/**
 * Created by bradylin on 8/15/17.
 */

public class VideoImageView extends AppCompatImageView {

    private Bitmap mBitmap;

    public VideoImageView(Context context) {
        super(context);
        initialize(context);
    }

    public VideoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public VideoImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.play_button_small);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = (getWidth() - mBitmap.getWidth()) / 2;
        int centerY = (getHeight() - mBitmap.getHeight()) / 2;

        canvas.drawBitmap(mBitmap, centerX, centerY, null);
    }
}

