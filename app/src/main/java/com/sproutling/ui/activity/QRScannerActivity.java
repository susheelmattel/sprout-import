/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.auth0.android.jwt.DecodeException;
import com.auth0.android.jwt.JWT;
import com.sproutling.R;
import com.sproutling.ui.view.QRCodeReaderView;
import com.sproutling.ui.view.ScannerView;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import java.nio.charset.StandardCharsets;

import static com.sproutling.utils.LogEvents.CARE_GIVER_QR_CODE_EXPIRED;

/**
 * Created by bradylin on 1/31/17.
 */

public class QRScannerActivity extends BaseActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private static final String TAG = "QRScannerActivity";

    private static final int PERMISSION_REQUEST_CAMERA = 1;

    private QRCodeReaderView mQRCodeReaderView;
    private ScannerView mScannerFilter;
    private ShTextView mMessageView, mAllowView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        mScannerFilter = (ScannerView) findViewById(R.id.scanner_filter);
        mMessageView = (ShTextView) findViewById(R.id.message);
        mAllowView = (ShTextView) findViewById(R.id.allow);
        mAllowView.setOnClickListener(mOnAllowClickListener);
        ((ShTextView) findViewById(R.id.navigation_title)).setText(R.string.qr_scanner_title);
        findViewById(R.id.navigation_back).setOnClickListener(mOnBackClickListener);
        findViewById(R.id.navigation_action).setVisibility(View.GONE);

        mHandler = new Handler();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QRScannerActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            showDisabledCameraView(false);
        } else {
            initCamera();
            showDisabledCameraView(true);
        }
//        mScannerFilter.setClearAreaSize(Utils.dpToPx(200));
    }

    void showDisabledCameraView(boolean cameraOn) {
        if (cameraOn) {
            mScannerFilter.setVisibility(View.VISIBLE);
            setMessage(R.string.qr_scanner_message);
            mAllowView.setVisibility(View.INVISIBLE);
        } else {
            mScannerFilter.setVisibility(View.INVISIBLE);
            setMessage(R.string.qr_scanner_message_allow_camera);
            mAllowView.setVisibility(View.VISIBLE);
        }
    }

    View.OnClickListener mOnAllowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActivityCompat.requestPermissions(QRScannerActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    };

    View.OnClickListener mOnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCamera();
                    showDisabledCameraView(true);
                } else {
                    showPermissionWarning();
                    showDisabledCameraView(false);
                }
                return;
            }
        }
    }

    private void showPermissionWarning() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert));

        dialog.setTitle(R.string.qr_scanner_permission_title);
        dialog.setMessage(R.string.qr_scanner_permission_message);
        dialog.setPositiveButton(R.string.qr_scanner_permission_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        finish();
                    }
                });
        dialog.setNegativeButton(R.string.qr_scanner_permission_negative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    void initCamera() {
        mQRCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mQRCodeReaderView.setVisibility(View.VISIBLE);
        mQRCodeReaderView.setOnQRCodeReadListener(this);
        mQRCodeReaderView.setQRDecodingEnabled(true);
        mQRCodeReaderView.setAutofocusInterval(2000L);
        mQRCodeReaderView.setTorchEnabled(true);
        mQRCodeReaderView.setBackCamera();
    }

    boolean isExpired(String token) {
        try {
            byte[] data = Base64.decode(token, Base64.DEFAULT);
            String inviteToken = new String(data, StandardCharsets.UTF_8);
            JWT jwt = new JWT(inviteToken);
            return System.currentTimeMillis() > jwt.getExpiresAt().getTime();
        } catch (DecodeException exception) {
            //Invalid token
            Log.d(TAG, "Invalid Token: " + token);
            Utils.logEvents(LogEvents.CARE_GIVER_QR_CODE_PARSING_FAILED);
            return false;
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Invalid Token: " + token);
            Utils.logEvents(LogEvents.CARE_GIVER_QR_CODE_PARSING_FAILED);
            return false;
        }
    }

    void setMessage(int messageId) {
        mMessageView.setText(messageId);
        mMessageView.setTextColor(Utils.getColor(this, messageId == R.string.qr_scanner_message_expired ? R.color.dullOrange : R.color.white));
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if (isExpired(text)) {
            setMessage(R.string.qr_scanner_message_expired);
            Utils.logEvents(CARE_GIVER_QR_CODE_EXPIRED);
            mQRCodeReaderView.pause(); // pause for a few seconds to avoid flickering
            mHandler.postDelayed(mResumeCameraRunnable, 3000);
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(SetupActivity.EXTRA_CAREGIVER_DATA, text);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    @Override
    public void onQRCodeProcessing() {
        setMessage(R.string.qr_scanner_message_wait);
    }

    @Override
    public void onQRCodeNotFound() {
        setMessage(R.string.qr_scanner_message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mQRCodeReaderView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mQRCodeReaderView.stopCamera();
        }
    }

    Runnable mResumeCameraRunnable = new Runnable() {
        @Override
        public void run() {
            mQRCodeReaderView.resume();
        }
    };
}
