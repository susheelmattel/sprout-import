/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.ui.widget


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.sproutling.common.R

/**
 * Created by subram13 on 1/24/18.
 */

class ProfilePhotoEditView : android.support.v7.widget.AppCompatImageView {

    private var isTouCutBitmap = false

    private var mMatrix: Matrix? = null
    private var mSaveMatrix: Matrix? = null
    private var mImgBitmap: Bitmap? = null
    private var mCurMode = NONE

    private var mCircleRadius: Float = 0.toFloat()
    private var mRectPaint: Paint? = null
    private var mCirclePaint: Paint? = null
    private var mRoundPaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var mRectF: RectF? = null

    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mImageSize: Int = 0

    private val mPre = PointF()
    private val mMid = PointF()
    private var dist = 1f

    private val mMXValues = FloatArray(9)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mMatrix = Matrix()
        mSaveMatrix = Matrix()
        mRectPaint = Paint()
        mRectPaint!!.isAntiAlias = true
        mRectPaint!!.color = -0x50000000
        mCirclePaint = Paint()
        mCirclePaint!!.isAntiAlias = true
        mCirclePaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        mCirclePaint!!.color = -0x1
        mRoundPaint = Paint()
        mRoundPaint!!.color = -0x1
        mRoundPaint!!.style = Paint.Style.STROKE
        mRoundPaint!!.isAntiAlias = true
        mRoundPaint!!.strokeWidth = DEF_FRAME_WIDTH
        mTextPaint = Paint()
        mTextPaint!!.color = Color.WHITE
        mTextPaint!!.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TEXT_SIZE.toFloat(), resources.displayMetrics)
        mTextPaint!!.style = Paint.Style.STROKE
    }

    fun setImageSize(size: Int) {
        mImageSize = size
    }

    @JvmOverloads
    fun clip(isCircle: Boolean = false): Bitmap {
        val paint = Paint()
        paint.isAntiAlias = true
        invalidate()
        isTouCutBitmap = true
        isDrawingCacheEnabled = true
        val bitmap = drawingCache.copy(drawingCache.config, false)
        isDrawingCacheEnabled = false
        val head = Bitmap.createBitmap(mCircleRadius.toInt() * 2, mCircleRadius.toInt() * 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(head)
        if (isCircle) {
            canvas.drawRoundRect(RectF(0f, 0f, 2 * mCircleRadius, 2 * mCircleRadius), mCircleRadius, mCircleRadius, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }
        val dst = RectF(-bitmap.width / 2 + mCircleRadius, -height / 2 + mCircleRadius, bitmap.width - bitmap.width / 2 + mCircleRadius, height - height / 2 + mCircleRadius)
        canvas.drawBitmap(bitmap, null, dst, paint)
        isTouCutBitmap = false

        return resizeBitmap(head, mImageSize, mImageSize)
    }

    fun resizeBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        val resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    private fun comSize(width: Int, height: Int) {
        if (width == 0 || height == 0) return
        if (width < height) {
            mCircleRadius = width * DEF_CLICK_SIZE
        } else {
            mCircleRadius = height * DEF_CLICK_SIZE
        }

        val bd = drawable as BitmapDrawable
        if (bd != null) {
            mImgBitmap = bd.bitmap
        }
        scaleType = ImageView.ScaleType.MATRIX
        setImageBitmap(mImgBitmap)

        center(true, true)
        imageMatrix = mMatrix
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var width = View.MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)

        if (mImgBitmap != null && (widthMode == View.MeasureSpec.UNSPECIFIED || widthMode == View.MeasureSpec.AT_MOST)) {
            width = mImgBitmap!!.width + paddingLeft + paddingRight
        }

        var height = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        if (mImgBitmap != null && (heightMode == View.MeasureSpec.UNSPECIFIED || heightMode == View.MeasureSpec.AT_MOST)) {
            height = mImgBitmap!!.height + paddingTop + paddingBottom
        }
        setMeasuredDimension(width, height)

        comSize(width, height)
        mWidth = width
        mHeight = height
    }

    @SuppressLint("WrongConstant")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isTouCutBitmap) return
        if (mRectF == null || mRectF!!.isEmpty) mRectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val sc = canvas.saveLayer(mRectF, null, Canvas.MATRIX_SAVE_FLAG
                or Canvas.CLIP_SAVE_FLAG or Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                or Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                or Canvas.CLIP_TO_LAYER_SAVE_FLAG or Canvas.ALL_SAVE_FLAG)
        canvas.drawRect(mRectF!!, mRectPaint!!)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), mCircleRadius, mCirclePaint!!)
        canvas.restoreToCount(sc)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), mCircleRadius, mRoundPaint!!)
        drawText(canvas, mTextPaint, resources.getString(R.string.adjust_photo_label))
    }

    private fun drawText(canvas: Canvas, paint: Paint?, text: String) {
        val r = Rect()
        canvas.getClipBounds(r)
        val cHeight = r.height()
        val cWidth = r.width()
        paint!!.textAlign = Paint.Align.LEFT
        paint.getTextBounds(text, 0, text.length, r)
        val x = cWidth / 2f - r.width() / 2f - r.left.toFloat()
        val y = cHeight / 2f + r.height() / 2f - r.bottom.toFloat() - mCircleRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                30f, resources.displayMetrics)
        canvas.drawText(text, x, y, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mCurMode = DRAG
                mSaveMatrix!!.set(mMatrix)
                mPre.set(event.x, event.y)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                mCurMode = ZOOM
                mSaveMatrix!!.set(mMatrix)
                dist = spacing(event)
                midPoint(mMid, event)
            }
            MotionEvent.ACTION_MOVE -> {
                if (mCurMode == DRAG) {
                    mMatrix!!.set(mSaveMatrix)
                    mMatrix!!.postTranslate(event.x - mPre.x, event.y - mPre.y)
                } else if (mCurMode == ZOOM) {
                    var scale = spacing(event) / dist
                    mMatrix!!.set(mSaveMatrix)
                    mMatrix!!.getValues(mMXValues)
                    if (mMXValues[0] * scale > MAX_SCALE) {
                        scale = MAX_SCALE / mMXValues[0]
                    } else if (mMXValues[0] * scale < MIN_SCALE) {
                        scale = MIN_SCALE / mMXValues[0]
                    }
                    mMatrix!!.postScale(scale, scale, mMid.x, mMid.y)
                } else if (mCurMode == NONE) {
                }
                checkBoundary(mMatrix)
            }
            MotionEvent.ACTION_POINTER_UP -> mCurMode = NONE
            MotionEvent.ACTION_UP -> mCurMode = NONE
            else -> {
            }
        }
        imageMatrix = mMatrix
        return true
    }

    protected fun checkBoundary(matrix: Matrix?) {
        if (matrix == null) return
        matrix.getValues(mMXValues)

        val size = (if (mImgBitmap!!.width < mImgBitmap!!.height) mImgBitmap!!.width else mImgBitmap!!.height).toFloat()
        if (mMXValues[0] < mCircleRadius * 2 / size || mMXValues[4] < mCircleRadius * 2 / size) {
            mMXValues[0] = mCircleRadius * 2 / size
            mMXValues[4] = mCircleRadius * 2 / size
        }
        if (mMXValues[2] > width / 2 - mCircleRadius) {
            mMXValues[2] = width / 2 - mCircleRadius
        }
        if (mMXValues[5] > height / 2 - mCircleRadius) {
            mMXValues[5] = height / 2 - mCircleRadius
        }
        if (mMXValues[2] < -mImgBitmap!!.width * mMXValues[0] + (width / 2).toFloat() + mCircleRadius) {
            mMXValues[2] = -mImgBitmap!!.width * mMXValues[0] + (width / 2).toFloat() + mCircleRadius
        }
        if (mMXValues[5] < -mImgBitmap!!.height * mMXValues[4] + (height / 2).toFloat() + mCircleRadius) {
            mMXValues[5] = -mImgBitmap!!.height * mMXValues[4] + (height / 2).toFloat() + mCircleRadius
        }
        matrix.setValues(mMXValues)
    }

    protected fun center(horizontal: Boolean, vertical: Boolean) {
        if (mImgBitmap == null) {
            return
        }
        val size = (if (mImgBitmap!!.width > mImgBitmap!!.height) mImgBitmap!!.height else mImgBitmap!!.width).toFloat()

        val scale = if (mImgBitmap!!.width > mImgBitmap!!.height) mHeight / size else mWidth / size
        mMatrix!!.setScale(scale, scale)
        imageMatrix = mMatrix

        val m = Matrix()
        m.set(mMatrix)
        val rect = RectF(0f, 0f, mImgBitmap!!.width.toFloat(), mImgBitmap!!.height.toFloat())
        m.mapRect(rect)

        val height = rect.height()
        val width = rect.width()

        var deltaX = 0f
        var deltaY = 0f

        if (vertical) {
            //            int screenHeight = getMeasuredHeight();
            val screenHeight = mHeight
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top
            } else if (rect.top > 0) {
                deltaY = -rect.top
            } else if (rect.bottom < screenHeight) {
                deltaY = getHeight() - rect.bottom
            }
        }

        if (horizontal) {
            //            int screenWidth = getMeasuredWidth();
            val screenWidth = mWidth
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left
            } else if (rect.left > 0) {
                deltaX = -rect.left
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right
            }
        }
        mMatrix!!.postTranslate(deltaX, deltaY)
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    companion object {

        private val DEF_FRAME_WIDTH = 4.0f
        private val DEF_CLICK_SIZE = 1 / 3f
        private val MAX_SCALE = 5.0f
        private val MIN_SCALE = 0.3f
        private val TEXT_SIZE = 18

        internal val NONE = 0
        internal val DRAG = 1
        internal val ZOOM = 2
    }
}