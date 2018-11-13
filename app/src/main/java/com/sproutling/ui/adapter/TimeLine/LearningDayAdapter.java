/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.adapter.TimeLine;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.sproutling.R;
import com.sproutling.ui.activity.ArticleWebViewActivity;
import com.sproutling.ui.activity.NapSummaryActivity;
import com.sproutling.ui.activity.TimelineActivity;
import com.sproutling.ui.dialog.NightSleepDialog;
import com.sproutling.ui.widget.ChartView;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.Const;
import com.sproutling.utils.DateTimeUtils;
import com.sproutling.utils.EventBean;
import com.sproutling.utils.EventListDay;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.TimelineUtils;
import com.sproutling.utils.Utils;

import java.util.List;

/**
 * Created by loren.hung on 2016/12/20.
 */

public class LearningDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private enum ITEM_TYPE {
        ITEM_TYPE_NAP,
        ITEM_TYPE_NIGHT,
        ITEM_TYPE_ARTICLE
    }

    private Context mContext;
    private EventListDay mEventListData = new EventListDay();
    private boolean isBottom = false;
    private boolean isOnlyOne = false;
    private String mChildName = "";
    private String mGender = "M";

    LearningDayAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mEventListData.getEventList().size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_NAP.ordinal()) {
            return new NapItemHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_timeline_nap, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_ARTICLE.ordinal()) {
            return new ArticleItemHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_timeline_article, parent, false));
        } else {
            return new NightItemHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_timeline_night, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        int type = getItemViewType(position);

        List<EventBean> eventList = mEventListData.getEventList();
        final EventBean eventBean = eventList.get(position);

        if (type == ITEM_TYPE.ITEM_TYPE_NAP.ordinal()) {
            initNapItem(viewHolder, position, eventBean);
        } else if (type == ITEM_TYPE.ITEM_TYPE_NIGHT.ordinal()) {
            initNightItem(viewHolder, position, eventBean);
        } else if (type == ITEM_TYPE.ITEM_TYPE_ARTICLE.ordinal()) {
            initArticleItem(viewHolder, position, eventBean);
        }
    }

    @Override
    public int getItemViewType(int position) {
        EventBean eventBean = mEventListData.getEventList().get(position);
        String eventType = eventBean.getEventType();

        if ("Article".equals(eventType)) {
            return ITEM_TYPE.ITEM_TYPE_ARTICLE.ordinal();
        } else if (TimelineUtils.isNightSleep(mContext, eventBean)) {
            return ITEM_TYPE.ITEM_TYPE_NIGHT.ordinal();
        } else {
            return ITEM_TYPE.ITEM_TYPE_NAP.ordinal();
        }
    }

    private void initNapItem(RecyclerView.ViewHolder viewHolder, int position, final EventBean eventBean) {
        NapItemHolder holder = (NapItemHolder) viewHolder;
        holder.id = position;
        holder.txtTime.setText(DateTimeUtils.getFormat(mContext, eventBean.getStartTime()));
        holder.titleIcon.setImageResource(TimelineUtils.getTypeIcon(eventBean));
        holder.title.setText(TimelineUtils.getEventTitle(mContext, eventBean));
        holder.txtDetail.setText(TimelineUtils.getTypeDetail(mContext, eventBean, mChildName, mGender));

        if (TimelineUtils.NAP.equals(eventBean.getEventType())) {
            holder.arrow.setVisibility(View.VISIBLE);
            holder.linearLayoutNap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.logEvents(LogEvents.TIMELINE_SELECTED_NAP);
                    Intent intent = new Intent(mContext, NapSummaryActivity.class);
                    intent.putExtra("Event", eventBean);
                    ((TimelineActivity) mContext).startActivityForResult(intent, 0);
                }
            });
        } else {
            holder.arrow.setVisibility(View.GONE);
        }

//            holder.whiteLine.setVisibility(position == eventList.size() - 1 && isBottom ? View.VISIBLE : View.GONE);
        holder.point.setVisibility(getItemCount() == 1 && isOnlyOne ? View.GONE : View.VISIBLE);
        holder.line.setVisibility(getItemCount() == 1 || getItemCount() - 1 == position ? View.GONE : View.VISIBLE);
    }

    private void initNightItem(RecyclerView.ViewHolder viewHolder, int position, final EventBean eventBean) {
        NightItemHolder holder = (NightItemHolder) viewHolder;
        holder.id = position;
        holder.title.setText(R.string.night_sleep);
        holder.time.setText(DateTimeUtils.getFormat(mContext, eventBean.getStartTime()));
        holder.summaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NapSummaryActivity.class);
                intent.putExtra("Event", eventBean);
                ((TimelineActivity) mContext).startActivityForResult(intent, 0);
//                LastNightEventList mLastNightEventList = new LastNightEventList();
//                mLastNightEventList.setLastNightEventList(mEventListData.getLastNightEventList());
//                mLastNightEventList.setLastSleepSum(mLastSleepSum);
//                intent.putExtra("Event", mLastNightEventList);
//                mContext.startActivity(intent);
            }
        });
        boolean hasSpells = eventBean.hasSpells();
        holder.timeAsleep.setText(TimelineUtils.formatHoursAndMinutesShort(mContext, hasSpells ? (int) (eventBean.getTimeAsleep() / Const.TIME_MS_MIN) : TimelineUtils.getTotalMin(eventBean)));
        holder.timeAwake.setText(TimelineUtils.formatHoursAndMinutesShort(mContext, hasSpells ? (int) (eventBean.getTimeAwake() / Const.TIME_MS_MIN) : 0));
        holder.timeStirring.setText(TimelineUtils.formatHoursAndMinutesShort(mContext, hasSpells ? (int) (eventBean.getTimeStirring() / Const.TIME_MS_MIN) : 0));
        holder.wakings.setText(String.valueOf(hasSpells ? eventBean.getNumOfWakings() : 0));
