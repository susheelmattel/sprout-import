package com.sproutling.common.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sproutling.common.R;

public class GenericAlertDialogFragment extends DialogFragment {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String BUTTON1_TXT = "button1Txt";
    public static final String BUTTON2_TXT = "button2Txt";

    OnDialogActionListner listner;

    // Empty constructor is required
    public GenericAlertDialogFragment() {
    }

    public static GenericAlertDialogFragment newInstance(String title, String message, String button1Txt, String button2Txt) {
        GenericAlertDialogFragment frag = new GenericAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        args.putString(BUTTON1_TXT, button1Txt);
        args.putString(BUTTON2_TXT, button2Txt);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.DialogFragmentPopup);
        dialog.setContentView(R.layout.dialog_generic_alert_layout);

        // Get field from view
        String title = getArguments().getString(TITLE);
        String message = getArguments().getString(MESSAGE);
        String button1Txt = getArguments().getString(BUTTON1_TXT);
        String button2Txt = getArguments().getString(BUTTON2_TXT);

        TextView titleTv = dialog.findViewById(R.id.title);
        TextView messageTv = dialog.findViewById(R.id.message);
        TextView button1Tv = dialog.findViewById(R.id.button1_txt);
        TextView button2Tv = dialog.findViewById(R.id.button2_txt);


        titleTv.setText(title);
        messageTv.setText(message);
        button1Tv.setText(button1Txt);
        button2Tv.setText(button2Txt);

        button1Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listner != null) {
                    listner.onNegativeButtonClick();
                }

                dismiss();
            }
        });

        button2Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listner != null) {
                    listner.onPositiveButtonClick();
                }

                dismiss();
            }
        });


        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);
        return dialog;
    }


    public void setOnClickListener(OnDialogActionListner onClickListener){
        listner = onClickListener;
    }

    public interface OnDialogActionListner {
        void onPositiveButtonClick();

        void onNegativeButtonClick();

    }

}
