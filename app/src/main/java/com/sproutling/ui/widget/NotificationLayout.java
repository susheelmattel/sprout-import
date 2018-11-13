/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sproutling.R;
import com.sproutling.ui.activity.ExpectationsActivity;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

/**
 * Created by Xylon on 2017/1/17.
 */

public class NotificationLayout extends LinearLayout {

    private RelativeLayout mNotificationView;
    private ShTextView mTxtTitle, mTxtDescription;
    private ImageView mImgTitle;
    private static String sNowType;
    private static boolean sLowBatteryCheck = false;
    private String mName;
    private String mRollOverTime;

    public interface Type {
        String LOW_BATTERY = "lowBattery";
        String WEAK_BLUETOOTH = "weakBluetooth";
        String WEAK_SIGNAL = "weakSignal";
        String TIMELINE = "timeLine";
        String ADVICE = "advice01";
        String ADVICE_LOG_SLEEP = "adviceLogSleep";
        String TEMPERATURE = "temperature";
        String EXPECTATIONS = "expectations";
        String ROLLOVER = "rollover";
    }

    public NotificationLayout(Context context) {
        super(context);
        init(context);
    }

    public NotificationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NotificationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        LinearLayout mainContent = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_notification, this);
        mNotificationView = (RelativeLayout) mainContent.findViewById(R.id.notificationView);
        mTxtTitle = (ShTextView) mainContent.findViewById(R.id.txtTitle);
        mTxtDescription = (ShTextView) mainContent.findViewById(R.id.txtDescription);
        mImgTitle = (ImageView) mainContent.findViewById(R.id.imgTitle);
        sNowType = Type.EXPECTATIONS;
    }

    public void show(String type) {
        // expectations is lowest priority
        switch (type) {
            case Type.LOW_BATTERY:
                if (Type.ADVICE_LOG_SLEEP.equals(sNowType)) return;
                sLowBatteryCheck = true;
                setNotificationFields(R.drawable.ic_sc_tip_battery, R.string.status_notification_title_battery,
                        R.string.status_notification_description_battery);
                break;
            case Type.WEAK_SIGNAL:
                setNotificationFields(R.drawable.ic_sc_tip_signal, R.string.status_notification_title_wifi,
                        R.string.status_notification_description_wifi);
                break;
            case Type.WEAK_BLUETOOTH:
                setNotificationFields(R.drawable.ic_sc_tip_bluetooth, R.string.status_notification_title_bluetooth,
                        R.string.status_notification_description_bluetooth);
                break;
            case Type.TIMELINE:
                setNotificationFields(R.drawable.ic_sc_tip_timeline, R.string.status_notification_title_timeline,
                        R.string.status_notification_description_timeline);
                break;
            case Type.ADVICE:
                setNotificationFields(R.drawable.ic_sc_tip_advice, R.string.status_notification_title_advice_01,
                        R.string.status_notification_description_advice_02);
                break;
            case Type.ADVICE_LOG_SLEEP:
                setNotificationFields(R.drawable.ic_sc_tip_advice, R.string.status_notification_title_advice_02,
                        R.string.status_notification_description_advice_01);
                break;
            case Type.TEMPERATURE:
                setNotificationFields(R.drawable.ic_sc_tip_temperature, R.string.status_notification_title_temperature,
                        R.string.status_notification_description_temperature);
                break;
            case Type.ROLLOVER:
                setNotificationFields(R.drawable.ic_sc_tip_rollover, R.string.status_notification_title_rollover,
                        String.format(getContext().getString(R.string.status_notification_description_rollover), mName, mRollOverTime));
                break;
            case Type.EXPECTATIONS:
            default:
                if (!Type.EXPECTATIONS.equals(sNowType)) return;
                mImgTitle.setImageResource(R.drawable.ic_sc_tip_advice);
                mTxtTitle.setText(R.string.status_notification_title_expectations);
                setSpannableText(mTxtDescription, R.string.status_notification_description_expectations, R.string.status_notification_description_expectations_spannable);
                break;
        }
        sNowType = type;
        mNotificationView.setVisibility(View.VISIBLE);
    }

    public void show(String type, String name, String time) {
        if (Type.ROLLOVER.equals(type)) {
            mName = name;
            mRollOverTime = time;
            show(type);
        }
    }

    private void setNotificationFields(int image, int title, int description){
        mImgTitle.setImageResource(image);
        mTxtTitle.setText(title);
        mTxtDescription.setText(description);
    }

    private void setNotificationFields(int image, int title, String description){
        mImgTitle.setImageResource(image);
        mTxtTitle.setText(title);
        mTxtDescription.setText(description);
    }

    public void restore() {
        if (sNowType != null) show(sNowType);
    }

    public void dismiss() {
        if (Type.LOW_BATTERY.equalsIgnoreCase(sNowType)) {
            sLowBatteryCheck = false;
        }

        mNotificationView.setVisibility(View.GONE);
    }

    public void dismissLowBattery() {
        sLowBatteryCheck = false;
        setToDefault();
    }

    public void setToDefault() {
        show(sNowType = Type.EXPECTATIONS);
    }

    public String getType() {
        return sNowType;
    }

    public boolean isNeedToShowLowBattery() {
        return sLowBatteryCheck;
    }

    public boolean isShowing() {
        return mNotificationView.getVisibility() == View.VISIBLE;
    }

    private void setSpannableText(TextView view, int textId, int spannableTextId) {
        String text = getContext().getString(textId);
        final String spannableText = getContext().getString(spannableTextId);

        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Utils.logEvents(LogEvents.TIP_CARD_CLICK);
                getContext().startActivity(new Intent(getContext(), ExpectationsActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setTypeface(Typeface.DEFAULT_BOLD);
                ds.setColor(getContext().getResources().getColor(R.color.tealish));
            }
        };
        int index = text.indexOf(spannableText);
        ss.setSpan(clickableSpan, index, index + spannableText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        view.setText(ss);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setHighlightColor(Color.TRANSPARENT);
    }
}
