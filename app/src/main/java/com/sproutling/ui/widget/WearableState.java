/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sproutling.R;
import com.sproutling.states.States;

/**
 * Created by Xylon on 2016/12/28.
 */

public class WearableState extends FrameLayout {

    private FrameLayout mFrameWearableCharging,
            mFrameWearableCharged,
            mFrameWearableOutOfBattery,
            mFrameWearableReady,
            mFrameWearableLoading,
            mFrameWearableDetecting,
            mFrameWearableTooFarAway,
            mFrameWearableCannotFind,
            mFrameWearableFellOff,
            mFrameSproutlingOffline,
            mFrameNoService,
            mFrameFirmwareUpdating;

    private ImageView mImgLightning;
    private ImageView mImgLightning2, mImgBatteryFrame2, mImgBatteryFrameGrow2, mImgBatteryValue2;
    private ImageView mImgBatteryFrame3, mImgBatteryFrameLine;
    private ImageView mImgWearable, mImgWearableGlow;
    private ImageView mImgLoadingOne, mImgLoadingTwo, mImgLoadingThree;
    private ImageView mImgWearableDetecting, mImgDetectingOne, mImgDetectingTwo, mImgDetectingThree;
    private ImageView mImgWearableFirmwareUpdating, mImgFirmwareUpdatingOne, mImgFirmwareUpdatingTwo, mImgFirmwareUpdatingThree;
    private ImageView mImgWearableFar, mImgWearableFarSignal1, mImgWearableFarSignal2, mImgWearableFarHub;
    private ImageView mImgQuestion, mImgWearableCannotFind;
    private ImageView mImgWearableLine, mImgWearableFellOff;
    private ImageView mImgSproutlingLine, mImgSproutlingOffline;
    private ImageView mImgNoServiceWifi3, mImgNoServiceWifi2, mImgNoServiceWifi1, mImgNoServiceLine, mImgNoServicePhone;
    private BatteryDisplay mBatteryDisplay;

    private ObjectAnimator mWearableCharging;
    private ObjectAnimator mWearableCharged;
    private ObjectAnimator mWearableReadyAnimator;
    private ObjectAnimator mWearableTooFar, mWearableTooFarSignal1, mWearableTooFarSignal2;
    private ObjectAnimator mWearableCanNoyFindAnimator;
    private ObjectAnimator mWearableFellOffAnimator;
    private ObjectAnimator mWearableSproutlingOffline;
    private ObjectAnimator mNoServiceWifi1, mNoServiceWifi2, mNoServiceWifi3;

    private AnimatorSet mWearableLoadingDotsAnimatorSet;
    private AnimatorSet mWearableDetectingDotsAnimatorSet;
    private AnimatorSet mFirmwareUpdatingDotsAnimatorSet;

    private String mStatus = null;


    public WearableState(Context context) {
        super(context);
        init(context);
    }

