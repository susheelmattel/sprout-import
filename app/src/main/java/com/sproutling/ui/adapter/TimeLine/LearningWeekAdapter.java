/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.adapter.TimeLine;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.EventListDay;
import com.sproutling.utils.TimelineUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by loren.hung on 2016/12/20.
 */

public class LearningWeekAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    enum ITEM_TYPE {
        ITEM_TYPE_TOP,
        ITEM_TYPE_MID,
        ITEM_TYPE_BOTTOM
    }

    private Context mContext;
    private String mChildName = "";
    private String mGender = "M";

    private List<EventListDay> mList;

    LearningWeekAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        if (mList == null) mList = new ArrayList<>();
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_TOP.ordinal()) {
            return new TopItemHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_timeline_day_summary_top, parent, false));

        } else if (viewType == ITEM_TYPE.ITEM_TYPE_BOTTOM.ordinal()) {
            return new TopItemHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_timeline_day_summary_bottom, parent, false));

        } else {
            return new TopItemHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_timeline_day_summary_mid, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);

        EventListDay eventListDay = mList.get(position);

        TopItemHolder holder = (TopItemHolder) viewHolder;
        holder._Id = position;
        holder.date = eventListDay.getCalendar();
        holder.text_Day.setText(TimelineUtils.getDateWeekString(mContext, eventListDay.getCalendar()));

        if (type == ITEM_TYPE.ITEM_TYPE_TOP.ordinal()) {
            holder.mLearningDayAdapter.setMathEvents(eventListDay, mChildName, false, mGender, getItemCount() == 1);
        } else if (type == ITEM_TYPE.ITEM_TYPE_BOTTOM.ordinal()) {
            holder.mLearningDayAdapter.setMathEvents(eventListDay, mChildName, true, mGender, false);
        } else {
            holder.mLearningDayAdapter.setMathEvents(eventListDay, mChildName, false, mGender, false);
        }
        holder.line.setVisibility(getItemCount() == 1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE.ITEM_TYPE_TOP.ordinal();
        } else if (position == mList.size() - 1) {
            return ITEM_TYPE.ITEM_TYPE_BOTTOM.ordinal();
        } else {
            return ITEM_TYPE.ITEM_TYPE_MID.ordinal();
        }
    }

    public class TopItemHolder extends RecyclerView.ViewHolder {
        int _Id;
        public ShTextView text_Day;
        RecyclerView recyclerView_Day;
        LearningDayAdapter mLearningDayAdapter;
        ImageView line;
        public Calendar date;

        TopItemHolder(View view) {
            super(view);
            text_Day = (ShTextView) itemView.findViewById(R.id.text_Day);
            line = (ImageView) itemView.findViewById(R.id.line);
            recyclerView_Day = (RecyclerView) itemView.findViewById(R.id.recyclerView_Day);
            recyclerView_Day.setLayoutManager(new LinearLayoutManager(mContext));
            mLearningDayAdapter = new LearningDayAdapter(mContext);
            recyclerView_Day.setAdapter(mLearningDayAdapter);
        }
    }

    void setMathList(List<EventListDay> list, String childName, String gender) {
        mChildName = childName;
        mList = list;
        mGender = gender;
        notifyDataSetChanged();
    }
}
