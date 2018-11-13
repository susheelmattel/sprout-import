/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.view;
/**
 * Created by bradylin on 12/2/16.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.sproutling.R;

public class AvatarView extends android.support.v7.widget.AppCompatImageView {

    private static final float DEF_FRAME_WIDTH = 4.0f;
    private static final float DEF_CLICK_SIZE = 1 / 3f;
    private static final float MAX_SCALE = 5.0f;
    private static final float MIN_SCALE = 0.3f;
    private static final int TEXT_SIZE = 18;

    private boolean isTouCutBitmap = false;

    private Matrix mMatrix;
    private Matrix mSaveMatrix;
    private Bitmap mImgBitmap;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    private int mCurMode = NONE;

    private float mCircleRadius;
    private Paint mRectPaint, mCirclePaint;
    private Paint mRoundPaint;
    private Paint mTextPaint;
    private RectF mRectF;

    private int mWidth;
    private int mHeight;
    private int mImageSize;

    private PointF mPre = new PointF();
    private PointF mMid = new PointF();
    private float dist = 1f;

    private float[] mMXValues = new float[9];

    public AvatarView(Context context) {
        super(context);
        init();
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMatrix = new Matrix();
        mSaveMatrix = new Matrix();
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
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TEXT_SIZE, getResources().getDisplayMetrics()));
        mTextPaint.setStyle(Paint.Style.STROKE);
    }

    public void setImageSize(int size) {
        mImageSize = size;
    }

    public Bitmap clip() {
        return clip(false);
    }

    public Bitmap clip(boolean isCircle) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        invalidate();
        isTouCutBitmap = true;
        setDrawingCacheEnabled(true);
        Bitmap bitmap = getDrawingCache().copy(getDrawingCache().getConfig(), false);
        setDrawingCacheEnabled(false);
        Bitmap head = Bitmap.createBitmap((int) mCircleRadius * 2, (int) mCircleRadius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(head);
        if (isCircle) {
            canvas.drawRoundRect(new RectF(0, 0, 2 * mCircleRadius, 2 * mCircleRadius), mCircleRadius, mCircleRadius, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }
        RectF dst = new RectF(-bitmap.getWidth() / 2 + mCircleRadius, -getHeight() / 2 + mCircleRadius, bitmap.getWidth() - bitmap.getWidth() / 2 + mCircleRadius, getHeight() - getHeight() / 2 + mCircleRadius);
        canvas.drawBitmap(bitmap, null, dst, paint);
        isTouCutBitmap = false;

        return resizeBitmap(head, mImageSize, mImageSize);
    }

    public Bitmap resizeBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void comSize(int width, int height) {
        if (width == 0 || height == 0) return;
        if (width < height) {
            mCircleRadius = width * DEF_CLICK_SIZE;
        } else {
            mCircleRadius = height * DEF_CLICK_SIZE;
        }

        BitmapDrawable bd = (BitmapDrawable) getDrawable();
        if (bd != null) {
            mImgBitmap = bd.getBitmap();
        }
        setScaleType(ScaleType.MATRIX);
        setImageBitmap(mImgBitmap);

        center(true, true);
        setImageMatrix(mMatrix);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (mImgBitmap != null
                && (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST)) {
            width = mImgBitmap.getWidth() + getPaddingLeft() + getPaddingRight();
        }

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (mImgBitmap != null
                && (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST)) {
            height = mImgBitmap.getHeight() + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(width, height);

        comSize(width, height);
        mWidth = width;
        mHeight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isTouCutBitmap) return;
        if (mRectF == null || mRectF.isEmpty()) mRectF = new RectF(0, 0, getWidth(), getHeight());
        int sc = canvas.saveLayer(mRectF, null, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(mRectF, mRectPaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCircleRadius, mCirclePaint);
        canvas.restoreToCount(sc);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCircleRadius, mRoundPaint);
        drawText(canvas, mTextPaint, getResources().getString(R.string.avatar_view_message));
    }

    private void drawText(Canvas canvas, Paint paint, String text) {
        Rect r = new Rect();
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom - mCircleRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                30, getResources().getDisplayMetrics());
        canvas.drawText(text, x, y, paint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mCurMode = DRAG;
                mSaveMatrix.set(mMatrix);
                mPre.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mCurMode = ZOOM;
                mSaveMatrix.set(mMatrix);
                dist = spacing(event);
                midPoint(mMid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurMode == DRAG) {
                    mMatrix.set(mSaveMatrix);
                    mMatrix.postTranslate(event.getX() - mPre.x, event.getY() - mPre.y);
                } else if (mCurMode == ZOOM) {
                    float scale = spacing(event) / dist;
                    mMatrix.set(mSaveMatrix);
                    mMatrix.getValues(mMXValues);
                    if (mMXValues[0] * scale > MAX_SCALE) {
                        scale = MAX_SCALE / mMXValues[0];
                    } else if (mMXValues[0] * scale < MIN_SCALE) {
                        scale = MIN_SCALE / mMXValues[0];
                    }
                    mMatrix.postScale(scale, scale, mMid.x, mMid.y);
                } else if (mCurMode == NONE) {
                }
                checkBoundary(mMatrix);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mCurMode = NONE;
                break;
            case MotionEvent.ACTION_UP:
                mCurMode = NONE;
                break;
            default:
                break;
        }
        setImageMatrix(mMatrix);
        return true;
    }

    protected void checkBoundary(Matrix matrix) {
        if (matrix == null) return;
        matrix.getValues(mMXValues);

        float size = mImgBitmap.getWidth() < mImgBitmap.getHeight() ? mImgBitmap.getWidth() : mImgBitmap.getHeight();
        if (mMXValues[0] < mCircleRadius * 2 / size || mMXValues[4] < mCircleRadius * 2 / size) {
            mMXValues[0] = mCircleRadius * 2 / size;
            mMXValues[4] = mCircleRadius * 2 / size;
        }
        if (mMXValues[2] > getWidth() / 2 - mCircleRadius) {
            mMXValues[2] = getWidth() / 2 - mCircleRadius;
        }
        if (mMXValues[5] > getHeight() / 2 - mCircleRadius) {
            mMXValues[5] = getHeight() / 2 - mCircleRadius;
        }
        if (mMXValues[2] < -mImgBitmap.getWidth() * mMXValues[0] + getWidth() / 2 + mCircleRadius) {
            mMXValues[2] = -mImgBitmap.getWidth() * mMXValues[0] + getWidth() / 2 + mCircleRadius;
        }
        if (mMXValues[5] < -mImgBitmap.getHeight() * mMXValues[4] + getHeight() / 2 + mCircleRadius) {
            mMXValues[5] = -mImgBitmap.getHeight() * mMXValues[4] + getHeight() / 2 + mCircleRadius;
        }
        matrix.setValues(mMXValues);
    }

    protected void center(boolean horizontal, boolean vertical) {
        if (mImgBitmap == null) {
            return;
        }
        float size = mImgBitmap.getWidth() > mImgBitmap.getHeight() ? mImgBitmap.getHeight() : mImgBitmap.getWidth();

        float scale = mImgBitmap.getWidth() > mImgBitmap.getHeight() ? mHeight / size : mWidth / size;
        mMatrix.setScale(scale, scale);
        setImageMatrix(mMatrix);

        Matrix m = new Matrix();
        m.set(mMatrix);
        RectF rect = new RectF(0, 0, mImgBitmap.getWidth(), mImgBitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
//            int screenHeight = getMeasuredHeight();
            int screenHeight = mHeight;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
//            int screenWidth = getMeasuredWidth();
            int screenWidth = mWidth;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        mMatrix.postTranslate(deltaX, deltaY);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}