    public WearableState(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WearableState(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        FrameLayout mainContent = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.framelayout_wearable_state, this);

        //WEARABLE_LOADING
        mFrameWearableLoading = (FrameLayout) mainContent.findViewById(R.id.frame_wearable_loading);
        mImgLoadingOne = (ImageView) mainContent.findViewById(R.id.img_loading_one);
        mImgLoadingTwo = (ImageView) mainContent.findViewById(R.id.img_loading_two);
        mImgLoadingThree = (ImageView) mainContent.findViewById(R.id.img_loading_three);

        //WEARABLE_DETECT
        mFrameWearableDetecting = (FrameLayout) mainContent.findViewById(R.id.frame_wearable_detecting);
        mImgWearableDetecting = (ImageView) mainContent.findViewById(R.id.img_wearable_detecting);
        mImgDetectingOne = (ImageView) mainContent.findViewById(R.id.img_detecting_one);
        mImgDetectingTwo = (ImageView) mainContent.findViewById(R.id.img_detecting_two);
        mImgDetectingThree = (ImageView) mainContent.findViewById(R.id.img_detecting_three);

        //FIRMWARE UPDATING
        mFrameFirmwareUpdating= (FrameLayout) mainContent.findViewById(R.id.frame_wearable_firmware_updating);
        mImgWearableFirmwareUpdating = (ImageView) mainContent.findViewById(R.id.img_wearable_firmware_updating);
        mImgFirmwareUpdatingOne = (ImageView) mainContent.findViewById(R.id.img_firmware_updating_one);
        mImgFirmwareUpdatingTwo = (ImageView) mainContent.findViewById(R.id.img_firmware_updating_two);
        mImgFirmwareUpdatingThree = (ImageView) mainContent.findViewById(R.id.img_firmware_updating_three);

        //WEARABLE_CHARGING
        mFrameWearableCharging = (FrameLayout) mainContent.findViewById(R.id.frame_wearable_charging);
        mImgLightning = (ImageView) mainContent.findViewById(R.id.img_lightning);
//        img_batteryFrame = (ImageView) mainContent.findViewById(R.id.img_batteryFrame);
//        img_batteryValue = (ImageView) mainContent.findViewById(R.id.img_batteryValue);
        mBatteryDisplay = (BatteryDisplay) mainContent.findViewById(R.id.battery_charging);

        //WEARABLE_CHARGED
        mFrameWearableCharged = (FrameLayout) mainContent.findViewById(R.id.frame_wearable_charged);
        mImgLightning2 = (ImageView) mainContent.findViewById(R.id.img_lightning2);
        mImgBatteryFrame2 = (ImageView) mainContent.findViewById(R.id.img_batteryFrame2);
        mImgBatteryFrameGrow2 = (ImageView) mainContent.findViewById(R.id.img_batteryFrame_grow2);
        mImgBatteryValue2 = (ImageView) mainContent.findViewById(R.id.img_batteryValue2);

        //WEARABLE_OUT_OF_BATTERY
        mFrameWearableOutOfBattery = (FrameLayout) mainContent.findViewById(R.id.frame_wearable_out_of_battery);
        mImgBatteryFrame3 = (ImageView) mainContent.findViewById(R.id.img_batteryFrame3);
        mImgBatteryFrameLine = (ImageView) mainContent.findViewById(R.id.img_batteryFrame_line);

        //WEARABLE_READY
        mFrameWearableReady = (FrameLayout) mainContent.findViewById(R.id.frame_wearable_ready);
        mImgWearable = (ImageView) mainContent.findViewById(R.id.img_wearable);
        mImgWearableGlow = (ImageView) mainContent.findViewById(R.id.img_wearable_glow);

        //WEARABLE_TOO_FAR_AWAY
        mFrameWearableTooFarAway = (FrameLayout) mainContent.findViewById(R.id.frame_wearable_too_far_away);
        mImgWearableFar = (ImageView) mainContent.findViewById(R.id.img_wearable_far);
        mImgWearableFarSignal1 = (ImageView) mainContent.findViewById(R.id.img_wearable_far_signal1);
        mImgWearableFarSignal2 = (ImageView) mainContent.findViewById(R.id.img_wearable_far_signal2);
        mImgWearableFarHub = (ImageView) mainContent.findViewById(R.id.img_wearable_far_hub);

        //WEARABLE_NOT_FOUND
        mFrameWearableCannotFind = (FrameLayout) mainContent.findViewById(R.id.frame_wearable_cannot_find);
        mImgQuestion = (ImageView) mainContent.findViewById(R.id.img_question);
        mImgWearableCannotFind = (ImageView) mainContent.findViewById(R.id.img_wearable_cannot_find);

        //WEARABLE_FELL_OFF
        mFrameWearableFellOff = (FrameLayout) mainContent.findViewById(R.id.frame_wearable_fell_off);
        mImgWearableLine = (ImageView) mainContent.findViewById(R.id.img_wearable_line);
        mImgWearableFellOff = (ImageView) mainContent.findViewById(R.id.img_wearable_fell_off);

        //SPROUTLING_OFFLINE
        mFrameSproutlingOffline = (FrameLayout) mainContent.findViewById(R.id.frame_sproutling_offline);
        mImgSproutlingLine = (ImageView) mainContent.findViewById(R.id.img_sproutling_line);
        mImgSproutlingOffline = (ImageView) mainContent.findViewById(R.id.img_sproutling_offline);

        //NO_SERVICE
        mFrameNoService = (FrameLayout) mainContent.findViewById(R.id.frame_no_service);
        mImgNoServiceWifi3 = (ImageView) mainContent.findViewById(R.id.img_no_service_wifi3);
        mImgNoServiceWifi2 = (ImageView) mainContent.findViewById(R.id.img_no_service_wifi2);
        mImgNoServiceWifi1 = (ImageView) mainContent.findViewById(R.id.img_no_service_wifi1);
        mImgNoServiceLine = (ImageView) mainContent.findViewById(R.id.img_no_service_line);
        mImgNoServicePhone = (ImageView) mainContent.findViewById(R.id.img_no_service_phone);

//        setStatus(States.StatusValue.INITIAL);
    }

