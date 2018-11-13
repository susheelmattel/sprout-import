/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShAlertView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

/**
 * Created by apple on 2016/12/28.
 */

public class NightSleepDialog extends Dialog {

    public NightSleepDialog(Context ctx) {
        super(ctx, R.style.CustomDialog);
        ShAlertView shAlertView = new ShAlertView(ctx);
        shAlertView.setImgAlert(R.drawable.ic_night_sleep);
        shAlertView.setTitle(ctx.getString(R.string.timeline_dlg_night_sleep_title));
        shAlertView.setMessage(ctx.getString(R.string.timeline_dlg_night_sleep_text));
        shAlertView.setButtonText(ctx.getString(R.string.got_it));
        shAlertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setContentView(shAlertView);
        Utils.logEvents(LogEvents.TIMELINE_WHATIS);
    }

}
