/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fuhu.states.payloads.Payload;
import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.services.SSManagement;
import com.sproutling.states.Actions;
import com.sproutling.ui.activity.StatusActivity;
import com.sproutling.ui.widget.ShTextView;

/**
 * Created by Xylon on 2017/1/24.
 */

public class IntegrationDialog extends Dialog implements View.OnClickListener {

    public static final int ROLL_OVER_SIX_MONTH = 100;
    public static final int ROLL_OVER = 101;
    public static final int STIRRING = 102;
    public static final int LEARNING_PERIOD_START = 103;
    public static final int LEARNING_PERIOD_FINISH = 104;

    private int mDialogType;
    private LinearLayout mLinearBtnFirst;
    private ShTextView mTxtTitle, mTxtDescription, mTtnFirst, mBtnSecond;
    private ImageView mImgMain;
    private Context mContext;

    public IntegrationDialog(Context context) {
        super(context, R.style.CustomDialog);
        mContext = context;
        setContentView(R.layout.dialog_summary);

        mLinearBtnFirst = (LinearLayout) findViewById(R.id.linear_btnFirst);
        mTxtTitle = (ShTextView) findViewById(R.id.txtTitle);
        mImgMain = (ImageView) findViewById(R.id.imgMain);
        mTxtDescription = (ShTextView) findViewById(R.id.txtDescription);
        mTtnFirst = (ShTextView) findViewById(R.id.btnFirst);
        mBtnSecond = (ShTextView) findViewById(R.id.btnSecond);

        mTtnFirst.setOnClickListener(this);
        mBtnSecond.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFirst:
                if (mDialogType == ROLL_OVER || mDialogType == ROLL_OVER_SIX_MONTH) {
                    App.getInstance().dispatchAction(Actions.DISMISS_ROLLOVER, new Payload().put(Actions.Key.DISABLE_ROLLOVER_NOTIFICATION, true), StatusActivity.sInstance);
                    dismiss();
                }
                break;

            case R.id.btnSecond:

                if (mDialogType == ROLL_OVER) {
                    App.getInstance().dispatchAction(Actions.DISMISS_ROLLOVER, new Payload(), StatusActivity.sInstance);
                    dismiss();
                } else if (mDialogType == ROLL_OVER_SIX_MONTH) {
                    App.getInstance().dispatchAction(Actions.DISMISS_ROLLOVER, new Payload(), StatusActivity.sInstance);
                    dismiss();
                } else if (mDialogType == STIRRING) {
                    dismiss();
                }
                break;
        }
    }

    public void setDialog(int targetDialog) {
        setDialog(targetDialog, null);
    }

    public void setDialog(int targetDialog, String childName) {
        setDialog(targetDialog, childName, SSManagement.Child.GENDER_GIRL);
    }

    public void setDialog(int targetDialog, String childName, String gender) {
        mDialogType = targetDialog;

        mLinearBtnFirst.setVisibility(View.GONE);
        mImgMain.setVisibility(View.GONE);

        if (mDialogType == ROLL_OVER) {
            mImgMain.setVisibility(View.VISIBLE);
            mLinearBtnFirst.setVisibility(View.VISIBLE);
            mTxtTitle.setText(R.string.turn_off_rollover_dlg_title);
            mImgMain.setImageResource(R.drawable.ic_rollover_dialog);
            mTxtDescription.setText(R.string.turn_off_rollover_dlg_text);
            mTtnFirst.setText(R.string.turn_off_rollover_dlg_yes);
            mBtnSecond.setText(R.string.turn_off_rollover_dlg_cancel);
        } else if (mDialogType == ROLL_OVER_SIX_MONTH) {
            mImgMain.setVisibility(View.VISIBLE);
            mLinearBtnFirst.setVisibility(View.VISIBLE);
            mTxtTitle.setText(R.string.rollover_dlg_title);
            mImgMain.setImageResource(R.drawable.ic_rollover_dialog);
            mTxtDescription.setText(R.string.rollover_dlg_text);
            mTtnFirst.setText(R.string.rollover_dlg_yes);
            mBtnSecond.setText(R.string.rollover_dlg_cancel);
        } else if (mDialogType == STIRRING) {
            mImgMain.setVisibility(View.VISIBLE);
            mTxtTitle.setText(R.string.stirring_dlg_title);
            mImgMain.setImageResource(R.drawable.ic_what_is_stirring);
            mTxtDescription.setText(R.string.stirring_dlg_text);
            mBtnSecond.setText(R.string.got_it);
        }
    }
}