    public void setBatteryCharging(int value) {
        mBatteryDisplay.setChargingLayout(true);
        mBatteryDisplay.setBatteryDisplay(value);
    }

    public void setStatus(String status) {

        if (status.equals(mStatus)) {
            return;
        }

        mStatus = status;

        mFrameWearableLoading.setVisibility(View.GONE);
        mFrameWearableDetecting.setVisibility(View.GONE);
        mFrameWearableReady.setVisibility(View.GONE);
        mFrameWearableTooFarAway.setVisibility(View.GONE);
        mFrameWearableCannotFind.setVisibility(View.GONE);
        mFrameWearableFellOff.setVisibility(View.GONE);
        mFrameSproutlingOffline.setVisibility(View.GONE);
        mFrameNoService.setVisibility(View.GONE);
        mFrameWearableCharging.setVisibility(View.GONE);
        mFrameWearableCharged.setVisibility(View.GONE);
        mFrameWearableOutOfBattery.setVisibility(View.GONE);
        mFrameFirmwareUpdating.setVisibility(View.GONE);

        if (mWearableLoadingDotsAnimatorSet != null)
            mWearableLoadingDotsAnimatorSet.cancel();
        if (mWearableDetectingDotsAnimatorSet != null)
            mWearableDetectingDotsAnimatorSet.cancel();
        if (mFirmwareUpdatingDotsAnimatorSet != null)
            mFirmwareUpdatingDotsAnimatorSet.cancel();
        if (mWearableCharging != null)
            mWearableCharging.cancel();
        if (mWearableCharged != null)
            mWearableCharged.cancel();
        if (mWearableReadyAnimator != null)
            mWearableReadyAnimator.cancel();
        if (mWearableTooFar != null)
            mWearableTooFar.cancel();
        if (mWearableTooFarSignal1 != null)
            mWearableTooFarSignal1.cancel();
        if (mWearableTooFarSignal2 != null)
            mWearableTooFarSignal2.cancel();
        if (mWearableCanNoyFindAnimator != null)
            mWearableCanNoyFindAnimator.cancel();
        if (mWearableFellOffAnimator != null)
            mWearableFellOffAnimator.cancel();
        if (mWearableSproutlingOffline != null)
            mWearableSproutlingOffline.cancel();
        if (mNoServiceWifi1 != null)
            mNoServiceWifi1.cancel();
        if (mNoServiceWifi2 != null)
            mNoServiceWifi2.cancel();
        if (mNoServiceWifi3 != null)
            mNoServiceWifi3.cancel();

        switch (status) {
            case States.StatusValue.HUB_ONLINE:
            case States.StatusValue.INITIAL:
                mFrameWearableLoading.setVisibility(View.VISIBLE);

                ObjectAnimator wearableLoadingScaleUpX1 = ObjectAnimator.ofFloat(mImgLoadingOne, "scaleX", 1.3f);
                ObjectAnimator wearableLoadingScaleUpY1 = ObjectAnimator.ofFloat(mImgLoadingOne, "scaleY", 1.3f);
                wearableLoadingScaleUpX1.setDuration(150);
                wearableLoadingScaleUpY1.setDuration(150);

                ObjectAnimator wearableLoadingScaleDownX1 = ObjectAnimator.ofFloat(mImgLoadingOne, "scaleX", 1.0f);
                ObjectAnimator wearableLoadingScaleDownY1 = ObjectAnimator.ofFloat(mImgLoadingOne, "scaleY", 1.0f);
                wearableLoadingScaleDownX1.setDuration(150);
                wearableLoadingScaleDownY1.setDuration(150);

                ObjectAnimator wearableLoadingScaleUpX2 = ObjectAnimator.ofFloat(mImgLoadingTwo, "scaleX", 1.3f);
                ObjectAnimator wearableLoadingScaleUpY2 = ObjectAnimator.ofFloat(mImgLoadingTwo, "scaleY", 1.3f);
                wearableLoadingScaleUpX2.setDuration(150);
                wearableLoadingScaleUpY2.setDuration(150);

                ObjectAnimator wearableLoadingScaleDownX2 = ObjectAnimator.ofFloat(mImgLoadingTwo, "scaleX", 1.0f);
                ObjectAnimator wearableLoadingScaleDownY2 = ObjectAnimator.ofFloat(mImgLoadingTwo, "scaleY", 1.0f);
                wearableLoadingScaleDownX2.setDuration(150);
                wearableLoadingScaleDownY2.setDuration(150);

                ObjectAnimator wearableLoadingScaleUpX3 = ObjectAnimator.ofFloat(mImgLoadingThree, "scaleX", 1.3f);
                ObjectAnimator wearableLoadingScaleUpY3 = ObjectAnimator.ofFloat(mImgLoadingThree, "scaleY", 1.3f);
                wearableLoadingScaleUpX3.setDuration(150);
                wearableLoadingScaleUpY3.setDuration(150);

                ObjectAnimator wearableLoadingScaleDownX3 = ObjectAnimator.ofFloat(mImgLoadingThree, "scaleX", 1.0f);
                ObjectAnimator wearableLoadingScaleDownY3 = ObjectAnimator.ofFloat(mImgLoadingThree, "scaleY", 1.0f);
                wearableLoadingScaleDownX3.setDuration(150);
                wearableLoadingScaleDownY3.setDuration(150);

                mWearableLoadingDotsAnimatorSet = new AnimatorSet();

                mWearableLoadingDotsAnimatorSet.play(wearableLoadingScaleUpX1).with(wearableLoadingScaleUpY1);
                mWearableLoadingDotsAnimatorSet.play(wearableLoadingScaleDownX1).with(wearableLoadingScaleDownY1).after(wearableLoadingScaleUpX1);

                mWearableLoadingDotsAnimatorSet.play(wearableLoadingScaleUpX2).with(wearableLoadingScaleUpY2).after(wearableLoadingScaleDownX1);
                mWearableLoadingDotsAnimatorSet.play(wearableLoadingScaleDownX2).with(wearableLoadingScaleDownY2).after(wearableLoadingScaleUpX2);

                mWearableLoadingDotsAnimatorSet.play(wearableLoadingScaleUpX3).with(wearableLoadingScaleUpY3).after(wearableLoadingScaleDownX2);
                mWearableLoadingDotsAnimatorSet.play(wearableLoadingScaleDownX3).with(wearableLoadingScaleDownY3).after(wearableLoadingScaleUpX3);

                mWearableLoadingDotsAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mWearableLoadingDotsAnimatorSet.start();
                    }
                });

                mWearableLoadingDotsAnimatorSet.start();
                break;
            case States.StatusValue.DETECTING:

                mFrameWearableDetecting.setVisibility(View.VISIBLE);

                ObjectAnimator wearableDetectScaleUpX1 = ObjectAnimator.ofFloat(mImgDetectingOne, "scaleX", 1.3f);
                ObjectAnimator wearableDetectScaleUpY1 = ObjectAnimator.ofFloat(mImgDetectingOne, "scaleY", 1.3f);
                wearableDetectScaleUpX1.setDuration(150);
                wearableDetectScaleUpY1.setDuration(150);

                ObjectAnimator wearableDetectScaleDownX1 = ObjectAnimator.ofFloat(mImgDetectingOne, "scaleX", 1.0f);
                ObjectAnimator wearableDetectScaleDownY1 = ObjectAnimator.ofFloat(mImgDetectingOne, "scaleY", 1.0f);
                wearableDetectScaleDownX1.setDuration(150);
                wearableDetectScaleDownY1.setDuration(150);

                ObjectAnimator wearableDetectScaleUpX2 = ObjectAnimator.ofFloat(mImgDetectingTwo, "scaleX", 1.3f);
                ObjectAnimator wearableDetectScaleUpY2 = ObjectAnimator.ofFloat(mImgDetectingTwo, "scaleY", 1.3f);
                wearableDetectScaleUpX2.setDuration(150);
                wearableDetectScaleUpY2.setDuration(150);

                ObjectAnimator wearableDetectScaleDownX2 = ObjectAnimator.ofFloat(mImgDetectingTwo, "scaleX", 1.0f);
                ObjectAnimator wearableDetectScaleDownY2 = ObjectAnimator.ofFloat(mImgDetectingTwo, "scaleY", 1.0f);
                wearableDetectScaleDownX2.setDuration(150);
                wearableDetectScaleDownY2.setDuration(150);

                ObjectAnimator wearableDetectScaleUpX3 = ObjectAnimator.ofFloat(mImgDetectingThree, "scaleX", 1.3f);
                ObjectAnimator wearableDetectScaleUpY3 = ObjectAnimator.ofFloat(mImgDetectingThree, "scaleY", 1.3f);
                wearableDetectScaleUpX3.setDuration(150);
                wearableDetectScaleUpY3.setDuration(150);

                ObjectAnimator wearableDetectScaleDownX3 = ObjectAnimator.ofFloat(mImgDetectingThree, "scaleX", 1.0f);
                ObjectAnimator wearableDetectScaleDownY3 = ObjectAnimator.ofFloat(mImgDetectingThree, "scaleY", 1.0f);
                wearableDetectScaleDownX3.setDuration(150);
                wearableDetectScaleDownY3.setDuration(150);

                mWearableDetectingDotsAnimatorSet = new AnimatorSet();

                mWearableDetectingDotsAnimatorSet.play(wearableDetectScaleUpX1).with(wearableDetectScaleUpY1);
                mWearableDetectingDotsAnimatorSet.play(wearableDetectScaleDownX1).with(wearableDetectScaleDownY1).after(wearableDetectScaleUpX1);

                mWearableDetectingDotsAnimatorSet.play(wearableDetectScaleUpX2).with(wearableDetectScaleUpY2).after(wearableDetectScaleDownX1);
                mWearableDetectingDotsAnimatorSet.play(wearableDetectScaleDownX2).with(wearableDetectScaleDownY2).after(wearableDetectScaleUpX2);

                mWearableDetectingDotsAnimatorSet.play(wearableDetectScaleUpX3).with(wearableDetectScaleUpY3).after(wearableDetectScaleDownX2);
                mWearableDetectingDotsAnimatorSet.play(wearableDetectScaleDownX3).with(wearableDetectScaleDownY3).after(wearableDetectScaleUpX3);

                mWearableDetectingDotsAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mWearableDetectingDotsAnimatorSet.start();
                    }
                });

                mWearableDetectingDotsAnimatorSet.start();

                break;

            case States.StatusValue.FIRMWARE_UPDATING:

                mFrameFirmwareUpdating.setVisibility(View.VISIBLE);

                ObjectAnimator firmwareUpdatingScaleUpX1 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingOne, "scaleX", 1.3f);
                ObjectAnimator firmwareUpdatingScaleUpY1 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingOne, "scaleY", 1.3f);
                firmwareUpdatingScaleUpX1.setDuration(150);
                firmwareUpdatingScaleUpY1.setDuration(150);

                ObjectAnimator firmwareUpdatingScaleDownX1 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingOne, "scaleX", 1.0f);
                ObjectAnimator firmwareUpdatingScaleDownY1 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingOne, "scaleY", 1.0f);
                firmwareUpdatingScaleDownX1.setDuration(150);
                firmwareUpdatingScaleDownY1.setDuration(150);

                ObjectAnimator firmwareUpdatingScaleUpX2 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingTwo, "scaleX", 1.3f);
                ObjectAnimator firmwareUpdatingScaleUpY2 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingTwo, "scaleY", 1.3f);
                firmwareUpdatingScaleUpX2.setDuration(150);
                firmwareUpdatingScaleUpY2.setDuration(150);

                ObjectAnimator firmwareUpdatingScaleDownX2 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingTwo, "scaleX", 1.0f);
                ObjectAnimator firmwareUpdatingScaleDownY2 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingTwo, "scaleY", 1.0f);
                firmwareUpdatingScaleDownX2.setDuration(150);
                firmwareUpdatingScaleDownY2.setDuration(150);

                ObjectAnimator firmwareUpdatingScaleUpX3 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingThree, "scaleX", 1.3f);
                ObjectAnimator firmwareUpdatingScaleUpY3 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingThree, "scaleY", 1.3f);
                firmwareUpdatingScaleUpX3.setDuration(150);
                firmwareUpdatingScaleUpY3.setDuration(150);

                ObjectAnimator firmwareUpdatingScaleDownX3 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingThree, "scaleX", 1.0f);
                ObjectAnimator firmwareUpdatingScaleDownY3 = ObjectAnimator.ofFloat(mImgFirmwareUpdatingThree, "scaleY", 1.0f);
                firmwareUpdatingScaleDownX3.setDuration(150);
                firmwareUpdatingScaleDownY3.setDuration(150);

                mFirmwareUpdatingDotsAnimatorSet = new AnimatorSet();

                mFirmwareUpdatingDotsAnimatorSet.play(firmwareUpdatingScaleUpX1).with(firmwareUpdatingScaleUpY1);
                mFirmwareUpdatingDotsAnimatorSet.play(firmwareUpdatingScaleDownX1).with(firmwareUpdatingScaleDownY1).after(firmwareUpdatingScaleUpX1);

                mFirmwareUpdatingDotsAnimatorSet.play(firmwareUpdatingScaleUpX2).with(firmwareUpdatingScaleUpY2).after(firmwareUpdatingScaleDownX1);
                mFirmwareUpdatingDotsAnimatorSet.play(firmwareUpdatingScaleDownX2).with(firmwareUpdatingScaleDownY2).after(firmwareUpdatingScaleUpX2);

                mFirmwareUpdatingDotsAnimatorSet.play(firmwareUpdatingScaleUpX3).with(firmwareUpdatingScaleUpY3).after(firmwareUpdatingScaleDownX2);
                mFirmwareUpdatingDotsAnimatorSet.play(firmwareUpdatingScaleDownX3).with(firmwareUpdatingScaleDownY3).after(firmwareUpdatingScaleUpX3);

                mFirmwareUpdatingDotsAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mFirmwareUpdatingDotsAnimatorSet.start();
                    }
                });

                mFirmwareUpdatingDotsAnimatorSet.start();

                break;

            case States.StatusValue.WEARABLE_READY:
                mFrameWearableReady.setVisibility(View.VISIBLE);
                mWearableReadyAnimator = ObjectAnimator.ofFloat(mImgWearableGlow, "alpha", 0f, 1f, 1f, 0f);
                mWearableReadyAnimator.setDuration(3000);
                mWearableReadyAnimator.setRepeatCount(-1);
                mWearableReadyAnimator.start();
                break;
            case States.StatusValue.WEARABLE_TOO_FAR_AWAY:
                mFrameWearableTooFarAway.setVisibility(View.VISIBLE);
                mWearableTooFar = ObjectAnimator.ofFloat(mImgWearableFar, "alpha", 1f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.2f, 0.1f,
                        0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f,
                        1f, 1f, 1f, 1f, 1f);
                mWearableTooFar.setDuration(2500);
                mWearableTooFar.setRepeatCount(-1);
                mWearableTooFar.start();

                mWearableTooFarSignal1 = ObjectAnimator.ofFloat(mImgWearableFarSignal1, "alpha", 1f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0f,
                        0f, 0f,
                        0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f,
                        1f, 1f, 1f);
                mWearableTooFarSignal1.setDuration(2500);
                mWearableTooFarSignal1.setRepeatCount(-1);
                mWearableTooFarSignal1.start();

                mWearableTooFarSignal2 = ObjectAnimator.ofFloat(mImgWearableFarSignal2, "alpha", 1f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0f,
                        0f, 0f, 0f, 0f,
                        0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f);
                mWearableTooFarSignal2.setDuration(2500);
                mWearableTooFarSignal2.setRepeatCount(-1);
                mWearableTooFarSignal2.start();

                break;
            case States.StatusValue.WEARABLE_NOT_FOUND:
                mFrameWearableCannotFind.setVisibility(View.VISIBLE);
                mWearableCanNoyFindAnimator = ObjectAnimator.ofFloat(mImgWearableCannotFind, "alpha", 1f, 0f, 1f);
                mWearableCanNoyFindAnimator.setDuration(4000);
                mWearableCanNoyFindAnimator.setRepeatCount(-1);
                mWearableCanNoyFindAnimator.start();
                break;
            case States.StatusValue.WEARABLE_FELL_OFF:
                mFrameWearableFellOff.setVisibility(View.VISIBLE);
                mWearableFellOffAnimator = ObjectAnimator.ofFloat(mImgWearableFellOff, "alpha", 1f, 0.5f, 1f);
                mWearableFellOffAnimator.setDuration(4000);
                mWearableFellOffAnimator.setRepeatCount(-1);
                mWearableFellOffAnimator.start();
                break;
            case States.StatusValue.HUB_OFFLINE:
                mFrameSproutlingOffline.setVisibility(View.VISIBLE);
                mWearableSproutlingOffline = ObjectAnimator.ofFloat(mImgSproutlingOffline, "alpha", 1f, 0.5f, 1f);
                mWearableSproutlingOffline.setDuration(4000);
                mWearableSproutlingOffline.setRepeatCount(-1);
                mWearableSproutlingOffline.start();
                break;
            case States.StatusValue.NO_SERVICE:
                mFrameNoService.setVisibility(View.VISIBLE);
                mNoServiceWifi1 = ObjectAnimator.ofFloat(mImgNoServiceWifi1, "alpha", 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f,
                        1f, 1f, 1f, 1f, 1f, 1f, 1f,
                        0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.5f, 0.5f);
                mNoServiceWifi1.setDuration(3800);
                mNoServiceWifi1.setRepeatCount(-1);
                mNoServiceWifi1.start();
                mNoServiceWifi2 = ObjectAnimator.ofFloat(mImgNoServiceWifi2, "alpha", 0.5f, 0.5f,
                        0.6f, 0.7f, 0.8f, 0.9f, 1f,
                        1f, 1f, 1f, 1f, 1f, 1f, 1f,
                        0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.5f);
                mNoServiceWifi2.setDuration(3800);
                mNoServiceWifi2.setRepeatCount(-1);
                mNoServiceWifi2.start();
                mNoServiceWifi3 = ObjectAnimator.ofFloat(mImgNoServiceWifi3, "alpha", 0.5f, 0.5f, 0.5f,
                        0.6f, 0.7f, 0.8f, 0.9f, 1f,
                        1f, 1f, 1f, 1f, 1f, 1f, 1f,
                        0.9f, 0.8f, 0.7f, 0.6f, 0.5f);
                mNoServiceWifi3.setDuration(3800);
                mNoServiceWifi3.setRepeatCount(-1);
                mNoServiceWifi3.start();
                break;
            case States.StatusValue.WEARABLE_CHARGING:
                mFrameWearableCharging.setVisibility(View.VISIBLE);
                mWearableCharging = ObjectAnimator.ofFloat(mImgLightning, "alpha", 0.25f, 1f, 1f, 0.25f);
                mWearableCharging.setDuration(3000);
                mWearableCharging.setRepeatCount(-1);
                mWearableCharging.start();
                break;
            case States.StatusValue.WEARABLE_CHARGED:
                mFrameWearableCharged.setVisibility(View.VISIBLE);
                mWearableCharged = ObjectAnimator.ofFloat(mImgBatteryFrameGrow2, "alpha", 0f, 1f, 1f, 0f);
                mWearableCharged.setDuration(3000);
                mWearableCharged.setRepeatCount(-1);
                mWearableCharged.start();
                break;
            case States.StatusValue.WEARABLE_OUT_OF_BATTERY:
                mFrameWearableOutOfBattery.setVisibility(View.VISIBLE);
                break;
            case States.StatusValue.NO_CONFIGURED_DEVICE:
                mFrameSproutlingOffline.setVisibility(View.VISIBLE);
                break;
        }
    }
}
