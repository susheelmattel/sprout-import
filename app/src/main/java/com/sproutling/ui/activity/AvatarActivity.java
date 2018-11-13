/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.DisplayMetrics;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.view.AvatarView;
import com.sproutling.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.sproutling.utils.Utils.getPathForPreV19;
import static com.sproutling.utils.Utils.getPathForV19AndUp;
import static com.sproutling.utils.Utils.isFilePath;

/**
 * Created by bradylin on 12/2/16.
 */

public class AvatarActivity extends BaseActivity {

    public static final String EXTRA_MODE = "mode";
    public static final int MODE_UNKNOWN = 0;
    public static final int MODE_URI = 1;
    public static final int MODE_BITMAP = 2;

    public static final String EXTRA_SRC = "imageSrc";
    public static final String EXTRA_DST = "imageDst";
    public static final String EXTRA_IMAGE_SIZE = "imageSize";
    public static final String EXTRA_BITMAP = "imageBitmap";
    public static final int IMAGE_SIZE_DEFAULT = 150;

    private String mImageDst;
    private int mImageSize;
    private Bitmap mBitmap = null;

    private AvatarView mAvatarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        mAvatarView = (AvatarView) findViewById(R.id.avatar_view);

        Intent intent = getIntent();
        mImageDst = intent.getStringExtra(EXTRA_DST);
        mImageSize = intent.getIntExtra(EXTRA_IMAGE_SIZE, IMAGE_SIZE_DEFAULT);

        mAvatarView.setImageBitmap(getBitmap(intent));

        findViewById(R.id.use).setOnClickListener(mOnUseClickListener);
        findViewById(R.id.cancel).setOnClickListener(mOnCancelClickListener);
    }

    private Bitmap getBitmap(Intent intent) {
        int mode = intent.getIntExtra(EXTRA_MODE, MODE_UNKNOWN);
        if (mode == MODE_URI) {
            Uri uri = Uri.parse(intent.getStringExtra(EXTRA_SRC));
            try {
//            mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                mBitmap = decodeBitmapFromUri(uri, mImageSize, mImageSize);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                mBitmap = decodeBitmapFromUri(uri, width, height);

                String path;
                if (isFilePath(uri.toString())) {
                    path = uri.getPath();
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(this, uri)) {
                    path = getPathForV19AndUp(this, uri);
                } else {
                    path = getPathForPreV19(this, uri);
                }
                if (path != null) mBitmap = Utils.rotateImageIfRequired(mBitmap, path);
            } catch (IOException e) {
                mBitmap = null;
                e.printStackTrace();
            }
        } else {
            mBitmap = (Bitmap) intent.getBundleExtra(EXTRA_BITMAP).get("data");
        }
        return mBitmap;
    }

    private Bitmap decodeBitmapFromUri(Uri uri, int reqWidth, int reqHeight) throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void saveBitmap() {
        try {
            FileOutputStream out = new FileOutputStream(mImageDst);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener mOnUseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBitmap != null && mBitmap.isRecycled()) {
                mBitmap.recycle();
            }
            mAvatarView.setImageSize(mImageSize);
            mBitmap = mAvatarView.clip(true);
            saveBitmap();
            setResult(RESULT_OK);
            finish();
        }
    };

    private View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(RESULT_CANCELED);
            finish();
        }
    };
}