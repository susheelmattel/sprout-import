/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class DeviceFitting {
    private static final String TAG = DeviceFitting.class.getName();

    private static SparseArray<Integer> sFitDeviceSizes;
    //    public static final int KEY_DESITY = 1;
    public static final int KEY_DENSITY_DPI = 2;
    public static final int KEY_DISPLAY_SHORT_SIDE = 3;
    public static final int KEY_DISPLAY_LONG_SIDE = 4;
    public static final int KEY_ACTION_BAR_HEIGHT = 6;
    public static final int KEY_STATUS_BAR_HEIGHT = 7;
//    public static final int KEY_NAVIGATIONBAR_HEIGHT = 8;

//    public static final int KEY_CATEGORY_CONTENT_HEIGHT = 10;
//    public static final int KEY_HOME_VIEWPAGER_HEIGHT = 101;

    public static SparseArray<Integer> getFitDeviceSizes(Activity activity) {
        if (sFitDeviceSizes == null) {
            sFitDeviceSizes = fitDeviceSizeCalculator(activity);
            Log.v(TAG, "sFitDeviceSizes init.");
        }
        return sFitDeviceSizes;
    }

    private static SparseArray<Integer> fitDeviceSizeCalculator(Activity activity) {
        SparseArray<Integer> resultSizes = new SparseArray<>();
//        Display display = activity.getWindowManager().getDefaultDisplay();
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        // Be aware if app orientation could be changed by device rotate
        int displayShort, displayLong;
        if (displaySize.x > displaySize.y) { //landscape
            displayLong = displaySize.x;
            displayShort = displaySize.y;
        } else { //portrait
            displayLong = displaySize.y;
            displayShort = displaySize.x;
        }
        resultSizes.put(KEY_DISPLAY_SHORT_SIDE, displayShort);
        resultSizes.put(KEY_DISPLAY_LONG_SIDE, displayLong);

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
//        resultSizes.put(KEY_DESITY, metrics.density); //not an integer
        resultSizes.put(KEY_DENSITY_DPI, metrics.densityDpi);

        Resources res = activity.getResources();

        TypedValue typedValue = new TypedValue();
        int actionBarHeight = 0;
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, res.getDisplayMetrics());
        }
        resultSizes.put(KEY_ACTION_BAR_HEIGHT, actionBarHeight);
        //
        int statusBarHeight = 0;
        int statusBarHeightId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarHeightId > 0) {
            statusBarHeight = res.getDimensionPixelSize(statusBarHeightId);
        }
        resultSizes.put(KEY_STATUS_BAR_HEIGHT, statusBarHeight);
        //
//        int categoryHeight = displayLong-actionBarHeight-statusBarHeight;
//        resultSizes.put(KEY_CATEGORY_CONTENT_HEIGHT, categoryHeight);

//        int homeViewPagerHeight = (int) (displayShort * 0.456);
//        resultSizes.put(KEY_HOME_VIEWPAGER_HEIGHT, homeViewPagerHeight);

        return resultSizes;
    }

    public static int getValueByKey(Activity activity, int key) {
        if (sFitDeviceSizes == null) {
            getFitDeviceSizes(activity);
        }
        return sFitDeviceSizes.get(key);
    }

    public static float getDesity(Activity activity) {
        if (sFitDeviceSizes == null) {
            getFitDeviceSizes(activity);
        }
        return sFitDeviceSizes.get(KEY_DENSITY_DPI) / DisplayMetrics.DENSITY_MEDIUM;
    }

    public static float convertDpToPixel(Context context, int valueOfDP) {
        float result = 0f;
        if (context != null) {
            float density = context.getResources().getDisplayMetrics().density;
            result = density * valueOfDP;
        }
        return result;
    }

    public static float convertPixelToDp(Context context, int valueOfPixel) {
        float result = 0f;
        if (context != null) {
            float density = context.getResources().getDisplayMetrics().density;
            result = valueOfPixel / density;
        }
        return result;
    }
}
