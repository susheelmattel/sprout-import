/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.status;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuhu.states.app.AStateFragment;
import com.fuhu.states.interfaces.IStatePayload;
import com.fuhu.states.payloads.Payload;
import com.sproutling.R;
import com.sproutling.databinding.FragmentFlowerBinding;
import com.sproutling.services.SSManagement;
import com.sproutling.states.Actions;
import com.sproutling.states.States;
import com.sproutling.ui.activity.StatusActivity;
import com.sproutling.ui.widget.FlowerItem;
import com.sproutling.ui.widget.StatusView;
import com.sproutling.utils.DeviceFitting;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

/**
 * Created by moi0312 on 2017/1/26.
 */

public class FlowerFragment extends AStateFragment {
    private static final String TAG = FlowerFragment.class.getSimpleName();
    private static final long DURATION_FLOWER = 500;

    private static int sStartAngle = -90;

    private FragmentFlowerBinding mBinding;
    private Point mCenterP;
    private Resources mRes;
    private int mItemCircleR;
    private FlowerItem[] mFlowerItems;
    private int mItemAngle;
    private int mFlowerR;
    private SSManagement.Child mChild;
    private ValueAnimator mFlowerGoAnim, mFlowerBackAnim;
    private boolean mBackViewsClickable = true;

    private ValueAnimator.AnimatorUpdateListener onLearningPeriodAnimUpdate = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float currentValue = (float) animation.getAnimatedValue();
            doLearningPeriodAnim(currentValue);
        }
    };
    private View.OnClickListener exitLearningPeriodListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBackViewsClickable) {
                mBackViewsClickable = false;
                mFlowerBackAnim.start();
            }
        }
    };

    private void updateStatusIconProgress() {
        if (getActivity() != null && getActivity() instanceof StatusActivity) {
            ((StatusActivity) getActivity()).updateStatusCircleProgress();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentFlowerBinding.inflate(inflater, container, true);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRes = getResources();

        //init learning period scene and animation
        int w = DeviceFitting.getFitDeviceSizes(getActivity()).get(DeviceFitting.KEY_DISPLAY_SHORT_SIDE);
        int h = mRes.getDimensionPixelSize(R.dimen.statusView_learning_height);
        if (mCenterP == null) {
            mCenterP = new Point(w / 2, h / 2);
        }

        mItemCircleR = mRes.getDimensionPixelSize(R.dimen.circle_sizeSmallR);
        mFlowerR = mRes.getDimensionPixelSize(R.dimen.flowerR);
        mFlowerItems = new FlowerItem[FlowerItem.FLOWER_ITEM_COUNT];
        mFlowerGoAnim = ValueAnimator.ofFloat(0, 1);
//        mFlowerGoAnim.setStartDelay(100);
        mFlowerBackAnim = ValueAnimator.ofFloat(1, 0);
        mFlowerGoAnim.setDuration(DURATION_FLOWER);
        mFlowerBackAnim.setDuration(DURATION_FLOWER);
        mFlowerGoAnim.addUpdateListener(onLearningPeriodAnimUpdate);
        mFlowerBackAnim.addUpdateListener(onLearningPeriodAnimUpdate);
        mFlowerBackAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                updateStatusIconProgress();
                disPatchAction(Actions.SCENE_EVENT,
                        new Payload().put(Actions.Key.SCENE, StatusActivity.SCENE_STATUS).put(Actions.Key.VIEW, mBinding.statusView)
                );
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Need to restore notification if it was displaying before showing this scene
                ((StatusActivity) getActivity()).setNotificationVisible(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mFlowerGoAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBackViewsClickable = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                doLearningPeriodAnim(1);
                mBinding.txtBack.setOnClickListener(exitLearningPeriodListener);
                mBinding.statusView.setBabyFaceClickListener(exitLearningPeriodListener);
                mBackViewsClickable = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mBinding.statusView.getLayoutParams().width = mCenterP.x * 2;
        mBinding.statusView.getLayoutParams().height = mCenterP.y * 2;
        mBinding.statusView.setMode(StatusView.MODE_LEARNING_PERIOD_INFO);

        mItemAngle = 360 / FlowerItem.FLOWER_ITEM_COUNT;
        for (int i = 0; i < FlowerItem.FLOWER_ITEM_COUNT; i++) {
            FlowerItem flower = new FlowerItem(getActivity());
            flower.setIndex(i);
            mFlowerItems[i] = flower;
            mBinding.flowerContainer.addView(mFlowerItems[i], 0);
            flower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFlowerItemClick(((FlowerItem) v).getIndex());
                }
            });
        }
        //
        mBinding.flowerContainer.invalidate();
        mFlowerGoAnim.start();
        //
        setFlowerDesc(-1);
    }

    private void onFlowerItemClick(int index) {
        for (int i = 0; i < FlowerItem.FLOWER_ITEM_COUNT; i++) {
            FlowerItem item = mFlowerItems[i];
            if (index == i) {
                item.setClicked(true);
                setFlowerDesc(i);
            } else {
                item.setClicked(false);
            }
        }
    }

    private void setFlowerDesc(int index) {
        int titleId = R.string.flower_about_title;
        int descId = R.string.flower_about_desc;
        if (index >= 0) {
            titleId = FlowerItem.sTitles[index];
            descId = FlowerItem.sDescriptions[index];
            Utils.logEvents(FlowerItem.sEventNames[index]);
        } else {
            Utils.logEvents(LogEvents.LEARNING_PERIOD_ABOUT);
        }

        String childGenderStr = mRes.getString(mChild.isMale() ? R.string.he : R.string.she);
        if (index == 3) { // Duration description
            childGenderStr = mRes.getString(mChild.isMale() ? R.string.his : R.string.her);

        }
        mBinding.flowerDescView.setTitleAndDesc(
                mRes.getString(titleId).replace("\n", " "),
                mRes.getString(descId, mChild.firstName, childGenderStr)
        );
    }

    private void doLearningPeriodAnim(float currentValue) {
        float currentR = mFlowerR * currentValue;
        for (int i = 0; i < FlowerItem.FLOWER_ITEM_COUNT; i++) {
            FlowerItem item = mFlowerItems[i];
            item.setAlpha(currentValue);
            float[] point = Utils.getPointByAngle(mCenterP.x, mCenterP.y, currentR, sStartAngle, mItemAngle * (i - 1 / 2));
            item.setX(point[0] - item.getWidth() / 2);
            item.setY(point[1] - item.getHeight() / 2);
        }
        mBinding.flowerDescView.setAlpha(currentValue);
        mBinding.txtBack.setAlpha(currentValue);
    }

    @Override
    public void onStateChanged(IStatePayload payload) {
        Object childItem = ((Payload) payload).get(States.Key.DATAITEM_CHILD);
        if (childItem != null) {
            mChild = (SSManagement.Child) childItem;
        }
    }
}
