/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Setup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sproutling.R;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.ShTextView;

import static com.sproutling.ui.activity.SetupActivity.EXTRA_SETUP_SKIP;
import static com.sproutling.ui.activity.SetupActivity.SETTING_TYPE;
import static com.sproutling.ui.activity.SetupActivity.SETTING_TYPE_SET_UP;

public class SetupHubNotFoundFragment extends BaseFragment {
    public static final String TAG = "SetupHubNotFoundFragment";
    private String mSettingType;
    private OnHubNotFoundListener mOnHubNotFoundListener;
    private boolean mSkip;

    public SetupHubNotFoundFragment() {}

    public static SetupHubNotFoundFragment newInstance(String settingType, boolean skip) {
        SetupHubNotFoundFragment fragment = new SetupHubNotFoundFragment();
        Bundle arguments = new Bundle();
        arguments.putString(SETTING_TYPE, settingType);
        arguments.putBoolean(EXTRA_SETUP_SKIP, skip);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSettingType = getArguments().getString(SETTING_TYPE);
            mSkip = getArguments().getBoolean(EXTRA_SETUP_SKIP, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_hub_not_found, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.help).setOnClickListener(mOnHelpClickListener);
        ShTextView tvSkip = (ShTextView) view.findViewById(R.id.tv_skip);
        tvSkip.setOnClickListener(mOnSkipClickListener);
        tvSkip.setVisibility(SETTING_TYPE_SET_UP.equalsIgnoreCase(mSettingType) ? View.VISIBLE : View.INVISIBLE);
    }

    private View.OnClickListener mOnHelpClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getOnHubNotFoundListener().onHelpClicked();
        }
    };

    private View.OnClickListener mOnSkipClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSkip)
                getActivity().finish();
            else
                getOnHubNotFoundListener().onSkipClicked(true);
        }
    };

    private OnHubNotFoundListener getOnHubNotFoundListener() {
        if (mOnHubNotFoundListener == null)
            mOnHubNotFoundListener = (OnHubNotFoundListener) getActivity();
        return mOnHubNotFoundListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnHubNotFoundListener {
        void onSkipClicked(boolean clicked);

        void onHelpClicked();
    }
}
