/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fuhu.states.interfaces.IStatePayload;
import com.fuhu.states.payloads.Payload;
import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.object.FragmentItem;
import com.sproutling.pipeline.MqttAPI;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.states.Actions;
import com.sproutling.states.States;
import com.sproutling.ui.dialogfragment.EOLUpdateDialogFragment;
import com.sproutling.ui.dialogfragment.SupportDialogFragment;
import com.sproutling.ui.fragment.BabyFragment;
import com.sproutling.ui.fragment.BabyFragment.OnBabyListener;
import com.sproutling.ui.fragment.Settings.SettingsAccountFragment;
import com.sproutling.ui.fragment.Settings.SettingsAccountFragment.OnMyAccountListener;
import com.sproutling.ui.fragment.Settings.SettingsCaregiversDetailFragment;
import com.sproutling.ui.fragment.Settings.SettingsCaregiversDetailFragment.OnCaregiverDetailListener;
import com.sproutling.ui.fragment.Settings.SettingsCaregiversFragment;
import com.sproutling.ui.fragment.Settings.SettingsCaregiversFragment.OnCaregiverListener;
import com.sproutling.ui.fragment.Settings.SettingsCaregiversQRCodeFragment;
import com.sproutling.ui.fragment.Settings.SettingsDeviceSettingsFragment;
import com.sproutling.ui.fragment.Settings.SettingsDeviceSettingsFragment.OnDeviceSettingsListener;
import com.sproutling.ui.fragment.Settings.SettingsHelpFragment;
import com.sproutling.ui.fragment.Settings.SettingsMainFragment;
import com.sproutling.ui.fragment.Settings.SettingsMainFragment.OnSettingsMainListener;
import com.sproutling.ui.fragment.Settings.SettingsNotificationFragment;
import com.sproutling.ui.fragment.Settings.SettingsNotificationFragment.OnNotificationListener;
import com.sproutling.ui.fragment.Settings.SettingsPasswordFragment;
import com.sproutling.ui.fragment.Settings.SettingsPasswordFragment.OnChangePasswordListener;
import com.sproutling.ui.fragment.Settings.SettingsWalkThroughFragment;
import com.sproutling.ui.fragment.Settings.SettingsWifiFragment;
import com.sproutling.ui.fragment.Settings.SettingsWifiPasswordFragment;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import sproutling.Hub;

import static com.sproutling.ui.activity.SetupActivity.SETTING_TYPE;
import static com.sproutling.ui.activity.SetupActivity.SETTING_TYPE_CHANGE;

/**
 * Created by bradylin on 12/12/16.
 */

