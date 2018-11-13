/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.StringDef;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;

import com.sproutling.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by subram13 on 2/2/17.
 */

public class ShTextView extends AppCompatTextView {
    private static final String TAG = "ShTextView";
    public static final String LONDON = "0";
    public static final String PARIS = "2";
    public static final String NEWYORK = "1";

    public ShTextView(Context context) {
        super(context);
        setCustomFontFromAttributeSet(context, null);
    }

    public ShTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFontFromAttributeSet(context, attrs);
    }

    public ShTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFontFromAttributeSet(context, attrs);
    }

    private void setCustomFontFromAttributeSet(Context ctx, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ShTextView);
            setCustomFont(ctx, getFont(a));
            a.recycle();
        } else {
            setCustomFont(ctx, TypeFaces.PARIS);
        }
//        setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f,  getResources().getDisplayMetrics()), 1.0f);
    }

    public boolean setCustomFont(Context ctx, @TypeFaces.FontType String asset) {
        Typeface tf = TypeFaces.get(ctx, asset);
        if (tf == null) {
            Log.e(TAG, "Font: " + asset + " is not found");
            return false;
        }
        setTypeface(tf);
        return true;
    }

    private String getFont(TypedArray a) {
        assert a != null;
        return getFont(a.getString(R.styleable.ShTextView_shFontStyle));
    }

    @TypeFaces.FontType
    private String getFont(String customFontType) {
        String customFont = TypeFaces.LONDON;
        if (!TextUtils.isEmpty(customFontType)) {
            switch (customFontType) {
                case LONDON:
                    customFont = TypeFaces.LONDON;
                    break;
                case PARIS:
                    customFont = TypeFaces.PARIS;
                    break;
                case NEWYORK:
                    customFont = TypeFaces.NEWYORK;
                    break;
            }
        }
        return customFont;
    }

    public static class TypeFaces {
        private static final String TAG = "Typefaces";
        public static final String LONDON = "Chalet LondonNineteenSixty.otf";
        public static final String PARIS = "Chalet ParisNineteenSixty.otf";
        public static final String NEWYORK = "Chalet NewYorkNineteenSixty.otf";
        private static final LruCache<String, Typeface> cache = new LruCache<>(12);

        @StringDef({LONDON, PARIS, NEWYORK})
        @Retention(RetentionPolicy.SOURCE)
        public @interface FontType {

        }

        public static Typeface get(Context c, String assetPath) {
            if (assetPath == null) assetPath = PARIS;

            synchronized (cache) {
                if (cache.get(assetPath) == null) {
                    try {
                        Typeface t = Typeface.createFromAsset(c.getAssets(),
                                "fonts/" + assetPath);
                        cache.put(assetPath, t);
                    } catch (Exception e) {
                        Log.e(TAG, "Could not get typeface '" + assetPath
                                + "' because " + e.getMessage());
                        return null;
                    }
                }
                return cache.get(assetPath);
            }
        }
    }
}
