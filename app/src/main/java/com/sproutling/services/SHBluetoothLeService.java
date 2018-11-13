/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.sproutling.object.BleConnectionEvent;
import com.sproutling.object.ConnectionReadWriteErrorEvent;
import com.sproutling.object.DeviceSerialEvent;
import com.sproutling.object.PublicKeyEvent;
import com.sproutling.object.WifiConnectionEvent;
import com.sproutling.object.WifiItem;
import com.sproutling.object.WifiListEvent;
import com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment.NONE;
import static com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment.WEP;
import static com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment.WPA;
import static com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment.WPA2;
import static com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment.WPA2_ENTERPRISE;
import static com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment.WPA_ENTERPRISE;
import static com.sproutling.utils.LogEvents.HUB_CONNECTED_TO_WIFI;
import static com.sproutling.utils.LogEvents.HUB_CONNECT_TO_WIFI_FAIL;
import static com.sproutling.utils.LogEvents.HUB_WIFI_AP_COUNT;

//import static com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment.WEP;

//import org.apache.commons.codec.binary.StringUtils;

/**
 * Created by subram13 on 1/11/17.
 */

public class SHBluetoothLeService extends Service {
    private final static String TAG = SHBluetoothLeService.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private boolean mScanning = false;
    private boolean mSettingUpWifi = false;
    private boolean mWifiConfigWriteSuccess = false;
    private int mConnectionState = GATT_STATE_DISCONNECTED;
//    private String mBleDeviceAddress;


    private static final int GATT_STATE_DISCONNECTED = 0;
    private static final int GATT_STATE_CONNECTING = 1;
    private static final int GATT_STATE_CONNECTED = 2;
    private static final int WIFI_STATE_DISCONNECTED = 0;
    private static final int WIFI_STATE_CONNECTING = 1;
    private static final int WIFI_STATE_CONNECTED = 2;
    private static final int WIFI_STATE_CONNECTED_WITH_IP = 3;

    public static final Integer SECURITY_OPEN = 1;
    public static final Integer SECURITY_WPA = 2;
    public static final Integer SECURITY_WEP = 3;

    public final static String ACTION_GATT_CONNECTED =
            "com.sproutling.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.sproutling.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.sproutling.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.sproutling.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.sproutling.bluetooth.le.EXTRA_DATA";

    public static final Charset UTF_8 = StandardCharsets.UTF_8;
//    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String UUID_SPROUTLING_BLE_SERVICE = "4482ACDF-D160-4E10-8FE3-82599F334433";
    private static final String UUID_SPROUTLING_DEVICE_INFO_SERVICE = "0000180A-0000-1000-8000-00805F9B34FB";
    private static final String UUID_SPROUTLING_BLE_WIFI_SCAN_SERVICE = "FB8C0001-D224-11E4-85A1-0002A5D5C51B";
    private static final String UUID_SPROUTLING_BLE_WIFI_CONNECT_SERVICE = "77880001-d229-11e4-8689-0002a5d5c51b";
    private static final String UUID_SPROUTLING_BLE_WIFI_SCAN_MODE_CHARACTERISTIC = "1F5FCD82-FE15-4962-A6B2-BAEDFF11FD76";
    private static final String UUID_SPROUTLING_BLE_WIFI_LIST_CHARACTERISTIC = "89277C18-8A79-48A4-89C8-11F754F0B02D";
    private static final String UUID_SPROUTLING_BLE_WIFI_LIST_COUNT_CHARACTERISTIC = "9C500CD5-C84A-459A-AF95-26E73C2D49D7";
    private static final String UUID_SPROUTLING_BLE_WIFI_CONNECTION_PARAMETERS_CHARACTERISTIC = "1317D1BC-5F64-4CD2-A5BE-A7C436BE6F88";
    private static final String UUID_SPROUTLING_BLE_WIFI_CONNECTION_STATE_CHARACTERISTIC = "ECB9EFD1-1CA6-4AE0-B579-2F58C6237F87";
    private static final String UUID_SPROUTLING_DEVICE_SERIAL_NUMBER_CHARACTERISTIC = "FAB97310-7E7E-4F52-AB2A-51B2332E01A3";
    private static final String UUID_SPROUTLING_DEVICE_PUBLIC_KEY_CHARACTERISTIC = "7A4747B5-B7FB-4643-9C5E-5778F77CAE7E";
    private static final String UUID_SPROUTLING_BLE_WIFI_ENCRYPTED_CONNECTION_PARAMETERS_CHARACTERISTIC = "1C4F894B-75D8-4008-8EC5-D3E77D75BD95";
    private static final String UUID_SPROUTLING_DEVICE_INFO_FIRMWARE_VERSION_CHARACTERISTIC = "00002A26-0000-1000-8000-00805F9B34FB";
    private static final String UUID_SPROUTLING_DEVICE_INFO_SERIAL_NUMBER_CHARACTERISTIC = "00002A25-0000-1000-8000-00805F9B34FB";
    private static final int WIFI_SCANNING_MODE_OFF = 0;
    private static final int WIFI_SCANNING_MODE_ON = 1;
    private static final int WAIT_TIME_FOR_NEXT_REQUEST = 1000;
    private static final int MAX_WIFI_SCANNING_TIME = 20000;
    private static final int MAX_WIFI_SETUP_TIME = 20000;
    private static final int MAX_WIFI_SETUP_TIME_COUNT = 2; // and extend max time if connecting in progress
    private int mExtendTimeoutCount = MAX_WIFI_SETUP_TIME_COUNT;
    private int mWifiConnState = WIFI_STATE_DISCONNECTED;
    private int mApListRequestCount = 0;
    private int mApListReceivedCount = 0;
    private WifiScanCallback mWifiScanCallback;
    private WifiConnectionCallback mWifiConnectionCallback;
    private DeviceCallback mDeviceCallback;
    private EncryptionPublicKeyCallback mEncryptionPublicKeyCallback;
    private boolean mIsNonErrorBleDisconnect = false;

