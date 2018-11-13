/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sproutling.BuildConfig;
import com.sproutling.R;
import com.sproutling.object.PushNotificationChannelEvent;
import com.sproutling.object.PushNotificationRegistrationEvent;
import com.sproutling.pojos.CreateHandheldResponse;
import com.sproutling.ui.widget.ShCustomProgressBar;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.sproutling.App.FISHER_PRICE_CHINA_FLAVOR;

/**
 * Created by bradylin on 3/10/17.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    public static final String FISHER_PRICE = "fisherPrice";
    private ShCustomProgressBar mShCustomProgressBar;
    private Toolbar mToolbar;
    private ShTextView mTitle;
    private ShTextView mActionButton;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShCustomProgressBar = new ShCustomProgressBar(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.logEvents(LogEvents.APP_IN_FOREGROUND);
    }

    protected void showProgressBar(boolean show) {

        if (show) {
            hideKeyboard();
            if (mShCustomProgressBar != null) mShCustomProgressBar.show();
        } else {
            if (mShCustomProgressBar != null && mShCustomProgressBar.isShowing()) {
                mShCustomProgressBar.dismiss();
            }
        }
    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void setUpPushNotification() {
        if (BuildConfig.FLAVOR_app.equalsIgnoreCase(FISHER_PRICE_CHINA_FLAVOR)) {
            //Baidu Push notification
            PushSettings.enableDebugMode(this, true);
            String pushApiKey = Utils.getMetaValue(this, "api_key");
            //start push notification
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, pushApiKey);
            Log.v(TAG, "bdpush PushEnabled: " + PushManager.isPushEnabled(getApplicationContext()));
        } else {
//            FCM push notification
            // Get updated InstanceID token.
            String pushToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Push token: " + pushToken);
            if (!TextUtils.isEmpty(pushToken)) {
                Utils.sendRegistrationToServer(this, pushToken);
            } else {
                onPushNotificationRegistrationFailure(null);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushNotificationChannelEvent pushNotificationChannelEvent) {
        Utils.sendRegistrationToServer(this, pushNotificationChannelEvent.getChannelId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushNotificationRegistrationEvent pushNotificationRegistrationEvent) {
        if (pushNotificationRegistrationEvent.isSuccess()) {
            onPushNotificationRegistrationSuccess(pushNotificationRegistrationEvent.getCreateHandheldResponse());
        } else {
            onPushNotificationRegistrationFailure(pushNotificationRegistrationEvent.getThrowable());
        }
    }


    protected void onPushNotificationRegistrationSuccess(CreateHandheldResponse createHandheldResponse) {


    }

    protected void onPushNotificationRegistrationFailure(Throwable t) {
        Log.e(TAG, "Error registering push notification");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.sendLogEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
        Utils.sendLogEvents();
    }

    protected void unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void initActionBar() {
        if (mToolbar != null) {
            mActionBar = getSupportActionBar();
            assert mActionBar != null;
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(true);
            mActionBar.setDisplayShowTitleEnabled(false);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }

    private void disableBack(){
        if(mActionBar != null){
            mActionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    protected void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.back_toolbar);
        if (mToolbar != null) {
            mTitle = (ShTextView) mToolbar.findViewById(R.id.toolbar_title);
            mActionButton = (ShTextView) mToolbar.findViewById(R.id.toolbar_menu_item);

            if (mActionButton != null) {
                ColorStateList colors = ContextCompat.getColorStateList(this, R.color.action_button);
                mActionButton.setTextColor(colors);
            }
            setSupportActionBar(mToolbar);
        }
    }

    protected void setActionButtonEnable(boolean enable) {
        if (mActionButton != null) {
            mActionButton.setEnabled(enable);
        }
    }

    protected void setActionMenuTitle(String actionMenuTitle) {
        if (mActionButton != null) {
            mActionButton.setText(actionMenuTitle);
        }
    }

    protected void setActionMenuClickListener(View.OnClickListener actionMenuClickListener) {
        if (mActionButton != null) {
            mActionButton.setOnClickListener(actionMenuClickListener);
        }
    }

    protected void setToolBarTitle(String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    protected void setBackDrawable(int backDrawable) {
        mActionBar.setHomeAsUpIndicator(backDrawable);
    }
}
