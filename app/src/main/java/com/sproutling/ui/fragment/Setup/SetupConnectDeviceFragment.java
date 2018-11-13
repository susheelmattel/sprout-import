/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Setup;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.sproutling.BuildConfig;
import com.sproutling.R;
import com.sproutling.services.SHBleScanManager;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.sproutling.ui.activity.SetupActivity.EXTRA_SETUP_SKIP;
import static com.sproutling.ui.activity.SetupActivity.SETTING_TYPE;
import static com.sproutling.ui.activity.SetupActivity.SETTING_TYPE_SET_UP;

public class SetupConnectDeviceFragment extends BaseFragment implements ResultCallback<LocationSettingsResult>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "SetupConctDeviceFgmnt";
    private static final int PERMISSION_REQUEST_CODE = 16;
    /**
     * Constant used in the location settings dialog.
     */
    public static final int REQUEST_CHECK_SETTINGS = 1612;
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
    private static final int REQUEST_ENABLE_BT = 1;
    private OnConnectDeviceListener mOnConnectDeviceListener;
    private IntentFilter mFilter;
    private boolean mFirstTimeResult = true;

    private AnimatorSet mDotsAnimatorSet, mFadeOutAnimatorSet, mFadeInAnimatorSet;

    private RelativeLayout mDotsLayout, mSuccessLayout;
    private ImageView mDot1, mDot2, mDot3;
    private ShTextView mStatusView, mDescriptionView, mSkipView;
    private ArrayAdapter<String> mBleListAdapter;
    private SHBleScanManager mSHBluetoothManager;
    private String mSettingType;
    private boolean mSkip;

    public SetupConnectDeviceFragment() {
    }

    public static SetupConnectDeviceFragment newInstance(String settingType, boolean skip) {
        SetupConnectDeviceFragment fragment = new SetupConnectDeviceFragment();
        Bundle arguments = new Bundle();
        arguments.putString(SETTING_TYPE, settingType);
        arguments.putBoolean(EXTRA_SETUP_SKIP, skip);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSettingType = getArguments().getString(SETTING_TYPE, SETTING_TYPE_SET_UP);
            mSkip = getArguments().getBoolean(EXTRA_SETUP_SKIP, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_connect_device, container, false);

        mDotsLayout = (RelativeLayout) view.findViewById(R.id.dots_animation_layout);
        mSuccessLayout = (RelativeLayout) view.findViewById(R.id.success_layout);
        mDot1 = (ImageView) view.findViewById(R.id.dot1);
        mDot2 = (ImageView) view.findViewById(R.id.dot2);
        mDot3 = (ImageView) view.findViewById(R.id.dot3);
        mStatusView = (ShTextView) view.findViewById(R.id.status);
        mDescriptionView = (ShTextView) view.findViewById(R.id.description);
        mSkipView = (ShTextView) view.findViewById(R.id.skip);

        mSkipView.setOnClickListener(mOnSkipClickListener);
        mSkipView.setVisibility(SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType) ? View.VISIBLE : View.INVISIBLE);

        startDotsAnimation();
        startSlideFadeOutAnimation();
        startSlideFadeInAnimation();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void startBleScanning() {
        mDotsLayout.setVisibility(View.VISIBLE);
        mSuccessLayout.setVisibility(View.GONE);
        mSHBluetoothManager.scanBleDevice(true, mBluetoothMgrCallback);
    }

    private void init() {
        mSHBluetoothManager = new SHBleScanManager(getActivity());
        mBleListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
    }

    private void initHardwareSettingsCheck() {
        if (mSHBluetoothManager.isBleEnabled()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (isLocationPermissionGranted() && Utils.isGPSEnabled(getActivity())) {
                    startBleScanning();
                } else {
                    setUpLocationRequest();
                    handleGpsAndRuntimePermissions();
                }
            } else {
                startBleScanning();
            }
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    private void setUpLocationRequest() {
        rebuildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    public boolean isLocationPermissionGranted() {
        return !(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED);
    }

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

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(getClass().getName(), "Bluetooth off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(getClass().getName(), "Turning Bluetooth off...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i(getClass().getName(), "Bluetooth on");
                        mSHBluetoothManager.initOnBluetoothTurnedOn();
//                        mSHBluetoothManager.scanLeDevice(true);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(getClass().getName(), "Turning Bluetooth on...");
                        break;
                }
            }
        }
    };

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
            mOnConnectDeviceListener = (OnConnectDeviceListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnConnectDeviceListener");
        }
    }

    private SHBleScanManager.SHBluetoothMgrCallback mBluetoothMgrCallback = new SHBleScanManager.SHBluetoothMgrCallback() {
        @Override
        public void onDevicesUpdated(List<String> devices) {

        }

        @Override
        public void onScanStop(List<BluetoothDevice> bluetoothDevices) {
            if (bluetoothDevices != null && bluetoothDevices.size() > 0) {
                if (bluetoothDevices.size() == 1) {
                    //connect
                    getOnConnectDeviceListener().onBleHubFound(bluetoothDevices.get(0));
                } else {
                    if (BuildConfig.DEBUG) {
                        List<String> devices = new ArrayList<>();
                        for (BluetoothDevice ble : bluetoothDevices) {
                            devices.add(ble.getAddress());
                        }
                        mBleListAdapter.clear();
                        mBleListAdapter.addAll(devices);
                        if (mFirstTimeResult) {
                            mFirstTimeResult = false;
                            showBLEDevices();
                        }
                        mBleListAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                getOnConnectDeviceListener().onActionButtonEnabled(true);
                mDotsLayout.setVisibility(View.GONE);
                getOnConnectDeviceListener().onHubNotFound();
            }
        }
    };

    private void handleGpsAndRuntimePermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!isLocationPermissionGranted()) {
                requestLocationPermission();
            } else {
                checkLocationSettingsAcquireLocation();
            }
        }
    }

    public void requestLocationPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {

            mOnConnectDeviceListener.onLocationPermissionDenied();
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
    protected void checkLocationSettingsAcquireLocation() {
        if (!Utils.isGPSEnabled(getActivity())) {
            if (mGoogleApiClient != null && mLocationSettingsRequest != null) {
                PendingResult<LocationSettingsResult> result =
                        LocationServices.SettingsApi.checkLocationSettings(
                                mGoogleApiClient,
                                mLocationSettingsRequest
                        );
                result.setResultCallback(this);
            }
        } else {
            startBleScanning();
        }
    }

    private void showBLEDevices() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(mBleListAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                Log.i(getClass().getName(), mBleListAdapter.getItem(i));
                //connect
                getOnConnectDeviceListener().onBleHubFound(mSHBluetoothManager.getAllBleDevices().get(i));

            }
        });
        builderSingle.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnConnectDeviceListener = null;
        stopBleScanning();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
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
        } else {
            initHardwareSettingsCheck();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        stopBleScanning();
    }

    private void stopBleScanning() {
        if (mSHBluetoothManager != null && mSHBluetoothManager.isScanning())
            mSHBluetoothManager.stopBleScanning(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    mSHBluetoothManager.initOnBluetoothTurnedOn();
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        handleGpsAndRuntimePermissions();
                    } else {
                        startBleScanning();
                    }
                } else {
                    mOnConnectDeviceListener.onBluetoothPermissionDenied();
                }
                break;
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.i(TAG, "User agreed to turn on GPS");
                        if (Utils.isGPSEnabled(getActivity())) {
                            startBleScanning();
                        }

                        break;
                    }
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to turn on GPS");

                        break;
                }
                break;
            default:
        }
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
                startBleScanning();

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
                    checkLocationSettingsAcquireLocation();
                } else {
                    Log.i(TAG, "Permission Denied, You cannot access location data.");
                }
                break;
        }
    }

    public void updateUIOnBleConnection(boolean connected) {
        if (connected) {
            mDotsLayout.setVisibility(View.GONE);
            mSuccessLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected.");
        initHardwareSettingsCheck();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public interface OnConnectDeviceListener {
        void onActionButtonEnabled(boolean enabled);

        void onBleHubFound(BluetoothDevice device);

        void onSkipClicked(boolean clicked);

        void onHubNotFound();

        void onBluetoothPermissionDenied();

        void onLocationPermissionDenied();
    }

    private OnConnectDeviceListener getOnConnectDeviceListener() {
        if (mOnConnectDeviceListener == null)
            mOnConnectDeviceListener = (OnConnectDeviceListener) getActivity();
        return mOnConnectDeviceListener;
    }

    private View.OnClickListener mOnSkipClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSkip)
                getActivity().finish();
            else
                getOnConnectDeviceListener().onSkipClicked(true);
        }
    };

    private void startDotsAnimation() {
        ObjectAnimator scaleUpX1 = ObjectAnimator.ofFloat(mDot1, "scaleX", 1.3f);
        ObjectAnimator scaleUpY1 = ObjectAnimator.ofFloat(mDot1, "scaleY", 1.3f);
        scaleUpX1.setDuration(150);
        scaleUpY1.setDuration(150);

        ObjectAnimator scaleDownX1 = ObjectAnimator.ofFloat(mDot1, "scaleX", 1.0f);
        ObjectAnimator scaleDownY1 = ObjectAnimator.ofFloat(mDot1, "scaleY", 1.0f);
        scaleDownX1.setDuration(150);
        scaleDownY1.setDuration(150);

        ObjectAnimator scaleUpX2 = ObjectAnimator.ofFloat(mDot2, "scaleX", 1.3f);
        ObjectAnimator scaleUpY2 = ObjectAnimator.ofFloat(mDot2, "scaleY", 1.3f);
        scaleUpX2.setDuration(150);
        scaleUpY2.setDuration(150);

        ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(mDot2, "scaleX", 1.0f);
        ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(mDot2, "scaleY", 1.0f);
        scaleDownX2.setDuration(150);
        scaleDownY2.setDuration(150);

        ObjectAnimator scaleUpX3 = ObjectAnimator.ofFloat(mDot3, "scaleX", 1.3f);
        ObjectAnimator scaleUpY3 = ObjectAnimator.ofFloat(mDot3, "scaleY", 1.3f);
        scaleUpX3.setDuration(150);
        scaleUpY3.setDuration(150);

        ObjectAnimator scaleDownX3 = ObjectAnimator.ofFloat(mDot3, "scaleX", 1.0f);
        ObjectAnimator scaleDownY3 = ObjectAnimator.ofFloat(mDot3, "scaleY", 1.0f);
        scaleDownX3.setDuration(150);
        scaleDownY3.setDuration(150);

        mDotsAnimatorSet = new AnimatorSet();

        mDotsAnimatorSet.play(scaleUpX1).with(scaleUpY1);
        mDotsAnimatorSet.play(scaleDownX1).with(scaleDownY1).after(scaleUpX1);

        mDotsAnimatorSet.play(scaleUpX2).with(scaleUpY2).after(scaleDownX1);
        mDotsAnimatorSet.play(scaleDownX2).with(scaleDownY2).after(scaleUpX2);

        mDotsAnimatorSet.play(scaleUpX3).with(scaleUpY3).after(scaleDownX2);
        mDotsAnimatorSet.play(scaleDownX3).with(scaleDownY3).after(scaleUpX3);

        mDotsAnimatorSet.addListener(mDotsAnimatorListener);

        mDotsAnimatorSet.start();
    }

    private void startSlideFadeOutAnimation() {
        ObjectAnimator slideUpY = ObjectAnimator.ofFloat(mDotsLayout, "translationY", -40);
        slideUpY.setDuration(300);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mDotsLayout, "alpha", 0);
        fadeOut.setDuration(300);

        mFadeOutAnimatorSet = new AnimatorSet();
        mFadeOutAnimatorSet.play(slideUpY).with(fadeOut);
        mFadeOutAnimatorSet.addListener(mDotsFadeOutAnimatorListener);
    }

    private void startSlideFadeInAnimation() {
        ObjectAnimator slideUpY = ObjectAnimator.ofFloat(mSuccessLayout, "translationY", 40, 0);
        slideUpY.setDuration(300);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mSuccessLayout, "alpha", 0, 1);
        fadeIn.setDuration(300);

        mFadeInAnimatorSet = new AnimatorSet();
        mFadeInAnimatorSet.play(slideUpY).with(fadeIn);
        mFadeInAnimatorSet.addListener(mSuccessFadeInAnimatorListener);
    }

    private Animator.AnimatorListener mDotsAnimatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mDotsAnimatorSet.start();
        }
    };

    private Animator.AnimatorListener mDotsFadeOutAnimatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mFadeInAnimatorSet.start();
        }
    };

    private Animator.AnimatorListener mSuccessFadeInAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            mSuccessLayout.setVisibility(View.VISIBLE);
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
        }
    };
}
