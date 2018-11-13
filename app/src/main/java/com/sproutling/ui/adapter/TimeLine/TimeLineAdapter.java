/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.adapter.TimeLine;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.sproutling.R;
import com.sproutling.services.SSError;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.Compatible;
import com.sproutling.utils.Const;
import com.sproutling.utils.DateTimeUtils;
import com.sproutling.utils.EventBean;
import com.sproutling.utils.EventListDataWeek;
import com.sproutling.utils.EventListDay;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.TimelineUtils;
import com.sproutling.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

//import kotlin.collections.EmptyList;

/**
 * Created by loren.hung on 2016/12/20.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<EventListDataWeek> mEventList = new ArrayList<>();
    //    private LinearLayoutManager mLayoutManager;
//    private int expandId = -1;
//    private ItemHolder mItemHolder = null;
    private String mGender = "M";
    private String mChildName = "";
    private Calendar mBirthDate = Calendar.getInstance();

    public TimeLineAdapter(Context context, String childFirstName, String childBirthDate) {
        mContext = context;
        mChildName = childFirstName;
        String[] date = childBirthDate.split("-");
        mBirthDate.set(Integer.parseInt(date[0]), (Integer.parseInt(date[1]) - 1), Integer.parseInt(date[2]), 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_timeline_week_summary, parent, false);
        return new TimeLineAdapter.ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final ItemHolder holder = (ItemHolder) viewHolder;
        holder.id = position;
        holder.eventListDataWeek = mEventList.get(position);
        holder.eventListDataWeek.id = position;
        holder.title.setText(String.format(mContext.getString(R.string.Week_Summary), mEventList.get(position).getDate()));
        holder.text.setText(
                String.format(mContext.getString(R.string.week_sleep_text),
                        mChildName, TimelineUtils.formatHoursAndMinutes(mContext, (int) (mEventList.get(position).getSleepAll() / Const.TIME_MS_MIN))));

        holder.totalSleep.setText(TimelineUtils.formatHoursAndMinutes(mContext, (int) (mEventList.get(position).getSleepAll() / Const.TIME_MS_MIN)));

        if (holder.eventListDataWeek.isExpand) {
            expandWeek(holder);
        } else {
            collapseWeek(holder);
        }

        holder.weekSummaryExpandLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (TimelineActivity.sIsHeaderShow) {
//                    animateHeaderUp();
//                }
                if (holder.eventListDataWeek.isExpand) {
                    collapseWeek(holder);
//                    mItemHolder = null;
//                    expandId = -1;
                } else {
                    expandWeek(holder);
                }
            }
        });
        if (position == getItemCount() - 1) {
            holder.birthdayLayout.setVisibility(View.VISIBLE);
            holder.birthday.setText(
                    String.format(mContext.getString(R.string.was_born), TimelineUtils.getDateString(mBirthDate), mChildName));
        } else {
            holder.birthdayLayout.setVisibility(View.GONE);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private int id;
        private ShTextView title, text, totalSleep;
        private ImageView arrow;
        public RelativeLayout weekSummaryExpandLayout;
        public RecyclerView recyclerViewWeek;
        private TimeLineWeekAdapter timeLineWeekAdapter;
        private LinearLayout expandAreaLayout;
        private BarChart chart;
        public EventListDataWeek eventListDataWeek;
        private LinearLayout birthdayLayout;
        private ShTextView birthday;

        ItemHolder(View view) {
            super(view);
            title = (ShTextView) itemView.findViewById(R.id.txt_title);
            text = (ShTextView) itemView.findViewById(R.id.text);
            totalSleep = (ShTextView) itemView.findViewById(R.id.total_sleep);
            arrow = (ImageView) itemView.findViewById(R.id.iv_arrow);
            weekSummaryExpandLayout = (RelativeLayout) itemView.findViewById(R.id.week_summary_expand);
            expandAreaLayout = (LinearLayout) itemView.findViewById(R.id.expand_area);
            birthdayLayout = (LinearLayout) itemView.findViewById(R.id.birthday_layout);
            birthday = (ShTextView) itemView.findViewById(R.id.txt_born);

            recyclerViewWeek = (RecyclerView) itemView.findViewById(R.id.recyclerView_week);
            recyclerViewWeek.setLayoutManager(new LinearLayoutManager(mContext));

            timeLineWeekAdapter = new TimeLineWeekAdapter(mContext);
            recyclerViewWeek.setAdapter(timeLineWeekAdapter);

            chart = (BarChart) itemView.findViewById(R.id.week_barChart);
            setupChart();
        }

        private void setupChart() {
            chart.getAxisRight().setEnabled(false);
            chart.getDescription().setEnabled(false);
            chart.setDrawBarShadow(false);
            chart.getLegend().setEnabled(false);
            chart.setScaleEnabled(false);
            chart.setTouchEnabled(false);

            chart.getXAxis().setValueFormatter(new WeekAxisXValueFormatter());
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setDrawGridLines(false);
            chart.getXAxis().setTypeface(text.getTypeface());
            chart.getXAxis().setTextColor(Compatible.getColor(mContext, R.color.dolphin));
            chart.getXAxis().setTextSize(11.5f);

            chart.getAxisLeft().setTypeface(text.getTypeface());
            chart.getAxisLeft().setTextColor(Compatible.getColor(mContext, R.color.dolphin));
            chart.getAxisLeft().setTextSize(11.5f);

            chart.getAxisLeft().setValueFormatter(new WeekAxisYValueFormatter());
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getAxisLeft().setLabelCount(5, false);
            chart.getAxisLeft().setAxisMinimum(0f);
            chart.getAxisLeft().setAxisMaximum(20f);
        }

        private TimeLineWeekAdapter getAdapter() {
            if (timeLineWeekAdapter == null) {
                timeLineWeekAdapter = new TimeLineWeekAdapter(mContext);
                recyclerViewWeek.setAdapter(timeLineWeekAdapter);
            }
            return timeLineWeekAdapter;
        }

        public RecyclerView getRecyclerView() {
            return recyclerViewWeek;
        }
    }

    private void expandWeek(final ItemHolder itemHolder) {
        Utils.logEvents(LogEvents.TIMELINE_EXPANDED_WEEK_SUMMARY);
//        if (mItemHolder != null) {
//            collapseWeek(mItemHolder);
//        }
//        mItemHolder = itemHolder;

        long sleepAll = itemHolder.eventListDataWeek.getSleepAll();

        if (sleepAll > Const.TIME_MS_MIN) {
            itemHolder.chart.setVisibility(View.VISIBLE);
            setWeekChartData(itemHolder.chart, itemHolder.eventListDataWeek);
        } else {
            itemHolder.chart.setVisibility(View.GONE);
        }

//        mLayoutManager.scrollToPositionWithOffset(itemHolder.eventListDataWeek.id, 0);
        itemHolder.expandAreaLayout.setVisibility(View.VISIBLE);
        itemHolder.arrow.setImageResource(R.drawable.ic_up_arrow);
        itemHolder.getAdapter().setMathList(itemHolder.eventListDataWeek, mChildName, mGender);
        itemHolder.eventListDataWeek.isExpand = true;
        mEventList.get(itemHolder.id).isExpand = true;
//        expandId = itemHolder.eventListDataWeek.id;
    }

    private void collapseWeek(ItemHolder itemHolder) {
        Utils.logEvents(LogEvents.TIMELINE_COLLAPSED_WEEK_SUMMARY);
        itemHolder.eventListDataWeek.isExpand = false;
        mEventList.get(itemHolder.id).isExpand = false;
        itemHolder.expandAreaLayout.setVisibility(View.GONE);
        itemHolder.arrow.setImageResource(R.drawable.ic_down_arrow);
    }

    private void setWeekChartData(BarChart chart, EventListDataWeek eventListDataWeek) {
        ArrayList<BarEntry> yValues = new ArrayList<>();
        int[] colorCode = new int[7];

        float sleepSun = (float) eventListDataWeek.getSleepSun() / Const.TIME_MS_HOUR;
        float sleepMon = (float) eventListDataWeek.getSleepMon() / Const.TIME_MS_HOUR;
        float sleepTue = (float) eventListDataWeek.getSleepTue() / Const.TIME_MS_HOUR;
        float sleepWed = (float) eventListDataWeek.getSleepWed() / Const.TIME_MS_HOUR;
        float sleepThu = (float) eventListDataWeek.getSleepThu() / Const.TIME_MS_HOUR;
        float sleepFri = (float) eventListDataWeek.getSleepFri() / Const.TIME_MS_HOUR;
        float sleepSat = (float) eventListDataWeek.getSleepSat() / Const.TIME_MS_HOUR;

        yValues.add(new BarEntry(0, sleepSun));
        colorCode[0] = setColor();

        yValues.add(new BarEntry(1, sleepMon));
        colorCode[1] = setColor();

        yValues.add(new BarEntry(2, sleepTue));
        colorCode[2] = setColor();

        yValues.add(new BarEntry(3, sleepWed));
        colorCode[3] = setColor();

        yValues.add(new BarEntry(4, sleepThu));
        colorCode[4] = setColor();

        yValues.add(new BarEntry(5, sleepFri));
        colorCode[5] = setColor();

        yValues.add(new BarEntry(6, sleepSat));
        colorCode[6] = setColor();

        BarDataSet set = new BarDataSet(yValues, "");
        set.setColors(colorCode);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        BarData data = new BarData(dataSets);
//            data.setValueTextSize(10f);
        data.setBarWidth(0.6f);
        data.setDrawValues(false);
        chart.setData(data);
    }

    private int setColor(long val) {
        return val >= 14.0f ? mContext.getResources().getColor(R.color.tealish2) : mContext.getResources().getColor(R.color.color_timeline_chart_grey);
    }

    private int setColor() {
        return mContext.getResources().getColor(R.color.tealish2);
    }

    public void setChildDate(String childFirstName, String childBirthDate, String childGender, List<EventListDay> list) {
        mBirthDate = Calendar.getInstance();
        String[] date = childBirthDate.split("-");
        mBirthDate.set(Integer.parseInt(date[0]), (Integer.parseInt(date[1]) - 1), Integer.parseInt(date[2]), 0, 0, 0);

        mChildName = childFirstName;
        mGender = childGender;
//        mItemHolder = null;
        getEvents(list);
    }

    private void getEvents(final List<EventListDay> list) {
        new AsyncTask<Void, Void, List<EventListDataWeek>>() {
            SSError mError;

            @Override
            protected List<EventListDataWeek> doInBackground(Void... params) {
                if (list != null) {
                    mEventList = setEventList(list);
                    setLastNightData();
                }
                return mEventList;
            }

            @Override
            protected void onPostExecute(List<EventListDataWeek> list) {
                if (list != null && list.size() > 0) {
                    notifyDataSetChanged();
                } else {
                    if (mError != null) TimelineUtils.handleError(mContext, mError);
                }
            }
        }.execute();
    }

    private List<EventListDataWeek> setEventList(List<EventListDay> eventListAllDay) {
        List<EventListDataWeek> eventListWeek = new ArrayList<>();

        Calendar calendarSunday = Calendar.getInstance();
        calendarSunday.setFirstDayOfWeek(Calendar.SUNDAY);

        mBirthDate = TimelineUtils.trimTimeToDay(mBirthDate);

        Calendar firstDayOfWeek = Calendar.getInstance();
        firstDayOfWeek.setFirstDayOfWeek(Calendar.SUNDAY);
        firstDayOfWeek.set(Calendar.DAY_OF_WEEK, mBirthDate.get(Calendar.DAY_OF_WEEK));
        int week = (int) (Math.ceil((firstDayOfWeek.getTimeInMillis() - mBirthDate.getTimeInMillis()) / Const.TIME_MS_WEEK)); // to check baby's age(how many weeks)

        calendarSunday.add(Calendar.DATE, -(calendarSunday.get(Calendar.DAY_OF_WEEK) - 1));// To set the week section "start Sunday" that include today.
        calendarSunday = TimelineUtils.trimTimeToDay(calendarSunday);

        Calendar calendarSaturday = Calendar.getInstance();
        calendarSaturday.setFirstDayOfWeek(Calendar.SUNDAY);

        calendarSaturday.add(Calendar.DATE, (7 - calendarSaturday.get(Calendar.DAY_OF_WEEK)));// To set the week section "end Saturday" that include today.

        calendarSaturday.set(Calendar.HOUR_OF_DAY, 23);
        calendarSaturday.set(Calendar.MINUTE, 59);
        calendarSaturday.set(Calendar.SECOND, 59);
        calendarSaturday.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i <= week; i++) {
            boolean isAdded = false;
            EventListDataWeek eventListDataWeek = new EventListDataWeek();
            eventListDataWeek.setTimeInMillis(calendarSunday.getTimeInMillis());

            for (int j = 0; j < eventListAllDay.size(); j++) {
                EventListDay eventListDay = eventListAllDay.get(j);
                Calendar calendar = eventListDay.getCalendar();

                if (calendar.getTimeInMillis() >= calendarSunday.getTimeInMillis() &&
                        calendar.getTimeInMillis() <= calendarSaturday.getTimeInMillis()) {
                    isAdded = true;
                    List<EventBean> eventList = eventListDay.getEventList();

                    for (int k = 0; k < eventList.size(); k++) {
                        EventBean eventBean = eventList.get(k);

                        if (TimelineUtils.NAP.equals(eventBean.getEventType())) {
                            long sleepTime = eventBean.getEndTime().getTimeInMillis() - eventBean.getStartTime().getTimeInMillis();
                            if (TimelineUtils.isNightSleep(mContext, eventBean)) {
                                eventListDay.setSleepNight(sleepTime);
                            } else {
                                eventListDay.setSleepDay(sleepTime);
                            }
                            eventListDataWeek.setSleepAll(sleepTime);
                        }
                    }
                    long sleep = eventListDay.getSleepDay() + eventListDay.getSleepNight();

                    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        eventListDataWeek.setSleepSun(sleep);
                    } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                        eventListDataWeek.setSleepMon(sleep);
                    } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                        eventListDataWeek.setSleepTue(sleep);
                    } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                        eventListDataWeek.setSleepWed(sleep);
                    } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                        eventListDataWeek.setSleepThu(sleep);
                    } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                        eventListDataWeek.setSleepFri(sleep);
                    } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                        eventListDataWeek.setSleepSat(sleep);
                    }
                    eventListDataWeek.add(eventListDay);
                }
            }
            if (isAdded) {
                eventListWeek.add(eventListDataWeek);
            }
            calendarSunday.add(Calendar.DATE, -7);
            calendarSaturday.add(Calendar.DATE, -7);
        }
        if (eventListWeek.size() > 0) {
            eventListWeek.get(0).isExpand = true;
        }
        return eventListWeek;
    }

    private void setLastNightData() {
        Calendar calendar = TimelineUtils.trimTimeToDay(Calendar.getInstance());

        if (mEventList.isEmpty()) return;

        List<EventListDay> eventListsByDay = mEventList.get(0).getEventList();
        EventListDay today = eventListsByDay.get(0);
        List<EventBean> eventsToday = today.getEventList();

        if (calendar.getTimeInMillis() == today.getCalendar().getTimeInMillis()) {
            for (int i = 0; i < eventsToday.size(); i++) {
                EventBean eventBean = eventsToday.get(i);
                if (TimelineUtils.isNightSleep(mContext, eventBean) && DateTimeUtils.isAM(eventBean.getStartTime())) {
                    today.addLastNight(eventBean);
                }
            }
            List<EventBean> lastNightEvents = today.getLastNightEventList();
            if (lastNightEvents.size() > 0) {
                for (int i = 0; i < lastNightEvents.size(); i++) {
                    eventsToday.remove(lastNightEvents.get(i));
                }
            }
            if (eventListsByDay.size() > 1) {
                EventListDay yesterday = eventListsByDay.get(1);
                List<EventBean> eventListYesterday = yesterday.getEventList();
                Calendar yesterdayCalendar = TimelineUtils.trimTimeToDay(yesterday.getCalendar());

                if ((calendar.getTimeInMillis() - Const.TIME_MS_DAY) == yesterdayCalendar.getTimeInMillis()) {
                    for (int i = 0; i < eventListYesterday.size(); i++) {
                        EventBean event = eventListYesterday.get(i);
                        if (TimelineUtils.NAP.equals(event.getEventType()) && DateTimeUtils.isPM(event.getStartTime(), event.getEndTime())) {
                            today.addLastNight(event);
                        }
                    }
                }
            }
            if (lastNightEvents.size() > 1) {
                Set<EventBean> eventQueue = new TreeSet<>(new Comparator<EventBean>() {
                    @Override
                    public int compare(EventBean lhs, EventBean rhs) {
                        int result = 0;
                        long time1 = lhs.getStartTime().getTimeInMillis();
                        long time2 = rhs.getStartTime().getTimeInMillis();
                        long diff = time1 - time2;

                        if (diff > 0) {
                            return -1;
                        } else if (diff == 0) {
                            return 1;
                        } else if (diff < 0) {
                            return 1;
                        }
                        return result;
                    }
                });

                for (int i = 0; i < lastNightEvents.size(); i++) {
                    eventQueue.add(lastNightEvents.get(i));
                }

                lastNightEvents.clear();
                lastNightEvents.addAll(eventQueue);
            }
        }
    }
}
