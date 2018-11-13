package com.sproutling.apitest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Created by loren.hung on 2017/1/13.
 */

public class TimeLineUtils {

    public static List<EventListDay> getEventListData(JSONObject jsonObject) throws JSONException {

        long learningPeriod_Time=-1;

        JSONArray jsonArray = jsonObject.getJSONObject("_embedded").getJSONArray("events");
//        List<EventBean> eventList = new ArrayList<>();

        List<String> date = new ArrayList<>();
        Map<Integer,EventListDay> map = new HashMap();

        for (int i=0; i<jsonArray.length(); i++) {
//            eventList.add(new EventBean((JSONObject) jsonArray.get(i)));

            Calendar mCalendar=Calendar.getInstance();

            String[] start_date=((JSONObject) jsonArray.get(i)).optString("start_date").replace("Z","").split("T");

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//
//            String abc = ((JSONObject) jsonArray.get(i)).optString("start_date").toString();
//            Date date1=new Date();
//
//            try{
//                date1 = sdf.parse(abc);
//            }catch (Exception e){
//
//            }
//
//            Log.d("***",date1.getTime()+" --- date1 ");

            String[] date2=start_date[0].split("-");
            String[] date3=start_date[1].split(":");

            mCalendar.set(Integer.parseInt(date2[0]),(Integer.parseInt(date2[1])-1),Integer.parseInt(date2[2]), Integer.parseInt(date3[0]),0,0);

            mCalendar.set(Calendar.MILLISECOND,0);

            mCalendar.add(Calendar.MILLISECOND, TimeZone.getDefault().getRawOffset());

            mCalendar.set(Calendar.HOUR_OF_DAY,0);

            EventBean mEventBean=new EventBean((JSONObject) jsonArray.get(i));

            if(mEventBean.getEvent_type().equals(TimeLineUtils.LearningPeriod)){

                if(mEventBean.getEnd_time().getTimeInMillis()<System.currentTimeMillis()){
                    learningPeriod_Time=mEventBean.getEnd_time().getTimeInMillis();
                }

            }

            if(map.containsKey((int)(mCalendar.getTimeInMillis()/1000))){

                map.get((int)(mCalendar.getTimeInMillis()/1000)).add(mEventBean);

            }else{
                EventListDay mEventListData=new EventListDay();
                mEventListData.add(mEventBean);
                mEventListData.setTimeInMillis(mCalendar.getTimeInMillis());
                map.put((int)(mCalendar.getTimeInMillis()/1000),mEventListData);
            }

            if(learningPeriod_Time>0){//add LearningPeriodEnd

                EventBean mLearningPeriodEnd=new EventBean();
                mLearningPeriodEnd.setStart_time(learningPeriod_Time);
                mLearningPeriodEnd.setsetEnd_time(learningPeriod_Time);
                mLearningPeriodEnd.setEvent_type(TimeLineUtils.LearningPeriodEnd);

                if(map.containsKey((int)(mLearningPeriodEnd.getStart_time().getTimeInMillis()/1000))){

                    map.get((int)(mLearningPeriodEnd.getStart_time().getTimeInMillis()/1000)).add(mLearningPeriodEnd);

                }else{
                    EventListDay mEventListData=new EventListDay();
                    mEventListData.add(mLearningPeriodEnd);
                    mEventListData.setTimeInMillis(mLearningPeriodEnd.getStart_time().getTimeInMillis());
                    map.put((int)(mLearningPeriodEnd.getStart_time().getTimeInMillis()/1000),mEventListData);
                }
            }

        }//for



        if(map.size()>0){

            List<EventListDay> mList=new ArrayList<>();
            Set<EventListDay> alarmQueue = new TreeSet<EventListDay>(new Comparator<EventListDay>() {
                @Override
                public int compare(EventListDay lhs, EventListDay rhs) {
                    int result = 0;

                    long time1 = lhs.getTimeInMillis();

                    long time2 = rhs.getTimeInMillis();
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

            for (Object key : map.keySet()) {



                alarmQueue.add(map.get(key));
//                Log.d("***",map.get(key).getEventList().size()+" --- map s ");
            }

            mList.addAll(alarmQueue);

            return mList;
        }
        return null;

    }


    public static final String[] month = {"January", "February","March","April","May","June"
            ,"July","August","September","October","November","December"};


    public static  String getDateString(Calendar mCalendar){

        String date=month[mCalendar.get(Calendar.MONTH)]+" "+
                mCalendar.get(Calendar.DAY_OF_MONTH)+", "+
                mCalendar.get(Calendar.YEAR);
        return date;
    }

    public static String getDateString3Word(Calendar mCalendar){

        String date=month[mCalendar.get(Calendar.MONTH)].substring(0,3)+" "+
                mCalendar.get(Calendar.DAY_OF_MONTH)+", "+
                mCalendar.get(Calendar.YEAR);
        return date;
    }

    public static String LearningPeriod="learningPeriod";
    public static String LearningPeriodEnd="learningPeriodEnd";
    public static String Nap="nap";

    public static String HeartRate = "heartRate";
    public static String SleepTip = "sleepTip";
    public static String YesterdaySleepSummary = "yesterdaySleepSummary";
    public static String Humidity = "humidity";
    public static String LightLevel = "lightLevel";
    public static String RoomTemperature = "roomTemperature";
    public static String NoiseLevel = "noiseLevel";

    public static int getHour(EventBean mEventBean){
        return (int)((mEventBean.getEnd_time().getTimeInMillis()-mEventBean.getStart_time().getTimeInMillis())/1000/60/60);
    }

    public static int getMin(EventBean mEventBean){
        return (int)((mEventBean.getEnd_time().getTimeInMillis()-mEventBean.getStart_time().getTimeInMillis())/1000/60%60);
    }

//    public static String getType(Context mContext,EventBean mEventBean){
//
//        if(mEventBean.getEvent_type().equals(TimeLineUtils.LearningPeriod)){
//            return mContext.getString(R.string.learning_period_started);
//        }else if(mEventBean.getEvent_type().equals(TimeLineUtils.LearningPeriodEnd)){
//            return mContext.getString(R.string.learning_period_end);
//        }else if(mEventBean.getEvent_type().equals(TimeLineUtils.Nap)){
//
//            if(mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)>=7 && mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)<12){
//                return mContext.getString(R.string.morning_nap);
//            }else if(mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)>=12 && mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)<19){
//                return mContext.getString(R.string.afternoon_nap);
//            }else if(mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)>=19 || mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)<7){
//                return mContext.getString(R.string.night_Sleep);
//            }
//
//        }
//
//        return "";
//
//    }
//
//    public static int getTypeIcon(EventBean mEventBean){
//
//
//        if(mEventBean.getEvent_type().equals(TimeLineUtils.LearningPeriod)){
//            return R.mipmap.learning_period_timeline;
//        }else if(mEventBean.getEvent_type().equals(TimeLineUtils.LearningPeriodEnd)){
//            return R.mipmap.learning_period_timeline;
//        }else if(mEventBean.getEvent_type().equals(TimeLineUtils.Nap)){
//
//            if(mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)>=7 && mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)<12){
//                return R.mipmap.zzz_log_sleep;
//            }else if(mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)>=12 && mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)<19){
//                return R.mipmap.zzz_log_sleep;
//            }else if(mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)>=19 || mEventBean.getStart_time().get(Calendar.HOUR_OF_DAY)<7){
//                return R.mipmap.night_sleep_dlg_icon;
//            }
//
//        }
//
//        return R.mipmap.zzz_log_sleep;
//
//    }
//
//    public static void handleError(Context mContext,SSError error) {
//        new AlertDialog.Builder(mContext)
//                .setTitle(R.string.settings_baby_error_message_title)
//                .setMessage(error.toString())
//                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//    }

}
