/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.ui.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View

import com.sproutling.common.R
import com.sproutling.common.ui.widget.ProfilePhotoEditView
import com.sproutling.common.utils.Utils

import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by subram13 on 1/23/18.
 */

class ProfilePhotoView : BaseView() {

    private var mImageDst: String? = null
    private var mImageSize: Int = 0
    private var mBitmap: Bitmap? = null

    private var mPhotoEditView: ProfilePhotoEditView? = null
    private val mOnUseClickListener = View.OnClickListener {
        if (mBitmap != null && mBitmap!!.isRecycled) {
            mBitmap!!.recycle()
        }
        mPhotoEditView!!.setImageSize(mImageSize)
        mBitmap = mPhotoEditView!!.clip(true)
        saveBitmap()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private val mOnCancelClickListener = View.OnClickListener {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_photo)

        mPhotoEditView = findViewById<View>(R.id.avatar_view) as ProfilePhotoEditView

        val intent = intent
        mImageDst = intent.getStringExtra(EXTRA_DST)
        mImageSize = intent.getIntExtra(EXTRA_IMAGE_SIZE, IMAGE_SIZE_DEFAULT)

        mPhotoEditView!!.setImageBitmap(getBitmap(intent))

        findViewById<View>(R.id.use).setOnClickListener(mOnUseClickListener)
        findViewById<View>(R.id.cancel).setOnClickListener(mOnCancelClickListener)
    }

    private fun getBitmap(intent: Intent): Bitmap? {
        val mode = intent.getIntExtra(EXTRA_MODE, MODE_UNKNOWN)
        val isCameraPath = intent.getBooleanExtra(EXTRA_PHOTO_TYPE, false)
        if (mode == MODE_URI) {
            val uri = Uri.parse(intent.getStringExtra(EXTRA_SRC))
            try {
                //            mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //                mBitmap = decodeBitmapFromUri(uri, mImageSize, mImageSize);
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val height = displayMetrics.heightPixels
                val width = displayMetrics.widthPixels
                mBitmap = decodeBitmapFromUri(uri, width, height)

                //                String path;
                //                if (Utils.isFilePath(uri.toString())) {
                //                    path = uri.getPath();
                //                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(this, uri)) {
                //                    path = Utils.getPathForV19AndUp(this, uri);
                //                } else {
                //                    path = Utils.getPathForPreV19(this, uri);
                //                }
                //                if (path != null) mBitmap = Utils.rotateImageIfRequired(mBitmap, path);
                if(isCameraPath){
                    if (mImageDst != null){
                        mBitmap = Utils.rotateImageIfRequired(this@ProfilePhotoView, mBitmap, mImageDst, uri)
                    }
                } else{
                    mBitmap = Utils.rotateBitmap(this@ProfilePhotoView, uri, mBitmap)
                }



            } catch (e: IOException) {
                mBitmap = null
                e.printStackTrace()
            }

        } else {
            mBitmap = intent.getBundleExtra(EXTRA_BITMAP).get("data") as Bitmap
        }
        return mBitmap
    }

    @Throws(FileNotFoundException::class)
    private fun decodeBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)
    }

    private fun saveBitmap() {
        try {
            val out = FileOutputStream(mImageDst!!)
            mBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        val EXTRA_MODE = "mode"
        val MODE_UNKNOWN = 0
        val MODE_URI = 1
        val MODE_BITMAP = 2

        val EXTRA_SRC = "imageSrc"
        val EXTRA_DST = "imageDst"
        val EXTRA_PHOTO_TYPE = "photoType"
        val EXTRA_IMAGE_SIZE = "imageSize"
        val EXTRA_BITMAP = "imageBitmap"
        val IMAGE_SIZE_DEFAULT = 150

        fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }
}