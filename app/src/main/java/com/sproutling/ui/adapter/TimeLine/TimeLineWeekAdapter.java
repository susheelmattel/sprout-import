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
import com.sproutling.utils.EventListDataWeek;
import com.sproutling.utils.EventListDay;
import com.sproutling.utils.TimelineUtils;

import java.util.Calendar;

/**
 * Created by loren.hung on 2016/12/20.
 */

public class TimeLineWeekAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private enum ITEM_TYPE {
        ITEM_TYPE_TOP,
        ITEM_TYPE_MID,
        ITEM_TYPE_BOTTOM
    }

    private Context mContext;
    private String mChildName = "";
    private String mGender = "M";
    private EventListDataWeek mEventListDataWeek = new EventListDataWeek();

    TimeLineWeekAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mEventListDataWeek.getEventList().size();
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

        EventListDay eventListDay = mEventListDataWeek.getEventList().get(position);

        TopItemHolder holder = (TopItemHolder) viewHolder;
        holder.id = position;
        holder.textDay.setText(TimelineUtils.getDateWeekString(mContext, eventListDay.getCalendar()));
        holder.date = eventListDay.getCalendar();

        if (type == TimeLineWeekAdapter.ITEM_TYPE.ITEM_TYPE_TOP.ordinal()) {
            holder.timeLineDayAdapter.setMathEvents(eventListDay, mChildName, false, mGender, getItemCount() == 1);
        } else if (type == TimeLineWeekAdapter.ITEM_TYPE.ITEM_TYPE_BOTTOM.ordinal()) {
            holder.timeLineDayAdapter.setMathEvents(eventListDay, mChildName, true, mGender, false);
        } else {
            holder.timeLineDayAdapter.setMathEvents(eventListDay, mChildName, false, mGender, false);
        }
        holder.line.setVisibility(getItemCount() == 1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return LearningWeekAdapter.ITEM_TYPE.ITEM_TYPE_TOP.ordinal();
        } else if (position == mEventListDataWeek.getEventList().size() - 1) {
            return LearningWeekAdapter.ITEM_TYPE.ITEM_TYPE_BOTTOM.ordinal();
        } else {
            return LearningWeekAdapter.ITEM_TYPE.ITEM_TYPE_MID.ordinal();
        }
    }

    public class TopItemHolder extends RecyclerView.ViewHolder {
        int id;
        public ShTextView textDay;
        ImageView line;
        RecyclerView recyclerViewDay;
        TimeLineDayAdapter timeLineDayAdapter;
        public Calendar date;

        TopItemHolder(View view) {
            super(view);
            textDay = (ShTextView) itemView.findViewById(R.id.text_Day);
            line = (ImageView) itemView.findViewById(R.id.line);
            recyclerViewDay = (RecyclerView) itemView.findViewById(R.id.recyclerView_Day);
            recyclerViewDay.setLayoutManager(new LinearLayoutManager(mContext));
            timeLineDayAdapter = new TimeLineDayAdapter(mContext);
            recyclerViewDay.setAdapter(timeLineDayAdapter);
        }
    }

    void setMathList(EventListDataWeek eventListDataWeek, String childName, String gender) {
        mEventListDataWeek = eventListDataWeek;
        mChildName = childName;
        mGender = gender;
        notifyDataSetChanged();
    }
}
