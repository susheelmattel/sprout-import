package com.sproutling.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sproutling.R;
import com.sproutling.object.StepPosition;
import com.sproutling.ui.widget.PagerDotView;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by subram13 on 10/12/17.
 */

public class LearningPeriodDialog extends Dialog {
    private List<View> mLearningPeriodList;
    private PagerDotView mPagerDotView;
    private int mPagePosition = -1;
    private ShTextView mTvClose;

    public LearningPeriodDialog(@NonNull Context context, String childName) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dialog_pager_one_button);
        setCanceledOnTouchOutside(true);
        mTvClose = (ShTextView) findViewById(R.id.tv_close);
        mTvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        LayoutInflater inflater = getLayoutInflater();

        ViewPager pager = (ViewPager) findViewById(R.id.info_pager);
        pager.addOnPageChangeListener(mOnPageChangeListener);
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.dot_layout);

        View lp1 = inflater.inflate(R.layout.learning_period_modal_1, null);
        View lp2 = inflater.inflate(R.layout.learning_period_modal_2, null);
        ShTextView description = (ShTextView) lp2.findViewById(R.id.description);

        String descriptionText = String.format(getContext().getString(R.string.unlock_wake_prediction_msg), TextUtils.isEmpty(childName) ? getContext().getString(R.string.your_baby) : childName);
        description.setText(descriptionText);
        View lp3 = inflater.inflate(R.layout.learning_period_modal_3, null);
        View lp4 = inflater.inflate(R.layout.learning_period_modal_4, null);
        View lp5 = inflater.inflate(R.layout.learning_period_modal_5, null);


        mLearningPeriodList = new ArrayList<>();
        mLearningPeriodList.add(lp1);
        mLearningPeriodList.add(lp2);
        mLearningPeriodList.add(lp3);
        mLearningPeriodList.add(lp4);
        mLearningPeriodList.add(lp5);


        LearningPeriodDialog.ViewPagerAdapter viewPagerAdapter = new LearningPeriodDialog.ViewPagerAdapter();
        pager.setAdapter(viewPagerAdapter);

        mPagerDotView = new PagerDotView(context, dotsLayout, mLearningPeriodList.size());
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String jsonString = Utils.toJsonString(new StepPosition(mPagePosition));
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                    Utils.logEvents(LogEvents.LEARNING_PERIOD_MODAL_CLOSE, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mPagerDotView.setPosition(position);
            mPagePosition = position + 1;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    private class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mLearningPeriodList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                container.addView(mLearningPeriodList.get(position), 0);
            } catch (Exception ignored) {
            }
            return mLearningPeriodList.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }


}
