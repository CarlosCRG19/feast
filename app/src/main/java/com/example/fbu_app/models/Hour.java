package com.example.fbu_app.models;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Hour")
public class Hour extends ParseObject{

    public static Hour fromJson(JSONObject jsonObject) {

        Hour hour = new Hour();

        hour.setDay(jsonObject.optInt("day"));
        hour.setOpen(jsonObject.optString("start"));
        hour.setEnd(jsonObject.optString("end"));

        return hour;

    }

    public static List<Hour> fromJsonArray(JSONArray jsonArray, Business business) throws JSONException {
        List<Hour> hours = new ArrayList<>();
        for(int i=0 ; i < jsonArray.length() ; i++) {
            Hour hour = fromJson(jsonArray.getJSONObject(i));
            hour.setBusiness(business);
            hours.add(hour);
        }

        return  hours;

    }

    public void setDay(int day) {
        put("day", formatDay(day));
    }

    public String getDay() {
        return getString("day");
    }

    public void setOpen(String open) {
        put("open", formatHour(open));
    }

    public String getStart() {
        return getString("start") ;
    }

    public void setEnd(String end) {
        put("closed", formatHour(end));
    }

    public String getEnd() {
        return getString("end");
    }

    public void setBusiness(Business business) {
        put("business", business);
    }

    public Business getBusiness() {
        return (Business) get("business");
    }

    private String formatDay(int day) {
        switch (day) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
            case 6:
                return "Sunday";
            default:
                return "";
        }
    }

    private String formatHour(String hour) {

        return hour.substring(0,2) + ":" + hour.substring(2);
    }

}
