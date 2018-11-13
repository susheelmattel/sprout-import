/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sproutling.R;
import com.sproutling.utils.Utils;

/**
 * Created by Xylon on 2016/12/27.
 */

public class BatteryDisplay extends FrameLayout {

    private static final String TAG = BatteryDisplay.class.getSimpleName();

    private static String COLOR_WHITE = "WHITE";
    private static String COLOR_GREY = "GREY";
    private static String COLOR_YELLOW = "YELLOW";
    private static final int UNKNOWN_STATE = -1216;
    private ImageView mImgBatteryFrame, mImgBatteryValue;
    private String mBatteryColor = COLOR_WHITE;
    private boolean isChargingLayout = false;
    private FrameLayout mBatteryContainer;
    private int mBatteryFullW, mBatteryFullH;

    public BatteryDisplay(Context context) {
        super(context);
        init(context, null);
    }

    public BatteryDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BatteryDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        FrameLayout mainContent = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.framelayout_battery_display, this);
        mImgBatteryFrame = (ImageView) mainContent.findViewById(R.id.img_batteryFrame);
        mImgBatteryValue = (ImageView) mainContent.findViewById(R.id.img_batteryValue);
        mBatteryContainer = (FrameLayout) mainContent.findViewById(R.id.batteryContainer);
        setChargingLayout(false);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BatteryDisplay);
            String value = a.getString(R.styleable.BatteryDisplay_batteryStyle);
            if (!TextUtils.isEmpty(value)) {
                int batteryColorVal = Integer.parseInt(value);
                mBatteryColor = getBatteryColor(batteryColorVal);
            }
        }
        mImgBatteryFrame.setImageDrawable(getOutlineDrawable());
    }

    private Drawable getOutlineDrawable() {
        Drawable whiteOutline = getResources().getDrawable(R.drawable.ic_battery_outline, null);
        if (isChargingLayout) {
            whiteOutline = getResources().getDrawable(R.drawable.ic_charging, null);
        }
        whiteOutline.setTint(getColor());
        return whiteOutline;
    }

    private int getColor() {
        int color = Utils.getColor(getContext(), R.color.white);
        if (COLOR_GREY.equalsIgnoreCase(mBatteryColor)) {
            color = Utils.getColor(getContext(), R.color.grey50);
        } else if (COLOR_WHITE.equalsIgnoreCase(mBatteryColor)) {
            color = Utils.getColor(getContext(), R.color.white);
        } else if (COLOR_YELLOW.equalsIgnoreCase(mBatteryColor)) {
            color = Utils.getColor(getContext(), R.color.yellow_oval);
        }
        return color;
    }

    private Drawable getFillDrawable(int value) {
//		Drawable fillDrawable = getResources().getDrawable(R.drawable.ic_white_battery_100, null);
//		if (value > 75) {
//			fillDrawable = isChargingLayout ? getDrawable(R.drawable.ic_charging_100) : getDrawable(R.drawable.ic_white_battery_100);
//		} else if (value > 50) {
//			fillDrawable = isChargingLayout ? getDrawable(R.drawable.ic_charging_75) : getDrawable(R.drawable.ic_white_battery_75);
//		} else if (value > 20) {
//			fillDrawable = isChargingLayout ? getDrawable(R.drawable.ic_charging_50) : getDrawable(R.drawable.ic_white_battery_50);
//		} else if (value > 10) {
//			fillDrawable = isChargingLayout ? getDrawable(R.drawable.ic_charging_20) : getDrawable(R.drawable.ic_white_battery_20);
//		} else if (value > 5) {
//			fillDrawable = isChargingLayout ? getDrawable(R.drawable.ic_charging_10) : getDrawable(R.drawable.ic_white_battery_10);
//		}

        ViewGroup.LayoutParams batteryValueLayoutParams = mImgBatteryValue.getLayoutParams();
        batteryValueLayoutParams.width = (int) (mBatteryFullW * value * 0.01f);
        batteryValueLayoutParams.height = mBatteryFullH;
        mImgBatteryValue.setLayoutParams(batteryValueLayoutParams);

        Drawable fillDrawable = isChargingLayout ? getDrawable(R.drawable.shape_battery_corners) : getDrawable(R.drawable.shape_battery);
        fillDrawable.setTint(getFillColor(value));
        return fillDrawable;
    }

    private Drawable getDrawable(int id) {
        return getResources().getDrawable(id, null);
    }

    private int getFillColor(int value) {
        int color = getColor();
        if (!isChargingLayout) {
            if (value <= 10) {
                color = Utils.getColor(getContext(), R.color.dark_poppy);
            } else if (value <= 20) {
                color = Utils.getColor(getContext(), R.color.canary);
            }
        }
        return color;
    }

    private String getBatteryColor(int batteryColorValue) {
        switch (batteryColorValue) {
            case 0:
                return COLOR_WHITE;
            case 1:
                return COLOR_GREY;
            default:
                return COLOR_WHITE;
        }
    }

    public void setChargingLayout(boolean chargingLayout) {
        isChargingLayout = chargingLayout;

        FrameLayout.LayoutParams batteryContainerLayoutParams = (FrameLayout.LayoutParams) mBatteryContainer.getLayoutParams();
        FrameLayout.LayoutParams batteryViewLayoutParams = (FrameLayout.LayoutParams) mImgBatteryValue.getLayoutParams();
        Resources res = getResources();
        if (chargingLayout) {
//			mImgBatteryValue.setAlpha(.5f);
            mBatteryFullW = batteryContainerLayoutParams.width = res.getDimensionPixelSize(R.dimen.batteryChargingW);
            mBatteryFullH = batteryContainerLayoutParams.height = res.getDimensionPixelSize(R.dimen.batteryChargingH);
            batteryContainerLayoutParams.setMargins(0, 0, res.getDimensionPixelSize(R.dimen.batteryChargingFixMargin), 0);
        } else {
//			mImgBatteryValue.setAlpha(1f);
            mBatteryFullW = batteryContainerLayoutParams.width = res.getDimensionPixelSize(R.dimen.batteryChargingIconW);
            mBatteryFullH = batteryContainerLayoutParams.height = res.getDimensionPixelSize(R.dimen.batteryChargingIconH);
            batteryContainerLayoutParams.setMargins(0,
                    res.getDimensionPixelSize(R.dimen.batteryChargingIconFixMargin),
                    res.getDimensionPixelSize(R.dimen.batteryChargingIconFixMargin),
                    res.getDimensionPixelSize(R.dimen.batteryChargingIconFixMargin));
        }
        mBatteryContainer.setLayoutParams(batteryContainerLayoutParams);
        mImgBatteryValue.setLayoutParams(batteryViewLayoutParams);
    }

    public void setBatteryDisplay(int value) {
        Log.v(TAG, "battery value: " + value);

        mImgBatteryFrame.setImageDrawable(getOutlineDrawable());
        if (value > 5) {
            mImgBatteryValue.setVisibility(VISIBLE);
            mImgBatteryValue.setImageDrawable(getFillDrawable(value));
        } else if (value == UNKNOWN_STATE) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_battery_unknown, null);
            drawable.setTint(getColor());
            mImgBatteryFrame.setImageDrawable(drawable);
            mImgBatteryValue.setVisibility(INVISIBLE);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_battery_out, null);
            drawable.setTint(getColor());
            mImgBatteryFrame.setImageDrawable(drawable);
            mImgBatteryValue.setVisibility(INVISIBLE);
        }
    }

    public void setUnknownState() {
        setBatteryDisplay(UNKNOWN_STATE);
    }
}
