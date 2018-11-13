/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sproutling.R;

/**
 * Created by bradylin on 12/13/16.
 */

public class SettingsHelpFragment extends Fragment {
    public static final String TAG = "SettingsHelpFragment";

    public SettingsHelpFragment() {}

    public static SettingsHelpFragment newInstance() {
        return new SettingsHelpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_help, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        try {
//            mListener = (SettingsMainFragment.OnSettingsMainListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(getActivity().toString()
//                    + " must implement OnSettingsMainListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

}


