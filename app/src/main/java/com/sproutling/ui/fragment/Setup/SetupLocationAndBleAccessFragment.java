/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Setup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.sproutling.R;
import com.sproutling.services.SHBleScanManager;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import static com.sproutling.ui.activity.SetupActivity.SETTING_TYPE;
import static com.sproutling.ui.activity.SetupActivity.SETTING_TYPE_CHANGE;

/**
 * Created by subram13 on 1/30/17.
 */

public class SetupLocationAndBleAccessFragment extends BaseFragment implements ResultCallback<LocationSettingsResult>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "SetupLoc&BleAcsFgmnt";
    public static final int LOCATION_OFF = 0;
    public static final int LOCATION_ACCESS_DENIED = 1;
    public static final int LOCATION_ACCESS = 2;
    public static final int BLUETOOTH_NOT_ON = 4;

    private static final String SCREEN_TYPE = "SCREEN_TYPE";
    private static final int PERMISSION_REQUEST_CODE = 16;
    /**
     * Constant used in the location settings dialog.
     */
    public static final int REQUEST_CHECK_SETTINGS = 1612;
    public static final int REQUEST_LOCATION_ACCESS_SETTINGS = 1984;
    public static final int REQUEST_GPS_SETTINGS = 5487;
    public static final int REQUEST_BLUETOOTH_SETTINGS = 1565;
    public static final int WAIT_PERIOD_MILLISECONDS = 1000;
    private static final int UPDATE_INTERVAL_IN_MILLISECONDS = 100000;
    private static final int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;
    protected GoogleApiClient mGoogleApiClient;

    private int mScreenType;
    private String mSettingType;

    private ImageView mScreenImg;
    private ShTextView mTvScreenTitle;
    private ShTextView mTvScreenMsg;
    private ShTextView mTvSkip;
    private Button mBtnSettings;
    private LocBleListener mLocBleListener;


    @IntDef({LOCATION_OFF, LOCATION_ACCESS_DENIED, LOCATION_ACCESS, BLUETOOTH_NOT_ON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Screen {

    }

    public static SetupLocationAndBleAccessFragment getInstance(@Screen int screen, String settingType) {
        SetupLocationAndBleAccessFragment fragment = new SetupLocationAndBleAccessFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(SCREEN_TYPE, screen);
        arguments.putString(SETTING_TYPE, settingType);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScreenType = getArguments().getInt(SCREEN_TYPE);
            mSettingType = getArguments().getString(SETTING_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_ble_access, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initOnConnectDeviceListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnConnectDeviceListener();
    }

    private void initOnConnectDeviceListener() {
        try {
            mLocBleListener = (LocBleListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement LocBleListener");
        }
    }

    private void initView(View view) {
        mScreenImg = (ImageView) view.findViewById(R.id.img_loc);
        mTvScreenTitle = (ShTextView) view.findViewById(R.id.tv_location_ble_access);
        mTvScreenMsg = (ShTextView) view.findViewById(R.id.tv_location_ble_info);
        mTvSkip = (ShTextView) view.findViewById(R.id.tv_skip);
        mTvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocBleListener.onSkipClicked(true);
            }
        });
        mBtnSettings = (Button) view.findViewById(R.id.btn_settings);
        mBtnSettings.setOnClickListener(mSettingClickListner);
        setUpUI();
    }

    public void setScreenType(int screenType) {
        mScreenType = screenType;
        setUpUI();
    }

    private void setUpUI() {
        switch (mScreenType) {
            case LOCATION_OFF:
                setUIValues(R.drawable.ic_location_onboarding_off, getString(R.string.location_must_be_on), getString(R.string.location_must_be_on_msg), getString(R.string.location_settings));
                break;
            case LOCATION_ACCESS_DENIED:
                setUIValues(R.drawable.ic_location_onboarding_off, getString(R.string.location_access), getString(R.string.location_access_info_settings), getString(R.string.location_permission));
                break;
            case LOCATION_ACCESS:
                setUIValues(R.drawable.ic_location_onboarding, getString(R.string.location_access), getString(R.string.location_access_info), getString(R.string.allow_access));
                break;
            case BLUETOOTH_NOT_ON:
                setUIValues(R.drawable.ic_bluetooth_onboarding, getString(R.string.bluetooth_turn_on), getString(R.string.bluetooth_warning), getString(R.string.bluetooth_settings));
                break;
            default:
                break;

        }
        if (!TextUtils.isEmpty(mSettingType) && SETTING_TYPE_CHANGE.equalsIgnoreCase(mSettingType)) {
            mTvSkip.setVisibility(View.INVISIBLE);
        } else {
            mTvSkip.setVisibility(View.VISIBLE);
        }
    }

    private void setUIValues(int imgValue, String title, String msg, String btnValue) {
        mScreenImg.setImageResource(imgValue);
        mTvScreenTitle.setText(title);
        mTvScreenMsg.setText(msg);
        mBtnSettings.setText(btnValue);
    }

    private View.OnClickListener mSettingClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (mScreenType) {
                case LOCATION_OFF:
                    showLocationSettings();
                    break;
                case LOCATION_ACCESS_DENIED:
                    showInstalledAppDetails();
                    break;
                case LOCATION_ACCESS:
                    initHardwareSettingsCheck();
                    break;
                case BLUETOOTH_NOT_ON:
                    showBluetoothSettings();
                    break;
                default:
                    break;

            }
        }
    };

    public interface LocBleListener {
        void onSkipClicked(boolean clicked);

        void onAllGpsConditionsSatisfied();

        void onLocationPermissionDenied();

        void onGPSTurnOnDenied();

        void onBluetoothTurnOn();
    }

    private void setUpLocationRequest() {
        rebuildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

//    private boolean isLocationPermissionGranted() {
//        if (ActivityCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else {
//            return true;
//        }
//
//    }

    private synchronized void rebuildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Sets up the location request. We use ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml to control the accuracy of the current location.
     * <p/>
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    private void handleGpsAndRuntimePermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!Utils.isLocationPermissionGranted(getActivity())) {
                requestLocationPermission();
            } else {
                checkLocationSettingsAcquireLocation();
            }
        }
    }

    private void requestLocationPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {

            mLocBleListener.onLocationPermissionDenied();
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions.toArray(new String[permissions.size()]),
                    PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    private void checkLocationSettingsAcquireLocation() {
        if (!Utils.isGPSEnabled(getActivity())) {
            if (mGoogleApiClient != null && mLocationSettingsRequest != null) {
                PendingResult<LocationSettingsResult> result =
                        LocationServices.SettingsApi.checkLocationSettings(
                                mGoogleApiClient,
                                mLocationSettingsRequest
                        );
                result.setResultCallback(this);
            }
        }
    }


    private void showBluetoothSettings() {
        Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        getActivity().startActivityForResult(settingsIntent, REQUEST_BLUETOOTH_SETTINGS);
    }

    private void showLocationSettings() {
        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        getActivity().startActivityForResult(callGPSSettingIntent, REQUEST_GPS_SETTINGS);
    }

    private void showInstalledAppDetails() {
//        String packageName = getActivity().getApplicationContext().getPackageName();
//        final int apiLevel = Build.VERSION.SDK_INT;
//        Intent intent = new Intent();
//
//        if (apiLevel >= 9) {
//            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.setData(Uri.parse("package:" + packageName));
//        } else {
//            final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");
//
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
//            intent.putExtra(appPkgName, packageName);
//        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        // Start Activity
        getActivity().startActivityForResult(Utils.getIntentToShowInstalledAppDetails(getActivity()), REQUEST_LOCATION_ACCESS_SETTINGS);
    }

    @Override
    public void onStart() {
        super.onStart();
        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)) {
            if (Utils.checkPlayServices(getActivity())) {
                setUpLocationRequest();
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private void initHardwareSettingsCheck() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Utils.isLocationPermissionGranted(getActivity()) && Utils.isGPSEnabled(getActivity())) {
                mLocBleListener.onAllGpsConditionsSatisfied();
            } else {
                handleGpsAndRuntimePermissions();
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.i(TAG, "User agreed to turn on GPS");
                        if (Utils.isGPSEnabled(getActivity())) {
                            mLocBleListener.onAllGpsConditionsSatisfied();
                        }
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to turn on GPS");
                        mLocBleListener.onGPSTurnOnDenied();
                        break;
                }
                break;
            case REQUEST_GPS_SETTINGS:
                if (Utils.isLocationPermissionGranted(getActivity()) && Utils.isGPSEnabled(getActivity())) {
                    mLocBleListener.onAllGpsConditionsSatisfied();
                }
                break;
            case REQUEST_LOCATION_ACCESS_SETTINGS:
                waitToConnectAndCheckLocationPermission();
                break;
            case REQUEST_BLUETOOTH_SETTINGS:
                waitToConnectAndCheckBlePermission();
                break;
            default:
        }
    }

    private void waitToConnectAndCheckBlePermission() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SystemClock.sleep(WAIT_PERIOD_MILLISECONDS);
                return null;
            }

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                showProgressBar(false);
                SHBleScanManager shBleScanManager = new SHBleScanManager(getActivity());
                if (shBleScanManager.isBleEnabled()) {
                    mLocBleListener.onBluetoothTurnOn();
                }
            }
        }.execute();
    }

    private void waitToConnectAndCheckLocationPermission() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SystemClock.sleep(WAIT_PERIOD_MILLISECONDS);
                return null;
            }

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                showProgressBar(false);
                if (Utils.isLocationPermissionGranted(getActivity())) {
                    if (!Utils.isGPSEnabled(getActivity())) {
                        checkLocationSettingsAcquireLocation();
                    }else {
                        //start scanning for HUB after GPS is enabled
                        mLocBleListener.onAllGpsConditionsSatisfied();
                    }
                }
            }
        }.execute();
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                //start scanning for HUB after GPS is enabled
                mLocBleListener.onAllGpsConditionsSatisfied();

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission Granted, Now you can access location data.");
                    waitToConnectAndCheckLocationPermission();
                } else {
                    mLocBleListener.onLocationPermissionDenied();
                    Log.i(TAG, "Permission Denied, You cannot access location data.");
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient onConnectionSuspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient onConnectionFailed.");
    }
}
