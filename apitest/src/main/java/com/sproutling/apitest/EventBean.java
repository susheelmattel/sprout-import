package com.sproutling.apitest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by loren.hung on 2017/1/12.
 */

public class EventBean implements Serializable {

    private static final long serialVersionUID = 886227229901L;
    private String id;
    private String child_id="";
    private String created_at="";
    private String data="";
    private String end_date="";
    private String start_date="";
    private String updated_at="";
    private String event_type="";

    private Calendar start_time=Calendar.getInstance();
    private Calendar end_time=Calendar.getInstance();
    private int sleepTime;
    public String jsonString;

    public EventBean(){}


    public EventBean(JSONObject jsonObject) throws JSONException {

        setId(jsonObject.optString("id"));
        setChild_id(jsonObject.optString("child_id"));
        setEnd_date(jsonObject.optString("end_date"));
        setStart_date(jsonObject.optString("start_date"));
        setEvent_type(jsonObject.optString("event_type"));
        setCreated_at(jsonObject.optString("created_at"));
        setUpdated_at(jsonObject.optString("updated_at"));
        jsonString = jsonObject.toString();
    }

    public void setId(String id){this.id=id;}
    public String getId(){return id;}

    public void setEvent_type(String event_type){
        this.event_type=event_type;
//        Log.d("***"," event_type - "+event_type);
    }
    public String getEvent_type(){return event_type;}

    public void setChild_id(String child_id){this.child_id=child_id;}
    public String getChild_id(){return child_id;}

    public void setEnd_date(String end_date){
        this.end_date=end_date;
        if(end_date.length()==20 && end_date.endsWith("Z")){
            setEnd_time(end_date);
        }
    }
    public String getEnd_date(){return end_date;}

    public void setStart_date(String start_date){
        this.start_date=start_date;
        if(start_date.length()==20 && start_date.endsWith("Z")){
            setStart_time(start_date);
        }
    }
    public String getStart_date(){return start_date;}

    public void setUpdated_at(String updated_at){this.updated_at=updated_at;}
    public String getUpdated_at(){return updated_at;}

    public void setCreated_at(String created_at){this.created_at=created_at;}
    public String getCreated_at(){return created_at;}

    public void setStart_time(String start_date){
//        2016-12-21T00:25:48Z
        String[] date=start_date.replace("Z","").split("T");

        if(date.length>1){
            String[] date2=date[0].split("-");
            String[] date3=date[1].split(":");
            start_time.set(Integer.parseInt(date2[0]),(Integer.parseInt(date2[1])-1),Integer.parseInt(date2[2]),
                    Integer.parseInt(date3[0]),Integer.parseInt(date3[1]),Integer.parseInt(date3[2]));

            start_time.add(Calendar.MILLISECOND, TimeZone.getDefault().getRawOffset());

            if(!end_date.equals("")){
                setSleepTime();
            }
        }

    }

    public void setStart_time(long time){
        start_time.setTimeInMillis(time);
    }

    public Calendar getStart_time(){return start_time;}

    public void setEnd_time(String end_date){
//        2016-12-21T00:25:48Z
        String[] date=end_date.replace("Z","").split("T");

        if(date.length>1){
            String[] date2=date[0].split("-");
            String[] date3=date[1].split(":");
            end_time.set(Integer.parseInt(date2[0]),(Integer.parseInt(date2[1])-1),Integer.parseInt(date2[2]),
                    Integer.parseInt(date3[0]),Integer.parseInt(date3[1]),Integer.parseInt(date3[2]));

            end_time.add(Calendar.MILLISECOND, TimeZone.getDefault().getRawOffset());

            if(!start_date.equals("")){
                setSleepTime();
            }
        }

    }

    public void setsetEnd_time(long time){
        end_time.setTimeInMillis(time);
    }

    public Calendar getEnd_time(){return end_time;}

    private void setSleepTime(){
        sleepTime=(int)(end_time.getTimeInMillis()/1000-start_time.getTimeInMillis()/1000);
    }

    private int getSleepTime(){
        return sleepTime/60;
    }

    @Override
    public String toString() {
        return jsonString;
    }

}
