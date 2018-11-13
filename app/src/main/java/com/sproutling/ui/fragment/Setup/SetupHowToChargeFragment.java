/*
 * Copyright (c) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sproutling.R;
import com.sproutling.ui.activity.VideoPlayerActivity;
import com.sproutling.ui.dialog.SetupHowToChargeDialog;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

public class SetupHowToChargeFragment extends BaseFragment {
    public static final String TAG = "SetupHowToChargeFragment";

    public SetupHowToChargeFragment() {
    }

    public static SetupHowToChargeFragment newInstance() {
        return new SetupHowToChargeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_how_to_charge, container, false);
        view.findViewById(R.id.video_image).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.instructions).setOnClickListener(mOnClickListener);
        return view;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.video_image:
                    Utils.logEvents(LogEvents.HOW_TO_CHARGE_VIDEO);
                    VideoPlayerActivity.startVideoActivity(getActivity(), VideoPlayerActivity.CHARGING_WEARABLE_VIDEO_URL);
                    break;
                case R.id.instructions:
                    Utils.logEvents(LogEvents.HOW_TO_CHARGE_STEP_BY_STEP);
                    new SetupHowToChargeDialog(getActivity()).show();
                    break;
            }
        }
    };
}