public class SettingsActivity extends BaseMqttActivity implements OnSettingsMainListener, OnMyAccountListener, OnChangePasswordListener, OnBabyListener,
        OnCaregiverListener, OnCaregiverDetailListener, OnDeviceSettingsListener, OnNotificationListener, SupportDialogFragment.OnSupportDialogClickListener {

    private static final String TAG = "SettingsActivity";

    public static final String TOOLTIPS = "100000";

    public static final String EXTRA_STATUS_SCREEN_AVAILABLE = "extra_status_screen_available";
    private static final int WIFI_SETUP = 16;

    private Stack<String> mFragmentStack;

    private ImageView mBackView;
    private ShTextView mTitleView, mActionView, mWarningView;

    private boolean isBack;
    private String mFragmentTag;

    private boolean isStatusScreenAvailable;

//    private String mHubFirmwareVersion;
//    private String mHubSerialNumber;
//    private String mWearableFirmwareVersion;
//    private String mWearableSerialNumber;
//    private int mWearableBatteryValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mFragmentStack = new Stack<>();

        mTitleView = (ShTextView) findViewById(R.id.navigation_title);
        mBackView = (ImageView) findViewById(R.id.navigation_back);
        mBackView.setOnClickListener(mOnBackClickListener);
        mActionView = (ShTextView) findViewById(R.id.navigation_action);
        mActionView.setOnClickListener(mOnActionClickListener);
        mWarningView = (ShTextView) findViewById(R.id.warning);
        mWarningView.setOnClickListener(mOnWarningClickListener);

        try {
            ColorStateList colors = ContextCompat.getColorStateList(this, R.color.action_button_tealish);
            mActionView.setTextColor(colors);
        } catch (Exception e) {
            // handle exceptions
            mActionView.setTextColor(Utils.getColor(this, R.color.tealish));
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isStatusScreenAvailable = extras.getBoolean(EXTRA_STATUS_SCREEN_AVAILABLE, false);
        }

        updateUI(SettingsMainFragment.TAG, null);
        Utils.logEvents(LogEvents.SETTINGS_VIEWED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectMqtt();
    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        ArrayList<String> topics = new ArrayList<>();
        SSManagement.DeviceResponse device = SharedPrefManager.getDevice(this);
        if (device != null) topics.add(MqttAPI.getMqttUserConfig(device.getSerial()));
        return topics;
    }

    @Override
    public void onStateChanged(IStatePayload payload) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
    }

    View.OnClickListener mOnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            back();
        }
    };

    View.OnClickListener mOnActionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (mFragmentTag) {
                case SettingsAccountFragment.TAG:
                    updateAccount();
                    break;
                case SettingsPasswordFragment.TAG:
                    changePassword();
                    break;
                case BabyFragment.TAG:
                    updateBabyProfile();
                    break;
                case SettingsWifiPasswordFragment.TAG:
                    break;
                case SettingsDeviceSettingsFragment.TAG:

                    break;
            }
        }
    };

    private void back() {
        SettingsMainFragment fragment = (SettingsMainFragment)
                getFragmentManager().findFragmentByTag(SettingsMainFragment.TAG);
        if (fragment != null && fragment.isVisible()) {
            finish();
        } else {
            isBack = true;
            getFragmentManager().popBackStack();
            mFragmentStack.pop();
            if (mFragmentStack.isEmpty())
                finish();
            else
                updateUI(mFragmentStack.peek(), null);
        }
    }


    @Override
    public void onBackPressed() {
        back();
    }

    View.OnClickListener mOnWarningClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: warning
            Toast.makeText(SettingsActivity.this, "Warning", Toast.LENGTH_SHORT).show();
        }
    };

    void updateUI(String tag, Object param) {
        Fragment fragment;
        mFragmentTag = tag;
        switch (tag) {
            case SettingsMainFragment.TAG:
                fragment = SettingsMainFragment.newInstance();
                updateNavigationBar(R.string.settings_title);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case SettingsDeviceSettingsFragment.TAG:
                fragment = SettingsDeviceSettingsFragment.newInstance();
                updateNavigationBar(R.string.settings_device_settings_title);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case SettingsNotificationFragment.TAG:
                fragment = SettingsNotificationFragment.newInstance();
                updateNavigationBar(R.string.settings_notification_title);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case SettingsAccountFragment.TAG:
                fragment = SettingsAccountFragment.newInstance();
                updateNavigationBar(R.string.settings_account_title, R.string.settings_navigation_save, false, View.VISIBLE);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case SettingsPasswordFragment.TAG:
                fragment = SettingsPasswordFragment.newInstance();
                updateNavigationBar(R.string.settings_change_password_title, R.string.settings_navigation_save, false, View.VISIBLE);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case BabyFragment.TAG:
                fragment = BabyFragment.newInstance(BabyFragment.MODE_SETTINGS);
                updateNavigationBar(R.string.settings_baby_title, R.string.settings_navigation_save, false, View.VISIBLE);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case SettingsCaregiversFragment.TAG:
                fragment = SettingsCaregiversFragment.newInstance();
                updateNavigationBar(R.string.settings_caregivers_title);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case SettingsCaregiversDetailFragment.TAG:
                SSManagement.Child child = AccountManagement.getInstance(this).getChild();
                fragment = SettingsCaregiversDetailFragment.newInstance((SSManagement.UserAccountInfo) param, child.firstName);
                updateNavigationBar(R.string.settings_caregivers_title);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case SettingsCaregiversQRCodeFragment.TAG:
                fragment = SettingsCaregiversQRCodeFragment.newInstance();
                updateNavigationBar(R.string.settings_caregivers_title);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case SettingsWifiFragment.TAG:
//                fragment = SettingsWifiFragment.newInstance();
//                updateNavigationBar(R.string.settings_wifi_title);
                showWifiSetup();
                break;
            case SettingsWifiPasswordFragment.TAG:
                fragment = SettingsWifiPasswordFragment.newInstance();
                updateNavigationBar(R.string.settings_wifi_title, R.string.settings_navigation_join, false, View.VISIBLE);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case SettingsHelpFragment.TAG:
//                fragment = SettingsHelpFragment.newInstance();
//                updateNavigationBar(R.string.settings_help_title);
//                if (!isBack) updateFragment(fragment, tag);

                SSManagement.EOLData eolData = AccountManagement.getInstance(SettingsActivity.this).readEOLData();
                if(eolData != null){
                    showSupportDialog();
                } else{
                    showHelp();
                }
                break;
            case SettingsWalkThroughFragment.TAG:
                setResult(Integer.valueOf(TOOLTIPS));
                finish();
                break;
            default:
                fragment = SettingsMainFragment.newInstance();
                updateNavigationBar(R.string.settings_title);
                if (!isBack) updateFragment(fragment, tag);
        }
        isBack = false;
    }

    private void showHelp() {
        App.getInstance().openHelp(this);
    }

    private void showSupportDialog(){
        SSManagement.EOLData eolData = AccountManagement.getInstance(SettingsActivity.this).readEOLData();
        if(eolData != null){
            SupportDialogFragment.newInstance(eolData.settingsPopUpSupportWebsite, getString(R.string.dialog_contact_support),
                    getString(R.string.dialog_contact_support_message).replace("^1", eolData.settingsPopUpEmail),getString(R.string.dialog_email_support), getString(R.string.dialog_visit_website), getString(R.string.cancel), eolData.settingsPopUpEmail).show(getFragmentManager(), null);
        }
    }

    private void showWifiSetup() {
        Intent intent = new Intent(this, SetupActivity.class);
        intent.putExtra(SETTING_TYPE, SETTING_TYPE_CHANGE);
        startActivityForResult(intent, WIFI_SETUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WIFI_SETUP) {
            if (resultCode == Activity.RESULT_OK) {
                //if wifi setup is successful, then take the user to Status view screen.
                finish();
            }
        }
    }

    void updateBackButton(int titleId) {
        mBackView.setVisibility(View.VISIBLE);
        if (titleId == R.string.settings_title) {
            mBackView.setVisibility((SharedPrefManager.getDevice(this) != null || isStatusScreenAvailable) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    void updateNavigationBar(int titleId) {
        updateBackButton(titleId);
        mTitleView.setText(titleId);
        mActionView.setVisibility(View.INVISIBLE);
    }

    void updateNavigationBar(int titleId, int actionId, boolean enabled, int visibility) {
        updateBackButton(titleId);
        mTitleView.setText(titleId);
        mActionView.setText(actionId);
        mActionView.setEnabled(enabled);
        mActionView.setVisibility(visibility);
    }

    void updateFragment(Fragment fragment, String tag) {
        mFragmentStack.push(tag);
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    public void updateAccount() {
        SettingsAccountFragment fragment = (SettingsAccountFragment)
                getFragmentManager().findFragmentByTag(SettingsAccountFragment.TAG);

        if (fragment != null) {
            fragment.updateAccount();
        } else {
            updateFragment(new SettingsAccountFragment(), SettingsAccountFragment.TAG);
        }
    }

    public void updateBabyProfile() {
        BabyFragment fragment = (BabyFragment)
                getFragmentManager().findFragmentByTag(BabyFragment.TAG);

        if (fragment != null) {
            fragment.updateBaby();
        } else {
            updateFragment(BabyFragment.newInstance(BabyFragment.MODE_SETTINGS), BabyFragment.TAG);
        }
    }

    public void changePassword() {
        SettingsPasswordFragment fragment = (SettingsPasswordFragment)
                getFragmentManager().findFragmentByTag(SettingsPasswordFragment.TAG);

        if (fragment != null) {
            fragment.changePassword();
        } else {
            updateFragment(new SettingsPasswordFragment(), SettingsPasswordFragment.TAG);
        }
    }

    void showWarning(int visibility) {
        mWarningView.setVisibility(visibility);
    }

    private void updateSettings(final SSManagement.PushNotification settings) {
        new AsyncTask<Void, Void, SSManagement.UserAccountInfo>() {
            AccountManagement account;
            SSManagement.User user;
            SSManagement.UserAccountInfo userAccountInfo;

            SSError mError;

            @Override
            protected void onPreExecute() {
                account = AccountManagement.getInstance(SettingsActivity.this);
                user = account.getUser();
                userAccountInfo = account.getUserAccountInfo();
            }

            @Override
            protected SSManagement.UserAccountInfo doInBackground(Void... params) {
                try {
                    return SKManagement.updateAlert(user.accessToken, userAccountInfo.id, settings.getSettingsAPIJSON());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.UserAccountInfo userAccountInfo) {
                if (userAccountInfo != null) {
                    AccountManagement.getInstance(SettingsActivity.this).writeUserAccountInfo(userAccountInfo);
                }
            }
        }.execute();
    }

    @Override
    public void onFragmentSelected(FragmentItem item) {
        updateUI(item.getTag(), null);
    }

    @Override
    public void onActionButtonEnabled(String tag, boolean enabled) {
        mActionView.setEnabled(enabled);
        mFragmentTag = tag;
    }

    @Override
    public void onChildCreated(boolean created, String ownerId) {
    }

    @Override
    public void onPasswordChanged(boolean changed) {
        if (changed) back();
    }

    @Override
    public void onChangePasswordClicked() {
        updateUI(SettingsPasswordFragment.TAG, null);
    }

//    @Override
//    public void onAccountErase() {
//        new AsyncTask<Void, Void, Boolean>() {
//            SSError mError;
//
//            @Override
//            protected Boolean doInBackground(Void... params) {
//                try {
//                    SSManagement.User account = AccountManagement.getInstance(SettingsActivity.this).getUser();
//                    SKManagement.deleteUserById(account.accessToken, account.resourceOwnerId);
//                    return true;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (SSException e) {
//                    e.printStackTrace();
//                    mError = e.getError();
//                }
//                return false;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean result) {
//                if (result) {
//                    Toast.makeText(SettingsActivity.this, "Account Deleted!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(SettingsActivity.this, "Account Deletion Failed!", Toast.LENGTH_SHORT).show();
////                    if (mError != null) handleError(mError);
//                }
//            }
//        }.execute();
//    }

    @Override
    public void onDeviceRemoved(boolean removed) {
        if (removed) {
            App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.DEVICE_SERIAL, States.Value.DEVICE_NULL));
            disconnectMqtt();
            SharedPrefManager.clearDevice(this);
            back();
            SettingsMainFragment fragment = (SettingsMainFragment)
                    getFragmentManager().findFragmentByTag(SettingsMainFragment.TAG);
            if (fragment != null && fragment.isVisible()) {
                fragment.refresh();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.settings_fragment);
        if (currentFragment != null) {
            currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onHubSettingsUpdated() {
//        back();
    }

//    @Override
//    public void onAlertSwitched(SSManagement.PushNotification settings) {
//        updateSettings(settings);
//    }

    @Override
    public void onChangeWifiSelected() {
        showWifiSetup();
    }

    @Override
    public void onSignOut() {
        Utils.logEvents(LogEvents.SETTINGS_ACCOUNT_LOGGED_OUT);
//        startActivity(new Intent(SettingsActivity.this, MainActivity.class)
//                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
//                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        finish();
        if (StatusActivity.sInstance != null) StatusActivity.sInstance.finish();
        Intent intent = IntentCompat.makeRestartActivityTask(new ComponentName(getPackageName(), MainActivity.class.getName()));
        startActivity(intent);
        finish();
    }

    @Override
    public void onAccountUpdated(boolean updated) {
        if (updated) {
            Utils.logEvents(LogEvents.SETTINGS_ACCOUNT_SAVED);
            back();
        }
    }

    @Override
    public void onBabyUpdated(boolean updated) {
        if (updated) {
            Utils.logEvents(LogEvents.UPDATED_CHILD);
            back();
        }
    }

    @Override
    public void onCaregiverRemoved(boolean removed) {
        if (removed) back();
    }

    @Override
    public void onCaregiverSelected(SSManagement.UserAccountInfo caregiver) {
        updateUI(SettingsCaregiversDetailFragment.TAG, caregiver);
    }

    @Override
    public void onQRGenerateClicked() {
        updateUI(SettingsCaregiversQRCodeFragment.TAG, null);
    }

    @Override
    public void onNotificationSwitched(SSManagement.PushNotification settings) {
        updateSettings(settings);
    }

//    @Override
//    protected void onHubStatusTopicEvent(HubStatusTopicEvent hubStatusTopicEvent) {
//        super.onHubStatusTopicEvent(hubStatusTopicEvent);
//        Hub.HubStatus hubStatus = hubStatusTopicEvent.getHubStatus();
//
//        mHubFirmwareVersion = Utils.getVersion(hubStatus.getFirmwareVersion());
//        SSManagement.DeviceResponse device = SharedPrefManager.getDevice(this);
//        if (device != null) mHubSerialNumber = device.getSerial();
//    }

//    @Override
//    protected void onHubBandStatusTopicEvent(HubBandStatusTopicEvent hubBandStatusTopicEvent) {
//        super.onHubBandStatusTopicEvent(hubBandStatusTopicEvent);
//        Hub.BandStatus bandStatus = hubBandStatusTopicEvent.getBandStatus();
//
//        mWearableFirmwareVersion = Utils.getVersion(bandStatus.getFirmwareVersion());
//        mWearableBatteryValue = Utils.getBatteryValue(bandStatus.getBatteryVoltage());
////        mWearableSerialNumber = ;
//    }

    @Override
    protected void onHubUserConfiguration(Hub.HubUserConfiguration hubUserConfiguration) {
        super.onHubUserConfiguration(hubUserConfiguration);
        App.getInstance().setHubUserConfiguration(hubUserConfiguration);
    }

    @Override
    public void onVisitWebsiteClicked(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
