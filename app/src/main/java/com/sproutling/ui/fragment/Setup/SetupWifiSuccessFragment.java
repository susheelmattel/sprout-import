package com.sproutling.ui.fragment.Setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sproutling.R;
import com.sproutling.ui.fragment.BaseFragment;

/**
 * Created by subram13 on 10/25/17.
 */

public class SetupWifiSuccessFragment extends BaseFragment {
    public static final String TAG ="SetupWifiSuccessFragment";

    public static SetupWifiSuccessFragment newInstance() {
        return new SetupWifiSuccessFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_setup_success, container, false);
        return view;
    }
}
