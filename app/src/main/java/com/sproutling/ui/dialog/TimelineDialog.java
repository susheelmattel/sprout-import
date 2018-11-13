/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShAlertView;

/**
 * Created by apple on 2016/12/28.
 */

public class TimelineDialog extends Dialog {

    private Handler mHandler;

    public TimelineDialog(Context ctx, Handler handler) {
        super(ctx, R.style.CustomDialog);

        ShAlertView shAlertView = new ShAlertView(ctx);
        shAlertView.setImgAlert(R.drawable.ic_timeline);
        shAlertView.setTitle(ctx.getString(R.string.timeline_dlg_timeline_title));
        shAlertView.setMessage(ctx.getString(R.string.timeline_dlg_timeline_text));
        shAlertView.setButtonText(ctx.getString(R.string.got_it));
        shAlertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(0);
                dismiss();
            }
        });
        setContentView(shAlertView);
        mHandler = handler;
    }
}
