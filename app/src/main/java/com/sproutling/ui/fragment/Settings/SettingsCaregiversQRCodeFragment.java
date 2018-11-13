/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.sproutling.R;
import com.sproutling.object.QRCodeContents;
import com.sproutling.object.QRCodeEncoder;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by bradylin on 12/13/16.
 */

public class SettingsCaregiversQRCodeFragment extends BaseFragment {
    public static final String TAG = "StgsCaregvrsQRCodeFgmnt";

    private static final int SIZE_QR_CODE = 116;

    private ImageView mQRCodeView, mQRCodeCoverView;
    private ShTextView mGenerateView;

    public static SettingsCaregiversQRCodeFragment newInstance() {
        return new SettingsCaregiversQRCodeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_caregivers_qr_code, container, false);

        mQRCodeView = (ImageView) view.findViewById(R.id.qr_code);
        mQRCodeCoverView = (ImageView) view.findViewById(R.id.qr_code_cover);
        mGenerateView = (ShTextView) view.findViewById(R.id.generate);
        mGenerateView.setOnClickListener(mOnGenerateClickListener);

        initUI();

        return view;
    }

    private void initUI() {
        getInvitation();
    }

    private View.OnClickListener mOnGenerateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getInvitation();
        }
    };

    private void generateQRCode(String data) {
        try {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            String encodedToken = Base64.encodeToString(dataBytes, Base64.DEFAULT).replace("\n", "").trim();
            QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(encodedToken,
                    null,
                    QRCodeContents.Type.TEXT,
                    BarcodeFormat.QR_CODE.toString(),
                    Utils.dpToPx(SIZE_QR_CODE));
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            mQRCodeView.setImageBitmap(bitmap);
            Utils.logEvents(LogEvents.CARE_GIVER_QR_CODE_GENERATED);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void getInvitation() {
        new AsyncTask<Void, Void, String>() {
            SSError mError;

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    SSManagement.User user = AccountManagement.getInstance(getActivity()).getUser();
                    SSManagement.UserAccountInfo userAccountInfo = AccountManagement.getInstance(getActivity()).getUserAccountInfo();
                    return SKManagement.invitations(user.accessToken, userAccountInfo.accountId);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String inviteToken) {
                showProgressBar(false);
                if (inviteToken != null) {
                    generateQRCode(inviteToken);
                } else
                    Log.d(TAG, "inviteToken is Null");
            }
        }.execute();
    }
}

