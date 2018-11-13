/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.services;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by subram13 on 1/6/17.
 */

public class SHBleScanManager {
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private static final String SPROUTLING_BLE_SERVICE_UUID = "4482ACDF-D160-4E10-8FE3-82599F334433";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private List<BluetoothDevice> mBluetoothDevices;
    private List<String> mBluetoothDeviceNames;
    private SHBluetoothMgrCallback mBluetoothMgrCallback;
    //    private BluetoothGatt mGatt;
    private Context mContext;
    private Handler mHandler;
    private boolean mScanning;


    public SHBleScanManager(Context context) {
        mContext = context;
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mHandler = new Handler();
        initOnBluetoothTurnedOn();
    }

    public void initOnBluetoothTurnedOn() {
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothDevices = new ArrayList<>();
        mBluetoothDeviceNames = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isBleEnabled()) {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
            filters.add(new ScanFilter.Builder().
                    setServiceUuid(ParcelUuid.fromString(SPROUTLING_BLE_SERVICE_UUID)).build());
        }

    }

    public boolean isBleEnabled() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void scanBleDevice(final boolean enable, SHBluetoothMgrCallback shBluetoothMgrCallback) {
        mBluetoothMgrCallback = shBluetoothMgrCallback;
        scanLeDevice(enable);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopBleScanning();
                }
            }, SCAN_PERIOD);
            mScanning = true;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                if (mBluetoothAdapter.isEnabled() && mLEScanner != null) mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            mScanning = false;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                if (mBluetoothAdapter.isEnabled() && mLEScanner != null) mLEScanner.stopScan(mScanCallback);
            }
        }
    }

    private void stopBleScanning() {
        stopBleScanning(true);
    }

    public void stopBleScanning(boolean getResult) {
        if (mScanning) {
            mScanning = false;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                if (mBluetoothAdapter.isEnabled() && mLEScanner != null) mLEScanner.stopScan(mScanCallback);
            }
            Log.i("BLE SCAN", "BLE Scan stopped");

            if (getResult && mBluetoothMgrCallback != null) {
                mBluetoothMgrCallback.onScanStop(mBluetoothDevices);
            }
        }
    }

    @SuppressLint("NewApi")
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (mScanning) {
                Log.i("callbackType", String.valueOf(callbackType));
                Log.i("result", result.toString());
                BluetoothDevice btDevice = result.getDevice();
                addBluetoothDevice(btDevice);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    public List<BluetoothDevice> getAllBleDevices() {
        return mBluetoothDevices;
    }

    private void addBluetoothDevice(BluetoothDevice device) {
        if (!mBluetoothDevices.contains(device)) {
            mBluetoothDevices.add(device);
            mBluetoothDeviceNames.add(device.getAddress());
            if (mBluetoothMgrCallback != null) {
                mBluetoothMgrCallback.onDevicesUpdated(mBluetoothDeviceNames);
            }
//            if (BuildConfig.DEBUG && device.getAddress().equalsIgnoreCase("A4:D5:78:40:FF:48") && mBluetoothDevices.size() == 1) {
//                stopBleScanning();
//            }
            if (mBluetoothDeviceNames.size() >= 0) {
                stopBleScanning();
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            Log.i("onLeScan", device.toString());
//            connectToDevice(device);
            addBluetoothDevice(device);

        }
    };

    public interface SHBluetoothMgrCallback {
        void onDevicesUpdated(List<String> devices);

        void onScanStop(List<BluetoothDevice> bluetoothDevices);
    }
}
