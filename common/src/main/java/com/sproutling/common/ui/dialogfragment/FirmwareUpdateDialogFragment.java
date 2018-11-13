/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.ui.dialogfragment;

import android.os.Bundle;
import android.view.View;

import com.sproutling.common.R;

import org.jetbrains.annotations.NotNull;


/**
 * Created by 322511 on 7/31/18.
 */

public class FirmwareUpdateDialogFragment extends BaseDialogFragment {


    public static FirmwareUpdateDialogFragment newInstance() {
        FirmwareUpdateDialogFragment fragment = new FirmwareUpdateDialogFragment();
        return fragment;
    }


    @Override
    public int getImage() {
        return R.drawable.ic_firmware_update_modal;
    }

    @NotNull
    @Override
    public String getTitle() {
        return getString(R.string.firmware_update);
    }

    @NotNull
    @Override
    public String getMessage() {
        return getString(R.string.firmware_update_available_message);
    }

    @NotNull
    @Override
    public String getButtonText() {
        return getString(R.string.firmware_update_availble);
    }

    @NotNull
    @Override
    public String getButton2Text() {
        return getString(R.string.cancel_firmware);
    }

    @Override
    public boolean getButton2Visibility() {
        return true;
    }

}
