package com.sproutling.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShAlertView;

/**
 * Created by subram13 on 5/25/17.
 */

public class FirmwareUpdateDialogFragment extends DialogFragment {
    public static final String TAG = "FirmwareUpdateDialogFragment";
    public static final String BTN_TXT = "BTN_TXT";
    public static final String MSG_TXT = "MSG_TXT";

    private String mBtnText;
    private String mMessage;


    public static FirmwareUpdateDialogFragment newInstance(String btnText, String msg) {
        FirmwareUpdateDialogFragment fragment = new FirmwareUpdateDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(BTN_TXT, btnText);
        arguments.putString(MSG_TXT, msg);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mBtnText = arguments.getString(BTN_TXT);
            mMessage = arguments.getString(MSG_TXT);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.SoftwareUpdatePopup);


        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setImgAlert(R.mipmap.software_update);
        shAlertView.setTitle(getString(R.string.firmware_update));
        shAlertView.setMessage(mMessage);
        shAlertView.setButtonText(mBtnText);
        shAlertView.setOnClickListener(mOnClickListener);

        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);


        return dialog;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

}
