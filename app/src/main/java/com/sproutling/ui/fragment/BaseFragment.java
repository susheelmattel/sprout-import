/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.sproutling.R;
import com.sproutling.ui.fragment.interfaces.IBaseFragment;
import com.sproutling.ui.widget.ShAlertView;
import com.sproutling.ui.widget.ShCustomProgressBar;

import org.jetbrains.annotations.NotNull;

/**
 * Created by subram13 on 1/25/17.
 * <p>
 * This class holds common methods to all fragments. Extend this class while working on other fragments in this application.
 */


public class BaseFragment extends Fragment implements IBaseFragment {
    private ShCustomProgressBar mShCustomProgressBar;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShCustomProgressBar = new ShCustomProgressBar(getActivity());
    }

    public void showProgressBar(boolean show) {
        if (show) {
            hideKeyboard();
            if (mShCustomProgressBar != null) mShCustomProgressBar.show();
        } else {
            if (mShCustomProgressBar != null && mShCustomProgressBar.isShowing()) {
                mShCustomProgressBar.dismiss();
            }
        }
    }

    protected void hideKeyboard() {
        Activity activity = getActivity();
        if (activity == null) return;
        View view = activity.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void showAlertDialog(@NotNull String title, @NotNull String message, @NotNull String buttonText, int imageRes) {
        final Dialog dialog = new Dialog(getActivity(), R.style.DialogFragmentPopup);
        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setTitle(title);
        shAlertView.setMessage(message);
        shAlertView.setButtonText(buttonText);
        shAlertView.setImgAlert(imageRes);
        dialog.setContentView(shAlertView);
        shAlertView.setButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public interface IBaseFragmentListener {
        void navigateBack();
        void enableActionMenuItem(boolean enable);
    }
}
