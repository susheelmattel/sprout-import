/*
 * Copyright (c) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.sproutling.R;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

/**
 * Created by brady.lin on 2016/12/28.
 */

public class SetupHowToChargeDialog extends Dialog {

    public SetupHowToChargeDialog(Context context) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dialog_setup_how_to_charge);
        setCanceledOnTouchOutside(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Utils.logEvents(LogEvents.HOW_TO_CHARGE_CLOSE);
            }
        });
    }
}
