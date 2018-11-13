/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fuhu.pipeline.manager.PipelineManager;
import com.fuhu.pipeline.mqtt.MqttAction;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.fuhu.states.interfaces.IStatePayload;
import com.google.protobuf.Timestamp;
import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.object.BleConnectionEvent;
import com.sproutling.object.ConnectionReadWriteErrorEvent;
import com.sproutling.object.DeviceSerialEvent;
import com.sproutling.object.PublicKeyEvent;
import com.sproutling.object.TimeSpent;
import com.sproutling.object.WifiItem;
import com.sproutling.object.WifiNetworkTypeEvent;
import com.sproutling.object.WifiParams;
import com.sproutling.pipeline.MqttAPI;
import com.sproutling.pipeline.callback.PublishMqttMessageCallback;
import com.sproutling.pipeline.taskList.BaseMqttTaskList;
import com.sproutling.pojos.CreateHandheldResponse;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SHBluetoothLeService;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.dialogfragment.DeviceErrorDialogFragment;
import com.sproutling.ui.dialogfragment.InternetOfflineDialogFragment;
import com.sproutling.ui.fragment.BabyFragment;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.fragment.Settings.SettingsWifiSetUpFragment;
import com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment;
import com.sproutling.ui.fragment.Setup.ManualSetupWiFiFragment;
import com.sproutling.ui.fragment.Setup.SetupAccountCreationFragment;
import com.sproutling.ui.fragment.Setup.SetupConnectDeviceFragment;
import com.sproutling.ui.fragment.Setup.SetupHowToChargeFragment;
import com.sproutling.ui.fragment.Setup.SetupHubNotFoundFragment;
import com.sproutling.ui.fragment.Setup.SetupLocationAndBleAccessFragment;
import com.sproutling.ui.fragment.Setup.SetupPasswordFragment;
import com.sproutling.ui.fragment.Setup.SetupPuttingOnFragment;
import com.sproutling.ui.fragment.Setup.SetupSettingUpFragment;
import com.sproutling.ui.fragment.Setup.SetupWiFiFragment;
import com.sproutling.ui.fragment.Setup.SetupWifiSuccessFragment;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import sproutling.Hub;

import static com.sproutling.utils.LogEvents.HUB_PAIRING_SUCCESS;
import static com.sproutling.utils.LogEvents.HUB_REGISTERED;
import static com.sproutling.utils.LogEvents.HUB_UPDATED_WIFI_SETTINGS;
import static com.sproutling.utils.LogEvents.PUTTING_ON_NEXT;


