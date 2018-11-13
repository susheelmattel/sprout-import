package com.sproutling.apitest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by loren.hung on 2017/1/13.
 */

public class EventListDay {

    private List<EventBean> eventList=new ArrayList<>();
    private long timeInMillis;
    private long sleepNight=0;
    private long sleepDay=0;
    private int dayOfWeek=0;
    private Calendar mCalendar=Calendar.getInstance();

    public void add(EventBean mEventBean){eventList.add(mEventBean);}

    public void setTimeInMillis(long timeInMillis){
        this.timeInMillis=timeInMillis;
        mCalendar.setTimeInMillis(timeInMillis);
        dayOfWeek=mCalendar.get(Calendar.DAY_OF_WEEK);
    }

    public int getDayOfTheWeek(){return dayOfWeek;}

    public long getSleepNight(){return sleepNight;}

    public long getSleepDay(){return sleepDay;}

    public void setSleepNight(long sleepNight){this.sleepNight+=sleepNight;}

    public void setSleepDay(long sleepDay){this.sleepDay+=sleepDay;}

    public long getTimeInMillis(){return timeInMillis;}

    public Calendar getCalendar(){return mCalendar;}

    public List<EventBean> getEventList(){return eventList;}

    public EventListDay(){}



}
