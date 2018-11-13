/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */
package com.sproutling.ui.fragment.status;

import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuhu.states.app.AStateFragment;
import com.fuhu.states.interfaces.IStatePayload;
import com.sproutling.R;
import com.sproutling.databinding.FragmentCoachmarksBinding;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.activity.StatusActivity;
import com.sproutling.ui.dialog.LearningPeriodStartDialog;
import com.sproutling.ui.view.StatusProgressBar;
import com.sproutling.ui.view.StatusScreen.StatusScreenCoachmarksBackground;
import com.sproutling.ui.widget.StatusView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

/**
 * Created by xylonchen on 2017/5/17.
 */

public class CoachmarkFragment extends AStateFragment {

    public static final String TAG = CoachmarkFragment.class.getSimpleName();
    public static CoachmarkFragment sInstance;
    private FragmentCoachmarksBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_coachmarks, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding.setBtnHandler(new BtnHandler());
    }

    @Override
    public void onStateChanged(IStatePayload payload) {
        if (StatusActivity.sInstance != null) {
            mBinding.setBatteryValue(StatusActivity.sInstance.mBatteryValue);

            if (StatusActivity.sInstance.mCurrentChildItem != null) {
                mBinding.setChildName(StatusActivity.sInstance.mCurrentChildItem.firstName);
            }
        }
    }

    public class BtnHandler {

        //For Tooltips welcome page.
        public void onLetsGoClicked() {
            Utils.logEvents(LogEvents.USER_STARTED_ONBOARDING_TUTORIAL);
            showTooltipsPage(StatusScreenCoachmarksBackground.BIG_CIRCLE_PAGE);
        }

        //For Tooltips welcome page.
        public void onNoThanksClicked() {
            Utils.logEvents(LogEvents.USER_DISMISSED_ONBOARDING_TUTORIAL);
            setTooltipsVisible(false);

            if (!StatusActivity.sIsCalledByReplay) {
                SSManagement.UserAccountInfo userAccountInfo = AccountManagement.getInstance(StatusActivity.sInstance).getUserAccountInfo();
                long lpStartTime = SharedPrefManager.getLong(getActivity(), SharedPrefManager.SPKey.LONG_LEARNING_PERIOD_START_TIMESTAMP, -1L);
                if (SSManagement.TYPE_GUARDIAN.equalsIgnoreCase(userAccountInfo.type) && lpStartTime == -1) {
//                    StatusActivity.sInstance.showAlarmDialog(IntegrationDialog.LEARNING_PERIOD_START);
                    LearningPeriodStartDialog learningPeriodStartDialog = new LearningPeriodStartDialog(getActivity());
                    learningPeriodStartDialog.show();
                }
            }
            StatusActivity.sIsCalledByReplay = false; //reset the flag;
        }

        //For Tooltips Big circle page.
        public void onBigCircleNextClicked() {
            showTooltipsPage(StatusScreenCoachmarksBackground.SMALL_CIRCLE_PAGE);
        }

        //For Tooltips Small circle page.
        public void onSmallCircleNextClicked() {
            showTooltipsPage(StatusScreenCoachmarksBackground.RECTANGLE_PAGE);
        }

        //For Tooltips Retangle page.
        public void onRectangleNextClicked() {
            setTooltipsVisible(false);

            if (!StatusActivity.sIsCalledByReplay) {
                SSManagement.UserAccountInfo userAccountInfo = AccountManagement.getInstance(StatusActivity.sInstance).getUserAccountInfo();
                long lpStartTime = SharedPrefManager.getLong(getActivity(), SharedPrefManager.SPKey.LONG_LEARNING_PERIOD_START_TIMESTAMP, -1L);
                if (SSManagement.TYPE_GUARDIAN.equalsIgnoreCase(userAccountInfo.type) && lpStartTime == -1) {
//                    StatusActivity.sInstance.showAlarmDialog(IntegrationDialog.LEARNING_PERIOD_START);
                    LearningPeriodStartDialog learningPeriodStartDialog = new LearningPeriodStartDialog(getActivity());
                    learningPeriodStartDialog.show();
                }
            } else {
                StatusActivity.sIsCalledByReplay = false; //reset the flag;
                StatusActivity.sInstance.goToTimeline(true);
            }
        }

        //For Tooltips Progress page.
        public void onProgressNextClicked() {
            setTooltipsVisible(false);
        }
    }

    StatusProgressBar.OnCenterPChangeListener onCenterPChangeListener;

    public void showTooltipsPage(int toolTipsStatus) {
        mBinding.layoutWelcomePage.setVisibility(View.GONE);
        mBinding.layoutBigCirclePage.setVisibility(View.GONE);
        mBinding.layoutSmallCirclePage.setVisibility(View.GONE);
        mBinding.layoutRetanglePage.setVisibility(View.GONE);
        mBinding.layoutProgressPage.setVisibility(View.GONE);
        mBinding.rootStatusView.setVisibility(View.GONE);
        mBinding.layoutRootStatusView.setVisibility(View.VISIBLE);
        mBinding.layoutRootStatusView.setBackgroundResource(R.drawable.shape_status_bg);

        int[] location = new int[2];
        int paddingY;

        switch (toolTipsStatus) {
            case StatusScreenCoachmarksBackground.WELCOME_PAGE:
                mBinding.rootStatusView.setVisibility(View.VISIBLE);
                mBinding.CoachmarksBackground.setWelcomePage();
                mBinding.rootStatusView.setIconCurrentPosition(12, 0);
                mBinding.layoutWelcomePage.setVisibility(View.VISIBLE);

                break;

            case StatusScreenCoachmarksBackground.BIG_CIRCLE_PAGE:
                mBinding.rootStatusView.setVisibility(View.VISIBLE);
                mBinding.rootStatusView.setMode(StatusView.MODE_AWAKE);
                mBinding.layoutBigCirclePage.setVisibility(View.VISIBLE);
                mBinding.rootStatusView.getProgressbar().getLocationOnScreen(location);

                final int paddingy = getResources().getDimensionPixelSize(R.dimen.big_circle_error_spacing);
                final int cx = location[0];
                final int cy = location[1];
                onCenterPChangeListener = new StatusProgressBar.OnCenterPChangeListener() {
                    @Override
                    public void onCenterPChange(Point c) {
                        Point centerP = mBinding.rootStatusView.getProgressbar().getCenterP();
                        mBinding.CoachmarksBackground.setBigCirclePage(cx + centerP.x, cy + mBinding.rootStatusView.getProgressbar().getProgressR() - (5 * paddingy)  );

                    }
                };

                mBinding.rootStatusView.getProgressbar().setOnCenterPChangeListener(onCenterPChangeListener);
                break;

            case StatusScreenCoachmarksBackground.SMALL_CIRCLE_PAGE:
                mBinding.rootStatusView.setVisibility(View.VISIBLE);
                mBinding.rootStatusView.setMode(StatusView.MODE_AWAKE);

                mBinding.layoutSmallCirclePage.setVisibility(View.VISIBLE);
                mBinding.imgControl.getLocationOnScreen(location);

                int moveUpMargin = (int) getResources().getDimension(R.dimen.margin1);
                mBinding.CoachmarksBackground.setSmallCirclePage(location[0] + mBinding.imgControl.getWidth() / 2,
                        location[1] - mBinding.imgControl.getHeight() / 2 + mBinding.imgControl.getPaddingTop() + mBinding.imgControl.getPaddingBottom() - (5 * moveUpMargin));
                break;

            case StatusScreenCoachmarksBackground.RECTANGLE_PAGE:
                mBinding.rootStatusView.setVisibility(View.VISIBLE);
                mBinding.rootStatusView.setMode(StatusView.MODE_AWAKE);
                mBinding.layoutRetanglePage.setVisibility(View.VISIBLE);

                mBinding.txtDescripRetanglePage.setText(String.format(getString(R.string.tooltips_timeline_descrip), StatusActivity.sInstance.mCurrentChildItem.firstName));

                mBinding.childNameButton.getLocationOnScreen(location);
                paddingY = (int) getResources().getDimension(R.dimen.paddingY);
                int margin = (int) getResources().getDimension(R.dimen.margin);

                mBinding.CoachmarksBackground.setRetanglePage(location[0] - margin,
                        location[1] - (3 * paddingY) - (2 * margin),
                        location[0] + mBinding.childNameButton.getWidth() + margin,
                        location[1] + mBinding.childNameButton.getHeight() - (3 * paddingY) + margin);
                break;

            case StatusScreenCoachmarksBackground.PROGRESS_PAGE:
                mBinding.rootStatusView.setVisibility(View.VISIBLE);
                mBinding.rootStatusView.setMode(StatusView.MODE_ASLEEP);
                mBinding.rootStatusView.setIconCurrentPosition(5, 0);

                mBinding.layoutProgressPage.setVisibility(View.VISIBLE);
                mBinding.rootStatusView.getProgressbar().getLocationOnScreen(location);

                final int padding = getResources().getDimensionPixelSize(R.dimen.big_circle_error_spacing);
                final int x = location[0];
                final int y = location[1];
                onCenterPChangeListener = new StatusProgressBar.OnCenterPChangeListener() {
                    @Override
                    public void onCenterPChange(Point c) {
                        Point centerP = mBinding.rootStatusView.getProgressbar().getCenterP();
                        mBinding.CoachmarksBackground.setProgressPage(x + centerP.x, y + mBinding.rootStatusView.getProgressbar().getProgressR() - padding);

                        mBinding.pStatusView.setMode(StatusView.MODE_TOOLTIP);
                        mBinding.pStatusView.setTooltipsPrediction(StatusView.timeToAngle(4, 0),
                                StatusView.timeToAngle(5, 0),
                                StatusView.timeToAngle(6, 0)); //Set StatusView
                    }
                };

                mBinding.rootStatusView.getProgressbar().setOnCenterPChangeListener(onCenterPChangeListener);

                break;
        }
    }

    public void setTooltipsVisible(boolean flag) {
        mBinding.Coachmarks.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

}
