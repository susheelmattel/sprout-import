/*
 * Copyright (c) 2017 Fuhu, Inc. All rights reserved.
 */

package com.sproutling.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;

/**
 * Created by bradylin on 7/31/17.
 */

public class DeleteSummaryDialogFragment extends DialogFragment {

    public static final String EXTRA_TYPE = "extra_type";

    private OnDeleteSummaryListener mListener;

    private String mType;

    public static DeleteSummaryDialogFragment newInstance(String type) {
        DeleteSummaryDialogFragment fragment = new DeleteSummaryDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_TYPE, type);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getString(EXTRA_TYPE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.DeleteSummeryPopup);
        dialog.setContentView(R.layout.dialogfragment_delete_summary);
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.yes).setOnClickListener(mOnClickListener);
        dialog.findViewById(R.id.cancel).setOnClickListener(mOnClickListener);

        ((ShTextView) dialog.findViewById(R.id.title)).setText(String.format(getString(R.string.dialog_delete_summary_title), mType));
        ((ShTextView) dialog.findViewById(R.id.message)).setText(String.format(getString(R.string.dialog_delete_summary_content), mType));

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener = (OnDeleteSummaryListener) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.yes:
                    if (mListener != null) mListener.onDeleteClicked();
                case R.id.cancel:
                    dismiss();
                    break;
            }
        }
    };

    public interface OnDeleteSummaryListener {
        void onDeleteClicked();
    }
}

