/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.adapter.TimeLine;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.EventListDay;
import com.sproutling.utils.TimelineUtils;

import java.util.Calendar;
import java.util.List;


/**
 * Created by loren.hung on 2016/01/12.
 */

public class LearningAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ItemHolder mItemHolder;
    private String mChildFirstName;
    private Calendar mBirthDate = Calendar.getInstance();

    public LearningAdapter(Context context, String childFirstName, String childBirthDate) {
        mContext = context;
        mChildFirstName = childFirstName;

        String[] date = childBirthDate.split("-");
        mBirthDate.set(Integer.parseInt(date[0]), (Integer.parseInt(date[1]) - 1), Integer.parseInt(date[2]), 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_learning, parent, false);
        return new LearningAdapter.ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        mItemHolder = (ItemHolder) viewHolder;
        mItemHolder.txtBorn.setText(
                String.format(mContext.getString(R.string.was_born), TimelineUtils.getDateString(mBirthDate), mChildFirstName));
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        ShTextView txtBorn;
        public RecyclerView mRecyclerView;
        private LearningWeekAdapter mLearningWeekAdapter;

        private ItemHolder(View view) {
            super(view);
            txtBorn = (ShTextView) itemView.findViewById(R.id.txt_born);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView_date_learn);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            mLearningWeekAdapter = new LearningWeekAdapter(mContext);
            mRecyclerView.setAdapter(mLearningWeekAdapter);
        }

        private LearningWeekAdapter getAdapter() {
            if (mLearningWeekAdapter == null) {
                mLearningWeekAdapter = new LearningWeekAdapter(mContext);
                mRecyclerView.setAdapter(mLearningWeekAdapter);
            }
            return mLearningWeekAdapter;
        }

        public RecyclerView getRecyclerView() {
            return mRecyclerView;
        }
    }

    private LearningWeekAdapter getAdapter() {
        return mItemHolder.getAdapter();
    }

    public void setChildDate(String childGender, List<EventListDay> list) {
        getAdapter().setMathList(list, mChildFirstName, childGender);
    }
}
