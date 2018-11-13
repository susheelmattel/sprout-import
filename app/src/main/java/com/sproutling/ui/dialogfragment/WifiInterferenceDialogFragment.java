package com.sproutling.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShAlertView;

/**
 * Created by subram13 on 11/29/17.
 */

public class WifiInterferenceDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.DialogFragmentPopup);
        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setTitle(getString(R.string.wifi_interference));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.wifi_interference_msg));
        int appNameLength = getString(R.string.app_name).length();
        if (spannableStringBuilder.length() >= 204 + appNameLength) {
            spannableStringBuilder.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 183 + appNameLength, 204 + appNameLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        shAlertView.setMessage(spannableStringBuilder);
        shAlertView.setButtonText(getString(R.string.got_it));
        shAlertView.setImgAlert(R.drawable.ic_error);
        shAlertView.setOnClickListener(mOnClickListener);
        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
}