    private ArrayList<WifiItem> mSsidList = new ArrayList<>();

    private boolean isWifiConnectionParamaterCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (UUID_SPROUTLING_BLE_WIFI_CONNECTION_PARAMETERS_CHARACTERISTIC.equalsIgnoreCase(characteristic.getUuid().toString())
                || UUID_SPROUTLING_BLE_WIFI_ENCRYPTED_CONNECTION_PARAMETERS_CHARACTERISTIC.equalsIgnoreCase(characteristic.getUuid().toString())) {
            return true;
        } else
            return false;
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = GATT_STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");

                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = GATT_STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                //close the connection after disconnection to release the resources.
                close();
                broadcastUpdate(intentAction);
            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onServicesDiscoverd: Bond State : " + gatt.getDevice().getBondState());
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "successful characteristic write");
                if (isWifiConnectionParamaterCharacteristic(characteristic)) {
                    Log.d(TAG, "successful characteristic write: UUID_SPROUTLING_BLE_WIFI_CONNECTION_PARAMETERS_CHARACTERISTIC");
                    mWifiConnectionCallback.onWriteConnectionMode(characteristic);
                } else {
                    Log.d(TAG, "successful characteristic write: UUID_SPROUTLING_BLE_WIFI_SCAN_MODE_CHARACTERISTIC");
                    mWifiScanCallback.onWriteScanMode(characteristic);
                }
            } else {
                Log.d(TAG, String.valueOf(characteristic.getValue()));
                if (isWifiConnectionParamaterCharacteristic(characteristic)) {
                    Log.e(TAG, "Failed characteristic write: UUID_SPROUTLING_BLE_WIFI_CONNECTION_PARAMETERS_CHARACTERISTIC");
                    mSettingUpWifi = false;
                    //broadcast wifi connection failed message through event bus.
                    EventBus.getDefault().post(new WifiConnectionEvent(WifiConnectionEvent.NOT_CONNECTED));
                } else if (UUID_SPROUTLING_BLE_WIFI_SCAN_MODE_CHARACTERISTIC.equalsIgnoreCase(characteristic.getUuid().toString())) {
                    Log.e(TAG, "Failed characteristic write: UUID_SPROUTLING_BLE_WIFI_SCAN_MODE_CHARACTERISTIC");
//                    failed to get the wifi list
                    //broadcast message through event bus.
                    EventBus.getDefault().post(new WifiListEvent(mSsidList));
                    stopWifiScanning();
                }
                Log.e(TAG, "Error onCharacteristic status: " + status);
                errorCode(characteristic, status);
            }

        }

        private void errorCode(BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED) {
                Log.e(TAG, "GATT_REQUEST_NOT_SUPPORTED");
            } else if (status == BluetoothGatt.GATT_INVALID_OFFSET) {
                Log.e(TAG, "GATT_INVALID_OFFSET");
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
                Log.e(TAG, "GATT_INSUFFICIENT_ENCRYPTION");
            } else if (status == BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER) {
                Log.e(TAG, "CONNECTION_PRIORITY_LOW_POWER");
            } else if (status == BluetoothGatt.CONNECTION_PRIORITY_HIGH) {
                Log.e(TAG, "CONNECTION_PRIORITY_HIGH");
            } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                Log.e(TAG, "no write permission");
            } else if (status == BluetoothGatt.GATT_CONNECTION_CONGESTED) {
                Log.e(TAG, "problem with remote device");
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                Log.e(TAG, "GATT failure");
            } else if (status == BluetoothGatt.GATT) {
                Log.e(TAG, "GATT");
            } else {

                Log.e(TAG, "unknown error in characteristic :" + characteristic.getUuid().toString());
            }
            disconnect(false);
            EventBus.getDefault().post(new ConnectionReadWriteErrorEvent(status));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                processCharacteristicResponse(characteristic);
                Log.i(TAG, "onCharacteristicRead status: " + status);
            } else {
                Log.e(TAG, "Error onCharacteristicRead status: " + status);

                //failed to get the wifi list
                //broadcast message through event bus.
                EventBus.getDefault().post(new WifiListEvent(mSsidList));
                stopWifiScanning();
                errorCode(characteristic, status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            processCharacteristicResponse(characteristic);
            Log.i(TAG, "onCharacteristicChanged status: " + characteristic.getUuid().toString());
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
        BleConnectionEvent bleConnectionEvent = new BleConnectionEvent(BleConnectionEvent.CONNECTED, mIsNonErrorBleDisconnect);
        if (ACTION_GATT_DISCONNECTED.equals(action)) {
            bleConnectionEvent = new BleConnectionEvent(BleConnectionEvent.DISCONNECTED, mIsNonErrorBleDisconnect);
        }
        EventBus.getDefault().post(bleConnectionEvent);
    }

    private void processCharacteristicResponse(final BluetoothGattCharacteristic characteristic) {
        String characteristicStr = characteristic.getUuid().toString().toUpperCase();
        Log.d(TAG, "processCharacteristicResponse :" + characteristicStr);
        if (UUID_SPROUTLING_BLE_WIFI_CONNECTION_STATE_CHARACTERISTIC.equalsIgnoreCase(characteristicStr)) {
            Log.d(TAG, "Notify : UUID_SPROUTLING_BLE_WIFI_CONNECTION_STATE_CHARACTERISTIC");
            mWifiConnectionCallback.onWifiConnectionModeUpdate(characteristic);
        } else if (UUID_SPROUTLING_DEVICE_SERIAL_NUMBER_CHARACTERISTIC.equalsIgnoreCase(characteristicStr)) {

            mDeviceCallback.onReadDeviceSerial(characteristic);
        } else if (UUID_SPROUTLING_DEVICE_INFO_SERIAL_NUMBER_CHARACTERISTIC.equalsIgnoreCase(characteristicStr)) {
            mDeviceCallback.onReadDeviceSerial(characteristic);
        } else if (UUID_SPROUTLING_DEVICE_PUBLIC_KEY_CHARACTERISTIC.equalsIgnoreCase(characteristicStr)) {
            mEncryptionPublicKeyCallback.onReadKey(characteristic);
        } else {
            if (mWifiScanCallback != null) {
                mWifiScanCallback.onReadScanMode(characteristic);
            } else {
                Log.d(TAG, "broadcastUpdate: mWifiScanCallback is null");
            }
        }
    }

    public class LocalBinder extends Binder {
        public SHBluetoothLeService getService() {
            return SHBluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        //Initialize it to false to identify manual  or automatic disconnect
        mIsNonErrorBleDisconnect = false;
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = GATT_STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }
        Log.i("BLE My device address:", mBluetoothAdapter.getAddress());

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        }

        // hack to force refresh the device cache and avoid problems with characteristic services read from cache and not updated
        refreshDeviceCache();

        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = GATT_STATE_CONNECTING;
        return true;
    }

    /**
     * Call to private Android method 'refresh'
     * This method does actually clear the cache from a bluetooth device. But the problem is that we don't have access to it. But in java we have reflection, so we can access this method.
     * http://stackoverflow.com/questions/22596951/how-to-programmatically-force-bluetooth-low-energy-service-discovery-on-android
     */
    public boolean refreshDeviceCache() {
        try {
            BluetoothGatt localBluetoothGatt = mBluetoothGatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh");
            if (localMethod != null) {
                boolean result = (Boolean) localMethod.invoke(localBluetoothGatt);
                if (result) {
                    Log.d(TAG, "Bluetooth refresh cache");
                }
                return result;
            }
        } catch (Exception localException) {
            Log.e(TAG, "An exception occurred while refreshing device");
        }
        return false;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect(boolean nonErrorDisconnect) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mIsNonErrorBleDisconnect = nonErrorDisconnect;
        Log.d(TAG, "BLE disconnect called.");
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void stopWifiScanning() {
        enableApListNotification(false);
        mScanning = false;
        mApListReceivedCount = 0;
        mApListRequestCount = 0;
        mSsidList.clear();

//        disconnect();
    }

    public void setupDeviceSerialCallback() {
        if (mDeviceCallback == null) {
            mDeviceCallback = new DeviceCallback() {
                @Override
                public void onReadDeviceSerial(BluetoothGattCharacteristic characteristic) {
                    if (characteristic != null) {
                        byte[] data = characteristic.getValue();
                        if (data != null && data.length > 0) {
                            ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
                            String deviceSerial = new String(byteBuffer.array(), UTF_8);
                            Log.d(TAG, "getDeviceSerialNumber :" + deviceSerial);
                            EventBus.getDefault().post(new DeviceSerialEvent(deviceSerial));

                            if (!Utils.isValidSerialID(deviceSerial)) {
                                Log.e(TAG, "Invalid serial ID format from HUB : " + deviceSerial);
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("invalidSerialID", deviceSerial);
                                    Utils.logEvents(LogEvents.INVALID_SERIAL_ID, jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "Valid serial ID format from HUB : " + deviceSerial);
                            }
                        } else {
                            disconnect(false);
                        }
                    }
                }
            };
        }
    }

    // Get Serial from Sproutling Service
    public void getDeviceSerialNumber() {
        setupDeviceSerialCallback();
        BluetoothGattService bluetoothGattService = getSproutlingDeviceService();
        if (bluetoothGattService != null) {
            BluetoothGattCharacteristic readCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_DEVICE_SERIAL_NUMBER_CHARACTERISTIC));
            readCharacteristic(readCharacteristic);
        }
    }

    // Get Serial from Device Info Service
    public void getDeviceSerialNumberv2() {
        setupDeviceSerialCallback();
        BluetoothGattService bluetoothGattDeviceInfoService = getDeviceInfoService();
        BluetoothGattCharacteristic readCharacteristic = bluetoothGattDeviceInfoService != null
                ? bluetoothGattDeviceInfoService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_DEVICE_INFO_SERIAL_NUMBER_CHARACTERISTIC))
                : null;

        if (readCharacteristic != null) {
            readCharacteristic(readCharacteristic);
        }
    }

    PublicKey mPublicKey = null;

    // Custom encrypt
    public byte[] RSAEncrypt(final byte input[]) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {

        if (mPublicKey != null) {
            try {
                Cipher cipher = Cipher.getInstance("RSA/None/OAEPwithSHA-1andMGF1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, mPublicKey);
                byte[] encryptedBytes = cipher.doFinal(input);
                return encryptedBytes;
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    // Generate Key from string
    public PublicKey getKeyFromString(String keystr) throws Exception {
        // Remove the first and last lines
        String pubKeyPEM = keystr.replace("-----BEGIN PUBLIC KEY-----\n", "");
        pubKeyPEM = pubKeyPEM.replace("-----END PUBLIC KEY-----", "");

        // Base64 decode the data
        byte[] encoded = Base64.decode(pubKeyPEM, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pubkey = kf.generatePublic(keySpec);

        return pubkey;
    }

    StringBuilder mPublicKeyString = new StringBuilder();

    private void setPublicKeyCallback() {
        if (mEncryptionPublicKeyCallback == null) {
            mEncryptionPublicKeyCallback = new EncryptionPublicKeyCallback() {
                @Override
                public void onReadKey(BluetoothGattCharacteristic characteristic) {
                    if (characteristic != null) {
                        byte[] data = characteristic.getValue();
                        if (data != null && data.length > 0) {
                            ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
                            String chunk = new String(byteBuffer.array(), UTF_8);
                            Log.d(TAG, "Public key whole Value: " + chunk);
                            if (chunk.contains("-----BEGIN PUBLIC KEY-----"))
                                mPublicKeyString = new StringBuilder(); // new key
                            mPublicKeyString.append(chunk);
                            if (chunk.contains("-----END PUBLIC KEY-----")) {
                                // Done, create RSAPublicKey
                                Log.d(TAG, "PublicKey Received");
                                try {
                                    mPublicKey = getKeyFromString(mPublicKeyString.toString());
                                    EventBus.getDefault().post(new PublicKeyEvent(mPublicKey));
                                } catch (Exception e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                }
                            } else {
                                // Getting public key takes multiple reads
                                getPublicKey();
                            }
                        }
                    }
                }
            };
        }
    }

    // Get Public Key used for custom encryption
    public void getPublicKey() {
        setPublicKeyCallback();
        BluetoothGattService service = getSproutlingDeviceService();
        BluetoothGattCharacteristic readCharacteristic = service == null ? null : service.getCharacteristic(UUID.fromString(UUID_SPROUTLING_DEVICE_PUBLIC_KEY_CHARACTERISTIC));

        if (readCharacteristic != null) {
            Log.d(TAG, "Get PublicKey");
            readCharacteristic(readCharacteristic);
        } else {
            EventBus.getDefault().post(new PublicKeyEvent(null));
        }
    }

    public void reConnect() {
        connect(mBluetoothDeviceAddress);
    }

    public void setUpWifi(final String ssid, final String pwd, @ManualNetworkTypeFragment.NetworkSecurityType final String securityType, boolean isManualSetup) {
        if (!TextUtils.isEmpty(ssid)) {
            if (TextUtils.isEmpty(securityType)) {
                throw new IllegalArgumentException("Network security type is missing");
            }
            if (!ManualNetworkTypeFragment.NONE.equalsIgnoreCase(securityType) && TextUtils.isEmpty(pwd)) {
                throw new IllegalArgumentException("password is missing");
            }
            //Initialize it to false to identify manual  or automatic disconnect
            mIsNonErrorBleDisconnect = false;
            mSettingUpWifi = true;
            if (mWifiConnectionCallback == null) {
                mWifiConnectionCallback = new WifiConnectionCallback() {
                    @Override
                    public void onWifiConnectionModeUpdate(BluetoothGattCharacteristic characteristic) {
                        if (UUID_SPROUTLING_BLE_WIFI_CONNECTION_STATE_CHARACTERISTIC.equalsIgnoreCase(characteristic.getUuid().toString())) {
                            mWifiConnState = getIntFromBytes(characteristic.getValue());
                            if (mSettingUpWifi) {
                                //TODO:broadcast connection state
                                switch (mWifiConnState) {
                                    case WIFI_STATE_DISCONNECTED:
                                        Log.d(TAG, "Current wifi conn state is disconnected");
                                        break;
                                    case WIFI_STATE_CONNECTING:
                                        Log.d(TAG, "Current wifi conn state is connecting");
                                        break;
                                    case WIFI_STATE_CONNECTED:
                                        Log.d(TAG, "Current wifi conn state is connected");
                                        Log.d(TAG, "waiting for conn state to get till connected with ip");
                                        break;
                                    case WIFI_STATE_CONNECTED_WITH_IP:
                                        Log.d(TAG, "Current wifi conn state is connected with ip");
                                        //broadcast connected state
                                        mSettingUpWifi = false;
                                        EventBus.getDefault().post(new WifiConnectionEvent(WifiConnectionEvent.CONNECTED));
                                        disconnect(true);
                                        Utils.logEvents(HUB_CONNECTED_TO_WIFI);
                                        break;
                                    default:
                                        Log.d(TAG, "Current wifi conn state is unknown :" + mWifiConnState);
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onWriteConnectionMode(BluetoothGattCharacteristic characteristic) {
                        if (isWifiConnectionParamaterCharacteristic(characteristic)) {
                            Log.d(TAG, "wifi connection params write success");
                            mWifiConfigWriteSuccess = true;
                            enableWifiConnectionStateNotification(true);
                        }
                    }

                };
            }
            //reset the flag before starting the wifi config
            mWifiConfigWriteSuccess = false;
            waitAndWriteWifiParams(ssid, pwd, securityType);
            mExtendTimeoutCount = MAX_WIFI_SETUP_TIME_COUNT;
            stopWifiSetupAfterDelay(MAX_WIFI_SETUP_TIME);

        } else {
            throw new IllegalArgumentException("Ssid is missing");
        }
    }

    private void enableWifiConnectionStateNotification(boolean enable) {
        Log.d(TAG, "enableWifiConnectionStateNotification :" + enable);
        BluetoothGattService bluetoothGattService = getWifiConnectService();
        if (bluetoothGattService != null) {
            BluetoothGattCharacteristic notificationCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_CONNECTION_STATE_CHARACTERISTIC));
            mBluetoothGatt.setCharacteristicNotification(notificationCharacteristic, enable);
            //get the first default descriptor for wifi connection state notification.
            BluetoothGattDescriptor bluetoothGattDescriptor = notificationCharacteristic.getDescriptors().get(0);
            bluetoothGattDescriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
        }
    }

    private void stopWifiSetupAfterDelay(final int timeMs) {
        AsyncTask<Void, Void, Void> stopTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SystemClock.sleep(timeMs);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mSettingUpWifi) {
                    if (mWifiConnState == WIFI_STATE_CONNECTED && mExtendTimeoutCount > 0) {
                        // If connected, extend timeout to give more time to get ip address
                        // In practice it can take over 20 seconds to go from Connected to Connected with IP
                        Log.d(TAG, "Extend WifiSetup Timeout");
                        stopWifiSetupAfterDelay(MAX_WIFI_SCANNING_TIME);
                        mExtendTimeoutCount--;
                    } else {
                        mSettingUpWifi = false;
                        enableWifiConnectionStateNotification(false);
                        Utils.logEvents(HUB_CONNECT_TO_WIFI_FAIL);
                        //broadcast wifi connection failed message through event bus.
                        EventBus.getDefault().post(new WifiConnectionEvent(WifiConnectionEvent.NOT_CONNECTED));
                    }
                }
            }
        };
        AsyncTaskCompat.executeParallel(stopTask);
    }

    private void waitAndWriteWifiParams(final String ssid, final String pwd, @ManualNetworkTypeFragment.NetworkSecurityType final String securityType) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                SystemClock.sleep(WAIT_TIME_FOR_NEXT_REQUEST);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//
//                writeWifiParams(ssid, pwd, securityType);
//            }
//        }.execute();
        writeWifiParams(ssid, pwd, securityType);
    }

    private void waitAndRequestWifiConnectionState() {
        //wait for one second and poll for current state again
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                SystemClock.sleep(WAIT_TIME_FOR_NEXT_REQUEST);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                getWifiConnectionState();
//            }
//        }.execute();
        getWifiConnectionState();
    }

    private void getWifiConnectionState() {
        Log.d(TAG, "Getting Wifi Connection state");
        BluetoothGattService bluetoothGattService = getWifiConnectService();
        if (bluetoothGattService != null) {
            BluetoothGattCharacteristic readCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_CONNECTION_STATE_CHARACTERISTIC));
            readCharacteristic(readCharacteristic);
        }
    }

    private void writeWifiParams(String ssid, String pwd, @ManualNetworkTypeFragment.NetworkSecurityType String securityType) {
        if (!TextUtils.isEmpty(ssid) && !TextUtils.isEmpty(pwd)) {
            Log.d(TAG, "Writing connection params");
            final BluetoothGattService wifiConnectService = getWifiConnectService();
            if (wifiConnectService != null) {
                BluetoothGattCharacteristic writeCharacteristic = wifiConnectService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_CONNECTION_PARAMETERS_CHARACTERISTIC));
                byte[] writeData = getWriteBytes(ssid, pwd, securityType);
                Log.i(TAG, "Wifi Conn data Byte array size :" + writeData.length);
                Log.i(TAG, "WIFI CONN DATA :" + writeData);

                // Try Encryption, older hubs do not support encrypt
                BluetoothGattCharacteristic writeEncryptCharacteristic = wifiConnectService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_ENCRYPTED_CONNECTION_PARAMETERS_CHARACTERISTIC));
                if (writeEncryptCharacteristic != null && mPublicKey != null) {
                    Log.d(TAG, "Encrypt wifi params");
                    try {
                        writeData = RSAEncrypt(writeData);
                        writeCharacteristic = writeEncryptCharacteristic;
                    } catch (Exception e) {
                        Log.e(TAG, "encrypting wifi: " + e.getLocalizedMessage());
                    }
                }
                writeCharacteristic.setValue(writeData);
                writeCharacteristic(writeCharacteristic);
            }
        }
    }

    private byte[] getWriteBytes(String ssid, String pwd, @ManualNetworkTypeFragment.NetworkSecurityType String securityType) {
        Integer ssidLength = ssid.getBytes().length;
        Integer pwdLength = pwd.getBytes().length;
        int size = 3 + ssidLength + pwdLength;
        Log.d(TAG, "Buffer Array size is :" + size);
        Integer networkSecurityType = getNetworkSecurity(securityType);
        ByteBuffer byteBuffer = ByteBuffer.allocate(size).put(networkSecurityType.byteValue());
        byteBuffer.put(ssidLength.byteValue());
        byteBuffer.put(ssid.getBytes());
        byteBuffer.put(pwdLength.byteValue());
        byteBuffer.put(pwd.getBytes());
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer.array();
    }

    private int getNetworkSecurity(@ManualNetworkTypeFragment.NetworkSecurityType String securityType) {
        if (TextUtils.isEmpty(securityType)) {
            return SECURITY_WPA;
        } else {
            switch (securityType) {
                case NONE:
                    return SECURITY_OPEN;
                case WPA:
                    return SECURITY_WPA;
                case WPA2:
                    return SECURITY_WPA;
                case WPA_ENTERPRISE:
                    return SECURITY_WPA;
                case WPA2_ENTERPRISE:
                    return SECURITY_WPA;
                case WEP:
                    return SECURITY_WEP;
                default:
                    return SECURITY_WPA;
            }
        }
    }

    public void scanAvailableWifi() {
        mScanning = true;
        //Initialize it to false to identify manual  or automatic disconnect
        mIsNonErrorBleDisconnect = false;
        List<BluetoothGattService> gattServices = getSupportedGattServices();
         /*check if the service is available on the device*/
        final BluetoothGattService wifiService = getWifiScanService();
        if (wifiService != null) {
            if (mWifiScanCallback == null) {
                mWifiScanCallback = new WifiScanCallback() {
                    @Override
                    public void onReadScanMode(BluetoothGattCharacteristic characteristic) {
                        if (characteristic != null) {
                            if (UUID_SPROUTLING_BLE_WIFI_SCAN_MODE_CHARACTERISTIC.equalsIgnoreCase(characteristic.getUuid().toString())) {
                                Log.d(TAG, "Read : UUID_SPROUTLING_BLE_WIFI_SCAN_MODE_CHARACTERISTIC");
                                final int wifiScanStatus = getWifiScanStatus(characteristic.getValue());
                                Log.d(TAG, "Wifi scan status is " + wifiScanStatus);
                                if (wifiScanStatus != -1) {
                                    if (wifiScanStatus == (WIFI_SCANNING_MODE_OFF)) {
                                        //if scanning is off, turn on wifi scanning in the hub
                                        setHubScanMode(WIFI_SCANNING_MODE_ON);
                                    } else if (wifiScanStatus == (WIFI_SCANNING_MODE_ON)) {
                                        //when scan mode is on, get the AP list
                                        if (mScanning) {
//                                            getApListCount();
                                            enableApListNotification(true);
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "Invalid wifi scan status :" + wifiScanStatus);
                                    Log.d(TAG, "Disconneting BLE connection cause of invalid wifi scan status");
                                    disconnect(false);
                                }
                            } else if (UUID_SPROUTLING_BLE_WIFI_LIST_CHARACTERISTIC.equalsIgnoreCase(characteristic.getUuid().toString())) {
                                Log.d(TAG, "Read : UUID_SPROUTLING_BLE_WIFI_LIST_CHARACTERISTIC");
                                Utils.logEvents(HUB_WIFI_AP_COUNT);
                                processWifiListResponse(characteristic.getValue());
                                //broadcast message through event bus.
                                EventBus.getDefault().post(new WifiListEvent(mSsidList));
                                if (mScanning) {
                                    //get all the ssid from hub
                                    if (mApListReceivedCount < mApListRequestCount) {
                                        Log.d(TAG, "Totol Ap list count :" + mApListRequestCount + " Received Ap list count :" + mApListReceivedCount);
                                        waitAndGetWifiList();
                                    } else {
                                        //once all ssid are received, stop requesting. when user pulls refresh we can query again.
                                        stopWifiScanning();
                                    }
                                }
                            } else if (UUID_SPROUTLING_BLE_WIFI_LIST_COUNT_CHARACTERISTIC.equalsIgnoreCase(characteristic.getUuid().toString())) {
                                Log.d(TAG, "Read : UUID_SPROUTLING_BLE_WIFI_LIST_COUNT_CHARACTERISTIC");
                                mApListRequestCount = getIntFromBytes(characteristic.getValue());
                                Log.i(TAG, "AP List count :" + mApListRequestCount);
                                if (mScanning) {
                                    if (mApListRequestCount > 0) {
                                        //once we get the AP list count, start get wifi list
//                                        getWifiList();
                                        waitAndGetWifiList();
                                    }
//                                    else {
//                                        waitAndGetApListCount();
//                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onWriteScanMode(BluetoothGattCharacteristic characteristic) {
                        if (characteristic != null) {
                            if (UUID_SPROUTLING_BLE_WIFI_SCAN_MODE_CHARACTERISTIC.equalsIgnoreCase(characteristic.getUuid().toString())) {
                                final int wifiScanStatus = getIntFromBytes(characteristic.getValue());
                                Log.d(TAG, "Wifi scan status is " + wifiScanStatus);
                                if (wifiScanStatus != -1) {
                                    if (wifiScanStatus == (WIFI_SCANNING_MODE_ON)) {
                                        BluetoothGattCharacteristic readCharacteristic = wifiService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_SCAN_MODE_CHARACTERISTIC));
                                        readCharacteristic(readCharacteristic);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onSetScanMode() {

                    }
                };
            }
            mApListReceivedCount = 0;
            mApListRequestCount = 0;
            mSsidList.clear();
            setHubScanMode(WIFI_SCANNING_MODE_ON);
            stopWifiScanningAfterDelay(MAX_WIFI_SCANNING_TIME);
        }
    }

    private void stopWifiScanningAfterDelay(final int timeMs) {
        AsyncTask<Void, Void, Void> waitTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SystemClock.sleep(timeMs);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mScanning) {
                    //broadcast message through event bus.
                    EventBus.getDefault().post(new WifiListEvent(mSsidList));
                    stopWifiScanning();
                }
            }
        };
        AsyncTaskCompat.executeParallel(waitTask);
    }

//    private void waitAndGetApListCount() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                //wait 1 second before we poll again to get the wifi list
//                SystemClock.sleep(1000);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                getApListCount();
//            }
//        };
//        getApListCount();
//    }

//    private void getApListCount() {
//        Log.d(TAG, "Getting AP list count");
//        BluetoothGattService bluetoothGattService = getWifiScanService();
//        if (bluetoothGattService != null) {
//            BluetoothGattCharacteristic readCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_LIST_COUNT_CHARACTERISTIC));
//            if (readCharacteristic != null)
//                readCharacteristic(readCharacteristic);
//        }
//    }

    private void enableApListNotification(boolean enable) {
        BluetoothGattService bluetoothGattService = getWifiScanService();
        if (bluetoothGattService != null) {
            BluetoothGattCharacteristic notificationCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_LIST_COUNT_CHARACTERISTIC));
            mBluetoothGatt.setCharacteristicNotification(notificationCharacteristic, enable);
            //get the first default descriptor for APlist notification.
            BluetoothGattDescriptor bluetoothGattDescriptor = notificationCharacteristic.getDescriptors().get(0);
            bluetoothGattDescriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

            mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
        }
    }

    private void waitAndGetWifiList() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                //wait 1 second before we poll again to get the wifi list
//                SystemClock.sleep(1000);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                if (mScanning) {
//                    getWifiList();
//                }
//            }
//        };
        getWifiList();
    }

    private void getWifiList() {
        BluetoothGattService bluetoothGattService = getWifiScanService();
        if (bluetoothGattService != null) {
            BluetoothGattCharacteristic readCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_LIST_CHARACTERISTIC));
            if (readCharacteristic != null)
                Log.d(TAG, "Getting Wifi List");
            readCharacteristic(readCharacteristic);
        }
    }

    private void processWifiListResponse(byte[] dataVal) {
        if (dataVal != null && dataVal.length > 0) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.wrap(dataVal).order(ByteOrder.LITTLE_ENDIAN);
                byte[] data = byteBuffer.array();
                Log.i(TAG, "Wifi list raw response:" + data);
                int responseLength = data.length;
                if (responseLength > 3) {
                    int i = 0;
                    while (i < responseLength) {
                        Byte rssiByte = data[i++];
                        int range = rssiByte.intValue();
                        Byte networkSecurityTypeByte = data[i++];
                        int networkSecurityType = networkSecurityTypeByte.intValue();
                        int ssidLength = data[i++];
                        if (i < responseLength) {
                            byte[] ssidBytes = Arrays.copyOfRange(data, i, i + ssidLength);
                            String ssid = new String(ssidBytes, UTF_8);
                            if (!TextUtils.isEmpty(ssid)) {
                                mApListReceivedCount++;
                                if (!containsSsid(ssid)) {
                                    mSsidList.add(new WifiItem(ssid, range, networkSecurityType));
                                    Log.d(TAG, "Added AP :" + ssid);
                                }
                                i = i + ssidLength;
                            }
                        } else {
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        } else {
            Log.e(TAG, "Wifi List data is empty");
        }
    }

    private boolean containsSsid(String ssid) {
        for (WifiItem wifiItem : mSsidList) {
            if (wifiItem.getSsid().equals(ssid)) {
                return true;
            }
        }
        return false;
    }

    private int getWifiScanStatus(byte[] data) {
        int scanStatus = getIntFromBytes(data);
        if (scanStatus != WIFI_SCANNING_MODE_OFF && scanStatus != WIFI_SCANNING_MODE_ON) {
            scanStatus = -1;
        }
        return scanStatus;
    }

    private int getIntFromBytes(byte[] data) {
        int retVal = -1;
        if (data != null && data.length > 0) {
            Log.d(TAG, "getIntFromBytes: data length :" + data.length);
            Log.d(TAG, "getIntFromBytes: raw data string  :" + data);
            ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            retVal = new BigInteger(byteBuffer.array()).intValue();
            Log.d(TAG, "getIntFromBytes: Integer value:" + retVal);
        } else {
            Log.d(TAG, "getIntFromBytes: data is empty");
        }
        return retVal;
    }

    // Device Info Service has serial number, public key for encryption
    private BluetoothGattService getSproutlingDeviceService() {
        if (mBluetoothGatt != null) {
            final BluetoothGattService sproutlingDeviceService = mBluetoothGatt.getService(UUID.fromString(UUID_SPROUTLING_BLE_SERVICE));
            if (sproutlingDeviceService == null) {
                Log.w(TAG, "sproutlingDeviceService BLE Service not found");
                return null;
            } else return sproutlingDeviceService;
        }
        Log.w(TAG, "sproutlingDeviceService BLE Service not found");
        return null;
    }

    // Device Info Service has serial number, fw version
    private BluetoothGattService getDeviceInfoService() {
        if (mBluetoothGatt != null) {
            final BluetoothGattService deviceService = mBluetoothGatt.getService(UUID.fromString(UUID_SPROUTLING_DEVICE_INFO_SERVICE));
            if (deviceService == null) {
                Log.w(TAG, "deviceInfoService Service not found");
                return null;
            } else return deviceService;
        }
        Log.w(TAG, "deviceInfoService Service not found");
        return null;
    }

    private BluetoothGattService getWifiScanService() {
        if (mBluetoothGatt != null) {
            final BluetoothGattService wifiService = mBluetoothGatt.getService(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_SCAN_SERVICE));
            if (wifiService == null) {
                Log.w(TAG, "wifiService BLE Service not found");
                return null;
            } else return wifiService;
        }
        return null;
    }

    private BluetoothGattService getWifiConnectService() {
        if (mBluetoothGatt != null) {
            final BluetoothGattService wifiConnectService = mBluetoothGatt.getService(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_CONNECT_SERVICE));
            if (wifiConnectService == null) {
                Log.w(TAG, "wifiConnectService BLE Service not found");
                return null;
            } else return wifiConnectService;
        }
        return null;
    }

    private void setHubScanMode(Integer mode) {
        final BluetoothGattService wifiConnectService = getWifiScanService();
        if (wifiConnectService != null) {
            Log.d(TAG, "Setting Hub scan mode:" + mode);
//            BluetoothGattDescriptor bluetoothGattDescriptor = new BluetoothGattDescriptor(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_SCAN_MODE_CHARACTERISTIC),BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED);
            BluetoothGattCharacteristic writeCharacteristic = wifiConnectService.getCharacteristic(UUID.fromString(UUID_SPROUTLING_BLE_WIFI_SCAN_MODE_CHARACTERISTIC));
//            writeCharacteristic.addDescriptor(bluetoothGattDescriptor);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1).put(mode.byteValue()).order(ByteOrder.LITTLE_ENDIAN);
            writeCharacteristic.setValue(byteBuffer.array());

            writeCharacteristic(writeCharacteristic);
        }
    }


    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(final BluetoothGattCharacteristic characteristic) {

        //wait for one second and read
        AsyncTask<Void, Void, Void> readTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SystemClock.sleep(WAIT_TIME_FOR_NEXT_REQUEST);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                    Log.w(TAG, "BluetoothAdapter not initialized");
                    return;
                }


                mBluetoothGatt.readCharacteristic(characteristic);
            }
        };
        AsyncTaskCompat.executeParallel(readTask);
    }

    public void writeCharacteristic(final BluetoothGattCharacteristic characteristic) {
        //wait for one second and write
        AsyncTask<Void, Void, Void> writeTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SystemClock.sleep(WAIT_TIME_FOR_NEXT_REQUEST);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                    Log.w(TAG, "BluetoothAdapter not initialized");
                    return;
                }
//                mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                boolean result = mBluetoothGatt.writeCharacteristic(characteristic);
                Log.i(TAG, "Write characteristic :" + result);
            }
        };
        AsyncTaskCompat.executeParallel(writeTask);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    private interface WifiScanCallback {
        void onReadScanMode(BluetoothGattCharacteristic characteristic);

        void onWriteScanMode(BluetoothGattCharacteristic characteristic);

        void onSetScanMode();
    }

    private interface WifiConnectionCallback {
        void onWifiConnectionModeUpdate(BluetoothGattCharacteristic characteristic);

        void onWriteConnectionMode(BluetoothGattCharacteristic characteristic);
    }

    private interface DeviceCallback {
        void onReadDeviceSerial(BluetoothGattCharacteristic characteristic);
    }

    private interface EncryptionPublicKeyCallback {
        void onReadKey(BluetoothGattCharacteristic characteristic);
    }

}