//            ((NightItemHolder) viewHolder).whiteLine.setVisibility(position == eventList.size() - 1 && isBottom ? View.VISIBLE : View.GONE);

        holder.chart.setEvent(eventBean).setMode(ChartView.MODE_INLINE).init();
        holder.nightDlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NightSleepDialog(mContext).show();
            }
        });
        holder.point.setVisibility(getItemCount() == 1 && isOnlyOne ? View.GONE : View.VISIBLE);
        holder.line.setVisibility(getItemCount() == 1 || getItemCount() - 1 == position ? View.GONE : View.VISIBLE);
    }

    private void initArticleItem(RecyclerView.ViewHolder viewHolder, int position, final EventBean eventBean) {
        ArticleItemHolder holder = (ArticleItemHolder) viewHolder;
        holder.txtTime.setText(DateTimeUtils.getFormat(mContext, eventBean.getStartTime()));
        holder.txtDate.setText(TimelineUtils.getDateString(eventBean.getStartTime()));
        holder.txtTitle.setText(eventBean.getTitle());

        Glide.with(mContext)
                .load(eventBean.getImageThumbnailUrl())
                .centerCrop()
                .into(holder.articleBg);

        holder.linearLayoutNap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.logEvents(LogEvents.TIMELINE_SELECTED_TIP);
                Intent intent = new Intent(mContext, ArticleWebViewActivity.class);
                intent.putExtra(ArticleWebViewActivity.EXTRA_URL, eventBean.getUrl());
                intent.putExtra(ArticleWebViewActivity.EXTRA_TITLE, eventBean.getTitle());
                ((TimelineActivity) mContext).startActivityForResult(intent, 0);
                ((TimelineActivity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });

//            holder.whiteLine.setVisibility(position == eventList.size() - 1 && isBottom ? View.VISIBLE : View.GONE);
        holder.point.setVisibility(getItemCount() == 1 && isOnlyOne ? View.GONE : View.VISIBLE);
        holder.line.setVisibility(getItemCount() == 1 || getItemCount() - 1 == position ? View.GONE : View.VISIBLE);
    }

    private class ArticleItemHolder extends RecyclerView.ViewHolder {
        ImageView line, point, articleBg;
        ShTextView txtTitle, txtTime, txtDate;
        LinearLayout linearLayoutNap;

        ArticleItemHolder(View view) {
            super(view);
            line = (ImageView) itemView.findViewById(R.id.line);
//            whiteLine = (ImageView) itemView.findViewById(R.id.whiteline);
            articleBg = (ImageView) itemView.findViewById(R.id.article_BG);
            point = (ImageView) itemView.findViewById(R.id.point);
            txtTitle = (ShTextView) itemView.findViewById(R.id.txt_title);
            txtTime = (ShTextView) itemView.findViewById(R.id.txt_time);
            txtDate = (ShTextView) itemView.findViewById(R.id.txt_date);
            linearLayoutNap = (LinearLayout) itemView.findViewById(R.id.linearlayout_nap);
        }
    }

    private class NapItemHolder extends RecyclerView.ViewHolder {
        int id;
        ImageView line, titleIcon, arrow, point;
        ShTextView title, txtTime, txtDetail;
        LinearLayout linearLayoutNap;

        NapItemHolder(View view) {
            super(view);
            line = (ImageView) itemView.findViewById(R.id.line);
//            whiteLine = (ImageView) itemView.findViewById(R.id.whiteline);
            titleIcon = (ImageView) itemView.findViewById(R.id.title_icon);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            point = (ImageView) itemView.findViewById(R.id.point);
            title = (ShTextView) itemView.findViewById(R.id.title);
            txtTime = (ShTextView) itemView.findViewById(R.id.txt_time);
            txtDetail = (ShTextView) itemView.findViewById(R.id.txt_detail);
            linearLayoutNap = (LinearLayout) itemView.findViewById(R.id.linearlayout_nap);
        }
    }

    private class NightItemHolder extends RecyclerView.ViewHolder {
        int id;
        ShTextView title;
        ShTextView timeAsleep, timeStirring, timeAwake, wakings, nightDlg;
        ShTextView time;
        ImageView point, line;
        ChartView chart;
        LinearLayout summaryLayout;

        NightItemHolder(View view) {
            super(view);
            title = (ShTextView) itemView.findViewById(R.id.title);
            summaryLayout = (LinearLayout) itemView.findViewById(R.id.summary_layout);
            time = (ShTextView) itemView.findViewById(R.id.time);
            point = (ImageView) itemView.findViewById(R.id.point);
            line = (ImageView) itemView.findViewById(R.id.line);
//            whiteLine = (ImageView) itemView.findViewById(R.id.whiteline);
//            whiteLine.setColorFilter(Compatible.getColor(mContext, R.color.white));
            timeAsleep = (ShTextView) itemView.findViewById(R.id.time_asleep);
            timeStirring = (ShTextView) itemView.findViewById(R.id.time_stirring);
            timeAwake = (ShTextView) itemView.findViewById(R.id.time_awake);
            wakings = (ShTextView) itemView.findViewById(R.id.wakings);
            nightDlg = (ShTextView) itemView.findViewById(R.id.night_dlg);
            chart = new ChartView(mContext, itemView.findViewById(R.id.last_night_barChart));
        }
    }

    void setMathEvents(EventListDay eventListData, String childName, boolean bottom, String gender, boolean onlyOne) {
        mEventListData = eventListData;
        mChildName = childName;
        isBottom = bottom;
        isOnlyOne = onlyOne;
        mGender = gender;
        notifyDataSetChanged();
    }
}
