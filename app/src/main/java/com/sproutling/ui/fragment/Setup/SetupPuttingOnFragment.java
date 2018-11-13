/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sproutling.R;
import com.sproutling.ui.activity.VideoPlayerActivity;
import com.sproutling.ui.dialog.SetupPuttingOnDialog;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

public class SetupPuttingOnFragment extends BaseFragment {
    public static final String TAG = "SetupPuttingOnFragment";

    public SetupPuttingOnFragment() {}

    public static SetupPuttingOnFragment newInstance() {
        return new SetupPuttingOnFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_putting_on, container, false);
        view.findViewById(R.id.video_image).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.steps).setOnClickListener(mOnClickListener);
        return view;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.video_image:
                    Utils.logEvents(LogEvents.PUTTING_ON_WEARABLE_VIDEO);
                    VideoPlayerActivity.startVideoActivity(getActivity(),VideoPlayerActivity.PUTTING_ON_WEARABLE_VIDEO_URL);
                    break;
                case R.id.steps:
                    Utils.logEvents(LogEvents.PUTTING_ON_STEP_BY_STEP);
                    new SetupPuttingOnDialog(getActivity()).show();
                    break;
            }
        }
    };
}