public class SetupActivity extends BaseMqttActivity implements SetupAccountCreationFragment.OnAccountCreationListener,
        BabyFragment.OnBabyListener, SetupConnectDeviceFragment.OnConnectDeviceListener,
        SetupWiFiFragment.OnChooseWiFiListener, SetupPasswordFragment.SetPasswordListener,
        ManualSetupWiFiFragment.ManualSetupListener, ManualNetworkTypeFragment.NetworkTypeSelectListener,
        SetupSettingUpFragment.OnSettingUpListener, SetupHubNotFoundFragment.OnHubNotFoundListener, SetupLocationAndBleAccessFragment.LocBleListener, SettingsWifiSetUpFragment.WifiSettingsSetupListener {
    public static final String TAG = "SetupActivity";
    public static final String EXTRA_CAREGIVER_DATA = "extra_caregiver_data";
    public static final String EXTRA_SETUP_SKIP = "extra_setup_skip";
    public static final String EXTRA_SETUP_DEVICE = "extra_setup_device";
    public static final String EXTRA_SETUP_CHILD = "extra_setup_child";
    public static final String EXTRA_SETTING_SETUP_NEW_DEVICE = "EXTRA_SETTING_SETUP_NEW_DEVICE";
    public static final String SETTING_TYPE = "SETTING_TYPE";
    public static final String SETTING_TYPE_SET_UP = "SETTING_TYPE_SET_UP";
    public static final String SETTING_TYPE_CHANGE = "SETTING_TYPE_CHANGE";
    static final int STEP_ACCOUNT_CREATION = 1;
    static final int STEP_ADD_CHILD = 2;
    static final int STEP_SETTING_UP = 3;
    static final int STEP_LOCATION_ACCESS = 4;
    static final int STEP_LOCATION_ACCESS_DENIED = 5;
    static final int STEP_LOCATION_OFF = 6;
    static final int STEP_BLUETOOTH_NOT_ON = 8;
    static final int STEP_CONNECT_DEVICE = 9;
    static final int STEP_SETUP_WIFI = 10;
    static final int STEP_SETUP_PASSWORD = 11;
    static final int STEP_MANUAL_WIFI_SETUP = 12;
    static final int STEP_MANUAL_NETWORK_TYPE_SETUP = 13;
    static final int STEP_DEVICE_NOT_FOUND = 14;
    //    static final int STEP_HOW = 7;
    static final int STEP_WIFI_SUCCESS = 15;
    static final int STEP_PUTTING_ON = 16;
    //    static final int STEP_ALERT = 16;
    static final int STEP_HOW_TO_CHARGE = 17;
    static final int INDICATOR_ONE = 1;
    static final int INDICATOR_TWO = 2;
    static final int INDICATOR_THREE = 3;
    static final int INDICATOR_FOUR = 4;
    static final int INDICATOR_FIVE = 5;
    static final int INDICATOR_SIX = 6;
    private static WeakReference<SetupActivity> sActivity = null;
    private String mAccountType = SSManagement.TYPE_GUARDIAN;
    private ConnectivityManager mConnectivityManager;
    private boolean isVisible;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
            } else {
                final SetupActivity setupActivity = sActivity.get();
                if (setupActivity != null && !setupActivity.isFinishing() && setupActivity.isVisible) {
                    InternetOfflineDialogFragment
                            .newInstance()
                            .show(getFragmentManager(), null);
                }
            }
        }
    };
    private boolean mSkip;
    private boolean mSkipToDeviceSetup;
    private boolean mSkipToChildSetup;
    private boolean mIsSettingsNewSetup;
    private boolean mHasDevice = true;
    private ShTextView mTitleView;
    private ShTextView mActionButton;
    private ImageView mBackButton;
    private View mStepProgresses[];
    private int mStepIndex;
    private LinearLayout mProgressLayout;
    private RelativeLayout mNavigationBar;
    private View mActionBarDivider;
    //    ProgressBar mProgressBar;
    private BaseFragment mCurrentFragment;
    private SHBluetoothLeService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((SHBluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private boolean mConnected = false;
    // Handles various mEvents fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (SHBluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Log.i(TAG, "BLE connection success");
                Utils.logEvents(HUB_PAIRING_SUCCESS);
            } else if (SHBluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.i(TAG, "BLE connection ACTION_GATT_DISCONNECTED");

            } else if (SHBluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "BLE connection ACTION_GATT_SERVICES_DISCOVERED");
                getPublicKey();
//                updateUIAndGotoWifiSetup();

            } else if (SHBluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.i(TAG, "BLE connection ACTION_DATA_AVAILABLE");
            }
        }
    };
    private WifiItem mSelectedWifiSsid;
    private String mSelectedNetworkType = ManualNetworkTypeFragment.WPA;
    private String mSettingType;
    View.OnClickListener mOnBackButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            back();
        }
    };
    private String mOwnerID;
    private String mDeviceSerialID;
    private SSManagement.DeviceResponse mDeviceResponse;
    private Hub.HubUserConfiguration mHubUserConfiguration;
    private long mOnboardingStartTime;
    View.OnClickListener mOnActionButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (mStepIndex) {
                case STEP_ACCOUNT_CREATION:
                    createAccount();
                    break;
                case STEP_ADD_CHILD:
                    createChild();
                    break;
                case STEP_SETTING_UP:
                    goToConnectDevice();
                    break;
                case STEP_DEVICE_NOT_FOUND:
                    setStep(STEP_CONNECT_DEVICE);
                    break;
                case STEP_CONNECT_DEVICE:
                    SetupConnectDeviceFragment setupConnectDeviceFragment = (SetupConnectDeviceFragment) getFragmentManager().findFragmentByTag(SetupConnectDeviceFragment.TAG);
                    setupConnectDeviceFragment.startBleScanning();
                    mActionButton.setEnabled(false);
                    break;
                case STEP_SETUP_WIFI:
                    // send back to hub
//                    SetupPasswordFragment setupPasswordFragment = (SetupPasswordFragment) mCurrentFragment;
//                    joinToWifi(mSelectedWifiSsid, setupPasswordFragment.getPassword());
//                    mActionButton.setEnabled(false);
                    break;
                case STEP_SETUP_PASSWORD:
                    // join
                    SetupPasswordFragment setupPasswordFragment = (SetupPasswordFragment) getFragmentManager().findFragmentByTag(SetupPasswordFragment.TAG);
                    joinToWifi(mSelectedWifiSsid.getSsid(), setupPasswordFragment.getPassword(), mSelectedWifiSsid.getNetworkSecurityType(), false);
//                    mActionButton.setEnabled(false);
                    break;
                case STEP_WIFI_SUCCESS:
                    if (mSettingType.equalsIgnoreCase(SETTING_TYPE_SET_UP))
                        setStep(STEP_PUTTING_ON);
                    else {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                    break;
                case STEP_PUTTING_ON:
                    Utils.logEvents(PUTTING_ON_NEXT);
                    setStep(STEP_HOW_TO_CHARGE);
                    break;
                case STEP_HOW_TO_CHARGE:
                    Utils.logEvents(LogEvents.HOW_TO_CHARGE_NEXT);
                    finishOnboarding();
                    break;
                case STEP_MANUAL_WIFI_SETUP:
                    joinToWifiViaManualSetup();
                    break;

                default:
                    break;
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SHBluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(SHBluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(SHBluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(SHBluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void getDeviceSerialNumber() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.getDeviceSerialNumber();
        }
    }

    private void getPublicKey() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.getPublicKey();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        sActivity = new WeakReference<>(this);
        //Pass the setting type to show default device setup or device setup from settings
        mSettingType = SETTING_TYPE_SET_UP;
        if (getIntent().getExtras() != null) {
            mSettingType = getIntent().getExtras().getString(SETTING_TYPE, SETTING_TYPE_SET_UP);
            mSkip = getIntent().getExtras().getBoolean(EXTRA_SETUP_SKIP, false);
            mSkipToDeviceSetup = getIntent().getExtras().getBoolean(EXTRA_SETUP_DEVICE, false);
            mSkipToChildSetup = getIntent().getExtras().getBoolean(EXTRA_SETUP_CHILD, false);
            mIsSettingsNewSetup = getIntent().getExtras().getBoolean(EXTRA_SETTING_SETUP_NEW_DEVICE, false);
        }

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver, filter);

        mTitleView = (ShTextView) findViewById(R.id.navigation_title);
        mActionButton = (ShTextView) findViewById(R.id.navigation_action);
        mActionButton.setOnClickListener(mOnActionButtonClickListener);
        mBackButton = (ImageView) findViewById(R.id.navigation_back);
        mBackButton.setOnClickListener(mOnBackButtonClickListener);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressLayout = (LinearLayout) findViewById(R.id.step_progress_layout);
        mNavigationBar = (RelativeLayout) findViewById(R.id.navigation_layout);
        mActionBarDivider = findViewById(R.id.navigation_divider);

        View stepProgress1 = findViewById(R.id.step_progress_1);
        View stepProgress2 = findViewById(R.id.step_progress_2);
        View stepProgress3 = findViewById(R.id.step_progress_3);
        View stepProgress4 = findViewById(R.id.step_progress_4);
        View stepProgress5 = findViewById(R.id.step_progress_5);
        View stepProgress6 = findViewById(R.id.step_progress_6);

        mStepProgresses = new View[]{stepProgress1, stepProgress2, stepProgress3, stepProgress4, stepProgress5, stepProgress6};
        Intent gattServiceIntent = new Intent(this, SHBluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        setUpUI();
    }

    private void setUpUI() {
        if (SETTING_TYPE_CHANGE.equalsIgnoreCase(mSettingType)) {
            setupColorChange();
            setupChange();
        } else {
            if (mSkip || mSkipToDeviceSetup) setupColorChange();
            else setupColor();
            setup();
            mOnboardingStartTime = System.currentTimeMillis();
        }
    }

    private void setupColorChange() {
        mProgressLayout.setVisibility(View.GONE);
        mNavigationBar.setBackgroundColor(Utils.getColor(this, R.color.white));
        mTitleView.setTextColor(Utils.getColor(this, R.color.brownishGrey));
        mBackButton.setImageResource(R.drawable.ic_android_back_green);
        mActionBarDivider.setVisibility(View.VISIBLE);
        try {
            ColorStateList colors = ContextCompat.getColorStateList(this, R.color.action_button_tealish);
            mActionButton.setTextColor(colors);
        } catch (Exception e) {
            // handle exceptions
            mActionButton.setTextColor(Utils.getColor(this, R.color.tealish));
        }
    }

    private void setupColor() {
        mProgressLayout.setVisibility(View.VISIBLE);
        mNavigationBar.setBackgroundColor(Utils.getColor(this, R.color.tealish));
        mTitleView.setTextColor(Utils.getColor(this, R.color.white));
        mBackButton.setImageResource(R.drawable.ic_android_back_white);
        mActionBarDivider.setVisibility(View.INVISIBLE);
        try {
            ColorStateList colors = ContextCompat.getColorStateList(this, R.color.action_button);
            mActionButton.setTextColor(colors);
        } catch (Exception e) {
            // handle exceptions
            mActionButton.setTextColor(Utils.getColor(this, R.color.white));
        }
    }

    private void setupChange() {
        setStep(STEP_SETTING_UP);
    }

    private void setup() {
        if (mSkipToChildSetup) goToChildSetup();
        else if (mSkip) goToSettingUp();
        else if (mSkipToDeviceSetup) goToConnectDevice();
        else setStep(STEP_ACCOUNT_CREATION);
    }

    private void showBackButton(boolean show) {
        mBackButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        if (intent != null && intent.getScheme() != null) {
            if (intent.getScheme().equals(getString(R.string.setup_account_creation_tos_scheme))) {
                Intent mIntent = new Intent(this, TermsActivity.class);
                mIntent.putExtra(TermsActivity.EXTRA_TITLE, getString(R.string.tos_title_terms));
                startActivity(mIntent);
            } else if (intent.getScheme().equals(getString(R.string.setup_account_creation_pp_scheme))) {
                Intent mIntent = new Intent(this, TermsActivity.class);
                mIntent.putExtra(TermsActivity.EXTRA_TITLE, getString(R.string.tos_title_pp));
                startActivity(mIntent);
            } else if (intent.getScheme().equals(getString(R.string.setup_account_creation_email_scheme))) {
                Intent mIntent = new Intent(this, LoginActivity.class);
                startActivity(mIntent);
                finish();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        connectMqtt();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void goToChildSetup() {
        setStep(STEP_ADD_CHILD);
    }

    private void goToConnectDevice() {
        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) &&
                (!Utils.isLocationPermissionGranted(SetupActivity.this) || !Utils.isGPSEnabled(SetupActivity.this))) {
            setStep(STEP_LOCATION_ACCESS);
        } else {
            setStep(STEP_CONNECT_DEVICE);
        }
    }

    private void finishOnboarding() {
        if (mHasDevice) {
            long elapsedTimeMillis = System.currentTimeMillis() - mOnboardingStartTime;
            String jsonString = Utils.toJsonString(new TimeSpent((elapsedTimeMillis / 1000F)));
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                Utils.logEvents(LogEvents.ONBOARDING_TIME_SPENT, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            goToStatusScreen();
        } else {
            goToSettings();
        }
    }

    private void transitionToActivity(String className) {
        Intent intent = IntentCompat.makeRestartActivityTask(new ComponentName(getPackageName(), className));
        startActivity(intent);
        finish();
    }

    private void goToStatusScreen() {
        transitionToActivity(StatusActivity.class.getName());
    }

    private void goToSettings() {
        transitionToActivity(SettingsActivity.class.getName());
    }

    private void joinToWifiViaManualSetup() {
        ManualSetupWiFiFragment manualSetupWiFiFragment = (ManualSetupWiFiFragment) getFragmentManager().findFragmentByTag(ManualSetupWiFiFragment.TAG);
        if (manualSetupWiFiFragment != null) {
            WifiParams wifiParams = manualSetupWiFiFragment.getWifiParams();
            Log.d(TAG, "setting up wifi manually..");
            joinToWifi(wifiParams.getSsid(), wifiParams.getPwd(), wifiParams.getSecurity(), true);
        }
    }

    private void joinToWifi(String selectedWifiSsid, String pwd, @ManualNetworkTypeFragment.NetworkSecurityType String securityType, boolean isManualSetup) {
        hideKeyboard();
        if (TextUtils.isEmpty(pwd)) return;
        if (mConnected) {
            Log.d(TAG, "BLE GATT is connected when setting up wifi");
            if (mBluetoothLeService != null) {
                if (mCurrentFragment != null) mCurrentFragment.showProgressBar(true);
                mBluetoothLeService.setUpWifi(selectedWifiSsid, pwd, securityType, isManualSetup);
            }
        } else {
            Log.d(TAG, "BLE GATT is disconnected when setting up wifi");
            if (mBluetoothLeService != null) {
//                    mBluetoothLeService.reConnect();
            }
        }
    }

    private void back() {
        if (mSettingType.equalsIgnoreCase(SETTING_TYPE_SET_UP)) {
            if (isCurrentFragmentWifiSetupSuccess() || isCurrentFragmentPuttingOn() || isCurrentFragmentHowToCharge() || isCurrentFragmentWifiSetup()) {
                return;
            }
            if (!mIsSettingsNewSetup) {
                if (mSkip || mSkipToDeviceSetup) {
                    finish();
                    return;
                }
            }
            SetupAccountCreationFragment fragment = (SetupAccountCreationFragment)
                    getFragmentManager().findFragmentByTag(SetupAccountCreationFragment.TAG);
            if (fragment != null && fragment.isVisible()) {
                finish();
            } else {
                if (mStepIndex == STEP_CONNECT_DEVICE || mStepIndex == STEP_SETUP_PASSWORD ||
                        mStepIndex == STEP_MANUAL_WIFI_SETUP || mStepIndex == STEP_MANUAL_NETWORK_TYPE_SETUP || mStepIndex == STEP_DEVICE_NOT_FOUND) {
                    if (mStepIndex == STEP_CONNECT_DEVICE && (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)) {
                        getFragmentManager().popBackStack();
                        setStep(getBackedStep(), false);
                    } else if (mStepIndex == STEP_DEVICE_NOT_FOUND) {
                        setStep(getBackedStep(), false);
                    } else {
                        getFragmentManager().popBackStack();
                        setStep(getBackedStep(), true);
                    }
                } else if (mStepIndex == STEP_HOW_TO_CHARGE) {
                    setStep(getBackedStep(), false);
                } else {
                    finish();
//                    getFragmentManager().popBackStack();
//                    setStep(getBackedStep());
                }
            }
        } else {
            SettingsWifiSetUpFragment fragment = (SettingsWifiSetUpFragment) getFragmentManager().findFragmentByTag(SettingsWifiSetUpFragment.TAG);
            if (fragment != null && fragment.isVisible()) {
                finish();
            } else {
                if (isCurrentFragmentWifiSetupSuccess()) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();

                } else {
                    getFragmentManager().popBackStack();
                    int backStep = getWifiSettingsBackedStep();
                    if (backStep == STEP_SETTING_UP) {
                        mBluetoothLeService.disconnect(true);
                    }
                    setStep(backStep, false);
                }
            }
        }
    }

    private boolean isCurrentFragmentPuttingOn() {
        SetupPuttingOnFragment setupWifiSuccessFragment = (SetupPuttingOnFragment) getFragmentManager().findFragmentByTag(SetupPuttingOnFragment.TAG);
        if (setupWifiSuccessFragment != null && setupWifiSuccessFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCurrentFragmentHowToCharge() {
        SetupHowToChargeFragment setupWifiSuccessFragment = (SetupHowToChargeFragment) getFragmentManager().findFragmentByTag(SetupHowToChargeFragment.TAG);
        if (setupWifiSuccessFragment != null && setupWifiSuccessFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCurrentFragmentWifiSetup() {
        SetupWiFiFragment setupWiFiFragment = (SetupWiFiFragment) getFragmentManager().findFragmentByTag(SetupWiFiFragment.TAG);
        if (setupWiFiFragment != null && setupWiFiFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCurrentFragmentWifiSetupSuccess() {
        SetupWifiSuccessFragment setupWifiSuccessFragment = (SetupWifiSuccessFragment) getFragmentManager().findFragmentByTag(SetupWifiSuccessFragment.TAG);
        if (setupWifiSuccessFragment != null && setupWifiSuccessFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    private int getWifiSettingsBackedStep() {
        switch (mStepIndex) {
            case STEP_CONNECT_DEVICE:
                return STEP_SETTING_UP;
            case STEP_SETUP_WIFI:
                return STEP_SETTING_UP;
            case STEP_SETUP_PASSWORD:
                return STEP_SETUP_WIFI;
            case STEP_LOCATION_ACCESS:
                return STEP_SETTING_UP;
            case STEP_LOCATION_ACCESS_DENIED:
                return STEP_SETTING_UP;
            case STEP_BLUETOOTH_NOT_ON:
                return STEP_SETTING_UP;
            case STEP_MANUAL_WIFI_SETUP:
                return STEP_SETUP_WIFI;
            case STEP_MANUAL_NETWORK_TYPE_SETUP:
                return STEP_MANUAL_WIFI_SETUP;
            default:
                return STEP_SETTING_UP;
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void setStepIndicator(int index) {
        for (int i = 0; i < mStepProgresses.length; i++) {
            mStepProgresses[i].setBackgroundResource(i < index ? R.color.marigold : R.color.lightGrey);
        }
    }

    private void setStep(int stepIndex) {
        setStep(stepIndex, false);
    }

    public void setStep(int stepIndex, boolean isBackTracked) {
        if (mStepIndex == stepIndex) return;
        mStepIndex = stepIndex;
        //hide progress if showing in other fragment
        if (mCurrentFragment != null) {
            mCurrentFragment.showProgressBar(false);
        }
        switch (stepIndex) {
            case STEP_ACCOUNT_CREATION:
                setStepIndicator(INDICATOR_ONE);
                updateNavigationBar(R.string.setup_account_creation_title, R.string.setup_navigation_next, false);
                if (!isBackTracked) {
                    String caregiverData = getIntent().getStringExtra(EXTRA_CAREGIVER_DATA);
                    updateFragment(SetupAccountCreationFragment.newInstance(caregiverData), SetupAccountCreationFragment.TAG);
                }
                break;
            case STEP_ADD_CHILD:
                setStepIndicator(INDICATOR_TWO);
                updateNavigationBar(R.string.setup_add_child_title, R.string.setup_navigation_next, true, false);
                if (!isBackTracked)
                    updateFragment(BabyFragment.newInstance(BabyFragment.MODE_SETUP), BabyFragment.TAG);
                break;
            case STEP_SETTING_UP:
                setStepIndicator(INDICATOR_THREE);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    if (mSkip)
                        updateNavigationBar(R.string.setup_setting_up_title, R.string.setup_navigation_next, false);
                    else
                        updateNavigationBar(R.string.setup_setting_up_title, R.string.setup_navigation_next, false, false);
                    if (!isBackTracked)
                        updateFragment(SetupSettingUpFragment.newInstance(), SetupSettingUpFragment.TAG);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, true);
                    if (!isBackTracked)
                        updateFragment(SettingsWifiSetUpFragment.newInstance(), SettingsWifiSetUpFragment.TAG);
                }
                break;
            case STEP_LOCATION_OFF:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.location_access, false);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, true);
                }
                if (!isBackTracked)
                    updateFragment(SetupLocationAndBleAccessFragment.getInstance(SetupLocationAndBleAccessFragment.LOCATION_OFF, mSettingType), SetupLocationAndBleAccessFragment.TAG, false);
                break;

            case STEP_LOCATION_ACCESS_DENIED:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.location_access, false);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, true);
                }
                if (!isBackTracked)
                    updateFragment(SetupLocationAndBleAccessFragment.getInstance(SetupLocationAndBleAccessFragment.LOCATION_ACCESS_DENIED, mSettingType), SetupLocationAndBleAccessFragment.TAG, false);
                break;
            case STEP_LOCATION_ACCESS:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.location_access, false);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, true);
                }
                if (!isBackTracked)
                    updateFragment(SetupLocationAndBleAccessFragment.getInstance(SetupLocationAndBleAccessFragment.LOCATION_ACCESS, mSettingType), SetupLocationAndBleAccessFragment.TAG, false);
                break;

            case STEP_BLUETOOTH_NOT_ON:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.bluetooth, false);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, true);
                }
                if (!isBackTracked)
                    updateFragment(SetupLocationAndBleAccessFragment.getInstance(SetupLocationAndBleAccessFragment.BLUETOOTH_NOT_ON, mSettingType), SetupLocationAndBleAccessFragment.TAG, false);
                break;
            case STEP_DEVICE_NOT_FOUND:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.setup_setting_up_title, R.string.setup_navigation_try_again, true);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, R.string.setup_navigation_try_again, true);
                }
                if (!isBackTracked)
                    updateFragment(SetupHubNotFoundFragment.newInstance(mSettingType, mSkip), SetupHubNotFoundFragment.TAG, true);
                break;
            case STEP_CONNECT_DEVICE:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.setup_connect_device_title, true);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, true);
                }
                if (!isBackTracked)
                    updateFragment(SetupConnectDeviceFragment.newInstance(mSettingType, mSkipToDeviceSetup), SetupConnectDeviceFragment.TAG);
                break;
            case STEP_SETUP_WIFI:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.setup_wifi_title, false);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, true);
                }
                if (!isBackTracked)
                    updateFragment(SetupWiFiFragment.newInstance(), SetupWiFiFragment.TAG);
                break;
            case STEP_SETUP_PASSWORD:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.setup_wifi_title, R.string.setup_navigation_join, false);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, R.string.setup_navigation_join, true);
                }
                if (!isBackTracked)
                    updateFragment(SetupPasswordFragment.newInstance(mSelectedWifiSsid), SetupPasswordFragment.TAG);
                break;

            case STEP_MANUAL_WIFI_SETUP:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.setup_wifi_title, R.string.setup_navigation_join, false);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, R.string.setup_navigation_join, true);
                }
                if (!isBackTracked)
                    updateFragment(ManualSetupWiFiFragment.newInstance(), ManualSetupWiFiFragment.TAG);
                break;
            case STEP_MANUAL_NETWORK_TYPE_SETUP:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.setup_wifi_title, true);
                } else {
                    updateNavigationBar(R.string.change_wifi_network, true);
                }
                if (!isBackTracked) {
                    updateFragment(ManualNetworkTypeFragment.newInstance(mSelectedNetworkType), ManualSetupWiFiFragment.TAG);
                }
                break;
            case STEP_WIFI_SUCCESS:
                setStepIndicator(INDICATOR_FOUR);
                if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
                    updateNavigationBar(R.string.connected, R.string.setup_navigation_next, true, false);
                } else {
                    updateNavigationBar(R.string.connected, R.string.done, true, true);
                }

                if (!isBackTracked)
                    updateFragment(SetupWifiSuccessFragment.newInstance(), SetupWifiSuccessFragment.TAG);
                break;
            case STEP_PUTTING_ON:
                setStepIndicator(INDICATOR_FIVE);
                updateNavigationBar(R.string.setup_putting_on_title, R.string.setup_navigation_next, true, false);
                if (!isBackTracked)
                    updateFragment(SetupPuttingOnFragment.newInstance(), SetupPuttingOnFragment.TAG);
                break;

            case STEP_HOW_TO_CHARGE:
                setStepIndicator(INDICATOR_SIX);
                updateNavigationBar(R.string.setup_how_to_charge_title, R.string.setup_navigation_next, true, false);
                if (!isBackTracked)
                    updateFragment(SetupHowToChargeFragment.newInstance(), SetupHowToChargeFragment.TAG);
                break;

            default:
                break;
        }
    }

    private int getBackedStep() {
        switch (mStepIndex) {
            case STEP_ADD_CHILD:
                return STEP_ACCOUNT_CREATION;
            case STEP_SETTING_UP:
                return STEP_ADD_CHILD;
            case STEP_CONNECT_DEVICE:
                return STEP_SETTING_UP;
            case STEP_SETUP_WIFI:
                return STEP_SETTING_UP;
            case STEP_SETUP_PASSWORD:
                return STEP_SETUP_WIFI;
            case STEP_PUTTING_ON:
                return STEP_SETUP_PASSWORD;
            case STEP_MANUAL_NETWORK_TYPE_SETUP:
                return STEP_MANUAL_WIFI_SETUP;
            case STEP_DEVICE_NOT_FOUND:
                return STEP_SETTING_UP;
            case STEP_HOW_TO_CHARGE:
                return STEP_PUTTING_ON;
            default:
                return STEP_ACCOUNT_CREATION;
        }
    }

    private void goToWifiSetup() {
        //delay for screen update to show the connection success and then change the screen
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SystemClock.sleep(2000);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                final SetupActivity setupActivity = sActivity.get();
                if (setupActivity != null && !setupActivity.isFinishing() && setupActivity.isVisible) {
                    if (mConnected) {
                        setStep(STEP_SETUP_WIFI);
                    }
                }
            }
        }.execute();
    }

    void updateNavigationBar(int titleId, boolean showBack) {
        mTitleView.setText(titleId);
        mActionButton.setVisibility(View.INVISIBLE);
        showBackButton(showBack);
    }

    void updateNavigationBar(int titleId, int actionId, boolean actionBtnEnabled) {
        updateNavigationBar(titleId, actionId, actionBtnEnabled, true);
    }

    void updateNavigationBar(int titleId, int actionId, boolean actionBtnEnabled, boolean showBack) {
        mTitleView.setText(titleId);
        mActionButton.setText(actionId);
        mActionButton.setEnabled(actionBtnEnabled);
        mActionButton.setVisibility(View.VISIBLE);
        showBackButton(showBack);
    }

    private void updateFragment(BaseFragment fragment, String tag) {
        updateFragment(fragment, tag, true);
    }

    private void updateFragment(BaseFragment fragment, String tag, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                .replace(R.id.setup_fragment, fragment, tag);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();
        mCurrentFragment = fragment;
    }

    public void createAccount() {
        SetupAccountCreationFragment fragment = (SetupAccountCreationFragment)
                getFragmentManager().findFragmentByTag(SetupAccountCreationFragment.TAG);

        if (fragment != null) {
            fragment.createAccount();
        } else {
            fragment = new SetupAccountCreationFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.setup_fragment, fragment)
                    .commit();
        }
    }

    public void createChild() {
        BabyFragment fragment = (BabyFragment)
                getFragmentManager().findFragmentByTag(BabyFragment.TAG);

        if (fragment != null) {
            fragment.createChild();
        } else {
            fragment = BabyFragment.newInstance(BabyFragment.MODE_SETUP);
            getFragmentManager().beginTransaction()
                    .replace(R.id.setup_fragment, fragment)
                    .commit();
        }
    }

    private void getChildInfo(final String accessToken) {
        new AsyncTask<Void, Void, SSManagement.Child>() {
            private SSException mException;

            private SSManagement.Child getChild() throws JSONException, SSException, IOException {
                List<SSManagement.Child> children = SKManagement.listChildren(accessToken);
                if (children.isEmpty()) return null;
                return children.get(0);
            }

            @Override
            protected SSManagement.Child doInBackground(Void... voids) {
                try {
                    return getChild();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    mException = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.Child child) {
                if (child != null) {
                    AccountManagement.getInstance(SetupActivity.this).writeChild(child);
                    listDevice(accessToken);
                } else {
                    showProgressBar(false);
                    goToStatusScreen();
                }
            }
        }.execute();
    }

    private void listDevice(final String accessToken) {
        new AsyncTask<Void, Void, List<SSManagement.DeviceResponse>>() {
            private SSException mException;

            @Override
            protected List<SSManagement.DeviceResponse> doInBackground(Void... voids) {
                try {
                    return SKManagement.listDevices(accessToken);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    mException = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<SSManagement.DeviceResponse> devices) {
                if (devices != null && !devices.isEmpty()) {
                    showProgressBar(false);
                    SharedPrefManager.saveDevice(SetupActivity.this, devices.get(0));
                    goToStatusScreen();
                } else if (devices != null && devices.isEmpty()) {
                    checkEvents(accessToken);
                } else if (mException.getResponseCode() == 404) {
                    showProgressBar(false);
                    // TODO: how to handle this
                    goToStatusScreen();
                } else if (SharedPrefManager.getDevice(SetupActivity.this) != null) {
                    showProgressBar(false);
                    goToStatusScreen();
                } else {
                    showProgressBar(false);
                    goToSettings();
                }
            }
        }.execute();
    }

    private void checkEvents(final String accessToken) {
        new AsyncTask<Void, Void, Boolean>() {
            SSError mError;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    List<SSManagement.SSEvent> events = SKManagement.listEventsByChild(accessToken, AccountManagement.getInstance(SetupActivity.this).getChild().id);
                    return events == null || events.isEmpty();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return Boolean.TRUE;
            }

            @Override
            protected void onPostExecute(Boolean empty) {
                showProgressBar(false);
                if (empty) {
                    SharedPrefManager.clearDevice(SetupActivity.this);
                    Utils.logEvents(LogEvents.TIMELINE_NO_DATA);
                    goToSettings();
                } else {
                    goToStatusScreen();
                }
            }
        }.execute();
    }

    @Override
    public void onActionButtonEnabled(boolean enabled) {
        mActionButton.setEnabled(enabled);
    }

    @Override
    public void onHubWifiSetupSuccess() {
        publishTimeZoneToMqtt();
        setStep(STEP_WIFI_SUCCESS);
        Utils.logEvents(HUB_UPDATED_WIFI_SETTINGS);
    }

    @Override
    public void onManualSetupClick() {
        setStep(STEP_MANUAL_WIFI_SETUP);
    }

    @Override
    public void onBleHubFound(BluetoothDevice device) {
        mBluetoothLeService.connect(device.getAddress());
    }

    @Override
    public void onAccountCreated(boolean created, String accountType) {
        mAccountType = accountType;
        if (created) {
            setUpPushNotification();
        }
    }

    @Override
    public void performAccountCreation() {
        mActionButton.performClick();
    }

    @Override
    public void onUserLoggedIn() {
        App.getInstance().initializeMqtt();
        connectMqtt();
    }

    @Override
    protected void onPushNotificationRegistrationSuccess(CreateHandheldResponse createHandheldResponse) {
        super.onPushNotificationRegistrationSuccess(createHandheldResponse);
        onPushTokenRegistration();
    }

    @Override
    protected void onPushNotificationRegistrationFailure(Throwable t) {
        super.onPushNotificationRegistrationFailure(t);
        onPushTokenRegistration();
    }

    private void onPushTokenRegistration() {
        if (SSManagement.TYPE_GUARDIAN.equals(mAccountType)) {
            setStep(STEP_ADD_CHILD);
        } else {
            getChildInfo(AccountManagement.getInstance(this).getAccessToken());
//            goToStatusScreen();
        }
    }

    @Override
    public void onActionButtonEnabled(String tag, boolean enabled) {
        onActionButtonEnabled(enabled);
    }

    @Override
    public void onChildCreated(boolean created, String ownerId) {
        mOwnerID = ownerId;
        if (created) setStep(STEP_SETTING_UP);
    }

    @Override
    public void onBabyUpdated(boolean updated) {

    }

    @Override
    public void onSkipClicked(boolean clicked) {
        if (clicked) {
            switch (mStepIndex) {
                case STEP_ADD_CHILD:
                    setStep(STEP_SETTING_UP);
                    break;
                case STEP_LOCATION_ACCESS:
                case STEP_CONNECT_DEVICE:
                case STEP_DEVICE_NOT_FOUND:
                    mHasDevice = false;
                    finishOnboarding();
                    break;
            }
        }
    }

    @Override
    public void onAllGpsConditionsSatisfied() {
        setStep(STEP_CONNECT_DEVICE);
    }

    @Override
    public void onLocationPermissionDenied() {
        setStep(STEP_LOCATION_ACCESS_DENIED);
    }

    @Override
    public void onGPSTurnOnDenied() {
        setStep(STEP_LOCATION_OFF);
    }

    @Override
    public void onBluetoothTurnOn() {
        setStep(STEP_CONNECT_DEVICE);
    }

    @Override
    public void onHelpClicked() {
        App.getInstance().openHelp(this);
    }

    @Override
    public void onHubNotFound() {
        setStep(STEP_DEVICE_NOT_FOUND);
    }

    @Override
    public void onBluetoothPermissionDenied() {
        setStep(STEP_BLUETOOTH_NOT_ON);
    }

    @Override
    public void startWifiScanOnHub() {
        if (mBluetoothLeService != null)
            mBluetoothLeService.scanAvailableWifi();
    }

    @Override
    public void refreshWifiScanOnHub() {
        if (mBluetoothLeService != null && mConnected) {
            mBluetoothLeService.stopWifiScanning();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPostExecute(Void aVoid) {
                    if (mBluetoothLeService != null) mBluetoothLeService.scanAvailableWifi();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    SystemClock.sleep(1000);
                    return null;
                }
            }.execute();
        } else {
            goToSettingUp();
        }
    }

    @Override
    public void onWiFiSelected(WifiItem wifiItem) {
        // send wifi ssid to setup password fragment
        mBluetoothLeService.stopWifiScanning();
        mSelectedWifiSsid = wifiItem;
        if (wifiItem.getSsid().equalsIgnoreCase(getString(R.string.setup_wifi_other))) {
            setStep(STEP_MANUAL_WIFI_SETUP);
        } else {
            setStep(STEP_SETUP_PASSWORD);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//            Log.d(TAG, "Connect request result=" + result);
        }
        isVisible = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BleConnectionEvent bleConnectionEvent) {
        if (bleConnectionEvent.getConnectionStatus() == BleConnectionEvent.DISCONNECTED && !bleConnectionEvent.isManualDisconnect()) {
            goToSettingUp();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final PublicKeyEvent publicKeyEvent) {
        // on successful connection & getting public key get device serial number and send it to server
        getDeviceSerialNumber();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final DeviceSerialEvent deviceSerialEvent) {
//        updateUIAndGotoWifiSetup();
        mDeviceSerialID = deviceSerialEvent.getDeviceSerial();
        SSManagement.DeviceResponse deviceResponse = SharedPrefManager.getDevice(this);
        if (SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType)) {
            createDevice(deviceSerialEvent.getDeviceSerial());
        } else {
            if (deviceResponse != null) {
                if (deviceResponse.getSerial().equalsIgnoreCase(mDeviceSerialID)) {
                    updateUIAndGotoWifiSetup();
                } else {
                    String serialID = deviceResponse != null ? deviceResponse.getSerial() : "N/A";
                    Log.e(TAG, "SerialID associated with Account : " + serialID);
                    Log.e(TAG, "SerialID from HUB : " + mDeviceSerialID);
                    mBluetoothLeService.disconnect(false);
                    showDeviceErrorAlert();
                }
            } else {
                updateUIAndGotoWifiSetup();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final ConnectionReadWriteErrorEvent connectionReadWriteErrorEvent) {
        if (mStepIndex == STEP_CONNECT_DEVICE) {
            setStep(STEP_SETTING_UP);
        }
    }

    private void createDevice(final String deviceSerial) {
        if (mOwnerID == null) {
            mOwnerID = AccountManagement.getInstance(this).getChild().id;
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", App.getAppContext().getString(R.string.my_sproutling));
            jsonObject.put("owner_id", mOwnerID);
            jsonObject.put("owner_type", "Child");
            jsonObject.put("serial", deviceSerial);
            jsonObject.put("firmware_version", "1.1.1"); // TODO: add firmware version
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String accessToken = AccountManagement.getInstance(this).getAccessToken();
        Log.d(TAG, "Device Serial ID :" + deviceSerial);

        new AsyncTask<Void, Void, SSManagement.DeviceResponse>() {
            private SSError mError;

            @Override
            protected SSManagement.DeviceResponse doInBackground(Void... voids) {
                try {
                    return SKManagement.createDevice(accessToken, jsonObject);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(SSManagement.DeviceResponse deviceResponse) {
                if (deviceResponse != null) {
                    SharedPrefManager.saveDevice(SetupActivity.this, deviceResponse);
                    Log.d(TAG, "Device created Successfully");
                    Utils.logEvents(HUB_REGISTERED);
                    updateUIAndGotoWifiSetup();
                }
                if (mError != null) {
                    Log.e(TAG, "Device create failed :" + mError.message);
                    setStep(STEP_DEVICE_NOT_FOUND);
                    if (mError.code == SSError.Taken) {
                        Log.e(TAG, "Device create failed. Try removing the device from DB. The device serial ID : " + deviceSerial);
                        showDeviceErrorAlert();
                    }
                    mBluetoothLeService.disconnect(false);
                }
            }
        }.execute();
    }

    private void showDeviceErrorAlert() {
        DeviceErrorDialogFragment deviceErrorDialogFragment = DeviceErrorDialogFragment.newInstance();
        deviceErrorDialogFragment.show(getFragmentManager(), null);
    }

    private void updateUIAndGotoWifiSetup() {
        SetupConnectDeviceFragment setupConnectDeviceFragment = (SetupConnectDeviceFragment) getFragmentManager().findFragmentByTag(SetupConnectDeviceFragment.TAG);
        if (setupConnectDeviceFragment != null) {
            setupConnectDeviceFragment.updateUIOnBleConnection(true);
        }
        goToWifiSetup();
    }

    private void goToSettingUp() {
//        Log.i(TAG, "Ble Connection state disconnected");
        if (mCurrentFragment != null) mCurrentFragment.showProgressBar(false);
        setStep(STEP_SETTING_UP);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        isVisible = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        unregisterReceiver(mBroadcastReceiver);
//        disconnectMqtt();
        EventBus.getDefault().unregister(this);
    }

    private void publishTimeZoneToMqtt() {
        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();

        Hub.HubUserConfiguration hubUserConfiguration;
        if (mHubUserConfiguration != null) {
            hubUserConfiguration = Hub.HubUserConfiguration.newBuilder()
                    .setBleAlert(mHubUserConfiguration.getBleAlert())
                    .setWifiAlert(mHubUserConfiguration.getWifiAlert())
                    .setTimestamp(timestamp)
                    .setTimezone(TimeZone.getDefault().getID()).build();

        } else {
            hubUserConfiguration = Hub.HubUserConfiguration.newBuilder()
                    .setBleAlert(true)
                    .setWifiAlert(true)
                    .setTimestamp(timestamp)
                    .setTimezone(TimeZone.getDefault().getID()).build();
        }
        Log.d(TAG, "TimeZone : " + TimeZone.getDefault().getID());

        String deviceId = TextUtils.isEmpty(mDeviceSerialID) ? SharedPrefManager.getDevice(this).getSerial() : mDeviceSerialID;
        Log.d(TAG, "DeviceID : " + deviceId);
        MqttItem mqttItem = new MqttItem.Builder(this).actionType(MqttAction.PUBLISH_MESSAGE)
                .topic(MqttAPI.getMqttUserConfig(deviceId), 2)
                .payload(hubUserConfiguration.toByteArray())
                .setRetained(true)
                .build();
        PipelineManager.getInstance().doPipeline(new BaseMqttTaskList(), mqttItem, new PublishMqttMessageCallback());
        Utils.logEvents(LogEvents.TIME_ZONE_SENT);
    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        mDeviceResponse = SharedPrefManager.getDevice(this);
        if (mDeviceResponse != null) {
            ArrayList<String> topics = new ArrayList<>();
            topics.add(MqttAPI.getMqttUserConfig(mDeviceResponse.getSerial()));
            return topics;
        }
        return null;
    }

    @Override
    protected void onHubUserConfiguration(Hub.HubUserConfiguration hubUserConfiguration) {
        super.onHubUserConfiguration(hubUserConfiguration);
        mHubUserConfiguration = hubUserConfiguration;
    }

    @Override
    public void onStateChanged(IStatePayload payload) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mCurrentFragment != null)
                mCurrentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCurrentFragment != null)
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNetworkTypeSelect(@ManualNetworkTypeFragment.NetworkSecurityType String networkType) {
        mSelectedNetworkType = networkType;
        mBackButton.performClick();
        EventBus.getDefault().post(new WifiNetworkTypeEvent(networkType));
    }

    @Override
    public void onNetworkTypeClick(@ManualNetworkTypeFragment.NetworkSecurityType String networkType) {
        setStep(STEP_MANUAL_NETWORK_TYPE_SETUP);
        mSelectedNetworkType = networkType;
    }

    @Override
    public void onContinueClicked() {
        goToConnectDevice();
    }
}
